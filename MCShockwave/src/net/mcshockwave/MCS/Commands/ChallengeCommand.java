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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChallengeCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			openMenu((Player) sender);
		}

		return false;
	}

	public int getMaxLines(Challenge[] cs) {
		int max = 0;
		for (Challenge c : cs) {
			Pattern pattern = Pattern.compile("//");
			Matcher matcher = pattern.matcher(c.getDesc());

			int count = 0;
			while (matcher.find())
				count++;
			if (count > max) {
				max = count;
			}
		}
		return max;
	}

	public void openMenu(Player p) {
		try {
			Challenge[] cs = ChallengeManager.getCurrentChallenges();
			ItemMenu m = new ItemMenu("Challenges", cs.length > 0 ? cs.length : 1);

			for (int i = 0; i < cs.length; i++) {
				Challenge c = cs[i];

				if (c != null) {
					boolean completed = c.getDone(p.getName()) == -1;

					boolean rewXP = c.reward < 0;
					int rewAm = Math.abs(c.reward);

					ArrayList<String> lore = new ArrayList<>();
					lore.add("");
					String[] spl = c.getDesc().split("//");
					for (int nx = 0; nx <= getMaxLines(cs); nx++) {
						lore.add(spl.length > nx ? spl[nx] : "");
					}
					lore.add("");
					lore.add("§aReward:");
					lore.add((rewXP ? "§b" : "§d") + rewAm + " " + (rewXP ? "XP" : "Points"));
					lore.add("");
					if (!completed) {
						lore.add("§eProgress:");
						lore.add("§b" + c.getDone(p.getName()) + " §8/ §3" + c.number);
						if (!Challenge.enoughPlayersOnline()) {
							lore.add("");
							lore.add("§cThere are not enough");
							lore.add("§cplayers online to unlock");
							lore.add("§cany challenges!");
						}
					} else {
						lore.add("§a§l - COMPLETED -");
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
