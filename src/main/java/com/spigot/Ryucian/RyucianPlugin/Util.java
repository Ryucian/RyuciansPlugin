package com.spigot.Ryucian.RyucianPlugin;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Util {

	private static Random random = new Random();

	public static int getRandom(int max)
	{
		return random.nextInt(max);
	}

    /**
     * 場所originから場所targetまでのベクトルを取得する
     * @param origin
     * @param target
     * @return
     */
	public static Vector GetVector2Loc(Location origin,Location target)
	{
		return target.toVector().subtract(origin.toVector()).normalize();
	}

	/**
	 * 抽選を行う
	 * @param denom 分母（指定した数値分の１の確率であたる）
	 * @return あたりの場合はtrue、はずれの場合はfalse
	 */
	public static Boolean isLottery(int denom)
	{
		int rnd =random.nextInt(denom);
		return rnd == 0;
	}

	public static void GetFreshSteak(Player player)
	{
		Location cowLoc = player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(3));
		Cow cow = (Cow)player.getWorld().spawnEntity(cowLoc,EntityType.COW);
		cow.setAI(false);
		cow.setFireTicks(9999);
		cow.setGravity(true);
	}

}
