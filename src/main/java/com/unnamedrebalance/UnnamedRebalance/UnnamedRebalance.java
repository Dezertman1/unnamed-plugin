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

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize NamespacedKey for heart items
        heartItemKey = new NamespacedKey(this, "heart_item");
        
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Load config values
        loadConfig();
        
        // Register event listeners
        registerListeners();
        
        // Register commands
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
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new TotemListener(this), this);
        getServer().getPluginManager().registerEvents(new MaceListener(this), this);
        getServer().getPluginManager().registerEvents(new EnderPearlListener(this), this);
        getServer().getPluginManager().registerEvents(new LifestealListener(this), this);
        getServer().getPluginManager().registerEvents(new HeartItemListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftableHeartListener(this), this);
        getServer().getPluginManager().registerEvents(new HeartContainerRecipeListener(this), this);
        getServer().getPluginManager().registerEvents(new PreventBeaconCraftListener(this), this);
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
}