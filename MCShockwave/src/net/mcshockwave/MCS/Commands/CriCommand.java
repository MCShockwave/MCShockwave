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

public class CriCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (SQLTable.hasRank(sender.getName(), Rank.ENDER)) {
				if (CooldownUtils.isOnCooldown("/cri", player.getName())) {
					MCShockwave.send(player, "You must wait %s seconds to use /cri again!",
							CooldownUtils.getCooldownForSec("/cri", player.getName(), 1));
					return false;
				}
				player.chat(ChatColor.AQUA + ";-; i cri evertim");
				PacketUtils.playParticleEffect(ParticleEffect.DRIP_WATER,
						player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.4)), 0, 0, 4);
				CooldownUtils.addCooldown("/cri", player.getName(), 600);
			} else {
				MCShockwave.send(player, "You must be %s to use /cri! Buy VIP at buy.mcshockwave.net", "Ender+");
			}
		}
		return false;
	}
}
