package me.SuperRonanCraft.BetterRTP.references.customEvents;

public enum RTPEvents {
    CANCELLED(RTP_CancelledEvent.class),
    COMMAND(RTP_CommandEvent.class),
    FINDLOCATION(RTP_FindLocationEvent.class),
    TELEPORT(RTP_TeleportEvent.class),
    TELEPORTPOST(RTP_TeleportPostEvent.class),
    TELEPORTPRE(RTP_TeleportPreEvent.class);

    public Class cl;

    RTPEvents(Class cl) {
        this.cl = cl;
    }
}
