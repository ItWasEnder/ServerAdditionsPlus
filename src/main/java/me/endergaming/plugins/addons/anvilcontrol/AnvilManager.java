package me.endergaming.plugins.addons.anvilcontrol;

import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.exceptions.AddonException;
import me.endergaming.plugins.misc.Globals;
import org.jetbrains.annotations.NotNull;

public class AnvilManager extends Addon {
    public AnvilManager(@NotNull ServerAdditionsPlus instance, @NotNull String alias, Globals.Plugins reqPlugin) {
        super(instance, alias, reqPlugin);
    }

    @Override
    public void onEnable() throws AddonException {
    }

    @Override
    public void onDisable() {

    }
}
