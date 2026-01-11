package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;

public class TotemListener implements Listener {

    private final UnnamedRebalance plugin;

    public TotemListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onTotemUse(EntityResurrectEvent event) {
        if (!plugin.isTotemsDisabled()) return;
        
        if (event.getEntity() instanceof Player) {
            if (event.getHand() != null) {
                Player player = (Player) event.getEntity();
                ItemStack item = player.getInventory().getItem(event.getHand());
                
                if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
                    event.setCancelled(true);
                    player.sendMessage(plugin.getTotemDisableMessage());
                }
            }
        }
    }
}