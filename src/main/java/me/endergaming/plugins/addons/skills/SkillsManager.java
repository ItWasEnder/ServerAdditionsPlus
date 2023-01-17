package me.endergaming.plugins.addons.skills;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skills;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.backend.Addon;
import me.endergaming.plugins.misc.Globals;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SkillsManager extends Addon {
    AureliumSkills aureliumSkills;

    public SkillsManager(@NotNull final ServerAdditionsPlus instance, @NotNull Globals.Plugins reqPlugin, @NotNull String... reqManagers) {
        super(instance, "skills", reqPlugin, reqManagers);
    }

    public void giveXP(Player player, Skills skill, double amount) {
        if (this.isRegistered()) {
            this.aureliumSkills.getLeveler().addXp(player, skill, amount);
        }
    }

    public String colorizeLevel(int level) {
        StringBuilder toReturn = new StringBuilder();

        if (level < 10) {
            toReturn.append("&a");
        } else if (level < 25) {
            toReturn.append("&b");
        } else if (level < 40) {
            toReturn.append("&e");
        } else if (level < 55) {
            toReturn.append("&c&l");
        } else {
            toReturn.append("&d&l");
        }

        return toReturn.append(level).toString();
    }

    public String getFormattedWeightedLevel(Player player) {
        int level = this.calculateWeightedLevel(player);
        return this.colorizeLevel(level);
    }

    public int getCleanWeightedLevel(Player player) {
        return this.calculateWeightedLevel(player);
    }

    public int getUnmodifiedLevel(Player player) {
        var opt = this.getPlayer(player);

        return opt.map(PlayerData::getPowerLevel).orElse(0);
    }

    public int getLevel(Player player, Skills skill) {
        var data = this.getPlayer(player);

        return data.map(playerData -> playerData.getSkillLevel(skill)).orElse(0);
    }

    private Optional<PlayerData> getPlayer(Player player) throws NullPointerException {
        var data = this.aureliumSkills.getPlayerManager().getPlayerData(player);

        return Optional.ofNullable(data);
    }

    public boolean hasData(Player player) {
        return this.aureliumSkills.getPlayerManager().hasPlayerData(player);
    }

    private int calculateWeightedLevel(Player player) {
        var opt = this.getPlayer(player);

        if (opt.isEmpty()) {
            return 0;
        }

        var data = opt.get();

        data.getSkillLevel(Skills.AGILITY);
        int fighting = data.getSkillLevel(Skills.FIGHTING);
        int archery = data.getSkillLevel(Skills.ARCHERY);
        int defense = data.getSkillLevel(Skills.DEFENSE);
        int endurance = data.getSkillLevel(Skills.ENDURANCE);
        int magic = data.getSkillLevel(Skills.SORCERY);

        double sum = defense + fighting + archery + 0.25 * endurance + 0.25 * magic;
        return NumberConversions.round(sum / 45);
    }

    public Skills parseItemSkill(ItemStack item) {
        if (item.getType() == Material.BOW || item.getType() == Material.CROSSBOW) {
            return Skills.ARCHERY;
        } else {
            return Skills.FIGHTING;
        }
    }

    public double getSkillXP(Player player, Skills skill) {
        return this.getPlayer(player).map(playerData -> playerData.getSkillXp(skill)).orElse(0.0);
    }

    public double getNeededXP(Player player, Skills skill) {
        return this.aureliumSkills.getLeveler().getXpRequirements().getXpRequired(skill, this.getLevel(player, skill) + 1);
    }

    @Override
    public void onEnable() {
        this.aureliumSkills = (AureliumSkills) Bukkit.getPluginManager().getPlugin("AureliumSkills");
    }

    @Override
    public void onDisable() {

    }
}
