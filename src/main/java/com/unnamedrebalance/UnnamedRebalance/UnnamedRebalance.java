package com.unnamedrebalance.UnnamedRebalance;

import com.unnamedrebalance.UnnamedRebalance.commands.ResetHealthCommand;
import com.unnamedrebalance.UnnamedRebalance.commands.TradeHeartCommand;
import com.unnamedrebalance.UnnamedRebalance.listeners.*;
import com.unnamedrebalance.UnnamedRebalance.managers.CombatManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class UnnamedRebalance extends JavaPlugin {

    private static UnnamedRebalance instance;
    private NamespacedKey heartItemKey;
    private CombatManager combatManager; // Moved inside the class

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
    
    // Combat & Radius Feature Variables
    private long combatDuration;
    private boolean disablePearlsInCombat;
    private boolean disableTotemsInCombat;
    private double deathMessageRadius;
    private boolean deathMessageRadiusEnabled;
    private boolean endDimensionEnabled;

    @Override
    public void onEnable() {
        instance = this;
        heartItemKey = new NamespacedKey(this, "heart_item");
        
        saveDefaultConfig();
        loadConfig();
        
        // Initialize the manager AFTER loading config
        this.combatManager = new CombatManager(this); 
        
        registerListeners();
        registerCommands();
        
        getLogger().info("UnnamedRebalance has been enabled for 2026!");
    }

    @Override
    public void onDisable() {
        getLogger().info("UnnamedRebalance has been disabled!");
    }

    private void loadConfig() {
        maceDamageMultiplier = getConfig().getDouble("mace-damage-multiplier", 0.5);
        totemsDisabled = getConfig().getBoolean("totems-disabled", false);
        totemDisableMessage = getConfig().getString("totem-disable-message", "§c§lTotems only function while in combat!");
        enderPearlTeleportDisabled = getConfig().getBoolean("ender-pearl-teleport-disabled", false);
        enderPearlDisableMessage = getConfig().getString("ender-pearl-disable-message", "§c§lYou cannot use pearls while in combat!");
        lifestealEnabled = getConfig().getBoolean("lifesteal-enabled", true);
        heartsPerKill = getConfig().getDouble("hearts-per-kill", 1.0);
        minHealth = getConfig().getDouble("min-health", 2.0);
        maxHealth = getConfig().getDouble("max-health", 40.0);
        
        banOnLastHeartEnabled = getConfig().getBoolean("ban-on-last-heart.enabled", true);
        banMessage = getConfig().getString("ban-on-last-heart.ban-message", "§c§lEliminated!");
        banReason = getConfig().getString("ban-on-last-heart.ban-reason", "Lost all hearts");
        banDuration = getConfig().getLong("ban-on-last-heart.ban-duration", 0);
        broadcastBanEnabled = getConfig().getBoolean("ban-on-last-heart.broadcast-enabled", true);
        banBroadcastMessage = getConfig().getString("ban-on-last-heart.broadcast-message", "§c{player} §7was eliminated!");
        
        combatDuration = getConfig().getLong("combat-duration-seconds", 10) * 1000L;
        disablePearlsInCombat = getConfig().getBoolean("disable-pearls-in-combat", true);
        disableTotemsInCombat = getConfig().getBoolean("disable-totems-outside-combat", true);
        deathMessageRadius = getConfig().getDouble("death-message-radius", 500.0);
        deathMessageRadiusEnabled = getConfig().getBoolean("death-message-radius-enabled", true);
        endDimensionEnabled = getConfig().getBoolean("end-dimension-enabled", false);
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        
        // Combat System
        pm.registerEvents(new CombatTagListener(this), this); // Required to tag players
        pm.registerEvents(new CombatManager(this), this); 
        
        // Item & Recipe Listeners
        pm.registerEvents(new HeartItemListener(this), this);
        pm.registerEvents(new CraftableHeartListener(this), this);
        pm.registerEvents(new HeartContainerRecipeListener(this), this);
        pm.registerEvents(new HeartItemProtectionListener(this), this);
        
        // Mechanics Listeners
        pm.registerEvents(new TotemListener(this), this);
        pm.registerEvents(new MaceListener(this), this);
        pm.registerEvents(new EnderPearlListener(this), this);
        pm.registerEvents(new LifestealSystemListener(this), this);
        
        // World & Resurrection Listeners
        pm.registerEvents(new UnbanBeaconCraftListener(this), this);
        pm.registerEvents(new UnbanBeaconUseListener(this), this);
        pm.registerEvents(new EndPortalLockListener(this), this);
    }

    private void registerCommands() {
        getCommand("withdraw").setExecutor(new TradeHeartCommand(this));
        getCommand("resethealth").setExecutor(new ResetHealthCommand());
    }

    // Getters
    public static UnnamedRebalance getInstance() { return instance; }
    public CombatManager getCombatManager() { return combatManager; }
    public NamespacedKey getHeartItemKey() { return heartItemKey; }
    public double getMaceDamageMultiplier() { return maceDamageMultiplier; }
    public boolean isTotemsDisabled() { return totemsDisabled; }
    public String getTotemDisableMessage() { return totemDisableMessage; }
    public boolean isEnderPearlTeleportDisabled() { return enderPearlTeleportDisabled; }
    public String getEnderPearlDisableMessage() { return enderPearlDisableMessage; }
    public boolean isLifestealEnabled() { return lifestealEnabled; }
    public double getHeartsPerKill() { return heartsPerKill; }
    public double getMinHealth() { return minHealth; }
    public double getMaxHealth() { return maxHealth; }
    public boolean isBanOnLastHeartEnabled() { return banOnLastHeartEnabled; }
    public String getBanMessage() { return banMessage; }
    public String getBanReason() { return banReason; }
    public long getBanDuration() { return banDuration; }
    public boolean isBroadcastBanEnabled() { return broadcastBanEnabled; }
    public String getBanBroadcastMessage() { return banBroadcastMessage; }
    public long getCombatDuration() { return combatDuration; }
    public boolean isDisablePearlsInCombat() { return disablePearlsInCombat; }
    public double getDeathMessageRadius() { return deathMessageRadius; }
    public boolean isDeathMessageRadiusEnabled() { return deathMessageRadiusEnabled; }
    public boolean isEndDimensionEnabled() { return endDimensionEnabled; }
    public boolean isDisableTotemsInCombat() { return disableTotemsInCombat; }
}
