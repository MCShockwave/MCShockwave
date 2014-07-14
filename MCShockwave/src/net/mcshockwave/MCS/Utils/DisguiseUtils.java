package net.mcshockwave.MCS.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Random;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class DisguiseUtils {

	public static HashMap<String, DisguiseUtils>	disguised;

	public static void init(Plugin pl) {
		disguised = new HashMap<>();
		ProtocolManager man = ProtocolLibrary.getProtocolManager();

		man.addPacketListener(new PacketAdapter(pl, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
			@Override
			public void onPacketSending(PacketEvent event) {
				WrapperPlayServerNamedEntitySpawn con = new WrapperPlayServerNamedEntitySpawn(event.getPacket());

				for (Player p : Bukkit.getOnlinePlayers()) {
					try {
						if (disguised.containsKey(p.getName())) {
							DisguiseUtils ut = disguised.get(p.getName());
							if (p.getName().equals(con.getPlayerName())) {
								event.setCancelled(true);
								getSpawnPacket(p, ut.type, ut.showName).sendPacket(event.getPlayer());
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void disguise(Player p, EntityType type, boolean showName) {
		undisguise(p);
		disguised.put(p.getName(), new DisguiseUtils(type, showName));

		WrapperPlayServerEntityDestroy dest = new WrapperPlayServerEntityDestroy();
		dest.setEntities(new int[] { p.getEntityId() });

		WrapperPlayServerSpawnEntityLiving ent = getSpawnPacket(p, type, showName);

		for (Player p2 : p.getWorld().getPlayers()) {
			if (p2 != p) {
				dest.sendPacket(p2);
				ent.sendPacket(p2);
			}
		}
	}

	public static WrapperPlayServerSpawnEntityLiving getSpawnPacket(Player p, EntityType type, boolean showName) {
		Location l = p.getLocation();

		WrapperPlayServerSpawnEntityLiving ent = new WrapperPlayServerSpawnEntityLiving();
		ent.setEntityID(p.getEntityId());
		ent.setType(type);
		ent.setHeadPitch(l.getPitch());
		ent.setHeadYaw(l.getYaw());
		ent.setX(l.getX());
		ent.setY(l.getY());
		ent.setZ(l.getZ());

		ent.setMetadata(getWatcher(p, showName));

		return ent;
	}

	public static byte getStatus(Player p) {
		byte status = 0;
		if (p.getFireTicks() > 0) {
			status += 0x01;
		}
		if (p.isSneaking()) {
			status += 0x02;
		}
		if (p.isSprinting()) {
			status += 0x08;
		}
		if (p.isBlocking()) {
			status += 0x10;
		}
		if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			status += 0x20;
		}
		return status;
	}

	public static WrappedDataWatcher getWatcher(Player p, boolean showName) {
		WrappedDataWatcher data = new WrappedDataWatcher();
		data.setObject(0, getStatus(p));
		data.setObject(6, (float) p.getHealth());
		if (showName) {
			data.setObject(10, p.getName());
			data.setObject(11, (byte) 1);
		}
		return data;
	}

	public static void undisguise(Player p) {
		if (disguised.containsKey(p.getName())) {
			DisguiseUtils ut = disguised.get(p.getName());
			disguised.remove(p.getName());
			for (Player p2 : p.getWorld().getPlayers()) {
				p2.hidePlayer(p);
				p2.showPlayer(p);
			}

			WrapperPlayServerEntityDestroy des = new WrapperPlayServerEntityDestroy();
			des.setEntities(new int[] { ut.localid });
			des.sendPacket(p);
		}
	}

	public EntityType	type;
	public boolean		showName;

	public short		localid;
	public Location		lastLoc;

	public DisguiseUtils(EntityType type, boolean showName) {
		this.type = type;
		this.showName = showName;
		localid = getRandomID();
		lastLoc = null;
	}

	static Random	rand	= new Random();

	public static short getRandomID() {
		short ret = -1;
		while (ret < 0) {
			int ch = rand.nextInt(Short.MAX_VALUE);

			ret = (short) ch;
			for (World w : Bukkit.getWorlds()) {
				for (Entity e : w.getEntities()) {
					if (e.getEntityId() == ch) {
						ret = -1;
						break;
					}
				}
			}
		}
		return ret;
	}
}
