package net.mcshockwave.MCS.Utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_12_R1.WorldBorder;

public class PacketUtils {

	public static void playBlockParticles(MaterialData m, Location l) {
		l.getWorld().spawnParticle(Particle.BLOCK_CRACK, l, 50, m);
	}

	public static void playBlockDustParticles(MaterialData m, Location l, float rad, float spd) {
		l.getWorld().spawnParticle(Particle.BLOCK_DUST, l, 50, 0, 0, 0, spd, m);
	}

	public static void playParticleEffect(Particle pe, Location l, float rad, float speed, int amount) {
		l.getWorld().spawnParticle(pe, l, amount, rad, rad, rad, speed);
	}

	@SuppressWarnings("deprecation")
	public static void setBlockFromPacket(Player p, org.bukkit.block.Block b, Material m, int data) {
		p.sendBlockChange(b.getLocation(), m.getId(), (byte) data);
	}

	public static void sendPacket(Player p, Packet<?> pack) {
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(pack);
	}

	public static void sendPacketGlobally(Location l, int distance, Packet<?> pack) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld() == l.getWorld() && p.getLocation().distance(l) <= distance) {
				sendPacket(p, pack);
			}
		}
	}

	// "custom" packets

	public static void registerPackets() {
		try {
		} catch (Exception e) {
			MiscUtils.printStackTrace(e);
		}
	}

	// credit to dori99xd on spigot forums
	// @SuppressWarnings("unchecked")
	// private static void addPacket(EnumProtocol protocol, boolean clientbound,
	// int id, Class<? extends Packet> packet)
	// throws NoSuchFieldException, IllegalAccessException {
	// Field packets;
	// if (!clientbound) {
	// packets = EnumProtocol.class.getDeclaredField("h");
	// } else {
	// packets = EnumProtocol.class.getDeclaredField("i");
	// }
	// packets.setAccessible(true);
	// BiMap<Integer, Class<? extends Packet>> pMap = (BiMap<Integer, Class<?
	// extends Packet>>) packets.get(protocol);
	// pMap.put(Integer.valueOf(id), packet);
	// Field map = EnumProtocol.class.getDeclaredField("f");
	// map.setAccessible(true);
	// Map<Class<? extends Packet>, EnumProtocol> protocolMap = (Map<Class<?
	// extends Packet>, EnumProtocol>) map
	// .get(null);
	// protocolMap.put(packet, protocol);
	// }

	public static void playTitle(Player p, int fi, int le, int fo, String tit, String sub) {
		playTitle(Arrays.asList(p), fi, le, fo, tit, sub);
	}

	public static void playTitle(List<Player> pls, int fi, int le, int fo, String tit, String sub) {
		pls.get(0).sendTitle(tit, sub, fi, le, fo);
	}

	public static void setWorldBorder(Player p, double x, double z, double radius, int warnTime, int warnBlocks) {
		setWorldBorder(Arrays.asList(p), x, z, radius, warnTime, warnBlocks);
	}

	public static void setWorldBorder(List<Player> ps, double x, double z, double radius, int warnTime,
			int warnBlocks) {
		WorldBorder worldBorder = new WorldBorder();
		worldBorder.setSize(radius);
		worldBorder.setCenter(x, z);
		worldBorder.setWarningTime(warnTime);
		worldBorder.setWarningDistance(warnBlocks);
		PacketPlayOutWorldBorder pack = new PacketPlayOutWorldBorder(worldBorder, EnumWorldBorderAction.INITIALIZE);
		for (Player p : ps) {
			sendPacket(p, pack);
		}
	}

	public static void lerpBorder(Player p, double oldrad, double newrad, long speed) {
		lerpBorder(Arrays.asList(p), oldrad, newrad, speed);
	}

	public static void lerpBorder(List<Player> ps, double oldrad, double newrad, long speed) {
		WorldBorder worldBorder = new WorldBorder();
		worldBorder.transitionSizeBetween(oldrad, newrad, speed);
		PacketPlayOutWorldBorder pack = new PacketPlayOutWorldBorder(worldBorder, EnumWorldBorderAction.LERP_SIZE);
		for (Player p : ps) {
			sendPacket(p, pack);
		}
	}
	
	public static void setHeaderFooter(Player p, String header, String footer) {
		String head = "{\"text\":\"" + header + "\"}";
		String foot = "{\"text\":\"" + footer + "\"}";
		PacketPlayOutPlayerListHeaderFooter pack = new PacketPlayOutPlayerListHeaderFooter();
		try {
            Field fieldA = (Field) pack.getClass().getDeclaredField("a");
            fieldA.setAccessible(true);
            fieldA.set(pack, ChatSerializer.a(head));
            Field fieldB = (Field) pack.getClass().getDeclaredField("b");
            fieldB.setAccessible(true);
            fieldB.set(pack, ChatSerializer.a(foot));
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		sendPacket(p, pack);
	}

}
