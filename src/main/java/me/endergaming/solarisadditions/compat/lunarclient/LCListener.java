package me.endergaming.solarisadditions.compat.lunarclient;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.lunarclient.bukkitapi.LunarClientAPI;
import me.endergaming.enderlibs.text.MessageUtils;
import me.endergaming.enderlibs.util.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class LCListener implements Listener {
    private final LCManager manager;
    private LunarClientAPI api;

    public LCListener(@NotNull LCManager manager) {
        this.manager = manager;
        api = LunarClientAPI.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Task.asyncDelayed(task -> {
            if (api == null) {
                HandlerList.unregisterAll(this);
                return;
            }
            Player player = event.getPlayer();
            if (!api.isRunningLunarClient(player.getUniqueId())) {
                // Player is not currently running Lunar
                MessageUtils.send(player,
                        """
                                \s
                                &bIt appears you are not running &f&bLunarClient.\s
                                &bIf you join with LunarClient you can receive &3100 MC&b\s
                                &aClick here for info https://www.lunarclient.com/features/
                                \s""");
            } else {
                if (manager.isIgnored(player.getUniqueId())) {
                    MessageUtils.send(player, " \n&aThanks for joining with LunarClient! &7(Reward already received)\n ");
                } else {
                    if (CMI.getInstance().getEconomyManager().isEnabled()) {
                        CMIUser user = CMIUser.getUser(player.getUniqueId());
                        user.deposit(manager.getReward());
                        MessageUtils.send(player, " \n&aThanks for joining with LunarClient!\n&bYou were rewarded &3100 MC\n ");
                    } else {
                        MessageUtils.send(player, " \n&aThanks for joining with LunarClient!\n ");
                    }
                    manager.addUUID(player.getUniqueId());
                }
            }
        }, 40L);
    }
}
