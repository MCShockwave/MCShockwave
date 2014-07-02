package net.mcshockwave.MCS;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class DefaultMessageListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String s, Player p, byte[] ba) {
		if (s.equalsIgnoreCase("MCShockwave")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(ba);
			DataInputStream in = new DataInputStream(stream);
			try {
				String str = in.readUTF();
				if (str.equals("§r§r§r§r§r§r§d§f")) {
					Bukkit.reload();
					Bukkit.reload();
				} else {
					Bukkit.broadcastMessage(ChatColor.RED + "[Broadcast] " + ChatColor.WHITE + str.replaceAll("&", "§"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (s.equalsIgnoreCase("SendMessage")) {
			ByteArrayInputStream stream = new ByteArrayInputStream(ba);
			DataInputStream in = new DataInputStream(stream);
			String str = null;

			try {
				str = in.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (str != null) {
				String[] spl = str.split("::");
				String player = spl[0];
				String mes = spl[1];

				Player to = Bukkit.getPlayer(player);

				if (to != null) {
					to.sendMessage(mes);
				}
			}
		}

		if (s.equalsIgnoreCase("MCSServer")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(ba);
			DataInputStream in = new DataInputStream(stream);
			String broad = null;
			try {
				broad = in.readUTF().replaceAll("&", "§");
			} catch (IOException e) {
				e.printStackTrace();
			}

			String upPl = ChatColor.translateAlternateColorCodes('&', "&1&2&3&4&5&6");
			if (broad.startsWith(upPl)) {
				if (broad.equals(upPl)) {
					for (Player p2 : Bukkit.getOnlinePlayers()) {
						p2.kickPlayer(ChatColor.RED + "Server is being updated! Reconnect to continue playing!");
					}
				} else if (broad.equals(upPl + "§7")) {
					Bukkit.broadcastMessage("§cServer updated!");
				}
				Plugin ad = Bukkit.getPluginManager().getPlugin("AutoDownloader");
				if (ad != null) {
					ad.onDisable();
				}
				Bukkit.reload();
			} else
				Bukkit.broadcastMessage(ChatColor.AQUA + "[Server Broadcast] " + ChatColor.WHITE + broad);
		}

		if (s.equalsIgnoreCase("MCSTips")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(ba);
			DataInputStream in = new DataInputStream(stream);
			try {
				Bukkit.broadcastMessage(SQLTable.Settings.get("Setting", "TipFormat", "Value") + " "
						+ in.readUTF().replaceAll("&", "§"));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (s.equalsIgnoreCase("MCSServerPing")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(ba);
			DataInputStream in = new DataInputStream(stream);
			try {
				MCShockwave.server = in.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (s.equalsIgnoreCase("MCSFriendPing")) {
			ByteArrayInputStream stream = new ByteArrayInputStream(ba);
			DataInputStream in = new DataInputStream(stream);
			String utf = null;
			try {
				utf = in.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String[] utfs = utf.split("::");

			String command = utfs[0];
			String player = utfs[1];
			String playerFound = utfs[2];
			String loc = utfs[3];

			if (Bukkit.getPlayer(player) != null) {
				Player pl = Bukkit.getPlayer(player);
				if (command.equalsIgnoreCase("StatCommand")) {
					if (!loc.equals("Offline")) {
						pl.sendMessage(ChatColor.AQUA + "Player " + playerFound + " is located at " + loc);
					} else {
						pl.sendMessage(ChatColor.RED + "Player " + playerFound + " is offline");
					}
				} else if (command.equalsIgnoreCase("StatusChange")) {
					if (!loc.equals("Offline")) {
						pl.sendMessage(MCShockwave.pre + playerFound + " has joined " + ChatColor.AQUA + loc);
					} else {
						pl.sendMessage(MCShockwave.pre + playerFound + " has gone " + ChatColor.RED + "offline");
					}
				}
			}
		}

		if (s.equalsIgnoreCase("BungeeCord")) {
			ByteArrayInputStream stream = new ByteArrayInputStream(ba);
			DataInputStream in = new DataInputStream(stream);
			String cmd = null;
			try {
				cmd = in.readUTF();
			} catch (Exception e) {
			}

			if (cmd.equalsIgnoreCase("PlayerCount")) {
				try {
					String ser = in.readUTF();
					int players = in.readInt();

					MCShockwave.serverCount.remove(ser);
					MCShockwave.serverCount.put(ser, players);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
