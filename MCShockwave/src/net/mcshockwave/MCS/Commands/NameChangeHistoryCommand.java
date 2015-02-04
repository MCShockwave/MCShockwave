package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.Utils.MiscUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class NameChangeHistoryCommand implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {

		if (args.length < 1) {
			sender.sendMessage("§cUsage: /nch [user]");
		} else {
			final String user = args[0];
			sender.sendMessage("§aSending request to Mojang servers...");
			new BukkitRunnable() {
				public void run() {
					String[] ncs = MCShockwave.getNameChangesFor(user);
					sender.sendMessage("§6Past usernames for user " + user + ":");
					for (String s : ncs) {
						String show = s;
						String[] ss = s.split("\\@");
						if (ss.length > 1) {
							long time = Long.parseLong(ss[1]);
							// ^heh
							show = ss[0] + " §8at §7" + MiscUtils.getTimeInFormat(time, MiscUtils.DEFAULT_FORMAT);
						}
						sender.sendMessage("§e" + show);
					}
				}
			}.runTaskAsynchronously(MCShockwave.instance);
		}

		return false;
	}

}
