package com.unnamedrebalance.UnnamedRebalance.managers;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.event.Listener;

public class CombatManager implements Listener {
    private final UnnamedRebalance plugin;
    private final Map<UUID, Long> combatLog = new HashMap<>();

    public CombatManager(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    public void tagPlayer(Player player) {
        combatLog.put(player.getUniqueId(), System.currentTimeMillis() + plugin.getCombatDuration());
    }

    public boolean isInCombat(Player player) {
        if (!combatLog.containsKey(player.getUniqueId())) return false;
        
        if (System.currentTimeMillis() > combatLog.get(player.getUniqueId())) {
            combatLog.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
}
