package me.SuperRonanCraft.BetterRTP.player.events;

import me.SuperRonanCraft.BetterRTP.player.commands.CommandTypes;
import me.SuperRonanCraft.BetterRTP.references.file.FileBasics;
import me.SuperRonanCraft.BetterRTP.Main;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

class Interact {

    private boolean enabled;
    private String title, coloredTitle;

    void load() {
        String pre = "Settings.";
        FileBasics.FILETYPE file = Main.getInstance().getFiles().getType(FileBasics.FILETYPE.SIGNS);
        enabled = file.getBoolean(pre + "Enabled");
        title = file.getString(pre + "Title");
        coloredTitle = Main.getInstance().getText().color(title);
    }

    void event(PlayerInteractEvent e) {
        if (enabled && e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && isSign(e.getClickedBlock())) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            if (sign.getLine(0).equals(coloredTitle)) {
                String command = sign.getLine(1).split(" ")[0];
                if (cmd(sign.getLines()).split(" ")[0].equalsIgnoreCase("") || cmd(sign.getLines()).split(" ")[0].equalsIgnoreCase("rtp")) {
                    action(e.getPlayer(), null);
                    return;
                } else
                    for (CommandTypes cmd : CommandTypes.values())
                        if (command.equalsIgnoreCase(cmd.name())) {
                            action(e.getPlayer(), cmd(sign.getLines()).split(" "));
                            return;
                        }
                e.getPlayer().sendMessage(Main.getInstance().getText().colorPre("&cError! &7Command &a"
                        + Arrays.toString(cmd(sign.getLines()).split(" ")) + "&7 does not exist! Defaulting command to /rtp!"));
            }
        }
    }

    void createSign(SignChangeEvent e) {
        if (enabled && Main.getInstance().getPerms().getSignCreate(e.getPlayer())) {
            String line = e.getLine(0);
            if (line != null && (line.equalsIgnoreCase(title) ||
                    line.equalsIgnoreCase("[RTP]"))) {
                e.setLine(0, coloredTitle != null ? coloredTitle : "[RTP]");
                Main.getInstance().getText().getSignCreated(e.getPlayer(), cmd(e.getLines()));
            }
        }
    }

    private void action(Player p, String[] line) {
        Main.getInstance().getCmd().commandExecuted(p, "rtp", line);
    }

    private String cmd(String[] signArray) {
        String actions = "";
        for (int i = 1; i < signArray.length; i++) {
            String line = signArray[i];
            if (line != null && !line.equals(""))
                if (actions.equals(""))
                    actions = line;
                else
                    actions = actions.concat(" " + line);
        }
        return actions;
    }

    private boolean isSign(Block block) {
        return block.getState() instanceof Sign;
    }
}
