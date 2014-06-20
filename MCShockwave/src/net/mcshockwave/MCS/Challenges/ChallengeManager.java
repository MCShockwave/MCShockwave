package net.mcshockwave.MCS.Challenges;

import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeModifier;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeType;

import java.util.ArrayList;

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

			ret.add(new Challenge(ChallengeType.valueOf(chTy), extr, ChallengeModifier.valueOf(modifiers.get(i)),
					Integer.parseInt(amounts.get(i)), progress.get(i)));
		}
		return ret.toArray(new Challenge[0]);
	}
}
