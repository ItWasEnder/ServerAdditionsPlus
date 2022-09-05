package me.endergaming.plugins.addons.papi;

import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.backend.Addon;
import me.endergaming.plugins.misc.Globals;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends Addon {
    private PlaceholderAPIHook hook;

    public PlaceholderManager(@NotNull final ServerAdditionsPlus instance, @NotNull Globals.Plugins reqPlugin) {
        super(instance, "placeholders", reqPlugin);
    }

    @Override
    public void onEnable() {
        if (this.hook == null) {
            this.hook = new PlaceholderAPIHook(this.getPlugin());
        }
        this.hook.register();
    }

    @Override
    public void onDisable() {
        this.hook.unregister();
    }
}
