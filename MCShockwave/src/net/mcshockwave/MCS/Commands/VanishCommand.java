package net.mcshockwave.MCS.Commands;

import java.util.HashMap;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

	public static HashMap<Player, Boolean>	vanished	= new HashMap<Player, Boolean>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (!SQLTable.hasRank(p.getName(), Rank.JR_MOD) && !p.isOp())
				return false;

			if (!vanished.containsKey(p)) {
				vanished.put(p, false);
			}
			setVanished(p, !vanished.get(p));
		}
		return false;
	}

	public static void setVanished(Player p, boolean v) {
		vanished.remove(p);
		vanished.put(p, v);
		if (v) {
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				p2.hidePlayer(p);
			}
			p.sendMessage(ChatColor.RED + "Now vanished!");
		} else {
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				p2.showPlayer(p);
			}
			p.sendMessage(ChatColor.GREEN + "Unvanished!");
		}
	}

}
