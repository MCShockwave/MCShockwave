package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SilenceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && SQLTable.hasRank(sender.getName(), Rank.MOD) || !(sender instanceof Player)
				&& sender.isOp()) {
			MCShockwave.chatSilenced = !MCShockwave.chatSilenced;
			
			if (MCShockwave.chatSilenced) {
				Bukkit.broadcastMessage("§c§lChat has been silenced by " + sender.getName());
			} else {
				Bukkit.broadcastMessage("§c§lChat has been un-silenced by " + sender.getName());
			}
		}
		return false;
	}

}
