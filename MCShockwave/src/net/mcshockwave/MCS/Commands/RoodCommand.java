package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Utils.CooldownUtils;
import net.mcshockwave.MCS.Utils.PacketUtils;
import net.mcshockwave.MCS.Utils.PacketUtils.ParticleEffect;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoodCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (SQLTable.hasRank(p.getName(), Rank.EMERALD)) {
				if (CooldownUtils.isOnCooldown("/" + label, p.getName())) {
					MCShockwave.send(p, "You must wait %s seconds to use /" + label + " again!",
							CooldownUtils.getCooldownForSec("/" + label, p.getName(), 1));
					return true;
				}
				p.chat(ChatColor.DARK_RED + ">:c rood!");
				PacketUtils.playParticleEffect(ParticleEffect.ANGRY_VILLAGER, p.getLocation(), 0, 1, 10);
				CooldownUtils.addCooldown("/" + label, p.getName(), 2400);
			} else {
				MCShockwave.send(p, "You must be %s to use /" + label + " ! Buy VIP at buy.mcshockwave.net", "Obsidian+");
			}
		}
		return false;
	}
}
