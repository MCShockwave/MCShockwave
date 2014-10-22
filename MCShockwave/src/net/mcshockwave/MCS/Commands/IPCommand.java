package net.mcshockwave.MCS.Commands;

import java.util.ArrayList;

import net.mcshockwave.MCS.BanManager;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IPCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (a.length < 1) {
			return false;
		}
		if (!SQLTable.hasRank(s.getName(), Rank.MOD)) {
			return false;
		}
		String toget = a[0];
		if (SQLTable.IPLogs.has("Username", toget)) {
			ArrayList<String> ips = SQLTable.IPLogs.getAll("Username", toget, "IP");
			s.sendMessage(ChatColor.AQUA + "=== IPs for player " + toget + " ===");
			for (String ip : ips) {
				s.sendMessage(ChatColor.RED + ip);
				ArrayList<String> acct = SQLTable.IPLogs.getAll("IP", ip, "Username");
				s.sendMessage(ChatColor.AQUA + "Associated Accounts (Same IP):");
				for (String user : acct) {
					s.sendMessage(ChatColor.GOLD + user + (BanManager.isBanned(user) ? " (Banned)" : ""));
				}
			}
			return true;
		} else {
			s.sendMessage(ChatColor.RED + "No Logged IPs for player " + toget);
		}
		return true;
	}

}
