package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.BanManager;
import net.mcshockwave.MCS.DefaultListener;
import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Challenges.Challenge;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeModifier;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeType;
import net.mcshockwave.MCS.Challenges.ChallengeGenerator;
import net.mcshockwave.MCS.Challenges.ChallengeManager;
import net.mcshockwave.MCS.Currency.LevelUtils;
import net.mcshockwave.MCS.Currency.PointsUtils;
import net.mcshockwave.MCS.Utils.DisguiseUtils;
import net.mcshockwave.MCS.Utils.FakePlayer;
import net.mcshockwave.MCS.Utils.HoloUtils;
import net.mcshockwave.MCS.Utils.ImageUtils;
import net.mcshockwave.MCS.Utils.ItemMetaUtils;
import net.mcshockwave.MCS.Utils.ListUtils;
import net.mcshockwave.MCS.Utils.LocUtils;
import net.mcshockwave.MCS.Utils.MiscUtils;
import net.mcshockwave.MCS.Utils.NametagUtils;
import net.mcshockwave.MCS.Utils.PacketUtils;
import net.mcshockwave.MCS.Utils.PacketUtils.ParticleEffect;
import net.mcshockwave.MCS.Utils.SchedulerUtils;
import net.minecraft.server.v1_7_R2.EntityPlayer;
import net.minecraft.server.v1_7_R2.PacketPlayOutSpawnEntity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class MCSCommand implements CommandExecutor {

	MCShockwave	plugin;

	public MCSCommand(MCShockwave instance) {
		plugin = instance;
	}

	Random	rand	= new Random();

	@Override
	public boolean onCommand(CommandSender sender, Command com, String label, String[] args) {
		if (label.equalsIgnoreCase("mcs")
				&& (sender.isOp() || sender instanceof Player && SQLTable.hasRank(sender.getName(), Rank.JR_MOD))) {
			if (args[0].equalsIgnoreCase("vip")) {
				if (args.length >= 4) {
					setVIP(args[1], Integer.parseInt(args[2]), Long.parseLong(args[3]));
				} else if (args.length == 3) {
					setVIP(args[1], Integer.parseInt(args[2]), -1);
				}
			}
			if (args[0].equalsIgnoreCase("isbanned")) {
				if (!BanManager.isBanned(args[1])) {
					sender.sendMessage("§c" + args[1] + " is not banned");
					return false;
				}
				sender.sendMessage("§eBan reason for player " + args[1] + ":");
				sender.sendMessage(BanManager.getBanReason(args[1]));
			}
			if (args[0].equalsIgnoreCase("join") && sender instanceof Player) {
				Player p = (Player) sender;
				boolean s = false;
				String s2 = SQLTable.Settings.get("Setting", "Silent_Joins", "Value");
				for (String l : s2.split(",")) {
					if (l.equalsIgnoreCase(p.getName())) {
						s = true;
					}
				}
				if (s) {
					p.sendMessage(ChatColor.GREEN + "Silent joins toggled OFF");
				} else
					p.sendMessage(ChatColor.RED + "Silent joins toggled ON");
				if (s) {
					for (String l : s2.split(",")) {
						if (l.equalsIgnoreCase(p.getName())) {
							s2 = s2.replaceAll("," + l, "");
						}
					}
				} else {
					s2 += "," + p.getName();
				}
				SQLTable.Settings.set("Value", s2, "Setting", "Silent_Joins");
			}
			if (args[0].equalsIgnoreCase("crashClient")) {
				Player p = Bukkit.getPlayer(args[1]);

				EntityPlayer nmsPlayer = ((CraftPlayer) p).getHandle();
				PacketPlayOutSpawnEntity pack = new PacketPlayOutSpawnEntity(nmsPlayer, 0);

				PacketUtils.sendPacket(p, pack);
			}
			if (args[0].equalsIgnoreCase("sound") && args.length > 3 && sender instanceof Player) {
				String sound = args[1].toUpperCase();
				float vol = Float.parseFloat(args[2]);
				float pit = Float.parseFloat(args[3]);

				((Player) sender).playSound(((Player) sender).getLocation(), Sound.valueOf(sound), vol, pit);
			}
			if (args[0].equalsIgnoreCase("curtime")) {
				sender.sendMessage(ChatColor.AQUA + "Current time: " + MiscUtils.getTime());
			}
			if (args[0].equalsIgnoreCase("par") && sender instanceof Player) {
				Player p = (Player) sender;

				PacketUtils.playParticleEffect(ParticleEffect.valueOf(args[1].toUpperCase()), p.getEyeLocation(),
						Integer.parseInt(args[2]), Float.parseFloat(args[3]), Integer.parseInt(args[4]));
			}
			if (args[0].equalsIgnoreCase("bpar") && sender instanceof Player) {
				Player p = (Player) sender;

				PacketUtils.playBlockParticles(Material.valueOf(args[1].toUpperCase()), Integer.parseInt(args[2]),
						p.getEyeLocation());
			}
			if (args[0].equalsIgnoreCase("bdpar") && sender instanceof Player) {
				Player p = (Player) sender;

				PacketUtils.playBlockDustParticles(Material.valueOf(args[1].toUpperCase()), Integer.parseInt(args[2]),
						p.getEyeLocation(), Float.parseFloat(args[3]), Float.parseFloat(args[4]));
			}
			if (args[0].equalsIgnoreCase("getTopXp")) {
				sender.sendMessage("Top Players - XP [" + args[1] + "]");
				sender.sendMessage(LevelUtils.getTop(Integer.parseInt(args[1])));
			}
			if (args[0].equalsIgnoreCase("getTopPoints")) {
				sender.sendMessage("Top Players - Points [" + args[1] + "]");
				sender.sendMessage(PointsUtils.getTop(Integer.parseInt(args[1])));
			}
			if (args[0].equalsIgnoreCase("getRankXP")) {
				String pl = args[1];
				int topSearch = Integer.parseInt(args[2]);
				for (int i = 0; i <= topSearch; i++) {
					if (Arrays.asList(LevelUtils.getTop(i)).contains(pl)) {
						sender.sendMessage("§cPlayer " + pl + " is rank " + i + " in XP");
						break;
					} else if (i == topSearch) {
						sender.sendMessage("§cPlayer is not in top " + topSearch);
					}
				}
			}
			if (args[0].equalsIgnoreCase("getRankPoints")) {
				String pl = args[1];
				int topSearch = Integer.parseInt(args[2]);
				for (int i = 0; i <= topSearch; i++) {
					if (Arrays.asList(PointsUtils.getTop(i)).contains(pl)) {
						sender.sendMessage("§cPlayer " + pl + " is rank " + i + " in Points");
						break;
					} else if (i == topSearch) {
						sender.sendMessage("§cPlayer is not in top " + topSearch);
					}
				}
			}
			if (args[0].equalsIgnoreCase("clearHistory")) {
				SQLTable.BanHistory.delWhere("Username='" + args[1] + "'");
				sender.sendMessage("§cCleared history for " + args[1]);
			}
			if (args[0].equalsIgnoreCase("testSched")) {
				List<Object> os = new ArrayList<>();
				os.add(sender);
				for (int i = 1; i < args.length; i++) {
					String s = args[i];
					try {
						int n = Integer.parseInt(s);
						os.add(n);
						continue;
					} catch (Exception e) {
					}
					os.add(s);
				}

				SchedulerUtils.scheduleEvents(os.toArray());
			}
			if (args[0].equalsIgnoreCase("holo") && sender instanceof Player) {
				Player p = (Player) sender;
				// Horse h = (Horse)
				// p.getWorld().spawnEntity(p.getLocation().add(0, 55, 0),
				// EntityType.HORSE);
				// WitherSkull sk = (WitherSkull) p.getWorld().spawnEntity(
				// p.getLocation().getBlock().getLocation().add(0.5, 55.5, 0.5),
				// EntityType.WITHER_SKULL);
				//
				// String name = "";
				// for (int i = 1; i < args.length; i++) {
				// name += ChatColor.translateAlternateColorCodes('&', args[i])
				// + ((args.length == i + 1) ? "" : " ");
				// }
				// h.setCustomName(name);
				// h.setCustomNameVisible(true);
				//
				// sk.setDirection(new Vector());
				// sk.setPassenger(h);
				//
				// NBTTagCompound skNBT = NBTUtils.getEntityNBT(sk);
				// skNBT.setBoolean("Invulnerable", true);
				// NBTUtils.setEntityNBT(sk, skNBT);
				//
				// NBTTagCompound hNBT = NBTUtils.getEntityNBT(h);
				// hNBT.setBoolean("Invulnerable", true);
				// hNBT.setInt("Age", -1700000);
				// NBTUtils.setEntityNBT(h, hNBT);

				String[] lin = new String[args.length - 1];
				for (int i = 1; i < args.length; i++) {
					lin[i - 1] = ChatColor.translateAlternateColorCodes('&', args[i].replace('_', ' '));
				}
				HoloUtils h = new HoloUtils(lin);
				h.show(p.getLocation());
			}
			if (args[0].equalsIgnoreCase("hi") && sender instanceof Player) {
				Player p = (Player) sender;

				BufferedImage image = null;
				try {
					URL url = new URL(args[3]);
					image = ImageIO.read(url);
				} catch (IOException e) {
					e.printStackTrace();
				}

				int height = image.getHeight() / Integer.parseInt(args[1]);
				int width = image.getWidth() / Integer.parseInt(args[2]);

				HoloUtils h = new HoloUtils(ImageUtils.toImgMessage(
						ImageUtils.toChatColorArray(ImageUtils.resizeImage(image, width, height), height),
						ImageUtils.ImgChar.DARK_SHADE.getChar()));
				h.show(p.getLocation());
			}
			if (args[0].equalsIgnoreCase("remholo") && sender instanceof Player) {
				Player p = (Player) sender;
				for (Entity e : p.getNearbyEntities(5, 100, 5)) {
					if (e.getType() == EntityType.WITHER_SKULL || e.getType() == EntityType.HORSE) {
						e.remove();
					}
				}
			}
			if (args[0].equalsIgnoreCase("worldspawn") && sender instanceof Player) {
				Player p = (Player) sender;
				Location l = p.getLocation();
				int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
				p.getWorld().setSpawnLocation(x, y, z);
				p.sendMessage("§aSet World Spawn to " + x + ", " + y + ", " + z);
			}

			// don't ask
			if (args[0].equalsIgnoreCase("cool") && sender instanceof Player) {
				Player p = (Player) sender;
				ArrayList<Location> circle = LocUtils.circle(p, p.getEyeLocation(), Integer.parseInt(args[1]), 1, true,
						false, 1);

				List<Object> os = new ArrayList<>();
				for (Location l : circle) {
					final Location m = l;
					Runnable run = new Runnable() {
						public void run() {
							PacketUtils.playBlockDustParticles(Material.GOLD_BLOCK, 0, m, 0, 0.1f);
						}
					};

					os.add(run);
					os.add(1);
				}
				SchedulerUtils.scheduleEvents(os.toArray());
			}

			if (args[0].equalsIgnoreCase("airstrike") && sender instanceof Player) {
				Player p = (Player) sender;
				final Location l = p.getEyeLocation();

				Random rand = new Random();
				SchedulerUtils u = SchedulerUtils.getNew();

				for (int i = 0; i < Integer.parseInt(args[1]); i++) {
					final Location m = LocUtils.addRand(l, 20, 0, 20).add(0, 50, 0);
					u.add(new Runnable() {
						public void run() {
							TNTPrimed tnt = (TNTPrimed) m.getWorld().spawnEntity(m, EntityType.PRIMED_TNT);
							DefaultListener.tntnoboom.add(tnt);
						}
					});
					u.add(rand.nextInt(10));
				}

				u.execute();

			}

			if (args[0].equalsIgnoreCase("rename") && sender instanceof Player) {
				Player p = (Player) sender;
				ItemStack it = p.getItemInHand();
				String[] sub = ListUtils.subarray(args, 1);
				p.setItemInHand(ItemMetaUtils.setItemName(it,
						ChatColor.translateAlternateColorCodes('&', ListUtils.arrayToString((Object[]) sub))));
			}

			if (args[0].equalsIgnoreCase("viewServerCount")) {
				for (Entry<String, Integer> e : MCShockwave.serverCount.entrySet()) {
					sender.sendMessage("§a" + e.getKey() + " : " + e.getValue() + "<>"
							+ MCShockwave.getPlayerCount(e.getKey()));
				}
			}
			if (args[0].equalsIgnoreCase("pingServers")) {
				sender.sendMessage("§a§oPinging...");
				for (String s : MCShockwave.servers) {
					MCShockwave.sendCommand("BungeeCord", "PlayerCount", s);
				}
				sender.sendMessage("§bPinged!");
			}

			if (args[0].equalsIgnoreCase("giveyoutuber") && SQLTable.hasRank(sender.getName(), Rank.ADMIN)) {
				SQLTable.Youtubers.add("Username", args[1]);
				sender.sendMessage("§c" + args[1] + " added as youtuber");
			}

			if (args[0].equalsIgnoreCase("restartSQL")) {
				SQLTable.restartConnection();
			}

			if (args[0].equalsIgnoreCase("ip")) {
				sender.sendMessage("§aIP: " + Bukkit.getIp());
			}

			if (args[0].equalsIgnoreCase("genChallenge")) {
				Challenge c = ChallengeGenerator.getRandom();

				sender.sendMessage("C: " + c.getDesc());
			}

			if (args[0].equalsIgnoreCase("genNewSet")) {
				int amount = Integer.parseInt(args[1]);

				Challenge[] set = ChallengeManager.generateNewSet(amount);
				ChallengeManager.clearChallenges();
				for (Challenge c : set) {
					ChallengeManager.saveToTable(c);
				}
			}

			if (args[0].equalsIgnoreCase("incrChal")) {
				if (args.length > 5) {
					try {
						ChallengeModifier mod = args[2].equalsIgnoreCase("NONE") ? null : ChallengeModifier
								.valueOf(args[2]);
						String extra = args[3].equalsIgnoreCase("NONE") ? null : args[3];
						ChallengeManager.incrChallenge(ChallengeType.valueOf(args[1]), mod, extra, args[4],
								Integer.parseInt(args[5]), true);
						sender.sendMessage("§aDone!");
					} catch (Exception e) {
						MiscUtils.printStackTrace(e);
					}
				} else {
					sender.sendMessage("§c/mcs incrChal <type> <mod/none> <extra/none> <player> <amount>");
				}
			}

			if (args[0].equalsIgnoreCase("chalProg")) {
				Challenge c = ChallengeManager.getCurrentChallenges()[Integer.parseInt(args[1])];
				sender.sendMessage("Challenge Progress for cid " + c.id);
				sender.sendMessage("String: " + c.progress);
				for (Entry<String, Integer> ent : c.getProgress().entrySet()) {
					sender.sendMessage(ent.getKey() + " : " + ent.getValue());
				}
			}

			if (args[0].equalsIgnoreCase("tasks")) {
				sender.sendMessage("§aPending Tasks: (Plugin.ID [Running/Queued])");
				sender.sendMessage("§e§nAsync§r §6§nSync§r\n §e");
				for (BukkitTask bt : Bukkit.getScheduler().getPendingTasks()) {
					sender.sendMessage((bt.isSync() ? "§6" : "§e")
							+ bt.getOwner().getName()
							+ "."
							+ bt.getTaskId()
							+ " "
							+ (Bukkit.getScheduler().isCurrentlyRunning(bt.getTaskId()) ? "Running" : Bukkit
									.getScheduler().isQueued(bt.getTaskId()) ? "Queued" : "Unknown"));
				}
				sender.sendMessage("§aActive Workers: (ThreadName: Plugin.ID)");
				for (BukkitWorker bw : Bukkit.getScheduler().getActiveWorkers()) {
					sender.sendMessage("§b" + bw.getThread().getName() + ": " + bw.getOwner().getName() + "."
							+ bw.getTaskId());
				}
			}

			if (args[0].startsWith("disguise")) {
				String mod = args[0].replaceFirst("disguise", "");
				EntityType type = EntityType.valueOf(args[1].toUpperCase());
				Player targ = (Player) sender;

				if (args.length > 2) {
					targ = Bukkit.getPlayer(args[2]);
				}

				DisguiseUtils.disguise(targ, type, !mod.contains("h"), mod.contains("s"));
			}

			if (args[0].equalsIgnoreCase("undisguise")) {
				if (args.length > 1) {
					DisguiseUtils.undisguise(Bukkit.getPlayer(args[1]));
				} else {
					DisguiseUtils.undisguise((Player) sender);
				}
			}

			if (args[0].equalsIgnoreCase("listdisguises")) {
				sender.sendMessage("§8All disguises:");
				for (Entry<String, DisguiseUtils> ds : DisguiseUtils.disguised.entrySet()) {
					sender.sendMessage("§c" + ds.getKey() + ": §6" + ds.getValue().toString());
				}
			}

			if (args[0].equalsIgnoreCase("fakeplayer") && sender instanceof Player) {
				Player p = (Player) sender;
				FakePlayer fp = FakePlayer.spawnNew(p.getLocation(), args[1]);

				fp.startAnimation(Integer.parseInt(args[2]), true);
			}

			if (args[0].equalsIgnoreCase("hidetag")) {
				if (args.length > 1) {
					NametagUtils.hideNametag(Bukkit.getPlayer(args[1]));
				} else {
					NametagUtils.hideNametag((Player) sender);
				}
			}
			
			if (args[0].equalsIgnoreCase("showtag")) {
				if (args.length > 1) {
					NametagUtils.showNametag(Bukkit.getPlayer(args[1]));
				} else {
					NametagUtils.showNametag((Player) sender);
				}
			}
		}
		return false;
	}

	public static void setVIP(String p, int id, long time) {
		if (time >= 0) {
			time += TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
		}
		SQLTable.VIPS.del("Username", p);
		SQLTable.VIPS.add("Username", p, "TypeId", id + "", "TimeLeft", time + "");
	}
}
