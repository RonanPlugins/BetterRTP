<p align="center">
  <b><a>Welcome to BetterRTP's repository!</a></b>
</p>

## Web configuration editor

[Open the BetterRTP editor](https://www.minecentral.net/editor/betterrtp) to start with defaults or upload config.yml. Edit world settings, teleport ranges, cooldowns, and supported lists.

1. Run `/rtp editor` on your server and open the private link it returns.
2. Edit the form or YAML and choose **Save and get sync command**.
3. Run `/betterrtp editorsync <token>` in Minecraft to apply that saved version.

Console and players with `betterrtp.editor` (default: op) can use both commands. No account or premium is required for editing, downloading, or command sync. Upload links last 24 hours; each sync command works once and expires after 15 minutes. Keep links and commands private.

Sync validates config.yml, saves a backup in `editor-backups`, then replaces the file and reloads the plugin. Nothing changes until the sync command runs. If reload fails after writing, the new file remains in place and Minecraft reports the error.

Shared behavior, deployment, and verification are documented in the [webapp repository](https://github.com/RonanPlugins/BetterRTP-Webapp/blob/main/docs/web-editor.md).

## Where's the Lang files?/Want to Contribute translating?  
All language files are located [here](BetterRTP-Core/src/main/resources/lang)
feel free to fork one of the language files and help translate!

## Libraries
BetterRTP uses and is compiled with the following libraries:

- [ParticleLib](https://github.com/ByteZ1337/ParticleLib) (included) - Particles library by ByteZ1337. Find all supported particles [here](https://github.com/ByteZ1337/ParticleLib/blob/master/src/main/java/xyz/xenondevs/particle/ParticleEffect.java)
- [PaperLib](https://github.com/PaperMC/PaperLib) (included) - Library for interfacing with PaperMC specific APIs, used for async chunk loading.
- [FoliaLib](https://github.com/TechnicallyCoded/FoliaLib) (included) - Library for interfacing with Folia specific APIs, used for cross-platform timers.

## Build instructions on Ubuntu

From this directory, run `mvn clean package` to build BetterRTP followed by BetterRTP Addons. Maven resolves Addons against the BetterRTP built in the same run; a separate `install` step is unnecessary.

- BetterRTP JAR: `dist/BetterRTP-3.6.13.jar`
- Addons JAR: `dist/BetterRTPAddons-1.8.10.jar`

Both server-ready JARs are collected in `dist/`. `mvn clean` removes each selected plugin’s previous JARs from this folder; intermediate build files stay in `target/`. The `dev` profile still outputs directly to its configured server plugins folder.

The root `pom.xml` aggregates both modules. Core plugin dependencies and build settings live in `BetterRTP-Core/pom.xml`; its sources live in `BetterRTP-Core/src/`. To build only the core plugin, use `mvn -pl :BetterRTP clean package`. Existing `-Deditor.website=...`, `-Deditor.buildSuffix=...`, and `-Pdev` options remain available.

## Where's the Wiki?  
The wiki is available [here](../../wiki)!
    
<p align="center">
  <b>Chat with us on Discord</b><br/>
  <a href="https://discord.gg/8Kt4wKm"><img src="https://img.shields.io/discord/182633513474850818.svg?longCache=true&style=flat-square&label=Discord" alt="Discord" /></a><br/>
  <b>Have a Suggestion? Make an issue!</b><br/>
  <a href="../../issues"><img src="https://img.shields.io/github/issues-raw/SuperRonanCraft/BetterRTP.svg?longCache=true&style=flat-square&label=Issues" alt="GitHub issues" /></a><br/>
  <br/>
  <a href="https://www.spigotmc.org/resources/36081/">Thank you for viewing the Wiki for BetterRTP!</a><br/>
  <i><a>Did this wiki help you out? Please give it a <b>Star</b> so I know it's getting use!</a></i><br/>
  <br/>
  <b><i><a href="https://www.spigotmc.org/resources/authors/superronancraft.13025/">Check out my other plugins!</a></i></b>
</p>

## Shared editor library

The web editor uses [BetterSync](https://github.com/RonanPlugins/BetterSync),
downloaded by Maven from JitPack as `com.github.RonanPlugins:BetterSync:1.1.0`.
No local BetterSync project or manual Maven install is required. Release `1.1.0` is published on JitPack. Update `bettersync.version` in the plugin and editor-test
POMs together when upgrading. Shade bundles and relocates the library into the
plugin JAR; server owners do not install a separate BetterSync plugin.

The plugin retains its scheduler, permissions, reload callbacks, and filtered
`editor.website` setting. Editor transport and configuration validation are
maintained in BetterSync.

### Editor schema ownership

Each plugin module owns `src/main/resources/META-INF/editor-schema.json`. Edit rules here,
not in the website or BetterSync's deprecated legacy profiles. `EditorSchema.load` reads
this resource from the plugin JAR; the client sends its independent `schemaVersion` and
checks the version returned by the website before applying changes.

Publish the same schema bytes as a public BetterSync GitHub release asset, then add the
asset URL and SHA-256 to the website's `editor-schemas.lock.json`. Keep old versions in the
lock for older servers. A changed rule requires a new schema version; plugin bug fixes
that do not affect configuration can retain the version. Schema publication does not
publish the rest of a private plugin repository.

Deploy the matching website and its schema-version database migrations before installing
the new plugin JARs. Schema resources are bundled automatically by Maven; no sibling
plugin/library checkout is needed to compile this project.
