package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand implements CommandExecutor {
	
@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (sender instanceof Player && SQLTable.hasRank(sender.getName(), Rank.JR_MOD)) {
		Player target = sender.getServer().getPlayer(args[0);
                if (target == null) {
                    sender.sendMessage(args[0] + " is not online.");
                    return true;
         	}
         	Player target = sender.getServer().getPlayer(args[0]);
                target.setHealth(0);
	}
        return false;
}
