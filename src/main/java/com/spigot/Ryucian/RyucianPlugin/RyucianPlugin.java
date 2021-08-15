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
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class RyucianPlugin extends JavaPlugin implements Listener
{

    @Override
    public void onEnable()
    {
		// イベントリスナーの登録
		getServer().getPluginManager().registerEvents((Listener) this,this);
    }

	/**
	 * コマンドが実行されたら呼び出される
	 */
    @Override
	public boolean onCommand(CommandSender sender,Command cmd,String commandLabel,String[] args)
	{

    	if(args[0].equalsIgnoreCase("SummonPero"))
    	{
    		SummonPero(sender);
    		return true;
    	}
    	else if(args[0].equalsIgnoreCase("PeroHeal"))
    	{
    		Player player = (Player) sender;
    		Cat pero = GetPero(player);
    		PeroHeal(player,pero);
    		return true;
    	}
    	else
    	{
    		getServer().broadcastMessage(cmd.getName() + " args:"+String.join(" " , args) );
    		getServer().broadcastMessage("test1");
    	}

    	return true;
	}

    /**
     * その猫がペロかどうかを判定する
     * @param cat
     * @return
     */
    private boolean isPero(Cat cat)
    {
		//猫の名前がペロでなければ処理しない
		//getServer().broadcastMessage("Cat Name Is" + cat.getCustomName());
		if(!cat.getCustomName().equals("ペロ")) return false;

		//猫の色が黒でなければ処理しない
		//getServer().broadcastMessage("Cat Color Is" + cat.getCatType().toString() );
		if(cat.getCatType() != Cat.Type.ALL_BLACK) return false;


		return true;
    }

    /**
     * ペロを召喚する
     * @param sender
     * @return
     */
    private boolean SummonPero(CommandSender sender)
    {
    	Player player = (Player) sender;

    	var playersLocation = player.getLocation();

    	var world = player.getWorld();

    	//ペロを召喚する
    	Cat pero=(Cat)world.spawnEntity(playersLocation,EntityType.CAT);
    	pero.setCatType(Cat.Type.ALL_BLACK);
    	pero.setCustomName("ペロ");
    	pero.setOwner(player);
    	pero.setMaxHealth(100);
    	pero.setHealth(100);
		return true;
    }

	/**
	 * エンティティがエンティティからダメージを受けるとき呼び出される
	 * （プレイヤーがダメージを受けたときは含まれない）
	 *
	 * @param e
	 */
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e)
	{
		//引数からエンティティを取得
		Entity eventEntity = e.getEntity();

		//ダメージを受けた対象が猫でない場合は処理を行わない
		if(eventEntity instanceof Cat)
		{
			onPeroDamageByEntityEvent(e);
			return;
		}
		else if(e.getDamager() instanceof Player)
		{
			onEntityDamageByPLayerEvent(e);
		}
	}

	/**
	 * プレイヤーがエンティティからダメージを与えたとき呼び出される
	 * onEntityDamageByEntityEventから呼び出される
	 * @param e
	 */
	@EventHandler
	public void onEntityDamageByPLayerEvent(EntityDamageByEntityEvent e)
	{
		//ダメージを与えた対象がプレイヤーでない場合は処理を行わない
		if(!(e.getDamager() instanceof Player)) return;

		//引数からプレイヤーと殴った相手を取得
		Entity enemy = e.getEntity();
		var player = (Player)e.getDamager();

		//ペロを取得、取得できなければ処理を終了する
		var pero = GetPero(player);
		if(Objects.isNull(pero)) return;

		//ペロショットを行う
		PeroShot(enemy,pero);
	}

	/**
	 * 猫がダメージを受けたときの処理
	 * onEntityDamageByEntityEventから呼び出される
	 * @param e
	 */
	public void onPeroDamageByEntityEvent(EntityDamageByEntityEvent e)
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
	 * プレイヤーが飼っている”ペロ”という黒猫を取得する関数
	 * @param player
	 * @return
	 */
	private Cat GetPero(Player player)
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
     * プレイヤーがダメージを受けたときに呼び出される
     * @param e
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e)
    {
		//引数からエンティティを取得
		Entity eventEntity = e.getEntity();

		//ダメージを受けた対象がプレイヤーでない場合は処理を行わない
		if(!(eventEntity instanceof Player)) return;

		//エンティティをプレイヤーとして扱う
		Player player = (Player)eventEntity;

		//ダメージを与えてきたやつを取得する
		var enemy = e.getDamager();

		//エンティティリストから猫型でオーナがダメージを受けてる猫を探す
		Cat pero = GetPero(player);

		//ペロが見つかってなければ処理を終了する
		if(Objects.isNull(pero)) return;

		//ペロヒールを行う
		PeroHeal(player,pero);

		//ペロショットを行う
		PeroShot(enemy,pero);
	}

    /**
     * ペロヒールを行います
     * @param player
     * @param pero
     */
    private void PeroHeal(Player player,Cat pero)
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
		Vector v = GetVector2Loc(pero.getLocation(),player.getLocation());
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
    private void PeroShot(Entity enemy,Cat pero)
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
    	Double distance = pero.getLocation().distance(enemy.getLocation());
    	if(distance<7)
    	{
        	pero.attack(enemy);
    	}
    	//getServer().broadcastMessage("distance:"+distance.toString());

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
     * 場所originから場所targetまでのベクトルを取得する
     * @param origin
     * @param target
     * @return
     */
	private Vector GetVector2Loc(Location origin,Location target)
	{
		return target.toVector().subtract(origin.toVector()).normalize();
	}

















}
