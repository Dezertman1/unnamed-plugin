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
        if (!(event.getEntity() instanceof Player player)) return;

        boolean shouldBlock = false;

        // If global disable is ON, block all totems.
        if (plugin.isTotemsDisabled()) {
            shouldBlock = true;
        } 
        // If global is OFF, check if we should block because they are in combat.
        else if (plugin.isDisableTotemsInCombat()) {
            if (plugin.getCombatManager().isInCombat(player)) {
                shouldBlock = true;
            }
        }

        if (shouldBlock) {
            // Check if they are actually holding a totem
            ItemStack item = (event.getHand() != null) ? player.getInventory().getItem(event.getHand()) : null;
            if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
                event.setCancelled(true);
                player.sendMessage(plugin.getTotemDisableMessage());
            }
        }
    }
}