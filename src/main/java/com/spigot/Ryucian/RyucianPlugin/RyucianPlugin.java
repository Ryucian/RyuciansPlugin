package com.spigot.Ryucian.RyucianPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class RyucianPlugin extends JavaPlugin implements Listener
{
	private Cat pero = null;

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

    	Player player = (Player) sender;

    	//ペロを召喚する
    	pero=(Cat)player.getWorld().spawnEntity((player).getLocation(),EntityType.CAT);
    	pero.setCatType(Cat.Type.ALL_BLACK);
    	pero.setCustomName("ペロ");
    	pero.setOwner(player);
    	pero.setMaxHealth(100);
    	pero.setHealth(100);
		return true;
	}

	/**
	 * エンティティがエンティティからダメージを受けるとき呼び出される
	 *
	 * @param e
	 */
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e)
	{
		Entity owner = (Entity)pero.getOwner();

		// ダメージを受けたエンティティはペロのご主人でない場合は処理を行わない
		if(e.getEntity().equals(owner) == false) return;

		//デバッグ用にログを吐き出す
		getServer().broadcastMessage("ペロ：痛そう・・・");

		//ダメージを受けたら回復を唱える
		Location locationPero = pero.getLocation();

		// 頭上にポーションを撒く
		ThrownPotion item=(ThrownPotion) pero.getWorld().spawnEntity(locationPero,EntityType.SPLASH_POTION);



		item.setItem(null);

		item.setItem(new ItemStack(Material.POTION,1,(short)-10));
		item.setShooter(pero);
		item.setVelocity(new Vector(0,0.6,0));

	}

















}
