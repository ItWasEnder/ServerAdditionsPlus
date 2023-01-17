package me.endergaming.plugins.addons.mmocore.events;

import me.endergaming.plugins.addons.mmocore.MMOManager;
import me.endergaming.plugins.backend.events.Event;
import me.endergaming.plugins.backend.events.EventListener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements EventListener {

    private final MMOManager manager;

    public OnPlayerJoin(MMOManager mmoManager) {
        this.manager = mmoManager;
    }

    @Event
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        this.manager.loadPlayer(event.getPlayer());
    }
}
