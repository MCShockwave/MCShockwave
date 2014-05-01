package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && SQLTable.hasRank(sender.getName(), Rank.ADMIN)
				|| sender == Bukkit.getConsoleSender()) {
			String pl = args[0];

			if (Bukkit.getPlayer(pl) != null) {
				String command = args[1].replaceFirst("/", "");

				for (int i = 2; i < args.length; i++) {
					command += " " + args[i];
				}

				if (args[1].charAt(0) == '/') {
					Bukkit.dispatchCommand(Bukkit.getPlayer(pl), command);
				} else {
					// AsyncPlayerChatEvent event = new
					// AsyncPlayerChatEvent(false, Bukkit.getPlayer(pl),
					// command,
					// new
					// HashSet<Player>(Arrays.asList(Bukkit.getOnlinePlayers())));
					//
					// Bukkit.getPluginManager().callEvent(event);
					//
					// for (Player p : event.getRecipients()) {
					// p.sendMessage(event.getFormat());
					// }

					Bukkit.getPlayer(pl).chat(command);
				}
			}
		}
		return false;
	}

}
