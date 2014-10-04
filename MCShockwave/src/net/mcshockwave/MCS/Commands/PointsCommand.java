package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Currency.PointsUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PointsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("points")) {
			if (sender instanceof Player && args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "You have " + ChatColor.GOLD
						+ PointsUtils.getPoints((Player) sender) + ChatColor.GREEN + " points.");
			}
			if (args.length == 3
					&& (sender instanceof Player && SQLTable.hasRank(sender.getName(), Rank.SR_MOD) || !(sender instanceof Player)
							&& sender.isOp())) {
				if (args[0].equalsIgnoreCase("set")) {
					if (Bukkit.getPlayer(args[1]) != null) {
						PointsUtils.setPoints(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
					}
				}
				if (args[0].equalsIgnoreCase("add")) {
					if (Bukkit.getPlayer(args[1]) != null) {
						PointsUtils.addPoints(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]), null);
					}
				}
			}
		}
		return false;
	}

}
