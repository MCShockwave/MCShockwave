package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiplierCommand implements CommandExecutor {

	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (!SQLTable.hasRank(p.getName(), Rank.ADMIN)) {
				return false;
			}
			if (a.length == 0) {
				MCShockwave.send(p, "§6Improper Syntax: §r%s", "Invalid Argument Count");
				return false;
			}
			String type = a[0];
			if (a.length == 1) {
				if (type.equalsIgnoreCase("test")) {
					p.sendMessage("Point Multiplier Local: " + MCShockwave.pointmult);
					p.sendMessage("XP Multiplier Local: " + MCShockwave.xpmult);
					p.sendMessage("Point Multiplier Remote: " + MCShockwave.getGlobalMultiplier(false));
					p.sendMessage("XP Multiplier Remote: " + MCShockwave.getGlobalMultiplier(true));
					return true;
				}
				if (type.equalsIgnoreCase("reset")) {
					// MCShockwave.pointmult = 1;
					// MCShockwave.xpmult = 1;
					MCShockwave.setGlobalMultiplier(false, 1);
					MCShockwave.setGlobalMultiplier(true, 1);
					MCShockwave.broadcast(ChatColor.RED, "Server multipliers %s!", "deactivated");
					return true;
				}
			}
			String m = a[1];
			int multiplier = 0;

			try {
				multiplier = Integer.parseInt(m);
			} catch (NumberFormatException e) {
				MCShockwave.send(p, "§6Improper Syntax: §r%s", "Not a number! " + m);
			}
			if (multiplier == 0 || multiplier == 1) {
				MCShockwave.send(p, "§6Improper Syntax: §r%s", "Can not be " + multiplier + "!");
				return false;
			}
			if (type.equalsIgnoreCase("point")) {
				MCShockwave.broadcast(ChatColor.AQUA, "Server point multiplier %s!", "activated");
				// MCShockwave.pointmult = multiplier;
				MCShockwave.setGlobalMultiplier(false, multiplier);
				return true;
			}
			if (type.equalsIgnoreCase("xp")) {
				MCShockwave.broadcast(ChatColor.AQUA, "Server xp multiplier %s!", "activated");
				// MCShockwave.xpmult = multiplier;
				MCShockwave.setGlobalMultiplier(true, multiplier);
				return true;
			}
			if (type.equalsIgnoreCase("all")) {

			}
		}
		return false;
	}

}
