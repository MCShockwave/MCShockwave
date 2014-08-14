package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KillCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (SQLTable.hasRank(sender.getName(), Rank.JR_MOD)) {
			if (args.length < 1) {
				sender.sendMessage("§cSyntax error: /killplayer {name} [optional reason]");
				return false;
			}
			String reason = "";
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					reason += args[i] + " ";
				}
				reason = reason.substring(0, reason.length() - 1);
			}

			final Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage("§c" + args[0] + " is not online.");
				return true;
			}
			target.damage(target.getHealth());
			new BukkitRunnable() {
				public void run() {
					if (!target.isDead()) {
						target.setHealth(0);
					}
				}
			}.runTaskLater(MCShockwave.instance, 1);
			if (reason.length() > 0) {
				target.sendMessage("§7Killed by§6 " + sender.getName() + " §7for §6" + reason);
			}
		}
		return false;
	}
}
