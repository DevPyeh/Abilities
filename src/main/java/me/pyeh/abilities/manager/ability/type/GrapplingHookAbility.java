package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.utils.CooldownUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Getter
public class GrapplingHookAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();

    @EventHandler
    public void onFishAbility(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("GRAPPLING_HOOK");
        if (!IAbilities.getInstance().getAbilitiesManager().isAbility(itemStack, abilityData)) return;

        if(this.cooldown.check(player)) {
            event.setCancelled(true);
            player.updateInventory();
            IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(player, this.cooldown.getString(player), "GRAPPLING_HOOK");
            return;
        }

        Location hookLocation = event.getHook().getLocation();
        Location playerLocation = player.getLocation();
        double hookX = (int) hookLocation.getX();
        double hookY = (int) hookLocation.getY();
        double hookZ = (int) hookLocation.getZ();
        Material inType = hookLocation.getWorld().getBlockAt(hookLocation).getType();

        if (inType == Material.AIR || inType == Material.WATER || inType == Material.LAVA) {
            Material belowType = hookLocation.getWorld().getBlockAt((int) hookX, (int) (hookY - 0.1), (int) hookZ).getType();
            if (belowType == Material.AIR || inType == Material.WATER || inType == Material.LAVA) return;
        }

        playerLocation.setY(playerLocation.getY() + 0.5);
        Vector diff = hookLocation.toVector().subtract(playerLocation.toVector());
        Vector vel = new Vector();
        double d = hookLocation.distance(playerLocation);
        vel.setX((1.0 + 0.07 * d) * diff.getX() / d);
        vel.setY((1.0 + 0.03 * d) * diff.getY() / d + 0.04 * d);
        vel.setZ((1.0 + 0.07 * d) * diff.getZ() / d);
        player.setVelocity(vel);

        this.cooldown.apply(player, abilityData.getCooldown() * 1000L, "GRAPPLING_HOOK");
        IAbilities.getInstance().getAbilitiesManager().sendMessageActivated(player, "GRAPPLING_HOOK");
    }

}
