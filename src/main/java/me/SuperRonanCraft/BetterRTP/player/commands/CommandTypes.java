package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.player.commands.types.*;

public enum CommandTypes {
    BIOME(new CmdBiome()),
    EDIT(new CmdEdit()),
    HELP(new CmdHelp()),
    INFO(new CmdInfo()),
    PLAYER(new CmdPlayer()),
    RELOAD(new CmdReload()),
    SETTINGS(new CmdSettings(), true),
    TEST(new CmdTest(), true),
    VERSION(new CmdVersion()),
    WORLD(new CmdWorld());

    private final RTPCommand cmd;
    private boolean debugOnly = false;

    CommandTypes(RTPCommand cmd) {
        this.cmd = cmd;
    }

    CommandTypes(RTPCommand cmd, boolean debugOnly) {
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
