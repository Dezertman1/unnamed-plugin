package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Date;

public class HeartBanListener implements Listener {

    private final UnnamedRebalance plugin;

    public HeartBanListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.isBanOnLastHeartEnabled()) return;
        
        Player victim = event.getEntity();
        
        // Check if the player is at minimum health after this death
        AttributeInstance health = victim.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            double currentMax = health.getBaseValue();
            
            // If they're at or below minimum health (2.0 = 1 heart), ban them
            if (currentMax <= plugin.getMinHealth()) {
                // Schedule the ban for next tick (after respawn)
                Bukkit.getScheduler().runTask(plugin, () -> {
                    banPlayer(victim);
                });
            }
        }
    }

    private void banPlayer(Player player) {
        String banMessage = plugin.getBanMessage();
        String banReason = plugin.getBanReason();
        long banDuration = plugin.getBanDuration();
        
        // Get the ban list
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        
        // Calculate expiration date (null = permanent, otherwise add duration in milliseconds)
        Date expiration = null;
        if (banDuration > 0) {
            expiration = new Date(System.currentTimeMillis() + banDuration);
        }
        
        // Add the ban
        banList.addBan(player.getName(), banReason, expiration, "UnnamedRebalance");
        
        // Kick the player with the ban message
        player.kickPlayer(banMessage);
        
        // Broadcast to server
        if (plugin.isBroadcastBanEnabled()) {
            String broadcastMessage = plugin.getBanBroadcastMessage()
                .replace("{player}", player.getName());
            Bukkit.broadcastMessage(broadcastMessage);
        }
    }
}