package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnbanBeaconCraftListener {

    private final UnnamedRebalance plugin;
    private final NamespacedKey unbanBeaconKey;

    public UnbanBeaconCraftListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
        this.unbanBeaconKey = new NamespacedKey(plugin, "unban_beacon");
        
        // Register the recipe natively on initialization
        registerUnbanBeaconRecipe();
    }

    private void registerUnbanBeaconRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(plugin, "unban_beacon_recipe");

        ItemStack tradedHeart = createTradedHeartReference();
        ItemStack craftableHeart = createCraftableHeartReference();

        // Allow both hearts in the recipe
        RecipeChoice.ExactChoice heartChoices = new RecipeChoice.ExactChoice(Arrays.asList(tradedHeart, craftableHeart));

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, createUnbanBeacon());
        recipe.shape("HDH", "DBD", "HDH");

        recipe.setIngredient('H', heartChoices);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('B', Material.BEACON);

        if (Bukkit.getRecipe(recipeKey) != null) Bukkit.removeRecipe(recipeKey);
        Bukkit.addRecipe(recipe);
    }

    public ItemStack createUnbanBeacon() {
        ItemStack unbanBeacon = new ItemStack(Material.BEACON);
        ItemMeta meta = unbanBeacon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§lBeacon of Resurrection");
            List<String> lore = new ArrayList<>();
            lore.add("§7Right-click to open the");
            lore.add("§7resurrection menu and revive");
            lore.add("§7an eliminated player.");
            lore.add("§e§lConsumes this item on use");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(unbanBeaconKey, PersistentDataType.BYTE, (byte) 1);
            meta.setEnchantmentGlintOverride(true);
            unbanBeacon.setItemMeta(meta);
        }
        return unbanBeacon;
    }

    private ItemStack createTradedHeartReference() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.getHeartItemKey(), PersistentDataType.DOUBLE, 1.0);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCraftableHeartReference() {
        HeartContainerRecipeListener heartListener = new HeartContainerRecipeListener(plugin);
        return heartListener.createCraftableHeartContainer();
    }
}