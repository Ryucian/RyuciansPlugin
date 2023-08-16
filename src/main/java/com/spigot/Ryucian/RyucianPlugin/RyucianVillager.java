package com.spigot.Ryucian.RyucianPlugin;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * 特殊な村人を作るためのコマンド
 */
public class RyucianVillager 
{
    /**
     * 商人を生成する
     */
    public static void SpawnTrader(Player player)
    {
        Villager v = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);


        var recipes = v.getRecipes();
        System.out.println(recipes.size());
        
        var itemStack = new ItemStack(Material.DIAMOND, 1);
        
        var recipe = new MerchantRecipe(itemStack, 1, 999, true, 10, 1, 0, 0);

        //recipes.add(recipe);

        //v.setRecipes(recipes);
        v.setRecipe(0,recipe);
    }
    


}
