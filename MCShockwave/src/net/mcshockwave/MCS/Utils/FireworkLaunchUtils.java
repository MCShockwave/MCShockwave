package net.mcshockwave.MCS.Utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkLaunchUtils {

	public static void playFirework(Location l, Color... c) {
		Firework fw = l.getWorld().spawn(l,Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        for (Color cl : c) {
        	meta.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL).trail(false).withColor(cl).build());
        }
        fw.setFireworkMeta(meta);
        ((CraftFirework) fw).getHandle().expectedLifespan = 1;
	}

}
