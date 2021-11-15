package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.utils.CooldownUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class DistortionAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();
    private final HashMap<UUID, Integer> hits = new HashMap<>();


    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility("DISTORTION");
            if (!IAbilities.getInstance().getAbilitiesManager().isAbility(attacker.getItemInHand(), ability)) return;

            if (this.cooldown.check(attacker)) {
                event.setCancelled(true);
                attacker.updateInventory();
                IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(attacker, this.cooldown.getString(attacker), "DISTORTION");
                return;
            }

            this.activate(attacker, damaged);
            this.cooldown.apply(attacker, ability.getCooldown() * 1000L, "DISTORTION");
        }
    }


    private void activate(Player attacker, Player target) {
        PlayerInventory inventory = target.getInventory();

        if (!this.hits.containsKey(attacker.getUniqueId())) {
            this.hits.put(attacker.getUniqueId(), 0);
        } else {
            this.hits.put(attacker.getUniqueId(), this.hits.get(attacker.getUniqueId()) + 1);
        }

        if (this.hits.get(attacker.getUniqueId()) == 3) {
            this.hits.put(attacker.getUniqueId(), 0);

            IAbilities.getInstance().getAbilitiesManager().getCountItem(attacker);
            IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByPlayer(attacker, target, "DISTORTION");
            IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByTarget(attacker, target, "DISTORTION");
            inventory.setItem(0, inventory.getItem(3));
            inventory.setItem(1, inventory.getItem(2));
            inventory.setItem(2, inventory.getItem(5));
            inventory.setItem(3, inventory.getItem(7));
            inventory.setItem(4, inventory.getItem(8));
            inventory.setItem(5, inventory.getItem(0));
            inventory.setItem(6, inventory.getItem(1));
            inventory.setItem(8, inventory.getItem(4));
            inventory.setItem(9, inventory.getItem(6));
        }
    }


}
