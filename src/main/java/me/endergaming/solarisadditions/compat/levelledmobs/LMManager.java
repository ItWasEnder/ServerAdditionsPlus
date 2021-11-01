package me.endergaming.solarisadditions.compat.levelledmobs;

import me.endergaming.enderlibs.util.EventListener;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.Manager;
import me.endergaming.solarisadditions.compat.backend.Managers;
import me.endergaming.solarisadditions.compat.backend.events.ManagerRegisterEvent;
import me.endergaming.solarisadditions.compat.mcmmo.MCMMOManager;
import me.lokka30.levelledmobs.LevelInterface;
import me.lokka30.levelledmobs.LevelledMobs;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.endergaming.solarisadditions.controllers.ConfigController.MCMMO;

public class LMManager extends Manager {
    private Listener listener;
    private LevelInterface levelInterface;

    public LMManager(@NotNull final SolarisAdditions instance, @NotNull String reqPlugin, @NotNull String... reqManagers) {
        super(instance, "levelled-mobs", reqPlugin, reqManagers);
    }

    @Override
    public void register() {
        if (super.registered) unregister();
        if (levelInterface == null) levelInterface = ((LevelledMobs) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("LevelledMobs"))).levelInterface;
        if (listener != null) HandlerList.unregisterAll(listener);
        // Register EXP or McMMO xp drop enhancements
        if (MCMMO) {
            if (!Managers.isRegistered("MCMMOManager")) {
                new EventListener<>(ManagerRegisterEvent.class, (l,e) -> {
                    if (!e.getManager().getName().equals("MCMMOManager")) return;
                    registerMcMMO();
                    l.unregister();
                });
            } else {
                registerMcMMO();
            }
        } else {
            listener = new DropXPListener(this);
            Bukkit.getPluginManager().registerEvents(listener, getPlugin());
        }
        super.register();
    }

    private void registerMcMMO() {
        if (listener != null) HandlerList.unregisterAll(listener);
        listener = new MMOExperienceListener((MCMMOManager) Managers.getManagerByAlias("mcmmo"), this);
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(listener);
        super.unregister();
    }

    public boolean isLevelled(LivingEntity entity) {
        if (levelInterface == null) return false;
        return levelInterface.isLevelled(entity);
    }

    public int getLevel(LivingEntity entity) {
        if (levelInterface == null) return 0;
        int level = levelInterface.getLevelOfMob(entity);
        return (level == -1) ? 0 : level;
    }

    protected LevelInterface getInterface() {
        return levelInterface;
    }
}
