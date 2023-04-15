package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import pl.minecodes.plots.api.plot.PlotApi;
import pl.minecodes.plots.api.plot.PlotServiceApi;

import java.util.Objects;

public class RTP_MinePlots implements RegionPluginCheck{
    // NOT TESTED (3.6.6)
    // MinePlots- (v4.0.1)
    // https://builtbybit.com/resources/mineplots.21646/

    private PlotServiceApi plotServiceApi;

    public boolean check(Location loc) {
        boolean result = true;
        if (REGIONPLUGINS.MINEPLOTS.isEnabled())
            try {
                RegisteredServiceProvider<PlotServiceApi> serviceProvider = Bukkit.getServicesManager().getRegistration(PlotServiceApi.class);
                Objects.requireNonNull(serviceProvider, "[MinePlots Respect] Service provider is null.");
                plotServiceApi = serviceProvider.getProvider();

                plotServiceApi = serviceProvider.getProvider();
                PlotApi plot = plotServiceApi.getPlot(loc);

                if (plot != null) {
                    result = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
    }
}
