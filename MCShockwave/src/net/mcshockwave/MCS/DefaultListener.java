package net.mcshockwave.MCS;

import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Commands.FriendCommand;
import net.mcshockwave.MCS.Commands.VanishCommand;
import net.mcshockwave.MCS.Commands.VoteCommand;
import net.mcshockwave.MCS.Currency.LevelUtils;
import net.mcshockwave.MCS.Currency.PointsUtils;
import net.mcshockwave.MCS.Menu.ItemMenu;
import net.mcshockwave.MCS.Menu.ItemMenu.Button;
import net.mcshockwave.MCS.Stats.Statistics;
import net.mcshockwave.MCS.Utils.FireworkLaunchUtils;
import net.mcshockwave.MCS.Utils.ItemMetaUtils;
import net.mcshockwave.MCS.Utils.LocUtils;
import net.mcshockwave.MCS.Utils.MiscUtils;
import net.mcshockwave.MCS.Utils.SchedulerUtils;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DefaultListener implements Listener {

	MCShockwave	plugin;

	public DefaultListener(MCShockwave instance) {
		plugin = instance;
	}

	String[]							cuss		= null;

	HashMap<Player, String>				lastMessage	= new HashMap<Player, String>();

	String								VIPLink		= "buy.mcshockwave.net";

	Random								rand		= new Random();

	public static ArrayList<TNTPrimed>	tntnoboom	= new ArrayList<>();

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();

		if (p.getLastDamageCause().getCause() != DamageCause.CUSTOM) {
			Statistics.incrDeaths(p.getName());
			if (p.getKiller() != null) {
				Statistics.incrKills(p.getKiller().getName());
			}
		}
	}

	@EventHandler
	public void vote(InventoryClickEvent event) {
		if (event.getInventory().getName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Vote!")
				&& event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			event.setCancelled(true);
			ItemStack it = event.getCurrentItem();
			if (it != null) {
				String nam = ItemMetaUtils.getItemName(it).replaceFirst(ChatColor.GOLD.toString(), "")
						.replaceAll(" ", "_");
				for (String n : VoteCommand.votes.keySet()) {
					if (n.equalsIgnoreCase(nam)) {
						int v = VoteCommand.votes.get(nam);
						VoteCommand.votes.put(nam, v + 1);
						Bukkit.broadcastMessage(ChatColor.GOLD + p.getName() + " has cast their vote!");
						VoteCommand.voters.add(p.getName());
						p.closeInventory();
					}
				}
			}
		}
	}

	HashMap<Player, Long>	cooldown	= new HashMap<>();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action a = event.getAction();
		ItemStack it = p.getItemInHand();

		if (a == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.PUMPKIN) {
			Location l = event.getClickedBlock().getLocation();
			String loc = l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ();
			String where = "Server='" + MCShockwave.server + "' AND Location='" + loc + "'";
			int id = SQLTable.Scavenger.getIntWhere(where, "ID");
			String players = SQLTable.Scavenger.getWhere(where, "PlayersFound");
			String clue = SQLTable.Scavenger.getWhere(where, "Clue");
			if (players.contains(p.getName())) {
				p.sendMessage(ChatColor.BLUE + clue);
				return;
			}

			for (int i = 0; i < id; i++) {
				for (String s : SQLTable.Scavenger.getAll("ID", i + "", "PlayersFound")) {
					if (!s.contains(p.getName())) {
						return;
					}
				}
			}

			p.sendMessage(ChatColor.RED + "You found the next pumpkin!\n" + ChatColor.AQUA + "Next clue:");
			p.sendMessage(ChatColor.BLUE + clue);

			SQLTable.Scavenger.set("PlayersFound",
					SQLTable.Scavenger.get("ID", id + "", "PlayersFound") + "," + p.getName(), "ID", id + "");
		}

		if (it.getType() == Material.BLAZE_ROD
				&& ItemMetaUtils.hasCustomName(it)
				&& ItemMetaUtils.getItemName(it).equalsIgnoreCase("Blax's Rod")
				&& (cooldown.containsKey(p) && cooldown.get(p) <= System.currentTimeMillis() || !cooldown
						.containsKey(p))) {
			cooldown.put(p, System.currentTimeMillis() + 1000);
			p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
			if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
				for (Entity e : p.getNearbyEntities(8, 8, 8)) {
					if (e != p) {
						Location l = p.getLocation();
						Location l2 = e.getLocation();
						e.setVelocity(new Vector(l2.getX() - l.getX(), 0.6, l2.getZ() - l.getZ()).multiply(5 / l2
								.distance(l)));
					}
				}
				p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
			}
			if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
				for (Entity e : p.getNearbyEntities(10, 10, 10)) {
					if (e != p) {
						Location l = p.getLocation();
						Location l2 = e.getLocation();
						e.setVelocity(new Vector(l.getX() - l2.getX(), -0.4, l.getZ() - l2.getZ()).multiply(5 / l2
								.distance(l)));
					}
				}
				p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			}
		}

		if (it.getType() == Material.NETHER_STAR && ItemMetaUtils.hasCustomName(it)
				&& ItemMetaUtils.getItemName(it).equalsIgnoreCase("Mage Call") && a.name().contains("RIGHT_CLICK")) {
			event.setCancelled(true);

			ArrayList<Location> ci = LocUtils.circle(p, p.getLocation(), 6, 1, true, false, 5);
			final Location pl = p.getLocation();

			SchedulerUtils sc = SchedulerUtils.getNew();

			for (int i = 0; i < ci.size(); i++) {
				final Location l = ci.get(i);
				sc.add(new Runnable() {
					public void run() {
						FireworkLaunchUtils.playFirework(l, Color.YELLOW);
					}
				});
				if (i % 2 == 0) {
					sc.add(4);
				}
			}
			sc.add(8);
			for (int i = 0; i < 5; i++) {
				sc.add(new Runnable() {
					public void run() {
						pl.getWorld().strikeLightningEffect(pl);
					}
				});
				sc.add(3);
			}

			sc.execute();
		}
	}

	@EventHandler
	public void onPlayerChat(final AsyncPlayerChatEvent event) {
		final Player p = event.getPlayer();

		if (MCShockwave.chatSilenced && !SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
			p.sendMessage("§cChat is silenced!");
			event.setCancelled(true);
			return;
		}
		if (cuss == null) {
			cuss = SQLTable.Settings.get("Setting", "CussBlock", "Value").split(",");
		}
		String mes = event.getMessage();
		for (String m : cuss) {
			String star = "";
			for (int i = 0; i < m.length(); i++) {
				star += "*";
			}
			mes = mes.replaceAll("(?i)" + new StringBuffer(m).reverse().toString(), star);
		}
		event.setMessage(mes);
		if (!SQLTable.hasRank(p.getName(), Rank.JR_MOD) && lastMessage.containsKey(p)
				&& lastMessage.get(p).equalsIgnoreCase(event.getMessage())) {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "No spamming, please");
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (p2.isOp()) {
					p2.sendMessage(ChatColor.GOLD + "Player " + p.getName() + " tried to spam. M: "
							+ event.getMessage());
				}
			}
		} else {
			lastMessage.remove(p);
			lastMessage.put(p, event.getMessage());
		}
		if (SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
			event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		}
		p.setDisplayName(p.getDisplayName().replace(
				ChatColor.RED + "[" + ChatColor.BOLD + "MUTED" + ChatColor.RESET + ChatColor.RED + "] "
						+ ChatColor.RESET, ""));
		if (SQLTable.Muted.has("Username", p.getName())) {
			if (SQLTable.Muted.getInt("Username", p.getName(), "Time") < TimeUnit.MILLISECONDS.toMinutes(System
					.currentTimeMillis())) {
				SQLTable.Muted.del("Username", p.getName());
			} else {
				p.setDisplayName(ChatColor.RED + "[" + ChatColor.BOLD + "MUTED" + ChatColor.RESET + ChatColor.RED
						+ "] " + ChatColor.RESET + p.getDisplayName());
				event.getRecipients().clear();
				event.getRecipients().add(p);
				for (Player p2 : Bukkit.getOnlinePlayers()) {
					if (SQLTable.hasRank(p2.getName(), Rank.JR_MOD)) {
						event.getRecipients().add(p2);
					}
				}
				p.sendMessage(ChatColor.RED + "You have been muted.");
			}
		}
		for (Player p2 : Bukkit.getOnlinePlayers()) {
			if (SQLTable.PrivateMutes.hasWhere("Muted", "Username='" + p2.getName() + "' AND Muted='" + p.getName()
					+ "'")) {
				event.getRecipients().remove(p2);
			}
		}
		if (event.getMessage().contains("lemonparty.org")) {
			event.setMessage(event.getMessage().replace("lemonparty.org", "I'm stupid"));
		}
		if (event.getMessage().contains("www.")) {
			event.setMessage(event.getMessage().replace("www.", ""));
		}
		if (event.getMessage().startsWith("@")) {
			event.getRecipients().clear();
			String server = MCShockwave.server != null ? " §a[§l" + MCShockwave.server + "§a]§7" : "";
			String ames = p.getDisplayName() + server + "§f: §7" + mes.replaceFirst("@", "");
			if (!SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
				p.sendMessage(ames);
			}
			MCShockwave.sendMessageToRank(ames, Rank.JR_MOD);
		}
		if (event.getMessage().length() > 3 && event.getMessage().toUpperCase().equals(event.getMessage())) {
			event.setMessage(WordUtils.capitalizeFully(event.getMessage()));
		}
		// if (event.isCancelled() || event.getMessage().startsWith("!"))
		// return;
		// if (MCShockwave.chatEnabled) {
		// for (Player rec : event.getRecipients()) {
		// rec.sendMessage(p.getDisplayName() + ChatColor.RESET + ": " +
		// event.getMessage());
		// }
		// }
		// event.getRecipients().clear();
		event.setFormat(p.getDisplayName() + ChatColor.RESET + ": " + event.getMessage());
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		String message = e.getMessage();
		String mlc = message.toLowerCase();
		Player p = e.getPlayer();
		String[] args = message.split(" ");
		String[] argslc = mlc.split(" ");

		boolean showToAdmins = true;

		List<String> owners = Arrays.asList("Blaxcraft", "Dannyville", "1bennettc", "TrustSean");

		if ((argslc[0].equalsIgnoreCase("/op") || argslc[0].equalsIgnoreCase("/deop"))
				&& !(owners.contains(p.getName()))) {
			e.setCancelled(true);
		}
		if (argslc[0].equalsIgnoreCase("/?") || argslc[0].equalsIgnoreCase("/help")) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "If you need help, contact an online staff member by\n"
					+ "starting your chat message with '@' [no quotes]\n"
					+ "If no staff members are online, try our forums at www.mcshockwave.net");
		}
		if (argslc[0].equalsIgnoreCase("/me") || argslc[0].equalsIgnoreCase("/pl")
				|| argslc[0].equalsIgnoreCase("/plugins")) {
			e.setCancelled(true);
			p.sendMessage("Unknown command. Type \"/help\" for help.");
		}
		if (argslc[0].equalsIgnoreCase("/tell") || argslc[0].equalsIgnoreCase("/w")
				|| argslc[0].equalsIgnoreCase("/msg")) {
			showToAdmins = false;
			boolean muted = SQLTable.Muted.has("Username", p.getName());
			e.setCancelled(true);
			if (!muted) {
				String mes = message;
				for (String m : cuss) {
					String star = "";
					for (int i = 0; i < m.length(); i++) {
						star += "*";
					}
					mes = mes.replaceAll("(?i)" + new StringBuffer(m).reverse().toString(), star);
				}
				message = message.replaceFirst(args[0] + " " + args[1], "");

				MCShockwave.privateMessage(p.getName(), args[1], message);
			}
		}
		if (argslc[0].equalsIgnoreCase("/r")) {
			showToAdmins = false;
			boolean muted = SQLTable.Muted.has("Username", p.getName());
			e.setCancelled(true);
			if (!muted) {
				String mes = message;
				for (String m : cuss) {
					String star = "";
					for (int i = 0; i < m.length(); i++) {
						star += "*";
					}
					mes = mes.replaceAll("(?i)" + new StringBuffer(m).reverse().toString(), star);
				}
				message = message.replaceFirst(args[0], "");

				MCShockwave.slashR(p.getName(), message);
			}
		}
		if (argslc[0].equalsIgnoreCase("/ban") && SQLTable.hasRank(p.getName(), Rank.MOD)) {
			e.setCancelled(true);
			if (SQLTable.hasRank(args[1], Rank.ADMIN)) {
				return;
			}
			String string = "";
			String pre = "Banned by " + p.getName() + ": ";
			if (args.length >= 3) {
				for (int i = 2; i < args.length; i++) {
					string += " " + args[i];
				}
			}
			MCShockwave.sendMessageToRank(
					ChatColor.GOLD + "[" + MCShockwave.server + "] " + ChatColor.RED + p.getName() + " Perma-Banned "
							+ args[1] + " for" + string, Rank.JR_MOD);
			string = pre + string;
			SQLTable.Banned.add("Username", args[1], "Time", 0 + "", "Reason", string, "BannedBy", p.getName());
			SQLTable.BanHistory.add("Username", args[1], "BanType", "Perma", "BanReason", string, "BanGiver",
					p.getName(), "BanTime", "Permanent", "TimeString", MiscUtils.getTime());
			if (Bukkit.getPlayer(args[1]) != null) {
				Bukkit.getPlayer(args[1]).kickPlayer(string);
			}
		}
		if (argslc[0].equalsIgnoreCase("/kick") && SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
			e.setCancelled(true);
			if (SQLTable.hasRank(args[1], Rank.ADMIN)) {
				return;
			}
			String string = "";
			String pre = "Kicked by " + p.getName() + ": ";
			if (args.length >= 3) {
				for (int i = 2; i < args.length; i++) {
					string += " " + args[i];
				}
			}
			MCShockwave.sendMessageToRank(
					ChatColor.GOLD + "[" + MCShockwave.server + "] " + ChatColor.RED + p.getName() + " Kicked "
							+ args[1] + " for " + string, Rank.JR_MOD);
			string = pre + string;
			if (Bukkit.getPlayer(args[1]) != null) {
				Bukkit.getPlayer(args[1]).kickPlayer(string);
			}
		}
		if (argslc[0].equalsIgnoreCase("/pardon") && SQLTable.hasRank(p.getName(), Rank.MOD)) {
			e.setCancelled(true);
			SQLTable.Banned.del("Username", args[1]);
			MCShockwave.sendMessageToRank(
					ChatColor.GOLD + "[" + MCShockwave.server + "] " + ChatColor.GREEN + p.getName() + " pardoned "
							+ args[1], Rank.JR_MOD);
		}
		if (argslc[0].equalsIgnoreCase("/say")) {
			e.setCancelled(true);
			String s = args[1].replace("@r",
					Bukkit.getOnlinePlayers()[rand.nextInt(Bukkit.getOnlinePlayers().length)].getName()).replace('&',
					'§');
			for (int i = 2; i < args.length; i++) {
				s += " "
						+ args[i].replace("@r",
								Bukkit.getOnlinePlayers()[rand.nextInt(Bukkit.getOnlinePlayers().length)].getName())
								.replace('&', '§');
			}
			if (SQLTable.hasRank(p.getName(), Rank.ADMIN)) {
				Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "ADMIN" + ChatColor.DARK_GRAY
						+ "] " + ChatColor.GRAY + s);
			} else if (SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
				Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MODERATOR" + ChatColor.DARK_GRAY
						+ "] " + ChatColor.GRAY + s);
			}
		}
		if (showToAdmins && SQLTable.hasRank(p.getName(), Rank.JR_MOD) && !SQLTable.hasRank(p.getName(), Rank.ADMIN)) {
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (SQLTable.hasRank(p2.getName(), Rank.ADMIN)) {
					p2.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.AQUA + " executed command: "
							+ ChatColor.GOLD + message);
				}
			}
			SQLTable.ModCommands.add("Username", p.getName(), "Command", e.getMessage());
		}
		if (p.isOp()) {
			String mes = e.getMessage();
			
			if (mes.contains("@all")) {
				e.setCancelled(true);
				for (Player p2 : Bukkit.getOnlinePlayers()) {
					p.performCommand(mes.replaceFirst("/", "").replace("@all", p2.getName()));
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		final String pl = event.getPlayer().getName();
		if (SQLTable.Banned.has("Username", pl)) {
			long min = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
			int time = SQLTable.Banned.getInt("Username", pl, "Time");
			if (time <= min && SQLTable.Banned.getInt("Username", pl, "Time") != 0) {
				SQLTable.Banned.del("Username", pl);
			}
		}
		if (SQLTable.Banned.has("Username", pl) || SQLTable.Banned.getInt("Username", pl, "Time") == 0) {
			String s = ChatColor.GREEN + "You are banned from this server: "
					+ SQLTable.Banned.get("Username", pl, "Reason") + "\nAppeal at forums.mcshockwave.net\n";
			int time = SQLTable.Banned.getInt("Username", pl, "Time");
			if (time >= 1) {
				long min = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
				long diff = time - min;
				s += "Time left of ban: " + diff + " minutes.";
			}
			event.disallow(Result.KICK_BANNED, s);
		}

		if (MCShockwave.min != null && !SQLTable.hasRank(pl, MCShockwave.min)
				&& !Bukkit.getWhitelistedPlayers().contains(Bukkit.getOfflinePlayer(pl))) {
			event.disallow(Result.KICK_WHITELIST, ChatColor.GREEN + "Only " + MCShockwave.min.name().replace('_', ' ')
					+ "+'s can join this server!\nBuy VIP at buy.mcshockwave.net");
		}

		if (MCShockwave.minLevel > -1 && LevelUtils.getLevelFromXP(LevelUtils.getXP(pl)) < MCShockwave.minLevel) {
			event.disallow(Result.KICK_WHITELIST, ChatColor.GREEN + "Only level " + MCShockwave.minLevel
					+ "+'s can join this server!\nEarn XP by killing players in our gamemodes to level up!");
		}

		if (!SQLTable.hasRank(pl, Rank.COAL) && MCShockwave.maxPlayers <= Bukkit.getOnlinePlayers().length) {
			event.disallow(Result.KICK_FULL, ChatColor.GREEN + "Server full! Buy VIP to join when a server is full!\n"
					+ VIPLink + "\nOr join another one of our servers!");
		}
	}

	@EventHandler
	public void setLeaveMessage(PlayerQuitEvent event) {
		final Player p = event.getPlayer();
		event.setQuitMessage(MCShockwave.mesLeave.replace('&', '§').replaceAll("%p", p.getName()));

		Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
			public void run() {
				for (Player p2 : Bukkit.getOnlinePlayers()) {
					if (p2 != p) {
						MCShockwave.updateTab(p2);
					}
				}
			}
		}, 10l);

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
				"enjin setpoints " + p.getName() + " " + PointsUtils.getPoints(p));
	}

	@EventHandler
	public void setKickMessage(PlayerKickEvent event) {
		final Player p = event.getPlayer();
		event.setLeaveMessage(MCShockwave.mesKick.replace('&', '§').replaceAll("%p", p.getName()));

		Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
			public void run() {
				for (Player p2 : Bukkit.getOnlinePlayers()) {
					if (p2 != p) {
						MCShockwave.updateTab(p2);
					}
				}
			}
		}, 10l);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();

		if (SQLTable.hasRank(p.getName(), Rank.COAL)) {
			int time = SQLTable.VIPS.getInt("Username", p.getName(), "TimeLeft");
			if (time >= TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())) {
				long timeleft = time - TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
				p.sendMessage(ChatColor.GREEN + "You have " + timeleft + " days of VIP left!");
			}
		}

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				setDisplayName(p);
			}
		}, 1L);

		event.setJoinMessage(MCShockwave.mesJoin.replace('&', '§').replaceAll("%p", p.getName()));
		if (SQLTable.Settings.get("Setting", "Silent_Joins", "Value").contains("," + p.getName())) {
			event.setJoinMessage("");
		}

		for (Player p2 : VanishCommand.vanished.keySet()) {
			if (!VanishCommand.vanished.get(p2))
				continue;
			p.hidePlayer(p2);
		}

		Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
			public void run() {
				// TabAPI.disableTabForPlayer(p);
				// TabAPI.setPriority(MCShockwave.instance, p, -2);

				MCShockwave.updateTab(p);

				for (Player p2 : Bukkit.getOnlinePlayers()) {
					if (p2 != p) {
						MCShockwave.updateTab(p2);
					}
				}
			}
		}, 10l);

		Statistics.initStats(p.getName());

		try {
			p.sendMessage(SQLTable.Settings.get("Setting", "JM-" + MCShockwave.server, "Value"));
		} catch (Exception e) {
		}
	}

	public static String getPrefix(Player p) {
		String pre = SQLTable.nickNames.get("Username", p.getName(), "Prefix");

		String vipPre = "";
		if (!SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
			if (SQLTable.hasRank(p.getName(), Rank.COAL)) {
				vipPre = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "C" + ChatColor.DARK_GRAY + "oal "
						+ ChatColor.RESET;
				Rank.COAL.suf.addPlayer(p);
			}
			if (SQLTable.hasRank(p.getName(), Rank.IRON)) {
				vipPre = ChatColor.GRAY + "" + ChatColor.BOLD + "I" + ChatColor.GRAY + "ron " + ChatColor.RESET;
				Rank.IRON.suf.addPlayer(p);
			}
			if (SQLTable.hasRank(p.getName(), Rank.GOLD)) {
				vipPre = ChatColor.YELLOW + "" + ChatColor.BOLD + "G" + ChatColor.YELLOW + "old " + ChatColor.RESET;
				Rank.GOLD.suf.addPlayer(p);
			}
			if (SQLTable.hasRank(p.getName(), Rank.DIAMOND)) {
				vipPre = ChatColor.AQUA + "" + ChatColor.BOLD + "D" + ChatColor.AQUA + "iamond " + ChatColor.RESET;
				Rank.DIAMOND.suf.addPlayer(p);
			}
			if (SQLTable.hasRank(p.getName(), Rank.EMERALD)) {
				vipPre = ChatColor.GREEN + "" + ChatColor.BOLD + "E" + ChatColor.GREEN + "merald " + ChatColor.RESET;
				Rank.EMERALD.suf.addPlayer(p);
			}
			if (SQLTable.hasRank(p.getName(), Rank.OBSIDIAN)) {
				vipPre = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "O" + ChatColor.DARK_PURPLE + "bsidian "
						+ ChatColor.RESET;
				Rank.OBSIDIAN.suf.addPlayer(p);
			}
			if (SQLTable.hasRank(p.getName(), Rank.NETHER)) {
				vipPre = ChatColor.DARK_RED + "" + ChatColor.BOLD + "N" + ChatColor.DARK_RED + "ether "
						+ ChatColor.RESET;
				Rank.NETHER.suf.addPlayer(p);
			}
			if (SQLTable.hasRank(p.getName(), Rank.ENDER)) {
				vipPre = "§0§lE§0nder §r";
				Rank.ENDER.suf.addPlayer(p);
			}
			if (SQLTable.Youtubers.has("Username", p.getName())) {
				vipPre = ChatColor.RED + "§c§lYou§cTuber " + ChatColor.RESET;
			}
		}

		return (pre == null || pre.equals("") ? "" : pre + " ") + vipPre;
	}

	public static void setDisplayName(Player p) {
		String name = p.getName();
		if (!SQLTable.hasRank(p.getName(), Rank.COAL)) {
			p.setDisplayName(p.getName());
		}
		if (SQLTable.hasRank(p.getName(), Rank.ADMIN)) {
			name = ChatColor.RED + p.getName();
			p.setOp(true);
			Rank.ADMIN.suf.addPlayer(p);
		} else if (SQLTable.hasRank(p.getName(), Rank.MOD)) {
			name = ChatColor.GOLD + p.getName();
			Rank.MOD.suf.addPlayer(p);
		} else if (SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
			name = ChatColor.GOLD + p.getName();
			Rank.JR_MOD.suf.addPlayer(p);
		}
		if (SQLTable.nickNames.has("Username", p.getName())
				&& SQLTable.nickNames.get("Username", p.getName(), "Color") != null
				&& SQLTable.nickNames.get("Username", p.getName(), "Nickname") != null) {
			name = ChatColor.valueOf(SQLTable.nickNames.get("Username", p.getName(), "Color"))
					+ SQLTable.nickNames.get("Username", p.getName(), "Nickname");
		}
		String pre = getPrefix(p);
		if (pre != null && pre != "") {
			name = "§7" + pre + "§r" + name;
		}

		int level = LevelUtils.getLevelFromXP(LevelUtils.getXP(p));
		String levelS = String.format("§8[%s§l%s§8]", LevelUtils.getSuffixColor(level), level);
		name = levelS + " " + name;
		name += "§r";

		p.setDisplayName(name + ChatColor.RESET);
		if (MCShockwave.server.equalsIgnoreCase("hub")) {
			p.setScoreboard(MCShockwave.suffix);
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (tntnoboom.contains(event.getEntity())) {
			event.blockList().clear();
			tntnoboom.remove(event.getEntity());
		}
	}

	public Button getButtonFromSlot(HashMap<Button, Integer> bs, int slot) {
		for (Button b : bs.keySet()) {
			if (b.getItemMenu().buttons.get(b) == slot) {
				return b;
			}
		}
		throw new IllegalArgumentException("No Button Found");
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		final Inventory i = event.getInventory();
		HumanEntity he = event.getWhoClicked();
		ItemStack cu = event.getCurrentItem();

		if (he instanceof Player) {
			final Player p = (Player) he;

			if (i.getName().endsWith(" Friends") && i.getName().startsWith(p.getName())) {
				if (cu.getType() == Material.SKULL_ITEM && ItemMetaUtils.hasCustomName(cu)) {
					String name = ItemMetaUtils.getItemName(cu);
					name = ChatColor.stripColor(name);

					p.performCommand("friend " + name);
					p.closeInventory();
					final int st = FriendCommand.state.get(p.getName());
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						public void run() {
							ItemMenu m = FriendCommand.getFriendsList(p);
							m.open(p);

							getButtonFromSlot(m.buttons, i.getSize() - 6 + st).onClick.run(p, event);
						}
					}, 1l);
				}
			}

			if (i.getName().startsWith("Edit Rank: ")) {
				event.setCancelled(true);
				String edit = i.getName().replaceFirst("Edit Rank: ", "");

				if (cu.getType() == Material.STICK) {
					exec(p, edit, "REMOVE");
				}
				if (cu.getType() == Material.BLAZE_ROD) {
					SQLTable.nickNames.del("Username", edit);
				}
				if (cu.getType() == Material.COAL) {
					exec(p, edit, "COAL");
				}
				if (cu.getType() == Material.IRON_INGOT) {
					exec(p, edit, "IRON");
				}
				if (cu.getType() == Material.GOLD_INGOT) {
					exec(p, edit, "GOLD");
				}
				if (cu.getType() == Material.DIAMOND) {
					exec(p, edit, "DIAMOND");
				}
				if (cu.getType() == Material.EMERALD) {
					exec(p, edit, "EMERALD");
				}
				if (cu.getType() == Material.OBSIDIAN) {
					exec(p, edit, "OBSIDIAN");
				}
				if (cu.getType() == Material.NETHERRACK) {
					exec(p, edit, "NETHER");
				}
				if (cu.getType() == Material.ENDER_STONE) {
					exec(p, edit, "ENDER");
				}

				if (cu.getType() == Material.WOOL) {
					short data = cu.getDurability();

					if (data == 4) {
						exec(p, edit, "JR_MOD");
						exec(p, edit, "prefix Jr._Moderator");
						exec(p, edit, "nick color YELLOW");
						exec(p, edit, "nick name " + edit);
					}
					if (data == 1) {
						exec(p, edit, "MOD");
						exec(p, edit, "prefix Moderator");
						exec(p, edit, "nick color GOLD");
						exec(p, edit, "nick name " + edit);
					}
					if (data == 3) {
						exec(p, edit, "MOD");
						exec(p, edit, "prefix Sr._Moderator");
						exec(p, edit, "nick color AQUA");
						exec(p, edit, "nick name " + edit);
					}
					if (data == 8) {
						exec(p, edit, "prefix Builder");
					}
				}

				p.closeInventory();
			}
		}
	}

	public void exec(Player p, String edit, String s) {
		Bukkit.dispatchCommand(p, "editrank " + edit + " " + s);
	}

}
