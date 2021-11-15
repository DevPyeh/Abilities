package me.pyeh.abilities;

import lombok.Getter;
import lombok.Setter;
import me.pyeh.abilities.manager.ability.AbilityManager;
import me.pyeh.abilities.manager.lunar.LunarManager;
import me.pyeh.abilities.manager.time.TimerManager;
import me.pyeh.abilities.utils.ColorUtils;
import me.pyeh.abilities.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class IAbilities extends JavaPlugin {

    @Getter public static IAbilities instance;
    public AbilityManager abilitiesManager;
    @Setter private ConfigUtils abilitiesFile;
    @Setter private ConfigUtils languageFile;
    @Setter private ConfigUtils effectsFile;
    @Setter private ConfigUtils configurationFile;
    private TimerManager timerManager;
    private LunarManager lunarManager;

    @Override
    public void onEnable() {
        instance = this;
        if(!this.checkLunarAPI()) return;
        this.registerConfig();
        this.registerAbilities();
        this.registerLunarAPI();
    }

    @Override
    public void onDisable() {
        ColorUtils.init();
        this.abilitiesManager.disable();
        this.lunarManager.disable();
        this.timerManager.disable();
        ColorUtils.log(" ");
        instance = null;
    }

    private void registerConfig() {
        try {
            this.abilitiesFile = new ConfigUtils("abilities.yml");
            this.languageFile = new ConfigUtils("language.yml");
            this.effectsFile = new ConfigUtils("effects.yml");
            this.configurationFile = new ConfigUtils("config.yml");
        } catch (RuntimeException e) {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void registerAbilities() {
        this.timerManager = new TimerManager();
        this.abilitiesManager = new AbilityManager();
        ColorUtils.init();
        this.timerManager.enabled();
        this.abilitiesManager.enabled();
    }

    private void registerLunarAPI() {
        if (this.getConfigurationFile().getBoolean("LUNAR_CLIENT_API") && Bukkit.getPluginManager().isPluginEnabled("LunarClient-API")) {
            this.lunarManager = new LunarManager();
            this.lunarManager.enabled();
        }
    }


    private boolean checkLunarAPI() {
        if (this.getConfigurationFile().getBoolean("LUNAR_CLIENT_API") && !Bukkit.getPluginManager().isPluginEnabled("LunarClient-API")) {
            ColorUtils.log("&cLunar client support is enable, but LunarClient-API is missing!");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }
}
