package com.spigot.Ryucian.RyucianPlugin;

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class CherryDash 
{
    /**
     * 桜走りを有効化する処理
     */
    public static void Enable(Player player)
    {
        if(!UseLeaves(player, 1)) return;

        int dulation = 20*60;
        //自分で新しいバフを描けるのは無理なので
        //速度１跳躍１のセットとする
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,dulation, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,dulation, 1));
    }
    /**
     * ジャンプする
     */
    public static void EnableJump(Player player)
    {
        if(!UseLeaves(player, 1)) return;

        player.setVelocity(player.getVelocity().add(new Vector(0,10,0)));

        //落下死防止のために落下速度低下のバフをかける
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,20*10, 1));

        for(int i=0;i<100;i++)
        {
            player.getWorld().spawnParticle(Particle.CHERRY_LEAVES, player.getEyeLocation().add(0, i, 0),5,1,1,1);
        }

    }

    /**
     * ダッシュしている人にエフェクトを付ける
     * @param e
     */
    @EventHandler
	public static void onPlayerMove(Player player)
	{
        //スピードバフかかってない場合は処理しない
        if(Objects.isNull(player.getPotionEffect(PotionEffectType.SPEED))) return;

        //ジャンプバフかかってない場合は処理しない
        if(Objects.isNull(player.getPotionEffect(PotionEffectType.JUMP))) return;

        player.getWorld().spawnParticle(Particle.CHERRY_LEAVES, player.getEyeLocation(),5,1,1,1);

	}

    /**
     * 桜の花びらを消費する
     * @param player
     */
    private static boolean UseLeaves(Player player,int amt)
    {
        var useResult = Util.RemoveItem(player.getInventory(),amt,Material.PINK_PETALS);
        if(!useResult)
        {
            player.sendMessage("桜の花びらが足りない（使用枚数："+amt+"）");
        }
        return useResult;
    }
    
}
