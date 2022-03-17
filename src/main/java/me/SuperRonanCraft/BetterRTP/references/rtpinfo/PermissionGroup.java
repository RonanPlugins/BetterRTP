package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPermissionGroup;

import java.util.HashMap;

public class PermissionGroup {

    String groupName;
    HashMap<String, WorldPermissionGroup> worlds = new HashMap<>();

    public PermissionGroup(String group) {

    }

}
