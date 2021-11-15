package me.pyeh.abilities.manager.lunar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.event.LCPlayerRegisterEvent;
import com.lunarclient.bukkitapi.event.LCPlayerUnregisterEvent;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.utils.ColorUtils;
import me.pyeh.abilities.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class LunarManager implements Listener {

    private final Set<UUID> players;
    private final LCPacketServerRule lcPacketServerRule;
    private final Map<String, LunarData> cooldown;

    public LunarManager() {
        this.players = new HashSet<>();
        this.lcPacketServerRule = new LCPacketServerRule(ServerRule.LEGACY_ENCHANTING, true);
        this.cooldown = new HashMap<>();
    }

    public void enabled() {
        ColorUtils.log("  &6&l♦ &eThe lunarAPI is currently &aEnabled&e!");
        this.loadCooldown();
        Bukkit.getPluginManager().registerEvents(this, IAbilities.getInstance());
    }

    public void disable() {
        this.players.clear();
        this.cooldown.clear();
    }

    private void loadCooldown() {
        IAbilities.getInstance().getAbilitiesFile().getKeys(false).forEach(abilityName -> {
            ConfigurationSection section = IAbilities.getInstance().getAbilitiesFile().getSection(abilityName);

            LunarData lunarCooldown = new LunarData();
            lunarCooldown.setName(abilityName);
            ItemStack itemStack = new ItemUtils(Material.getMaterial(section.getString(abilityName + ".item_data")), 1, section.getInt(abilityName + ".item_id")).build();
            lunarCooldown.setMaterial(itemStack.getType());

            this.cooldown.put(section.getString(abilityName), lunarCooldown);
        });
    }

    @EventHandler
    public void onRegister(LCPlayerRegisterEvent event) {
        Player player = event.getPlayer();
        this.players.add(player.getUniqueId());
        if (!IAbilities.getInstance().getServer().getVersion().equals("1.8")) {
            LunarClientAPI.getInstance().sendPacket(player, this.lcPacketServerRule);
        }
    }

    @EventHandler
    public void onUnregister(LCPlayerUnregisterEvent event) {
        Player player = event.getPlayer();
        this.players.remove(player.getUniqueId());
    }

}
