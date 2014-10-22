package net.mcshockwave.MCS.Commands;

import java.util.ArrayList;

import net.mcshockwave.MCS.BanManager;
import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Utils.MiscUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IPBanCommand implements CommandExecutor {
		
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (!SQLTable.hasRank(s.getName(), Rank.ADMIN)) {
			return false;
		}
		if (a.length < 1) {
			return false;
		}
		String toban = a[0];
		if (SQLTable.hasRank(toban, Rank.ADMIN)) {
			return false;
		}
		if (!SQLTable.IPLogs.has("Username", toban)) {
			s.sendMessage(ChatColor.RED + "No logged IP to ban!");
			return false;
		}
		String reason = "";
		if (a.length > 1) {
			for (int i = 1; i < a.length; i++) {
				reason += a[i] + " ";
			}
		} else {
			reason = "Banned by Admin";
		}
		if (SQLTable.IPBans.has("Username", toban)) {
			SQLTable.IPBans.del("Username", toban);
		}
		ArrayList<String> ips = SQLTable.IPLogs.getAll("Username", toban, "IP");
		for (int i = 0; i < ips.size(); i++) {
			String ip = ips.get(i);
			SQLTable.IPBans.add("Username", toban, "IP", ip, "ID", "" + (i + 1), "BannedBy", s.getName(), "Reason", reason);
		}
		SQLTable.BanHistory.add("Username", toban, "BanType", "IP-Banned", "BanReason", reason, "BanGiver", s.getName(), "BanTime", "Permanent", "TimeString", MiscUtils.getTime());
		if (Bukkit.getPlayer(toban) != null) {
			Bukkit.getPlayer(toban).kickPlayer(BanManager.getBanReason(s.getName(), reason, 0).replace("Banned", "IP-Banned"));
		}
		MCShockwave.sendMessageToRank(("§6[" + MCShockwave.server + "] §e" + s.getName() + " IP-banned " + toban
				+ " for " + reason), Rank.JR_MOD);
		return true;
	}

}