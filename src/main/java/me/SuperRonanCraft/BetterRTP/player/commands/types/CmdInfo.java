package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.Main;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.worlds.RTP_WORLD_TYPE;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdInfo implements RTPCommand {

    public void execute(CommandSender sendi, String label, String[] args) {
        List<String> info = new ArrayList<>();
        info.add("&e&m-----&6 BetterRTP Info &e&m-----");
        Main pl = Main.getInstance();
        for (World w : Bukkit.getWorlds()) {
            info.add("&aWorld: &7" + w.getName());
            if (pl.getRTP().getDisabledWorlds().contains(w.getName())) //DISABLED
                info.add("&7- &6Disabled: &bTrue");
            else {
                info.add("&7- &6Disabled: &cFalse");
                if (pl.getRTP().overriden.containsKey(w.getName()))
                    info.add("&7- &6Overriden: &bTrue");
                else {
                    info.add("&7- &7WorldType: " + pl.getRTP().world_type.getOrDefault(w.getName(), RTP_WORLD_TYPE.NORMAL).name());
                    info.add("&7- &6Overriden: &cFalse");
                    RTPWorld _rtpworld = pl.getRTP().Default;
                    for (RTPWorld __rtpworld : pl.getRTP().customWorlds.values()) {
                        if (__rtpworld.getWorld().equals(w.getName())) {
                            _rtpworld = __rtpworld;
                            break;
                        }
                    }
                    if (_rtpworld == pl.getRTP().Default)
                        info.add("&7- &6Custom: &cFalse");
                    else
                        info.add("&7- &6Custom: &bTrue");
                    if (_rtpworld.getUseWorldborder()) {
                        info.add("&7- &6UseWorldborder: &bTrue");
                        WorldBorder border = w.getWorldBorder();
                        info.add("&7- &6Center X: &7" + border.getCenter().getBlockX());
                        info.add("&7- &6Center Z: &7" + border.getCenter().getBlockZ());
                        info.add("&7- &6MaxRad: &7" + (border.getSize() / 2));
                    } else {
                        info.add("&7- &6UseWorldborder: &cFalse");
                        info.add("&7- &6Center X: &7" + _rtpworld.getCenterX());
                        info.add("&7- &6Center Z: &7" + _rtpworld.getCenterZ());
                        info.add("&7- &6MaxRad: &7" + _rtpworld.getMaxRad());
                    }
                    info.add("&7- &6MinRad: &7" + _rtpworld.getMinRad());
                }
            }
        }
        info.forEach(str ->
                info.set(info.indexOf(str), pl.getText().color(str)));
        sendi.sendMessage(info.toArray(new String[0]));
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    public boolean permission(CommandSender sendi) {
        return Main.getInstance().getPerms().getInfo(sendi);
    }
}
