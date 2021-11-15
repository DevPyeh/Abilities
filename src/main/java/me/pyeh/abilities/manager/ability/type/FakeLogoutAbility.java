package me.pyeh.abilities.manager.ability.type;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.utils.ColorUtils;
import me.pyeh.abilities.utils.CooldownUtils;
import me.pyeh.abilities.utils.PacketUtils;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class FakeLogoutAbility implements Listener {

    public final CooldownUtils cooldown = new CooldownUtils();
    private final Set<UUID> players = new HashSet<>();
    private final Set<UUID> offline = new HashSet<>();

    @EventHandler
    public void onInteractiveAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("FAKE_LOGOUT");
        if (!IAbilities.getInstance().getAbilitiesManager().isAbility(item, abilityData)) return;

        if (this.cooldown.check(player)) {
            event.setCancelled(true);
            player.updateInventory();
            IAbilities.getInstance().getAbilitiesManager().sendMessageCooldown(player, this.cooldown.getString(player), "FAKE_LOGOUT");
            return;
        }
        IAbilities.getInstance().getAbilitiesManager().getCountItem(player);
        this.activate(player);
        this.hideSystem(player);
    }


    private void activate(Player player) {
        IAbilities.getInstance().getAbilitiesManager().sendMessageActivated(player, "FAKE_LOGOUT");
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility("FAKE_LOGOUT");
        this.cooldown.apply(player, abilityData.getCooldown() * 1000L, "FAKE_LOGOUT");
        List<String> effects = IAbilities.getInstance().getAbilitiesManager().getEffects("FAKE_LOGOUT");

        if (IAbilities.getInstance().getAbilitiesManager().getOptions("FAKE_LOGOUT", "spawn_entity").equalsIgnoreCase("VILLAGER")) {
            Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
            villager.setCustomName(IAbilities.getInstance().getAbilitiesManager().getOptions("FAKE_LOGOUT", "name_entity").replace("<player>", player.getName()));
        } else if (IAbilities.getInstance().getAbilitiesManager().getOptions("FAKE_LOGOUT", "spawn_entity").equalsIgnoreCase("SKELETON")) {
            Skeleton skeleton = (Skeleton) player.getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);
            skeleton.setCustomName(IAbilities.getInstance().getAbilitiesManager().getOptions("FAKE_LOGOUT", "name_entity").replace("<player>", player.getName()));
        } else {
            player.sendMessage(ColorUtils.translate("&cThe entity to be spawned was not registered, check the config.yml"));
        }

        if (effects.size() != 0) {
            for (String str : effects) {
                PotionEffectType effect = PotionEffectType.getByName(str.split(":")[0]);
                int duration = (Ints.tryParse(str.split(":")[1]) + 1) * 20;
                int level = Ints.tryParse(str.split(":")[2]) - 1;
                player.addPotionEffect(new PotionEffect(effect, duration, level));
            }
        }
    }

    private void hideSystem(Player player) {
        PacketUtils.updateArmor(player, true);
        this.players.add(player.getUniqueId());
    }

    private void showSystem(Player player) {
        this.players.remove(player.getUniqueId());
        PacketUtils.updateArmor(player, false);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof  Player)) return;
        Player target = (Player) event.getEntity();

        if (this.players.contains(target.getUniqueId())) {
            this.showSystem(target);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(this.players.contains(player.getUniqueId())) {
            this.showSystem(player);
            this.offline.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(this.offline.remove(player.getUniqueId())) {
            this.hideSystem(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if(this.players.contains(player.getUniqueId())) {
            this.showSystem(player);
        }
    }

    @EventHandler
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if(this.players.contains(player.getUniqueId())) {
            this.showSystem(player);
        }
    }


}
