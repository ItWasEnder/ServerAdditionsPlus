package me.endergaming.solarisadditions.compat.levelledmobs;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.NumberConversions;

public class DropXPListener implements Listener {
    private final LMManager lmManager;

    public DropXPListener(LMManager lmManager) {
        this.lmManager = lmManager;
    }

    @EventHandler
    public void onExperience(EntityDeathEvent event) {
        if (!lmManager.isLevelled(event.getEntity())) return;
        int level = lmManager.getLevel(event.getEntity());
        event.setDroppedExp(event.getDroppedExp() + NumberConversions.round(level * 1.15));
    }
}
