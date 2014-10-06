package net.mcshockwave.MCS.Currency;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.mcshockwave.MCS.DefaultListener;
import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LevelUtils {

	public static void addXP(final Player p, int xp, String reason, boolean mult) {
		int po = getXP(p);
		final int poF = po;
		if (mult) {
			xp *= getMultiplier(p.getName());
			xp *= getBoughtMultiplier(p.getName());
		}
		po += xp;
		MCShockwave.send(xp >= 0 ? ChatColor.GREEN : ChatColor.RED, p, "%s XP"
				+ (reason != null ? " for " + reason : ""), (xp >= 0 ? "+" : "") + xp);
		setXP(p, po);

		if (isLevelUp(poF, xp)) {
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 0);
			p.sendMessage(" ");
			MCShockwave.send(ChatColor.YELLOW, p, "You leveled up! New level: %s", getLevelFromXP(po));
			MCShockwave.send(ChatColor.YELLOW, p, "Type %s to see level info!", "/data");
			int lvl = getLevelFromXP(po);
			if (lvl == 25) {
				MCShockwave.send(ChatColor.YELLOW, p, "You unlocked the %s hub pet!", "Chicken");
			}
			if (lvl == 50) {
				MCShockwave.send(ChatColor.YELLOW, p, "You unlocked the %s hub pet!", "Pig");
			}
			if (lvl == 100) {
				MCShockwave.send(ChatColor.YELLOW, p, "You unlocked the %s hub pet!", "Cow");
			}
			if (lvl == 250) {
				MCShockwave.send(ChatColor.YELLOW, p, "You unlocked the %s hub pet!", "Sheep");
			}
			p.sendMessage(" ");

			Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
				public void run() {
					DefaultListener.setDisplayName(p);
				}
			}, 20L);
		} else
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1.4f);
	}

	public static boolean isLevelUp(int xpSt, int xpAdd) {
		int lvlS = getLevelFromXP(xpSt);
		int lvlE = getLevelFromXP(xpSt + xpAdd);

		return lvlE > lvlS;
	}

	public static int getXP(Player p) {
		return SQLTable.Level.getInt("Username", p.getName(), "XP");
	}

	public static int getXP(String pl) {
		return SQLTable.Level.getInt("Username", pl, "XP");
	}

	public static void setXP(Player p, int xp) {
		SQLTable.Level.set("XP", xp + "", "Username", p.getName());
	}

	public static int getLevelFromXP(int xp) {
		int i = 0;
		while (xp >= getXPPerLevel(i)) {
			xp -= getXPPerLevel(i);
			i++;
		}
		if (i < 1) {
			i = 1;
		}
		return i;
	}

	public static int getOffsetXP(int xp) {
		int i = 0;
		while (xp >= getXPPerLevel(i)) {
			xp -= getXPPerLevel(i);
			i++;
		}
		return xp;
	}

	public static float getPercentDone(int xp) {
		int ofXP = getOffsetXP(xp);
		int req = getXPPerLevel(getLevelFromXP(xp));

		return (ofXP * 100) / req;
	}

	public static int getXPPerLevel(int lev) {
		int xp = lev >= 30 ? (62 + (lev - 30) * 7) : (lev >= 15 ? 17 + (lev - 15) * 3 : 17);
		return xp;
	}

	public static long getTimeLeftBoost(String p) {
		if (ItemsUtils.hasItem(p, SQLTable.MiscItems, "Boost_Time")) {
			int itc = ItemsUtils.getItemCount(p, SQLTable.MiscItems, "Boost_Time");
			long ctm = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
			if (itc >= ctm) {
				return itc - ctm;
			}
		}
		return 0;
	}

	public static float getBoughtMultiplier(String p) {
		if (ItemsUtils.hasItem(p, SQLTable.MiscItems, "Boost_Time")) {
			if (ItemsUtils.getItemCount(p, SQLTable.MiscItems, "Boost_Time") >= TimeUnit.MILLISECONDS.toMinutes(System
					.currentTimeMillis())) {
				return SQLTable.MiscItems.getFloat("Username", p, "Boost_Mult");
			}
		}
		return 1;
	}

	public static float getMultiplier(String pl) {
		if (SQLTable.hasRank(pl, Rank.NETHER)) {
			return 2;
		}
		if (SQLTable.hasRank(pl, Rank.OBSIDIAN)) {
			return 1.6f;
		}
		if (SQLTable.hasRank(pl, Rank.EMERALD)) {
			return 1.5f;
		}
		if (SQLTable.hasRank(pl, Rank.DIAMOND)) {
			return 1.4f;
		}
		if (SQLTable.hasRank(pl, Rank.GOLD)) {
			return 1.3f;
		}
		if (SQLTable.hasRank(pl, Rank.IRON)) {
			return 1.2f;
		}
		if (SQLTable.hasRank(pl, Rank.COAL)) {
			return 1.1f;
		}
		return 1;
	}

	public static ChatColor getSuffixColor(int l) {
		if (l >= 1000) {
			return ChatColor.DARK_AQUA;
		}
		if (l >= 500) {
			return ChatColor.DARK_RED;
		}
		if (l >= 250) {
			return ChatColor.LIGHT_PURPLE;
		}
		if (l >= 150) {
			return ChatColor.BLUE;
		}
		if (l >= 100) {
			return ChatColor.RED;
		}
		if (l >= 50) {
			return ChatColor.GOLD;
		}
		if (l >= 25) {
			return ChatColor.YELLOW;
		}
		if (l >= 10) {
			return ChatColor.GREEN;
		}
		return ChatColor.DARK_GREEN;
	}

	/*public static String[] getTop(int num, boolean includeStaff) {
		LinkedHashMap<String, String> get = SQLTable.Level.getAllOrder("Username", "XP", num);
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
				get = SQLTable.Level.getAllOrder("Username", "XP", num);
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
	}*/
	
	public static String[] getTop(int num) {
		LinkedHashMap<String, String> get = SQLTable.Level.getAllOrder("Username", "XP", num);
		List<String> ret = new ArrayList<>();
		for (String s : get.keySet()) {
			ret.add(s);
		}
		return ret.toArray(new String[0]);
	}
}
