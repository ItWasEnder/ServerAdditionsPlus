package me.endergaming.plugins.addons.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.backend.AddonManager;
import me.endergaming.plugins.addons.skills.SkillsManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

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
        if (parsed[0].equalsIgnoreCase("skills")) {
            if (!AddonManager.isRegistered("SkillsManager")) {
                return "";
            }

            SkillsManager manager = (SkillsManager) AddonManager.getManagerByAlias("skills");

            if (parsed.length < 2 || manager == null) {
                return "";
            }

            if (!manager.hasData(player)) {
                return "";
            }

            int level = manager.getCleanWeightedLevel(player);

            if (parsed.length == 3) {
                double mod = NumberUtils.toDouble(parsed[2], 1.0);
                level = (int) Math.round(mod * level);
            }

            if (parsed[1].equalsIgnoreCase("level")) {
                return manager.colorizeLevel(level);
            } else if (parsed[1].equalsIgnoreCase("cleanlevel")) {
                return "" + level;
            }
        }
        return "";
    }
}
