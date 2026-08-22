# BetterRTP — Lophine 26.1.2 Adaptation (AI Improvements)

> **All code changes in this project were made by an AI coding agent** (a full adaptation
> session performed by a DeepSeek-based agent), targeting **Lophine 26.1.2** (a Folia-family,
> Paper fork, Minecraft 26.1.2).
> Upstream: [RonanPlugins/BetterRTP](https://github.com/RonanPlugins/BetterRTP) v3.6.13 (MIT License).

---

## 1. Layout

| Path | Description |
|---|---|
| `BetterRTP/` | Plugin source (adapted) |
| `BetterRTP/target/BetterRTP-3.6.13.jar` | **Final build artifact** (includes LICENSE) |
| `lophine-test/` | Test server (Lophine 26.1.2) |
| `lophine-test/plugins/BetterRTP-3.6.13.jar` | Deployed plugin |
| `_backup/BetterRTP-20260822-100842/` | Pre-change source backup (297 files) |
| `_tools/` | Maven 3.9.16 toolchain (local repo `m2repo`, RedProtect mirror, etc.) |

---

## 2. AI Improvement List

### 🔴 Fix A — Core: RTP completely broken on Folia 26.1.2 (upstream issue [#257](https://github.com/RonanPlugins/BetterRTP/issues/257))

- **Problem**: BetterRTP delegated *scheduling* to FoliaLib, but async chunk loading still went
  through **PaperLib 1.0.8** (a 2019-era library that does not recognize Folia's new
  `getChunkAtAsync` signature → reflection fallback → **synchronous** `world.getChunkAt`).
  Called on Folia's *global region* thread, this trips the Moonrise thread check:
  `Thread failed main thread check: Async chunk retrieval` → `/rtp` is unusable and players get
  stuck in the queue.
- **Change** (4 files): `PaperLib.getChunkAtAsync(...)` / `PaperLib.teleportAsync(...)`
  → native **`World#getChunkAtAsync(...)`** / **`Entity#teleportAsync(...)`** (Folia-safe APIs;
  the future completes on the correct region thread).
  - `player/rtp/RTPPlayer.java`
  - `player/rtp/RTPTeleport.java`
  - `references/rtpinfo/QueueGenerator.java`
  - `references/rtpinfo/RandomLocation.java`
- **Verified**: **0** thread-check errors after the fix; the queue pre-generator produced
  **57 safe locations** across overworld + nether (the exact code path from #257).

### 🟠 Fix B — Native particles

- **Problem**: ParticleLib 1.8.4 officially supports only **MC 1.8–1.19.3**; its NMS reflection
  broke on the MC 1.21.5+ particle rewrite (silent failure + console stack traces), and it
  spawned particles from an async thread (violates Folia's region-thread model).
- **Change** (`player/rtp/effects/RTPEffect_Particles.java` + `commands/types/CmdInfo.java`):
  - Switched to native **`org.bukkit.Particle`** with a legacy-name mapping table
    (`EXPLOSION_NORMAL→POOF`, `CRIT→CRIT`, …);
  - Particle display moved to the **entity's region thread** (`AsyncHandler.syncAtEntity`);
  - Removed the `xyz.xenondevs:particle` dependency.

### 🟠 Fix C — Sounds without ProtocolLib

- **Change** (`player/rtp/effects/RTPEffect_Sounds.java`): removed the ProtocolLib branch and the
  two `packets/` classes; uses native `playSound(loc, key, SoundCategory.MASTER, …)` (enum names
  first, arbitrary custom sound keys as fallback — functionally equivalent to the old
  ProtocolLib path); removed the `com.comphenix.protocol` dependency.

### 🟠 Fix D — First-join RTP timing (upstream issue [#256](https://github.com/RonanPlugins/BetterRTP/issues/256))

- **Problem**: at `PlayerJoinEvent` the player entity is not fully ready, so the RTP ran too early.
- **Change** (`player/events/Join.java`): deferred via the Paper **entity scheduler**
  `p.getScheduler().run(...)` plus an `isOnline()` guard.

### 🟡 Permission defaults opened up (for testing)

- **Change** (`src/main/resources/plugin.yml`): added `default: true` to `betterrtp.world.*`.
  Upstream only defaults `betterrtp.use`; `canRTP()` additionally requires
  `betterrtp.world.*` / `betterrtp.world.<world>` (default false), which made RTP impossible for
  regular players on servers without a permissions plugin. This change lets all players RTP in
  all worlds by default.

### 🟢 LICENSE bundled

- `pom.xml` now packages the MIT `LICENSE` into the jar at `META-INF/LICENSE`.

### ⚙️ Toolchain adaptation (required to build, not plugin logic)

| Problem | Solution |
|---|---|
| JDK 23+ no longer auto-discovers annotation processors on the classpath | maven-compiler-plugin: explicit `<annotationProcessorPaths>` + `<proc>full</proc>` |
| Original compile target (spigot-api 1.8.8) lacks modern APIs | Compile against **lophine-api 26.1.2** (`fun.bm.lophine:lophine-api:26.1.2.build.638-stable`), Java release 21 |
| Annotations/libs referenced by the modern API | Added `org.jspecify:jspecify`, `net.kyori:adventure-api/4.26.1`, `adventure-key` |
| Corrupted RedProtect artifacts (raw.githubusercontent hijacked via hosts) | Cloned the official mvn-repo branch and mirrored it with a local `file://` mirror |
| User `.m2` not writable under the sandbox | Local repository relocated into the workspace `_tools/m2repo` |

---

## 3. Building

```bat
set MAVEN_OPTS=-Dmaven.repo.local=D:\BETTER~1\_tools\m2repo
call _tools\apache-maven-3.9.16\bin\mvn.cmd -s _tools\maven-settings.xml -B -DskipTests clean package
:: artifact: BetterRTP\target\BetterRTP-3.6.13.jar
```

- Build with JDK 21+ (this machine uses JDK 25); the server runtime requires **Java 25**
  (required by Lophine 26.1.2).
- `_tools/maven-settings.xml` contains the local RedProtect mirror — the `-s` flag is mandatory.

---

## 4. Deploying to another server (same version: Lophine 26.1.2)

**Must copy (1 file)**:
```
lophine-test/plugins/BetterRTP-3.6.13.jar   →  <server>/plugins/
```

**Strongly recommended (Lophine's own config bug)**:
```
lophine-test/lophine_config/lophine_global_config.toml  →  <server>/lophine_config/
```
> On first boot Lophine 26.1.2 generates this TOML with a malformed line (a wrapped comment
> loses its `#`: `n is added to fix this behavior.`), which makes startup config parsing fail.
> This file has been fixed and set **read-only** — keep it read-only (`attrib +R`) after copying,
> otherwise the server re-writes the bug back on the next boot.

**Deploy order**: stop server → copy → start (copying while the server runs fails because the
jar is file-locked by the JVM).

**Auto-generated, do not copy**: everything under `plugins/BetterRTP/` (config.yml, effects.yml,
lang, data, log.txt) and `plugins/bStats/`.
**Do NOT copy**: the `world/` save and `data/database.db` (cross-server data leakage).

**Smoke test**:
1. Log shows `[BetterRTP] Enabling BetterRTP v3.6.13`, no ERROR;
2. No `Thread failed main thread check`;
3. Within ~30 s `plugins/BetterRTP/log.txt` shows `Queue position generated`;
4. In-game `/rtp`: teleport succeeds, particles/sounds/potions work.

---

## 5. Verification results (Lophine 26.1.2-da005b1 + JDK 25)

| Item | Result |
|---|---|
| Plugin enable | ✅ clean, no exceptions |
| Queue pre-generation (the #257 path) | ✅ 57 safe locations (overworld + nether), 0 thread errors |
| Jar contents | ✅ PaperLib/ParticleLib/ProtocolLib removed; FoliaLib kept; LICENSE included |
| Server stability | ✅ repeated clean restarts (with the read-only config workaround) |
| Real-player `/rtp` end-to-end | ⚠️ **pending manual confirmation** (mineflayer only supports up to 1.21.11; Lophine has no SimulatedPlayer API) |

---

## 6. Rollback

- **git tag**: `backup-before-folia-fixes-20260822-100842` (`git checkout <tag>` restores the
  original code)
- **Physical backup**: `_backup/BetterRTP-20260822-100842/`

---

## 7. Known limitations

- Verified only against **Lophine 26.1.2**; other versions (Lophine 26.2, vanilla Folia) are not
  tested — the API surface should be compatible but requires re-validation.
- The compile target moved from "all-version compatible" (spigot 1.8.8) to "modern Paper/Folia"
  (lophine-api 26.1.2), **dropping support for old server versions** (aligned with the
  modernization direction in upstream issue [#260](https://github.com/RonanPlugins/BetterRTP/issues/260)).
- The particle mapping covers common particles; a few exotic ones (ITEM/BLOCK types that require
  ItemStack/BlockData data) may not render and safely fall back to the default particle.
