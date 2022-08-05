package me.endergaming.plugins.addons.moneyfrommobs;

import me.chocolf.moneyfrommobs.api.event.AttemptToDropMoneyEvent;
import me.chocolf.moneyfrommobs.api.event.DropMoneyEvent;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.backend.AddonManager;
import me.endergaming.plugins.addons.levelledmobs.LMManager;
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

import static me.endergaming.plugins.controllers.ConfigController.*;

public class MFMListener implements Listener {
    private final MFMManager manager;

    public MFMListener(@NotNull MFMManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDropAttempt(AttemptToDropMoneyEvent event) {
        // Levelled Mob Drop Chance
        if (AddonManager.isRegistered("LMManager")) {
            LMManager lm = (LMManager) AddonManager.getManagerByName("LMManager");
            assert lm != null;
            if (!lm.isLevelled((LivingEntity) event.getEntity())) {
                return;
            }
            int level = lm.getLevel((LivingEntity) event.getEntity());
            double chance = event.getDropChance();
            ServerAdditionsPlus.debug("- - - - - - - - - - -");
            ServerAdditionsPlus.debug("Mob: " + event.getEntity().getType().getKey().getKey());
            ServerAdditionsPlus.debug("Old Drop Chance: " + chance);
            chance += chance * (level * DROP_CHANCE_MOD);
            ServerAdditionsPlus.debug("New Drop Chance: " + chance);
            ServerAdditionsPlus.debug("- - - - - - - - - - -");
            event.setDropChance(chance);
        }
        // Block Unnatural Spawners
        if (RESTRICT_SPAWNERS) {
            PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
            if (!container.has(ServerAdditionsPlus.KEY, PersistentDataType.STRING)) {
                return;
            }
            if (Objects.equals(container.get(ServerAdditionsPlus.KEY, PersistentDataType.STRING), RESTRICTION.UNNATURAL_SPAWN.toString())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMoneyDrop(DropMoneyEvent event) {
        // Do Drop Amount
        int amount = event.getAmount().intValue();
        int dropAmount = event.getNumberOfDrops();
        if (this.isSpawnerSpawned(event.getEntity())) {
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
        if (event.isCancelled()) {
            return;
        }
        if (!this.manager.isUnnatural(event.getSpawner())) {
            return;
        }
        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        container.set(ServerAdditionsPlus.KEY, PersistentDataType.STRING, RESTRICTION.UNNATURAL_SPAWN.toString());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlockPlaced().getType() != Material.SPAWNER) {
            return;
        }
        if (!(event.getBlock().getState() instanceof CreatureSpawner)) {
            return;
        }
        CreatureSpawner spawner = (CreatureSpawner) event.getBlockPlaced().getState();
        this.manager.addSpawner(spawner);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawnerPlace(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getBlock().getType() != Material.SPAWNER) {
            return;
        }
        if (!(event.getBlock().getState() instanceof CreatureSpawner)) {
            return;
        }
        CreatureSpawner spawner = (CreatureSpawner) event.getBlock().getState();
        this.manager.removeSpawner(spawner);
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
