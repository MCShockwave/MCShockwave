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
	
	public static String[] getTop(int num, boolean staff) {
		LinkedHashMap<String, String> get = SQLTable.Points.getAllOrder("Username", "Points", staff ? num : num + 10);
		List<String> ret = new ArrayList<>();
		for (String s : get.keySet()) {
			if (ret.size() == num) {
				break;
			}
			if (staff) {
				ret.add(s);
			} else {
				if (!SQLTable.hasRank(s, Rank.JR_MOD)) {
					ret.add(s);
				}
			}
		}
		return ret.toArray(new String[0]);
	}
	
	/*public static String[] getTop(int num) {
		LinkedHashMap<String, String> get = SQLTable.Points.getAllOrder("Username", "Points", num);
		List<String> ret = new ArrayList<>();
		for (String s : get.keySet()) {
			ret.add(s);
		}
		return ret.toArray(new String[0]);
	}*/
	
}
