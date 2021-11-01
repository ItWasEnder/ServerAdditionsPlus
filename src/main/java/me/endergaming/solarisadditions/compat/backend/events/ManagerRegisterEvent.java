package me.endergaming.solarisadditions.compat.backend.events;

import me.endergaming.solarisadditions.compat.backend.Manager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManagerRegisterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Manager manager;
    private final boolean isRegistered;

    public ManagerRegisterEvent(@NotNull Manager manager, boolean registerd) {
        this.manager = manager;
        this.isRegistered = registerd;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    @NotNull
    public Manager getManager() {
        return manager;
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
