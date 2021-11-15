package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.utils.CooldownUtils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

@Getter
public class TeleportAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();

    @EventHandler
    public void onLaunchEvent(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Projectile entity = event.getEntity();
        Player player = (Player) entity.getShooter();
        ItemStack item = player.getItemInHand();
        AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility("TELEPORT");
        if (!IAbilities.getInstance().getAbilitiesManager().isAbility(item, ability)) return;

        if (this.cooldown.check(player)) {
            event.setCancelled(true);
            player.updateInventory();
            IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(player, this.cooldown.getString(player), "TELEPORT");
            return;
        }

        if (entity instanceof Snowball) {
            Snowball snowball = (Snowball) entity;
            snowball.setMetadata("teleport_snow", new FixedMetadataValue(IAbilities.getInstance(), player.getUniqueId()));
        } else if (entity instanceof Egg) {
            Egg egg = (Egg) entity;
            egg.setMetadata("teleport_egg", new FixedMetadataValue(IAbilities.getInstance(), player.getUniqueId()));
        }

        this.cooldown.apply(player, ability.getCooldown() * 1000L, "TELEPORT");
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof Snowball && entity instanceof Player) {
            Player damaged = (Player) entity;
            Snowball snowball = (Snowball) damager;

            if (!snowball.hasMetadata("teleport_snow")) return;

            if (snowball.getShooter() instanceof Player) {
                Player shooter = (Player) snowball.getShooter();

                if (shooter.getLocation().distance(damaged.getLocation()) > 8) {
                    IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(shooter, "radius_access", "TELEPORT");
                    return;
                }
                shooter.teleport(damaged.getLocation());
                damaged.teleport(shooter.getLocation());
                IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByPlayer(shooter, damaged, "TELEPORT");
                IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByTarget(shooter, damaged, "TELEPORT");
                snowball.removeMetadata("teleport_snow", IAbilities.getInstance());
            }
        } else if (damager instanceof Egg && entity instanceof Player) {
            Player damaged = (Player) entity;
            Egg egg = (Egg) damager;

            if (!egg.hasMetadata("teleport_egg")) return;

            if (egg.getShooter() instanceof Player) {
                Player shooter = (Player) egg.getShooter();

                if (shooter.getLocation().distance(damaged.getLocation()) > 8) {
                    IAbilities.getInstance().getAbilitiesManager().sendCustomMessage(shooter, "radius_access", "TELEPORT");
                    return;
                }
                shooter.teleport(damaged.getLocation());
                damaged.teleport(shooter.getLocation());
                IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByPlayer(shooter, damaged, "TELEPORT");
                IAbilities.getInstance().getAbilitiesManager().sendMessageActivatedByTarget(shooter, damaged, "TELEPORT");
                egg.removeMetadata("teleport_egg", IAbilities.getInstance());
            }
        }
    }
}
