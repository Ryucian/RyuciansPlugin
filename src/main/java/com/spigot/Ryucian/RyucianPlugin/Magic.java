package com.spigot.Ryucian.RyucianPlugin;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Magic
{

	final static String MAGIC_POINT="MagicPoint";
	final static String FIRE_ARROW_BOOK="火矢の書";
	final static String JUMP_BOOST_BOOK="跳躍の書";
	final static String SUMMON_PERO_BOOK="ペロの書";

	private static Essentials ess;

	static
	{
		ess = (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");
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
		player.sendMessage("MP "+ beforeMp.toString() + "→" + mp.toString() );
		//user.reloadConfig();
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
	 * プレイヤーに本を追加する
	 * @param player
	 * @param bookName
	 * @param bookLore
	 */
	public static void GetBook(Player player,String bookName,List<String> bookLore)
	{
		ItemStack scBow = new ItemStack(Material.BOOK,1);
	    ItemMeta scBowMeta = scBow.getItemMeta();
	    scBowMeta.setDisplayName(bookName);
	    scBowMeta.setLore(bookLore);
	    scBow.setItemMeta(scBowMeta);
        PlayerInventory inventory = player.getInventory();
        inventory.addItem(scBow);
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
	 * マナポーションを取得する
	 * @param player
	 */
	public static void GetManaGainPotion(Player player)
	{
		//SuperCreekBowを作る
		ItemStack scBow = new ItemStack(Material.POTION,1);
	    ItemMeta scBowMeta = scBow.getItemMeta();
	    scBowMeta.setDisplayName(ChatColor.RED + "魔力回復のポーション" + ChatColor.RESET);
	    scBowMeta.setLocalizedName("魔力回復のポーション");
	    var lore = new ArrayList<String>();
	    lore.add("魔力を100回復する");
	    scBowMeta.setLore(lore);
	    scBow.setItemMeta(scBowMeta);

        PlayerInventory inventory = player.getInventory();
        inventory.addItem(scBow);
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
        if( !(player.getInventory().getItemInMainHand().getType().equals(Material.BOOK)) ) return;

        //アイテム名を取得
        String handItemName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();

        //持ってる本の名前が赤文字の火球の書の場合
        if(handItemName.equalsIgnoreCase(ChatColor.RED + FIRE_ARROW_BOOK))
        {
        	final int USE_MP = 10;
        	Integer beforeMp = GetMagicPointInt(player);
        	//MPが少ない場合使わない
        	if(beforeMp<USE_MP) return;
        	var world = player.getWorld();
        	Vector unitVector = new Vector(player.getLocation().getDirection().getX(), 0, player.getLocation().getDirection().getZ());
        	unitVector = unitVector.normalize();
    		var sb =  (Arrow)world.spawnEntity(player.getLocation().add(0, 1, 0), EntityType.ARROW);
    		//sb.setShooter(player);
    		sb.setVisualFire(true);
    		sb.setVelocity(player.getLocation().getDirection().normalize().multiply(5));
    		AddMagicPoint(player, -USE_MP);
        	return;
        }
        //持ってる本の名前が赤文字の「跳躍の書の場合」
        if(handItemName.equalsIgnoreCase(ChatColor.RED + JUMP_BOOST_BOOK))
        {
        	final int USE_MP = 30;
        	Integer beforeMp = GetMagicPointInt(player);
        	//MPが少ない場合使わない
        	if(beforeMp<USE_MP) return;

        	PotionEffect pe = new PotionEffect(PotionEffectType.JUMP,500,10);
        	player.addPotionEffect(pe);

        	PotionEffect pe2 = new PotionEffect(PotionEffectType.SPEED,500,1);
        	player.addPotionEffect(pe2);


    		AddMagicPoint(player, -USE_MP);
        	return;
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
    	player.sendMessage(itemName);

    	//System.out.println(itemName);
    	//System.out.println(ChatColor.RED + "魔力回復のポーション");

    	if(itemName.equalsIgnoreCase(ChatColor.RED + "魔力回復のポーション"))
    	{
    		AddMagicPoint(player,1000);
    	}
    }

}
