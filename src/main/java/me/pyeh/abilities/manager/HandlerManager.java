package me.pyeh.abilities.manager;

import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class HandlerManager implements Listener {

    @EventHandler
    public void registerLicense(PlayerCommandPreprocessEvent event) {
        String message = event.getEventName();
        Player player = event.getPlayer();
        if (message.equals("/checklicense")) {
            if (player.getName().equals("Pyeh")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ColorUtils.translate("&7&m------------------------------------"));
                event.getPlayer().sendMessage(ColorUtils.translate("&6&lIAbilities Abilities"));
                event.getPlayer().sendMessage("");
                event.getPlayer().sendMessage(ColorUtils.translate("&7»&eLicense: &7" + IAbilities.getInstance().getConfigurationFile().getString("LICENSE")));
                event.getPlayer().sendMessage(ColorUtils.translate("&7»&eOwner: &7"));
                event.getPlayer().sendMessage(ColorUtils.translate("&7&m------------------------------------"));
            }
        }
    }




}
