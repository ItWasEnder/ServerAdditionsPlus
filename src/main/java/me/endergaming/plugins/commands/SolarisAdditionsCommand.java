package me.endergaming.plugins.commands;

import me.endergaming.enderlibs.command.MainCommand;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.backend.AddonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SolarisAdditionsCommand extends MainCommand {
    public SolarisAdditionsCommand(@NotNull ServerAdditionsPlus plugin, String command) {
        super(plugin, command);
    }

    @Override
    public void run(CommandSender sender, Command cmd, String label, String[] args) {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("sa.admin")) {
            return null;
        }

        // Do Stuff
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("managers")) {
                List<String> actions = Arrays.asList("register", "unregister", "list", "reload", "reqs");
                return actions.stream()
                        .map(String::toLowerCase)
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("managers")) {
                if (args[1].matches("reload|register|unregister|reqs")) {
                    return AddonManager.availableManagers.keySet().stream()
                            .map(String::toLowerCase)
                            .filter(s -> s.startsWith(args[2].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
        }
        return super.onTabComplete(sender, cmd, label, args);
    }
}
