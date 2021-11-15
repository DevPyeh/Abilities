package me.pyeh.abilities.utils;

import me.pyeh.abilities.IAbilities;
import org.bukkit.Bukkit;

public class TaskUtils {

    public static void aSyncDelayed(Callable callable, long delay) {
        IAbilities.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(IAbilities.getInstance(), callable::call, delay);
    }

    public static void sync(Callable callable) {
        Bukkit.getScheduler().runTask(IAbilities.getInstance(), callable::call);
    }

    public interface Callable {
        void call();
    }
}
