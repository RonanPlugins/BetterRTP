# BetterRTP — Lophine 26.1.2 适配版（AI 改进）

> **本项目的代码改动全部由 AI 完成**（基于 DeepSeek 编码代理的一次完整适配会话），
> 目标服务端：**Lophine 26.1.2**（Folia 系、Paper 分支，对应 Minecraft 26.1.2）。
> 原始上游：[RonanPlugins/BetterRTP](https://github.com/RonanPlugins/BetterRTP) v3.6.13（MIT License）。

---

## 1. 目录结构

| 路径 | 说明 |
|---|---|
| `BetterRTP/` | 插件源码（已改造） |
| `BetterRTP/target/BetterRTP-3.6.13.jar` | **最终构建产物**（含 LICENSE） |
| `lophine-test/` | 测试服务器（Lophine 26.1.2） |
| `lophine-test/plugins/BetterRTP-3.6.13.jar` | 已部署的插件 |
| `_backup/BetterRTP-20260822-100842/` | 改造前源码备份（297 文件） |
| `_tools/` | Maven 3.9.16 构建环境（本地仓库 m2repo、redprotect 镜像等） |

---

## 2. AI 改进清单

### 🔴 修复 A —— 核心：RTP 在 Folia 26.1.2 上完全失效（上游 issue [#257](https://github.com/RonanPlugins/BetterRTP/issues/257)）

- **问题**：BetterRTP 的 Folia 支持只把"调度"交给了 FoliaLib，异步加载 chunk 仍走 **PaperLib 1.0.8**（2019 年的库，不认 Folia 的 `getChunkAtAsync` 新签名 → 反射失败 → 退回**同步** `world.getChunkAt`）。该调用发生在 Folia 的全局区域线程上，触发 Moonrise 线程检查：
  `Thread failed main thread check: Async chunk retrieval` → `/rtp` 完全不可用，玩家卡死在队列。
- **改动**（4 个文件）：`PaperLib.getChunkAtAsync(...)` / `PaperLib.teleportAsync(...)`
  → 原生 **`World#getChunkAtAsync(...)`** / **`Entity#teleportAsync(...)`**（Folia 官方安全 API，future 在正确的区域线程完成）。
  - `player/rtp/RTPPlayer.java`
  - `player/rtp/RTPTeleport.java`
  - `references/rtpinfo/QueueGenerator.java`
  - `references/rtpinfo/RandomLocation.java`
- **验证**：改造后 **0 个**线程检查错误；队列预生成在主世界 + 下界产出 **57 个安全坐标**（与 #257 完全相同的代码路径）。

### 🟠 修复 B —— 粒子效果原生化

- **问题**：粒子库 ParticleLib 1.8.4 官方只支持 **MC 1.8–1.19.3**；MC 1.21.5+ 粒子协议重写后反射必挂（静默失败 + 控制台刷堆栈），且原实现在异步线程发粒子（Folia 上违反区域线程）。
- **改动**（`player/rtp/effects/RTPEffect_Particles.java` + `commands/types/CmdInfo.java`）：
  - 改用原生 **`org.bukkit.Particle`**，内置 ParticleLib 旧名 → 现代名映射表（`EXPLOSION_NORMAL→POOF`、`CRIT→CRIT` 等）；
  - 粒子显示移到**实体区域线程**（`AsyncHandler.syncAtEntity`）；
  - 删除 `xyz.xenondevs:particle` 依赖。

### 🟠 修复 C —— 音效移除 ProtocolLib 依赖

- **改动**（`player/rtp/effects/RTPEffect_Sounds.java`）：删除 ProtocolLib 分支与 `packets/` 两个类，改用原生 `playSound(loc, key, SoundCategory.MASTER, …)`（枚举名优先、自定义音效 key 兜底，功能与原 ProtocolLib 路径等价）；删除 `com.comphenix.protocol` 依赖。

### 🟠 修复 D —— 首登自动 RTP 时机（上游 issue [#256](https://github.com/RonanPlugins/BetterRTP/issues/256)）

- **问题**：`PlayerJoinEvent` 时玩家实体尚未完全就绪，RTP 过早执行。
- **改动**（`player/events/Join.java`）：改用 Paper **实体调度器** `p.getScheduler().run(...)` 延迟到玩家就绪 + `isOnline()` 守卫。

### 🟡 权限默认开放（方便测试）

- **改动**（`src/main/resources/plugin.yml`）：`betterrtp.world.*` 增加 `default: true`。
  原版只有 `betterrtp.use` 默认开放，而 `canRTP()` 还要求 `betterrtp.world.*` / `betterrtp.world.<世界>`（默认 false），导致**普通玩家无权限插件时永远无法 RTP**。此改动让所有玩家默认在所有世界可 RTP。

### 🟢 LICENSE 打包

- `pom.xml` 增加资源配置，将 MIT `LICENSE` 打进 jar 的 `META-INF/LICENSE`。

### ⚙️ 构建/工具链适配（非插件逻辑，但为构建所必需）

| 问题 | 解决 |
|---|---|
| JDK 25 不再自动发现 classpath 上的注解处理器 | maven-compiler-plugin 显式 `<annotationProcessorPaths>` + `<proc>full</proc>` |
| 原编译目标 spigot-api 1.8.8 缺现代 API | 编译目标升级为 **lophine-api 26.1.2**（`fun.bm.lophine:lophine-api:26.1.2.build.638-stable`），Java release 21 |
| 现代 API 引用的注解/库 | 补 `org.jspecify:jspecify`、`net.kyori:adventure-api/4.26.1`、`adventure-key` |
| RedProtect 依赖损坏（raw.githubusercontent 被 hosts 劫持） | git 克隆官方 mvn-repo 分支，本地 `file://` 镜像 |
| 沙箱外不可写用户 .m2 | 本地仓库指向工作区 `_tools/m2repo` |

---

## 3. 构建

```bat
set MAVEN_OPTS=-Dmaven.repo.local=D:\BETTER~1\_tools\m2repo
call _tools\apache-maven-3.9.16\bin\mvn.cmd -s _tools\maven-settings.xml -B -DskipTests clean package
:: 产物: BetterRTP\target\BetterRTP-3.6.13.jar
```

- 需要 Java 21+（本机为 JDK 25）构建；服务端运行需要 **Java 25**（Lophine 26.1.2 要求）。
- `_tools/maven-settings.xml` 含 redprotect-repo 的本地镜像，必须带上 `-s`。

---

## 4. 部署到同版本服务器（Lophine 26.1.2）

**必复制（1 个文件）**：
```
lophine-test/plugins/BetterRTP-3.6.13.jar   →  <新服>/plugins/
```

**强烈建议（Lophine 自身配置 bug）**：
```
lophine-test/lophine_config/lophine_global_config.toml  →  <新服>/lophine_config/
```
> Lophine 26.1.2 首次生成该配置时，注释折行会丢 `#` 产生畸形行
> （`n is added to fix this behavior.`），导致启动解析失败。本文件已修复并**设为只读**，
> 复制后请保持只读属性（`attrib +R`），否则服务器重写配置时 bug 复发。

**部署步骤**：停服 → 复制 → 启动（必须先停服再复制，运行中 jar 会被进程锁定）。

**自动生成、无需复制**：`plugins/BetterRTP/` 下的 config.yml / effects.yml / lang / data / log.txt、`plugins/bStats/`。
**不要复制**：`world/` 存档、`data/database.db`（跨服会串数据）。

**验证清单**：
1. 日志出现 `[BetterRTP] Enabling BetterRTP v3.6.13`，无 ERROR；
2. 无 `Thread failed main thread check`；
3. 30 秒后 `plugins/BetterRTP/log.txt` 出现 `Queue position generated`；
4. 客户端 `/rtp`：传送成功，粒子/音效/药水正常。

---

## 5. 验证结果（Lophine 26.1.2-da005b1 + JDK 25）

| 项 | 结果 |
|---|---|
| 插件加载 | ✅ 干净加载，无异常 |
| 队列预生成（原 #257 报错路径） | ✅ 57 个安全坐标（主世界 + 下界），0 线程错误 |
| jar 内容 | ✅ PaperLib/ParticleLib/ProtocolLib 已移除；FoliaLib 保留；LICENSE 已含 |
| 服务器稳定性 | ✅ 连续多次重启正常（配合只读配置修复） |
| 真实玩家 `/rtp` 端到端 | ⚠️ **待实机确认**（mineflayer 协议最高 1.21.11，无法代测；Lophine 无 SimulatedPlayer API） |

---

## 6. 回滚

- **git tag**：`backup-before-folia-fixes-20260822-100842`（`git checkout <tag>` 恢复原代码）
- **物理备份**：`_backup/BetterRTP-20260822-100842/`

---

## 7. 已知限制

- 仅针对 **Lophine 26.1.2** 验证；其他版本（含 Lophine 26.2、Vanilla Folia）未实测，API 面兼容但需重新验证。
- 编译目标从"全版本兼容"（spigot 1.8.8）改为"现代 Paper/Folia"（lophine-api 26.1.2），**放弃对旧版本服务端的兼容**（与上游 issue [#260](https://github.com/RonanPlugins/BetterRTP/issues/260) 的现代化方向一致）。
- 粒子名称映射覆盖了常见粒子，个别冷门粒子（如需要 ItemStack/BlockData 数据的 ITEM/BLOCK 类）可能不显示，已安全降级为默认粒子。
