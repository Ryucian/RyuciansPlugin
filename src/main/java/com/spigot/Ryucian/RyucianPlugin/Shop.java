package com.spigot.Ryucian.RyucianPlugin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;

public class Shop
{
	final static String SHOPER_NAME="廃品回収屋さん";

	//アイテムとその売値を管理するやつ
	private static HashMap<Material,Long>  priceMap = new HashMap<Material,Long>();

	//プレイヤーが開いているインベントリーを管理するやつ
	public static HashMap<Player, Inventory> playerInvMap = new HashMap<Player,Inventory>();

	static
	{
		priceMap.put(Material.DIRT,5L);
	}

    /**
     * プレイヤーがエンティティを右クリックする時に呼び出される。
     * @param e
     */
    public static void onPlayerInteractEntity(PlayerInteractEntityEvent e)
    {
    	var entity = e.getRightClicked();

    	//右クリックされたエンティティに名前がついていない場合は処理しない
    	if(Objects.isNull(entity.getCustomName())) return;

    	//プレイヤーが右クリックしたエンティティが生きてるエンティティでなければ処理しない
		if(!(entity instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity)entity;

    	//プレイヤーを取得
    	var player = e.getPlayer();

    	//名前が廃品回収屋さんの場合
    	if(entity.getCustomName().equalsIgnoreCase(SHOPER_NAME))
    	{
    		//インベントリを生成
    		var shopInventory = player.getServer().createInventory(null,9,SHOPER_NAME);

    		//生成したインベントリをプレイヤーに開かせる
	    	player.openInventory(shopInventory);

	    	//プレイヤーインベントリリストに登録する
	    	playerInvMap.put(player,shopInventory);
    	}
    }

    /**
     * インベントリをクリックしたときに呼び出される
     * @param e
     */
    public static void onInventoryClick(InventoryClickEvent e,Player player)
    {
    	System.out.println("Shop.onInventoryClick is Start");

    	//プレイヤーがショッパーを開いていない場合は処理しない
    	if(!playerInvMap.containsKey(player)) return;

    	//プレイヤーが開いているインベントリがショップインベントリでない場合は処理しない
    	if( !e.getView().getTitle().equals(SHOPER_NAME)) return;
    	var shopInv = e.getInventory();

    	//クリックされたインベントリがプレイヤーの場合は処理しない
    	if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;

    	//クリック前のアイテムを取得
    	var itemStack = e.getCursor();

    	//クリック前のアイテムがなければ処理しない
    	if(Objects.isNull(itemStack)) return;

    	//クリック前のアイテムが価格表に存在しないの場合は処理しない
    	if( !priceMap.containsKey(itemStack.getType()) )
    	{
    		e.setCancelled(true);
    		return;
    	}
    	var price = priceMap.get(itemStack.getType())*itemStack.getAmount();

    	//プレイヤーにおかねをあげる
    	Magic.AddMoney(player, price);

    	//ショップの中のアイテムを消す
    	//なぜか見た目上消せないため断念
    	//Util.RemoveItem(shopInv,itemStack.getAmount(),itemStack.getType());

    	//買取メッセージを出す
    	player.sendMessage(SHOPER_NAME+"が"+itemStack.getItemMeta().getLocalizedName()+itemStack.getAmount()+"を"+price+"$で買い取りました。");

    }

    public static void onInventoryClose(InventoryCloseEvent e)
    {
    	var player = (Player)e.getPlayer();
    	playerInvMap.remove(player);
    }
}
