package me.SuperRonanCraft.BetterRTP.player.commands.types;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.RTPCommand;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.messages.Message;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTP.references.web.LogUploader;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CmdLogger implements RTPCommand {

    public String getName() {
        return "logger";
    }
    public void execute(CommandSender sendi, String label, String[] args) {
        get(sendi, label, args, args.length >= 2 && args[args.length - 1].equalsIgnoreCase("_UPLOAD_"));
    }

    private void get(CommandSender sendi, String label, String[] args, boolean upload) {
        String cmd = "/" + label + " " + String.join(" ", args);
        if (!upload) {
            if (sendi instanceof Player) {
                TextComponent component = new TextComponent(Message.color("&7- &7Click to upload log to &fmclo.gs"));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd + " _UPLOAD_"));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Message.color("&6Suggested command&f: &7" + "/betterrtp " + String.join(" ", args) + " _UPLOAD_")).create()));
                ((Player) sendi).spigot().sendMessage(component);
            } else {
                sendi.sendMessage("Execute `" + cmd + " _UPLOAD_`" + " to upload log to https://mclo.gs");
            }
        } else {
            CompletableFuture.runAsync(() -> {
                String url = LogUploader.post(BetterRTP.getInstance().getRtpLogger().getFile());
                if (url == null) {
                    Message.sms(sendi, new ArrayList<>(Collections.singletonList("&cCould not upload the log. Check your connection and ensure the log is under 5 MiB, then try again.")), null);
                } else {
                    Message.sms(sendi, Arrays.asList(" ", Message.getPrefix(Message_RTP.msg) + "&aLog uploaded! &fView&7: &6" + url), null);
                }
            });
        }
    }

    public List<String> tabComplete(CommandSender sendi, String[] args) {
        return null;
    }

    @NotNull public PermissionNode permission() {
        return PermissionNode.ADMIN;
    }

    public void usage(CommandSender sendi, String label) {
    }
}
