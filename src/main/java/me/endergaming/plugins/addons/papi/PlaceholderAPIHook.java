package me.endergaming.plugins.addons.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.backend.AddonManager;
import me.endergaming.plugins.addons.mcmmo.MCMMOManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final ServerAdditionsPlus plugin;

    public PlaceholderAPIHook(@NotNull ServerAdditionsPlus instance) {
        this.plugin = instance;
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return "sa";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return this.plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull
    String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        String[] parsed = params.replace("sa_", "").split("_");
        if (parsed.length == 0) {
            return "";
        }
        if (parsed[0].equalsIgnoreCase("mcmmo")) {
            if (!AddonManager.isRegistered("MCMMOManager")) {
                return "";
            }
            MCMMOManager manager = (MCMMOManager) AddonManager.getManagerByAlias("mcmmo");
            if (parsed.length < 2 || manager == null) {
                return "";
            }
            if (!manager.isLoaded(player)) {
                return "";
            }
            if (parsed[1].equalsIgnoreCase("combat")) {
                return manager.getFormattedWeightedLevel(player);
            } else if (parsed[1].equalsIgnoreCase("cleancombat")) {
                int level = manager.getCleanWeightedLevel(player);
                if (parsed.length == 3) {
                    double mod = NumberUtils.toDouble(parsed[2], 1.0);
                    level = (int) Math.round(mod * level);
                }
                return String.valueOf(level);
            }
        }
        return "";
    }
}
