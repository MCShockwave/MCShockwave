package net.mcshockwave.MCS;

import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Commands.BCCommand;
import net.mcshockwave.MCS.Commands.BootsCommand;
import net.mcshockwave.MCS.Commands.ChallengeCommand;
import net.mcshockwave.MCS.Commands.CriCommand;
import net.mcshockwave.MCS.Commands.DataCommand;
import net.mcshockwave.MCS.Commands.EXPCommand;
import net.mcshockwave.MCS.Commands.EditRankCommand;
import net.mcshockwave.MCS.Commands.FriendCommand;
import net.mcshockwave.MCS.Commands.GMItemCommand;
import net.mcshockwave.MCS.Commands.HistoryCommand;
import net.mcshockwave.MCS.Commands.KillCommand;
import net.mcshockwave.MCS.Commands.LoafCommand;
import net.mcshockwave.MCS.Commands.MCSCommand;
import net.mcshockwave.MCS.Commands.MuteCommand;
import net.mcshockwave.MCS.Commands.MultiplierCommand;
import net.mcshockwave.MCS.Commands.PointsCommand;
import net.mcshockwave.MCS.Commands.RedeemCommand;
import net.mcshockwave.MCS.Commands.RestrictCommand;
import net.mcshockwave.MCS.Commands.RoodCommand;
import net.mcshockwave.MCS.Commands.RulesCommand;
import net.mcshockwave.MCS.Commands.SMCommand;
import net.mcshockwave.MCS.Commands.SetOpCommand;
import net.mcshockwave.MCS.Commands.SilenceCommand;
import net.mcshockwave.MCS.Commands.SpeedCommand;
import net.mcshockwave.MCS.Commands.SudoCommand;
import net.mcshockwave.MCS.Commands.TabCommand;
import net.mcshockwave.MCS.Commands.TempBanCommand;
import net.mcshockwave.MCS.Commands.UpdateCommand;
import net.mcshockwave.MCS.Commands.VanishCommand;
import net.mcshockwave.MCS.Commands.VoteCommand;
import net.mcshockwave.MCS.Menu.ItemMenu;
import net.mcshockwave.MCS.Menu.ItemMenu.Button;
import net.mcshockwave.MCS.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.MCS.Menu.ItemMenuListener;
import net.mcshockwave.MCS.Utils.CustomSignUtils.CustomSignListener;
import net.mcshockwave.MCS.Utils.DisguiseUtils;
import net.mcshockwave.MCS.Utils.NametagUtils;
import net.mcshockwave.MCS.Utils.PacketUtils;
import net.mcshockwave.MCS.Utils.PacketUtils.ParticleEffect;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class MCShockwave extends JavaPlugin {

	public static MCShockwave				instance;

	public static String					mesJoin			= "&9%p&7 has joined";

	public static String					mesLeave		= "&9%p&7 has left";

	public static String					mesKick			= "&9%p&7 has left unexpectedly";

	public static boolean					chatSilenced	= false;

	public static String					server			= null;

	public static boolean					chatEnabled		= true;

	public static int						pointmult		= 1;
	public static int						xpmult			= 1;

	public static final String				pre				= ChatColor.translateAlternateColorCodes('&',
																	"&b[&lFriends&b] &r");

	public static Rank						min				= null;
	public static int						minLevel		= -1;

	public static int						maxPlayers		= 50;

	public static String					servers[]		= { "event", "hub", "mynerim", "MG1", "MG2", "MG3", "test",
			"ztd", "build"									};
	public static HashMap<String, Integer>	serverCount		= new HashMap<>();

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new DefaultListener(this), this);
		Bukkit.getPluginManager().registerEvents(new ItemMenuListener(), this);
		Bukkit.getPluginManager().registerEvents(new CustomSignListener(), this);

		getCommand("mcs").setExecutor(new MCSCommand(this));
		getCommand("mute").setExecutor(new MuteCommand());
		getCommand("unmute").setExecutor(new MuteCommand());
		getCommand("tempban").setExecutor(new TempBanCommand());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("history").setExecutor(new HistoryCommand());
		getCommand("bc").setExecutor(new BCCommand());
		getCommand("sm").setExecutor(new SMCommand());
		getCommand("tablist").setExecutor(new TabCommand());
		getCommand("vanish").setExecutor(new VanishCommand());
		getCommand("editrank").setExecutor(new EditRankCommand());
		getCommand("friend").setExecutor(new FriendCommand());
		getCommand("friends").setExecutor(new FriendCommand());
		getCommand("status").setExecutor(new FriendCommand());
		getCommand("reloadall").setExecutor(new BCCommand());
		getCommand("redeem").setExecutor(new RedeemCommand());
		getCommand("sudo").setExecutor(new SudoCommand());
		getCommand("updateplugin").setExecutor(new UpdateCommand());
		getCommand("points").setExecutor(new PointsCommand());
		getCommand("data").setExecutor(new DataCommand());
		getCommand("exp").setExecutor(new EXPCommand());
		getCommand("restrict").setExecutor(new RestrictCommand());
		getCommand("gmitem").setExecutor(new GMItemCommand());
		getCommand("silence").setExecutor(new SilenceCommand());
		getCommand("rules").setExecutor(new RulesCommand());
		getCommand("flyspeed").setExecutor(new SpeedCommand());
		getCommand("killplayer").setExecutor(new KillCommand());
		getCommand("cri").setExecutor(new CriCommand());
		getCommand("lloro").setExecutor(new CriCommand());
		getCommand("setop").setExecutor(new SetOpCommand());
		getCommand("loaf").setExecutor(new LoafCommand());
		getCommand("challenges").setExecutor(new ChallengeCommand());
		getCommand("rood").setExecutor(new RoodCommand());
		getCommand("multiplier").setExecutor(new MultiplierCommand());
		getCommand("boots").setExecutor(new BootsCommand());

		instance = this;

		SQLTable.enable();

		Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
			public void run() {
				for (Player p2 : Bukkit.getOnlinePlayers()) {
					MCShockwave.updateTab(p2);
				}
			}
		}, 10l);

		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				for (String s : MCShockwave.servers) {
					sendCommand("BungeeCord", "PlayerCount", s);
				}
			}
		}, 10, 600);

		String[] incoming = { "BungeeCord", "MCShockwave", "MCSServer", "MCSTips", "MCSServerPing", "MCSFriendPing",
				"SendMessage" };
		String[] outgoing = { "BungeeCord", "MCShockwave", "MCSServer", "MCSServerPing", "MCSFriendPing",
				"MCSFriendsList", "SendMessage", "MCSTellRank", "MCSPrivMes", "MCSReplyMes" };

		for (String s : incoming) {
			Bukkit.getMessenger().registerIncomingPluginChannel(this, s, new DefaultMessageListener());
		}

		for (String s : outgoing) {
			Bukkit.getMessenger().registerOutgoingPluginChannel(this, s);
		}

		pingForServer();

		DisguiseUtils.init(this);

		NametagUtils.init();

		MCShockwave.pointmult = getGlobalMultiplier(false);
		MCShockwave.xpmult = getGlobalMultiplier(true);
	}

	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			DisguiseUtils.undisguise(p);
		}
	}

	public static void broadcast(String message) {
		sendCommand("MCShockwave", message);
	}

	public static void serverSideBC(String server, String message) {
		sendCommand("MCSServer", server + ":::" + message);
	}

	public void pingForServer() {
		sendCommand("MCSServerPing", "GetServer");
	}

	public static void pingPlayerLoc(String player, String playerMes) {
		sendCommand("MCSFriendPing", playerMes + ":::" + player);
	}

	public static void getFriendsList(String player) {
		sendCommand("MCSFriendsList", player);
	}

	public static void sendMessageToProxy(String message, String playerToSend) {
		sendCommand("SendMessage", message, playerToSend);
	}

	public static void privateMessage(String sender, String receiver, String message) {
		sendCommand("MCSPrivMes", message, sender, receiver);
	}

	public static void slashR(String sender, String message) {
		sendCommand("MCSReplyMes", message, sender);
	}

	public static void sendMessageToRank(String message, Rank send) {
		sendCommand("MCSTellRank", message, send.name());
	}

	public static void sendCommand(String toSend, String command, String... args) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try {
			out.writeUTF(command);
			if (args.length > 0) {
				for (String s : args) {
					out.writeUTF(s);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (Bukkit.getOnlinePlayers().size() > 0) {
			Bukkit.getOnlinePlayers().toArray(new Player[0])[0].sendPluginMessage(instance, toSend,
					stream.toByteArray());
		}
	}

	public static void setMinRankRequired(Rank r) {
		min = r;
	}

	public static void setMaxPlayers(int max) {
		maxPlayers = max;
	}

	public static String getBroadcastMessage(String mes, Object... form) {
		return getBroadcastMessage(ChatColor.GOLD, mes, form);
	}

	public static String getBroadcastMessage(ChatColor color, String mes, Object... form) {
		String b = ChatColor.GRAY + mes;
		Object[] format = form;
		for (int i = 0; i < format.length; i++) {
			String s = format[i].toString();
			format[i] = color + ChatColor.ITALIC.toString() + s + ChatColor.GRAY;
		}
		b = String.format(b, format);
		return b;
	}

	public static void broadcast(String mes, Object... form) {
		Bukkit.broadcastMessage(getBroadcastMessage(mes, form));
	}

	public static void broadcast(ChatColor color, String mes, Object... form) {
		Bukkit.broadcastMessage(getBroadcastMessage(color, mes, form));
	}

	public static void send(Player p, String mes, Object... form) {
		p.sendMessage(getBroadcastMessage(mes, form));
	}

	public static void send(ChatColor color, Player p, String mes, Object... form) {
		p.sendMessage(getBroadcastMessage(color, mes, form));
	}

	public static void broadcastAll(String... broad) {
		Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

		for (String s : broad) {
			Bukkit.broadcastMessage(s);
		}

		Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
	}

	public static void sendAll(Player p, String... send) {
		p.sendMessage(ChatColor.DARK_GRAY + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

		for (String s : send) {
			p.sendMessage(s);
		}

		p.sendMessage(ChatColor.DARK_GRAY + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
	}

	public static int getGlobalMultiplier(boolean xp) {
		return SQLTable.NetMultipliers.getInt("MultiplierType", xp ? "XP" : "Point", "Multiplier");
	}

	public static void setGlobalMultiplier(boolean xp, int to) {
		SQLTable.NetMultipliers.del("MultiplierType", xp ? "XP" : "Point");
		SQLTable.NetMultipliers.add("MultiplierType", xp ? "XP" : "Point", "Multiplier", to + "");
	}

	public static Rank getRankForPlayer(Player p) {
		Rank[] ranks = { Rank.ADMIN, Rank.SR_MOD, Rank.MOD, Rank.JR_MOD, Rank.ENDER, Rank.NETHER, Rank.OBSIDIAN,
				Rank.EMERALD, Rank.DIAMOND, Rank.GOLD, Rank.IRON, Rank.COAL };
		for (Rank r : ranks) {
			if (SQLTable.hasRank(p.getName(), r)) {
				return r;
			}
		}
		return null;
	}

	public static int getPlayerCount(String server) {
		if (serverCount.containsKey(server)) {
			return serverCount.get(server);
		}
		return 0;
	}

	public static ItemMenu getMGServers(ItemMenu im) {
		ItemMenu mg = new ItemMenu("MCMinigames Servers", 9);
		Button mgb = new Button(false, Material.DIAMOND_SWORD, 1, 0, "MCMinigames Servers", "Click to view all servers");
		im.addButton(mgb, 0);

		Button mg1 = new Button(true, Material.DIAMOND_SWORD, 1, 0, "MCMinigames Server 1", "Click to join server", "",
				"Players: " + getPlayerCount("MG1") + " / 30");
		Button mg2 = new Button(true, Material.DIAMOND_SWORD, 2, 0, "MCMinigames Server 2", "Click to join server", "",
				"Players: " + getPlayerCount("MG2") + " / 30");
		Button mg3 = new Button(true, Material.DIAMOND_SWORD, 3, 0, "MCMinigames Server 3", "Click to join server", "",
				"Players: " + getPlayerCount("MG3") + " / 30");

		mg1.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent e) {
				connectToServer(p, "mg1", "MCMinigames Server 1");
			}
		});
		mg2.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent e) {
				connectToServer(p, "mg2", "MCMinigames Server 2");
			}
		});
		mg3.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent e) {
				connectToServer(p, "mg3", "MCMinigames Server 3");
			}
		});

		mg.addButton(mg1, 2);
		mg.addButton(mg2, 4);
		mg.addButton(mg3, 6);

		im.addSubMenu(mg, mgb, true);

		return mg;
	}

	public static ItemMenu getServerMenu(Player p) {
		ItemMenu im = new ItemMenu("Servers - " + p.getName(), 9);

		// Minigames submenu
		getMGServers(im);

		// ZTD

		Button ztd = new Button(true, Material.ROTTEN_FLESH, 1, 0, "ZombiezTD", "Click to join server", "", "Players: "
				+ getPlayerCount("ztd") + " / 50");
		ztd.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent e) {
				connectToServer(p, "ztd", "Zombiez Tower Defense");
			}
		});
		im.addButton(ztd, 2);

		// Mynerim

		Button msg = new Button(true, Material.DRAGON_EGG, 1, 0, "Mynerim SG", "Click to join server", "", "Players: "
				+ getPlayerCount("mynerim") + " / 32");
		msg.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent e) {
				connectToServer(p, "mynerim", "Mynerim SG");
			}
		});
		im.addButton(msg, 4);

		// Battle Bane

		Button bb = new Button(true, Material.NETHER_STAR, 1, 0, "Battle Bane", new String[] { "Click to join server",
				"", "Players: " + getPlayerCount("bane") + " / 80" });
		bb.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent e) {
				MCShockwave.connectToServer(p, "bane", "Battle Bane");
			}
		});
		im.addButton(bb, 6);

		// Staff Only Submenu

		if (SQLTable.nickNames.has("Username", p.getName()) || SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
			ItemMenu so = new ItemMenu("Staff Only Servers", 9);
			Button sob = new Button(false, Material.WOOL, 1, 14, "Staff Only Servers", "Click to view all servers");
			im.addButton(sob, 8);

			Button build = new Button(true, Material.GRASS, 1, 0, "Build Server", "Click to join server", "",
					"Players: " + getPlayerCount("build") + " / 60");
			Button test = new Button(true, Material.DIAMOND, 1, 0, "Testing Server", "Click to join server", "",
					"Players: " + getPlayerCount("test") + " / 60");

			build.setOnClick(new ButtonRunnable() {
				public void run(Player p, InventoryClickEvent e) {
					connectToServer(p, "build", "Build Server");
				}
			});
			test.setOnClick(new ButtonRunnable() {
				public void run(Player p, InventoryClickEvent e) {
					connectToServer(p, "test", "Testing Server");
				}
			});

			so.addButton(build, 2);
			so.addButton(test, 6);

			im.addSubMenu(so, sob, true);
		}

		return im;
	}

	public static void connectToServer(Player p, String server, String full) {
		PacketUtils.sendPacketGlobally(p.getLocation(), 16,
				PacketUtils.generateParticles(ParticleEffect.FLAME, p.getLocation(), 1, 0.2f, 200));

		p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		p.sendPluginMessage(instance, "BungeeCord", out.toByteArray());
		p.sendMessage(ChatColor.AQUA + "Connecting to " + full);
	}

	public static void sendRawMessage(Player p, String json) {
		IChatBaseComponent comp = ChatSerializer.a(json);
		PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

	public static void updateTab(final Player p) {
	}

	public static boolean isOp(String name) throws SQLException {
		return isOp(name, server);
	}

	public static boolean isOp(String name, String server) throws SQLException {
		if (SQLTable.Points.getInt("Username", name, "Points") == -1) {
			throw new SQLException("SQL not enabled!");
		}
		if (SQLTable.OPS.has("Username", name)) {
			String servers = SQLTable.OPS.get("Username", name, "Servers");
			String[] ss = servers.split(";");
			String regex = "[1234567890]";
			for (String s : ss) {
				if (s.equalsIgnoreCase(server)
						|| s.replaceAll(regex, "").equalsIgnoreCase(server.replaceAll(regex, "")) || s.equals("*")) {
					return true;
				}
			}
			return false;
		}
		return false;
	}
}
