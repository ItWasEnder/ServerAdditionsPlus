package me.endergaming.plugins.backend;

import com.marcusslover.plus.lib.events.EventHandler;
import com.marcusslover.plus.lib.events.EventListener;
import com.marcusslover.plus.lib.task.Task;
import com.marcusslover.plus.lib.util.BundledListener;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.backend.events.AddonRegisterEvent;
import me.endergaming.plugins.backend.exceptions.AddonException;
import me.endergaming.plugins.misc.Globals;
import org.bukkit.Bukkit;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class Addon {
    private final ServerAdditionsPlus plugin;
    private final String name;
    private final String managerAlias;
    private BundledListener<PluginEnableEvent> pluginListener;

    final String requiredPlugin;
    final List<String> requirements = new ArrayList<>();
    boolean registered = false;

    protected Addon(@NotNull final ServerAdditionsPlus instance, @NotNull String alias, Globals.Plugins reqPlugin) {
        String[] split = this.getClass().getName().split("\\.");
        this.name = split[split.length - 1];
        this.managerAlias = alias;
        this.requiredPlugin = reqPlugin.name;
        this.plugin = instance;
        // Add manager to AddonManager
        AddonManager.add(this);
        this.initRegister();
    }

    protected Addon(@NotNull final ServerAdditionsPlus instance, @NotNull String alias, Globals.Plugins reqPlugin, String... manReqs) {
        String[] split = this.getClass().getName().split("\\.");
        this.name = split[split.length - 1];
        this.managerAlias = alias;
        this.requiredPlugin = reqPlugin.name;
        this.plugin = instance;
        this.requirements.addAll(Arrays.asList(manReqs));
        // Add manager to AddonManager
        AddonManager.add(this);
        // Create call-back listener for manager requirements
        BundledListener<AddonRegisterEvent> listener = new BundledListener<>(AddonRegisterEvent.class, (l, e) -> {
            ServerAdditionsPlus.debug("- - - - - - - - - -");
            ServerAdditionsPlus.debug("Addon: " + this.name);
            if (this.requirements.isEmpty()) {
                l.unregister();
            }

            ServerAdditionsPlus.debug("Issues: ");
            if (e.getAddon().getName().equals(this.name)) {
                ServerAdditionsPlus.debug("    1 - Addon called itself");
                return;
            }
            if (!e.isRegistered()) {
                ServerAdditionsPlus.debug("    2 - Event manager failed to register (" + e.getAddon().getName() + ")");
                return;
            }
            if (this.requirementsNotFilled()) {
                ServerAdditionsPlus.debug("    3 - Requirements are not fulfilled");
                return;
            }
            // Attempt to register if all reqManagers are met
            ServerAdditionsPlus.debug("Starting Registration...");
            this.initRegister();
            if (this.registered) {
                l.unregister();
            }
            ServerAdditionsPlus.debug("- - - - - - - - - -");
        });
        // Timeout Event Listener
        Task.syncDelayed(task -> listener.unregister(), 20L * 120);
    }

    private void initRegister() {
        ServerAdditionsPlus.debug("Attempting to register " + this.name);
        if (!ServerAdditionsPlus.isEnabled(this.requiredPlugin)) {
            if (this.pluginListener != null) {
                return;
            }
            this.pluginListener = new BundledListener<>(PluginEnableEvent.class, new BiConsumer<>() {
                // Timeout Event Listener
                final Task timeout = Task.syncDelayed(task -> {
                    ServerAdditionsPlus.debug("- - - - - - - - - -");
                    ServerAdditionsPlus.debug("Registration Timed Out - " + Addon.this.name);
                    Addon.this.register();
                    Addon.this.pluginListener.unregister();
                    ServerAdditionsPlus.debug("IsRegistered: " + Addon.this.registered);
                    ServerAdditionsPlus.debug("- - - - - - - - - -");
                }, 20L * 15);

                @Override
                public void accept(BundledListener<PluginEnableEvent> l, PluginEnableEvent e) {
                    if (Addon.this.registered) {
                        l.unregister();
                        return;
                    }
                    ServerAdditionsPlus.debug("- - - - - - - - - -");
                    ServerAdditionsPlus.debug("Plugin Enabled Event - " + Addon.this.name);
                    ServerAdditionsPlus.debug("Plugin: " + e.getPlugin().getName());
                    if (e.getPlugin().getName().equals(Addon.this.requiredPlugin)) {
                        Addon.this.register();
                        Bukkit.getPluginManager().callEvent(new AddonRegisterEvent(Addon.this, Addon.this.registered));
                        this.timeout.cancel();
                        l.unregister();
                        ServerAdditionsPlus.debug(Addon.this.name + " has all requirements fulfilled");
                        ServerAdditionsPlus.debug("IsRegistered: " + Addon.this.registered);
                        ServerAdditionsPlus.debug("- - - - - - - - - -");
                    } else {
                        ServerAdditionsPlus.debug("- - - - - - - - - -");
                    }
                }
            });
        } else {
            this.register();
            ServerAdditionsPlus.debug("- - - - - - - - - -");
            ServerAdditionsPlus.debug(this.name + " has all requirements fulfilled");
            ServerAdditionsPlus.debug("IsRegistered: " + this.registered);
            ServerAdditionsPlus.debug("- - - - - - - - - -");
            Bukkit.getPluginManager().callEvent(new AddonRegisterEvent(this, this.registered));
        }
    }

    protected ServerAdditionsPlus getPlugin() {
        return this.plugin;
    }

    protected void register() {
        if (this.registered) {
            this.unregister();
        }

        this.registered = true;

        try {
            this.onEnable();

            if (this instanceof EventListener BundledListener) {
                EventHandler.get().subscribe(BundledListener);
            }
        } catch (AddonException e) {
            ServerAdditionsPlus.logger().severe("Failed to register " + this.name + ": " + Arrays.toString(e.getMessages().toArray()));
        }
    }

    protected void unregister() {
        this.registered = false;

        this.onDisable();
    }

    public abstract void onEnable() throws AddonException;

    public abstract void onDisable();

    public boolean isRegistered() {
        return this.registered;
    }

    public String getRequiredPlugin() {
        return this.requiredPlugin;
    }

    public final List<String> getRequirements() {
        return Collections.unmodifiableList(this.requirements);
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public String getManagerAlias() {
        return this.managerAlias;
    }

    public boolean hasReqPlugin() {
        return this.requiredPlugin.isBlank() || ServerAdditionsPlus.isEnabled(this.requiredPlugin);
    }

    public boolean requirementsNotFilled() {
        return !this.requirements.isEmpty() && !this.requirements.stream().allMatch(AddonManager::isRegistered);
    }
}
