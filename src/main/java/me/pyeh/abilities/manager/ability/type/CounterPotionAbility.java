package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.utils.ColorUtils;
import me.pyeh.abilities.utils.CooldownUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Getter
public class CounterPotionAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();

    @EventHandler
    public void onAttackAbility(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();
            ItemStack itemStack = attacker.getItemInHand();
            AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("COUNTER_POTION");
            if (!IAbilities.getInstance().getAbilitiesManager().isAbility(itemStack, abilityData)) return;

            if (this.cooldown.check(attacker)) {
                event.setCancelled(true);
                attacker.updateInventory();
                IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(attacker, this.cooldown.getString(attacker), "COUNTER_POTION");
                return;
            }

        this.activate(attacker, damaged);

        }
    }

    private void activate(Player owner, Player target) {
        String count = this.getCounter(target) + "";
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("COUNTER_POTION");
        IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByPlayer(owner, target, "COUNTER_POTION");
        IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByTarget(owner, target, "COUNTER_POTION");
        IAbilities.getInstance().getLanguageFile().getStringList("COUNTER_POTION_ABILITY.counter_potions").
                forEach( lines -> owner.sendMessage(ColorUtils.translate(lines.replace("<count>", count).replace("<damaged>", target.getName()))));

        this.cooldown.apply(owner,abilityData.getCooldown() * 1000L, "COUNTER_POTION");
    }

    private int getCounter(Player player) {
        int i = 0;
        PlayerInventory playerInventory = player.getInventory();
        for(ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack != null) {
                if (itemStack.getType() == Material.POTION && itemStack.getDurability() == 16421) {
                    i += itemStack.getAmount();
                }
            }
        }
        return i;
    }





}
