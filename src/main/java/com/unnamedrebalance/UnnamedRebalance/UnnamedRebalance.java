package com.unnamedrebalance.UnnamedRebalance;

import com.unnamedrebalance.UnnamedRebalance.commands.ResetHealthCommand;
import com.unnamedrebalance.UnnamedRebalance.commands.TradeHeartCommand;
import com.unnamedrebalance.UnnamedRebalance.listeners.*;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class UnnamedRebalance extends JavaPlugin {

    private static UnnamedRebalance instance;
    private NamespacedKey heartItemKey;

    // Config values
    private double maceDamageMultiplier;
    private boolean totemsDisabled;
    private String totemDisableMessage;
    private boolean enderPearlTeleportDisabled;
    private String enderPearlDisableMessage;
    private boolean lifestealEnabled;
    private double heartsPerKill;
    private double minHealth;
    private double maxHealth;
    private boolean banOnLastHeartEnabled;
    private String banMessage;
    private String banReason;
    private long banDuration;
    private boolean broadcastBanEnabled;
    private String banBroadcastMessage;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize NamespacedKey for heart items
        heartItemKey = new NamespacedKey(this, "heart_item");
        
        saveDefaultConfig();
        
        loadConfig();
        
        registerListeners();
        
        registerCommands();
        
        getLogger().info("UnnamedRebalance has been enabled!");
        logConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("UnnamedRebalance has been disabled!");
    }

    private void loadConfig() {
        maceDamageMultiplier = getConfig().getDouble("mace-damage-multiplier", 0.5);
        totemsDisabled = getConfig().getBoolean("totems-disabled", true);
        totemDisableMessage = getConfig().getString("totem-disable-message", "§cTotems of Undying are disabled!");
        enderPearlTeleportDisabled = getConfig().getBoolean("ender-pearl-teleport-disabled", true);
        enderPearlDisableMessage = getConfig().getString("ender-pearl-disable-message", "§cEnder pearl teleportation is disabled!");
        lifestealEnabled = getConfig().getBoolean("lifesteal-enabled", true);
        heartsPerKill = getConfig().getDouble("hearts-per-kill", 1.0);
        minHealth = getConfig().getDouble("min-health", 2.0);
        maxHealth = getConfig().getDouble("max-health", 40.0);
        banOnLastHeartEnabled = getConfig().getBoolean("ban-on-last-heart.enabled", true);
        banMessage = getConfig().getString("ban-on-last-heart.ban-message", "§c§lYou have been eliminated!\n§7You lost your last heart.");
        banReason = getConfig().getString("ban-on-last-heart.ban-reason", "Lost all hearts");
        banDuration = getConfig().getLong("ban-on-last-heart.ban-duration", 0); // 0 = permanent
        broadcastBanEnabled = getConfig().getBoolean("ban-on-last-heart.broadcast-enabled", true);
        banBroadcastMessage = getConfig().getString("ban-on-last-heart.broadcast-message", "§c{player} §7has been eliminated!");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new TotemListener(this), this);
        getServer().getPluginManager().registerEvents(new MaceListener(this), this);
        getServer().getPluginManager().registerEvents(new EnderPearlListener(this), this);
        getServer().getPluginManager().registerEvents(new LifestealSystemListener(this), this);
        getServer().getPluginManager().registerEvents(new HeartItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PreventBeaconCraftListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftableHeartListener(this), this);
        getServer().getPluginManager().registerEvents(new HeartContainerRecipeListener(this), this);
        getServer().getPluginManager().registerEvents(new UnbanBeaconCraftListener(this), this);
        getServer().getPluginManager().registerEvents(new UnbanBeaconUseListener(this), this);
    }

    private void registerCommands() {
        getCommand("tradeheart").setExecutor(new TradeHeartCommand(this));
        getCommand("resethealth").setExecutor(new ResetHealthCommand());
    }

    private void logConfig() {
        getLogger().info("Mace damage multiplier: " + maceDamageMultiplier);
        getLogger().info("Totems disabled: " + totemsDisabled);
        getLogger().info("Ender pearl teleport disabled: " + enderPearlTeleportDisabled);
        getLogger().info("Lifesteal enabled: " + lifestealEnabled + " (" + heartsPerKill + " hearts per kill)");
        getLogger().info("Ban on last heart: " + banOnLastHeartEnabled);
    }

    // Getters for config values
    public static UnnamedRebalance getInstance() {
        return instance;
    }

    public NamespacedKey getHeartItemKey() {
        return heartItemKey;
    }

    public double getMaceDamageMultiplier() {
        return maceDamageMultiplier;
    }

    public boolean isTotemsDisabled() {
        return totemsDisabled;
    }

    public String getTotemDisableMessage() {
        return totemDisableMessage;
    }

    public boolean isEnderPearlTeleportDisabled() {
        return enderPearlTeleportDisabled;
    }

    public String getEnderPearlDisableMessage() {
        return enderPearlDisableMessage;
    }

    public boolean isLifestealEnabled() {
        return lifestealEnabled;
    }

    public double getHeartsPerKill() {
        return heartsPerKill;
    }

    public double getMinHealth() {
        return minHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public boolean isBanOnLastHeartEnabled() {
        return banOnLastHeartEnabled;
    }

    public String getBanMessage() {
        return banMessage;
    }

    public String getBanReason() {
        return banReason;
    }

    public long getBanDuration() {
        return banDuration;
    }

    public boolean isBroadcastBanEnabled() {
        return broadcastBanEnabled;
    }

    public String getBanBroadcastMessage() {
        return banBroadcastMessage;
    }
}