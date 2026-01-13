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

public class LifestealSystemListener implements Listener {

    private final UnnamedRebalance plugin;

    public LifestealSystemListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (plugin.isLifestealEnabled() && killer != null && killer != victim) {
            
            // Victim Heart Loss
            AttributeInstance victimHealth = victim.getAttribute(Attribute.MAX_HEALTH);
            double newVictimMax = 20.0; // Default fallback

            if (victimHealth != null) {
                double currentVictimMax = victimHealth.getBaseValue();
                double heartsToRemove = plugin.getHeartsPerKill() * 2.0;
                
                newVictimMax = Math.max(plugin.getMinHealth(), currentVictimMax - heartsToRemove);
                victimHealth.setBaseValue(newVictimMax);
                
                victim.sendMessage("§c§lYou lost " + plugin.getHeartsPerKill() + " heart(s)! New max health: §4" + (newVictimMax / 2.0) + " hearts");
            }

            // Killer heart gain
            AttributeInstance killerHealth = killer.getAttribute(Attribute.MAX_HEALTH);
            if (killerHealth != null) {
                double currentKillerMax = killerHealth.getBaseValue();
                double heartsToAdd = plugin.getHeartsPerKill() * 2.0;
                double newKillerMax = Math.min(plugin.getMaxHealth(), currentKillerMax + heartsToAdd);
                
                killerHealth.setBaseValue(newKillerMax);
                killer.setHealth(Math.min(killer.getHealth() + heartsToAdd, newKillerMax));
                
                killer.sendMessage("§a§lYou gained " + plugin.getHeartsPerKill() + " heart(s)! New max health: §2" + (newKillerMax / 2.0) + " hearts");
            }

            if (plugin.isBanOnLastHeartEnabled() && newVictimMax <= plugin.getMinHealth()) {
                // Schedule the ban for the next tick so the death event finishes processing
                Bukkit.getScheduler().runTask(plugin, () -> {
                    banPlayer(victim);
                });
            }
        }
    }

    // Banning and broadcast logic

    private void banPlayer(Player player) {
        String banMessage = plugin.getBanMessage();
        String banReason = plugin.getBanReason();
        long banDuration = plugin.getBanDuration();
        
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        
        Date expiration = (banDuration > 0) ? new Date(System.currentTimeMillis() + banDuration) : null;
        
        banList.addBan(player.getName(), banReason, expiration, "UnnamedRebalance");
        
        if (plugin.isBroadcastBanEnabled()) {
            String broadcastMessage = plugin.getBanBroadcastMessage()
                .replace("{player}", player.getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcastMessage));
        }

        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', banMessage));
    }
}