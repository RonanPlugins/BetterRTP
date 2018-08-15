# Economy ([View Default Economy File](files/economy.yml))
***
## Summary ##
 - [Economy](#economy)
 - [CustomWorlds](#customworlds)

### Economy
Info: Edit basic economy settings
***
  #### Enable ####
  Info: Enable the economy system at all?  
  Value Type: Boolean  
  Default Value: false
  #### Price
  Info: The default price to an [uncustomized](#customworlds) world  
  Value Type: Integer  
  Default Value: 5

### CustomWorlds
Info: Create custom worlds with a toggle system
***
  #### Enabled
  Info: Enable the customworlds charge? All worlds will default to the basic price  
  Value Type: Boolean  
  Default Value: true
  #### Worlds
  Info: A list of sections with the price as their value  
  Value Type: List of Sections  
  Default Value:
  ```yaml
  Worlds:
    world: 5
    world_end: 0
  ```
