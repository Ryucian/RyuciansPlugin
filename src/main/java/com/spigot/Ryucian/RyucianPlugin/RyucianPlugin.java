package com.spigot.Ryucian.RyucianPlugin;

import java.math.BigDecimal;
import java.util.Objects;

import org.bukkit.Bukkit;
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
    	else if(cmd.getName().equalsIgnoreCase("GetRyucianBook")) Magic.GetBookByCommand(player,args[0]);
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

		Entity eventEntity = projectileHitEvent.getEntity();

		//投擲物にあったのがエンティティでない（ブロックである）なら処理しない
		if( Objects.isNull(projectileHitEvent.getHitEntity())) return;

		//あたったのがプレイヤーなら処理しない
		if(projectileHitEvent.getHitEntity() instanceof Player) return;
		Entity targetEntity = projectileHitEvent.getHitEntity();

		//あたった相手が生きていないならば処理しない
		if(!(projectileHitEvent.getHitEntity() instanceof LivingEntity)) return;
		LivingEntity targetLivingEntity = (LivingEntity)targetEntity;

		//投擲物が雪玉でない場合は処理しない
		if(!(eventEntity instanceof Snowball)) return;
		Snowball snowball = (Snowball)eventEntity;

    	Pero.onProjectileHit(projectileHitEvent);


    	Magic.onSnowBallHit(snowball,targetLivingEntity);
    	//SuperCreekBow.onProjectileHit(projectileHitEvent);
    }

    /**
     * プレイヤーがエンティティを右クリックする時に呼び出される。
     * @param e
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
    {
    	Magic.onPlayerInteractEntity(e);
    }



    /**
     * プレイヤーがオブジェクトや空気を右クリックする時に呼び出される。
     * それぞれの手に対して呼び出される可能性がある。
     * @param e
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        //クリックした先が空気か普通のブロックでない場合は処理しない
        if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;

    	SuperCreekBow.onPlayerInteract(e);

    	Magic.onPlayerInteract(e);

    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent e)
    {

    	//生き物とブロックの衝突でない場合は処理しない
    	if(Objects.isNull(e.getBlock())) return;

    	//生き物が歪んだ感圧板を踏んで居たらそれを無効にする
    	if (e.getBlock().getType() == Material.WARPED_PRESSURE_PLATE)
    	{
            e.setCancelled(true);
        }
    }


    /**
     * プレイヤーがポーションや牛乳を飲んだとき
     * @param e
     */
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent e)
    {
    	Magic.onPlayerItemConsume(e);
    }

    /**
     * 生き物がスポーンしたとき
     * @param e
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e)
    {
    	var entity = e.getEntity();

    	//スポーンしたやつがピグリンの場合
    	if(e.getEntityType().equals(EntityType.PIGLIN))
    	{
    		//スポーンしたエンティティをピグリン型に変換
    		var piglin = (Piglin)entity;
    		piglin.setImmuneToZombification(true);
    	}
    }

    /**
     * プレイヤーがチャットを打ち込んだとき
     * @param event
     */
    @EventHandler
    public void OnPlayerChat(PlayerChatEvent event)
    {
    	//各種メッセージコマンドを一旦すべて廃止
    	return;
    	/*
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
    	else if(msg.equalsIgnoreCase("マナ確認"))
    	{
    		player.sendMessage("MP："+Magic.GetMagicPoint(player));
    		event.setCancelled(true);
    	}
    	else if(msg.equalsIgnoreCase("マナ設定"))
    	{
    		Magic.SetMagicPoint(player,1000);
    		player.sendMessage("MP："+Magic.GetMagicPoint(player));
    		event.setCancelled(true);
    	}
    	else if(msg.equalsIgnoreCase("マナ加算"))
    	{
    		Magic.AddMagicPoint(player,1000);
    		player.sendMessage("MP："+Magic.GetMagicPoint(player));
    		event.setCancelled(true);
    	}
    	else if(msg.equalsIgnoreCase("火矢の書"))
    	{
    		Magic.GetArrowWand(player);
    		event.setCancelled(true);
    	}
    	else if(msg.equalsIgnoreCase("氷矢の書"))
    	{
    		Magic.GetIceArrow(player);
    		event.setCancelled(true);
    	}
    	else if(msg.equalsIgnoreCase("跳躍の書"))
    	{
    		Magic.GetJumpBoostBook(player);
    		event.setCancelled(true);
    	}
    	else if(msg.equalsIgnoreCase("魔力回復"))
    	{
    		Magic.GetManaGainPotion(player);
    		event.setCancelled(true);
    	}
    	*/
    }

    /**
     * 何らかのエンティティがブロックからダメージを受けたとき
     * 落下・マグマなど
     * @param e
     */
    @EventHandler
    public static void onEntityDamageByBlocks(EntityDamageByBlockEvent e)
    {
    	//ダメージを受けたエンティティを取得する
    	var entity = e.getEntity();

    	//ダメージを受けたエンティティがプレイヤーでなければ処理しない
    	if( !(entity instanceof Player) ) return;

    	//ダメージを受けたエンティティをプレイヤー型に変換する
    	var player = (Player)entity;

    	//ダメージを受けた原因を取得する
    	var cause = e.getCause();
   }


}
