package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.Challenges.Challenge;
import net.mcshockwave.MCS.Challenges.ChallengeManager;
import net.mcshockwave.MCS.Menu.ItemMenu;
import net.mcshockwave.MCS.Menu.ItemMenu.Button;
import net.mcshockwave.MCS.Utils.MiscUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ChallengeCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			openMenu((Player) sender);
		}

		return false;
	}

	public void openMenu(Player p) {
		try {
			Challenge[] cs = ChallengeManager.getCurrentChallenges();
			ItemMenu m = new ItemMenu("Challenges", cs.length > 0 ? cs.length : 1);

			for (int i = 0; i < cs.length; i++) {
				Challenge c = cs[i];

				if (c != null) {					
					boolean rewXP = c.reward < 0;
					int rewAm = Math.abs(c.reward);
					rewAm *= c.getRewardMultiplier(p.getName());

					ArrayList<String> lore = new ArrayList<>();
					lore.add("");
					for (String s : c.getDesc().split("//")) {
						lore.add(s);
					}
					lore.add("");
					lore.add("§aReward:");
					lore.add((rewXP ? "§b" : "§d") + rewAm + " " + (rewXP ? "XP" : "Points") + " §7(§c"
							+ c.getRewardMultiplier(p.getName()) + "x§7)");
					lore.add("");
					lore.add("§eProgress:");
					lore.add("§b0 §8/ §3" + c.number);
					if (!Challenge.enoughPlayersOnline()) {
						lore.add("");
						lore.add("§cYou do not have enough");
						lore.add("§cplayers online to unlock");
						lore.add("§cany challenges!");
					}

					Button b = new Button(false, Material.SKULL_ITEM, 1, 0, "§eDaily Challenge",
							lore.toArray(new String[0]));

					m.addButton(b, i);
				}
			}

			m.open(p);
		} catch (Exception e) {
			MiscUtils.printStackTrace(e);
		}
	}
}
