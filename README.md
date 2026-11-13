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

## Support log uploads

The logger, info, and queue upload actions share logs through `https://mclo.gs`. Uploads are manual and use the mclogs JSON API at `https://api.mclo.gs/1/log`. Files larger than 5 MiB are rejected locally. Uploads have connection/read timeouts, and failed or malformed responses show an upload error instead of a broken link.

No self-hosted log service, DNS setup, account pairing, or API key is required. Logs are uploaded to the third-party mclo.gs service only when the administrator chooses the upload action. Server owners need an updated BetterRTP JAR to use this endpoint.

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
downloaded by Maven from JitPack as `com.github.RonanPlugins:BetterSync:2.0.0`.
No local BetterSync project or manual Maven install is required. Release `2.0.0` is published on JitPack. Update `bettersync.version` in the plugin and editor-test
POMs together when upgrading. Shade bundles and relocates the library into the
plugin JAR; server owners do not install a separate BetterSync plugin.

The plugin retains its scheduler, permissions, reload callbacks, and filtered
`editor.website` setting. Editor transport and configuration validation are
maintained in BetterSync.

### Editor schema ownership

Each plugin module owns `src/main/resources/META-INF/editor-schema.json` and packages
it in its JAR. It describes field types, lists, defaults and form collection hints.
BetterSync checks basic types and required sections; it does not enforce business
rules such as ranges, material names or relationships between fields.

When a stable GitHub release is published, `.github/workflows/publish-editor-schema.yml`
attaches the schema from that exact release tag to this repository's release. BetterRTP
publishes both Core and Addons schemas. The workflow uses the built-in GitHub token;
no cross-repository publishing secret is needed. Release tags must include the workflow
and the schema resource. Do not replace a published schema; increment `schemaVersion`
when changing field metadata.

Website builds discover this repository's latest stable release and download its schema.
No plugin checkout or local Maven install is needed. The website uses a build-only token
with Contents read access for private repositories. It must be rebuilt after the schema
publication workflow succeeds. An unsupported plugin schema version is rejected until
the corresponding website build is deployed.

Use the website's schema-version database migrations before deploying these clients.

### Automatic Maven releases

`.github/workflows/release.yml` runs on pushes to `online-editor` (or manually from
that branch). It reads only the Core POM version, builds and verifies BetterRTP Core,
runs the editor tests, and publishes a stable GitHub release with exactly one plugin
JAR. For the current Core POM the title is `BetterRTP 3.6.13` and the tag is `v3.6.13`.
This single-JAR release is compatible with the website's GitHub release importer.

Both Core and Addons editor-schema JSON files remain attached for the website editor.
Copying Addons metadata does not compile or publish its plugin binary. GitHub's Maven
test workflow also selects Core only. Build and upload the Addons plugin manually;
do not attach its JAR to the automated BetterRTP release, which must contain one JAR.
Local root Maven builds still build both modules.

An existing release is left untouched. Bump `BetterRTP-Core/pom.xml` to publish the next
Core release. An Addons-only version bump does not create a new Core release.
SNAPSHOT versions are rejected. GitHub's built-in token needs Contents write
permission, which the workflow declares. No extra publishing token is required.
