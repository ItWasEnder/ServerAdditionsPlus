package me.endergaming.solarisadditions.compat.mcmmo;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.McMMOPlayerNotFoundException;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.backend.Manager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

public class MCMMOManager extends Manager {
    private static final int MAX_LEVEL = 1000;

    public MCMMOManager(@NotNull final SolarisAdditions instance, @NotNull String reqPlugin, @NotNull String... reqManagers) {
        super(instance, "mcmmo", reqPlugin, reqManagers);
    }

    public void giveXP(Player player, PrimarySkillType skill, int amount) {
        if (!registered) return;
        ExperienceAPI.addXP(player, skill.toString(), amount, "PVE");
    }

    public String colorizeLevel(int level) {
        StringBuilder toReturn = new StringBuilder();

        if (level < 10) toReturn.append("&a");
        else if (level < 25) toReturn.append("&b");
        else if (level < 40) toReturn.append("&e");
        else if (level < 55) toReturn.append("&c&l");
        else toReturn.append("&d&l");

        return toReturn.toString() + level;
    }

    public String getFormattedWeightedLevel(Player player) {
        int level = calculateWeightedLevel(getPlayer(player).getProfile());
        return colorizeLevel(level);
    }

    public int getCleanWeightedLevel(Player player) {
        return calculateWeightedLevel(getPlayer(player).getProfile());
    }

    public int getUnmodifiedLevel(Player player) {
        return getPlayer(player).getPowerLevel();
    }

    public int getLevel(PlayerProfile mcMMOProfile, PrimarySkillType skillType) {
        int skillLevel = mcMMOProfile.getSkillLevel(skillType);
        return Math.min(skillLevel, MAX_LEVEL);
    }

    @Deprecated
    private McMMOPlayer getPlayer(Player player) throws McMMOPlayerNotFoundException {
        if (!UserManager.hasPlayerDataKey(player)) {
            throw new McMMOPlayerNotFoundException(player);
        } else {
            return UserManager.getPlayer(player);
        }
    }

    public boolean isLoaded(Player player) {
        McMMOPlayer p = UserManager.getPlayer(player);
        if (p == null) return false;
        PlayerProfile profile = p.getProfile();
        return profile != null && profile.isLoaded();
    }

    private int calculateWeightedLevel(PlayerProfile mcMMOProfile) {
        int swords = getLevel(mcMMOProfile, PrimarySkillType.SWORDS);
        int axes = getLevel(mcMMOProfile, PrimarySkillType.AXES);
        int unarmed = getLevel(mcMMOProfile, PrimarySkillType.UNARMED);
        int archery = getLevel(mcMMOProfile, PrimarySkillType.ARCHERY);
        int taming = getLevel(mcMMOProfile, PrimarySkillType.TAMING);
        int acrobatics = getLevel(mcMMOProfile, PrimarySkillType.ACROBATICS);

        double sum = unarmed + swords + axes + archery + 0.25 * acrobatics + 0.25 * taming;
        return NumberConversions.round(sum / 45);
    }

    public PrimarySkillType parseItemSkill(ItemStack item) {
        if (item.getType().toString().toLowerCase().contains("sword")) {
            return PrimarySkillType.SWORDS;
        } else if (item.getType().toString().toLowerCase().contains("axe")) {
            return PrimarySkillType.AXES;
        } else if (item.getType() == Material.BOW || item.getType() == Material.CROSSBOW) {
            return PrimarySkillType.ARCHERY;
        } else {
            return PrimarySkillType.UNARMED;
        }
    }

    public int getSkillXP(Player player, PrimarySkillType skill) {
        return getPlayer(player).getSkillXpLevel(skill);
    }

    public int getNeededXP(Player player, PrimarySkillType skill) {
        return getPlayer(player).getXpToLevel(skill);
    }
}
