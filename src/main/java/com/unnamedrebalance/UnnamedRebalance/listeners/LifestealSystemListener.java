package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class LifestealSystemListener implements Listener {

    private final UnnamedRebalance plugin;
    // Tracks when a player's heart-loss protection expires
    private final Map<UUID, Long> gracePeriods = new HashMap<>();

    public LifestealSystemListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Only proceed if Lifesteal is enabled and killed by another player
        if (!plugin.isLifestealEnabled() || killer == null || killer == victim) return;

        // Grace period
        if (gracePeriods.containsKey(victim.getUniqueId())) {
            if (System.currentTimeMillis() < gracePeriods.get(victim.getUniqueId())) {
                killer.sendMessage("§e§l! §7That player is protected by a grace period.");
                return;
            }
        }

        // Victim heart loss
        AttributeInstance victimHealth = victim.getAttribute(Attribute.MAX_HEALTH);
        double newVictimMax = 20.0; 

        if (victimHealth != null) {
            double currentVictimMax = victimHealth.getBaseValue();
            double heartsToRemove = plugin.getHeartsPerKill() * 2.0;
            
            newVictimMax = Math.max(plugin.getMinHealth(), currentVictimMax - heartsToRemove);
            victimHealth.setBaseValue(newVictimMax);
            
            victim.sendMessage("§c§lYou lost " + plugin.getHeartsPerKill() + " heart(s)! New max health: §4" + (newVictimMax / 2.0) + " hearts");
            
            // Apply Grace Period (Loaded from config, e.g., 300 seconds)
            long graceSeconds = plugin.getConfig().getLong("death-grace-period-seconds", 300);
            gracePeriods.put(victim.getUniqueId(), System.currentTimeMillis() + (graceSeconds * 1000L));
        }

        // Killer heart gain + overflow
        AttributeInstance killerHealth = killer.getAttribute(Attribute.MAX_HEALTH);
        if (killerHealth != null) {
            double currentKillerMax = killerHealth.getBaseValue();
            double heartsToAdd = plugin.getHeartsPerKill() * 2.0;
            
            // If killer is already at or above max health, give item instead
            if (currentKillerMax >= plugin.getMaxHealth()) {
                ItemStack heartItem = createHeartItem(plugin.getHeartsPerKill());
                
                if (killer.getInventory().firstEmpty() == -1) {
                    killer.getWorld().dropItemNaturally(killer.getLocation(), heartItem);
                } else {
                    killer.getInventory().addItem(heartItem);
                }
                killer.sendMessage("§a§lYou are at max health! A Heart was added to your inventory.");
            } else {
                double newKillerMax = Math.min(plugin.getMaxHealth(), currentKillerMax + heartsToAdd);
                killerHealth.setBaseValue(newKillerMax);
                killer.setHealth(Math.min(killer.getHealth() + heartsToAdd, newKillerMax));
                killer.sendMessage("§a§lYou gained " + plugin.getHeartsPerKill() + " heart(s)!");
            }
        }

        // Banning Logic
        if (plugin.isBanOnLastHeartEnabled() && newVictimMax <= plugin.getMinHealth()) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                banPlayer(victim);
            });
        }
    }

    private void banPlayer(Player player) {
        String banMessage = plugin.getBanMessage();
        String banReason = plugin.getBanReason();
        long banDuration = plugin.getBanDuration();
        
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        Date expiration = (banDuration > 0) ? new Date(System.currentTimeMillis() + banDuration) : null;
        
        banList.addBan(player.getName(), banReason, expiration, "UnnamedRebalance");
        
        if (plugin.isBroadcastBanEnabled()) {
            String broadcastMessage = plugin.getBanBroadcastMessage().replace("{player}", player.getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcastMessage));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1f, 1f);
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 0.8f);
            }
        }

        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', banMessage));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathMessage(PlayerDeathEvent event) {
        if (!plugin.isDeathMessageRadiusEnabled()) return;

        String originalMessage = event.getDeathMessage();
        if (originalMessage == null || originalMessage.isEmpty()) return;

        event.setDeathMessage(null);

        Player victim = event.getEntity();
        double radiusSquared = Math.pow(plugin.getDeathMessageRadius(), 2);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getWorld().equals(victim.getWorld())) {
                if (onlinePlayer.getLocation().distanceSquared(victim.getLocation()) <= radiusSquared) {
                    onlinePlayer.sendMessage(originalMessage);
                }
            }
        }
    }

    private ItemStack createHeartItem(double hearts) {
        ItemStack heartItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = heartItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§c§lHeart " + (hearts == 1.0 ? "" : "§7(" + hearts + ")"));
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Right-click to consume");
            lore.add("§7and restore §c1.0 heart§7!");
            meta.setLore(lore);
            
            meta.getPersistentDataContainer().set(plugin.getHeartItemKey(), PersistentDataType.DOUBLE, hearts);
            meta.setEnchantmentGlintOverride(true);
            
            heartItem.setItemMeta(meta);
        }
        
        return heartItem;
    }
}