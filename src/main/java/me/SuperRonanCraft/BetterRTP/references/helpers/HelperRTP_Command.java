package me.SuperRonanCraft.BetterRTP.references.helpers;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;

public class HelperRTP_Command {

    public static void registerCommand(RTPCommand cmd, boolean forced) {
        BetterRTP.getInstance().getCmd().registerCommand(cmd, forced);
    }

}
