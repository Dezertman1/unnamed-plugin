package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EndPortalLockListener implements Listener {

    private final UnnamedRebalance plugin;

    public EndPortalLockListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPortalFrameInteract(PlayerInteractEvent event) {
        if (plugin.isEndDimensionEnabled()) return;

        // Check if the player is right-clicking end portal frame with an eye of ender
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            
            if (event.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
                
                if (event.getItem() != null && event.getItem().getType() == Material.ENDER_EYE) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§c§lThe End Portal is currently locked!");
                }
            }
        }
    }
}