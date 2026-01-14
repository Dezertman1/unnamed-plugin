package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HeartItemProtectionListener implements Listener {

    private final UnnamedRebalance plugin;

    public HeartItemProtectionListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item itemEntity)) return;

        ItemStack item = itemEntity.getItemStack();
        if (!isCustomHeart(item)) return;

        EntityDamageEvent.DamageCause cause = event.getCause();

        // Blast Protection
        if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            if (plugin.getConfig().getBoolean("heart-items-blastproof", true)) {
                event.setCancelled(true);
            }
        }

        // Fire/Lava Protection
        if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.LAVA || cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if (plugin.getConfig().getBoolean("heart-items-fireproof", true)) {
                event.setCancelled(true);
                itemEntity.setFireTicks(0);
                itemEntity.setVisualFire(false);
            }
        }
    }

    private boolean isCustomHeart(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(plugin.getHeartItemKey(), PersistentDataType.DOUBLE) ||
               meta.getPersistentDataContainer().has(new org.bukkit.NamespacedKey(plugin, "craftable_heart"), PersistentDataType.DOUBLE);
    }
}