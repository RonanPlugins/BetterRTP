<p align="center">
  <b><a>Welcome to BetterRTP's repository!</a></b>
</p>

## Where's the Lang files?/Want to Contribute translating?  
All language files are located [here](src/main/resources/lang)
feel free to fork one of the language files and help translate!

## Libraries
BetterRTP uses and is compiled with the following libraries:

- [Paper API](https://github.com/PaperMC/Paper) (provided) - Compile-time baseline: 1.20.1.
- [FoliaLib](https://github.com/TechnicallyCoded/FoliaLib) (included) - Selects Bukkit, Paper, or Folia scheduling and teleport APIs at runtime.

## Compatibility

BetterRTP and BetterRTPAddons target Minecraft 1.20+ with Java 17 bytecode and `api-version: '1.20'`. Run each server with the Java version required by that server (for example Java 21 for 1.21, Java 25 for 26.1). Build with JDK 21 or newer because some optional integration APIs require it.

CI builds both jars, runs regression tests, and smoke-tests Paper 1.20.1, Paper 1.21.8, Folia 1.21.8, and Folia 26.1.2 using pinned server artifacts. The client confirms first-join and delayed RTP, sounds, particles, and potion effects. Other 1.20+ releases and third-party integrations still need their own runtime validation; future server compatibility is not guaranteed.

First-join RTP waits for player data to load, then runs on the player's owning scheduler. It retains the configured delay bypass. Sounds use Bukkit's version-aware API, accepting both legacy names (`ENTITY_GENERIC_EXPLODE`) and resource keys (`minecraft:entity.generic.explode`). The legacy `ProtocolLibSound` setting no longer selects a packet implementation.

Particles use Bukkit with legacy-name translation; /rtp info particles lists supported types.

## Build instructions on Ubuntu

mvn clean install

The file will be in the Target file.

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
