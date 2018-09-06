package net.mcshockwave.MCS.Utils;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerBed;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.mojang.authlib.GameProfile;

import net.mcshockwave.MCS.MCShockwave;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

public class FakePlayer {

	public static HashMap<Short, FakePlayer> fakePlayers = new HashMap<>();

	public short id;
	public String name;
	public Location loc;

	public boolean exists = false;

	FakePlayer(short id, String name, Location loc) {
		this.id = id;
		this.name = name;
		this.loc = loc;
	}

	public static FakePlayer spawnNew(Location loc, String name) {
		short id = getEntityId();

		String uuid = UUID.randomUUID().toString();
		char[] uuidChars = uuid.toCharArray();
		uuidChars[14] = '2';
		uuid = new String(uuidChars);

		MinecraftServer mserver = MinecraftServer.getServer();
		WorldServer wserver = ((CraftWorld) loc.getWorld()).getHandle();
		GameProfile gprofile = new ProfileLoader(uuid, name).loadProfile();
		PlayerInteractManager pimanager = new PlayerInteractManager(wserver);
		PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(
				new EntityPlayer(mserver, wserver, gprofile, pimanager));

		WrapperPlayServerBed bed = new WrapperPlayServerBed();
		bed.setEntityID(id);
		bed.setLocation(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

		WrapperPlayServerEntityTeleport tele = new WrapperPlayServerEntityTeleport();
		tele.setEntityID(id);
		tele.setX(loc.getX());
		tele.setY(loc.getY() + 0.4);
		tele.setZ(loc.getZ());

		for (Player p : loc.getWorld().getPlayers()) {
			PacketUtils.sendPacket(p, spawn);
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

					PacketUtils.playBlockDustParticles(new MaterialData(Material.REDSTONE_BLOCK), loc.clone().add(-0.5, -0.2, -0.5), 0,
							0.1f);

					loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_HURT, 3, 0);

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
		des.setEntityIds(new int[] { id });
		for (Player p : loc.getWorld().getPlayers()) {
			des.sendPacket(p);
		}
		fakePlayers.remove(id);
		exists = false;
	}

	static Random rand = new Random();

	public static short getEntityId() {
		return (short) rand.nextInt(Short.MAX_VALUE);
	}

}
