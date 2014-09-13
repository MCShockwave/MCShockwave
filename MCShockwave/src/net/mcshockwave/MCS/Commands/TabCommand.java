package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TabCommand implements CommandExecutor {

	// public static ArrayList<Player> tab = new ArrayList<Player>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;

			if (!Bukkit.getPluginManager().isPluginEnabled("TabAPI")) {
				p.sendMessage("§cTabAPI is disabled!");
				return false;
			}

			MCShockwave.updateTab(p);

			p.sendMessage(ChatColor.GREEN + "Tab list updated!");
		}
		return false;
	}

}
