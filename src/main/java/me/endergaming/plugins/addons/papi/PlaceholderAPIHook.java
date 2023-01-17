package me.endergaming.plugins.addons.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.endergaming.plugins.ServerAdditionsPlus;
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
        return "sap";
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
        String[] parsed = params.replace("sap_", "").split("_");
        if (parsed.length == 0) {
            return "";
        }

        return "";
    }
}
