package net.mcshockwave.MCS.Currency;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;

import org.bukkit.Bukkit;

public class ItemsUtils {

	public static int getItemCount(String p, SQLTable t, String get) {
		return t.getInt("Username", p, get);
	}

	public static void addItem(final String p, final SQLTable t, final String item, int amount) {
		int am = getItemCount(p, t, item);
		am += amount;
		if (!t.has("Username", p)) {
			t.add("Username", p);
		}
		final int amf = am;
		Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
			public void run() {
				t.set(item, amf + "", "Username", p);
			}
		}, 20);
	}

	public static boolean hasItem(String p, SQLTable t, String item) {
		return getItemCount(p, t, item) > 0;
	}

}
