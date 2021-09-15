package com.spigot.Ryucian.RyucianPlugin;

import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
 import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public  class Pero
{
	/**
	 * プレイヤーがダメージを受けたときの処理
	 * @param e
	 */
	static void onPlayerDamage(EntityDamageByEntityEvent e)
	{

		//エンティティをプレイヤーとして扱う
		Player player = (Player)e.getEntity();

		//ダメージを与えてきたやつを取得する
		var enemy = e.getDamager();

		//エンティティリストから猫型でオーナがダメージを受けてる猫を探す
		Cat pero = Pero.GetPero(player);

		//ペロが見つかってなければ処理を終了する
		if(Objects.isNull(pero)) return;

		//ペロヒールを行う
		Pero.PeroHeal(player,pero);

		//ペロショットを行う
		Pero.PeroShot(enemy,pero);
	}

	/**
	 * 猫がダメージを受けたときの処理
	 * onEntityDamageByEntityEventから呼び出される
	 * @param e
	 */
	public static void onDamageByEntityEvent(EntityDamageByEntityEvent e)
	{
		//引数からエンティティを取得
		Entity eventEntity = e.getEntity();

		//ダメージを受けた対象が猫でない場合は処理を行わない
		if(!(eventEntity instanceof Cat)) return;
		var cat = (Cat) eventEntity;

		//ダメージを受けた対象がペロでない場合は処理を行わない
		if(!isPero(cat)) return;

		//ペロショットを行う
		PeroShot(e.getDamager(),cat);
	}

	/**
	 * プレイヤーがエンティティにダメージを与えたとき呼び出される
	 * onEntityDamageByEntityEventから呼び出される
	 * @param e
	 */
	public static void onEntityDamageByPLayerEvent(EntityDamageByEntityEvent e)
	{
		//ダメージを与えた対象がプレイヤーでない場合は処理を行わない
		if(!(e.getDamager() instanceof Player)) return;
		var player = (Player)e.getDamager();

		//ペロを取得、取得できなければ処理を終了する
		var pero = GetPero(player);
		if(Objects.isNull(pero)) return;

		//ペロショットを行う
		Entity enemy = e.getEntity();
		PeroShot(enemy,pero);
	}

	/**
	 * ペロの投擲物が何かにあたったときの処理
	 * @param projectileHitEvent
	 */
	public static void onProjectileHit(ProjectileHitEvent projectileHitEvent)
	{
		Entity eventEntity = projectileHitEvent.getEntity();

		//投擲物が雪玉でない場合は処理しない
		if(!(eventEntity instanceof Snowball)) return;
		Snowball snowball = (Snowball)eventEntity;

		//投擲物の発射者が猫でない場合は処理しない
		if(!(snowball.getShooter() instanceof Cat)) return;
		Cat pero = (Cat)snowball.getShooter();

		//投擲物にあったのがエンティティでない（ブロックである）なら処理しない
		if( Objects.isNull(projectileHitEvent.getHitEntity())) return;

		//あたったのがプレイヤーなら処理しない
		if(projectileHitEvent.getHitEntity() instanceof Player) return;
		Entity targetEntity = projectileHitEvent.getHitEntity();

		//あたった相手が生きていないならば処理しない
		if(!(projectileHitEvent.getHitEntity() instanceof LivingEntity)) return;
		LivingEntity targetLivingEntity = (LivingEntity)targetEntity;

		targetLivingEntity.damage(5, pero);

	}

    /**
     * ペロヒールを行います
     * @param player
     * @param pero
     */
    static void PeroHeal(Player player,Cat pero)
    {
		//ペロが見つかってなければ処理を終了する
		if(Objects.isNull(pero)) return;

		//N分の1の確率で行動
		if(!Util.isLottery(3)) return;

		player.sendMessage("ペロが回復魔法を唱えた");
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10, 10));

    }

    /**
     * ペロヒールを行います
     * @param player
     * @param pero
     */
    static void PeroHeal_Old(Player player,Cat pero)
    {
		//ペロが見つかってなければ処理を終了する
		if(Objects.isNull(pero)) return;

		//持続回復する残留ポーションを作る
		ItemStack itemStack = new ItemStack(Material.LINGERING_POTION);
		PotionMeta potionMeta = (PotionMeta)itemStack.getItemMeta();
		potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 600*8, 0), true);
		potionMeta.setColor(Color.BLUE);
		itemStack.setItemMeta(potionMeta);

		//残留ポーションをペロからプレイヤーに向かって投げる
		Vector v = Util.GetVector2Loc(pero.getLocation(),player.getLocation());
    	World world = player.getWorld();
    	ThrownPotion item=(ThrownPotion) world.spawnEntity(pero.getLocation().add(0,1,0),EntityType.SPLASH_POTION);
		item.setItem(itemStack);
		item.setShooter(pero);
		item.setVelocity(v);
    }

    /**
     * ペロショットを行います
     * @param enemy
     * @param pero
     */
    static void PeroShot(Entity enemy,Cat pero)
    {
    	//プレイヤーには攻撃しない
    	if(enemy instanceof Player) return;

    	//ペロ同士は殴らない
    	if(enemy instanceof Cat)
    	{
    		Cat cat = (Cat)enemy;
    		if(isPero(cat)) return;
    	}

    	//距離が近ければ攻撃する
    	/*
    	Double distance = pero.getLocation().distance(enemy.getLocation());
    	if(distance<7)
    	{
        	pero.attack(enemy);
    	}
    	*/

    	var world = pero.getWorld();
		Vector v = Util.GetVector2Loc(pero.getLocation(),enemy.getLocation());
		Snowball sb =  (Snowball)world.spawnEntity(pero.getLocation().add(0, 1, 0), EntityType.SNOWBALL);
		sb.setShooter(pero);
		sb.setVisualFire(true);
		sb.setVelocity(v);




    	/*
    	var world = pero.getWorld();
		Vector v = GetVector2Loc(pero.getLocation(),enemy.getLocation());
		Arrow arrow = world.spawnArrow(pero.getLocation().add(0, 1, 0), v, 0.5F, 0);
		arrow.setShooter(pero);
		arrow.setColor(Color.BLACK);
		arrow.setTicksLived(1);
		*/
    }

    /**
     * その猫がペロかどうかを判定する
     * @param cat
     * @return
     */
	public static boolean isPero(Cat cat)
    {
		//猫に名前がつけられていなければペロではない
		if(Objects.isNull(cat.getCustomName())) return false;

		//猫の名前がペロでなければ処理しない
		//getServer().broadcastMessage("Cat Name Is" + cat.getCustomName());
		if(!cat.getCustomName().equals("ペロ")) return false;

		//猫の色が黒でなければ処理しない
		//getServer().broadcastMessage("Cat Color Is" + cat.getCatType().toString() );
		if(cat.getCatType() != Cat.Type.ALL_BLACK) return false;


		return true;
    }

	/**
	 * プレイヤーが飼っている”ペロ”という黒猫を取得する関数
	 * @param player
	 * @return
	 */
    public static Cat GetPero(Player player)
	{
		var entityList = player.getWorld().getEntities();
		//エンティティリストから猫型でオーナがダメージを受けてる猫を探す
		Cat pero = null;
		for(Entity entity:entityList)
		{
			//猫型でないならば処理しない
			if(!(entity instanceof Cat)) continue;
			Cat cat = (Cat) entity;

			//猫の飼い主がダメージを受けた人でなければ処理しない
			if(cat.getOwner() != player) continue;

			//猫がペロでなければ処理しない
			if(!isPero(cat)) continue;

			//上の条件に当てはまらない場合はペロなので検索終了とする
			pero = cat;
			break;
		}
		return pero;
	}

    /**
     * ペロを召喚する
     * @param sender
     * @return 召喚に成功したかどうか
     */
    public static boolean SummonPero(Player player)
    {
    	Cat pero = GetPero(player);

    	//ペロを既に飼っている場合は処理しない
    	if(!Objects.isNull(pero)) return false;

    	var playersLocation = player.getLocation();

    	var world = player.getWorld();

    	//ペロを召喚する
    	pero=(Cat)world.spawnEntity(playersLocation,EntityType.CAT);
    	pero.setCatType(Cat.Type.ALL_BLACK);
    	pero.setCustomName("ペロ");
    	pero.setOwner(player);
    	pero.setMaxHealth(100);
    	pero.setHealth(100);
		return true;
    }


}
