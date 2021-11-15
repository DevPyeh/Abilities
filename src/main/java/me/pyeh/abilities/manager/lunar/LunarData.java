package me.pyeh.abilities.manager.lunar;

import com.lunarclient.bukkitapi.object.LCCooldown;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class LunarData {

    private String name;
    private Material material;

    public LCCooldown createCooldown(long duration) {
        return new LCCooldown(this.name, duration, TimeUnit.SECONDS, this.material);
    }
}
