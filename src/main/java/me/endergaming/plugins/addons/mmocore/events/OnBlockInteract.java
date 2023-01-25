package me.endergaming.plugins.addons.mmocore.events;

import com.marcusslover.plus.lib.events.EventListener;
import com.marcusslover.plus.lib.events.annotations.Event;
import com.marcusslover.plus.lib.text.Text;
import me.endergaming.plugins.addons.mmocore.MMOManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

public class OnBlockInteract implements EventListener {

    private final MMOManager manager;

    public OnBlockInteract(MMOManager mmoManager) {
        this.manager = mmoManager;
    }

    @Event
    private void onBlockInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getItem() == null || !this.isPickaxe(event.getItem().getType())) {
            return;
        }

        int requiredLevel = this.manager.getBlockLevel(event.getClickedBlock().getType());

        if (requiredLevel <= 0) {
            return;
        }

        int playerLevel = this.manager.getLevel(event.getPlayer().getUniqueId());

        System.out.println("Player level: " + playerLevel + " | Required level: " + requiredLevel);

        if (playerLevel < requiredLevel) {
            event.setCancelled(true);

            Text.of("&7[&#FFB03F⛏&7] &#FF4F54Insufficient mining level! &8(&e%s&8)".formatted(requiredLevel)).send(event.getPlayer());

            PotionEffectType.SLOW_DIGGING.createEffect(20, 9)
                    .withParticles(false)
                    .withIcon(false)
                    .apply(event.getPlayer());
        }
    }

    @Event
    private void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        var requiredLevel = this.manager.getBlockLevel(event.getBlock().getType());

        if (requiredLevel <= 0) {
            return;
        }

        var playerLevel = this.manager.getLevel(event.getPlayer().getUniqueId());

        var item = event.getPlayer().getInventory().getItemInMainHand();

        if (this.isPickaxe(item.getType())) {
            if (playerLevel < requiredLevel) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isPickaxe(Material material) {
        return material.toString().toLowerCase().endsWith("_pickaxe");
    }
}