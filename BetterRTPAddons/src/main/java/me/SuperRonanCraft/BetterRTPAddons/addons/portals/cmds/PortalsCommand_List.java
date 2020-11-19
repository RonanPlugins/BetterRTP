package me.SuperRonanCraft.BetterRTPAddons.addons.portals.cmds;

import me.SuperRonanCraft.BetterRTPAddons.addons.portals.AddonPortals;
import me.SuperRonanCraft.BetterRTPAddons.addons.portals.region.PortalsRegionInfo;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class PortalsCommand_List implements PortalsCommands {

    @Override
    public void execute(CommandSender sendi, String label, String[] args, AddonPortals addonPortals) {
        List<PortalsRegionInfo> portalList = addonPortals.getPortals().getRegisteredPortals();
        if (portalList.isEmpty()) {
            addonPortals.msgs.getListNone(sendi);
            return;
        }
        String strPrefix = addonPortals.msgs.getListPrefix();
        List<String> list = new ArrayList<>();
        list.add(strPrefix);
        String strPortal = addonPortals.msgs.getListPortal();
        for (PortalsRegionInfo portal : portalList) {
            String locInfo = "World: " + portal.getLoc1().getWorld().getName() +
                    " Loc1: " + locToString(portal.getLoc1()) +
                    " Loc2: " + locToString(portal.getLoc2());
            list.add(strPortal.replace("%name%", portal.getName()).replace("%location%", locInfo));
        }
        addonPortals.msgs.sms(sendi, list);
    }

    private String locToString(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }
}
