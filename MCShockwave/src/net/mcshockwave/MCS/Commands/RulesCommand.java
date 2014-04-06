package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String[] rules = SQLTable.Rules.getAll("Rules").toArray(new String[0]);
			p.sendMessage("§a§lMCShockwave Server Network Rules:");
			for (String s : rules) {
				p.sendMessage("§b" + s);
			}
		}
		return false;
	}
}
