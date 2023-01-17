package me.endergaming.plugins;

import com.marcusslover.plus.lib.command.CommandManager;
import me.endergaming.plugins.addons.mmocore.MMOManager;
import me.endergaming.plugins.backend.AddonManager;
import me.endergaming.plugins.backend.events.EventManager;
import me.endergaming.plugins.commands.ManagerCommand;
import me.endergaming.plugins.controllers.ConfigController;
import me.endergaming.plugins.misc.Globals;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static me.endergaming.plugins.misc.Globals.PLUGIN_KEY;

public final class ServerAdditionsPlus extends JavaPlugin {
    private final ConfigController configController = new ConfigController();

    public static final long FIVE_MINUTES_IN_TICKS = 6000;
    public static NamespacedKey KEY;
    private static ServerAdditionsPlus PLUGIN;
    private CommandManager commandManager;

    public static void debug(String s) {
        if (ConfigController.DEBUG) {
            logger().info("[DEBUG] " + s);
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        PLUGIN = this;
        KEY = new NamespacedKey(this, PLUGIN_KEY);
//        this.configController.init();

        // Register Commands
        this.commandManager = CommandManager.get(this);

        this.commandManager.register(new ManagerCommand(this));

        // Create & Register AddonManager
        this.registerManagers();
        Bukkit.getPluginManager().registerEvents(EventManager.get(), this);
    }

    private void registerManagers() {
        new MMOManager(this, Globals.Addons.MMOManager.name, Globals.Plugins.MMOCORE);

//        if (LUNAR_CLIENT) {
//            new LCManager(this, Globals.Plugins.LUNAR_CLIENT, Globals.Addons.CMIManager.name);
//        }
//
//        if (MONEY_FROM_MOBS) {
//            new MFMManager(this, Globals.Plugins.MONEY_FROM_MOBS);
//        }
//
//        if (CMI) {
//            new CMIManager(this, Globals.Plugins.CMI);
//        }
//
//        if (PLACEHOLDER_API) {
//            new PlaceholderManager(this, Globals.Plugins.PLACEHOLDER_API);
//        }
//
//        if (SKILLS) {
//            new SkillsManager(this, Globals.Plugins.AURELIUM_SKILLS, Globals.Addons.LMManager.name);
//        }
//
//        if (LEVELLED_MOBS) {
//            new LMManager(this, Globals.Plugins.LEVELLED_MOBS);
//        }
//
//        if (KONQUEST) {
//            new KQManager(this, Globals.Plugins.KONQUEST);
//        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        AddonManager.unregisterAll();
        this.commandManager.clearCommands();
    }

    public static Logger logger() {
        return PLUGIN.getLogger();
    }

    public static ServerAdditionsPlus getPlugin() {
        return PLUGIN;
    }

    public static boolean isEnabled(@NotNull final String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }
}
