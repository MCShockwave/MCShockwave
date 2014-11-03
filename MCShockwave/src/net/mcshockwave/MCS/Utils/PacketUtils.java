package net.mcshockwave.MCS.Utils;

import net.mcshockwave.MCS.MCShockwave;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.EnumProtocol;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketDataSerializer;
import net.minecraft.server.v1_7_R4.PacketListener;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import net.minecraft.util.com.google.common.collect.BiMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PacketUtils {

	public static enum ParticleEffect {
		HUGE_EXPLOSION(
			"hugeexplosion"),
		LARGE_EXPLODE(
			"largeexplode"),
		FIREWORKS_SPARK(
			"fireworksSpark"),
		BUBBLE(
			"bubble"),
		SUSPEND(
			"suspend"),
		DEPTH_SUSPEND(
			"depthSuspend"),
		TOWN_AURA(
			"townaura"),
		CRIT(
			"crit"),
		MAGIC_CRIT(
			"magicCrit"),
		MOB_SPELL(
			"mobSpell"),
		MOB_SPELL_AMBIENT(
			"mobSpellAmbient"),
		SPELL(
			"spell"),
		INSTANT_SPELL(
			"instantSpell"),
		WITCH_MAGIC(
			"witchMagic"),
		NOTE(
			"note"),
		PORTAL(
			"portal"),
		ENCHANTMENT_TABLE(
			"enchantmenttable"),
		EXPLODE(
			"explode"),
		FLAME(
			"flame"),
		LAVA(
			"lava"),
		FOOTSTEP(
			"footstep"),
		SPLASH(
			"splash"),
		LARGE_SMOKE(
			"largesmoke"),
		CLOUD(
			"cloud"),
		RED_DUST(
			"reddust"),
		SNOWBALL_POOF(
			"snowballpoof"),
		DRIP_WATER(
			"dripWater"),
		DRIP_LAVA(
			"dripLava"),
		SNOW_SHOVEL(
			"snowshovel"),
		SLIME(
			"slime"),
		HEART(
			"heart"),
		ANGRY_VILLAGER(
			"angryVillager"),
		HAPPY_VILLAGER(
			"happyVillager");

		public String	particleName;

		ParticleEffect(String particleName) {
			this.particleName = particleName;
		}

	}

	public static void playBlockParticles(Material m, int data, Location l) {
		sendPacketGlobally(l, 20, generateBlockParticles(m, data, l));
	}

	public static void playBlockDustParticles(Material m, int data, Location l, float rad, float spd) {
		sendPacketGlobally(l, 20, generateBlockDustParticles(m, data, l, rad, spd));
	}

	public static void playParticleEffect(ParticleEffect pe, Location l, float rad, float speed, int amount) {
		sendPacketGlobally(l, 20, generateParticles(pe, l, rad, speed, amount));
	}

	@SuppressWarnings("deprecation")
	public static PacketPlayOutWorldParticles generateBlockParticles(Material m, int data, Location l) {
		l = l.add(0.5, 0.5, 0.5);
		String icn = "";
		if (m.isBlock()) {
			icn = "blockcrack_" + m.getId() + "_" + data;
		}
		PacketPlayOutWorldParticles pack = new PacketPlayOutWorldParticles(icn, (float) l.getX(), (float) l.getY(),
				(float) l.getZ(), 0.5f, 0.5f, 0.5f, 1, 50);
		return pack;
	}

	@SuppressWarnings("deprecation")
	public static PacketPlayOutWorldParticles generateBlockDustParticles(Material m, int data, Location l, float rad,
			float spd) {
		l = l.add(0.5, 0.5, 0.5);
		String icn = "";
		if (m.isBlock()) {
			icn = "blockdust_" + m.getId() + "_" + data;
		}
		PacketPlayOutWorldParticles pack = new PacketPlayOutWorldParticles(icn, (float) l.getX(), (float) l.getY(),
				(float) l.getZ(), rad, rad, rad, spd, 50);
		return pack;
	}

	public static PacketPlayOutWorldParticles generateParticles(ParticleEffect particle, Location l, float rad,
			float speed, int amount) {
		PacketPlayOutWorldParticles pack = new PacketPlayOutWorldParticles(particle.particleName, (float) l.getX(),
				(float) l.getY(), (float) l.getZ(), rad, rad, rad, speed, amount);
		return pack;
	}

	@SuppressWarnings("deprecation")
	public static void setBlockFromPacket(Player p, org.bukkit.block.Block b, Material m, int data) {
		p.sendBlockChange(b.getLocation(), m.getId(), (byte) data);
	}

	public static void sendPacket(Player p, Packet pack) {
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(pack);
	}

	public static void sendPacketGlobally(Location l, int distance, Packet pack) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld() == l.getWorld() && p.getLocation().distance(l) <= distance) {
				sendPacket(p, pack);
			}
		}
	}

	// "custom" packets

	public static void registerPackets() {
		try {
			addPacket(EnumProtocol.PLAY, true, 0x45, PacketPlayOutTitle.class);
			addPacket(EnumProtocol.PLAY, true, 0x47, PacketPlayOutHeaderFooter.class);
		} catch (Exception e) {
			MiscUtils.printStackTrace(e);
		}
	}

	// credit to dori99xd on spigot forums
	@SuppressWarnings("unchecked")
	private static void addPacket(EnumProtocol protocol, boolean clientbound, int id, Class<? extends Packet> packet)
			throws NoSuchFieldException, IllegalAccessException {
		Field packets;
		if (!clientbound) {
			packets = EnumProtocol.class.getDeclaredField("h");
		} else {
			packets = EnumProtocol.class.getDeclaredField("i");
		}
		packets.setAccessible(true);
		BiMap<Integer, Class<? extends Packet>> pMap = (BiMap<Integer, Class<? extends Packet>>) packets.get(protocol);
		pMap.put(Integer.valueOf(id), packet);
		Field map = EnumProtocol.class.getDeclaredField("f");
		map.setAccessible(true);
		Map<Class<? extends Packet>, EnumProtocol> protocolMap = (Map<Class<? extends Packet>, EnumProtocol>) map
				.get(null);
		protocolMap.put(packet, protocol);
	}

	public static void playTitle(Player p, int fi, int le, int fo, String tit, String sub) {
		playTitle(Arrays.asList(p), fi, le, fo, tit, sub);
	}

	public static void playTitle(List<Player> pls, int fi, int le, int fo, String tit, String sub) {
		for (Player p : pls.toArray(new Player[0])) {
			if (MCShockwave.getClientVersion(p) != 47) {
				pls.remove(p);
			}
		}

		PacketPlayOutTitle timing = new PacketPlayOutTitle(TitleAction.TIMES);
		timing.fadeIn = fi;
		timing.length = le;
		timing.fadeOut = fo;

		for (Player p : pls) {
			sendPacket(p, timing);
		}

		if (tit != null) {
			tit = "{text:\"" + tit + "\"}";
			PacketPlayOutTitle title = new PacketPlayOutTitle(TitleAction.TITLE);
			title.title = ChatSerializer.a(tit);

			for (Player p : pls) {
				sendPacket(p, title);
			}
		}
		if (sub != null) {
			sub = "{text:\"" + sub + "\"}";
			PacketPlayOutTitle subtitle = new PacketPlayOutTitle(TitleAction.SUBTITLE);
			subtitle.subtitle = ChatSerializer.a(sub);

			for (Player p : pls) {
				sendPacket(p, subtitle);
			}
		}
	}

	public static class PacketPlayOutTitle extends Packet {

		public int	fadeIn, length, fadeOut;
		public IChatBaseComponent	title, subtitle;
		public TitleAction			a;

		public PacketPlayOutTitle(TitleAction a) {
			this.a = a;
		}

		public void a(PacketDataSerializer pds) throws IOException {
			a = TitleAction.values()[pds.a()];
			switch (a) {
				case TITLE:
					title = ChatSerializer.a(pds.c(32767));
					break;
				case SUBTITLE:
					subtitle = ChatSerializer.a(pds.c(32767));
					break;
				case TIMES:
					fadeIn = pds.readInt();
					length = pds.readInt();
					fadeOut = pds.readInt();
					break;
				default:
					break;
			}
		}

		public void b(PacketDataSerializer pds) throws IOException {
			pds.b(a.ordinal());
			switch (a) {
				case TITLE:
					pds.a(ChatSerializer.a(title));
					break;
				case SUBTITLE:
					pds.a(ChatSerializer.a(subtitle));
					break;
				case TIMES:
					pds.writeInt(fadeIn);
					pds.writeInt(length);
					pds.writeInt(fadeOut);
					break;
				default:
					break;
			}
		}

		public void handle(PacketListener pl) {
		}

	}

	public static enum TitleAction {
		TITLE,
		SUBTITLE,
		TIMES,
		CLEAR,
		RESET;
	}

	public static class PacketPlayOutHeaderFooter extends Packet {

		public IChatBaseComponent	header, footer;

		public PacketPlayOutHeaderFooter(String header, String footer) {
			this.header = ChatSerializer.a(header);
			this.footer = ChatSerializer.a(footer);
		}

		public void a(PacketDataSerializer pds) throws IOException {
			header = ChatSerializer.a(pds.c(32767));
			footer = ChatSerializer.a(pds.c(32767));
		}

		public void b(PacketDataSerializer pds) throws IOException {
			pds.a(ChatSerializer.a(header));
			pds.a(ChatSerializer.a(footer));
		}

		public void handle(PacketListener pl) {
		}

	}
}
