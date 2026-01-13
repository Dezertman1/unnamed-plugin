package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HeartContainerRecipeListener implements Listener {

    private final UnnamedRebalance plugin;
    private final NamespacedKey craftableHeartKey;

    public HeartContainerRecipeListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
        this.craftableHeartKey = new NamespacedKey(plugin, "craftable_heart");
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();
        
        // Check for the heart container recipe pattern
        // Pattern:
        //  N S N
        //  S T S
        //  N S N
        // N = Netherite Ingot, S = Nautilus Shell, T = Totem of Undying
        
        if (matrix.length == 9) {
            boolean isHeartRecipe = 
                isNetheriteIngot(matrix[0]) && isNautilusShell(matrix[1]) && isNetheriteIngot(matrix[2]) &&
                isNautilusShell(matrix[3]) && isTotemOfUndying(matrix[4]) && isNautilusShell(matrix[5]) &&
                isNetheriteIngot(matrix[6]) && isNautilusShell(matrix[7]) && isNetheriteIngot(matrix[8]);
            
            if (isHeartRecipe) {
                // Valid recipe - create the craftable heart container
                inventory.setResult(createCraftableHeartContainer());
            }
        }
    }

    private boolean isNetheriteIngot(ItemStack item) {
        return item != null && item.getType() == Material.NETHERITE_INGOT;
    }

    private boolean isNautilusShell(ItemStack item) {
        return item != null && item.getType() == Material.NAUTILUS_SHELL;
    }

    private boolean isTotemOfUndying(ItemStack item) {
        return item != null && item.getType() == Material.TOTEM_OF_UNDYING;
    }

    private ItemStack createCraftableHeartContainer() {
        ItemStack heartItem = new ItemStack(Material.RED_DYE);
        ItemMeta meta = heartItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§c§lCraftable Heart Container");
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Right-click to consume");
            lore.add("§7and restore §c1.0 heart§7!");
            lore.add("");
            lore.add("§6⚠ §7Can only restore up to §610 hearts§7!");
            lore.add("");
            lore.add("§8Crafted from an ancient totem...");
            meta.setLore(lore);
            
            // Store the heart value in persistent data with craftable key
            meta.getPersistentDataContainer().set(craftableHeartKey, PersistentDataType.DOUBLE, 1.0);
            
            // Add enchantment glint
            meta.setEnchantmentGlintOverride(true);
            
            heartItem.setItemMeta(meta);
        }
        
        return heartItem;
    }
}