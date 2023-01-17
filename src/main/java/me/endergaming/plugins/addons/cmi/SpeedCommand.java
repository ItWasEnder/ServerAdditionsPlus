package me.endergaming.plugins.addons.cmi;

import me.endergaming.enderlibs.command.MainCommand;
import me.endergaming.enderlibs.text.MessageUtils;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.backend.AddonManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static me.endergaming.plugins.commands.CommandRegistry.prefix;

public class SpeedCommand extends MainCommand {
    public SpeedCommand(@NotNull JavaPlugin plugin, String command) {
        super(plugin, command);
    }

    @Override
    public void run(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        Addon addon = AddonManager.getManagerByName("CMIManager");
        if (addon == null || !addon.isRegistered()) {
            MessageUtils.send(player, prefix.replace("&a", "&c") + "&cModule is missing or not enabled");
            return;
        }

        if (args.length < 1) {
            MessageUtils.send(player, this);
            return;
        }

        String runCMD;

        if (args.length == 1) {
            if (!this.validateDouble(args[0])) {
                MessageUtils.send(player, this);
                return;
            }
            if (player.isFlying()) {
                runCMD = "cmi flyspeed " + args[0];
            } else {
                runCMD = "cmi walkspeed " + args[0];
            }
        } else {
            Player bukkitPlayer = Bukkit.getPlayer(args[0]);
            if (bukkitPlayer == null) {
                MessageUtils.send(player, prefix.replace("&a", "&c") + "&cInvalid Player.");
                return;
            }
            String speed = args[1];
            String option = args.length >= 3 ? args[2] : "";
            if (!this.validateDouble(speed)) {
                MessageUtils.send(player, this);
                return;
            }
            if (player.isFlying()) {
                runCMD = String.format("cmi flyspeed %s %s %s", bukkitPlayer.getName(), speed, option);
            } else {
                runCMD = String.format("cmi walkspeed %s %s %s", bukkitPlayer.getName(), speed, option);
            }
        }

        player.performCommand(runCMD);
    }

    public boolean validateDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
