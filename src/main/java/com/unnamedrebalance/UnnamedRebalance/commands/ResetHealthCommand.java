package com.unnamedrebalance.UnnamedRebalance.commands;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetHealthCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("unnamedrebalance.resethealth")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        Player target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§cYou must specify a player!");
            return true;
        }
        
        AttributeInstance health = target.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(20.0); // Reset to 10 hearts
            target.setHealth(20.0);
            sender.sendMessage("§aReset " + target.getName() + "'s health to 10 hearts!");
            target.sendMessage("§aYour health has been reset to 10 hearts!");
        }
        
        return true;
    }
}