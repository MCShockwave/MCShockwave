package net.mcshockwave.MCS.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Utils.CooldownUtils;
import net.mcshockwave.MCS.Utils.PacketUtils;

public class CriCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SQLTable.hasRank(sender.getName(), Rank.ENDER)) {
				if (CooldownUtils.isOnCooldown("/" + label, player.getName())) {
					MCShockwave.send(player, "You must wait %s seconds to use /" + label + " again!",
							CooldownUtils.getCooldownForSec("/" + label, player.getName(), 1));
					return false;
				}
				player.chat(ChatColor.AQUA + ";-; "
						+ (label.equalsIgnoreCase("cri") ? "i cri evertim" : "yo lloro cada vez"));
				PacketUtils.playParticleEffect(Particle.DRIP_WATER,
						player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.4)), 0, 0, 4);
				CooldownUtils.addCooldown("/" + label, player.getName(), 2400);
			} else {
				MCShockwave.send(player, "You must be %s to use /" + label + "! Buy VIP at buy.mcshockwave.net",
						"Ender+");
			}
		}
		return false;
	}
}
