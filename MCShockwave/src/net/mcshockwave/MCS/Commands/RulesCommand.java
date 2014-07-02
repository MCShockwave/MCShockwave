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
			for (int i = 0; i < rules.length; i++) {
				p.sendMessage("§7" + (i + 1) + ". §b§o" + rules[i]);
			}
		}
		return false;
	}
}
