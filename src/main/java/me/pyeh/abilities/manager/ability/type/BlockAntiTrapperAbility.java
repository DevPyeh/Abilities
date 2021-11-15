package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.utils.CooldownUtils;
import me.pyeh.abilities.utils.TaskUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class BlockAntiTrapperAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();
    private final CooldownUtils interactive = new CooldownUtils();
    private boolean checking = false;

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility("BLOCK_ANTI_TRAPPER");
        Block block = event.getBlockPlaced();
        if (!IAbilities.getInstance().getAbilitiesManager().isAbility(item, ability)) return;

        if (this.cooldown.check(player)) {
            event.setCancelled(true);
            player.updateInventory();
            IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(player, this.cooldown.getString(player), "BLOCK_ANTI_TRAPPER");
            return;
        }
        this.activate(player);
        TaskUtils.aSyncDelayed((() -> this.activatedTask(block, player)), 300L);
    }

    private void activate(Player player) {
        List<Entity> near = player.getNearbyEntities(10, 10, 10);
        AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility("BLOCK_ANTI_TRAPPER");
        IAbilities.getInstance().getAbilitiesManager().sendMessageActivated(player,"BLOCK_ANTI_TRAPPER");
        this.cooldown.apply(player, ability.getCooldown() * 1000L, "BLOCK_ANTI_TRAPPER");
        this.checking = true;
        if (near instanceof Player) {
            near.forEach(players -> {
                IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByTarget(player,(Player) players, "TELEPORT");
                this.interactive.apply((Player) players, 15 * 1000L, "BUILDING");
            });
        }
    }

    @EventHandler
    public void onPlaceInTack(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.interactive.check(player)) {
            event.setCancelled(true);
            IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(player, "not_cannot_build", "BLOCK_ANTI_TRAPPER");
        }
    }

    @EventHandler
    public void onBreakInTack(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.interactive.check(player)) {
            event.setCancelled(true);
            IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(player, "not_cannot_build", "BLOCK_ANTI_TRAPPER");
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
                IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(player, "not_cannot_build", "BLOCK_ANTI_TRAPPER");
            }
        }
    }

    @EventHandler
    public void onBreakAbility(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("BLOCK_ANTI_TRAPPER");
        Material abilityItem = Material.getMaterial(abilityData.getItemData());
        if (material == abilityItem) {
            block.getDrops().clear();
            IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(player, "break_ability_in_world", "BLOCK_ANTI_TRAPPER");
            this.checking = false;
            player.getNearbyEntities(10, 10, 10).forEach(players -> {
                IAbilities.getInstance().getAbilitiesManager().sendCustomMessage((Player) players, "break_ability_in_world", "BLOCK_ANTI_TRAPPER");
                if (this.interactive.check((Player) players) || this.interactive.check((Player) players)) {
                    this.interactive.remove(player, "BUILDING");
                    this.interactive.remove((Player) players, "BUILDING");
                }
            });
        }
    }

    public void activatedTask(Block block, Player player) {
        Material material = block.getType();
        if (material != Material.AIR) {
            block.setType(Material.AIR);
            this.checking = false;
            IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(player, "break_after_ability_in_world", "BLOCK_ANTI_TRAPPER");
        }
    }

}
