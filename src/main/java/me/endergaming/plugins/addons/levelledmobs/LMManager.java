package me.endergaming.plugins.addons.levelledmobs;

import me.endergaming.enderlibs.misc.EventListener;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.AddonManager;
import me.endergaming.plugins.backend.events.AddonRegisterEvent;
import me.endergaming.plugins.addons.skills.SkillsManager;
import me.endergaming.plugins.misc.Globals;
import me.lokka30.levelledmobs.LevelInterface;
import me.lokka30.levelledmobs.LevelledMobs;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.endergaming.plugins.controllers.ConfigController.SKILLS;

public class LMManager extends Addon {
    private Listener listener;
    private LevelInterface levelInterface;

    public LMManager(@NotNull final ServerAdditionsPlus instance, @NotNull Globals.Plugins reqPlugin, @NotNull String... reqManagers) {
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
        if (SKILLS) {
            if (!AddonManager.isRegistered(Globals.Addons.SkillsManager.name)) {
                new EventListener<>(AddonRegisterEvent.class, (l, e) -> {
                    if (!e.getAddon().getName().equals(Globals.Addons.SkillsManager.name)) {
                        return;
                    }
                    this.registerListener();
                    l.unregister();
                });
            } else {
                this.registerListener();
            }
        } else {
            this.listener = new DropXPListener(this);
            Bukkit.getPluginManager().registerEvents(this.listener, this.getPlugin());
        }
    }

    private void registerListener() {
        if (this.listener != null) {
            HandlerList.unregisterAll(this.listener);
        }

        ServerAdditionsPlus.debug("Registering SkillsManager listener");
        this.listener = new SkillExperienceListener((SkillsManager) AddonManager.getManagerByAlias("skills"), this);
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
