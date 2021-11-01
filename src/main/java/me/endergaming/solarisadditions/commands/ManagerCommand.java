package me.endergaming.solarisadditions.commands;

import me.endergaming.enderlibs.command.SubCommand;
import me.endergaming.enderlibs.text.MessageUtils;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.Manager;
import me.endergaming.solarisadditions.compat.backend.Managers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.endergaming.solarisadditions.commands.CommandRegistry.prefix;

public class ManagerCommand extends SubCommand {
    private final SolarisAdditions plugin;

    public ManagerCommand(@NotNull SolarisAdditions plugin, String command) {
        super(plugin, command);
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            MessageUtils.send(sender, this);
            return;
        }

        if (args[1].equalsIgnoreCase("list")) {
            List<String> messages = new ArrayList<>();
            messages.add("&b&l&m                             ");
            messages.add("&6Available Managers:");
            for (Manager manager : Managers.getManagers()) {
                messages.add("&7  ▪ " + (manager.isRegistered() ? "&a" : "&c") + manager.getManagerAlias());
            }
            messages.add("&b&l&m                             ");
            MessageUtils.send(sender, String.join("\n", messages));
            return;
        }

        if (args.length < 3) {
            MessageUtils.send(sender, this);
            return;
        }

        Manager manager = Managers.getManagerByAlias(args[2]);
        if (manager == null) {
            MessageUtils.send(sender, prefix.replace("&a", "&c") + "&cInvalid Manager.");
            return;
        }

        if (args[1].equalsIgnoreCase("reqs")) {
            StringBuilder message = new StringBuilder("&b" + manager.getName() + " &erequires these; ");
            message.append(SolarisAdditions.isEnabled(manager.getRequiredPlugin()) ? "&a" : "&c").append(manager.getRequiredPlugin());
            List<String> reqs = manager.getRequirements();
            if (!reqs.isEmpty()) {
                for (int i = 0; i < reqs.size(); ++i) {
                    if (i <= reqs.size() - 1) message.append("&7, ");
                    message.append(Managers.isRegistered(reqs.get(i)) ? "&a" : "&c").append(reqs.get(i));
                }
            }
            MessageUtils.send(sender, prefix + message);
            return;
        }

        if (!manager.requirementsFulfilled() && !manager.hasReqPlugin()) {
            MessageUtils.send(sender, String.format("%s&cSomething is not installed or enabled! &7Run: /sa managers reqs %s", prefix.replace("&a", "&c"), manager.getManagerAlias()));
            return;
        }

        if (args[1].equalsIgnoreCase("reload")) {
            long start = System.nanoTime();
            manager.unregister();
            manager.register();
            long total = System.nanoTime() - start;
            MessageUtils.send(sender, String.format("%s&dReloaded manager &b%s&d in &a%.2fms", prefix, manager.getManagerAlias(), total / 1000000D));
        } else if (args[1].equalsIgnoreCase("register")) {
            manager.register();
            MessageUtils.send(sender, prefix + "&aRegistered manager &b" + manager.getManagerAlias());
        } else if (args[1].equalsIgnoreCase("unregister")) {
            manager.unregister();
            MessageUtils.send(sender, prefix + "&cUnregistered manager &b" + manager.getManagerAlias());
        } else {
            MessageUtils.send(sender, this);
        }
    }
}
