package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EnderPearlListener implements Listener {

    private final UnnamedRebalance plugin;

    public EnderPearlListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnderPearlTeleport(PlayerTeleportEvent event) {
        // Only run if the teleport was caused by an Ender Pearl
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        boolean shouldBlock = false;

        if (plugin.isEnderPearlTeleportDisabled()) {
            // Globally disabled
            shouldBlock = true;
        } else if (plugin.isDisablePearlsInCombat() && plugin.getCombatManager().isInCombat(event.getPlayer())) {
            // Disabled only because they are in combat
            shouldBlock = true;
        }

        if (shouldBlock) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getEnderPearlDisableMessage());
        }
    }
}
