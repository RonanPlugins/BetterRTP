package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.player.commands.types.*;

public enum RTPCommandType {
    BIOME(new CmdBiome()),
    EDIT(new CmdEdit()),
    HELP(new CmdHelp()),
    INFO(new CmdInfo()),
    PLAYER(new CmdPlayer()),
    RELOAD(new CmdReload()),
    LOCATION(new CmdLocation()),
    //SETTINGS(new CmdSettings(), true),
    TEST(new CmdTest(), true),
    VERSION(new CmdVersion()),
    WORLD(new CmdWorld());

    private final RTPCommand cmd;
    private boolean debugOnly = false;

    RTPCommandType(RTPCommand cmd) {
        this.cmd = cmd;
    }

    RTPCommandType(RTPCommand cmd, boolean debugOnly) {
        this.cmd = cmd;
        this.debugOnly = debugOnly;
    }

    public boolean isDebugOnly() {
        return debugOnly;
    }

    public RTPCommand getCmd() {
        return cmd;
    }
}
