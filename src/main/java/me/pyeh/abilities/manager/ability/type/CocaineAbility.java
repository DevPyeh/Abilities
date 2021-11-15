package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.utils.CooldownUtils;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@Getter
public class CocaineAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();

    @EventHandler
    public void onInteractiveAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("COCAINE");
        if (!IAbilities.getInstance().getAbilitiesManager().isAbility(item, abilityData)) return;

        if (this.cooldown.check(player)) {
            event.setCancelled(true);
            player.updateInventory();
            IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(player, this.cooldown.getString(player), "COCAINE");
            return;
        }
        activate(player);
        IAbilities.getInstance().getAbilitiesManager().getCountItem(player);
    }


    private void activate(Player player){
        List<String> effects = IAbilities.getInstance().getAbilitiesManager().getEffects("COCAINE");
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("COCAINE");
        this.cooldown.apply(player, abilityData.getCooldown() * 1000L, "COCAINE");
        IAbilities.getInstance().getAbilitiesManager().sendMessageActivated(player, "COCAINE");
        if (effects.size() != 0) {
            for (String str : effects) {
                PotionEffectType effect = PotionEffectType.getByName(str.split(":")[0]);
                int duration = (Ints.tryParse(str.split(":")[1]) + 1) * 20;
                int level = Ints.tryParse(str.split(":")[2]) - 1;
                player.addPotionEffect(new PotionEffect(effect, duration, level));
            }
        }
    }

}
