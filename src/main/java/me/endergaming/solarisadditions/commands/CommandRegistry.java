package me.endergaming.solarisadditions.commands;

import me.endergaming.enderlibs.command.CommandManager;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.cmi.SpeedCommand;
import org.jetbrains.annotations.NotNull;

public class CommandRegistry {
    private final SolarisAdditions plugin;
    public static final String prefix = "&7[&a\uD83E\uDDEA&7] ";

    public CommandRegistry(@NotNull final SolarisAdditions instance) {
        this.plugin = instance;
    }

    public void register() {
        CommandManager commandManager = new CommandManager();

        SolarisAdditionsCommand saCommand = (SolarisAdditionsCommand) new SolarisAdditionsCommand(plugin, "sa")
                .setAlias("sadmin")
                .setHasCommandArgs(true)
                .setPlayerOnly(true)
                .setDescription("&b* Manage all custom additions on the server")
                .setUsage("&bUsage: &3/sa &b[managers/debug] &7- &fBase Admin Command");

        commandManager.register(saCommand,
                new ManagerCommand(plugin, "managers")
                        .setDescription("&b* Perform actions on specific managers")
                        .setUsage("&bUsage: &3/sa manager &b[reload/list/unregister/register] [id] &7- &fPreform actions on managers"),
                new DebugCommand(plugin, "debug")
                        .setDescription("&b* Prints debug information to player"));

        SpeedCommand speed = (SpeedCommand) new SpeedCommand(plugin, "speed")
                .setHasCommandArgs(false)
                .setPlayerOnly(true)
                .setDescription("&b* Set your speed from 0 to 10")
                .setUsage("&bUsage: &3/speed &b[playerName] [amount]");

        commandManager.register(speed);
    }
}
