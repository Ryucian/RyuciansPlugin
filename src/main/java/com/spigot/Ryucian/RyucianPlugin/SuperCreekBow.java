package com.spigot.Ryucian.RyucianPlugin;

import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import  org.bukkit.event.player.*;

public class SuperCreekBow
{
	public static void GetSuperCreekBow(Player player)
	{
		//SuperCreekBowを作る
		ItemStack scBow = new ItemStack(Material.BOW,1);
	    ItemMeta scBowMeta = scBow.getItemMeta();
	    scBowMeta.addEnchant(Enchantment.ARROW_FIRE, 10, true);
	    scBowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 10, true);
	    scBowMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 10, true);
	    scBowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
	    scBowMeta.addEnchant(Enchantment.MENDING, 1, true);
	    scBowMeta.setDisplayName("SuperCreekBow");
	    scBow.setItemMeta(scBowMeta);

        PlayerInventory inventory = player.getInventory();
        inventory.addItem(scBow);
        //inventory.addItem(new ItemStack(Material.SPECTRAL_ARROW,64));
	}

    public static void onProjectileHit(ProjectileHitEvent projectileHitEvent)
    {
    	/*
    	//放たれたのが矢系統でなければ処理しない
    	if(!(projectileHitEvent.getEntity() instanceof AbstractArrow)) return;
    	AbstractArrow arrow = (AbstractArrow) projectileHitEvent.getEntity();

    	//撃ったのがプレイヤーでなければ処理しない
    	if(!(projectileHitEvent.getEntity().getShooter() instanceof Player)) return;
    	Player player = (Player)projectileHitEvent.getEntity().getShooter();


    	//撃った弓が「SuperCreekBow」でなければ処理しない
    	if(!bowName.equalsIgnoreCase("SuperCreekBow")) return;

    	//一番近い敵を探す
    	var entityList = player.getNearbyEntities(30, 30, 30);
    	player.sendMessage("Entity:"+entityList.size()+";");
    	Double distance = (double) -1;
    	Entity nearestEntity = null;
    	for(Entity entity:entityList)
    	{
    		//モンスターでないエンティティは無視
    		if(!(entity instanceof Creature)) continue;

        	player.sendMessage("Monster");

    		//モンスターとの距離を取得
    		Double tempDist = player.getLocation().distance(entity.getLocation());
    		if(distance.equals((double)-1) || distance<tempDist)
    		{
    			distance = tempDist;
    			nearestEntity = entity;
    		}

    	}

    	//矢の場所を一番近い敵にテレポートする
    	event.getProjectile().teleport(nearestEntity.getLocation().add(0,1,0));
    	event.getProjectile().setVelocity(new Vector(0,-1,0));
    	//event.getProjectile().getLocation(nearestEntity.getLocation());
    	*/
    }

    /**
     * プレイヤーがオブジェクトや空気を右クリックする時に呼び出される。
     * それぞれの手に対して呼び出される可能性がある。
     * @param e
     */
    public static void onPlayerInteract(PlayerInteractEvent e)
    {
        //クリックした先が空気か普通のブロックでない場合は処理しない
        if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;


        Player p = e.getPlayer();

        //持っているものが弓でなければ処理しない
        //また持っているものの名前がSuperCreekBowでない場合は処理しない
        if( !(p.getInventory().getItemInMainHand().getType().equals(Material.BOW)) ) return;
        if( !p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("SuperCreekBow") ) return;

        //矢を持っている場合は処理を終了する
        for(ItemStack itemStack :p.getInventory().getContents())
        {
        	//Nullの場合は次のインベントリを見る
        	if(Objects.isNull(itemStack)) continue;

        	if(itemStack.getType().equals(Material.ARROW)) return;
        	if(itemStack.getType().equals(Material.SPECTRAL_ARROW)) return;
        	if(itemStack.getType().equals(Material.TIPPED_ARROW)) return;
        }

        //矢を持っていない場合は矢を与える
        p.getInventory().addItem(new ItemStack(Material.SPECTRAL_ARROW,1));
    }

}
