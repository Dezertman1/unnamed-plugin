package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class LifestealListener implements Listener {

    private final UnnamedRebalance plugin;

    public LifestealListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.isLifestealEnabled()) return;
        
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        // Only apply lifesteal if killed by another player
        if (killer != null && killer != victim) {
            // Remove hearts from victim
            AttributeInstance victimHealth = victim.getAttribute(Attribute.MAX_HEALTH);
            if (victimHealth != null) {
                double currentVictimMax = victimHealth.getBaseValue();
                double heartsToRemove = plugin.getHeartsPerKill() * 2.0;
                double newVictimMax = Math.max(plugin.getMinHealth(), currentVictimMax - heartsToRemove);
                victimHealth.setBaseValue(newVictimMax);
                
                victim.sendMessage("§c§lYou lost " + plugin.getHeartsPerKill() + " heart(s)! New max health: §4" + (newVictimMax / 2.0) + " hearts");
            }
            
            // Add hearts to killer
            AttributeInstance killerHealth = killer.getAttribute(Attribute.MAX_HEALTH);
            if (killerHealth != null) {
                double currentKillerMax = killerHealth.getBaseValue();
                double heartsToAdd = plugin.getHeartsPerKill() * 2.0;
                double newKillerMax = Math.min(plugin.getMaxHealth(), currentKillerMax + heartsToAdd);
                killerHealth.setBaseValue(newKillerMax);
                
                // Heal the killer
                killer.setHealth(Math.min(killer.getHealth() + heartsToAdd, newKillerMax));
                
                killer.sendMessage("§a§lYou gained " + plugin.getHeartsPerKill() + " heart(s)! New max health: §2" + (newKillerMax / 2.0) + " hearts");
            }
        }
    }
}