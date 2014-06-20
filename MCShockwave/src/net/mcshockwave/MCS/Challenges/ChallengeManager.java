package net.mcshockwave.MCS.Challenges;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeModifier;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeType;

import java.util.ArrayList;
import java.util.List;

public class ChallengeManager {

	public static Challenge[] getCurrentChallenges() {
		ArrayList<String> types = SQLTable.CurrentChallenges.getAll("Type");
		ArrayList<String> amounts = SQLTable.CurrentChallenges.getAll("Amount");
		ArrayList<String> modifiers = SQLTable.CurrentChallenges.getAll("Modifier");
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

			ret.add(new Challenge(ChallengeType.valueOf(chTy), extr, noMod ? null : ChallengeModifier.valueOf(modifiers
					.get(i)), Integer.parseInt(amounts.get(i)), progress.get(i)));
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

		SQLTable.CurrentChallenges.add("Type", type, "Amount", am, "Modifier", mod, "Progress", prog);
	}
}
