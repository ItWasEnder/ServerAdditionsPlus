package me.endergaming.plugins.addons.levelledmobs;

import com.archyx.aureliumskills.skills.Skills;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.skills.SkillsManager;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.NumberConversions;

import java.text.DecimalFormat;

public class SkillExperienceListener implements Listener {

    DecimalFormat df = new DecimalFormat("#.#");
    private final SkillsManager skillsManager;
    private final LMManager lmManager;

    public SkillExperienceListener(SkillsManager skillsManager, LMManager lmManager) {
        this.skillsManager = skillsManager;
        this.lmManager = lmManager;
    }

    // TODO: Implement Enchantment Checks to check damage or to add enchant that increases XP from mobs

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExperience(EntityDamageByEntityEvent event) {
        if (!this.lmManager.isRegistered()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (entity instanceof Player) {
            return;
        }

        Player damager;

        boolean isProjectile = false;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else {
            if (event.getDamager() instanceof Projectile projectile) {
                isProjectile = true;

                if (projectile.getShooter() instanceof Player player) {
                    damager = player;
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        if (entity.getHealth() - event.getFinalDamage() > 0) {
            return;
        }

        int level = this.lmManager.getLevel(entity);
        double damage = event.getFinalDamage();
        double bonus = (level * 0.75) + (damage * 0.35);

        bonus = Double.parseDouble(this.df.format(bonus));

        Skills skill = isProjectile ? Skills.ARCHERY : Skills.FIGHTING;

        if (isProjectile && !this.hasBow(damager.getInventory())) {
            return;
        }

        // Give bonus exp
        ServerAdditionsPlus.debug("- - - - - - - - - - -");
        ServerAdditionsPlus.debug("Player: " + damager.getName());
        ServerAdditionsPlus.debug("Giving " + bonus + " bonus XP for skill " + skill.name());
        ServerAdditionsPlus.debug("PreBonus: " + this.skillsManager.getSkillXP(damager, skill) + "/" + this.skillsManager.getNeededXP(damager, skill));
        this.skillsManager.giveXP(damager, skill, bonus);
        ServerAdditionsPlus.debug("PostBonus: " + this.skillsManager.getSkillXP(damager, skill) + "/" + this.skillsManager.getNeededXP(damager, skill));
        ServerAdditionsPlus.debug("- - - - - - - - - - -");
    }

    public boolean hasBow(PlayerInventory inv) {
        var main = inv.getItemInMainHand();
        var off = inv.getItemInOffHand();

        boolean hasBow = false;

        if (main.getType() == Material.BOW || off.getType() == Material.BOW) {
            hasBow = true;
        } else if (main.getType() == Material.CROSSBOW || off.getType() == Material.CROSSBOW) {
            hasBow = true;
        }

        return hasBow;
    }
}
