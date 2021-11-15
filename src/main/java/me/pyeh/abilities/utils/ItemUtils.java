package me.pyeh.abilities.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;


public class ItemUtils {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemUtils(Material material) {
        this(material, 1, 0);
    }

    public ItemUtils(Material material, int amount, int durability) {
        this.itemStack = new ItemStack(material, amount, (short) durability);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemUtils setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemUtils setName(String name) {
        this.itemMeta.setDisplayName(ColorUtils.translate(name));
        return this;
    }

    public ItemUtils setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemUtils setLore(String... lore) {
        this.itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemUtils setEnchantment() {
        this.itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}
