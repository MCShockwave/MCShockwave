package net.mcshockwave.MCS.Commands;

import java.util.concurrent.TimeUnit;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Utils.MiscUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempBanCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("tempban") && sender instanceof Player) {
			Player p = (Player) sender;
			if (SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
				if (args.length >= 3) {
					OfflinePlayer k = Bukkit.getOfflinePlayer(args[0]);
					int min = 0;
					try {
						if (args[1].startsWith("m")) {
							min = Integer.parseInt(args[1].replaceFirst("m", ""));
						} else if (args[1].startsWith("h")) {
							min = Integer.parseInt(args[1].replaceFirst("h", "")) * 60;
						} else if (args[1].startsWith("d")) {
							min = Integer.parseInt(args[1].replaceFirst("d", "")) * 1440;
						}
					} catch (Exception e) {
					}
					if (min < 1)
						return false;
					String reason = args[2];
					for (int i = 3; i < args.length; i++) {
						reason += " " + args[i];
					}
					if (k.isOnline()) {
						k.getPlayer().kickPlayer("Temp-banned for " + min + " minutes for " + reason);
					}
					MCShockwave.sendMessageToRank(
							ChatColor.GOLD + "[" + MCShockwave.server + "] " + ChatColor.RED + k.getName()
									+ " was temp-banned for " + min + " minutes for " + reason + " by "
									+ sender.getName(), Rank.JR_MOD);
					SQLTable.Banned.add("Username", k.getName(), "Reason", "Temp-banned for " + min + " minutes for "
							+ reason + " by " + sender.getName(), "Time",
							min + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) + "", "BannedBy",
							p.getName());
					SQLTable.BanHistory.add("Username", args[0], "BanType", "Temp", "BanReason", reason, "BanGiver",
							p.getName(), "BanTime", args[1], "TimeString", MiscUtils.getTime());
				}
			}
		}
		return false;
	}

	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
