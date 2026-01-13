package com.unnamedrebalance.UnnamedRebalance.listeners;

import com.unnamedrebalance.UnnamedRebalance.UnnamedRebalance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HeartContainerRecipeListener {

    private final UnnamedRebalance plugin;
    private final NamespacedKey craftableHeartKey;

    public HeartContainerRecipeListener(UnnamedRebalance plugin) {
        this.plugin = plugin;
        this.craftableHeartKey = new NamespacedKey(plugin, "craftable_heart");
        // Register the recipe immediately when this listener is created
        registerRecipe();
    }

    private void registerRecipe() {
        // Create the unique key for this recipe
        NamespacedKey recipeKey = new NamespacedKey(plugin, "heart_container_recipe");
        
        // Delete existing recipe if it exists (prevents duplicates on reload)
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey);
        }

        ShapedRecipe recipe = new ShapedRecipe(recipeKey, createCraftableHeartContainer());
        
        // N = Netherite Ingot, S = Nautilus Shell, T = Totem of Undying
        recipe.shape("NSN", "STS", "NSN");
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('S', Material.NAUTILUS_SHELL);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);

        Bukkit.addRecipe(recipe);
    }

    public ItemStack createCraftableHeartContainer() {
        ItemStack heartItem = new ItemStack(Material.RED_DYE);
        ItemMeta meta = heartItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§c§lCraftable Heart Container");
            
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
