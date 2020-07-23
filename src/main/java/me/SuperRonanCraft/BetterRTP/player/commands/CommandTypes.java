package me.SuperRonanCraft.BetterRTP.player.commands;

import me.SuperRonanCraft.BetterRTP.player.commands.types.*;

public enum CommandTypes {
    BIOME(new CmdBiome()),
    HELP(new CmdHelp()),
    INFO(new CmdInfo()),
    PLAYER(new CmdPlayer()),
    RELOAD(new CmdReload()),
    //SETTINGS(new CmdSettings()),
    VERSION(new CmdVersion()),
    WORLD(new CmdWorld());

    private RTPCommand cmd;

    CommandTypes(RTPCommand cmd) {
        this.cmd = cmd;
    }

    public RTPCommand getCmd() {
        return cmd;
    }
}
