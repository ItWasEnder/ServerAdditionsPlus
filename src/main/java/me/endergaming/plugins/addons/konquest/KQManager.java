package me.endergaming.plugins.addons.konquest;

import konquest.Konquest;
import konquest.manager.LootManager;
import me.endergaming.enderlibs.text.MessageUtils;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.exceptions.AddonException;
import me.endergaming.plugins.backend.exceptions.AddonRegistrationException;
import me.endergaming.plugins.misc.Globals;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;

public class KQManager extends Addon {
    private HashMap<ItemStack, Integer> lootTable;
    private Konquest kon;

    public KQManager(@NotNull final ServerAdditionsPlus instance, @NotNull Globals.Plugins reqPlugin, @NotNull String... reqManagers) {
        super(instance, "Konquest", reqPlugin, reqManagers);
    }

    //     TODO:
    //          - Disable breaking town blocks outside of monument during raid
    //          - Add custom item support probably Vouchers plugin
    @Override
    public void onEnable() throws AddonException {
        this.kon = Konquest.getInstance();

        this.setupLootTable();
    }

    private void setupLootTable() throws AddonRegistrationException {
        var loot = this.kon.getLootManager();
        try {
            Field _lootTable = LootManager.class.getDeclaredField("lootTable");

            _lootTable.setAccessible(true);

            this.lootTable = (HashMap<ItemStack, Integer>) _lootTable.get(loot);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AddonRegistrationException(e.getMessage());
        }
    }

    @Override
    public void onDisable() {

    }

    public void addLoot(ItemStack item, int weight) {
        if (this.lootTable == null) {
            MessageUtils.log(MessageUtils.LogLevel.WARNING, "Konquest loot table not found! Could not add loot.");
            return;
        }

        this.lootTable.put(item, weight);
    }
}
