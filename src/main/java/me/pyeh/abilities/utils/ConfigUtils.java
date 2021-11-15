package me.pyeh.abilities.utils;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigUtils extends YamlConfiguration {

    @Getter private final File file;

    public ConfigUtils(String name) throws RuntimeException {
        this.file = new File(IAbilities.getInstance().getDataFolder(), name);

        if(!this.file.exists()) {
            IAbilities.getInstance().saveResource(name, false);
        }

        try {
            this.load(this.file);
        } catch(IOException | InvalidConfigurationException e) {
            ColorUtils.log("&4&lX &cError occurred while loading " + name + ".");
            Stream.of(e.getMessage().split("\n")).forEach(ColorUtils::log);
            throw new RuntimeException();
        }
    }

    public void save() {
        try {
            this.save(this.file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getSection(String name) {
        return super.getConfigurationSection(name);
    }

    @Override
    public int getInt(String path) {
        return super.getInt(path, 0);
    }

    @Override
    public double getDouble(String path) {
        return super.getDouble(path, 0.0);
    }

    @Override
    public boolean getBoolean(String path) {
        return super.getBoolean(path, false);
    }

    @Override
    public String getString(String path) {
        return ColorUtils.translate(super.getString(path, ""));
    }

    @Override
    public List<String> getStringList(String path) {
        return super.getStringList(path).stream().map(ColorUtils::translate).collect(Collectors.toList());
    }
}
