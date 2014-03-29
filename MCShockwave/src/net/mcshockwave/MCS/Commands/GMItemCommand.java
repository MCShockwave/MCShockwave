package net.mcshockwave.MCS.Commands;

import java.util.concurrent.TimeUnit;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Currency.ItemsUtils;
import net.mcshockwave.MCS.Currency.LevelUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GMItemCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) && sender.isOp() || sender instanceof Player
				&& SQLTable.hasRank(sender.getName(), Rank.ADMIN)) {
			final String p = args[1];
			final String it = args[2];

			if (args[0].equalsIgnoreCase("mynesg")) {
				// Mynerim items

				ItemsUtils.addItem(p, SQLTable.MynerimItems, it, args.length > 3 ? Integer.parseInt(args[3]) : 1);
			}

			if (args[0].equalsIgnoreCase("hub")) {
				// Hub items

				ItemsUtils.addItem(p, SQLTable.MiscItems, it, args.length > 3 ? Integer.parseInt(args[3]) : 1);
			}

			if (args[0].equalsIgnoreCase("xpb")) {
				// Hub items

				xpBoost(p, Integer.parseInt(args[2]), Float.parseFloat(args[3]));
			}

		}
		return false;
	}
	
	public void xpBoost(final String p, final int time, final float mult) {
		if (LevelUtils.getTimeLeftBoost(p) > 0) {
			ItemsUtils.addItem(p, SQLTable.MiscItems, "Boost_Time", time);
			if (LevelUtils.getBoughtMultiplier(p) < mult) {
				SQLTable.MiscItems.set("Boost_Mult", mult + "", "Username", p);
			}
		} else {
			ItemsUtils.addItem(p, SQLTable.MiscItems, "Boost_Time", 0);
			Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
				public void run() {
					long min = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
					min += time;
					SQLTable.MiscItems.set("Boost_Time", min + "", "Username", p);
					SQLTable.MiscItems.set("Boost_Mult", mult + "", "Username", p);
				}
			}, 25l);
		}
	}
}
