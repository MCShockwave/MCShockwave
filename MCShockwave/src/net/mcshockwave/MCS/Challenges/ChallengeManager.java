package net.mcshockwave.MCS.Challenges;

import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeModifier;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeType;
import net.mcshockwave.MCS.Currency.LevelUtils;
import net.mcshockwave.MCS.Currency.PointsUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChallengeManager {

	public static Challenge[] getCurrentChallenges() {
		ArrayList<String> ids = SQLTable.CurrentChallenges.getAll("ID");
		ArrayList<String> types = SQLTable.CurrentChallenges.getAll("Type");
		ArrayList<String> amounts = SQLTable.CurrentChallenges.getAll("Amount");
		ArrayList<String> modifiers = SQLTable.CurrentChallenges.getAll("Modifier");
		ArrayList<String> rewards = SQLTable.CurrentChallenges.getAll("Reward");
		ArrayList<String> progress = SQLTable.CurrentChallenges.getAll("Progress");

		ArrayList<Challenge> ret = new ArrayList<>();

		for (int i = 0; i < types.size(); i++) {
			String[] tys = types.get(i).split(":");

			String chTy = types.get(i);
			String extr = "";
			if (tys.length > 1) {
				chTy = tys[0];
				extr = tys[1];
			}

			boolean noMod = modifiers.get(i).equalsIgnoreCase("NONE");

			ret.add(new Challenge(Integer.parseInt(ids.get(i)), ChallengeType.valueOf(chTy), extr, noMod ? null
					: ChallengeModifier.valueOf(modifiers.get(i)), Integer.parseInt(amounts.get(i)), Integer
					.parseInt(rewards.get(i)), progress.get(i)));
		}
		return ret.toArray(new Challenge[0]);
	}

	public static void clearChallenges() {
		SQLTable.CurrentChallenges.delWhere("1");
	}

	public static Challenge[] generateNewSet(int amount) {
		List<Challenge> chals = new ArrayList<>();

		for (int i = 0; i < amount; i++) {
			Challenge c = ChallengeGenerator.getRandom();
			boolean add = true;
			for (Challenge m : chals) {
				// if same challenge
				if (c.type.equals(m.type) && c.mod == m.mod
						&& (c.extra == null && m.extra == null || c.extra.equalsIgnoreCase(m.extra))) {
					add = false;
					i--;
					continue;
				}
			}
			if (add)
				chals.add(c);
		}

		return chals.toArray(new Challenge[0]);
	}

	public static void saveToTable(Challenge c) {
		String type = c.type.name();
		String extra = c.extra;
		if (extra.length() > 1) {
			type += ":" + extra;
		}

		String am = c.number + "";
		String mod = "NONE";
		String prog = c.progress;

		if (c.mod != null) {
			mod = c.mod.name();
		}

		SQLTable.CurrentChallenges.add("ID", c.id + "", "Type", type, "Amount", am, "Modifier", mod, "Reward", c.reward
				+ "", "Progress", prog);
	}

	public static void incrChallenge(ChallengeType chl, ChallengeModifier chlMod, String extra, String pl, int am,
			boolean overridePlayerCount) {
		if (overridePlayerCount || Challenge.enoughPlayersOnline()) {
			try {
				for (final Challenge c : getCurrentChallenges()) {
					if (c.type == chl
							&& chlMod == c.mod
							&& (extra != null && extra.equalsIgnoreCase(c.extra) || extra == null
									&& c.extra.length() < 1) && c.getDone(pl) != -1) {
						final Player p = Bukkit.getPlayer(pl);

						int prog = c.getDone(pl);
						int progOld = prog;
						prog += am;
						if (prog >= c.number) {
							prog = -1;

							if (p != null) {
								p.playSound(p.getLocation(), Sound.LEVEL_UP, 1000, 0);
								p.sendMessage("§f");
								p.sendMessage("§a§lChallenge Complete!");
								p.sendMessage("§eChallenge: §3§o" + ChatColor.stripColor(c.getDesc().replace("//", "")));
								p.sendMessage("§f");
								Bukkit.getScheduler().runTaskLater(MCShockwave.instance, new Runnable() {
									public void run() {
										if (c.reward < 0) {
											LevelUtils.addXP(p, Math.abs(c.reward), null, false);
										} else {
											PointsUtils.addPoints(p, Math.abs(c.reward), null);
										}
									}
								}, 10);
							}
						} else {
							if (p != null) {
								p.sendMessage("§aChallenge Progress: §2" + prog + " §8/ §2" + c.number);
							}
						}

						c.progress = c.progress.replaceAll("\\|" + pl + ":" + progOld, "\\|" + pl + ":" + prog);
						SQLTable.CurrentChallenges.set("Progress", c.progress, "ID", c.id + "");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void incrChallenge(ChallengeModifier chlMod, String extra, String pl, int am,
			boolean overridePlayerCount) {
		incrChallenge(chlMod.type, chlMod, extra, pl, am, overridePlayerCount);
	}
}
