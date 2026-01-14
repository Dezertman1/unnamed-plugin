package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HeartItemListener implements Listener {

    private final UnnamedRebalance plugin;

    public HeartItemListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHeartItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item.getType() == Material.NETHER_STAR && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(plugin.getHeartItemKey(), PersistentDataType.DOUBLE)) {
                event.setCancelled(true);
                
                Double hearts = meta.getPersistentDataContainer().get(plugin.getHeartItemKey(), PersistentDataType.DOUBLE);
                if (hearts != null) {
                    AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
                    if (health != null) {
                        double currentMax = health.getBaseValue();
                        double newMax = Math.min(plugin.getMaxHealth(), currentMax + (hearts * 2.0));
                        
                        if (newMax > currentMax) {
                            health.setBaseValue(newMax);
                            player.setHealth(Math.min(player.getHealth() + (hearts * 2.0), newMax));
                            
                            // Remove one heart item
                            item.setAmount(item.getAmount() - 1);
                            
                            player.sendMessage("§a§lYou consumed a Heart!");
                            player.sendMessage("§a+§c" + hearts + " heart(s)§a! New max health: §2" + (newMax / 2.0) + " hearts");
                        } else {
                            player.sendMessage("§c§lYou are already at maximum health!");
                        }
                    }
                }
            }
        }
    }
}