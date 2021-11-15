package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.utils.CooldownUtils;
import me.pyeh.abilities.utils.TaskUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class BrewingRefillAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();

    @EventHandler
    public void onPlaceAbility(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility("BREWING_REFILL");
        Block block = event.getBlockPlaced();
        if (!IAbilities.getInstance().getAbilitiesManager().isAbility(item, ability)) return;

        if (this.cooldown.check(player)) {
            event.setCancelled(true);
            player.updateInventory();
            IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(player, this.cooldown.getString(player), "BREWING_REFILL");
            return;
        }
        event.setCancelled(true);
        this.cooldown.apply(player, ability.getCooldown() * 1000L, "BREWING_REFILL");
        TaskUtils.aSyncDelayed(() -> this.activate(block.getLocation()), 40);
        IAbilities.getInstance().getAbilitiesManager().getCountItem(player);
        IAbilities.getInstance().getAbilitiesManager().sendMessageActivated(player,"BREWING_REFILL");
    }

    private void activate(Location location) {
        Block two, three, four;

        location.getBlock().setType(Material.CHEST);
        two = location.clone().add(0,0,1).getBlock();
        three = location.clone().add(0,1,0).getBlock();
        four = location.clone().add(0,1,1).getBlock();

        two.setType(Material.CHEST);
        three.setType(Material.CHEST);
        four.setType(Material.CHEST);

        if(location.getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) location.getBlock().getState();
            Inventory inventory = chest.getInventory();
            this.addedPotion(inventory);
        }
        if(two.getState() instanceof Chest) {
            Chest chest = (Chest) two.getState();
            Inventory inventory = chest.getInventory();
            this.addedPotion(inventory);
        }
        if(three.getState() instanceof Chest) {
            Chest chest = (Chest) three.getState();
            Inventory inventory = chest.getInventory();
            this.addedPotion(inventory);
        }
        if(four.getState() instanceof Chest) {
            Chest chest = (Chest) four.getState();
            Inventory inventory = chest.getInventory();
            this.addedPotion(inventory);
        }
    }

    private void addedPotion(Inventory inventory) {
        ItemStack itemStack = new ItemStack(Material.POTION, 1 , (short) 16421);
        for (int i = 0; i < inventory.getSize(); ++i) {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, itemStack);
            }
        }
    }






}
