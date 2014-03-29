package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RestrictCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && SQLTable.hasRank(sender.getName(), Rank.MOD) || !(sender instanceof Player)
				&& sender.isOp()) {

			if (args.length > 1) {
				String res = args[0];
				String typ = args[1];

				if (res.equalsIgnoreCase("level")) {
					if (typ.equalsIgnoreCase("none")) {
						MCShockwave.minLevel = -1;
						sender.sendMessage("Level restriction removed");
					} else {
						MCShockwave.minLevel = Integer.parseInt(typ);
						sender.sendMessage("§cRestricted to level " + typ);
					}
				}

				if (res.equalsIgnoreCase("rank")) {
					if (typ.equalsIgnoreCase("none")) {
						MCShockwave.min = null;
						sender.sendMessage("Rank restriction removed");
					} else {
						MCShockwave.min = Rank.valueOf(typ.toUpperCase());
						sender.sendMessage("§cRestricted to rank " + typ);
					}
				}

				if (res.equalsIgnoreCase("slots")) {
					MCShockwave.maxPlayers = Integer.parseInt(typ);
					sender.sendMessage("§cRestricted to " + typ + " slots");
				}
			}
		}
		return false;
	}

}
