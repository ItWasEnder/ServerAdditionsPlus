package me.endergaming.plugins.addons.konquest;

import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.backend.Addon;
import org.jetbrains.annotations.NotNull;

public class KQManager extends Addon {
    public KQManager(@NotNull final ServerAdditionsPlus instance, @NotNull String reqPlugin, @NotNull String... reqManagers) {
        super(instance, "Konquest", reqPlugin, reqManagers);
    }

//     TODO:
//          - Disable breaking town blocks outside of monument during raid
    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
