package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetOpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (SQLTable.hasRank(sender.getName(), Rank.ADMIN) || sender == Bukkit.getConsoleSender()) {
			if (args.length == 0) {
				sender.sendMessage("§c/setop {name} {servers}");
				sender.sendMessage("§cServers: +servertoadd -servertoremove +@ for all");
			} else if (args.length > 1) {
				String player = args[0];
				for (int i = 1; i < args.length; i++) {
					String server = args[i];
					if (server.startsWith("+")) {
						String ser = server.replaceFirst("+", "");
						if (ser.equalsIgnoreCase("@")) {
							SQLTable.OPS.del("Username", player);
							SQLTable.OPS.add("Username", player, "Servers", "*");
							sender.sendMessage("§aOpped" + player + " on all servers");
						} else {
							addOpFor(player, ser);
							sender.sendMessage("§aOpped " + player + " on " + ser);
						}
					} else if (server.startsWith("-")) {
						String ser = server.replaceFirst("-", "");
						if (ser.equalsIgnoreCase("@")) {
							SQLTable.OPS.del("Username", player);
							sender.sendMessage("§cDe-opped" + player + " on all servers");
						} else {
							removeOpFor(player, ser);
							sender.sendMessage("§cDe-opped " + player + " on " + ser);
						}
					}
				}
			}
		}

		return false;
	}

	public void addOpFor(String player, String server) {
		if (!SQLTable.OPS.has("Username", player)) {
			SQLTable.OPS.add("Username", player, "Servers", ";" + server);
		} else {
			String cur = SQLTable.OPS.get("Username", player, "Servers");
			cur += ";" + server;
			SQLTable.OPS.set("Servers", cur, "Username", player);
		}
	}

	public void removeOpFor(String player, String server) {
		if (SQLTable.OPS.has("Username", player)) {
			String cur = SQLTable.OPS.get("Username", player, "Servers");
			cur.replaceAll(";" + server, "");
			if (cur.length() > 0) {
				SQLTable.OPS.set("Servers", cur, "Username", player);
			} else {
				SQLTable.OPS.del("Username", player);
			}
		}
	}

}
