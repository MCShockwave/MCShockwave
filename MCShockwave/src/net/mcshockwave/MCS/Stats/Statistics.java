package net.mcshockwave.MCS.Stats;

import org.bukkit.Bukkit;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;

public class Statistics {

	private static SQLTable	t	= SQLTable.Statistics;

	public static void initStats(String pl) {
		if (!t.has("Username", pl)) {
			t.add("Username", pl);
		}
	}

	public static void incrStat(String pl, String st) {
		int s = t.getInt("Username", pl, st);
		t.set(st, ++s + "", "Username", pl);
	}

	public static int getStat(String pl, String st) {
		return t.getInt("Username", pl, st);
	}

	public static void incrKills(String pl) {
		String ser = MCShockwave.server;

		incrStat(pl, "Kills_Total");
		if (ser.startsWith("MG")) {
			incrStat(pl, "Kills_MG");
		}
		if (ser.equalsIgnoreCase("mynerim")) {
			incrStat(pl, "Kills_SG");
		}
		if (ser.equalsIgnoreCase("event") && Bukkit.getPluginManager().getPlugin("UltraHardcore") != null) {
			incrStat(pl, "Kills_UHC");
		}
	}

	public static void incrDeaths(String pl) {
		String ser = MCShockwave.server;

		incrStat(pl, "Deaths_Total");
		if (ser.startsWith("MG")) {
			incrStat(pl, "Deaths_MG");
		}
		if (ser.equalsIgnoreCase("mynerim")) {
			incrStat(pl, "Deaths_SG");
		}
		if (ser.equalsIgnoreCase("event") && Bukkit.getPluginManager().getPlugin("UltraHardcore") != null) {
			incrStat(pl, "Deaths_UHC");
		}
	}

	public static void incrWins(String pl, boolean solo) {
		String ser = MCShockwave.server;

		if (ser.startsWith("MG")) {
			incrStat(pl, "Wins_MG_" + (solo ? "Solo" : "Team"));
		}
		if (ser.equalsIgnoreCase("mynerim")) {
			incrStat(pl, "Wins_SG");
		}
	}

}
