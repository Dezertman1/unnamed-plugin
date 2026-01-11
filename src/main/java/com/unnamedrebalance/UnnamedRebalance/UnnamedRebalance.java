package com.unnamedrebalance.UnnamedRebalance;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class UnnamedRebalance extends JavaPlugin implements Listener {

    private double maceDamageMultiplier;
    private boolean totemsDisabled;
    private String totemDisableMessage;
    private boolean enderPearlTeleportDisabled;
    private String enderPearlDisableMessage;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Load config values
        maceDamageMultiplier = getConfig().getDouble("mace-damage-multiplier", 0.5);
        totemsDisabled = getConfig().getBoolean("totems-disabled", true);
        totemDisableMessage = getConfig().getString("totem-disable-message", "§cTotems of Undying are disabled!");
        enderPearlTeleportDisabled = getConfig().getBoolean("ender-pearl-teleport-disabled", true);
        enderPearlDisableMessage = getConfig().getString("ender-pearl-disable-message", "§cEnder pearl teleportation is disabled!");
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("UnnamedRebalance has been enabled!");
        getLogger().info("Mace damage multiplier: " + maceDamageMultiplier);
        getLogger().info("Totems disabled: " + totemsDisabled);
        getLogger().info("Ender pearl teleport disabled: " + enderPearlTeleportDisabled);
    }

    @Override
    public void onDisable() {
        getLogger().info("UnnamedRebalance has been disabled!");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onTotemUse(EntityResurrectEvent event) {
        if (!totemsDisabled) return;
        
        if (event.getEntity() instanceof Player) {
            if (event.getHand() != null) {
                Player player = (Player) event.getEntity();
                ItemStack item = player.getInventory().getItem(event.getHand());
                
                if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
                    event.setCancelled(true);
                    player.sendMessage(totemDisableMessage);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMaceDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();
            
            if (item.getType() == Material.MACE) {
                double originalDamage = event.getDamage();
                double reducedDamage = originalDamage * maceDamageMultiplier;
                event.setDamage(reducedDamage);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnderPearlTeleport(PlayerTeleportEvent event) {
        if (!enderPearlTeleportDisabled) return;
        
        // Only cancel if it's an ender pearl teleport
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(enderPearlDisableMessage);
        }
    }
}