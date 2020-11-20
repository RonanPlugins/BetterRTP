package me.SuperRonanCraft.BetterRTP.player.rtp;

public enum RTP_TYPE {
    COMMAND, //Player executed command
    FORCED, //Player was forced to rtp (/rtp player <player>)
    JOIN, //Player joined and was rtp'd automatically
    ADDON, //Player RTP'd from the external addons plugin
    ADDON_PORTAL, //Player RTP'd from the external addons (Portals)
    ADDON_MAGICSTICK, //Player RTP'd from the external addons (MagicStick)
}
