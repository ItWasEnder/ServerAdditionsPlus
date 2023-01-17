package me.endergaming.plugins.addons.mmocore;

import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.mmocore.events.OnBlockInteract;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.events.EventManager;
import me.endergaming.plugins.backend.exceptions.AddonException;
import me.endergaming.plugins.misc.Globals;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MMOManager extends Addon implements Listener {
    Map<UUID, Integer> levelCache = new HashMap<>();

    EnumMap<Material, Integer> blockLevels;

    public MMOManager(@NotNull ServerAdditionsPlus instance, @NotNull String alias, Globals.Plugins reqPlugin) {
        super(instance, alias, reqPlugin);
    }

    @Override
    public void onEnable() throws AddonException {
        EventManager.get().register(new OnBlockInteract(this));

        if (this.blockLevels == null) {
            this.blockLevels = new EnumMap<>(Material.class);
        }

        this.blockLevels.put(Material.IRON_ORE, 1);
        this.blockLevels.put(Material.GOLD_ORE, 3);
        this.blockLevels.put(Material.DIAMOND_ORE, 5);
        this.blockLevels.put(Material.EMERALD_ORE, 7);
        this.blockLevels.put(Material.NETHER_GOLD_ORE, 9);
        this.blockLevels.put(Material.NETHERITE_BLOCK, 100);

        System.out.println("Blocks registered: " + this.blockLevels.size());

        Bukkit.getPluginManager().registerEvents(this, ServerAdditionsPlus.getPlugin());
    }

    public Integer getLevel(UUID uuid) {
        return this.levelCache.computeIfAbsent(uuid, k -> 0);
    }

    public void setLevel(UUID uuid, int level) {
        this.levelCache.put(uuid, level);
    }

    public Integer getBlockLevel(Material material) {
        return this.blockLevels.getOrDefault(material, 0);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    private void onProfessionLevelUp(PlayerLevelUpEvent event) {
        if (event.getProfession().getName().equalsIgnoreCase("mining")) {
            this.setLevel(event.getPlayer().getUniqueId(), event.getNewLevel());
        }
    }
}
