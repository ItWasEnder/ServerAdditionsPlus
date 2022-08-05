package me.endergaming.plugins.commands;

import me.endergaming.enderlibs.command.SubCommand;
import me.endergaming.enderlibs.text.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DebugCommand extends SubCommand {
    public DebugCommand(@NotNull JavaPlugin plugin, String command) {
        super(plugin, command);
    }

    @Override
    public void run(CommandSender sender, Command cmd, String label, String[] args) {
        PluginManager pm = Bukkit.getPluginManager();
        List<String> list = Arrays.stream(pm.getPlugins()).map(Plugin::getName).collect(Collectors.toList());
        StringBuilder plugins = new StringBuilder("Plugins: ");

        for (int i = 0; i < list.size(); i++) {
            plugins.append(list.get(i)).append(" v").append(pm.getPlugin(list.get(i)).getDescription().getVersion());
            if (i != list.size() - 1) {
                plugins.append(", ");
            }
        }

        TextComponent pluginPart = new TextComponent();
        pluginPart.addExtra("(Click to Copy)");
        pluginPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(MessageUtils.colorize("&e~copy~"))));
        pluginPart.setColor(ChatColor.GRAY);
        pluginPart.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, plugins.toString()));

        TextComponent finalPluginPart = new TextComponent(TextComponent.fromLegacyText(MessageUtils.colorize("&dPlugins: " + pm.getPlugins().length + " ")));
        finalPluginPart.addExtra(pluginPart);

        String version = "Version: " + Bukkit.getVersion();
        String saVersion = "SA Version: " + this.plugin.getDescription().getVersion();
        String sys = "System: " + System.getProperty("os.name");
        String java = "Java: " + System.getProperty("java.version");

        TextComponent copyDebug = new TextComponent();
        copyDebug.addExtra("(Click to Copy)");
        copyDebug.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(MessageUtils.colorize("&e~copy~"))));
        copyDebug.setColor(ChatColor.GRAY);
        copyDebug.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, version + "\n" + sys + "\n" + java + "\n" + saVersion + "\n" + plugins + "\n"));

        TextComponent debugPart = new TextComponent(TextComponent.fromLegacyText(MessageUtils.colorize("&8Debug Data: ")));
        debugPart.addExtra(copyDebug);
        MessageUtils.send(sender, "&b&m                         ");
        MessageUtils.send(sender, debugPart);
        MessageUtils.send(sender, "&a" + sys);
        MessageUtils.send(sender, "&c" + java);
        MessageUtils.send(sender, "&e" + version);
        MessageUtils.send(sender, "&f" + saVersion);
        MessageUtils.send(sender, finalPluginPart);
        MessageUtils.send(sender, "&b&m                         ");
    }
}
