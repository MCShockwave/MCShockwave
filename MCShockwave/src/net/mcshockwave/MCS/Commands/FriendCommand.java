package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Menu.ItemMenu;
import net.mcshockwave.MCS.Menu.ItemMenu.Button;
import net.mcshockwave.MCS.Menu.ItemMenu.ButtonRunnable;
import net.mcshockwave.MCS.Utils.ItemMetaUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (label.equalsIgnoreCase("status") && args.length >= 1) {
				String s = args[0];

				MCShockwave.pingPlayerLoc(s, p.getName());
				return false;
			}

			if (!SQLTable.Friends.has("Username", p.getName())) {
				SQLTable.Friends.add("Username", p.getName());
			}

			if (label.equalsIgnoreCase("friend") && args.length >= 1) {
				String s = args[0];
				String pfs = SQLTable.Friends.get("Username", p.getName(), "Friends");
				boolean hasFriend = false;

				for (String fs : pfs.split(",")) {
					if (fs.equalsIgnoreCase(s)) {
						hasFriend = true;
					}
				}

				if (hasFriend) {
					pfs = pfs.replaceAll("(?i)," + s, "");
					p.sendMessage(ChatColor.RED + s + " removed as friend");
					MCShockwave.sendMessageToProxy(MCShockwave.pre + p.getName() + " has de-friended you!", s);
				} else {
					if (pfs.split(",").length <= getMaxFriends(p)) {
						pfs += "," + s;
						p.sendMessage(ChatColor.GREEN + s + " added as friend");
						MCShockwave.sendMessageToProxy(MCShockwave.pre + p.getName() + " has friended you!", s);
					} else {
						p.sendMessage(ChatColor.RED + "You have too many friends!");
					}
				}

				SQLTable.Friends.set("Friends", pfs, "Username", p.getName());
			}

			if (label.equalsIgnoreCase("friends")) {
				// MCShockwave.getFriendsList(p.getName());

				getFriendsList(p).open(p);
			}
		}
		return false;
	}

	public static HashMap<String, Integer>	page	= new HashMap<>();
	public static HashMap<String, Integer>	state	= new HashMap<>();

	public static ItemMenu getFriendsList(final Player p) {
		String suf = p.getName().endsWith("s") ? "'" : "'s";
		ItemMenu fr = new ItemMenu(p.getName() + suf + " Friends", 54);
		final Inventory in = fr.i;

		for (int i = in.getSize() - 9; i < in.getSize(); i++) {
			in.setItem(i, ItemMetaUtils.setItemName(new ItemStack(Material.THIN_GLASS), "§r"));
		}

		final String[][] ff = getFriendsFor(p);

		page.remove(p.getName());
		page.put(p.getName(), 0);

		state.remove(p.getName());
		state.put(p.getName(), 0);

		Button friends = new Button(false, Material.EMERALD, 1, 0, "Friends [" + ff[0].length + "]", "Click to view",
				"", "People that have friended", "you, and you have friended", "them back.");
		fr.addButton(friends, in.getSize() - 6);

		Button friended = new Button(false, Material.EMERALD, 1, 0, "Friended [" + ff[1].length + "]", "Click to view",
				"", "People you have friended, but", "have not friended you");
		fr.addButton(friended, in.getSize() - 5);

		Button friendBy = new Button(false, Material.EMERALD, 1, 0, "Friended By [" + ff[2].length + "]",
				"Click to view", "", "People that have friended you,", "but you haven't friended");
		fr.addButton(friendBy, in.getSize() - 4);

		friends.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent event) {
				String[] frs = ff[0];
				for (int i = 0; i < in.getSize() - 9; i++) {
					if (i < frs.length) {
						in.setItem(
								i,
								ItemMetaUtils.setItemName(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "§r"
										+ frs[i]));
					} else
						in.setItem(i, new ItemStack(Material.AIR));
				}

				page.remove(p.getName());
				page.put(p.getName(), 0);

				state.remove(p.getName());
				state.put(p.getName(), 0);

				in.getItem(in.getSize() - 9).setDurability((short) 8);
				if (frs.length > in.getSize() - 9) {
					in.getItem(in.getSize() - 1).setDurability((short) 10);
				} else {
					in.getItem(in.getSize() - 1).setDurability((short) 8);
				}
			}
		});

		friended.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent event) {
				String[] frs = ff[1];
				for (int i = 0; i < in.getSize() - 9; i++) {
					if (i < frs.length) {
						in.setItem(
								i,
								ItemMetaUtils.setItemName(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "§r"
										+ frs[i]));
					} else
						in.setItem(i, new ItemStack(Material.AIR));
				}

				page.remove(p.getName());
				page.put(p.getName(), 0);

				state.remove(p.getName());
				state.put(p.getName(), 1);

				in.getItem(in.getSize() - 9).setDurability((short) 8);
				if (frs.length > in.getSize() - 9) {
					in.getItem(in.getSize() - 1).setDurability((short) 10);
				} else {
					in.getItem(in.getSize() - 1).setDurability((short) 8);
				}
			}
		});

		friendBy.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent event) {
				String[] frs = ff[2];
				for (int i = 0; i < in.getSize() - 9; i++) {
					if (i < frs.length) {
						in.setItem(
								i,
								ItemMetaUtils.setItemName(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "§r"
										+ frs[i]));
					} else
						in.setItem(i, new ItemStack(Material.AIR));
				}

				page.remove(p.getName());
				page.put(p.getName(), 0);

				state.remove(p.getName());
				state.put(p.getName(), 2);

				in.getItem(in.getSize() - 9).setDurability((short) 8);
				if (frs.length > in.getSize() - 9) {
					in.getItem(in.getSize() - 1).setDurability((short) 10);
				} else {
					in.getItem(in.getSize() - 1).setDurability((short) 8);
				}
			}
		});

		final Button next = new Button(false, Material.INK_SACK, 1, 8, "Next Page", "Click to navigate");
		final Button prev = new Button(false, Material.INK_SACK, 1, 8, "Previous Page", "Click to navigate");

		fr.addButton(next, in.getSize() - 1);
		fr.addButton(prev, in.getSize() - 9);

		next.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent event) {
				String[] pl = ff[state.get(p.getName())];
				int pa = page.get(p.getName());

				if (pl.length > (in.getSize() - 9) * (pa + 1)) { // is next page
					page.remove(p.getName());
					page.put(p.getName(), ++pa);
				} else {
					return;
				}

				in.getItem(in.getSize() - 1).setDurability(
						(short) ((pl.length > (in.getSize() - 9) * (pa + 1)) ? 10 : 8));
				in.getItem(in.getSize() - 9).setDurability((short) 10);

				for (int i = 0; i < in.getSize() - 9; i++) {
					if (i < pl.length - ((in.getSize() - 9) * pa)) {
						in.setItem(
								i,
								ItemMetaUtils.setItemName(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "§r"
										+ pl[i + ((in.getSize() - 9) * pa)]));
					} else
						in.setItem(i, new ItemStack(Material.AIR));
				}
			}
		});

		prev.setOnClick(new ButtonRunnable() {
			public void run(Player p, InventoryClickEvent event) {
				String[] pl = ff[state.get(p.getName())];
				int pa = page.get(p.getName());

				if (pa > 0) { // is prev page
					page.remove(p.getName());
					page.put(p.getName(), --pa);
				} else {
					return;
				}

				in.getItem(in.getSize() - 9).setDurability((short) ((pa > 0) ? 10 : 8));
				in.getItem(in.getSize() - 1).setDurability((short) 10);

				for (int i = 0; i < in.getSize() - 9; i++) {
					if (i < pl.length - ((in.getSize() - 9) * pa)) {
						in.setItem(
								i,
								ItemMetaUtils.setItemName(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "§r"
										+ pl[i + ((in.getSize() - 9) * pa)]));
					} else
						in.setItem(i, new ItemStack(Material.AIR));
				}
			}
		});

		return fr;
	}

	public static String[][] getFriendsFor(Player p) {
		ArrayList<String> dobFr = new ArrayList<String>();
		ArrayList<String> hasFr = new ArrayList<String>();
		ArrayList<String> friBy = new ArrayList<String>();

		String pfs = SQLTable.Friends.get("Username", p.getName(), "Friends");
		String[] pfss = pfs.split(",");
		if (pfss.length > 1) {
			for (int i = 1; i < pfss.length; i++) {
				hasFr.add(pfss[i]);
			}

			ArrayList<String> fAll = SQLTable.Friends.getAll("Friends");
			ArrayList<String> fdAll = SQLTable.Friends.getAll("Username");
			for (int fi = 0; fi < fAll.size(); fi++) {
				String s = fAll.get(fi);
				for (String ss : s.split(",")) {
					if (ss.equalsIgnoreCase(p.getName())) {
						friBy.add(fdAll.get(fi));
					}
				}
			}

			String[] hasfrie = hasFr.toArray(new String[0]);
			String[] isfriBy = friBy.toArray(new String[0]);
			for (String s : hasfrie) {
				for (String s2 : isfriBy) {
					if (s2.equalsIgnoreCase(s)) {
						dobFr.add(s);
						hasFr.remove(s);
						friBy.remove(s);
					}
				}
			}
		}
		return new String[][] { dobFr.toArray(new String[0]), hasFr.toArray(new String[0]),
				friBy.toArray(new String[0]) };
	}

	public static int getMaxFriends(Player p) {
		String max = SQLTable.Settings.get("Setting", "FriendLimit", "Value");

		String[] mas = max.split("::");

		int id = 0;
		if (SQLTable.hasRank(p.getName(), Rank.IRON)) {
			id = 1;
		}
		if (SQLTable.hasRank(p.getName(), Rank.GOLD)) {
			id = 2;
		}
		if (SQLTable.hasRank(p.getName(), Rank.DIAMOND)) {
			id = 3;
		}
		if (SQLTable.hasRank(p.getName(), Rank.EMERALD)) {
			id = 4;
		}
		if (SQLTable.hasRank(p.getName(), Rank.OBSIDIAN)) {
			id = 5;
		}
		if (SQLTable.hasRank(p.getName(), Rank.NETHER)) {
			id = 6;
		}
		if (SQLTable.hasRank(p.getName(), Rank.ENDER)) {
			id = 7;
		}
		return Integer.parseInt(mas[id]);
	}

}
