package me.endergaming.solarisadditions.compat.moneyfrommobs;

import me.chocolf.moneyfrommobs.api.event.AttemptToDropMoneyEvent;
import me.chocolf.moneyfrommobs.api.event.DropMoneyEvent;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.Managers;
import me.endergaming.solarisadditions.compat.levelledmobs.LMManager;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.endergaming.solarisadditions.controllers.ConfigController.*;

public class MFMListener implements Listener {
    private final MFMManager manager;

    public MFMListener(@NotNull MFMManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDropAttempt(AttemptToDropMoneyEvent event) {
        // Levelled Mob Drop Chance
        if (Managers.isRegistered("LMManager")) {
            LMManager lm = (LMManager) Managers.getManagerByName("LMManager");
            assert lm != null;
            if (!lm.isLevelled((LivingEntity) event.getEntity())) return;
            int level = lm.getLevel((LivingEntity) event.getEntity());
            double chance = event.getDropChance();
            SolarisAdditions.debug("- - - - - - - - - - -");
            SolarisAdditions.debug("Mob: " + event.getEntity().getType().getKey().getKey());
            SolarisAdditions.debug("Old Drop Chance: " + chance);
            chance += chance * (level * DROP_CHANCE_MOD);
            SolarisAdditions.debug("New Drop Chance: " + chance);
            SolarisAdditions.debug("- - - - - - - - - - -");
            event.setDropChance(chance);
        }
        // Block Unnatural Spawners
        if (RESTRICT_SPAWNS) {
            PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
            if (!container.has(SolarisAdditions.KEY, PersistentDataType.STRING)) return;
            if (Objects.equals(container.get(SolarisAdditions.KEY, PersistentDataType.STRING), RESTRICTION.UNNATURAL_SPAWN.toString()))
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMoneyDrop(DropMoneyEvent event) {
        // Do Drop Amount
        int amount = event.getAmount().intValue();
        int dropAmount = event.getNumberOfDrops();
        if (isSpawnerSpawned(event.getEntity())) {
            int nAmount = (int) Math.floor((double) amount / SPAWNER_NERF_MOD);
            int mod = Math.floorMod(nAmount, dropAmount);
            nAmount -= mod;
            event.setAmount((double) nAmount);
        } else {
            int mod = Math.floorMod(amount, dropAmount);
            int nAmount = amount - mod;
            event.setAmount((double) nAmount);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawner(SpawnerSpawnEvent event) {
        if (event.isCancelled()) return;
        if (!manager.isUnnatural(event.getSpawner())) return;
        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        container.set(SolarisAdditions.KEY, PersistentDataType.STRING, RESTRICTION.UNNATURAL_SPAWN.toString());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (event.getBlockPlaced().getType() != Material.SPAWNER) return;
        if (!(event.getBlock().getState() instanceof CreatureSpawner)) return;
        CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
        manager.addSpawner(spawner);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawnerPlace(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (event.getBlock().getType() != Material.SPAWNER) return;
        if (!(event.getBlock().getState() instanceof CreatureSpawner)) return;
        CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
        manager.removeSpawner(spawner);
    }

    private boolean isSpawnerSpawned(Entity entity) {
        return entity.getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER;
    }

    private enum RESTRICTION {
        UNNATURAL_SPAWN("UNNATURAL_SPAWN");

        String value;

        RESTRICTION(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
