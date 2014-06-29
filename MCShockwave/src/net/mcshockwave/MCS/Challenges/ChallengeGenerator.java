package net.mcshockwave.MCS.Challenges;

import java.util.Random;

import net.mcshockwave.MCS.Challenges.Challenge.ChallengeModifier;
import net.mcshockwave.MCS.Challenges.Challenge.ChallengeType;
import net.mcshockwave.MCS.Utils.MiscUtils;

public class ChallengeGenerator {

	private static Random	rand	= new Random();

	public static Challenge getRandom() {
		int id = rand.nextInt(1000000);
		ChallengeType type = ChallengeType.values()[rand.nextInt(ChallengeType.values().length)];
		ChallengeModifier mod = null;
		String extra = "";
		int number = type.randomAmount();
		ChallengeModifier[] mods = ChallengeModifier.getAll(type);
		if (mods.length > 0 && !rand.nextBoolean()) {
			mod = mods[rand.nextInt(mods.length)];
			number *= mod.mult;
		}

		if (number < 1) {
			number = 1;
		}
		int reward = type.getReward(number);
		if (mod != null) {
			reward *= mod.multRe;
		}

		if (type == ChallengeType.Win_Solo_Minigame) {
			String[] mgs = MiscUtils.readTextFile("http://mcsw.us/sologamelist.txt");
			extra = mgs[rand.nextInt(mgs.length)];
		}
		if (type == ChallengeType.Win_Team_Minigame) {
			String[] mgs = MiscUtils.readTextFile("http://mcsw.us/teamgamelist.txt");
			extra = mgs[rand.nextInt(mgs.length)];
		}
		return new Challenge(id, type, extra, mod, number, reward, "");
	}
}
