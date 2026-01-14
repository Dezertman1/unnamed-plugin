package com.unnamedrebalance.UnnamedRebalance.commands;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class TradeHeartCommand implements CommandExecutor {

    private final UnnamedRebalance plugin;

    public TradeHeartCommand(UnnamedRebalance plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        double heartsToTrade = 1.0;
        if (args.length > 0) {
            try {
                heartsToTrade = Double.parseDouble(args[0]);
                
                // Enforce minimum withdraw of 1 heart
                if (heartsToTrade < 1.0) {
                    player.sendMessage("§cMinimum trade is 1 full heart.");
                    return true;
                }

                // Limits hearts to increments of 0.5
                if ((heartsToTrade * 2.0) % 1.0 != 0) {
                    player.sendMessage("§cHearts must be in increments of 0.5!");
                    return true;
                }

            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid number! Usage: /tradeheart [hearts]");
                return true;
            }
        }
        
        AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            double currentMax = health.getBaseValue();
            double healthPointsToRemove = heartsToTrade * 2.0;
            double newMax = currentMax - healthPointsToRemove;
            
            if (newMax < plugin.getMinHealth()) {
                player.sendMessage("§cYou cannot trade that many hearts! Minimum health: " + (plugin.getMinHealth() / 2.0) + " hearts");
                return true;
            }

            // Check if inventory is full before taking health
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage("§cYour inventory is full!");
                return true;
            }
            
            // Remove the hearts
            health.setBaseValue(newMax);
            if (player.getHealth() > newMax) {
                player.setHealth(newMax);
            }
            
            int fullHearts = (int) Math.floor(heartsToTrade);
            double remainingPartial = heartsToTrade - fullHearts;

            if (fullHearts > 0) {
                ItemStack heartItem = createHeartItem(1.0);
                heartItem.setAmount(fullHearts);
                player.getInventory().addItem(heartItem);
            }
            
            if (remainingPartial > 0) {
                player.getInventory().addItem(createHeartItem(remainingPartial));
            }
            
            player.sendMessage("§6§lYou traded §c" + heartsToTrade + " heart(s) §6§lfor Heart(s)!");
            player.sendMessage("§6New max health: §e" + (newMax / 2.0) + " hearts");
        }
        
        return true;
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