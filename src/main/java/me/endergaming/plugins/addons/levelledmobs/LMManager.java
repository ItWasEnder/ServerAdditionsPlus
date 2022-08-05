package me.endergaming.plugins.addons.levelledmobs;

import me.endergaming.enderlibs.misc.EventListener;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.backend.Addon;
import me.endergaming.plugins.addons.backend.AddonManager;
import me.endergaming.plugins.addons.backend.events.AddonRegisterEvent;
import me.endergaming.plugins.addons.mcmmo.MCMMOManager;
import me.lokka30.levelledmobs.LevelInterface;
import me.lokka30.levelledmobs.LevelledMobs;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.endergaming.plugins.controllers.ConfigController.MCMMO;

public class LMManager extends Addon {
    private Listener listener;
    private LevelInterface levelInterface;

    public LMManager(@NotNull final ServerAdditionsPlus instance, @NotNull String reqPlugin, @NotNull String... reqManagers) {
        super(instance, "levelled-mobs", reqPlugin, reqManagers);
    }

    @Override
    public void onEnable() {
        if (this.levelInterface == null) {
            this.levelInterface = ((LevelledMobs) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("LevelledMobs"))).levelInterface;
        }
        if (this.listener != null) {
            HandlerList.unregisterAll(this.listener);
        }
        // Register EXP or McMMO xp drop enhancements
        if (MCMMO) {
            if (!AddonManager.isRegistered("MCMMOManager")) {
                new EventListener<>(AddonRegisterEvent.class, (l, e) -> {
                    if (!e.getAddon().getName().equals("MCMMOManager")) {
                        return;
                    }
                    this.registerMcMMO();
                    l.unregister();
                });
            } else {
                this.registerMcMMO();
            }
        } else {
            this.listener = new DropXPListener(this);
            Bukkit.getPluginManager().registerEvents(this.listener, this.getPlugin());
        }
    }

    private void registerMcMMO() {
        if (this.listener != null) {
            HandlerList.unregisterAll(this.listener);
        }
        this.listener = new MMOExperienceListener((MCMMOManager) AddonManager.getManagerByAlias("mcmmo"), this);
        Bukkit.getPluginManager().registerEvents(this.listener, this.getPlugin());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this.listener);
    }

    public boolean isLevelled(LivingEntity entity) {
        if (this.levelInterface == null) {
            return false;
        }
        return this.levelInterface.isLevelled(entity);
    }

    public int getLevel(LivingEntity entity) {
        if (this.levelInterface == null) {
            return 0;
        }
        int level = this.levelInterface.getLevelOfMob(entity);
        return (level == -1) ? 0 : level;
    }

    protected LevelInterface getInterface() {
        return this.levelInterface;
    }
}
