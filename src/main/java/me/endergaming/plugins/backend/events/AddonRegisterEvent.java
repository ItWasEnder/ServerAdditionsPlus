package me.endergaming.plugins.backend.events;

import me.endergaming.plugins.backend.Addon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AddonRegisterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Addon addon;
    private final boolean isRegistered;

    public AddonRegisterEvent(@NotNull Addon addon, boolean registered) {
        this.addon = addon;
        this.isRegistered = registered;
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    @NotNull
    public Addon getAddon() {
        return this.addon;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
