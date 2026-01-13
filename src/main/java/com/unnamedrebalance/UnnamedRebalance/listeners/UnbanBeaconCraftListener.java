package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class UnbanBeaconCraftListener implements Listener {

    private final UnnamedRebalance plugin;
    private final NamespacedKey unbanBeaconKey;
    private final NamespacedKey craftableHeartKey;

    public UnbanBeaconCraftListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
        this.unbanBeaconKey = new NamespacedKey(plugin, "unban_beacon");
        this.craftableHeartKey = new NamespacedKey(plugin, "craftable_heart");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();
        
        // Check for the unban beacon recipe pattern (same as your image)
        // Pattern:
        //  H D H
        //  D B D
        //  H D H
        // H = Heart Container (any type), D = Diamond Block, B = Beacon
        
        if (matrix.length == 9) {
            boolean isUnbanBeaconRecipe = 
                isHeartContainer(matrix[0]) && isDiamondBlock(matrix[1]) && isHeartContainer(matrix[2]) &&
                isDiamondBlock(matrix[3]) && isBeacon(matrix[4]) && isDiamondBlock(matrix[5]) &&
                isHeartContainer(matrix[6]) && isDiamondBlock(matrix[7]) && isHeartContainer(matrix[8]);
            
            if (isUnbanBeaconRecipe) {
                // Valid recipe - create the unban beacon
                inventory.setResult(createUnbanBeacon());
            }
        }
    }

    private boolean isHeartContainer(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        // Check if it's any type of heart container (traded or craftable)
        boolean isTradedHeart = item.getType() == Material.NETHER_STAR && 
                                meta.getPersistentDataContainer().has(plugin.getHeartItemKey(), PersistentDataType.DOUBLE);
        boolean isCraftableHeart = item.getType() == Material.RED_DYE && 
                                   meta.getPersistentDataContainer().has(craftableHeartKey, PersistentDataType.DOUBLE);
        
        return isTradedHeart || isCraftableHeart;
    }

    private boolean isDiamondBlock(ItemStack item) {
        return item != null && item.getType() == Material.DIAMOND_BLOCK;
    }

    private boolean isBeacon(ItemStack item) {
        return item != null && item.getType() == Material.BEACON;
    }

    private ItemStack createUnbanBeacon() {
        ItemStack unbanBeacon = new ItemStack(Material.BEACON);
        ItemMeta meta = unbanBeacon.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§6§lBeacon of Resurrection");
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Right-click to open the");
            lore.add("§7resurrection menu and revive");
            lore.add("§7an eliminated player.");
            lore.add("");
            lore.add("§e§lConsumes this item on use");
            lore.add("");
            lore.add("§8A beacon of hope for the fallen...");
            meta.setLore(lore);
            
            // Mark this as an unban beacon
            meta.getPersistentDataContainer().set(unbanBeaconKey, PersistentDataType.BYTE, (byte) 1);
            
            // Add enchantment glint
            meta.setEnchantmentGlintOverride(true);
            
            unbanBeacon.setItemMeta(meta);
        }
        
        return unbanBeacon;
    }
}