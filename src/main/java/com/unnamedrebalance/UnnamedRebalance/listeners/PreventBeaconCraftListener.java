package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PreventBeaconCraftListener implements Listener {

    private final UnnamedRebalance plugin;

    public PreventBeaconCraftListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();
        
        // Check if the result is a beacon
        if (result != null && result.getType() == Material.BEACON) {
            // Check if any of the crafting ingredients are heart containers
            for (ItemStack item : inventory.getMatrix()) {
                if (item != null && item.getType() == Material.NETHER_STAR && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null && meta.getPersistentDataContainer().has(plugin.getHeartItemKey(), PersistentDataType.DOUBLE)) {
                        // Heart container detected - cancel the craft
                        inventory.setResult(new ItemStack(Material.AIR));
                        return;
                    }
                }
            }
        }
    }
}