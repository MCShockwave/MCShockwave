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
			addPacket(EnumProtocol.PLAY, true, 0x44, PacketPlayOutWorldBorder.class);
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

	public static void setWorldBorder(Player p, double x, double z, double radius, int warnTime, int warnBlocks) {
		setWorldBorder(Arrays.asList(p), x, z, radius, warnTime, warnBlocks);
	}

	public static void setWorldBorder(List<Player> ps, double x, double z, double radius, int warnTime, int warnBlocks) {
		PacketPlayOutWorldBorder pack = new PacketPlayOutWorldBorder(x, z, radius, radius, 0, warnTime, warnBlocks);
		for (Player p : ps) {
			sendPacket(p, pack);
		}
	}

	public static void lerpBorder(Player p, double oldrad, double newrad, long speed) {
		lerpBorder(Arrays.asList(p), oldrad, newrad, speed);
	}

	public static void lerpBorder(List<Player> ps, double oldrad, double newrad, long speed) {
		PacketPlayOutWorldBorder pack = new PacketPlayOutWorldBorder(oldrad, newrad, speed);
		for (Player p : ps) {
			sendPacket(p, pack);
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

	// for all your worldbordering needs
	public static class PacketPlayOutWorldBorder extends Packet {

		WorldBorderAction	act;

		// SET_SIZE
		double				radius;

		/** Action: SET SIZE **/
		public PacketPlayOutWorldBorder(double radius) {
			act = WorldBorderAction.SET_SIZE;
			this.radius = radius;
		}

		// LERP_SIZE
		double	oldradius;
		double	newradius;
		long	speed;

		/** Action: LERP SIZE **/
		public PacketPlayOutWorldBorder(double oldradius, double newradius, long speed) {
			act = WorldBorderAction.LERP_SIZE;
			this.oldradius = oldradius;
			this.newradius = newradius;
			this.speed = speed;
		}

		// SET_CENTER
		double	x;
		double	z;

		/** Action: SET CENTER **/
		public PacketPlayOutWorldBorder(double x, double z) {
			act = WorldBorderAction.SET_CENTER;
			this.x = x;
			this.z = z;
		}

		// INITIALIZE
		int	portalTeleportBoundary	= 29999984;

		/** Action: INITIALIZE **/
		public PacketPlayOutWorldBorder(double x, double z, double oldradius, double newradius, long speed,
				int warningTime, int warningBlocks) {
			act = WorldBorderAction.INITIALIZE;
			this.x = x;
			this.z = z;
			this.oldradius = oldradius;
			this.newradius = newradius;
			this.speed = speed;
			this.warningTime = warningTime;
			this.warningBlocks = warningBlocks;
		}

		// SET_WARNING_TIME
		int	warningTime;

		/**
		 * Action: SET WARNING TIME
		 * 
		 * @param TIME
		 *            Does nothing, signifies setting warning time instead of
		 *            warning blocks
		 **/
		public PacketPlayOutWorldBorder(int warningTime, boolean TIME) {
			this.act = WorldBorderAction.SET_WARNING_TIME;
			this.warningTime = warningTime;
		}

		public// SET_WARNING_BLOCKS
		int	warningBlocks;

		/** Action: SET WARNING BLOCKS **/
		public PacketPlayOutWorldBorder(int warningBlocks) {
			this.act = WorldBorderAction.SET_WARNING_BLOCKS;
			this.warningBlocks = warningBlocks;
		}

		public void a(PacketDataSerializer pds) throws IOException {
			this.act = WorldBorderAction.values()[pds.a()];
			switch (act) {
				case SET_SIZE:
					radius = pds.readDouble();
					break;
				case LERP_SIZE:
					oldradius = pds.readDouble();
					newradius = pds.readDouble();
					speed = pds.readLong();
					break;
				case SET_CENTER:
					x = pds.readDouble();
					z = pds.readDouble();
					break;
				case INITIALIZE:
					x = pds.readDouble();
					z = pds.readDouble();
					oldradius = pds.readDouble();
					newradius = pds.readDouble();
					speed = pds.readLong();
					portalTeleportBoundary = pds.a();
					warningTime = pds.a();
					warningBlocks = pds.a();
					break;
				case SET_WARNING_TIME:
					warningTime = pds.a();
					break;
				case SET_WARNING_BLOCKS:
					warningBlocks = pds.a();
					break;
				default:
					break;
			}
		}

		public void b(PacketDataSerializer pds) throws IOException {
			pds.b(act.ordinal());
			switch (act) {
				case SET_SIZE:
					pds.writeDouble(radius);
					break;
				case LERP_SIZE:
					pds.writeDouble(oldradius);
					pds.writeDouble(newradius);
					pds.b((int) speed);
					break;
				case SET_CENTER:
					pds.writeDouble(x);
					pds.writeDouble(z);
					break;
				case INITIALIZE:
					pds.writeDouble(x);
					pds.writeDouble(z);
					pds.writeDouble(oldradius);
					pds.writeDouble(newradius);
					pds.b((int) speed);
					pds.b(portalTeleportBoundary);
					pds.b(warningTime);
					pds.b(warningBlocks);
					break;
				case SET_WARNING_TIME:
					pds.b(warningTime);
					break;
				case SET_WARNING_BLOCKS:
					pds.b(warningBlocks);
					break;
				default:
					break;
			}
		}

		public void handle(PacketListener pl) {
		}

	}

	public static enum WorldBorderAction {
		SET_SIZE,
		LERP_SIZE,
		SET_CENTER,
		INITIALIZE,
		SET_WARNING_TIME,
		SET_WARNING_BLOCKS;
	}
}
