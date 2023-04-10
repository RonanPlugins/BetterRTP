package me.SuperRonanCraft.BetterRTP.references.helpers;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.commands.types.CmdLocation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_ERROR_REQUEST_REASON;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.PermissionCheck;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.WarningHandler;
import me.SuperRonanCraft.BetterRTP.references.messages.Message_RTP;
import me.SuperRonanCraft.BetterRTP.references.messages.placeholder.Placeholders;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.PermissionGroup;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HelperRTP {

    //Teleported and Sender are the same
    public static void tp(Player player, World world, List<String> biomes, RTP_TYPE rtpType) {
        tp(player, player, world, biomes, rtpType, false, false);
    }

    //Teleported and Sender MAY be different
    public static void tp(Player player, CommandSender sendi, World world, List<String> biomes, RTP_TYPE rtpType) {
        tp(player, sendi, world, biomes, rtpType, false, false);
    }

    //
    public static void tp(Player player, CommandSender sendi, World world, List<String> biomes, RTP_TYPE rtpType,
                   boolean ignoreCooldown, boolean ignoreDelay) {
        tp(player, sendi, world, biomes, rtpType, ignoreCooldown, ignoreDelay, null);
    }

    public static void tp(@NotNull Player player, CommandSender sendi, @Nullable World world, List<String> biomes, RTP_TYPE rtpType,
                          boolean ignoreCooldown, boolean ignoreDelay, @Nullable WorldLocation location) {
        world = getActualWorld(player, world, location);
        RTPSetupInformation setup_info = new RTPSetupInformation(
                world,
                sendi,
                player,
                true,
                biomes,
                !ignoreDelay && HelperRTP_Check.applyDelay(player, sendi),
                rtpType,
                location,
                !ignoreCooldown && HelperRTP_Check.applyCooldown(sendi, player)
        );
        //RTP request cancelled reason
        WorldPlayer pWorld = getPlayerWorld(setup_info);
        RTP_ERROR_REQUEST_REASON cantReason = HelperRTP_Check.canRTP(player, sendi, pWorld, ignoreCooldown);
        if (cantReason != null) {
            String msg = cantReason.getMsg().get(player, null);
            if (cantReason == RTP_ERROR_REQUEST_REASON.COOLDOWN) {
                msg = msg.replace(Placeholders.COOLDOWN.name, HelperDate.total(HelperRTP_Check.getCooldown(player, pWorld)));
                msg = msg.replace(Placeholders.TIME.name, HelperDate.total(HelperRTP_Check.getCooldown(player, pWorld)));
            }
            Message_RTP.sms(sendi, msg, pWorld);
            return;
        }
        //ignore cooldown or else
        //BetterRTP.getInstance().getLogger().info("Center X: " + pWorld.getCenterX());
        getPl().getRTP().start(pWorld);
    }

    private static BetterRTP getPl() {
        return BetterRTP.getInstance();
    }

    public static World getActualWorld(Player player, World world, @Nullable WorldLocation location) {
        if (world == null)
            world = player.getWorld();
        if (location != null)
            world = location.getWorld();
        if (BetterRTP.getInstance().getRTP().overriden.containsKey(world.getName()))
            world = Bukkit.getWorld(BetterRTP.getInstance().getRTP().overriden.get(world.getName()));
        return world;
    }

    public static World getActualWorld(Player player, World world) {
        return getActualWorld(player, world, null);
    }

    @Nullable
    public static WorldLocation getRandomLocation(CommandSender sender, World world) {
        HashMap<String, RTPWorld> locations_permissible = CmdLocation.getLocations(sender, world);
        if (!locations_permissible.isEmpty()) {
            List<String> valuesList = new ArrayList<>(locations_permissible.keySet());
            String randomIndex = valuesList.get(new Random().nextInt(valuesList.size()));
            return (WorldLocation) locations_permissible.get(randomIndex);
        }
        return null;
    }

    public static WorldPlayer getPlayerWorld(RTPSetupInformation setup_info) {
        WorldPlayer pWorld = new WorldPlayer(setup_info);

        //Random Location
        if (setup_info.getLocation() == null
                && BetterRTP.getInstance().getSettings().isLocationEnabled()
                && BetterRTP.getInstance().getSettings().isUseLocationIfAvailable()) {
            WorldLocation worldLocation = HelperRTP.getRandomLocation(setup_info.getSender(), setup_info.getWorld());
            if (worldLocation != null) {
                setup_info.setLocation(worldLocation);
                setup_info.setWorld(worldLocation.getWorld());
            }
            if (setup_info.getLocation() == null && BetterRTP.getInstance().getSettings().isDebug())
                WarningHandler.warn(WarningHandler.WARNING.USELOCATION_ENABLED_NO_LOCATION_AVAILABLE,
                        "This is not an error! UseLocationIfAvailable is set to `true`, but no location was found for "
                                + setup_info.getSender().getName() + "! Using world defaults! (Maybe they dont have permission?)");
        }
        //Location
        if (setup_info.getLocation() != null) {
            String setup_name = null;
            for (Map.Entry<String, RTPWorld> location_set : BetterRTP.getInstance().getRTP().getRTPworldLocations().entrySet()) {
                RTPWorld location = location_set.getValue();
                if (location == setup_info.getLocation()) {
                    setup_name = location_set.getKey();
                    break;
                }
            }
            pWorld.setup(setup_name, setup_info.getLocation(), setup_info.getLocation().getBiomes());
            //BetterRTP.getInstance().getLogger().info("Location x: " + setup_info.getLocation().getCenterX());
        }

        //Setup world (if no location pre-setup)
        if (!pWorld.isSetup()) {
            WorldPermissionGroup group = getGroup(pWorld);

            //Permission Group
            if (group != null) {
                pWorld.setup(null, group, setup_info.getBiomes());
                pWorld.config = group;
            }
            //Custom World
            else if (BetterRTP.getInstance().getRTP().getRTPcustomWorld().containsKey(setup_info.getWorld().getName())) {
                RTPWorld cWorld = BetterRTP.getInstance().getRTP().getRTPcustomWorld().get(pWorld.getWorld().getName());
                pWorld.setup(null, cWorld, setup_info.getBiomes());
            }
            //Default World
            else
                pWorld.setup(null, BetterRTP.getInstance().getRTP().getRTPdefaultWorld(), setup_info.getBiomes());
        }
        //World type
        pWorld.setWorldtype(getWorldType(pWorld.getWorld()));
        return pWorld;
    }

    public static WORLD_TYPE getWorldType(World world) {
        WORLD_TYPE world_type;
        RTP rtp = BetterRTP.getInstance().getRTP();
        if (rtp.world_type.containsKey(world.getName()))
            world_type = rtp.world_type.get(world.getName());
        else {
            world_type = WORLD_TYPE.NORMAL;
            rtp.world_type.put(world.getName(), world_type); //Defaults this so the error message isn't spammed
            WarningHandler.warn(WarningHandler.WARNING.NO_WORLD_TYPE_DECLARED, "Seems like the world `" + world.getName() + "` does not have a `WorldType` declared. " +
                    "Please add/fix this in the config.yml file! This world will be treated as an overworld! " +
                    "If this world is a nether world, configure it to NETHER (example: `- " + world.getName() + ": NETHER`", false);
        }
        return world_type;
    }

    public static WorldPermissionGroup getGroup(WorldPlayer pWorld) {
        WorldPermissionGroup group = null;
        if (pWorld.getPlayer() != null)
            for (Map.Entry<String, PermissionGroup> permissionGroup : BetterRTP.getInstance().getRTP().getPermissionGroups().entrySet()) {
                for (Map.Entry<String, WorldPermissionGroup> worldPermission : permissionGroup.getValue().getWorlds().entrySet()) {
                    if (pWorld.getWorld().equals(worldPermission.getValue().getWorld())) {
                        if (PermissionCheck.getPermissionGroup(pWorld.getPlayer(), permissionGroup.getKey())) {
                            if (group != null) {
                                if (group.getPriority() < worldPermission.getValue().getPriority())
                                    continue;
                            }
                            group = worldPermission.getValue();
                        }
                    }
                }
            }
        return group;
    }
}
