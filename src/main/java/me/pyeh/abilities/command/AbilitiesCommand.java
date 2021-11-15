package me.pyeh.abilities.command;

import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.AbilityData;
import me.pyeh.abilities.utils.ColorUtils;
import me.pyeh.abilities.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbilitiesCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("iability.permission.admin")) {
            sender.sendMessage(ColorUtils.translate("&cNo permission!"));
            return false;
        }

        if(args.length < 1) {
            this.getUsage(sender, label);
            return false;
        }

        String format =  args[0].toLowerCase();
        switch (format) {
            case "give": {
                if (!sender.hasPermission("iability.permission.admin")) {
                    sender.sendMessage(ColorUtils.translate("&cNo permission!"));
                    return false;
                }

                if (args.length < 4) {
                    sender.sendMessage(ColorUtils.translate("&cUsage: /" + label + " give <player> <ability|all> <amount>"));
                    return false;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ColorUtils.translate("&cPlayer &f" + args[1] + "&c not found!"));
                    return false;
                }

                Integer tryParse = ColorUtils.tryParse(args[3]);
                if (tryParse == null) {
                    sender.sendMessage(ColorUtils.translate("&cAmount must be a number."));
                    return false;
                }
                if (tryParse <= 0) {
                    sender.sendMessage(ColorUtils.translate("&cAmount must be positive."));
                    return false;
                }

                for (String stg : IAbilities.getInstance().getAbilitiesFile().getKeys(false)) {
                    AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility(stg);
                    if (args[2].equals(stg)) {
                        if (!abilityData.isEnabled()) {
                            sender.sendMessage(ColorUtils.translate("&cThe ability &f" + args[2] + "&c is currently disabled!"));
                            return false;
                        }
                    } else {
                        if (!args[2].equalsIgnoreCase("all")) {
                            continue;
                        }
                        if (!abilityData.isEnabled()) {
                            continue;
                        }
                    }
                    IAbilities.getInstance().getAbilitiesManager().giveAbility(sender ,target, abilityData, tryParse);
                }
                break;
            }

            case "list": {
                if (!sender.hasPermission("pAbility.permission.admin")) {
                    sender.sendMessage(ColorUtils.translate("&cNo permission!"));
                    return false;
                }
                IAbilities.getInstance().getAbilitiesManager().getAbilityList(sender);
                break;
            }

            case "edit": {
                if (!sender.hasPermission("pAbility.permission.admin")) {
                    sender.sendMessage(ColorUtils.translate("&cNo permission!"));
                    return false;
                }

                if (args.length < 2) {
                    sender.sendMessage(ColorUtils.translate("&cUsage: /" + label + " edit <ability>"));
                    return false;
                }

                for (String stg : IAbilities.getInstance().getAbilitiesFile().getKeys(false)) {
                    AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility(stg);
                    if (args[1].equalsIgnoreCase(stg)) {
                        Player player = (Player) sender;
                        Inventory inventory = IAbilities.getInstance().getAbilitiesManager().getPrincipal(player, abilityData);
                        player.openInventory(inventory);
                        return false;
                    }
                }
                break;
            }

            case "time": {
                if (!sender.hasPermission("pAbility.permission.admin")) {
                    sender.sendMessage(ColorUtils.translate("&cNo permission!"));
                    return false;
                }

                if (args.length < 3) {
                    sender.sendMessage(ColorUtils.translate("&cUsage: /" + label + " time <player> <set|remove> <value>"));
                    return false;
                }

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(ColorUtils.translate("&cPlayer &f" + args[1] + "&c not found!"));
                    return false;
                }

                Integer tryParse = ColorUtils.tryParse(args[4]);
                if (tryParse == null) {
                    sender.sendMessage(ColorUtils.translate("&cAmount must be a number."));
                    return false;
                }
                if (tryParse <= 0) {
                    sender.sendMessage(ColorUtils.translate("&cAmount must be positive."));
                    return false;
                }

                for (String stg : IAbilities.getInstance().getAbilitiesFile().getKeys(false)) {
                    AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility(stg);
                    if (args[1].equalsIgnoreCase("set")) {
                        if (args[3].equals(stg)) {
                            if (!abilityData.isEnabled()) {
                                sender.sendMessage(ColorUtils.translate("&cThe ability &f" + args[3] + "&c is currently disabled!"));
                                return false;
                            }
                            IAbilities.getInstance().getTimerManager().executeCooldown(target, stg, tryParse);
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (args[3].equals(stg)) {
                            if (!abilityData.isEnabled()) {
                                sender.sendMessage(ColorUtils.translate("&cThe ability &f" + args[3] + "&c is currently disabled!"));
                                return false;
                            }
                            IAbilities.getInstance().getTimerManager().executeCooldown(target, stg, 0);
                        }
                    } else {
                        sender.sendMessage(ColorUtils.translate("&cUsage: /" + label + " time <player> <set|remove> <value>"));
                    }
                }
                break;
            }

            case "reload": {
                if (!sender.hasPermission("pAbility.permission.admin")) {
                    sender.sendMessage(ColorUtils.translate("&cNo permission!"));
                    return false;
                }
                IAbilities.getInstance().setAbilitiesFile(new ConfigUtils("abilities.yml"));
                IAbilities.getInstance().setLanguageFile(new ConfigUtils("language.yml"));
                IAbilities.getInstance().setEffectsFile(new ConfigUtils("effects.yml"));
                IAbilities.getInstance().setConfigurationFile(new ConfigUtils("config.yml"));
                sender.sendMessage(ColorUtils.translate("&aThe all file has been reloaded!"));
                break;
            }
        }
        return false;
    }


    private void getUsage(CommandSender sender, String label) {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add(ColorUtils.translate("&6&lIAbilities &7(" + IAbilities.getInstance().getDescription().getVersion() + ")"));
        list.add("");
        list.add(ColorUtils.translate("  &6&l♦ &e/" + label + " give <player> <ability|all> <amount>"));
        list.add(ColorUtils.translate("  &6&l♦ &e/" + label + " list"));
        list.add(ColorUtils.translate("  &6&l♦ &e/" + label + " edit <ability>"));
        list.add(ColorUtils.translate("  &6&l♦ &e/" + label + " time <player> <set|remove> <ability> <value>"));
        list.add(ColorUtils.translate("  &6&l♦ &e/" + label + " reload"));
        list.add("");
        list.forEach(sender::sendMessage);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return ColorUtils.getCompletions(args, Arrays.asList("give", "time", "list", "reload", "edit"));
        }
        if (args[0].equalsIgnoreCase("time") && args.length == 2) {
            return ColorUtils.getCompletions(args, Arrays.asList("set", "remove"));
        }
        if (args[0].equalsIgnoreCase("time") && args.length == 4) {
            return ColorUtils.getCompletions(args, IAbilities.getInstance().getAbilitiesManager().getAbilities());
        }
        if (args[0].equalsIgnoreCase("edit")) {
            return ColorUtils.getCompletions(args, IAbilities.getInstance().getAbilitiesManager().getAbilities());
        }
        if (args[0].equalsIgnoreCase("give") && args.length == 3) {
            return ColorUtils.getCompletions(args, IAbilities.getInstance().getAbilitiesManager().getAbilities());
        }
        return null;
    }
}
