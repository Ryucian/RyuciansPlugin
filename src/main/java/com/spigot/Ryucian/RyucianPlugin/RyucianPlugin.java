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
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
		//以下のコマンドはプレイヤーでない場合は処理しない
		if(!(sender instanceof Player)) return true;
		Player player = (Player) sender;
    	if(     cmd.getName().equalsIgnoreCase("SummonPero")) 	Pero.SummonPero(player);
    	else if(cmd.getName().equalsIgnoreCase("SummonUsa")   ) Usagi.SummonUsa(player,this);
    	else if(cmd.getName().equalsIgnoreCase("ComeOnUsa")   ) Usagi.ComeOnUsa(player);
    	else if(cmd.getName().equalsIgnoreCase("OgyaBow")     ) SuperCreekBow.GetSuperCreekBow(player);
    	else if(cmd.getName().equalsIgnoreCase("SteakTabetai")) Util.GetFreshSteak(player);
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
			Pero.onDamageByEntityEvent(e);
			return;
		}
		else if(e.getDamager() instanceof Player)
		{
			onEntityDamageByPLayerEvent(e);
		}
	}

	/*
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();

		Rabbit usa3 = Usagi.GetUsa3(player);

		if(Objects.isNull(usa3)) return;

		//プレイヤーとうさぴょんの距離が近い場合は処理しない
		if(usa3.getLocation().distance(player.getLocation()) < 5) return;

		//うさぴょんがプレイヤーを向く
		usa3.setRotation(player.getLocation(usa3.getLocation()).getYaw(),usa3.getLocation().getPitch());

		//うさぴょんをプレイヤーに近づける
		usa3.setVelocity(Util.GetVector2Loc(usa3.getEyeLocation(), player.getLocation()));

	}
	*/

	/**
	 * プレイヤーがエンティティにダメージを与えたとき呼び出される
	 * onEntityDamageByEntityEventから呼び出される
	 * @param e
	 */
	@EventHandler
	public void onEntityDamageByPLayerEvent(EntityDamageByEntityEvent e)
	{
		Pero.onEntityDamageByPLayerEvent(e);
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

		//ペロの行動
		Pero.onPlayerDamage(e);

		//うさこの行動を行う
		Usagi.onPlayerDamage(e);
	}

    /**
     * 投擲物が何かにあたった場合に呼び出される
     * @param projectileHitEvent
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent projectileHitEvent)
    {
    	Pero.onProjectileHit(projectileHitEvent);
    	//SuperCreekBow.onProjectileHit(projectileHitEvent);
    }

    /**
     * プレイヤーがオブジェクトや空気を右クリックする時に呼び出される。
     * それぞれの手に対して呼び出される可能性がある。
     * @param e
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
    	SuperCreekBow.onPlayerInteract(e);
    }

    /**
     * プレイヤーがチャットを打ち込んだとき
     * @param event
     */
    @EventHandler
    public void OnPlayerChat(PlayerChatEvent event)
    {
    	String msg = event.getMessage();
    	Player player = event.getPlayer();

    	if(msg.equalsIgnoreCase("うさこ人参頂戴"))
    	{
    		Usagi.GiveCarrot(Usagi.GetUsako(player),player,false);
    		event.setCancelled(true);
    	}
    	else if(msg.equalsIgnoreCase("みんな人参頂戴"))
    	{
    		var entityList = player.getWorld().getEntities();
    		//エンティティリストから猫型でオーナがダメージを受けてる猫を探す
    		for(Entity entity:entityList)
    		{
    			//猫型でないならば処理しない
    			if(!(entity instanceof Rabbit)) continue;
    			Rabbit rabbit = (Rabbit) entity;
        		Usagi.GiveCarrot(rabbit,player,false);
    		}
    		event.setCancelled(true);
    	}
    	else if(msg.equalsIgnoreCase("ステーキ食べたい") || msg.equalsIgnoreCase("ステーキたべたい"))
    	{
    		Util.GetFreshSteak(player);
    	}
    }
}
