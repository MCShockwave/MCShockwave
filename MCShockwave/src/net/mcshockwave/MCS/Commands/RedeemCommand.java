package net.mcshockwave.MCS.Commands;

import java.util.ArrayList;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.Currency.PointsUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedeemCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if (args.length > 0) {
				String code = args[0];
				
				ArrayList<String> codes = SQLTable.RedeemCodes.getAll("Code");
				
				if (codes.contains(code)) {
					String pls = SQLTable.RedeemCodes.get("Code", code, "Players");
					for (String pla : pls.split(",")) {
						if (pla.equalsIgnoreCase(p.getName())) {
							p.sendMessage(ChatColor.RED + "You have already redeemed this code!");
							return false;
						}
					}
					
					pls += "," + p.getName();
					SQLTable.RedeemCodes.set("Players", pls, "Code", code);
					int amount = SQLTable.RedeemCodes.getInt("Code", code, "Amount");
					
					PointsUtils.addPoints(p, amount, "redeeming code \"" + code + "\"", false);
					
					MCShockwave.updateTab(p);
				} else {
					p.sendMessage(ChatColor.RED + "Invalid Code! Reminder: Codes are case sensitive");
				}
			}
		}
		return false;
	}

}
