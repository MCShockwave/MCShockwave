package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SMCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (SQLTable.hasRank(p.getName(), Rank.ADMIN)) {
				String bc = "";
				for (int i = 1; i < args.length; i++) {
					String s = args[i];
					bc += " " + s;
				}
				MCShockwave.serverSideBC(args[0], bc.replaceFirst(" ", ""));
				p.sendMessage(ChatColor.AQUA + "[You -> " + args[0] + "]§r" + ChatColor.translateAlternateColorCodes('&', bc));
			}
		}
		return false;
	}

}
