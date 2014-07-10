package net.mcshockwave.MCS.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length >= 1 && sender.isOp()) {
				float speed = 0;
				try {
					speed = Float.parseFloat(args[0]);
				} catch (IllegalArgumentException e) {
					p.sendMessage(ChatColor.RED + "Invalid speed: " + args[0] + ", must be from 1 to 10.");
				}
				if (args.length == 1) {
					p.setFlySpeed((float) speed / 10.0f);
					p.sendMessage("Your speed is now " + speed);
					return true;
				}
				if (args.length == 2) {
					Player t = Bukkit.getPlayer(args[1]);
					t.setFlySpeed((float) speed / 10.0f);
					p.sendMessage(t.getName() + "'s speed is now " + speed);
					return true;
				}
			}
		}

		return false;
	}
}
