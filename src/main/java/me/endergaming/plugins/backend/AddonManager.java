package me.endergaming.plugins.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public final class AddonManager {
    private static final ConcurrentHashMap<String, Addon> managerMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, String> availableManagers = new ConcurrentHashMap<>();

    public static void register(@NotNull Addon addon) {
        addon.register();
    }

    public static void unregister(@NotNull Addon addon) {
        addon.unregister();
    }

    public static void reload(@NotNull Addon addon) {
        addon.unregister();
        addon.register();
    }

    public static void add(Addon addon) {
        if (managerMap.containsKey(addon.getName())) {
            throw new IllegalArgumentException("Addon is already registered");
        }
        managerMap.put(addon.getName(), addon);
        availableManagers.put(addon.getManagerAlias(), addon.getName());
    }

    public static void registerAll() {
        managerMap.values().forEach(Addon::register);
    }

    public static void unregisterAll() {
        managerMap.values().stream().filter(AddonManager::isRegistered).forEach(Addon::unregister);
    }

    @Nullable
    public static Addon getManagerByName(String name) {
        return managerMap.get(name);
    }

    @Nullable
    public static Addon getManagerByAlias(String alias) {
        if (!availableManagers.containsKey(alias)) {
            return null;
        }
        return managerMap.get(availableManagers.get(alias));
    }

    public static Collection<Addon> getManagers() {
        return managerMap.values();
    }

    public static boolean isRegistered(String name) {
        if (!managerMap.containsKey(name)) {
            return false;
        }
        return isRegistered(managerMap.get(name));
    }

    public static boolean isRegistered(Addon addon) {
        return addon.registered;
    }
}
