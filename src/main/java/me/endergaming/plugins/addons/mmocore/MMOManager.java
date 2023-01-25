package me.endergaming.plugins.addons.mmocore;

import com.marcusslover.plus.lib.events.EventHandler;
import io.lumine.mythic.lib.hologram.Hologram;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.mmocore.events.OnPlayerSkillHit;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.exceptions.AddonException;
import me.endergaming.plugins.misc.Globals;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MMOManager extends Addon {
    Map<UUID, PlayerData> playerDataCache = new ConcurrentHashMap<>();
    EnumMap<Material, Integer> blockLevels;

    public static final int TIME_TO_KILL = 15;
    public static final double SKILL_XP_MODIFIER = 0.8;
    public static final double PASSIVE_MOB_MODIFIER = 0.5;
    public static final boolean DEBUG = true;

    public MMOManager(@NotNull ServerAdditionsPlus instance, @NotNull String alias, Globals.Plugins reqPlugin) {
        super(instance, alias, reqPlugin);
    }

    @Override
    public void onEnable() throws AddonException {
        /* Subscribe events */
        EventHandler.get().subscribe(new OnPlayerSkillHit());

        if (this.blockLevels == null) {
            this.blockLevels = new EnumMap<>(Material.class);
        }

        /* Block level requirements */
        this.blockLevels.put(Material.IRON_ORE, 1);
        this.blockLevels.put(Material.GOLD_ORE, 3);
        this.blockLevels.put(Material.DIAMOND_ORE, 5);
        this.blockLevels.put(Material.EMERALD_ORE, 7);
        this.blockLevels.put(Material.NETHER_GOLD_ORE, 9);
        this.blockLevels.put(Material.NETHERITE_BLOCK, 100);

        ServerAdditionsPlus.logger()
                .info("Block Requirements Registered: " + this.blockLevels.size());
    }

    @Override
    public void onDisable() {
        // Code
    }

    public PlayerData getData(UUID uuid) {
        return this.playerDataCache.computeIfAbsent(uuid, k -> PlayerData.get(uuid));
    }

    public void loadPlayer(Player player) {
        this.playerDataCache.remove(player.getUniqueId());

        this.getData(player.getUniqueId());
    }

    public Integer getLevel(UUID uuid) {
        var data = this.getData(uuid);

        return data.getCollectionSkills().getLevel(Professions.MINING);
    }

    public Integer getBlockLevel(Material material) {
        return this.blockLevels.getOrDefault(material, 0);
    }

    public static Vector getIndicatorDirection(Entity entity) {

        if (entity instanceof Player) {
            double a = Math.toRadians(((Player) entity).getEyeLocation().getYaw()) + Math.PI * (1 + (Globals.RANDOM.nextDouble() - .5) / 2);
            return new Vector(Math.cos(a), 0, Math.sin(a));
        }

        double a = Globals.RANDOM.nextDouble() * Math.PI * 2;
        return new Vector(Math.cos(a), 0, Math.sin(a));
    }

    public static void displayIndicator(Entity entity, String message, @NotNull Vector dir) {
        double yOffset = .6;
        double entityHeightPercentage = 0.75;

        Location loc = entity.getLocation().add((Globals.RANDOM.nextDouble() - .5) * 1.2, yOffset + entity.getHeight() * entityHeightPercentage, (Globals.RANDOM.nextDouble() - .5) * 1.2);
        displayIndicator(loc, message, dir);
    }

    private static void displayIndicator(Location loc, String message, Vector dir) {
        Hologram holo = Hologram.create(loc, Collections.singletonList(message));

        final int lifeTime = 7;
        new BukkitRunnable() {
            double v = 6; // Initial upward velocity
            int i = 0; // Counter

            private final double acc = -10; // Downwards acceleration
            private final double dt = 3d / 20d; // Delta_t used to integrate acceleration and velocity

            @Override
            public void run() {

                if (this.i == 0) {
                    dir.multiply(2);
                }

                // Remove hologram when reaching end of life
                if (this.i++ >= lifeTime) {
                    holo.despawn();
                    this.cancel();
                    return;
                }

                this.v += this.acc * this.dt;
                loc.add(dir.getX() * this.dt, this.v * this.dt, dir.getZ() * this.dt);
                holo.updateLocation(loc);
            }
        }.runTaskTimer(ServerAdditionsPlus.getPlugin(), 0, 3);
    }

    public static void log(String msg) {
        if (MMOManager.DEBUG) {
            ServerAdditionsPlus.logger().info(msg);
        }
    }
}
