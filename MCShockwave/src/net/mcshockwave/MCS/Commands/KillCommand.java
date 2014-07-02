package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (SQLTable.hasRank(sender.getName(), Rank.JR_MOD)) {
			if (args.length < 1) {
				sender.sendMessage("§cSyntax error: /killplayer {name}");
				return false;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage("§c" + args[0] + " is not online.");
				return true;
			}
			target.setHealth(0f);
		}
		return false;
	}
}
