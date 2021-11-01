package me.endergaming.solarisadditions.compat.levelledmobs;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import me.endergaming.solarisadditions.SolarisAdditions;
import me.endergaming.solarisadditions.compat.mcmmo.MCMMOManager;
import me.lokka30.levelledmobs.events.MobPreLevelEvent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.NumberConversions;

public class MMOExperienceListener implements Listener {
    private final MCMMOManager mmoManager;
    private final LMManager lmManager;

    public MMOExperienceListener(MCMMOManager mmoManager, LMManager lmManager) {
        this.mmoManager = mmoManager;
        this.lmManager = lmManager;
    }

    // TODO: Implement Enchantment Checks to check damage or to add enchant that increases XP from mobs

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExperience(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity instanceof Player) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        int level = lmManager.getLevel(entity);
        double damage = event.getFinalDamage();
        int bonus = NumberConversions.round((level * 1.15) + damage);
        PrimarySkillType skill = mmoManager.parseItemSkill(damager.getInventory().getItemInMainHand());
        if (skill == PrimarySkillType.UNARMED && damager.getInventory().getItemInMainHand().getType() != Material.AIR)
            return;
        // Give bonus exp
        SolarisAdditions.debug("- - - - - - - - - - -");
        SolarisAdditions.debug("Player: " + damager.getName());
        SolarisAdditions.debug("Giving " + bonus + " bonus XP for skill " + skill.name());
        SolarisAdditions.debug("PreBonus: " + mmoManager.getSkillXP(damager, skill) + "/" + mmoManager.getNeededXP(damager, skill));
        mmoManager.giveXP(damager, skill, bonus);
        SolarisAdditions.debug("PostBonus: " + mmoManager.getSkillXP(damager, skill) + "/" + mmoManager.getNeededXP(damager, skill));
        SolarisAdditions.debug("- - - - - - - - - - -");
    }
}
