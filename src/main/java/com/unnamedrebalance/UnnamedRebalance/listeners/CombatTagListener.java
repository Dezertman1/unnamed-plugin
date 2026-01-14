package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatTagListener implements Listener {
    private final UnnamedRebalance plugin;

    public CombatTagListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
            plugin.getCombatManager().tagPlayer(victim);
            plugin.getCombatManager().tagPlayer(attacker);
        }
    }
}
