package me.SuperRonanCraft.BetterRTPAddons.addons.commands;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_CancelledEvent;
import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_TeleportEvent;
import me.SuperRonanCraft.BetterRTPAddons.Main;
import me.SuperRonanCraft.BetterRTPAddons.util.Files;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;

public class CommandsLoader implements Listener {

    HashMap<Command_types, List<String>> commands = new HashMap<>();

    void load() {
        Files.FILETYPE file = getFile();
        String name = "Commands";
        commands.clear();
        for (Command_types type : Command_types.values()) {
            String path = name + ".Events." + type.path;
            if (file.getBoolean(path + ".Enabled")) {
                List<String> cmds = file.getStringList(path + ".Commands");
                commands.put(type, cmds);
            }
        }
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    void unload() {
        HandlerList.unregisterAll(this);
    }

    private Files.FILETYPE getFile() {
        return Main.getInstance().getFiles().getType(Files.FILETYPE.CONFIG);
    }

    //Events
    @EventHandler
    public void eventTeleport(RTP_TeleportEvent event) {
        if (!commands.containsKey(Command_types.TELEPORT)) return;
        executeCommands(commands.get(Command_types.TELEPORT), event.getPlayer());
    }

    @EventHandler
    public void eventCancelled(RTP_CancelledEvent event) {
        if (!commands.containsKey(Command_types.CANCELLED)) return;
        executeCommands(commands.get(Command_types.CANCELLED), event.getPlayer());
    }

    private void executeCommands(List<String> cmds, Player p) {
        for (String cmd : cmds) {
            cmd = cmd.replaceAll("%player_name%", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }

}

enum Command_types {
    TELEPORT("Teleport"),
    CANCELLED("Cancelled");

    String path;

    Command_types(String path) {
        this.path = path;
    }
}