package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.Utils.ItemMetaUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BootsCommand implements CommandExecutor, Listener {

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (!(s instanceof Player)) {
			return false;
		}
		Player p = (Player) s;
		Inventory i = Bukkit.createInventory(null, 9, p.getName());
		ItemStack it = new ItemStack(Material.LEATHER_BOOTS);
		ItemMetaUtils.setLeatherColor(it, Color.RED);
		ItemMetaUtils.setItemName(it, ChatColor.RED + "Red Boots!!");
		int o = 0;
		if (SQLTable.MiscItems.has("Username", p.getName())) {
			o = SQLTable.MiscItems.getInt("Username", p.getName(), "Red_Boots");
		} else {
			SQLTable.MiscItems.add("Username", p.getName());
		}		
		if (o == 0) {
			ItemMetaUtils.setLore(it, ChatColor.DARK_PURPLE + "Yes, they are finally back! Purchase for 250,000 points and show your style!");
		} else {
			ItemMetaUtils.setLore(it, ChatColor.AQUA + "Enjoy your nostalgic 1.0 red boots!!! Click to un-equip.");
		}
		i.setItem(4, it);
		p.openInventory(i);
		return true;
	}
}
