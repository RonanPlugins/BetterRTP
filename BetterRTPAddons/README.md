<p align="center">
  <b><a>Welcome to BetterRTPAddons's repository!</a></b>
</p>

BetterRTP-Addons is a paid resource, adding optional middleware to BetterRTP to help financially support the developement of BetterRTP.
But this can be compiled free of charge if you know how to compile maven resources!

## Web configuration editor

[Open the BetterRTP Addons editor](https://www.minecentral.net/editor/betterrtp-addons) to start with defaults or upload config.yml. Edit addon toggles, menus, command lists, and flashback warnings. Install the matching updated BetterRTP JAR too: Addons shares its editor bridge.

1. Run `/rtp addons editor` on your server and open the private link it returns.
2. Edit the form or YAML and choose **Save and get sync command**.
3. Run `/betterrtp addons editorsync <token>` in Minecraft to apply that saved version.

Console and players with `betterrtp.addon.editor` (default: op) can use both commands. No account or premium is required for editing, downloading, or command sync. Upload links last 24 hours; each sync command works once and expires after 15 minutes. Keep links and commands private.

Sync validates config.yml, saves a backup in `editor-backups`, then replaces the file and reloads the plugin. Nothing changes until the sync command runs. If reload fails after writing, the new file remains in place and Minecraft reports the error.

Shared behavior, deployment, and verification are documented in the [webapp repository](https://github.com/RonanPlugins/BetterRTP-Webapp/blob/main/docs/web-editor.md#addons-deployment).

## Where's the Lang files?/Want to Contribute translating?  
All language files are located [here](src/main/resources/lang)
feel free to fork one of the language files and help translate!

## Libraries
BetterRTPAddons uses and is compiled with the following libraries:

- [PaperLib](https://github.com/PaperMC/PaperLib) (included) - Library for interfacing with PaperMC specific APIs, used for async chunk loading.

## Where's the Wiki?  
The wiki is available [here](../../../wiki)!
    
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

Build both BetterRTP and Addons together by running `mvn clean package` from the parent BetterRTP directory. Both finished JARs are written to the parent directory’s `dist/` folder.
