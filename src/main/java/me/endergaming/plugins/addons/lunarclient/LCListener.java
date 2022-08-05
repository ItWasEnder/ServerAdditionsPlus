package me.endergaming.plugins.addons.lunarclient;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.lunarclient.bukkitapi.LunarClientAPI;
import me.endergaming.enderlibs.misc.Task;
import me.endergaming.enderlibs.text.MessageUtils;
import me.endergaming.plugins.misc.Globals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class LCListener implements Listener {
    private final LCManager manager;
    private final LunarClientAPI api;

    public LCListener(@NotNull LCManager manager) {
        this.manager = manager;
        this.api = LunarClientAPI.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Task.asyncDelayed(task -> {
            if (this.api == null) {
                HandlerList.unregisterAll(this);
                return;
            }
            Player player = event.getPlayer();
            if (!this.api.isRunningLunarClient(player.getUniqueId())) {
                // Player is not currently running Lunar
                MessageUtils.send(player, """
                        \s
                        &bIt appears you are not running &f&bLunarClient.\s
                        &bIf you join with LunarClient you can receive &3100 %s&b\s
                        &aClick here for info https://www.lunarclient.com/features/
                        \s""".formatted(Globals.CURRENCY_1));
            } else {
                if (this.manager.isIgnored(player.getUniqueId())) {
                    MessageUtils.send(player, " \n&aThanks for joining with LunarClient! &7(Reward already received)\n ");
                } else {
                    if (CMI.getInstance().getEconomyManager().isEnabled()) {
                        CMIUser user = CMIUser.getUser(player.getUniqueId());
                        user.deposit(this.manager.getReward());
                        MessageUtils.send(player, " \n&aThanks for joining with LunarClient!\n&bYou were rewarded &3100 %s\n ".formatted(Globals.CURRENCY_1));
                    } else {
                        MessageUtils.send(player, " \n&aThanks for joining with LunarClient!\n ");
                    }
                    this.manager.addUUID(player.getUniqueId());
                }
            }
        }, 40L);
    }
}
