package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.utils.CooldownUtils;
import me.pyeh.abilities.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FakePearlAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();
    private final Map<UUID, Boolean> check = new HashMap<>();

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            EnderPearl enderPearl = (EnderPearl) projectile;
            ProjectileSource source = enderPearl.getShooter();
            if (source instanceof Player) {
                Player player = (Player) source;
                ItemStack item = player.getItemInHand();
                AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("FAKE_PEARL");

                if (!IAbilities.getInstance().getAbilitiesManager().isAbility(item, abilityData)) return;
                if (this.cooldown.check(player)) {
                    event.setCancelled(true);
                    player.updateInventory();
                    IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(player, this.cooldown.getString(player), "FAKE_PEARL");
                    return;
                }

                this.check.put(player.getUniqueId(), true);
                this.cooldown.apply(player, abilityData.getCooldown() * 1000L, "FAKE_PEARL");
                IAbilities.getInstance().getAbilitiesManager().sendMessageActivated(player, "FAKE_PEARL");

            }
        }
    }

    @EventHandler
    public void onTeleportByAbility(PlayerTeleportEvent event) {
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("FAKE_PEARL");
        ItemStack itemStack = new ItemUtils(Material.getMaterial(abilityData.getItemData()), 1, abilityData.getItemID()).setName(abilityData.getName()).setLore(abilityData.getLore()).build();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL == IAbilities.getInstance().getAbilitiesManager().isAbility(itemStack, abilityData) && this.check.get(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.check.put(player.getUniqueId(), false);
    }

}
