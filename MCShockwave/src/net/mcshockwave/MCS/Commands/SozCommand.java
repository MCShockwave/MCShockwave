package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Utils.CooldownUtils;
import net.mcshockwave.MCS.Utils.PacketUtils;
import net.mcshockwave.MCS.Utils.PacketUtils.ParticleEffect;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SozCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SQLTable.hasRank(sender.getName(), Rank.OBSIDIAN)) {
				if (CooldownUtils.isOnCooldown("/soz", player.getName())) {
					MCShockwave.send(
							player,
							"You must wait %s seconds to use /soz again!",
							CooldownUtils.getCooldownForSec("/soz",
									player.getName(), 1));
					return false;
				}
				player.chat("§9 i so soz. pls forgiv");
				PacketUtils.playParticleEffect(ParticleEffect.SNOWBALL_POOF,
						player.getLocation(), 3, 1, 25);

				CooldownUtils.addCooldown("/" + label, player.getName(), 2400);
			} else {
				MCShockwave
						.send(player,
								"You must be %s to use /soz! Buy VIP at buy.mcshockwave.net",
								"Obsidian+");
			}
		}
		return false;
	}
}
