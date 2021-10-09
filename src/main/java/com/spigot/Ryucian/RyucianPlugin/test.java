package com.spigot.Ryucian.RyucianPlugin;

import java.math.BigDecimal;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class test
{
	private static final String GRAPPRING_HOOK = "フックショット";

	public static void onDamage(EntityDamageEvent e)
	{
		//ダメージを受けたエンティティがプレイヤーでなければ処理しない
	    if (!(e.getEntity() instanceof Player)) { return; }

	    //プレイヤーが持っているのを確認
	    Player player = (Player) e.getEntity() ;

	    //持っているものが釣り竿でなければ処理しない
	    if( !(player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD))) return;

	    //　フックショットじゃないもので緑じゃないものを処理しない
	     String handItemName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();

	    //持っているアイテムが釣り竿で緑文字「フックショット」なら
	     if(!handItemName.equalsIgnoreCase(ChatColor.GREEN + GRAPPRING_HOOK)) return;


	    //ダメージを受けた原因を取得する
	    var cause = e.getCause();

	    //ダメージを受けた原因がFALLでなければ処理しない
	    if (!cause.equals(DamageCause.FALL))return;

	    //FALLダメージを取得する
	    var damage = e.getDamage();

	    //半減させてダメージを無効化させる
	    if (damage > 1  )  e.setDamage(0);

	}


	public static void onPlayerInteract(PlayerInteractEvent e)
	{
	    Player player = e.getPlayer();

	    //持っているものが釣り竿でなければ処理しない
	    if( !(player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD))) return;

	    //　フックショットじゃないもので緑じゃないものを処理しない
	    String handItemName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();

	//持っているアイテムが釣り竿で緑文字「フックショット」なら

	        if(!handItemName.equalsIgnoreCase(ChatColor.GREEN + GRAPPRING_HOOK)) return;


	            //↓「https://www.spigotmc.org/threads/get-location-of-hook-on-playerfishevent.164687/」

	        Material item = player.getItemInHand().getType();
	        if(item == Material.FISHING_ROD){



	            java.util.List<Entity> nearby = player.getNearbyEntities(50,50,50); // searches in a 100100100 radius around the player for other entities
	            Entity hook = null; // holds the future hook
	            for (Entity en : nearby) { // loop through entities

	                if (en.getType() == EntityType.FISHING_HOOK) { // it is a hook!
	                    //近かった場合、無視をする
	                    if (3>en.getLocation().distance(player.getLocation())) continue;
	                    hook = en;
	                    break;
	                }
	            }
	            if (hook != null) {
	                Location hookLocation = hook.getLocation(); // the location of the hook
	                player.getLocation();
	            var Vec = Util.GetVector2Loc(player.getLocation() , hook.getLocation());

	              player.setVelocity(player.getVelocity().add(Vec.multiply(2)));


	        if(Objects.isNull(hook)) return;


	            }
	        }
	}
}
