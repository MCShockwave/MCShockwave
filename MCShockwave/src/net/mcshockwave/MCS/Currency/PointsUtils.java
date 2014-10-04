package net.mcshockwave.MCS.Currency;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PointsUtils {

	public static void addPoints(Player p, int points, String reason) {
		int po = getPoints(p);
		po += points;
		MCShockwave.send(points >= 0 ? ChatColor.GREEN : ChatColor.RED, p, "%s points"
				+ (reason != null ? " for " + reason : ""), (points >= 0 ? "+" : "") + points);
		setPoints(p, po);
	}

	public static int getPoints(Player p) {
		return SQLTable.Points.getInt("Username", p.getName(), "Points");
	}

	public static void setPoints(Player p, int points) {
		SQLTable.Points.set("Points", points + "", "Username", p.getName());
		MCShockwave.updateTab(p);
	}

	public static String[] getTop(int num, boolean includeStaff) {
		LinkedHashMap<String, String> get = SQLTable.Points.getAllOrder("Username", "Points", num);
		boolean recheck = true;
		while (!includeStaff && recheck) {
			recheck = false;
			for (String s : get.keySet()) {
				if (SQLTable.hasRank(s, Rank.JR_MOD)) {
					num++;
					recheck = true;
				}
			}
			if (recheck) {
				get = SQLTable.Points.getAllOrder("Username", "Points", num);
			}
		}
		List<String> ret = new ArrayList<>();
		for (String s : get.keySet()) {
			if (!includeStaff && SQLTable.hasRank(s, Rank.JR_MOD)) {
				continue;
			}
			ret.add(s);
		}
		return ret.toArray(new String[0]);
	}
}
