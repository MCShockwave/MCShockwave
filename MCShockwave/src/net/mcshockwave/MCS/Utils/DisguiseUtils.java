package net.mcshockwave.MCS.Utils;

import net.minecraft.util.com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerEntityVelocity;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

public class DisguiseUtils {

	public static HashMap<String, DisguiseUtils>	disguised;

	public static ArrayList<String>					noresend	= new ArrayList<>();

	static Plugin									pl;

	public static void init(Plugin plug) {
		pl = plug;
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
							if (p.getUniqueId().equals(con.getPlayerUUID())) {
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

		man.addPacketListener(new PacketAdapter(pl, PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata(event.getPacket());

				Entity m = meta.getEntity(event);
				if (m != null) {
					if (m instanceof Player) {
						Player p = (Player) m;

						if (!noresend.contains(p.getName()) && disguised.containsKey(p.getName())) {
							DisguiseUtils ut = disguised.get(p.getName());
							if (ut.canSeeSelf && p.getEntityId() == meta.getEntityId()) {
								for (WrappedWatchableObject obj : meta.getEntityMetadata()) {
									if (obj.getIndex() == 0) {
										obj.setValue(getStatus(p, true));
									}
								}
								event.setCancelled(true);
								noresend.add(p.getName());
								meta.sendPacket(p);

								WrapperPlayServerEntityMetadata lm = new WrapperPlayServerEntityMetadata();
								lm.setEntityId(ut.localid);
								lm.setEntityMetadata(Arrays.asList(new WrappedWatchableObject(0, getStatus(p))));
								lm.sendPacket(p);
								return;
							}
						}
						noresend.remove(p.getName());
					}
				}
			}
		});

		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onPlayerMove(PlayerMoveEvent event) {
				Player p = event.getPlayer();

				if (disguised.containsKey(p.getName())) {
					DisguiseUtils ut = disguised.get(p.getName());
					if (ut.canSeeSelf) {
						if (ut.lastLoc != null) {
							Location l = event.getTo();

							WrapperPlayServerEntityTeleport tele = new WrapperPlayServerEntityTeleport();
							tele.setEntityID(ut.localid);
							tele.setX(l.getX());
							tele.setY(l.getY());
							tele.setZ(l.getZ());
							tele.setPitch(l.getPitch());
							tele.setYaw(l.getYaw());
							tele.sendPacket(p);

							WrapperPlayServerEntityHeadRotation hr = new WrapperPlayServerEntityHeadRotation();
							hr.setEntityId(ut.localid);
							hr.setHeadYaw(l.getYaw());

							Vector v = p.getVelocity();
							WrapperPlayServerEntityVelocity vel = new WrapperPlayServerEntityVelocity();
							vel.setEntityId(ut.localid);
							vel.setVelocityX(v.getX());
							vel.setVelocityY(v.getY());
							vel.setVelocityZ(v.getZ());
							vel.sendPacket(p);
						}

						ut.lastLoc = event.getTo();
					}
				}
			}
		}, pl);
	}

	public static void disguise(Player p, EntityType type, boolean showName, boolean canSeeSelf) {
		undisguise(p);
		DisguiseUtils ut = new DisguiseUtils(type, showName, canSeeSelf);
		disguised.put(p.getName(), ut);

		WrapperPlayServerEntityDestroy dest = new WrapperPlayServerEntityDestroy();
		dest.setEntities(new int[] { p.getEntityId() });

		WrapperPlayServerSpawnEntityLiving ent = getSpawnPacket(p, type, showName);

		WrapperPlayServerEntityEquipment[] equips = getEquipmentPackets(p);

		if (canSeeSelf) {
			WrapperPlayServerSpawnEntityLiving locent = getSpawnPacket(p, ut.type, ut.showName);
			locent.setEntityID(ut.localid);
			locent.sendPacket(p);
			ut.lastLoc = p.getLocation();

			WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
			meta.setEntityId(p.getEntityId());
			meta.setEntityMetadata(Arrays.asList(new WrappedWatchableObject(0, getStatus(p))));
			meta.sendPacket(p);
		}

		for (Player p2 : p.getWorld().getPlayers()) {
			if (p2 != p) {
				dest.sendPacket(p2);
				ent.sendPacket(p2);
				for (WrapperPlayServerEntityEquipment eq : equips) {
					eq.sendPacket(p2);
				}
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

	public static WrapperPlayServerEntityEquipment[] getEquipmentPackets(Player p) {
		WrapperPlayServerEntityEquipment[] ret = new WrapperPlayServerEntityEquipment[5];

		for (int i = 0; i < ret.length; i++) {
			WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment();

			equip.setEntityId(p.getEntityId());
			equip.setSlot((short) i);
			if (i == 0) {
				equip.setItem(p.getItemInHand());
			} else
				equip.setItem(p.getEquipment().getArmorContents()[i - 1]);

			ret[i] = equip;
		}

		return ret;
	}

	public static byte getStatus(Player p, boolean invis) {
		byte status = 0x00;
		if (p.getFireTicks() > 0) {
			status |= 0x01;
		}
		if (p.isSneaking()) {
			status |= 0x02;
		}
		if (p.isSprinting()) {
			status |= 0x08;
		}
		if (p.isBlocking()) {
			status |= 0x10;
		}
		if (p.hasPotionEffect(PotionEffectType.INVISIBILITY) || invis) {
			status |= 0x20;
		}
		return status;
	}

	public static byte getStatus(Player p) {
		return getStatus(p, false);
	}

	public static WrappedDataWatcher getWatcher(Player p, boolean showName) {
		WrappedDataWatcher data = new WrappedDataWatcher();
		data.setObject(0, getStatus(p));
		float hp = (float) (p.getMaxHealth() * (p.getMaxHealth() / p.getHealth()));
		data.setObject(6, hp);
		if (showName) {
			Team t = p.getScoreboard().getPlayerTeam(p);
			data.setObject(10, (t != null ? t.getPrefix() : "") + p.getName() + (t != null ? t.getSuffix() : ""));
			data.setObject(11, (byte) 1);
		}
		return data;
	}

	public static void undisguise(final Player p) {
		DisguiseUtils ut = null;
		for (Entry<String, DisguiseUtils> ds : Lists.newArrayList(disguised.entrySet())) {
			if (ds.getKey().equalsIgnoreCase(p.getName())) {
				ut = ds.getValue();
				disguised.remove(ds.getKey());
			}
		}

		for (final Player p2 : p.getWorld().getPlayers()) {
			p2.hidePlayer(p);
			p2.showPlayer(p);
		}

		if (ut != null && ut.canSeeSelf) {
			WrapperPlayServerEntityDestroy des = new WrapperPlayServerEntityDestroy();
			des.setEntities(new int[] { ut.localid });
			des.sendPacket(p);
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10, 0));
		}
	}

	public EntityType	type;
	public boolean		showName;
	public boolean		canSeeSelf;

	public short		localid;
	public Location		lastLoc;

	public DisguiseUtils(EntityType type, boolean showName, boolean canSeeSelf) {
		this.type = type;
		this.showName = showName;
		lastLoc = null;

		if (this.canSeeSelf = canSeeSelf) {
			localid = getRandomID();
		}
	}

	static Random	rand	= new Random();

	@Override
	public String toString() {
		return "type:" + type + " showName:" + showName + " canSeeSelf:" + canSeeSelf + " localid:" + localid;
	}

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
