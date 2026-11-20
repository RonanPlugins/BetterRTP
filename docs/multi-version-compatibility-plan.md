# BetterRTP multi-version compatibility execution plan

Status: proposed implementation plan; no platform migration has been performed.
Research date: 2026-11-19.

## Objective and constraints

Maintain one codebase with explicitly tested Bukkit/CraftBukkit, Spigot, Paper, and
Folia distributions. Target Minecraft 1.8.8 through the latest verified stable
release, limited to versions on which each platform actually exists. Future
releases become supported only after validation. A successful compile is not a
runtime compatibility guarantee.

Prefer public APIs and maintained upstream libraries. Shading and relocation are
allowed; maintaining our own forks is not the default. Keep BetterRTP free of
NMS/CraftBukkit internals and bytecode rewriting. Avoid scattered reflection and
version-string routing: any necessary capability detection belongs in a small,
tested compatibility boundary or an audited upstream library.
Retain Maven and existing configuration, database, commands, permissions, and
public events unless a separately documented migration is necessary.

Prefer a single user-facing JAR when isolated adapters and upstream libraries
can safely cover the supported runtimes. Use separate builds only when verified
API, bytecode, or dependency constraints justify them. Do not create a fork or permanent branch per Minecraft version.
Do not automatically create a module for every patch release.

## Patterns adopted from existing projects

| Reference | Observed structure | Pattern to adopt |
| --- | --- | --- |
| EssentialsX | BaseProviders, PaperProvider, legacy providers, and an NMSReflectionProvider | Small capability interfaces with isolated implementations; do not copy its NMS approach |
| LuckPerms | common, api, bukkit, bukkit-legacy, and platform loaders | Shared behavior and thin platform entry points; one repository and coordinated releases |
| TAB | shared, platform modules, API-specific submodules, jar assembly, and a compatibility/backport policy | Add build boundaries at demonstrated API changes and publish explicit compatibility ranges |
| SkinsRestorer | shared, multiver, platform modules, test, and universal assembly | Isolate version-sensitive implementation and integration code; centralize packaging and testing |

Borrow verified patterns, not entire implementations or their complexity. Their
use of internal APIs does not establish that BetterRTP needs internal APIs. Their development branches can differ from released artifacts.
Pin reviewed commits when using their implementation details during execution.
Do not copy their code without reviewing applicable licenses.

Sources reviewed:
- https://github.com/EssentialsX/Essentials/blob/2.x/settings.gradle.kts
- https://github.com/LuckPerms/LuckPerms/blob/master/settings.gradle
- https://github.com/NEZNAMY/TAB/blob/master/settings.gradle.kts
- https://github.com/NEZNAMY/TAB/wiki/Compatibility
- https://github.com/SkinsRestorer/SkinsRestorer/blob/dev/settings.gradle.kts

## Current baseline and known gaps

- Core compiles against Spigot 1.8.8 with a Java 8 release target.
- Core shades PaperLib, FoliaLib, BetterSync, and potentially other compile dependencies.
- QueueGenerator now schedules chunk loading and safety inspection at the destination.
  This change is still uncommitted and must be preserved during implementation.
- RTPPlayer still routes chunk loading and player operations through global sync tasks.
- RTPTeleport still uses PaperLib teleportation. RandomLocation contains a PaperLib
  chunk diagnostic. Commented-out calls must be distinguished from executable code.
- Effects, third-party hooks, database driver provisioning, and bundled dependencies
  need inspection beyond the queue fix.
- Reflection exists in optional claim integrations, metrics, packet helpers, and
  JDBC driver loading. Classify these uses; driver loading is not server API probing.
- Existing unit tests are useful, but no full real-server compatibility matrix is established.

## Proposed module boundaries

Keep BetterRTP-Core initially; extract modules incrementally instead of moving
all source files in one change.

- betterrtp-api: stable public events and integration contracts; preserve existing
  package names and verify BetterRTPAddons consumers.
- betterrtp-core: request lifecycle, configuration, queue policy, location selection,
  and orchestration. Initially allow the oldest supported Bukkit API as a baseline;
  do not require a costly rewrite into a completely server-independent model.
- betterrtp-platform-api: scheduling, chunk readiness, teleportation, world-height,
  and effect capabilities expressed without modern platform types.
- betterrtp-platform-bukkit: legacy public Bukkit implementation, Java 8 compatible.
- betterrtp-platform-paper-<baseline>: native asynchronous APIs compiled against
  the earliest verified API in that compatible range.
- betterrtp-platform-folia-<baseline>: public region/entity scheduler implementation.
- betterrtp-integrations: separate optional hook modules where dependency versions
  require isolation; classes must not load before their dependency is available.
- betterrtp-distributions: explicit bootstrap and plugin descriptors per build family.
- tests/compatibility: server harness, fixtures, test companion, and result collection.

Modern adapters can use newer toolchains while the shared/legacy modules retain
Java 8 bytecode and API compatibility. A legacy runtime must never load incompatible
classes or resolve unavailable APIs. If a universal artifact includes isolated modern
classes, prove safe class loading on the oldest runtime and verify server tooling
does not eagerly scan them. Otherwise split artifacts. Confirm real API boundaries
before assigning exact adapter ranges. The module list is conceptual: create only
the boundaries the implementation actually needs.

Keep implementation selection centralized and tested. Package first-party modules
and approved dependencies with Maven Shade as appropriate. Relocate private library
packages to avoid plugin conflicts; do not relocate public integration APIs or bundle
server APIs. Preserve licenses and required service descriptors. Avoid aggressive
JAR minimization unless runtime-loaded classes are covered by tests. Publish one JAR
if validated; otherwise use build-specific metadata and descriptive filenames such
as BetterRTP-<release>-<platform>-<verified-range>.jar.

## Execution work packages

### 1. Inventory and support manifest

Deliver a dependency inventory and a machine-readable support manifest containing:
platform, exact Minecraft version, server build identity/checksum, Java runtime,
adapter, artifact, integration set, and validation status.

Audit all scheduled tasks, future callbacks, world/player access, public events,
plugin lifecycle callbacks, effects, and addon API dependencies. Determine API
and runtime boundaries from actual dependency signatures and executable probes.

Inventory PaperLib, FoliaLib, BetterSync, particle libraries, ProtocolLib usage,
JDBC, metrics, and every optional hook. For each record whether it is provided,
bundled, replaceable with a public API, or requires a declared external plugin.
Keeping BetterSync shaded is allowed; verify its API and runtime compatibility.
Do not introduce an external plugin dependency merely to avoid shading.
For each retained library record upstream activity, supported runtimes, transitive
dependencies, required relocations, and upgrade tests. Prefer an upstream fix or a
maintained replacement over a private fork. Any unavoidable temporary fork needs
a pinned patch, owner, upstream issue, and explicit removal criteria.
If a feature needs an external dependency or a capability downgrade, document the
specific user-visible consequence before implementing that change.

Acceptance: every runtime dependency and compatibility-sensitive operation has
an owner; the initial matrix distinguishes planned, tested, and unsupported rows.

### 2. Establish reproducible real-server tests before refactoring

Create an isolated harness with temporary server directories, localhost-only
ports, pinned Java executables, pinned server builds, timeouts, graceful stop,
and archived logs/results. Obtain binaries through official downloads or official
build tooling as applicable. Handle required EULA acceptance explicitly; do not
silently accept terms or redistribute restricted server binaries.

Initial targets: Spigot 1.8.8, current stable Paper, and current stable Folia.
Add CraftBukkit and historical boundaries as the harness becomes reliable.
Use a small test companion plugin for server-side assertions and compatible test
clients for player flows. Do not assume one bot library supports the whole range.
Keep test-client tooling isolated from the production plugin.

Acceptance: startup and empty-queue scenarios run unattended; tests detect failed
queue generation and plugin enablement errors. A known broken build demonstrates
that the harness fails correctly. Player test tooling can execute and observe /rtp.
Record existing failures rather than declaring the baseline supported.

### 3. Introduce contracts around validated upstream implementations

Define explicit operations for global work, location-owned work, player-owned
work, background I/O, delayed tasks, cancellation, chunk readiness, and teleportation.
Specify callback ownership and completion semantics. A completed future alone
must never imply ownership of a region or player.

First evaluate the supported FoliaLib release against our real-server matrix.
Retain it behind our contracts if it correctly supplies scheduling and teleportation;
do not rewrite it solely to own the implementation. Provide native public API
adapters only for demonstrated gaps. Bukkit fallback operations use main-thread
scheduling and public synchronous chunk/teleport APIs. Define retired-player behavior and rejected scheduling so
requests complete or cancel instead of hanging. Use injectable interfaces in unit
tests; avoid static singleton dependencies in new orchestration code.

Acceptance: existing tests pass and Spigot 1.8.8 scenarios pass using the adapter.

### 4. Close adapter gaps and migrate the RTP flow

Use public async APIs for Paper, directly or through validated libraries.
Use location schedulers for Folia world
inspection, entity schedulers for player operations, and native async teleportation.
Never block a tick thread waiting for a future. Ensure a chunk remains usable
through the region-owned inspection; define ticket/lifetime behavior where needed.

Migrate in this order: QueueGenerator, RTPPlayer search and safety checks,
RTPTeleport, countdowns and post-teleport effects, diagnostics, then remaining tasks.
Capture required player state on its owner, inspect destinations on their owner,
then return to the player owner and revalidate the operation before charging and
teleporting. Define failure/refund and cooldown semantics explicitly.

Audit protection hooks for the thread required by their public APIs. Do not assume
that a destination-region callback makes every third-party integration safe.
Preserve event cancellation and event order while documenting execution context.
Use lifecycle generation tokens/cancellation tracking so reload, quit, disable,
and late completions cannot revive an old request or queue generator.

Acceptance: real RTP, cancellation, cross-region, and cross-world tests pass;
all scheduling/loading/teleport calls pass through tested compatibility contracts.
Replace PaperLib's broken version-dependent paths without creating a private PaperLib
fork by default. FoliaLib may remain as a shaded, pinned upstream dependency.

### 5. Complete dependency and integration separation

Remove superseded libraries only after their callers migrate; retain validated
upstream libraries and their required relocations. Replace version-sensitive
particles/sounds/titles with public API implementations where possible; explicitly
report unavailable optional capabilities instead of attempting internal access.
Resolve BetterSync delivery and JDBC provisioning from the inventory. Keep external
plugin dependencies declared and optional hooks isolated by supported API version.
Compile against API artifacts, removing the full Spigot server dependency if unused.

Acceptance: inspect final JAR contents and dependency trees to verify approved
bundling, relocations, service resources, licenses, and runtime compatibility; startup works with optional plugins absent, present, and unsupported.
No silent loss of protection/economy behavior is acceptable.

### 6. Expand the matrix and enforce architectural rules

Candidate boundary tests: 1.8.8, 1.12.2, 1.13.2, 1.16.5, 1.18.2, 1.19.4,
1.20.4, 1.20.6, and supported 1.21.x/26.x releases. These are starting probes,
not a claim that all intervening versions work. Folia rows exist only for released
Folia versions. Current endpoints and Java requirements must be verified during setup.

Every change: unit tests, all artifact builds, API/bytecode checks, and a small
real-server smoke matrix. Nightly/release: expanded exact-version manifest and
integration scenarios. Test each claimed release, or explicitly narrow the claim.

Enforce forbidden internal API references in BetterRTP, compatibility logic outside
its designated boundary, unapproved dependency bundling, missing required
relocations, modern API leakage into legacy modules, and unsupported bytecode/API
usage. Audit retained libraries for internal access and reflective behavior; document
necessary internals rather than assuming a library automatically meets our policy. Existing violations get explicit migration entries;
new violations fail immediately. Audit dependencies as well as our own source.

Acceptance: deliberately introducing a forbidden reference or wrong bytecode target
fails CI; each distribution has machine-readable test results.

### 7. Release and support policy

One coordinated version and changelog across distributions. Generate a download
compatibility table from the manifest, with exact tested versions and required Java.
Only tested combinations are supported. Add new stable Minecraft releases through
an API audit and matrix run; avoid automatically claiming future compatibility.

Keep a previous known-good artifact available for each supported family. Preserve
config/database compatibility where possible and test upgrade paths with copied
fixtures. Publish explicit migration notes if formats or external dependencies change.

Acceptance: a clean checkout produces all artifacts and reports, and a maintainer
can select the correct download without reading code.

## Required behavioral assertions

- Fresh startup: plugin enables and queue reaches its configured target in a bounded time.
- RTP: real player arrives within configured bounds with safe ground/headroom,
  matching biome/world constraints; test queued and uncached destinations.
- World access: normal/Nether/End, negative coordinates, world-height boundaries,
  loaded/unloaded/generated/ungenerated chunks, and cross-world teleportation.
- Failure: no safe location, cancelled events, chunk/teleport/database failure,
  denied permission, insufficient funds, and unavailable optional dependencies.
- Lifecycle: movement cancellation, quit/rejoin, duplicate commands, config reload,
  shutdown, and late future completion; no retained request or resurrected work.
- Folia: independent distant regions, simultaneous players, and player movement
  between capture and completion; no wrong-thread access or cross-region deadlock.
- Integrations: protected destinations rejected, charges/refunds and cooldowns applied
  exactly as documented, public events remain usable by BetterRTPAddons.

Logs are supporting evidence. Assert outcomes and resource cleanup, not merely the
absence of exceptions. Deterministic worlds/seeds and bounded retries reduce flakes.
Track stress-test performance separately from functional correctness.

## First implementation slice

Execute packages 1 and 2 first: inventory + pinned three-server baseline harness.
Resolve whether validated upstream libraries enable one artifact before committing
to multiple distributions. Then implement the minimal contracts needed for fresh-start queue generation and
one /rtp request. Complete that vertical slice on all three initial platforms before
moving optional effects and integrations. No broad source reorganization first.

The exact number of distributions, API boundaries, external dependency delivery,
and feasible client tooling remain research outcomes, not assumptions hidden in code.
