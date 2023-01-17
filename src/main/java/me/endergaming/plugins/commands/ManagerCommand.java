package me.endergaming.plugins.commands;

import com.marcusslover.plus.lib.command.Command;
import com.marcusslover.plus.lib.command.CommandContext;
import com.marcusslover.plus.lib.command.ICommand;
import com.marcusslover.plus.lib.command.TabCompleteContext;
import com.marcusslover.plus.lib.text.Text;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.AddonManager;
import me.endergaming.plugins.misc.Globals;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "sap",
        aliases = {"sa", "sadmin"},
        permission = "sap.admin")
public class ManagerCommand implements ICommand {
    private final ServerAdditionsPlus plugin;

    public ManagerCommand(@NotNull ServerAdditionsPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandContext commandContext) {
        var sender = commandContext.sender();
        var args = commandContext.args();

        if (this.debug(commandContext)) {
            return true;
        }

        if (args.length < 2) {
            Text.of("&b* Manage all custom additions on the server" +
                    "\n&bUsage: &3/sa &b[managers/debug] &7- &fBase Admin Command")
                    .send(sender);
            return false;
        }

        if (args[1].equalsIgnoreCase("list")) {
            List<String> messages = new ArrayList<>();
            messages.add("&b&l&m                             ");
            messages.add("&6Available AddonManager:");
            for (Addon addon : AddonManager.getManagers()) {
                messages.add("&7  ▪ " + (addon.isRegistered() ? "&a" : "&c") + addon.getManagerAlias());
            }
            messages.add("&b&l&m                             ");
            Text.of(String.join("\n", messages)).send(sender);
            return false;
        }

        if (args.length < 3) {
            Text.of("&b* Manage all custom additions on the server" +
                    "\n&bUsage: &3/sa &b[managers/debug] &7- &fBase Admin Command")
                    .send(sender);
            return false;
        }

        Addon addon = AddonManager.getManagerByAlias(args[2]);
        if (addon == null) {
            Text.of(Globals.PREFIX.replace("&a", "&c") + "&cInvalid Addon.")
                    .send(sender);
            return false;
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
            Text.of(Globals.PREFIX + message)
                    .send(sender);
            return true;
        }

        if (addon.requirementsNotFilled() && !addon.hasReqPlugin()) {
            Text.of(String.format("%s&cSomething is not installed or enabled! &7Run: /sa managers reqs %s", Globals.PREFIX.replace("&a", "&c"), addon.getManagerAlias()))
                    .send(sender);
            return true;
        }

        if (args[1].equalsIgnoreCase("reload")) {
            long start = System.nanoTime();
            AddonManager.reload(addon);
            long total = System.nanoTime() - start;
            Text.of(String.format("%s&dReloaded addon &b%s&d in &a%.2fms", Globals.PREFIX, addon.getManagerAlias(), total / 1000000D))
                    .send(sender);
        } else if (args[1].equalsIgnoreCase("register")) {
            AddonManager.register(addon);
            Text.of(Globals.PREFIX + "&aRegistered addon &b" + addon.getManagerAlias())
                    .send(sender);
        } else if (args[1].equalsIgnoreCase("unregister")) {
            AddonManager.unregister(addon);
            Text.of(Globals.PREFIX + "&cUnregistered addon &b" + addon.getManagerAlias())
                    .send(sender);
        } else {
            Text.of("&b* Manage all custom additions on the server" +
                    "\n&bUsage: &3/sa &b[managers/debug] &7- &fBase Admin Command")
                    .send(sender);
        }

        return false;
    }

    private boolean debug(CommandContext context) {
        var sender = context.sender();
        var args = context.args();

        if (args.length < 1) {
            Text.of("&b* Manage all custom additions on the server" +
                    "\n&bUsage: &3/sa &b[managers/debug] &7- &fBase Admin Command")
                    .send(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("debug")) {
            PluginManager pm = Bukkit.getPluginManager();
            List<String> list = Arrays.stream(pm.getPlugins()).map(Plugin::getName).collect(Collectors.toList());
            StringBuilder plugins = new StringBuilder("Plugins: ");

            for (int i = 0; i < list.size(); i++) {
                plugins.append(list.get(i)).append(" v").append(pm.getPlugin(list.get(i)).getDescription().getVersion());
                if (i != list.size() - 1) {
                    plugins.append(", ");
                }
            }

            Component pluginPart = Component.text("(Click to Copy)")
                    .hoverEvent(HoverEvent.showText(Text.of("&e~copy~").comp()))
                    .color(TextColor.color(170, 170, 170))
                    .clickEvent(ClickEvent.copyToClipboard(plugins.toString()));

            Component finalPluginPart = Text.of("&dPlugins: " + pm.getPlugins().length + " ").comp()
                    .append(pluginPart);

            String version = "Version: " + Bukkit.getVersion();
            String saVersion = "S.A.P Version: " + this.plugin.getDescription().getVersion();
            String sys = "System: " + System.getProperty("os.name");
            String java = "Java: " + System.getProperty("java.version");

            Component copyDebug = Component.text("(Click to Copy)")
                    .hoverEvent(HoverEvent.showText(Text.of("&e~copy~").comp()))
                    .color(TextColor.color(170, 170, 170))
                    .clickEvent(ClickEvent.copyToClipboard(version + "\n" + sys + "\n" + java + "\n" + saVersion + "\n" + plugins + "\n"));

            Component debugPart = Text.of("&8Debug Data: ").comp();
            debugPart = debugPart.append(copyDebug);
            Text.of("&b&m                         ").send(sender);
            Text.of(debugPart).send(sender);
            Text.of("&a" + sys).send(sender);
            Text.of("&c" + java).send(sender);
            Text.of("&e" + version).send(sender);
            Text.of("&f" + saVersion).send(sender);
            Text.of(finalPluginPart).send(sender);
            Text.of("&b&m                         ").send(sender);
            return true;
        }

        return false;
    }

    @Override
    public @NotNull List<String> tab(@NotNull TabCompleteContext tab) {
        var sender = tab.sender();
        var args = tab.args();

        if (!sender.hasPermission("sap.admin")) {
            return ICommand.super.tab(tab);
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

        return ICommand.super.tab(tab);
    }
}
