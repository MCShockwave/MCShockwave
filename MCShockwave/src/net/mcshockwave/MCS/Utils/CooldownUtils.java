package net.mcshockwave.MCS.Utils;

import net.mcshockwave.MCS.MCShockwave;

import org.bukkit.Bukkit;

import java.util.HashMap;

public class CooldownUtils {

	private static HashMap<String, HashMap<String, Long>>	cooldowns	= new HashMap<>();

	public static void register(String name) {
		if (!isRegistered(name)) {
			cooldowns.put(name, new HashMap<String, Long>());
		}
	}

	private static boolean isRegistered(String name) {
		return cooldowns.containsKey(name);
	}

	public static void addCooldown(String cool, String name, long time) {
		register(cool);

		cooldowns.get(cool).remove(name);
		cooldowns.get(cool).put(name, System.currentTimeMillis() + (time * 50));
	}

	public static void addCooldown(String cool, String name, long time, Runnable run) {
		addCooldown(cool, name, time);
		Bukkit.getScheduler().runTaskLater(MCShockwave.instance, run, time);
	}

	public static boolean isOnCooldown(String cool, String name) {
		register(cool);

		if (!cooldowns.get(cool).containsKey(name)) {
			return false;
		}

		long time = cooldowns.get(cool).get(name);
		return time > System.currentTimeMillis();
	}

	public static long getCooldownForMillis(String cool, String name) {
		register(cool);

		if (!cooldowns.get(cool).containsKey(name)) {
			return -1;
		}

		long time = cooldowns.get(cool).get(name);
		return time - System.currentTimeMillis();
	}

	public static double getCooldownForSec(String cool, String name, int places) {
		return MiscUtils.getRoundedNumber(getCooldownForSec(cool, name), places);
	}

	public static double getCooldownForSec(String cool, String name) {
		long time = getCooldownForMillis(cool, name);
		if (time == -1) {
			return -1;
		}

		double sec = ((double) time) / 1000;
		return sec;
	}
}
