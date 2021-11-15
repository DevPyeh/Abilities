package me.pyeh.abilities.manager.ability;

import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.command.AbilitiesCommand;
import me.pyeh.abilities.manager.ability.type.*;
import me.pyeh.abilities.utils.ColorUtils;
import me.pyeh.abilities.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AbilityManager implements Listener {

    private final List<Listener> abilityHandlers;
    private final List<AbilityData> abilities;
    private final Map<UUID, String> editingAbility;
    private final List<String> editingStringAbility;
    private final List<String> editingItemStackAbility;
    public final Map<UUID, String> string;

    public AbilityManager() {
        this.abilityHandlers = new ArrayList<>();
        this.abilities = new ArrayList<>();
        this.editingAbility = new HashMap<>();
        this.editingStringAbility = new ArrayList<>();
        this.editingItemStackAbility = new ArrayList<>();
        this.string = new HashMap<>();
    }

    public void enabled() {
        this.registerAbilitiesHandlers();
        this.loadAbilities();

        IAbilities.getInstance().getCommand("ability").setExecutor(new AbilitiesCommand());
        Bukkit.getPluginManager().registerEvents(this, IAbilities.getInstance());
        ColorUtils.log("  &6&l♦ &f" + this.abilityHandlers.size() + "&e abilities registered successfully.");
        ColorUtils.log("&6===&e=============================================&6===");
    }

    public void disable() {
        this.savedAbilities();
        this.abilityHandlers.clear();
        this.abilities.clear();
        ColorUtils.log("  &6&l♦ &eThe abilities manager was successfully saved.");
    }

    private void registerAbilitiesHandlers() {
        this.abilityHandlers.add(new TeleportAbility());
        this.abilityHandlers.add(new BlockAntiTrapperAbility());
        this.abilityHandlers.add(new AntiBuildAbility());
        this.abilityHandlers.add(new BrewingRefillAbility());
        this.abilityHandlers.add(new CocaineAbility());
        this.abilityHandlers.add(new DistortionAbility());
        this.abilityHandlers.add(new FakeLogoutAbility());
        this.abilityHandlers.add(new FakePearlAbility());
        this.abilityHandlers.add(new GrapplingHookAbility());
        this.abilityHandlers.add(new CounterPotionAbility());
        this.abilityHandlers.stream().filter(Objects::nonNull).forEach(abilityHandlers ->
                Bukkit.getPluginManager().registerEvents(abilityHandlers, IAbilities.getInstance()));
    }

    private void loadAbilities() {
        IAbilities.getInstance().getAbilitiesFile().getKeys(false).forEach(abilityName -> {
            ConfigurationSection section = IAbilities.getInstance().getAbilitiesFile().getSection(abilityName);

            AbilityData data = new AbilityData();
            data.setName(abilityName);
            data.setDisplayName(section.getString("display_name"));
            data.setEnabled(section.getBoolean("enabled"));
            data.setGlow(section.getBoolean("glow"));
            data.setItemData(section.getString("item_data"));
            data.setItemID(section.getInt("item_id"));
            data.setCooldown(section.getInt("cooldown"));
            data.setLore(section.getStringList("lore"));

            this.abilities.add(data);
        });
    }

    private void savedAbilities() {
        this.abilities.forEach(ability -> {
            ConfigurationSection section = IAbilities.getInstance().getAbilitiesFile().createSection(ability.getName());

            section.set("display_name", ability.getDisplayName());
            section.set("enabled", ability.isEnabled());
            section.set("glow", ability.isGlow());
            section.set("item_data", ability.getItemData());
            section.set("item_id", ability.getItemID());
            section.set("cooldown", ability.getCooldown());
            section.set("lore", ability.getLore());
        });

        IAbilities.getInstance().getAbilitiesFile().save();
    }

    public AbilityData getAbility(String name) {
        return this.abilities.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<String> getAbilities() {
        return new ArrayList<>(IAbilities.getInstance().getAbilitiesFile().getKeys(false));
    }

    public boolean isAbility(final ItemStack itemStack, final AbilityData data) {
        return itemStack != null && itemStack.getType() != Material.AIR && itemStack.hasItemMeta() && itemStack.getItemMeta().getDisplayName() != null && itemStack.getItemMeta().getLore() != null
                && itemStack.getItemMeta().getDisplayName().equals(ColorUtils.translate(data.getDisplayName())) && itemStack.getItemMeta().getLore().equals(ColorUtils.translate(data.getLore()));
    }

    public void giveAbility(CommandSender sender, Player player, AbilityData ability, int amount) {

        ItemStack itemBuild;
        if (ability.isGlow()) {
            itemBuild = new ItemUtils(Material.getMaterial(ability.getItemData()), amount, (short) ability.getItemID()).setName(ability.getDisplayName()).setLore(ColorUtils.translate(ability.getLore())).setEnchantment().setAmount(amount).build();
        } else {
            itemBuild = new ItemUtils(Material.getMaterial(ability.getItemData()), amount, (short) ability.getItemID()).setName(ability.getDisplayName()).setLore(ColorUtils.translate(ability.getLore())).setAmount(amount).build();
        }

        if (player.getInventory().firstEmpty() == -1) {
            sender.sendMessage(ColorUtils.translate("&aYou just give the ability <ability>&a to the player <target>.".replace("<ability>", ability.getDisplayName()).replace("<target>", player.getName())));
            sender.sendMessage(ColorUtils.translate("&aYou just received the ability <ability> by player <sender>.".replace("<ability>", ability.getDisplayName()).replace("<sender>", sender.getName())));
            player.getWorld().dropItemNaturally(player.getLocation(), itemBuild);
        } else {
            player.getInventory().addItem(itemBuild);
            sender.sendMessage(ColorUtils.translate("&aYou just give the ability <ability>&a to the player <target>.".replace("<ability>", ability.getDisplayName()).replace("<target>", player.getName())));
            sender.sendMessage(ColorUtils.translate("&aYou just received the ability <ability>&a by player <sender>.".replace("<ability>", ability.getDisplayName()).replace("<sender>", sender.getName())));
        }
    }

    public void getAbilityList(CommandSender sender) {
        sender.sendMessage(" ");
        sender.sendMessage(ColorUtils.translate("&6&lIAbilities List"));
        this.abilities.stream().sorted(Comparator.comparing(AbilityData::getName)).forEach(ability -> sender.sendMessage(ColorUtils.translate("  &6&l♦ &e" + ability.getName() + " &7(" + ability.isEnabled() + "&7)")));
        sender.sendMessage(" ");
    }

    public void getCountItem(Player player) {
        ItemStack air = new ItemStack(Material.AIR);
        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(air);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
    }

    public String getOptions(String ability, String argument) {
        return IAbilities.getInstance().getConfigurationFile().getString( ability + "." + argument);
    }

    public List<String> getEffects(String ability) {
        return IAbilities.getInstance().getEffectsFile().getStringList(ability + ".effects");
    }

    public void sendMessageCooldown(Player player, String delay, String ability) {
        AbilityData abilityData = this.getAbility(ability);
        for (String line : IAbilities.getInstance().getLanguageFile().getStringList(ability + "_ABILITY.cooldown")) {
            player.sendMessage(line.replace("<display_name>", ColorUtils.translate(abilityData.getDisplayName()))
                    .replace("<delay>", delay));
        }
    }

    public void sendMessageActivated(Player player, String ability) {
        AbilityData abilityData = this.getAbility(ability);
        for (String line : IAbilities.getInstance().getLanguageFile().getStringList(ability + "_ABILITY.activated")) {
            player.sendMessage(line.replace("<display_name>", ColorUtils.translate(abilityData.getDisplayName()))
                    .replace("<delay>", abilityData.getCooldown() + ""));
        }
    }

    public void sendMessageActivatedByPlayer(Player player,Player target, String ability) {
        AbilityData abilityData = this.getAbility(ability);
        for (String line : IAbilities.getInstance().getLanguageFile().getStringList(ability + "_ABILITY.activated_attacker")) {
            player.sendMessage(ColorUtils.translate(line.replace("<enemy>", target.getName())
                    .replace("<display_name>", ColorUtils.translate(abilityData.getDisplayName()))
                    .replace("<delay>", abilityData.getCooldown() + "")));
        }
    }

    public void sendMessageActivatedByTarget(Player player,Player target, String ability) {
        AbilityData abilityData = this.getAbility(ability);
        for (String line : IAbilities.getInstance().getLanguageFile().getStringList(ability + "_ABILITY.activated_victim")) {
            target.sendMessage(ColorUtils.translate(line.replace("<attacker>", player.getName())
                    .replace("<display_name>", ColorUtils.translate(abilityData.getDisplayName()))));
        }
    }

    public void sendCustomMessage(Player player, String argument, String ability) {
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility(ability);
        for (String line : IAbilities.getInstance().getLanguageFile().getStringList(ability + "_ABILITY." + argument)) {
            player.sendMessage(ColorUtils.translate(line.replace("<display_name>", abilityData.getDisplayName())));
        }
    }

    public Inventory getPrincipal(Player player, AbilityData data) {
        Inventory inventory = Bukkit.createInventory(null, 4 * 9, ColorUtils.translate("&eAbility Manager"));

        ItemStack info = new ItemUtils(Material.PAPER).setName(ColorUtils.translate("&6&lAbility Information"))
                .setLore(ColorUtils.translate("&7&m-----------------------------"), ColorUtils.translate(" &6&l♦ &eDisplay Name: " + data.getDisplayName()),
                        ColorUtils.translate(" &6&l♦ &eCooldown: &f" + data.getCooldown() + " seconds"), ColorUtils.translate(" &6&l♦ &eStatus: &f" + this.getColor(data.isEnabled()) + data.isEnabled()), ColorUtils.translate(" &6&l♦ &eGlow: " + this.getColor(data.isGlow()) + data.isGlow()),
                        ColorUtils.translate(" &6&l♦ &eItem Data: &f" + data.getItemData()), ColorUtils.translate("&7&m-----------------------------")).build();

        ItemStack displayName = new ItemUtils(Material.FEATHER).setName(ColorUtils.translate("&eChange Display Name")).setLore(ColorUtils.translate("&7&nRight click to change!")).build();
        ItemStack cooldown = new ItemUtils(Material.LEVER).setName(ColorUtils.translate("&eChange Cooldown")).setLore(ColorUtils.translate("&7&nRight click to change!")).build();
        ItemStack itemData = new ItemUtils(Material.getMaterial(data.getItemData()), 1, data.getItemID()).setName(ColorUtils.translate("&eChange Material")).setLore(ColorUtils.translate("&7&nRight click to change!")).build();

        ItemStack status;
        ItemStack glow;
        if (data.isEnabled()) {
            status = new ItemUtils(Material.NETHER_STAR).setName(ColorUtils.translate("&eChange Status")).setLore(ColorUtils.translate(" &f» &aEnabled"), ColorUtils.translate(" &cDisabled"), "",ColorUtils.translate("&7&nRight click to change!")).build();
        } else {
            status = new ItemUtils(Material.NETHER_STAR).setName(ColorUtils.translate("&eChange Status")).setLore(ColorUtils.translate(" &aEnabled"), ColorUtils.translate(" &f» &cDisabled"), "",ColorUtils.translate("&7&nRight click to change!")).build();
        }

        if (data.isGlow()) {
            glow = new ItemUtils(Material.ENCHANTED_BOOK).setName(ColorUtils.translate("&eChange Glow")).setLore(ColorUtils.translate(" &f» &aEnabled"), ColorUtils.translate(" &cDisabled"), "",ColorUtils.translate("&7&nRight click to change!")).build();
        } else {
            glow = new ItemUtils(Material.ENCHANTED_BOOK).setName(ColorUtils.translate("&eChange Glow")).setLore(ColorUtils.translate(" &aEnabled"), ColorUtils.translate(" &f» &cDisabled"), "",ColorUtils.translate("&7&nRight click to change!")).build();
        }

        inventory.setItem(4, info);
        inventory.setItem(22, glow);
        inventory.setItem(19, displayName);
        inventory.setItem(20, cooldown);
        inventory.setItem(24, status);
        inventory.setItem(25, itemData);

        this.editingAbility.put(player.getUniqueId(), data.getName());
        return inventory;
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!event.getInventory().getName().equals(ColorUtils.translate("&eAbility Manager"))) return;
        AbilityData abilityData = IAbilities.getInstance().getAbilitiesManager().getAbility(this.editingAbility.get(player.getUniqueId()));

        this.savedAbilities();
        player.sendMessage(ColorUtils.translate("&aAbility " + abilityData.getDisplayName() + "&a edit was saved successfully! "));
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (!event.getInventory().getName().equals(ColorUtils.translate("&eAbility Manager"))) return;
        if(event.getClickedInventory() == null || event.getInventory() != event.getClickedInventory()) return;

        event.setCancelled(true);
        switch (slot) {
            case 19: {
                if (this.editingAbility.get(player.getUniqueId()) == null) {
                    player.sendMessage(ColorUtils.translate("&cContact a developer!"));
                    break;
                }
                this.editingStringAbility.add(player.getName());
                this.string.put(player.getUniqueId(), "name");
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                player.closeInventory();
                player.sendMessage(" ");
                player.sendMessage(ColorUtils.translate("&ePlace the name of the ability as you like!"));
                player.sendMessage(ColorUtils.translate("&eRemember to use the variable '&' to get te colors."));
                player.sendMessage(ColorUtils.translate("&eIf you want to go back to the edit inventory, just put '&ccancel&e' in the chat!"));
                player.sendMessage(" ");
                break;
            }
            case 20: {
                if (this.editingAbility.get(player.getUniqueId()) == null) {
                    player.sendMessage(ColorUtils.translate("&cContact a developer!"));
                    break;
                }
                this.editingStringAbility.add(player.getName());
                this.string.put(player.getUniqueId(), "cooldown");
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                player.closeInventory();
                player.sendMessage(" ");
                player.sendMessage(ColorUtils.translate("&ePlace the cooldown of the ability as you like!"));
                player.sendMessage(ColorUtils.translate("&eIf you want to go back to the edit inventory, just put '&ccancel&e' in the chat!"));
                player.sendMessage(" ");
                break;
            }
            case 22: {
                if (this.editingAbility.get(player.getUniqueId()) == null) {
                    player.sendMessage(ColorUtils.translate("&cContact a developer!"));
                    break;
                }
                AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility(this.editingAbility.get(player.getUniqueId()));
                ability.setGlow(!ability.isGlow());
                player.closeInventory();

                player.openInventory(this.getPrincipal(player, ability));
                break;
            }
            case 24: {
                if (this.editingAbility.get(player.getUniqueId()) == null) {
                    player.sendMessage(ColorUtils.translate("&cContact a developer!"));
                    break;
                }
                AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility(this.editingAbility.get(player.getUniqueId()));
                ability.setEnabled(!ability.isEnabled());
                player.closeInventory();

                player.openInventory(this.getPrincipal(player, ability));
                break;
            }
            case 25: {
                if (this.editingAbility.get(player.getUniqueId()) == null) {
                    player.sendMessage(ColorUtils.translate("&cContact a developer!"));
                    break;
                }
                this.editingItemStackAbility.add(player.getName());
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                player.closeInventory();
                player.sendMessage(" ");
                player.sendMessage(ColorUtils.translate("&ePut '&aapply&e' in the chat to apply the change!"));
                player.sendMessage(ColorUtils.translate(",you must have an item in your hand to save the item!"));
                player.sendMessage(ColorUtils.translate("&eIf you want to go back to the edit inventory, just put '&ccancel&e' in the chat!"));
                player.sendMessage(" ");
            }
        }
    }

    @EventHandler
    public void onChatEditing(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (this.editingStringAbility.contains(player.getName())) {
            event.setCancelled(true);
            String message = event.getMessage();
            AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility(this.editingAbility.get(player.getUniqueId()));
            if (message.equalsIgnoreCase("cancel")) {
                this.editingStringAbility.remove(player.getName());
                player.sendMessage(ColorUtils.translate("&cYou just canceled the event!"));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                player.openInventory(this.getPrincipal(player, ability));
                return;
            }
            this.editingStringAbility.remove(player.getName());
            this.activateRegister(player, ability, message);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f,1.0f);
            player.sendMessage(ColorUtils.translate("&aThe change was successfully updated!"));
            player.openInventory(this.getPrincipal(player, ability));
        }
    }

    @Deprecated @EventHandler
    public void onChatEditingItemStack(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (this.editingItemStackAbility.contains(player.getName())) {
            event.setCancelled(true);
            String message = event.getMessage();
            AbilityData ability = IAbilities.getInstance().getAbilitiesManager().getAbility(this.editingAbility.get(player.getUniqueId()));
            if (message.equalsIgnoreCase("cancel")) {
                this.editingItemStackAbility.remove(player.getName());
                player.sendMessage(ColorUtils.translate("&cYou just canceled the event!"));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                player.openInventory(this.getPrincipal(player, ability));
            } else if (message.equalsIgnoreCase("apply")) {
                this.editingItemStackAbility.remove(player.getName());
                if (player.getItemInHand() != null) {
                    ability.setItemData(itemStack.getType().name());
                    ability.setItemID(itemStack.getDurability());
                    player.sendMessage(ColorUtils.translate("&aThe change was successfully updated!"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    player.openInventory(this.getPrincipal(player, ability));
                    return;
                }
                player.sendMessage(ColorUtils.translate("&cNo item was registered in your hand, therefore the edit inventory will open."));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                player.openInventory(this.getPrincipal(player, ability));
            } else {
                player.sendMessage(ColorUtils.translate("&cInvalid argument, therefore the edit inventory will open."));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                player.openInventory(this.getPrincipal(player, ability));
            }
        }
    }

    private String getColor(boolean value) {
        if (value) {return "&a"; } else { return "&c"; }
    }

    public void activateRegister(Player player,AbilityData abilityData, String value) {
        if (this.string.get(player.getUniqueId()).equals("name")) {
            abilityData.setDisplayName(value);
        } else if (this.string.get(player.getUniqueId()).equals("cooldown")) {
            abilityData.setCooldown(Integer.parseInt(value));
        }
    }

}
