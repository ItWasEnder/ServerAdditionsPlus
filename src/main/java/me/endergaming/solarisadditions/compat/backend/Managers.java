package me.endergaming.solarisadditions.compat.backend;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public final class Managers {
    private static final ConcurrentHashMap<String, Manager> managerMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, String> availableManagers = new ConcurrentHashMap<>();

    public static void add(Manager manager) {
        if (managerMap.containsKey(manager.getName()))
            throw new IllegalArgumentException("Manager is already registered");
        managerMap.put(manager.getName(), manager);
        availableManagers.put(manager.getManagerAlias(), manager.getName());
    }

    public static void registerAll() {
        managerMap.values().forEach(Manager::register);
    }

    public static void unregisterAll() {
        managerMap.values().stream().filter(Managers::isRegistered).forEach(Manager::unregister);
    }

    @Nullable
    public static Manager getManagerByName(String name) {
        return managerMap.get(name);
    }

    @Nullable
    public static Manager getManagerByAlias(String alias) {
        if (!availableManagers.containsKey(alias)) return null;
        return managerMap.get(availableManagers.get(alias));
    }

    public static Collection<Manager> getManagers() {
        return managerMap.values();
    }

    public static boolean isRegistered(String name) {
        if (!managerMap.containsKey(name)) return false;
        return isRegistered(managerMap.get(name));
    }

    public static boolean isRegistered(Manager manager) {
        return manager.registered;
    }
}
