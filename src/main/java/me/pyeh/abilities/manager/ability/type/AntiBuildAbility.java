package me.pyeh.abilities.manager.ability.type;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.utils.CooldownUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class AntiBuildAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();
    private final CooldownUtils interactive = new CooldownUtils();
    private final Table<UUID, UUID, Integer> playerHits = HashBasedTable.create();

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            ItemStack item = attacker.getItemInHand();
            AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility("ANTI_BUILD");
            if (!IAbilities.getInstance().getAbilitiesManager().isAbility(item, ability)) return;

            if (this.cooldown.check(attacker)) {
                event.setCancelled(true);
                attacker.updateInventory();
                IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(attacker, this.cooldown.getString(attacker), "ANTI_BUILD");
                return;
            }
            this.activate(attacker, damaged);
            this.cooldown.apply(attacker, ability.getCooldown() + 1000L, "ANTI_BUILD");
        }
    }

    private void activate(Player player, Player target) {
        if (this.playerHits.contains(player.getUniqueId(), target.getUniqueId())) {
            int hitsNeeded = this.playerHits.get(player.getUniqueId(), target.getUniqueId()) - 1;
            if (hitsNeeded == 0) {
                this.playerHits.remove(player.getUniqueId(), target.getUniqueId());
                this.interactive.apply(target, 15 * 1000L, "ANTI_BUILD");
                IAbilities.getInstance().getAbilitiesManager().getCountItem(player);
                IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByPlayer(player, target, "ANTI_BUILD");
                IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByTarget(player, target, "ANTI_BUILD");
                return;
            }
            this.playerHits.put(player.getUniqueId(), target.getUniqueId(), hitsNeeded);
        }
        this.playerHits.put(player.getUniqueId(), target.getUniqueId(), 2);
    }

    @EventHandler
    public void onPlaceInTack(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.interactive.check(player)) {
            event.setCancelled(true);
            IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(player, "not_cannot_build", "ANTI_BUILD");
        }
    }

    @EventHandler
    public void onBreakInTack(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.interactive.check(player)) {
            event.setCancelled(true);
            IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(player, "not_cannot_build", "ANTI_BUILD");
        }
    }

    @EventHandler
    public void onInteractiveInTack(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.interactive.check(player)) {
            Material material = event.getClickedBlock().getType();
            if (material == Material.WOOD_DOOR || material == Material.TRAP_DOOR
                    || material == Material.WOODEN_DOOR || material == Material.FENCE_GATE) {
                event.setCancelled(true);
                IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(player, "not_cannot_build", "ANTI_BUILD");
            }
        }
    }

}
