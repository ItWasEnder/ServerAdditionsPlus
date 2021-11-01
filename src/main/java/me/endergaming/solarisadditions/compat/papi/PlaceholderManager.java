package me.endergaming.solarisadditions.compat.papi;

import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.Manager;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends Manager {
    private PlaceholderAPIHook hook;

    public PlaceholderManager(@NotNull final SolarisAdditions instance, @NotNull String reqPlugin) {
        super(instance, "placeholders", reqPlugin);
    }

    @Override
    public void register() {
        if (super.registered) unregister();
        if (hook == null) hook = new PlaceholderAPIHook(getPlugin());
        hook.register();
        super.register();
    }

    @Override
    public void unregister() {
        hook.unregister();
        super.unregister();
    }
}
