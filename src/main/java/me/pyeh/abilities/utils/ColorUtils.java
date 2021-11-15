package me.pyeh.abilities.utils;

import com.google.common.base.Preconditions;
import me.pyeh.abilities.IAbilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ColorUtils {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(final List<String> list) {
        for (int i = 0; i < list.size(); ++i) {
            list.set(i, translate(list.get(i)));
        }
        return list;
    }

    public static void log(String message) {Bukkit.getConsoleSender().sendMessage(translate(message));}

    public static Integer tryParse(final String s) {
        try {
            return Integer.parseInt(s);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return null;
        }
    }



    public static List<String> getCompletions(String[] args, List<String> input) {
        return getCompletions(args, input, 80);
    }

    public static List<String> getCompletions(String[] args, List<String> input, int limit) {
        Preconditions.checkNotNull(args);
        Preconditions.checkArgument(args.length != 0);
        String argument = args[args.length - 1];
        return input.stream().filter(string -> string.regionMatches(true, 0, argument, 0, argument.length()))
                .limit(limit).collect(Collectors.toList());
    }

    public static void init() {
        log("&6===&e=============================================&6===");
        log("       &6&lIAbilities &7(0.0.4-STABLED)");
        log(" ");
        log("   &6&l♦ &eAuthor: &f" + IAbilities.getInstance().getDescription().getAuthors());
        log("   &6&l♦ &eVersion: &f" + IAbilities.getInstance().getDescription().getVersion());
        log("   &6&l♦ &eServer Version: &f" + IAbilities.getInstance().getServer().getVersion());
        log(" ");
    }
}
