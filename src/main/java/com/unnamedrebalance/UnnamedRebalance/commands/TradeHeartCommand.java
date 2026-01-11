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
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Parse hearts argument (default to 1)
        double heartsToTrade = 1.0;
        if (args.length > 0) {
            try {
                heartsToTrade = Double.parseDouble(args[0]);
                if (heartsToTrade <= 0) {
                    player.sendMessage("§cYou must trade at least 0.5 hearts!");
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
            double newMax = currentMax - (heartsToTrade * 2.0);
            
            if (newMax < plugin.getMinHealth()) {
                player.sendMessage("§cYou cannot trade that many hearts! Minimum health: " + (plugin.getMinHealth() / 2.0) + " hearts");
                return true;
            }
            
            // Remove the hearts
            health.setBaseValue(newMax);
            if (player.getHealth() > newMax) {
                player.setHealth(newMax);
            }
            
            // Calculate how many full heart containers and partial hearts
            int fullHearts = (int) heartsToTrade;
            double partialHeart = heartsToTrade - fullHearts;
            
            // Give full heart containers (1 heart each)
            if (fullHearts > 0) {
                ItemStack heartItem = createHeartItem(1.0);
                heartItem.setAmount(fullHearts);
                player.getInventory().addItem(heartItem);
            }
            
            // Give partial heart container if needed
            if (partialHeart > 0) {
                ItemStack partialHeartItem = createHeartItem(partialHeart);
                player.getInventory().addItem(partialHeartItem);
            }
            
            player.sendMessage("§6§lYou traded §c" + heartsToTrade + " heart(s) §6§lfor Heart Container(s)!");
            player.sendMessage("§6New max health: §e" + (newMax / 2.0) + " hearts");
        }
        
        return true;
    }

    private ItemStack createHeartItem(double hearts) {
        ItemStack heartItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = heartItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§c§lHeart Container");
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Right-click to consume");
            lore.add("§7and restore §c" + hearts + " heart(s)§7!");
            meta.setLore(lore);
            
            // Store the heart value in persistent data
            meta.getPersistentDataContainer().set(plugin.getHeartItemKey(), PersistentDataType.DOUBLE, hearts);
            
            // Make it uncraftable/unusable in recipes
            meta.setEnchantmentGlintOverride(true); // Adds a glint to distinguish it
            
            heartItem.setItemMeta(meta);
        }
        
        return heartItem;
    }
}