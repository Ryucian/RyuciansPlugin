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
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.TradeSelectEvent;

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
		else if(cmd.getName().equalsIgnoreCase("CherryDash")) CherryDash.Enable(player);
		else if(cmd.getName().equalsIgnoreCase("CherryJump")) CherryDash.EnableJump(player);
		else if(cmd.getName().equalsIgnoreCase("SpawnTrader1")) RyucianVillager.SpawnTrader(player);
		return true;
	}

	// /**
	//  * ブロックが爆発するときに呼び出されるらしい
	//  * @param e
	//  */
	// @EventHandler
	// public void onBlockExplodeEvent(BlockExplodeEvent e)
	// {
	// }

	// /**
	//  * 爆発が起きる前に呼び出されるイベント？
	//  * @param e
	//  */
	// @EventHandler
	// public void onExplosionPrime(ExplosionPrimeEvent e)
	// {
	// }

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e)
	{
		var entityType = e.getEntityType();
		if(entityType == EntityType.CREEPER
		  || entityType == EntityType.GHAST)
		{
			e.blockList().clear();
		}
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

	/**
	 * プレイヤーが動く度に発生するイベント
	 * @param e
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		CherryDash.onPlayerMove(player);
	}

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
    	Shop.onPlayerInteractEntity(e);

		if(!(e.getRightClicked() instanceof Villager)) return;

		var targetVillager = (Villager) e.getRightClicked();


		var recipes = targetVillager.getRecipes();
        var itemStack = new ItemStack(Material.DIAMOND, 1);
        
        var recipe = new MerchantRecipe(itemStack, 1, 999, true, 10, 1, 0, 0);

		recipes.add(recipe);

		targetVillager.setRecipes(recipes);

    	return;
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
    	test.onPlayerInteract(e);

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

    		//ピグリンにゾンビ化への免疫を付与する
    		piglin.setImmuneToZombification(true);

    	}
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e)
    {
    	//プレイヤーが敵を倒したときの処理
    	//エンティティを倒したのがプレイヤーでなければ処理しない
    	if(Objects.isNull(e.getEntity().getKiller())) return;

    	//プレイヤーを取得
		var player = e.getEntity().getKiller();

    	var entity = e.getEntity();
		
		e.getDrops().add(new ItemStack(Material.PINK_PETALS,1));

    	//スポーンしたやつがピグリンの場合
    	if(e.getEntityType().equals(EntityType.PIGLIN))
    	{
    		//20%の確率で金塊をドロップ品に追加
    		if(Util.isLottery(5,2))
    		{
    			e.getDrops().add(new ItemStack(Material.GOLD_NUGGET,1));
    		}
    		//1%の確率でドロップ品に金インゴットを追加
    		else if(Util.isLottery(100,50))
    		{
        		e.getDrops().add(new ItemStack(Material.GOLD_INGOT,1));
    		}

    		//プレイヤーのマナを５０回復させる
    		Magic.AddMagicPoint(player,50);

    	}
    	else if(e.getEntityType().equals(EntityType.PILLAGER))
    	{
    		//プレイヤーのマナを５０回復させる
    		Magic.AddMagicPoint(player,50);
    	}



    }

    /**
     * エンティティがダメージを受けたとき
     * @param e
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e)
    {
    	test.onDamage(e);
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
    	else if(msg.equalsIgnoreCase("翠さん助けて"))
    	{
    		SuperCreekBow.GetSuperCreekBow(player);
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
    	//各種メッセージコマンドを一旦すべて廃止
    	return;
    }

    /**
     *
     * @param e
     */
    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e)
    {
    	//インベントリクリックしていない場合は処理しない
    	if( Objects.isNull(e.getClickedInventory()) ) return;

    	//printInventoryClickEventData(e);

    	//インベントリの主がプレイヤーでなければ処理しない
    	if((e.getWhoClicked() instanceof Player) == false) return;
    	var player = (Player)e.getWhoClicked();

    	//printInventoryClickEventData(e);

    	//クリックしたインベントリ
    	Shop.onInventoryClick(e, player);

    }

    /**
     *
     * @param e
     */
    @EventHandler
    public static void onInventoryClose(InventoryCloseEvent e)
    {
    	Shop.onInventoryClose(e);
    }

    public static void printInventoryClickEventData(InventoryClickEvent e)
    {
    	System.out.println("アイテム情報");
    	System.out.println("getCursor（クリック前のアイテム）:"+e.getCursor().getType().name());
    	if(Objects.isNull(e.getCurrentItem()))
    	{
        	System.out.println("getCurrentItem（クリック後のアイテム）:なし");
    	}
    	else
    	{
        	System.out.println("getCurrentItem（クリック後のアイテム）:"+e.getCurrentItem().getType().name());
    	}
    	System.out.println("インベントリ情報");
    	System.out.println("getInventory:"+e.getInventory().getType().name());
    	System.out.println("getClickedInventory:"+e.getClickedInventory().getType().name());
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
