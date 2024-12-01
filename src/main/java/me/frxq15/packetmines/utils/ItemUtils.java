package me.frxq15.packetmines.utils;

import me.frxq15.packetmines.PacketMines;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {
    private PacketMines plugin;

    public ItemUtils(PacketMines plugin) {
        this.plugin = plugin;
    }
    public ItemStack getWand() {
        List<String> lore = new ArrayList<String>();
        String material = plugin.getConfig().getString("WAND.MATERIAL");
        final ItemStack i = new ItemStack(Material.valueOf(material), 1);
        String name = plugin.getConfig().getString("WAND.NAME");

        final ItemMeta meta = i.getItemMeta();
        for (String lines : plugin.getConfig().getStringList("WAND.LORE")) {
            lore.add(plugin.format(lines));
        }
        meta.setDisplayName(plugin.format(name));
        if (hasGlow()) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        NamespacedKey key = new NamespacedKey(plugin, "PacketMines");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "packetmines_wand");
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }
    public boolean hasGlow() {
        return plugin.getConfig().getBoolean("WAND.GLOW");
    }
    public void giveWand(Player player) {
        player.getInventory().addItem(getWand());
    }
    public boolean isWand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        NamespacedKey key = new NamespacedKey(plugin, "PacketMines");
        return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }
}
