# Config ([View Default Config File](files/config.yml))
***
## Summary ##
- [Disable-Updater](#disable-updater)
- [Language-File](#language-file)
- [Settings](#settings)
- [Default](#default)
- [BlacklistedBlocks](#blacklistedblocks)
- [DiabledWorlds](#disabledworlds)
- [CustomWorlds](#customworlds)
- [Override](#override)

### Language-File ###
***
Info: Set the language, accessible at the [lang](files/lang) file  
Values: String  
Default Value: 'en.yml'

### Settings ###
Info: Basic Plugin settings that can be disabled/changed
***
  #### RespectWorldGuard ####
  Info: Should the plugin respect worldguard regions when attempting to teleport a player  
  Value Type: Boolean  
  Default Value: true  
  **COMING SOON**
  ~~#### RespectGriefprevention ####
  Info: Should the plugin respect worldguard regions when attempting to teleport a player  
  Value Type: Boolean  
  Default Value: true  ~~
  **COMING SOON**
  #### MinRadius ####
  Info: The default minimum value if worldborder is not set  
  Value Type: Integer  
  Default Value: 25  
  #### MaxAttempts ####
  Info: The maximum amount of tries to teleport before giving up  
  Value Type: Integer  
  Default Value: 15
  #### Cooldown ####
  Info: The amount of time \(in seconds\) someone without the 'betterrtp.bypass.cooldown' permission must wait before rtp'ing again  
  Value Type:
  ```yaml
  Settings:
    Cooldown:
      Enabled: Boolean  
      Time: Integer
  ```
  Default Value: 
  ```yaml 
  Settings:
    Cooldown:
      Enabled: true 
      Time: 60
  ```
  #### Delay ####
  Info: Delay time someone types /rtp, to actually rtp'ing  
  Value Type:   
  ```yaml
  Settings:
    Delay:
      Time: Integer
      CancelOnMove: Boolean
  ```
  Default Value:  
  ```yaml
  Settings:
    Delay:
      Time: 5
      CancelOnMove: true
  ```
  
### Default ###
Info: Set defaults when a world is not [customized](#customworlds) below
***
  #### UseWorldBorder ####
  Info: Use the vanilla worldborder of a world? \(Must set worldborder with the vanilla '/worldborder' command!\)  
  Value Type: Boolean  
  Default Value: true
  #### Biomes ####
  Info: Set a list of biomes you'd like to limit rtp to teleport to, view more biomes [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/block/Biome.html)  
  Value Type: List  
  Default Value: '[]'
  #### MaxRadius ####
  Info: Maximum allowed radius from [CenterX](#centerx) and [CenterZ](#centerz) if useworldborder is false  
  Value Type: Integer  
  Default Value: 100
  #### MinRadius ####
  Info: Minimum allowed radius from the worldborder or center
  Value Type: Integer  
  Default Value: 100
  #### CenterX ####
  Info: Center X position that you'd like BetterRTP to function in any world if useworldborder is false  
  Value Type: Integer  
  Default Value: 0
  #### CenterZ ####
  Info: Center Z position that you'd like BetterRTP to function in any world if useworldborder is false  
  Value Type: Integer  
  Default Value: 0

### BlacklistedBlocks ###
Info: A list of blocks you'd like to not have BetterRTP teleport a player onto  
Value Type: List  
Default Value:
```yaml
BlacklistedBlocks:
  - stationary_lava
  - lava
  - cactus
```

### DisabledWorlds ###
***
Info: A list of worlds to NOT allowed BetterRTP to function, only [overriding](#override) a world will allow it to function  
Value Type: List  
Default Value:
```yaml
DiabledWorlds:
  - world_nether
  - world_the_end
```

### CustomWorlds ###
***
Info: Make custom worlds to have different values than the [default](#default)
Value Type: List of Sections Keys
Default Value:
```yaml
CustomWorlds:
  world:
    UseWorldBorder: false
    MaxRadius: 1000
    MinRadius: 100
    CenterX: 0
    CenterZ: 0
  world_nether:
    MaxRadius: 100000
    MinRadius: 1000
    CenterX: 123
    CenterZ: -123
    Biomes:
      - 'desert'
      - 'forest'
```
  
### Override ###
***
Info: Redirect one worlds '/rtp' command to another world
Value Type: List of Sections
Default Value:
```yaml
Override:
  world_nether: 'world'
  world_the_end: 'world'
```
