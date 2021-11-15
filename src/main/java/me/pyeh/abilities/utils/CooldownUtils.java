package me.pyeh.abilities.utils;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCCooldown;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.lunar.LunarManager;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownUtils {

    private final Map<UUID, Long> cooldown = new HashMap<>();

    public void apply(Player player, long delay, String ability) {
        this.cooldown.put(player.getUniqueId(), System.currentTimeMillis() + delay);
        if (IAbilities.getInstance().getConfigurationFile().getBoolean("LUNAR_CLIENT_API") && Bukkit.getPluginManager().isPluginEnabled("LunarClient-API")) {
            LunarManager lunarManager = IAbilities.getInstance().getLunarManager();
            if (!lunarManager.getPlayers().contains(player.getUniqueId())) return;
            if (!lunarManager.getCooldown().containsKey(ability)) return;

            this.sendLunarCooldown(player.getUniqueId(), lunarManager.getCooldown().get(ability).createCooldown(delay));
        }
    }

    public boolean check(Player player) {
        return this.cooldown.containsKey(player.getUniqueId()) && (this.cooldown.get(player.getUniqueId()) >= System.currentTimeMillis());
    }

    public void remove(Player player, String ability) {
        this.cooldown.remove(player.getUniqueId());

        if (IAbilities.getInstance().getConfigurationFile().getBoolean("LUNAR_CLIENT_API") && Bukkit.getPluginManager().isPluginEnabled("LunarClient-API")) {
            LunarManager lunarManager = IAbilities.getInstance().getLunarManager();
            if (!lunarManager.getPlayers().contains(player.getUniqueId())) return;
            if (!lunarManager.getCooldown().containsKey(ability)) return;

            this.sendLunarCooldown(player.getUniqueId(), lunarManager.getCooldown().get(ability).createCooldown(0L));
        }
    }

    public String getString(Player player) {
        long time = this.cooldown.get(player.getUniqueId()) - System.currentTimeMillis();
        return DurationFormatUtils.formatDuration(time, "s");
    }

    private void sendLunarCooldown(UUID uuid, LCCooldown cooldown) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        LunarClientAPI.getInstance().sendCooldown(player, cooldown);
    }

}
