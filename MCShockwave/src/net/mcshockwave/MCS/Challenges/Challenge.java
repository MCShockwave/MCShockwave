package net.mcshockwave.MCS.Challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Challenge {

	public ChallengeType		type;

	public String				extra;

	public ChallengeModifier	mod;

	public int					number;

	public String				progress;

	public Challenge(ChallengeType type, String extra, ChallengeModifier mod, int number, String progress) {
		this.type = type;
		this.extra = extra;
		this.mod = mod;
		this.number = number;
		this.progress = progress;
	}

	public HashMap<String, Integer> getProgress() {
		HashMap<String, Integer> ret = new HashMap<>();

		String[] players = progress.split("|");

		for (String s : players) {
			String[] ss = s.split(":");

			ret.put(ss[0], Integer.parseInt(ss[1]));
		}

		return ret;
	}

	private static Random	rand	= new Random();

	public static enum ChallengeType {
		Win_Team_Minigame(
			"Win # games of [E]",
			4,
			14),
		Win_Solo_Minigame(
			"Win # games of [E]",
			3,
			8),
		Kills(
			"Get # kills",
			10,
			50);

		public String	desc;
		public int		min, max;

		private ChallengeType(String desc, int min, int max) {
			this.desc = desc;
			this.min = min;
			this.max = max;
		}

		public int randomAmount() {
			return rand.nextInt(max - min) + min;
		}
	}

	public static enum ChallengeModifier {
		No_Team_Deaths(
			"without anyone on your team dying",
			ChallengeType.Win_Team_Minigame,
			0.1);

		public String			desc;
		public ChallengeType	type;
		public double			mult	= 1;

		private ChallengeModifier(String desc, ChallengeType type) {
			this.desc = desc;
			this.type = type;
		}

		private ChallengeModifier(String desc, ChallengeType type, double mult) {
			this.desc = desc;
			this.type = type;
			this.mult = mult;
		}

		public static ChallengeModifier[] getAll(ChallengeType type) {
			List<ChallengeModifier> ret = new ArrayList<>();

			for (ChallengeModifier m : values()) {
				if (m.type == type) {
					ret.add(m);
				}
			}

			return ret.toArray(new ChallengeModifier[0]);
		}
	}

	public String getDesc() {
		String[] repl = { "#:" + number, "[E]:" + extra };

		String tds = "§b" + type.desc;
		for (String s : repl) {
			String[] ss = s.split(":");
			if (ss.length > 1 && ss[0] != null && ss[1] != null) {
				tds = tds.replace(ss[0], "§6§o" + ss[1] + "§b");
			}
		}
		String mds = null;
		if (mod != null) {
			mds = mod.desc;
		}

		return tds + (mds == null ? "" : ("//§b " + mds));
	}
}
