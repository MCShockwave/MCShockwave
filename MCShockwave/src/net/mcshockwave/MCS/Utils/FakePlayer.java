package net.mcshockwave.MCS.Utils;

import net.mcshockwave.MCS.MCShockwave;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;

import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerBed;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class FakePlayer {

	public static HashMap<Short, FakePlayer>	fakePlayers	= new HashMap<>();

	public short								id;
	public String								name;
	public Location								loc;

	public boolean								exists		= false;

	FakePlayer(short id, String name, Location loc) {
		this.id = id;
		this.name = name;
		this.loc = loc;
	}

	public static FakePlayer spawnNew(Location loc, String name) {
		short id = getEntityId();

		WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
		spawn.setEntityID(id);
		spawn.setPosition(loc.toVector());
		spawn.setPlayerName(name);
		spawn.setPlayerUUID("0-0-0-0-0");

		spawn.setYaw(0);
		spawn.setPitch(0);

		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(0, (byte) 0);

		spawn.setMetadata(watcher);

		WrapperPlayServerBed bed = new WrapperPlayServerBed();
		bed.setEntityId(id);
		bed.setX(loc.getBlockX());
		bed.setY((byte) loc.getBlockY());
		bed.setZ(loc.getBlockZ());

		WrapperPlayServerEntityTeleport tele = new WrapperPlayServerEntityTeleport();
		tele.setEntityID(id);
		tele.setX(loc.getX());
		tele.setY(loc.getY() + 0.4);
		tele.setZ(loc.getZ());

		for (Player p : loc.getWorld().getPlayers()) {
			spawn.sendPacket(p);
			bed.sendPacket(p);
			tele.sendPacket(p);
		}

		FakePlayer fp = new FakePlayer(id, name, loc);
		fp.exists = true;
		fakePlayers.put(id, fp);

		return fp;
	}

	public void startAnimation(int times, boolean destroy) {
		long delay = 0;
		for (int i = 0; i < times; i++) {
			Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
				public void run() {
					if (!exists) {
						return;
					}

					WrapperPlayServerAnimation ani = new WrapperPlayServerAnimation();
					ani.setEntityID(id);
					ani.setAnimation(0);

					WrapperPlayServerAnimation ani2 = new WrapperPlayServerAnimation();
					ani2.setEntityID(id);
					ani2.setAnimation(1);

					PacketUtils.playBlockDustParticles(Material.REDSTONE_BLOCK, 0, loc.clone().add(-0.5, -0.2, -0.5),
							0, 0.1f);

					loc.getWorld().playSound(loc, Sound.HURT_FLESH, 3, 0);

					for (Player p : loc.getWorld().getPlayers()) {
						ani.sendPacket(p);
						ani2.sendPacket(p);
					}
				}
			}, delay += rand.nextInt(15) + 5);
		}
		if (destroy && exists) {
			Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
				public void run() {
					destroy();
				}
			}, delay += 20);
		}
	}

	public void destroy() {
		WrapperPlayServerEntityDestroy des = new WrapperPlayServerEntityDestroy();
		des.setEntities(new int[] { id });
		for (Player p : loc.getWorld().getPlayers()) {
			des.sendPacket(p);
		}
		fakePlayers.remove(id);
		exists = false;
	}

	static Random	rand	= new Random();

	public static short getEntityId() {
		return (short) rand.nextInt(Short.MAX_VALUE);
	}

}
