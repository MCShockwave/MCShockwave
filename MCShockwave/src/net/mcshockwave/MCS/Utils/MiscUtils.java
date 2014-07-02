package net.mcshockwave.MCS.Utils;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MiscUtils {

	public static final String	DEFAULT_FORMAT	= "MMM d, yyyy hh:mm:ss a zz";

	public static String getTimeInFormat(long time, String format) {
		DateFormat form = new SimpleDateFormat(format);
		return form.format(new Date(time));
	}

	public static String getTime(long time) {
		return getTimeInFormat(time, DEFAULT_FORMAT);
	}

	public static String getTimeInFormat(String format) {
		return getTimeInFormat(System.currentTimeMillis(), format);
	}

	public static String getTime() {
		return getTime(System.currentTimeMillis());
	}

	public static double getRoundedNumber(double num, int places) {
		double pla = Math.pow(10, places);

		num = Math.round(num * pla);
		num /= pla;

		return num;
	}

	public static String[] readTextFile(String link) {
		ArrayList<String> ret = new ArrayList<>();

		try {
			URL url = new URL(link);
			Scanner in = new Scanner(url.openStream());
			String inputLine;
			while ((inputLine = in.next()) != null) {
				ret.add(inputLine);
			}
			in.close();
		} catch (Exception e) {
		}

		return ret.toArray(new String[0]);
	}

	public static void printStackTrace(Exception e) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (SQLTable.hasRank(p.getName(), Rank.ADMIN)) {
				p.sendMessage("§c§lStack trace please ignore unless needed");
				for (StackTraceElement ste : e.getStackTrace()) {
					String msg = ste.toString();

					p.sendMessage("§4" + msg);
				}
			}
		}
	}
}
