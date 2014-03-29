package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Currency.LevelUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EXPCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("exp")) {
			if (args.length == 3
					&& (sender instanceof Player && SQLTable.hasRank(sender.getName(), Rank.ADMIN) || !(sender instanceof Player)
							&& sender.isOp())) {
				if (args[0].equalsIgnoreCase("set")) {
					if (Bukkit.getPlayer(args[1]) != null) {
						LevelUtils.setXP(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
					}
				}
				if (args[0].equalsIgnoreCase("add")) {
					if (Bukkit.getPlayer(args[1]) != null) {
						LevelUtils.addXP(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]), null, false);
					}
				}
			}
		}
		return false;
	}

}
