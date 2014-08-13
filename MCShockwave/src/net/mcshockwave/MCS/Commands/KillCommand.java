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
				sender.sendMessage("§cSyntax error: /killplayer {name} [optional reason]");
				return false;
			}
			String reason = "";
			for(String s : args) {
			        if(s != args[0]) {
			                reason += s + " ";
			        }
			}

			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage("§c" + args[0] + " is not online.");
				return true;
			}
			target.setHealth(0f);
			target.sendMessage("§7Killed by§6 " + sender.getName() + " §7for §6" + reason);
		}
		return false;
	}
}
