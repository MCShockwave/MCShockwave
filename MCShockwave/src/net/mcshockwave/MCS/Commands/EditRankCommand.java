package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Utils.ItemMetaUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EditRankCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (!SQLTable.hasRank(p.getName(), Rank.ADMIN))
				return false;

			if (args.length > 1 && args[1].equalsIgnoreCase("GUI")) {
				Inventory i = Bukkit.createInventory(null, 18, "Edit Rank: " + args[0]);
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.STICK), "Remove Rank"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.BLAZE_ROD), "Remove Prefix"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.COAL), "Coal VIP"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.IRON_INGOT), "Iron VIP"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.GOLD_INGOT), "Gold VIP"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.DIAMOND), "Diamond VIP"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.EMERALD), "Emerald VIP"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.OBSIDIAN), "Obsidian VIP"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.NETHERRACK), "Nether VIP"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.ENDER_STONE), "Ender VIP"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.WOOL, 1, (short) 4), "Jr. Mod"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.WOOL, 1, (short) 1), "Mod"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.WOOL, 1, (short) 3), "Sr. Mod"));
				i.addItem(ItemMetaUtils.setItemName(new ItemStack(Material.WOOL, 1, (short) 8), "Builder"));
				
				p.openInventory(i);
				return false;
			}

			try {
				String name = args[0];
				String ra = args[1];
				if (ra.equalsIgnoreCase("REMOVE")) {
					SQLTable.VIPS.del("Username", name);
					SQLTable.JunMODS.del("Username", name);
					SQLTable.MODS.del("Username", name);
					SQLTable.nickNames.del("Username", name);
					SQLTable.Youtubers.del("Username", name);
					p.sendMessage("§c§lRemoved rank for " + name);
					return true;
				}
				Rank r = null;
				try {
					r = Rank.valueOf(ra.toUpperCase());
				} catch (Exception e) {
				}
				if (r != null) {
					SQLTable.setRank(name, r);
					p.sendMessage(ChatColor.GREEN + "Player " + name + "'s rank set to " + ra);
				} else {
					if (ra.equalsIgnoreCase("prefix")) {
						String pre = args[2].replace('_', ' ');
						if (SQLTable.nickNames.has("Username", name)) {
							SQLTable.nickNames.set("Prefix", pre, "Username", name);
						} else {
							SQLTable.nickNames.add("Username", name, "Prefix", pre);
						}
						p.sendMessage(ChatColor.GREEN + "Player " + name + "'s prefix set to " + pre);
					} else if (ra.equalsIgnoreCase("nick")) {
						String nick = args[2];
						if (nick.equalsIgnoreCase("name")) {
							String nname = args[3].replace('_', ' ');
							if (SQLTable.nickNames.has("Username", name)) {
								SQLTable.nickNames.set("Nickname", nname, "Username", name);
							} else {
								SQLTable.nickNames.add("Username", name, "Nickname", nname, "Color", "GOLD");
							}
							p.sendMessage(ChatColor.GREEN + "Player " + name + "'s nickname set to " + nname);
						} else if (nick.equalsIgnoreCase("color")) {
							ChatColor color = ChatColor.valueOf(args[3].toUpperCase());
							if (color != null) {
								if (SQLTable.nickNames.has("Username", name)) {
									SQLTable.nickNames.set("Color", color.name(), "Username", name);
								} else {
									SQLTable.nickNames.add("Username", name, "Color", color.name(), "Nickname", name);
								}
								p.sendMessage(ChatColor.GREEN + "Player " + name + "'s nick color set to " + args[3]);
							} else {
								p.sendMessage(ChatColor.RED + "Incorrect color: " + args[3]);
							}
						}
					} else {
						p.sendMessage(ChatColor.RED + "Invalid Rank: \"" + ra + "\"");
					}
				}
			} catch (Exception e) {
				p.sendMessage(ChatColor.RED + "Error! Incorrect syntax!");
				p.sendMessage(ChatColor.RED + "E: " + e.getMessage());
			}
		}
		return false;
	}

}
