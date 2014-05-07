package net.mcshockwave.MCS;

import net.mcshockwave.MCS.Utils.MiscUtils;

import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

public class BanManager {

	private static SQLTable	bt	= SQLTable.Banned;

	public static boolean isBanned(String name) {
		if (bt.has("Username", name)) {
			int time = bt.getInt("Username", name, "Time");

			if (time == 0 || time > TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())) {
				return true;
			}
		}
		return false;
	}

	public static String getBanReason(String by, String reason, double minutes) {
		if (minutes == -1) {
			return String.format("§aBanned by %s: §f%s §b[Permanent]", by, reason);
		}
		double time = minutes - TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
		String timeUnit = "minute";
		if (time >= 1440) {
			time /= 1440;
			timeUnit = "day";
		} else if (time >= 60) {
			time /= 60;
			timeUnit = "hour";
		}
		if (time != 1) {
			timeUnit += "s";
		}
		return String.format("§aBanned by %s: §f%s §b[%s " + timeUnit + " left]", by, reason,
				MiscUtils.getRoundedNumber(time, 1));
	}

	public static String getBanReason(String name) {
		String by = bt.get("Username", name, "BannedBy");
		String reason = bt.get("Username", name, "Reason");
		int time = bt.getInt("Username", name, "Time");

		return getBanReason(by, reason, time);
	}

	public static void setBanned(String name, int minutes, String reason, String by, String time) {
		if (bt.has("Username", name)) {
			bt.del("Username", name);
		}

		SQLTable.BanHistory.add("Username", name, "BanType", minutes > 0 ? "Temp" : "Perma", "BanReason", reason,
				"BanGiver", by, "BanTime", time, "TimeString", MiscUtils.getTime());

		bt.add("Username", name, "Time",
				"" + (minutes <= -1 ? 0 : minutes + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())),
				"Reason", reason, "BannedBy", by);

		if (Bukkit.getPlayer(name) != null) {
			Bukkit.getPlayer(name).kickPlayer(getBanReason(by, reason, minutes));
		}
	}

}
