package com.spigot.Ryucian.RyucianPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Usagi
{

	//private static Map<Player,Rabbit> map = new HashMap<>();

    /**
     * プレイヤーがダメージを受けたときの挙動
     * @param e
     */
	public static void onPlayerDamage(EntityDamageByEntityEvent e)
	{

		//引数からプレイヤーと殴った相手を取得
		var player = (Player)e.getEntity();

		// 2/3の確率で行動しない
		if(!Util.isLottery(3)) return;

		//うさこを取得、取得できなければ処理を終了する
		var usako = GetUsako(player);
		if(Objects.isNull(usako)) return;

		//うさぎ人参を行う
		GiveCarrot(usako,player,true);
	}

	/**
	 * プレイヤーの周りにいるうさこを探す
	 * @param player
	 * @return
	 */
	public static Rabbit GetUsako(Player player)
	{
		Rabbit usako = null;
		var entityList = player.getWorld().getEntities();
		//エンティティリストから猫型でオーナがダメージを受けてる猫を探す
		for(Entity entity:entityList)
		{
			//猫型でないならば処理しない
			if(!(entity instanceof Rabbit)) continue;
			Rabbit rabbit = (Rabbit) entity;

			//player.sendMessage("Name=" + rabbit.getName());

			//うさぎの名前が「うさこ」でなければ処理しない
			if(rabbit.getName().equalsIgnoreCase("うさこ") == false) continue;

			//上の条件に当てはまらない場合はうさこなので検索終了とする
			usako = rabbit;
			break;
		}
		return usako;
	}

    /**
     * うさぎたちを召喚する
     * @param sender
     * @return
     */
    public static boolean SummonUsa(Player player,Plugin plugin)
    {
    	var playersLocation = player.getLocation();
    	var world = player.getWorld();

    	//うさぎを召喚する
    	Rabbit pero=(Rabbit)world.spawnEntity(playersLocation,EntityType.RABBIT);
    	pero.setRabbitType(Rabbit.Type.BLACK);
    	pero.setCustomName("うさ吉");
    	pero.setMaxHealth(100);
    	pero.setHealth(100);
    	pero.setTarget(player);
    	pero.setBaby();
    	pero.setAgeLock(true);


    	//うさぎを召喚する
    	Rabbit usa2=(Rabbit)world.spawnEntity(playersLocation,EntityType.RABBIT);
    	usa2.setRabbitType(Rabbit.Type.BLACK_AND_WHITE);
    	usa2.setCustomName("ぴょんぴょん");
    	usa2.setMaxHealth(100);
    	usa2.setHealth(100);
    	usa2.setTarget(player);
    	usa2.setBaby();
    	usa2.setAgeLock(true);

    	//うさぎを召喚する
    	Rabbit usa3=(Rabbit)world.spawnEntity(playersLocation,EntityType.RABBIT);
    	usa3.setRabbitType(Rabbit.Type.BROWN);
    	usa3.setCustomName("うさぴょん");
    	usa3.setTarget(player);
    	usa3.setMaxHealth(100);
    	usa3.setHealth(100);
    	usa3.setBaby();
    	usa3.setAgeLock(true);
    	//usa3.setMetadata("RyucianPlugin", new FixedMetadataValue(plugin,true));
    	usa3.playEffect(EntityEffect.LOVE_HEARTS);
    	usa3.setBreedCause(player.getUniqueId());
    	//map.put(player, usa3);

    	//うさぎを召喚する
    	Rabbit usa4=(Rabbit)world.spawnEntity(playersLocation,EntityType.RABBIT);
    	usa4.setRabbitType(Rabbit.Type.GOLD);
    	usa4.setCustomName("うーたん");
    	usa4.setMaxHealth(100);
    	usa4.setHealth(100);
    	usa4.setTarget(player);
    	usa4.setBaby();
    	usa4.setAgeLock(true);

		return true;
    }

    /**
     * プレイヤーの周りにいるうさぎをすべて招集する
     * @param player
     */
    public static void ComeOnUsa(Player player)
    {
		//エンティティリストからうさぎを探す
		var entityList = player.getWorld().getEntities();
		for(Entity entity:entityList)
		{
			//うさぎでないならば処理しない
			if(!(entity instanceof Rabbit)) continue;
			Rabbit rabbit = (Rabbit) entity;
    		rabbit.teleport(player);
		}
    }

	/**
	 * 傷ついたプレイヤーに人参を投げる
	 * @param rabbit
	 * @param player
	 */
	public static void GiveCarrot(Rabbit rabbit,Player player,Boolean isDamage)
	{
		//距離が遠ければ何もしない
    	Double distance = rabbit.getLocation().distance(player.getLocation());
    	if(10<distance)
    	{
			return;
    	}

		rabbit.setTarget(player);
		if(isDamage)
		{
			player.sendMessage(rabbit.getName()+"：殴られて可愛そうだから人参あげるぴょん");
		}
		else
		{
			player.sendMessage(rabbit.getName()+"：仕方ないぴょん");
		}
		ItemStack itemStack = new ItemStack(Material.CARROT);
		player.getWorld().dropItem(rabbit.getLocation(),itemStack);
	}
}
