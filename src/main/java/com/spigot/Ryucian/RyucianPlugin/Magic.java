package com.spigot.Ryucian.RyucianPlugin;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.EssentialsUserConf;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserData;
import net.ess3.api.MaxMoneyException;
import com.earth2me.essentials.storage.*;

import net.ess3.api.MaxMoneyException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

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
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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


public class Magic
{

	final static String SCORE_BOARD_ID="RyucianMP";
	final static String MAGIC_POINT="MagicPoint";
	final static String FIRE_ARROW_BOOK="火矢の書";
	final static String ICE_ARROW_BOOK="氷矢の書";
	final static String JUMP_BOOST_BOOK="跳躍の書";
	final static String SUMMON_PERO_BOOK="ペロの書";
	final static String ONIKU_TABETAI_BOOK="焼き肉の書";
	final static int POTION_PRICE = 1000;
	final static int GACHA_PRICE = 500;
	final static String POTION_STORE_NAME="ポーション屋さん("+POTION_PRICE+"$)";
	final static String BOOK_GACHA_STORE_NAME="魔導書ガチャ屋さん";

	final static Map<String, Integer> MAGIC_COST = new HashMap<String, Integer>() {
    {
    	put(FIRE_ARROW_BOOK,10);
    	put(ICE_ARROW_BOOK,10);
    	put(JUMP_BOOST_BOOK,10);
    	put(SUMMON_PERO_BOOK, 300);
    	put(ONIKU_TABETAI_BOOK,300);
    }};

	private static Essentials ess;

	private static LocalDateTime onPlayerInteractEntityTime = LocalDateTime.now();

	static
	{
		ess = (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");
	}

	/**
	 * ユーザのお金を加算する
	 * @param player
	 * @param money 加算するお金（お金を引く場合はマイナスを設定）
	 */
	public static void AddMoney(Player player,long money)
	{
		var user = ess.getUser(player);
		var nowMoney = user.getMoney();
		try
		{
			user.setMoney(nowMoney.add(BigDecimal.valueOf(money)) );
			user.save();
		}
		catch (MaxMoneyException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * ユーザのお金を加算する
	 * @param player
	 * @param money 加算するお金（お金を引く場合はマイナスを設定）
	 */
	public static long GetMoney(Player player)
	{
		var user = ess.getUser(player);
		return user.getMoney().toBigInteger().longValue();
	}


    /**
     * プレイヤーがエンティティを右クリックする時に呼び出される。
     * @param e
     */
    public static void onPlayerInteractEntity(PlayerInteractEntityEvent e)
    {
    	//0.5秒以内に同一処理が実行されている場合は処理しない
    	if(Math.abs(ChronoUnit.MILLIS.between(onPlayerInteractEntityTime,LocalDateTime.now()))<500)
    	{
    		//System.out.println("実行間隔:"+ChronoUnit.SECONDS.between(LocalDateTime.now(), onPlayerInteractEntityTime));
    		return;
    	}

    	var entity = e.getRightClicked();

    	//右クリックされたエンティティに名前がついていない場合は処理しない
    	if(Objects.isNull(entity.getCustomName())) return;

    	//エンティティが生きてるエンティティでなければ処理しない
		if(!(entity instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity)entity;

    	//名前がポーション屋さんの場合
    	if(entity.getCustomName().equalsIgnoreCase(POTION_STORE_NAME))
    	{
	    	//プレイヤーのお金が少なすぎる場合は処理しない
	    	var player = e.getPlayer();
	    	var money = GetMoney(player);
	    	if(money<POTION_PRICE)
	    	{
	    		player.sendMessage("所持金が足りないようだ。（所持金："+money+"$）");
	    		return;
	    	}

	    	//プレイヤーのお金を減らしてポーションを与える
	    	player.getWorld().dropItem(entity.getLocation(), GetManaPotionData());
	    	AddMoney(player,-POTION_PRICE);
    	}
    	else if(entity.getCustomName().equalsIgnoreCase(BOOK_GACHA_STORE_NAME))
    	{

	    	//プレイヤーのお金が少なすぎる場合は処理しない
	    	var player = e.getPlayer();
	    	var money = GetMoney(player);
	    	if(money<GACHA_PRICE)
	    	{
	    		player.sendMessage("所持金が足りないようだ。（一回："+GACHA_PRICE+"$）");
	    		return;
	    	}

	    	//プレイヤーのお金を減らしてポーションを与える
	    	int rnd = Util.getRandom(MAGIC_COST.size());
	    	int cnt = 1;
	    	for(String key : MAGIC_COST.keySet())
	    	{
	    		if(rnd == cnt)
	    		{
	    			var bookname = ChatColor.RED + key + ChatColor.RESET;
	    			var bookLore = new ArrayList<String>();
	    			bookLore.add("消費MP:"+MAGIC_COST.get(key));
	    			bookLore.add(player.getName()+"がガチャで当てたやつ");
	    	    	//本をドロップする
	    	    	player.getWorld().dropItem(entity.getLocation(), GetBookData(bookname,bookLore));
	    			break;
	    		}
	    		cnt++;
	    	}
	    	AddMoney(player,-GACHA_PRICE);
    	}
    	//どちらにも該当しない場合は処理終了
    	else {
    		return;
    	}

    	//最後に実行した時間を設定する
    	onPlayerInteractEntityTime = LocalDateTime.now();
    }

	/**
	 * 魔力を設定する（管理者用）
	 * @param player
	 * @param mp
	 */
	public static void SetMagicPoint(Player player,long mp)
	{
		UUID uuid = player.getUniqueId();
		var user = ess.getUser(uuid);
		user.setConfigProperty(MAGIC_POINT, BigDecimal.valueOf(mp) );
		user.save();
	}


	/**
	 * 魔力を加算する（魔法を使ったとき・回復薬を飲んだときに使う）
	 * @param player
	 * @param addMp
	 */
	public static void AddMagicPoint(Player player,int addMp)
	{
		AddMagicPoint(player,BigDecimal.valueOf(addMp));
	}

	/**
	 * 魔力を加算する（魔法を使ったとき・回復薬を飲んだときに使う）
	 * @param player
	 * @param addMp
	 */
	public static void AddMagicPoint(Player player,Double addMp)
	{
		AddMagicPoint(player,BigDecimal.valueOf(addMp));
	}

	/**
	 * 魔力を加算する（魔法を使ったとき・回復薬を飲んだときに使う）
	 * @param player
	 * @param addMp
	 */
	public static void AddMagicPoint(Player player,BigDecimal addMp)
	{
		UUID uuid = player.getUniqueId();
		var user = ess.getUser(uuid);
		var confMap = user.getConfigMap();
		var mp_temp = confMap.get(MAGIC_POINT);

		BigDecimal mp;
		if(Objects.isNull(mp_temp))
		{
			mp = BigDecimal.valueOf(0);
		}
		else if(mp_temp.getClass().getSimpleName().equalsIgnoreCase("Double"))
		{
			mp = BigDecimal.valueOf((Double)mp_temp);
		}
		else
		{
			mp = (BigDecimal)mp_temp;
		}
		BigDecimal beforeMp = mp;
		mp = mp.add(addMp);
		user.setConfigProperty(MAGIC_POINT, mp );
		user.save();
		upsetMpBoard(player);
	}

	/**
	 * スコアボード表示用オブジェクトを取得する
	 * @return
	 */
	public static Objective GetSBObjective()
	{
		// メインスコアボードを取得します。
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        // オブジェクティブが既に登録されているかどうか確認し、
        // 登録されていないなら新規作成します。
        Objective objective = board.getObjective(SCORE_BOARD_ID);
        if ( objective == null )
        {
        	objective = board.registerNewObjective(SCORE_BOARD_ID, SCORE_BOARD_ID,"Status");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        return objective;
	}

	/**
	 * MPスコアボード表示
	 * @return
	 */
	public static void upsetMpBoard(Player player)
	{
		Objective objective = GetSBObjective();
        player.setScoreboard(objective.getScoreboard());
        Score score = objective.getScore("MP   :");
        score.setScore(GetMagicPointInt(player));
        Score scoreMoney = objective.getScore("Money:");
        scoreMoney.setScore((int) GetMoney(player));
	}

	/**
	 * プレイヤーのマナを取得する
	 * @param player
	 * @return
	 */
	public static String GetMagicPoint(Player player)
	{
		UUID uuid = player.getUniqueId();
		var user = ess.getUser(uuid);
		var confMap = user.getConfigMap();
		return confMap.get(MAGIC_POINT).toString();
	}

	/**
	 * プレイヤーのマナを取得する
	 * @param player
	 * @return
	 */
	public static int GetMagicPointInt(Player player)
	{
		String mpstr = GetMagicPoint(player);
		double mpDbl = Double.parseDouble(mpstr);
		return (int)mpDbl;
	}

	/**
	 * 魔導書のアイテムデータを取得する
	 * @param bookName
	 * @param bookLore
	 * @return
	 */
	public static ItemStack GetBookData(String bookName,List<String> bookLore)
	{
		ItemStack scBow = new ItemStack(Material.ENCHANTED_BOOK,1);
	    ItemMeta scBowMeta = scBow.getItemMeta();
	    scBowMeta.setDisplayName(bookName);
	    scBowMeta.setLore(bookLore);
	    scBow.setItemMeta(scBowMeta);
	    return scBow;
	}

	/**
	 * プレイヤーに本を追加する
	 * @param player
	 * @param bookName
	 * @param bookLore
	 */
	public static void GetBook(Player player,String bookName,List<String> bookLore)
	{
        PlayerInventory inventory = player.getInventory();
        inventory.addItem(GetBookData(bookName,bookLore));
	}


	/**
	 * プレイヤーに本を追加する
	 * @param player
	 * @param bookName
	 */
	public static void GetBook(Player player,String bookNameNoColor)
	{
	    var bookLore = new ArrayList<String>();
	    bookLore.add("消費MP:"+MAGIC_COST.get(bookNameNoColor));
	    GetBook( player, ChatColor.RED + bookNameNoColor + ChatColor.RESET, bookLore);
	}

	/**
	 * プレイヤーに火矢の書を追加する
	 * @param player
	 */
	public static void GetArrowWand(Player player)
	{
		String bookName = ChatColor.RED + FIRE_ARROW_BOOK + ChatColor.RESET;
	    var bookLore = new ArrayList<String>();
	    bookLore.add("消費MP:10");
		GetBook(player,bookName,bookLore);
	}

	/**
	 * プレイヤーに氷矢の書を追加する
	 * @param player
	 */
	public static void GetIceArrow(Player player)
	{
		String bookName = ChatColor.RED + ICE_ARROW_BOOK + ChatColor.RESET;
	    var bookLore = new ArrayList<String>();
	    bookLore.add("消費MP:10");
		GetBook(player,bookName,bookLore);
	}

	/**
	 * プレイヤーに跳躍の書を追加する
	 * @param player
	 */
	public static void GetJumpBoostBook(Player player)
	{
		String bookName = ChatColor.RED + JUMP_BOOST_BOOK + ChatColor.RESET;
	    var bookLore = new ArrayList<String>();
	    bookLore.add("消費MP:30");
		GetBook(player,bookName,bookLore);
	}

	/**
	 * マナ回復ポーションのアイテムデータを取得する
	 * @return
	 */
	public static ItemStack GetManaPotionData()
	{
		//SuperCreekBowを作る
		ItemStack manaPotion = new ItemStack(Material.POTION,1);
	    ItemMeta scBowMeta = manaPotion.getItemMeta();
	    scBowMeta.setDisplayName(ChatColor.RED + "魔力回復のポーション" + ChatColor.RESET);
	    scBowMeta.setLocalizedName("魔力回復のポーション");
	    var lore = new ArrayList<String>();
	    lore.add("魔力を1000回復する");
	    scBowMeta.setLore(lore);
	    manaPotion.setItemMeta(scBowMeta);
	    return manaPotion;
	}

	/**
	 * マナポーションを取得する
	 * @param player
	 */
	public static void GetManaGainPotion(Player player)
	{
        PlayerInventory inventory = player.getInventory();
        inventory.addItem(GetManaPotionData());
	}

	/**
	 * 魔法で放つ雪玉を取得する
	 * @param player
	 * @param customName
	 * @return
	 */
	private static Snowball getMagicSnowball(Player player,String customName)
	{
		var world = player.getWorld();
		var plocation = player.getLocation();
    	var sb =  (Snowball)world.spawnEntity(plocation.add(0, 1, 0), EntityType.SNOWBALL);
		sb.setShooter(player);
    	sb.setCustomName(customName);
		sb.setVelocity(plocation.getDirection().normalize());
		//sb.setTicksLived(100);
		sb.setGravity(true);
		//sb.setFallDistance(1000);
		return sb;
	}

    /**
     * プレイヤーがオブジェクトや空気を右クリックする時に呼び出される。
     * それぞれの手に対して呼び出される可能性がある。
     * @param e
     */
    public static void onPlayerInteract(PlayerInteractEvent e)
    {
        Player player = e.getPlayer();

        //持っているものが本でなければ処理しない
        if( !(player.getInventory().getItemInMainHand().getType().equals(Material.BOOK))
        	&& !(player.getInventory().getItemInMainHand().getType().equals(Material.ENCHANTED_BOOK))
        	&& !(player.getInventory().getItemInMainHand().getType().equals(Material.KNOWLEDGE_BOOK)) ) return;

        //アイテム名を取得
        String handItemName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();

        //持ってる本の名前が赤文字の火球の書の場合
        if(handItemName.equalsIgnoreCase(ChatColor.RED + FIRE_ARROW_BOOK))
        {
        	final int USE_MP = 10;
        	Integer beforeMp = GetMagicPointInt(player);
        	//MPが少ない場合使わない
        	if(beforeMp<USE_MP) return;
        	var sb = getMagicSnowball(player,FIRE_ARROW_BOOK);
        	sb.setFireTicks(100);
    		AddMagicPoint(player, -USE_MP);
    		e.setCancelled(false);
        	return;
        }
        //持ってる本の名前が赤文字の氷矢の書の場合
        else if(handItemName.equalsIgnoreCase(ChatColor.RED + ICE_ARROW_BOOK))
        {
        	final int USE_MP = 10;
        	Integer beforeMp = GetMagicPointInt(player);
        	//MPが少ない場合使わない
        	if(beforeMp<USE_MP) return;
        	var sb = getMagicSnowball(player,ICE_ARROW_BOOK);
    		AddMagicPoint(player, -USE_MP);
        	return;
        }
        //持ってる本の名前が赤文字の「跳躍の書の場合」
        else if(handItemName.equalsIgnoreCase(ChatColor.RED + JUMP_BOOST_BOOK))
        {
        	final int USE_MP = 30;
        	Integer beforeMp = GetMagicPointInt(player);
        	//MPが少ない場合使わない
        	if(beforeMp<USE_MP) return;

        	player.setVelocity(player.getVelocity().add(new Vector(0,10,0)));

    		AddMagicPoint(player, -USE_MP);
        	return;
        }
        //持ってる本の名前が赤文字の「ペロの書の場合」
        else if(handItemName.equalsIgnoreCase(ChatColor.RED + SUMMON_PERO_BOOK))
        {

        	final int USE_MP = MAGIC_COST.get(SUMMON_PERO_BOOK);
        	Integer beforeMp = GetMagicPointInt(player);

        	//MPが少ない場合使わない
        	if(beforeMp<USE_MP) return;

        	var isPeroSummoned =  Pero.SummonPero(player);

        	//ペロの召喚に失敗した（既に召喚済み）の場合は魔力を引かない
        	if(isPeroSummoned == false) return;

    		AddMagicPoint(player, -USE_MP);
        	return;
        }
        //持ってる本の名前が赤文字の「焼き肉の書の場合」
        else if(handItemName.equalsIgnoreCase(ChatColor.RED + ONIKU_TABETAI_BOOK))
        {

        	final int USE_MP = MAGIC_COST.get(ONIKU_TABETAI_BOOK);
        	Integer beforeMp = GetMagicPointInt(player);

        	//MPが少ない場合使わない
        	if(beforeMp<USE_MP) return;

        	Util.GetFreshSteak(player);

    		AddMagicPoint(player, -USE_MP);

    		return;
        }



    }

    /**
     * 雪玉がプレイヤーでない生きてるエンティティにあたった場合に呼び出される
     * @param projectileHitEvent
     */
    @EventHandler
    public static void onSnowBallHit(Snowball snowBall,LivingEntity target)
    {
    	//投擲者がプレイヤーでない場合は処理しない
    	if(!(snowBall.getShooter() instanceof Player)) return;
    	var player = (Player)snowBall.getShooter();

    	//あたった生物が名前付きのうさぎの場合は処理しない
    	if((target instanceof Rabbit) && (Objects.isNull(target.getCustomName())==false)) return;

    	String sbName = snowBall.getCustomName();
    	if(sbName.equalsIgnoreCase(FIRE_ARROW_BOOK))
    	{
    		target.damage(5,player);
    		target.setFireTicks(100);
    		//var ede = new EntityDamageEvent(player,DamageCause.ENTITY_ATTACK,5);
    		//target.setLastDamageCause(ede);
    	}
    	else if(sbName.equalsIgnoreCase(ICE_ARROW_BOOK))
    	{
    		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,100,100));
    		target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,100,250));
    		//sb.addCustomEffect(new PotionEffect(PotionEffectType.SLOW,100,100), false);
    		//sb.addCustomEffect(new PotionEffect(PotionEffectType.JUMP,100,250), false);
    	}
    }

    /**
     * プレイヤーがポーションや牛乳を飲んだときの処理
     * @param e
     */
    public static void onPlayerItemConsume(PlayerItemConsumeEvent e)
    {
    	Player player = e.getPlayer();
    	ItemStack item = e.getItem();
    	ItemMeta itemMeta = item.getItemMeta();
    	String itemName = itemMeta.getDisplayName();

    	//System.out.println(itemName);
    	//System.out.println(ChatColor.RED + "魔力回復のポーション");

    	if(itemName.equalsIgnoreCase(ChatColor.RED + "魔力回復のポーション"))
    	{
    		AddMagicPoint(player,1000);
    	}
    }

	public static void GetBookByCommand(Player player, String bookName)
	{
		//本の名前が指定されていないときは処理しない
		if(Objects.isNull(bookName)) return;

		if(bookName.equalsIgnoreCase(FIRE_ARROW_BOOK))
		{
			GetBook(player,FIRE_ARROW_BOOK);
		}
		else if(bookName.equalsIgnoreCase(ICE_ARROW_BOOK))
		{
			GetBook(player,ICE_ARROW_BOOK);
		}
		else if(bookName.equalsIgnoreCase(JUMP_BOOST_BOOK))
		{
			GetBook(player,JUMP_BOOST_BOOK);
		}
		else if(bookName.equalsIgnoreCase(SUMMON_PERO_BOOK))
		{
			GetBook(player,SUMMON_PERO_BOOK);
		}
		else if(bookName.equalsIgnoreCase(ONIKU_TABETAI_BOOK))
		{
			GetBook(player,ONIKU_TABETAI_BOOK);
		}
	}

}
