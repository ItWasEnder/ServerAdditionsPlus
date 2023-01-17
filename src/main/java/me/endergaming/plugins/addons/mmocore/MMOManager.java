package me.endergaming.plugins.addons.mmocore;

import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.mmocore.events.OnBlockInteract;
import me.endergaming.plugins.addons.mmocore.events.OnPlayerJoin;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.events.EventManager;
import me.endergaming.plugins.backend.exceptions.AddonException;
import me.endergaming.plugins.misc.Globals;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MMOManager extends Addon {
    Map<UUID, PlayerData> playerDataCache = new ConcurrentHashMap<>();

    EnumMap<Material, Integer> blockLevels;

    public MMOManager(@NotNull ServerAdditionsPlus instance, @NotNull String alias, Globals.Plugins reqPlugin) {
        super(instance, alias, reqPlugin);
    }

    @Override
    public void onEnable() throws AddonException {
        /* Register observers */
        EventManager.get().register(new OnBlockInteract(this));
        EventManager.get().register(new OnPlayerJoin(this));

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
}
