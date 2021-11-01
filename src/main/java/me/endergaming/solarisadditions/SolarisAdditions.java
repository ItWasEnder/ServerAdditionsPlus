package me.endergaming.solarisadditions;

import me.endergaming.enderlibs.text.MessageUtils;
import me.endergaming.solarisadditions.commands.CommandRegistry;
import me.endergaming.solarisadditions.compat.backend.Managers;
import me.endergaming.solarisadditions.compat.cmi.CMIManager;
import me.endergaming.solarisadditions.compat.levelledmobs.LMManager;
import me.endergaming.solarisadditions.compat.lunarclient.LCManager;
import me.endergaming.solarisadditions.compat.mcmmo.MCMMOManager;
import me.endergaming.solarisadditions.compat.moneyfrommobs.MFMManager;
import me.endergaming.solarisadditions.compat.papi.PlaceholderManager;
import me.endergaming.solarisadditions.controllers.ConfigController;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static me.endergaming.enderlibs.text.MessageUtils.LogLevel.WARNING;
import static me.endergaming.solarisadditions.controllers.ConfigController.*;

public final class SolarisAdditions extends JavaPlugin {
    private final ConfigController configController = new ConfigController();
    private final CommandRegistry commandRegistry = new CommandRegistry(this);

    public static final long FIVE_MINUTES_IN_TICKS = 6000;
    public static NamespacedKey KEY;

    @Override
    public void onEnable() {
        // Plugin startup logic
        KEY = new NamespacedKey(this, "solaris-additions");
        configController.init();
        // Register Commands
        commandRegistry.register();
        // Create & Register Managers
        registerManagers();
    }

    private void registerManagers() {
        if (LUNAR_CLIENT) new LCManager(this, "LunarClient-API", "CMIManager");
        if (MONEY_FROM_MOBS) new MFMManager(this, "MoneyFromMobs");
        if (CMI) new CMIManager(this, "CMI");
        if (PLACEHOLDER_API) new PlaceholderManager(this, "PlaceholderAPI");
        if (MCMMO) new MCMMOManager(this, "mcMMO");
        if (LEVELLED_MOBS) new LMManager(this, "LevelledMobs");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Managers.unregisterAll();
    }

    /**
     * Used for internal logging for SolarisAdditions
     */
    public static void log(MessageUtils.LogLevel logLevel, String msg) {
        switch (logLevel) {
            case WARNING -> MessageUtils.log(logLevel, msg, "&e[SolarisAdditions]&f ");
            case SEVERE -> MessageUtils.log(logLevel, msg, "&c[SolarisAdditions]&f ");
            default -> MessageUtils.log(logLevel, msg, "[SolarisAdditions]&f ");
        }
    }

    /**
     * Used to send messages to console with "Debug" prefix.
     * <br /><br />
     * <b>Messages will only be shown when debug mode is enabled.</b>
     */
    public static void debug(String msg) {
        if (DEBUG) MessageUtils.log(WARNING, msg, "&e&l[DEBUG@SolarisAdditions]&f ");
    }

    public static boolean isEnabled(@NotNull final String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }
}
