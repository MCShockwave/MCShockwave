package net.mcshockwave.MCS.Commands;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor, Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("flyspeed")){
				if (args.length == 1) {
					  try {
					    int speed = Integer.parseInt(args[0]);
					    p.setFlySpeed((float)speed / 10.0f);
					    p.sendMessage("Your speed is now " + speed);
					  } catch (IllegalArgumentException e) {
					    p.sendMessage(ChatColor.RED + "Invalid speed: " + args[0] +", must be from 1 to 10.");
					  }
					}
			}
		}
	}