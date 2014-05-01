package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CriCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("cri")) {
			if (SQLTable.hasRank(sender.getName(), Rank.ENDER)) {
				player.chat(ChatColor.AQUA + ";-; i cri evertim");
			}
		}
		return false;
	}
}