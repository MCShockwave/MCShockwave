package net.mcshockwave.MCS.Commands;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
				if (args.length == 1) {
					List<Ban> bans = getBans(args[0]);
					if (bans != null) {
						p.sendMessage(ChatColor.GRAY + "§nAll bans for player " + args[0] + ":§r\n\n");
						for (Ban b : bans) {
							p.sendMessage(ChatColor.GOLD + b.giver + " §7" + b.type + " §6" + b.player + "§7 for §6"
									+ b.time + "§7 for §6" + b.reason + (b.date != null ? "§7 on §6" + b.date : ""));
						}
					} else {
						p.sendMessage(ChatColor.RED + "No bans for this player.");
					}
				}
			}
		}
		return false;
	}

	public static List<Ban> getBans(String name) {
		try {
			List<Ban> b = new ArrayList<Ban>();
			ResultSet rs = SQLTable.BanHistory.getRSet("Username='" + name + "'");
			while (rs.next()) {
				b.add(new Ban(rs.getString("Username"), rs.getString("BanType"), rs.getString("BanReason"), rs
						.getString("BanGiver"), rs.getString("BanTime"), rs.getString("TimeString")));
			}
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class Ban {
		public String	player, type, reason, giver, time, date;

		public Ban(String player, String type, String reason, String giver, String time, String date) {
			this.player = player;
			if (type.equalsIgnoreCase("Temp") || type.equalsIgnoreCase("Perma")) {
				type += "-Banned";
			}
			this.type = type;
			this.reason = reason;
			this.giver = giver;
			this.time = time;
			this.date = date;
		}
	}

}
