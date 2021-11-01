package me.endergaming.solarisadditions.compat.backend;

import me.endergaming.enderlibs.util.EventListener;
import me.endergaming.enderlibs.util.Task;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.events.ManagerRegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class Manager {
    private final SolarisAdditions plugin;
    private final String name;
    private final String managerAlias;
    private EventListener<PluginEnableEvent> pluginListener;
    protected final String requiredPlugin;
    protected final List<String> requirements = new ArrayList<>();
    protected boolean registered = false;

    protected Manager(@NotNull final SolarisAdditions instance, @NotNull String alias, @NotNull String reqPlugin) {
        String[] split = this.getClass().getName().split("\\.");
        name = split[split.length - 1];
        managerAlias = alias;
        requiredPlugin = reqPlugin;
        plugin = instance;
        initRegister();
        // Add manager to Managers
        Managers.add(this);
    }

    protected Manager(@NotNull final SolarisAdditions instance, @NotNull String alias, @NotNull String reqPlugin, String... manReqs) {
        String[] split = this.getClass().getName().split("\\.");
        name = split[split.length - 1];
        managerAlias = alias;
        requiredPlugin = reqPlugin;
        plugin = instance;
        this.requirements.addAll(Arrays.asList(manReqs));
        // Create call-back listener for manager requirements
        EventListener<ManagerRegisterEvent> listener = new EventListener<>(ManagerRegisterEvent.class, (l, e) -> {
            SolarisAdditions.debug("- - - - - - - - - -");
            SolarisAdditions.debug("Manager: " + name);
            if (requirements.isEmpty()) l.unregister();

            SolarisAdditions.debug("Issues: ");
            if (e.getManager().getName().equals(name)) {
                SolarisAdditions.debug("    1 - Manager called itself");
                return;
            }
            if (!e.isRegistered())  {
                SolarisAdditions.debug("    2 - Event manager failed to register (" + e.getManager().getName() + ")");
                return;
            }
            if (!requirementsFulfilled()) {
                SolarisAdditions.debug("    3 - Requirements are not fulfilled");
                return;
            }
            // Attempt to register if all reqManagers are met
            SolarisAdditions.debug("Starting Registration...");
            initRegister();
//            Bukkit.getPluginManager().callEvent(new ManagerRegisterEvent(Manager.this, registered));
            if (registered) l.unregister();
            SolarisAdditions.debug("- - - - - - - - - -");
        });
        // Timeout Event Listener
        Task.syncDelayed(task -> listener.unregister(), 20L * 120);
        // Add manager to Managers
        Managers.add(this);
    }

    private void initRegister() {
        SolarisAdditions.debug("Attempting to register " + name);
        if (!SolarisAdditions.isEnabled(requiredPlugin)) {
            if (pluginListener != null) return;
            pluginListener = new EventListener<>(PluginEnableEvent.class, new BiConsumer<>() {
                // Timeout Event Listener
                final Task timeout = Task.syncDelayed(task -> {
                    SolarisAdditions.debug("- - - - - - - - - -");
                    SolarisAdditions.debug("Registration Timed Out - " + name);
                    register();
                    pluginListener.unregister();
                    SolarisAdditions.debug("IsRegistered: " + registered);
                    SolarisAdditions.debug("- - - - - - - - - -");
                }, 20L * 15);

                @Override
                public void accept(EventListener<PluginEnableEvent> l, PluginEnableEvent e) {
                    if (registered) {
                        l.unregister();
                        return;
                    }
                    SolarisAdditions.debug("- - - - - - - - - -");
                    SolarisAdditions.debug("Plugin Enabled Event - " + name);
                    SolarisAdditions.debug("Plugin: " + e.getPlugin().getName());
                    if (e.getPlugin().getName().equals(requiredPlugin)) {
                        register();
                        Bukkit.getPluginManager().callEvent(new ManagerRegisterEvent(Manager.this, registered));
                        timeout.cancel();
                        l.unregister();
                        SolarisAdditions.debug(name + " has all requirements fulfilled");
                        SolarisAdditions.debug("IsRegistered: " + registered);
                        SolarisAdditions.debug("- - - - - - - - - -");
                    } else {
                        SolarisAdditions.debug("- - - - - - - - - -");
                    }
                }
            });
        } else {
            register();
            SolarisAdditions.debug("- - - - - - - - - -");
            SolarisAdditions.debug(name + " has all requirements fulfilled");
            SolarisAdditions.debug("IsRegistered: " + registered);
            SolarisAdditions.debug("- - - - - - - - - -");
            Bukkit.getPluginManager().callEvent(new ManagerRegisterEvent(this, registered));
        }
    }

    protected SolarisAdditions getPlugin() {
        return plugin;
    }

    public void register() {
        if (registered) unregister();
        registered = true;
    }

    public void unregister() {
        registered = false;
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getRequiredPlugin() {
        return requiredPlugin;
    }

    public final List<String> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getManagerAlias() {
        return managerAlias;
    }

    public boolean hasReqPlugin() {
        return SolarisAdditions.isEnabled(requiredPlugin);
    }

    public boolean requirementsFulfilled() {
        return requirements.isEmpty() || requirements.stream().allMatch(Managers::isRegistered);
    }
}
