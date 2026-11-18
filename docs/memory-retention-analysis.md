# BetterRTP memory-retention analysis

Reviewed 2026-11-18 at commit `04309fb`; core POM version 3.6.13. Scope: BetterRTP-Core player lifecycle, cooldown persistence, RTP execution, menus, queues, scheduling, and shutdown. Addons were screened but are not covered by the conclusions below. This is source analysis plus an isolated Java reproduction, not a production heap diagnosis. No plugin behavior was changed.

All source references below are relative to `BetterRTP-Core/src/main/java/me/SuperRonanCraft/BetterRTP/`.

## Implementation status: deferred player lookups

Finding 1 has now been addressed in the working tree. The findings below describe the original reviewed commit; the other issues remain separate work.

- Sessions are created explicitly on join and for online players during enable/reload, and cleared on quit/disable. `getData(Player)` is now a nullable, non-creating lookup that rejects a different Player instance for the same UUID. External integrations using this API must handle absent sessions.
- Cooldown loads capture the session before scheduling, avoid duplicate loads, and apply results through an atomic current-session check. Old results cannot update a replacement session or repopulate the map.
- Player saves capture immutable UUID/count/timestamp values before executor submission. Per-world saves copy the cooldown timestamp before scheduling. Deferred writers do not look up or capture PlayerData/Player.
- Core placeholder, menu, damage, and effect callers handle absent session data.
- Validation: `mvn -o -q -f BetterRTP-Core/pom.xml test` passed six regression tests, including a blocked database executor released after session mutation/quit. Quit, reconnect, reload, and non-creating reads are covered. Live Paper/Folia integration has not been exercised.

## 1. High: deferred player lookups recreate cache entries after quit

Evidence: `references/player/playerdata/PlayerDataManager.java:15`, `PlayerData.java:16`, `references/player/HelperPlayer.java:13`, `player/events/Join.java:25`, `references/rtpinfo/CooldownHandler.java:130-166`.

`getData(Player)` is a creating operation: `computeIfAbsent` installs a PlayerData containing a strong, final Player reference. Neither this method nor loadPlayer checks whether that player session remains active. Quit only removes the UUID from this map.

Two concrete sequences:

1. Join schedules async loadPlayer(p); player quits and the cache is cleared; the async task starts and getData(p) creates a new entry for the departed player.
2. A cooldown change schedules savePlayer(p); player quits; the save callback calls `DatabaseHandler.getPlayers().setData(getData(player))`, recreating the cache entry.

The entry has no expiry or offline sweep. It remains until another explicit clear for that UUID or a plugin reload. A stream of different departing players can grow the map beyond the online population. Reference path: plugin -> PlayerDataManager -> map -> PlayerData -> Player. The exact retained bytes behind Player require heap inspection; this does not establish that every loaded chunk is retained by each player.

The save race also risks overwriting persisted count/global cooldown with defaults: the callback can obtain a newly created empty PlayerData instead of the state that originally triggered the save. If the player reconnects while the stale entry exists, getData(newPlayer) returns data whose final player field still refers to the previous session. UUID keys alone do not solve this.

Important distinction: when loadPlayer obtained PlayerData before quit, updating that detached object afterward does not itself reinsert it. The problematic insertion is the creating lookup after removal. ConcurrentHashMap makes map operations safe but does not make join/quit/callback ordering correct.

Validation: compiled the actual, unmodified PlayerDataManager and PlayerData sources with minimal Bukkit/annotation/menu stubs. Simulated 1,000 join -> clear -> deferred lookup sequences: 1,000 entries remained. Reconnecting with a different Player instance and the same UUID returned the old Player reference. This validates cache semantics, not real scheduler timing or production incidence. Reproduction is in `/tmp/betterrtp-retention-2sz2g7g5/Repro.java` for this workspace session.

Recommended correction: create session state explicitly on join; separate non-creating lookup from creation; invalidate the session on quit; carry a session token through callbacks. Save immutable UUID/count/timestamp snapshots captured before scheduling, without looking up PlayerData in the writer. Apply loaded results only if the original session is still current. An isOnline check alone still leaves check/remove/insert and reconnect races.

## 2. High: RTP operation map has permanent retention paths

Evidence: `player/PlayerInfo.java:19,45-59`, `player/rtp/RTP.java:82-94`, `RTPDelay.java:31-36`, `RTPPlayer.java:64-76`, `RTPTeleport.java:55-71`, and `player/events/Leave.java:9-12`.

`PlayerInfo.rtping` is a HashMap keyed by Player. RTP inserts before firing the teleport pre-event. If another plugin cancels that event, both instant and delayed paths stop without removing the entry. This is a direct persistent retention path, even without a database race. Quit does not clear it; PlayerInfo.unload and unloadAll are private and never invoked.

Additional ways to strand the entry:

- The chunk-loading continuation has no exceptional completion handler. Its surrounding catch also silently discards Throwable without cleanup.
- teleportAsync uses thenRun, so exceptional completion skips cleanup. Errors thrown by afterTeleport also skip the later removal.
- A false Boolean teleport result still runs the success path because thenRun ignores the result; this is a correctness issue separate from retention.

Ordinary completion can remove an entry; this is not a claim that every RTP leaks. The cancellation and failure paths make retention possible without a later cleanup guarantee. Player keys can retain distinct disconnected session objects across reconnects.

Recommended correction: centralize idempotent operation completion and use it for success, cancellation, exceptions, quit, reload, and disable. Scope removal to the operation/session so an old callback cannot clear a newer request. Handle both exceptional completion and unsuccessful teleport results. Use thread-safe or correctly scheduler-confined state for supported Folia execution.

## 3. High/medium: RTP keeps searching after disconnect

Evidence: `player/rtp/RTPDelay.java:72-78`, `RTPPlayer.java:38-114`, `RTPTeleport.java:40-85`, and `player/rtp/effects/RTPEffect_Potions.java:56`.

Quit does not cancel a delay or in-progress search. The delay checks rtping, which quit does not clear. Search callbacks do not verify online/session/operation state before generating locations, loading chunks, charging, adding cooldowns, or teleporting. Later cooldown and effect lookups can recreate PlayerData as in finding 1.

Ordinary failed-location retries are limited by maxAttempts, so these callbacks alone are not necessarily a permanent leak. They are avoidable post-quit work and temporary Player retention that can feed permanent caches. An unresolved chunk future can prolong retention; no application timeout exists.

A separate retry bug exists at RTPPlayer.java:46-49: when the location event is cancelled, recursive randomlyTeleport runs before attempts is incremented. Repeated cancellation can cause stack overflow instead of reaching the attempt limit. Treat this as a control-flow/resource-exhaustion bug, not proof of a growing heap cache.

Recommended correction: cancel the logical operation on quit, check its token at every async boundary and before side effects, bound its lifetime, and guarantee terminal cleanup. Do not assume cancelling a future stops underlying chunk IO. Count attempts before retrying and schedule retries without recursive stack growth.

## 4. Medium: settings menu maps retain players

Evidence: `player/PlayerInfo.java:16-17,31-36,41-66`, `references/invs/types/RTPInvCoordinates.java:18-22`, `RTPInvWorlds.java:55`, `references/invs/RTPInventories.java:22-29`.

Menu navigation writes Player keys into invWorld and invNextInv. Quit does not clear these maps. The apparent reload cleanup is ineffective: closeAll gates cleanup on playerExists, which checks the legacy invs map; its only insertion method is commented out. Offline users would be skipped by closeAll's online-player loop anyway. No InventoryCloseEvent handler performs cleanup in the core listener.

This is a source-confirmed retention path for users who navigate those settings screens. It affects fewer players than the general cooldown race, but does not require async timing.

Recommended correction: consolidate menu state into session-owned data or explicitly remove all menu references on quit and clear them on reload/disable. Close inventory cleanup must use the active menu state, not the unused legacy map.

## 5. Medium: database backlog and executor lifecycle

Evidence: `references/database/SQLiteExecutor.java:7`, `DatabasePlayers.java:49-85`, `DatabaseQueue.java:63-95`, `BetterRTP.java:78-83`.

A static single-thread executor serves player and queue work, has an unbounded pending-task queue, and is never shut down by plugin code. Writes capture PlayerData, which holds Player. Several callers block on Future.get without an application timeout while holding their own captured Player/world state. Under sustained IO slowdown, queued tasks and waiting async callers can retain an increasing population of objects. This is a conditional backlog, not proof that normal successful queries permanently leak.

After first use, the executor also leaves a worker running across plugin disable. This is a lifecycle leak risk when unloading/replacing the plugin in-process; exact classloader roots should be verified in a heap dump. It is distinct from an ordinary `/rtp reload`, which does not create a fresh static executor.

Recommended correction: own the executor in the plugin lifecycle, define a bounded pending-work policy, snapshot primitive persistence data, expose queue depth/age, and drain/shut down explicitly. Avoid blocking worker threads waiting for other workers. Shutdown must coordinate pending saves before clearing session state.

## 6. Medium/conditional: queue generation can outlive reload

Evidence: `references/rtpinfo/QueueGenerator.java:33-66,165-204`, `QueueHandler.java:48-51`.

Only the latest queueGenerator delayed task is tracked. The initial generate delay is not retained; in-flight chunk/database continuations can call queueGenerator after unload. Concurrent low-queue requests can each enter generate before generating becomes true. Reload or demand bursts can therefore leave multiple chains active and make cancellation incomplete. A request can carry WorldPlayer via RTPWorld, retaining a player through that chain.

This is a lifecycle/concurrency risk, not a demonstrated indefinitely growing in-memory location cache. DatabaseQueue queries explicitly limit results to queueMax + 1 (33). Queue contents are persisted rather than held in an unlimited core list.

Recommended correction: use a generation token and atomic single-flight admission; invalidate the generation on reload/disable and check it before each continuation schedules more work.

## Other observations and limits

- PlaceholderAPI calls creating getData before validating the request and without a null/session guard (`DepPlaceholderAPI.java:40-41`). A stale Player passed by an integration can reproduce finding 1; a null player can throw. This method itself does not issue SQL, and the core contains no active getOfflinePlayer lookup. Do not attribute automatic offline placeholder polling to this plugin without tracing the caller.
- CooldownHandler.downloading removes entries in finally. Under normal completion it is not a permanent leak, although blocked database work prolongs retention. It also allows duplicate loads.
- Most query paths explicitly close JDBC resources. SQLite.close wraps all closes in a single try, so one close failure can prevent subsequent cleanup; use independent try-with-resources. This is exception-path hardening, not evidence that every query leaks connections.
- Metrics' timer cancels when it next notices the plugin disabled, so it can retain the old plugin until that scheduled check (up to roughly 30 minutes). It is not necessarily permanent.
- Logger setup closes the prior file handler. LogUploader bounds uploaded file content. Neither supplied a stronger explanation for offline Player retention.
- No production heap, live server, or user logs were available. No full plugin integration tests were run. External libraries, BetterSync internals, and addon-specific lifecycle bugs are outside this report's validated scope.

## Fix order and validation

1. Introduce explicit session ownership/non-creating reads and snapshot-based persistence.
2. Centralize RTP completion, cancel on quit, and cover cancellation/failure/timeout branches.
3. Remove legacy menu references on quit/reload/disable.
4. Bound and close database work; invalidate queue-generation continuations on reload.

Regression scenarios should deliberately control callback ordering: quit before join-load starts; quit before save starts; reconnect before an old callback finishes; cancelled pre-event in both delay modes; exceptional chunk/teleport futures; exception during post-teleport effects; quit during delay/search; menu use then quit; reload during queue generation; database backlog then shutdown.

For production confirmation, compare retained objects after equivalent join/RTP/quit cycles and after pending work drains. Inspect strong paths from BetterRTP to PlayerDataManager.playerData, PlayerInfo.rtping/invWorld/invNextInv, executor pending tasks, and RTP callback/listener instances. Count offline Player objects in those structures, oldest operation age, database queue depth, and outstanding generation chains. A rising heap before garbage collection alone is insufficient; distinguish live retained objects from allocation churn, ordinary loaded chunks, and work that eventually drains. Expected steady state: no departed session in player/menu/operation maps and no stale callback able to recreate it.
