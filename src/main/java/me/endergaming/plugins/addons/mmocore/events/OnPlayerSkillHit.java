package me.endergaming.plugins.addons.mmocore.events;

import com.marcusslover.plus.lib.events.EventListener;
import com.marcusslover.plus.lib.events.annotations.Event;
import com.marcusslover.plus.lib.task.Task;
import com.marcusslover.plus.lib.text.Text;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageType;
import me.endergaming.plugins.ServerAdditionsPlus;
import me.endergaming.plugins.addons.mmocore.MMOManager;
import me.endergaming.plugins.misc.Globals;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.kyori.adventure.util.Ticks;
import org.bukkit.ChatColor;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnPlayerSkillHit implements EventListener {
    Map<UUID, Long> experienceMap = Collections.synchronizedMap(new HashMap<>());
    Map<UUID, Long> lastUpdated = Collections.synchronizedMap(new HashMap<>());
    Map<UUID, Task> taskMap = Collections.synchronizedMap(new HashMap<>());

    @Event
    private void onPlayerHit(PlayerAttackEvent event) {
        if (!Globals.Addons.MMOManager.get().isRegistered()) {
            return;
        }

        var damageData = event.getAttack().getDamage();
        var skillDamage = damageData.getDamage(DamageType.MAGIC);
        var player = event.getAttacker().getPlayer();
        var playerData = PlayerData.get(event.getAttacker().getPlayer().getUniqueId());
        var entity = event.getAttack().getTarget();
        var modifier = 1.0;

        MMOManager.log(damageData.toString());

        /* Check if mob was artificially spawned */
        if (entity.getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }

        /* Apply modifiers */
        if (!(entity instanceof Monster) && !(entity instanceof Player)) {
            modifier = MMOManager.PASSIVE_MOB_MODIFIER;
        }

        var exp = (int) Math.max(1, Math.floor((skillDamage * MMOManager.SKILL_XP_MODIFIER) * modifier));

        /* Able to check if damage is a skill*/
        if (event.getAttack().getDamage().hasType(DamageType.SKILL)) {
            MMOManager.log(ChatColor.GREEN + player.getName() + " EXP Gained: " + ChatColor.GOLD + exp);

            playerData.giveExperience(exp, EXPSource.OTHER);

            this.experienceMap.put(player.getUniqueId(), (long) exp + this.experienceMap.getOrDefault(player.getUniqueId(), 0L));
            this.lastUpdated.put(player.getUniqueId(), System.currentTimeMillis());

            MMOManager.displayIndicator(
                    entity,
                    ChatColor.translateAlternateColorCodes('&', "&a🧪&f") + exp,
                    MMOManager.getIndicatorDirection(player));
        }

        final long time = System.currentTimeMillis() - this.lastUpdated.getOrDefault(player.getUniqueId(), 0L);

        if (time < MMOManager.TIME_TO_KILL && this.taskMap.containsKey(player.getUniqueId())) {
            this.taskMap.get(player.getUniqueId()).cancel();
        }

        this.taskMap.put(player.getUniqueId(), Task.syncDelayed(ServerAdditionsPlus.getPlugin(), () -> {
            this.gainedExperience(player, this.experienceMap.remove(player.getUniqueId()));
        }, Ticks.TICKS_PER_SECOND * MMOManager.TIME_TO_KILL));
    }

    private void gainedExperience(Player key, Long value) {
        if (value == null) {
            return;
        }

        Text.of(" \n&7You gained &a" + value + " &7experience! \n ").send(key);
    }
}
