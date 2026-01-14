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
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class HeartContainerRecipeListener implements Listener {

    private final UnnamedRebalance plugin;
    private final NamespacedKey craftableHeartKey;

    public HeartContainerRecipeListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
        this.craftableHeartKey = new NamespacedKey(plugin, "craftable_heart");
        registerRecipe();
    }

    private void registerRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(plugin, "heart_container_recipe");
        
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
        }

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, createCraftableHeartContainer());
        
        // N = Netherite Ingot, S = Nautilus Shell, T = Core Ingredient
        recipe.shape("NSN", "STS", "NSN");
        
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('S', Material.NAUTILUS_SHELL);

        // Players can use a Totem, Ominous Key, Wither Skull, OR Recovery Compass
        RecipeChoice.MaterialChoice coreIngredients = new RecipeChoice.MaterialChoice(
                Material.TOTEM_OF_UNDYING,
                Material.OMINOUS_TRIAL_KEY,
                Material.WITHER_SKELETON_SKULL,
                Material.RECOVERY_COMPASS
        );
        
        recipe.setIngredient('T', coreIngredients);

        Bukkit.addRecipe(recipe);
    }

    public ItemStack createCraftableHeartContainer() {
        ItemStack heartItem = new ItemStack(Material.RED_DYE);
        ItemMeta meta = heartItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§c§lCraftable Heart");
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Right-click to consume");
            lore.add("§7and restore §c1.0 heart§7!");
            meta.setLore(lore);
            
            meta.getPersistentDataContainer().set(craftableHeartKey, PersistentDataType.DOUBLE, 1.0);
            meta.setEnchantmentGlintOverride(true);
            
            heartItem.setItemMeta(meta);
        }
        return heartItem;
    }
}