package me.endergaming.plugins.commands;

import me.endergaming.enderlibs.command.SubCommand;
import me.endergaming.enderlibs.text.MessageUtils;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.AddonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.endergaming.plugins.commands.CommandRegistry.prefix;

public class ManagerCommand extends SubCommand {
    private final ServerAdditionsPlus plugin;

    public ManagerCommand(@NotNull ServerAdditionsPlus plugin, String command) {
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
            messages.add("&6Available AddonManager:");
            for (Addon addon : AddonManager.getManagers()) {
                messages.add("&7  ▪ " + (addon.isRegistered() ? "&a" : "&c") + addon.getManagerAlias());
            }
            messages.add("&b&l&m                             ");
            MessageUtils.send(sender, String.join("\n", messages));
            return;
        }

        if (args.length < 3) {
            MessageUtils.send(sender, this);
            return;
        }

        Addon addon = AddonManager.getManagerByAlias(args[2]);
        if (addon == null) {
            MessageUtils.send(sender, prefix.replace("&a", "&c") + "&cInvalid Addon.");
            return;
        }

        if (args[1].equalsIgnoreCase("reqs")) {
            StringBuilder message = new StringBuilder("&b" + addon.getName() + " &erequires these; ");
            message.append(ServerAdditionsPlus.isEnabled(addon.getRequiredPlugin()) ? "&a" : "&c").append(addon.getRequiredPlugin());
            List<String> reqs = addon.getRequirements();
            if (!reqs.isEmpty()) {
                for (int i = 0; i < reqs.size(); ++i) {
                    if (i <= reqs.size() - 1) {
                        message.append("&7, ");
                    }
                    message.append(AddonManager.isRegistered(reqs.get(i)) ? "&a" : "&c").append(reqs.get(i));
                }
            }
            MessageUtils.send(sender, prefix + message);
            return;
        }

        if (addon.requirementsNotFilled() && !addon.hasReqPlugin()) {
            MessageUtils.send(sender, String.format("%s&cSomething is not installed or enabled! &7Run: /sa managers reqs %s", prefix.replace("&a", "&c"), addon.getManagerAlias()));
            return;
        }

        if (args[1].equalsIgnoreCase("reload")) {
            long start = System.nanoTime();
            AddonManager.reload(addon);
            long total = System.nanoTime() - start;
            MessageUtils.send(sender, String.format("%s&dReloaded addon &b%s&d in &a%.2fms", prefix, addon.getManagerAlias(), total / 1000000D));
        } else if (args[1].equalsIgnoreCase("register")) {
            AddonManager.register(addon);
            MessageUtils.send(sender, prefix + "&aRegistered addon &b" + addon.getManagerAlias());
        } else if (args[1].equalsIgnoreCase("unregister")) {
            AddonManager.unregister(addon);
            MessageUtils.send(sender, prefix + "&cUnregistered addon &b" + addon.getManagerAlias());
        } else {
            MessageUtils.send(sender, this);
        }
    }
}
