package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CraftableHeartListener implements Listener {

    private final UnnamedRebalance plugin;
    private final NamespacedKey craftableHeartKey;

    public CraftableHeartListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
        this.craftableHeartKey = new NamespacedKey(plugin, "craftable_heart");
    }

    @EventHandler
    public void onCraftableHeartUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item.getType() == Material.RED_DYE && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(craftableHeartKey, PersistentDataType.DOUBLE)) {
                event.setCancelled(true);
                
                Double hearts = meta.getPersistentDataContainer().get(craftableHeartKey, PersistentDataType.DOUBLE);
                if (hearts != null) {
                    AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
                    if (health != null) {
                        double currentMax = health.getBaseValue();
                        
                        // Craftable hearts can only restore up to 20 health (10 hearts)
                        if (currentMax >= 20.0) {
                            player.sendMessage("§c§lYou are already at or above 10 hearts!");
                            player.sendMessage("§7§o(Craftable Heart Containers only restore up to 10 hearts)");
                            return;
                        }
                        
                        // Calculate how much to restore (cap at 20)
                        double heartsToAdd = hearts * 2.0;
                        double newMax = Math.min(20.0, currentMax + heartsToAdd);
                        double actualHeartsAdded = (newMax - currentMax) / 2.0;
                        
                        health.setBaseValue(newMax);
                        player.setHealth(Math.min(player.getHealth() + (actualHeartsAdded * 2.0), newMax));
                        
                        // Remove one heart item
                        item.setAmount(item.getAmount() - 1);
                        
                        player.sendMessage("§a§lYou consumed a Craftable Heart Container!");
                        player.sendMessage("§a+§c" + actualHeartsAdded + " heart(s)§a! Current max health: §2" + (newMax / 2.0) + " hearts");
                    }
                }
            }
        }
    }
}