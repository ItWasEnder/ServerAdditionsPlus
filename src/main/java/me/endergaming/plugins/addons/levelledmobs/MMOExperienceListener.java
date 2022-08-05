package me.endergaming.plugins.addons.levelledmobs;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.mcmmo.MCMMOManager;
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
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        if (entity instanceof Player) {
            return;
        }
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }
        int level = this.lmManager.getLevel(entity);
        double damage = event.getFinalDamage();
        int bonus = NumberConversions.round((level * 1.15) + damage);
        PrimarySkillType skill = this.mmoManager.parseItemSkill(damager.getInventory().getItemInMainHand());
        if (skill == PrimarySkillType.UNARMED && damager.getInventory().getItemInMainHand().getType() != Material.AIR) {
            return;
        }
        // Give bonus exp
        ServerAdditionsPlus.debug("- - - - - - - - - - -");
        ServerAdditionsPlus.debug("Player: " + damager.getName());
        ServerAdditionsPlus.debug("Giving " + bonus + " bonus XP for skill " + skill.name());
        ServerAdditionsPlus.debug("PreBonus: " + this.mmoManager.getSkillXP(damager, skill) + "/" + this.mmoManager.getNeededXP(damager, skill));
        this.mmoManager.giveXP(damager, skill, bonus);
        ServerAdditionsPlus.debug("PostBonus: " + this.mmoManager.getSkillXP(damager, skill) + "/" + this.mmoManager.getNeededXP(damager, skill));
        ServerAdditionsPlus.debug("- - - - - - - - - - -");
    }
}
