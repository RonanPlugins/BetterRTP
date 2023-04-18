package me.SuperRonanCraft.BetterRTP.references.depends.regionPlugins;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.references.settings.SoftDepends;

public enum REGIONPLUGINS {
    FACTIONSUUID("FactionsUUID", "Factions", new RTP_FactionsUUID()),
    GRIEFDEFENDER("GriefDefender", new RTP_GriefDefender()),
    GRIEFPREVENTION("GriefPrevention", new RTP_GriefPrevention()),
    HCLAIMS("hClaims", "hClaim", new RTP_hClaims()),
    HUSKTOWNS("HuskTowns", new RTP_HuskTowns()),
    KINGDOMSX("KingdomsX", "Kingdoms", new RTP_KingdomsX()),
    LANDS("Lands", new RTP_Lands()),
    PUEBLOS("Pueblos", new RTP_Pueblos()),
    REDPROTECT("RedProtect", new RTP_RedProtect()),
    RESIDENCE("Residence", new RTP_Residence()),
    SABERFACTIONS("SaberFactions", "Factions", new RTP_SaberFactions()),
    TOWNY("Towny", new RTP_Towny()),
    ULTIMATECLAIMS("UltimateClaims", new RTP_UltimateClaims()),
    WORLDGUARD("WorldGuard", new RTP_WorldGuard()),
    MINEPLOTS("MinePlots", new RTP_MinePlots()),

    ;
    @Getter private final SoftDepends.RegionPlugin plugin = new SoftDepends.RegionPlugin();
    @Getter private final String setting_name, pluginyml_name;
    @Getter private final RegionPluginCheck validator;

    REGIONPLUGINS(String all_name, RegionPluginCheck validator) {
        this(all_name, all_name, validator);
    }

    REGIONPLUGINS(String setting_name, String pluginyml_name, RegionPluginCheck validator) {
        this.setting_name = setting_name;
        this.pluginyml_name = pluginyml_name;
        this.validator = validator;
    }

    public boolean isEnabled() {
        return plugin.isEnabled();
    }
}
