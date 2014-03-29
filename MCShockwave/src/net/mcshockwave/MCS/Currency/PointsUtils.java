package net.mcshockwave.MCS.Currency;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PointsUtils {

	public static void addPoints(Player p, int points, String reason, boolean mult) {
		int po = getPoints(p);
		if (mult) {
			points *= getMultiplier(p);
		}
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
	
	public static float getMultiplier(Player p) {
		if (SQLTable.hasRank(p.getName(), Rank.NETHER)) {
			return 3;
		}
		if (SQLTable.hasRank(p.getName(), Rank.OBSIDIAN)) {
			return 2.5f;
		}
		if (SQLTable.hasRank(p.getName(), Rank.EMERALD)) {
			return 2.25f;
		}
		if (SQLTable.hasRank(p.getName(), Rank.DIAMOND)) {
			return 2f;
		}
		if (SQLTable.hasRank(p.getName(), Rank.GOLD)) {
			return 1.75f;
		}
		if (SQLTable.hasRank(p.getName(), Rank.IRON)) {
			return 1.5f;
		}
		if (SQLTable.hasRank(p.getName(), Rank.COAL)) {
			return 1.25f;
		}
		return 1;
	}
	
	public static String[] getTop(int num) {
		LinkedHashMap<String, String> get = SQLTable.Points.getAllOrder("Username", "Points", num);
		List<String> ret = new ArrayList<>();
		for (String s : get.keySet()) {
			ret.add(s);
		}
		return ret.toArray(new String[0]);
	}

}
