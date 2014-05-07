package net.mcshockwave.MCS.Commands;

import java.util.concurrent.TimeUnit;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Utils.MiscUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (label.equalsIgnoreCase("mute") && args.length >= 1 && SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
				Player p2 = Bukkit.getPlayer(args[0]);
				String reason = "";
				if (args.length >= 3) {
					for (int i = 2; i < args.length; i++) {
						reason += " " + args[i];
					}
				}
				reason = reason.replaceFirst(" ", "");
				int time = 0;
				try {
					if (args[1].startsWith("m")) {
						time = Integer.parseInt(args[1].replaceFirst("m", ""));
					} else if (args[1].startsWith("h")) {
						time = Integer.parseInt(args[1].replaceFirst("h", "")) * 60;
					} else if (args[1].startsWith("d")) {
						time = Integer.parseInt(args[1].replaceFirst("d", "")) * 1440;
					}
				} catch (Exception e) {
				}
				if (time < 1)
					return false;
				if (p2 != null && !SQLTable.hasRank(p2.getName(), Rank.ADMIN)) {
					SQLTable.Muted.add("Username", p2.getName(), "Time",
							time + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) + "");
					SQLTable.BanHistory.add("Username", p2.getName(), "BanType", "Muted", "BanReason",
							((reason == "") ? "(default reason)" : reason), "BanGiver", p.getName(), "BanTime",
							args[1], "TimeString", MiscUtils.getTime());
					MCShockwave.sendMessageToRank(ChatColor.GOLD + "[" + MCShockwave.server + "] " + ChatColor.YELLOW
							+ p.getName() + ": Muted " + p2.getName() + " for " + time + " minutes"
							+ ((reason == "") ? "" : " for " + reason), Rank.JR_MOD);
					p2.sendMessage(ChatColor.RED + p.getName() + " Muted you for " + time + " minutes"
							+ ((reason == "") ? "" : " for " + reason) + "!");
				}
			} else if (label.equalsIgnoreCase("unmute") && args.length == 1
					&& SQLTable.hasRank(p.getName(), Rank.JR_MOD)) {
				Player p2 = Bukkit.getPlayer(args[0]);
				if (p2 != null) {
					SQLTable.Muted.del("Username", p2.getName());
					MCShockwave.sendMessageToRank(ChatColor.GOLD + "[" + MCShockwave.server + "] " + ChatColor.GREEN
							+ p.getName() + ": Unmuted " + p2.getName(), Rank.JR_MOD);
					p2.sendMessage(ChatColor.GREEN + p.getName() + " Unmuted you!");
				}
			}
		}
		return false;
	}

}
