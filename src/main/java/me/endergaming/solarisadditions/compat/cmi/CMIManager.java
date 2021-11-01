package me.endergaming.solarisadditions.compat.cmi;

import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.Manager;
import org.jetbrains.annotations.NotNull;

public class CMIManager extends Manager {
    public CMIManager(@NotNull final SolarisAdditions instance, @NotNull String reqPlugin) {
        super(instance, "cmi", reqPlugin);
    }
}
