package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BCCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (SQLTable.hasRank(p.getName(), Rank.ADMIN)) {
				if (label.equalsIgnoreCase("bc")) {
					String bc = "";
					for (String s : args) {
						bc += " " + s;
					}
					MCShockwave.broadcast(bc.replaceFirst(" ", ""));
				} else if (label.equalsIgnoreCase("reloadall")) {
					MCShockwave.broadcast("§r§r§r§r§r§r§d§f");
				}
			}
		} else if (sender instanceof ConsoleCommandSender) {
			if (label.equalsIgnoreCase("bc")) {
				String bc = "";
				for (String s : args) {
					bc += " " + s;
				}
				MCShockwave.broadcast(bc.replaceFirst(" ", ""));
			}
		}
		return false;
	}

}
