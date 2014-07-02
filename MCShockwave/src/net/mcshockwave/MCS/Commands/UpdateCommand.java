package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((sender instanceof Player && SQLTable.hasRank(((Player) sender).getName(), Rank.ADMIN) || !(sender instanceof Player)
				&& sender.isOp())) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("*")) {
					for (String s : MCShockwave.servers) {
						MCShockwave.serverSideBC(
								s,
								ChatColor.translateAlternateColorCodes('&', "&1&2&3&4&5&6"
										+ (args.length > 1 ? "&7" : "")));
						sender.sendMessage(ChatColor.AQUA + "Updated plugin for server " + s
								+ (args.length > 1 ? " (No Kick)" : ""));
					}
				}

				String server = args[0];

				MCShockwave.serverSideBC(server,
						ChatColor.translateAlternateColorCodes('&', "&1&2&3&4&5&6" + (args.length > 1 ? "&7" : "")));
				sender.sendMessage(ChatColor.AQUA + "Updated plugin for server " + server
						+ (args.length > 1 ? " (No Kick)" : ""));
			}
		}
		return false;
	}

}
