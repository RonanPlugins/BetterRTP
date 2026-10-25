package me.SuperRonanCraft.BetterRTP.player.events;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTP_TYPE;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;
import me.SuperRonanCraft.BetterRTP.references.helpers.HelperRTP;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JoinTest {
    @Test
    void firstJoinWaitsForDataAndEntitySchedulerAndPreservesBypassFlags() {
        verifyJoin(false, true, true);
    }
    @Test
    void returningPlayerDoesNotRtp() {
        verifyJoin(true, true, false);
    }
    @Test
    void playerDisconnectingBeforeScheduledTaskDoesNotRtp() {
        verifyJoin(false, false, false);
    }
    private void verifyJoin(boolean played, boolean online, boolean expectTeleport) {
        BetterRTP plugin = mock(BetterRTP.class, RETURNS_DEEP_STUBS);
        Player player = mock(Player.class);
        World world = mock(World.class);
        ConsoleCommandSender console = mock(ConsoleCommandSender.class);
        when(plugin.getDataFolder()).thenReturn(new File("target/test-plugin"));
        when(plugin.getSettings().isRtpOnFirstJoin_Enabled()).thenReturn(true);
        when(plugin.getSettings().getRtpOnFirstJoin_World()).thenReturn("world");
        when(player.hasPlayedBefore()).thenReturn(played);
        when(player.isOnline()).thenReturn(online);
        List<Runnable> async = new ArrayList<>(), entity = new ArrayList<>();
        try (MockedStatic<BetterRTP> singleton = mockStatic(BetterRTP.class);
             MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class);
             MockedStatic<AsyncHandler> scheduler = mockStatic(AsyncHandler.class);
             MockedStatic<HelperRTP> rtp = mockStatic(HelperRTP.class)) {
            singleton.when(BetterRTP::getInstance).thenReturn(plugin);
            bukkit.when(Bukkit::getConsoleSender).thenReturn(console);
            bukkit.when(() -> Bukkit.getWorld("world")).thenReturn(world);
            FileOther.FILETYPE.CONFIG.getConfig().set("Settings.DisableUpdater", true);
            scheduler.when(() -> AsyncHandler.async(any())).thenAnswer(call -> {async.add(call.getArgument(0)); return null;});
            scheduler.when(() -> AsyncHandler.syncAtEntity(eq(player), any())).thenAnswer(call -> {entity.add(call.getArgument(1)); return null;});
            Join.event(new PlayerJoinEvent(player, ""));
            rtp.verifyNoInteractions();
            assertEquals(1, async.size());
            verify(plugin.getCooldowns(), never()).loadPlayer(player);
            // The first-join decision must survive the state changing after the event.
            when(player.hasPlayedBefore()).thenReturn(true);
            async.get(0).run();
            verify(plugin.getCooldowns()).loadPlayer(player);
            rtp.verifyNoInteractions();
            assertEquals(1, entity.size());
            entity.get(0).run();
            if (expectTeleport)
                rtp.verify(() -> HelperRTP.tp(player, console, world, null, RTP_TYPE.JOIN, true, true));
            else
                rtp.verifyNoInteractions();
        }
    }
}
