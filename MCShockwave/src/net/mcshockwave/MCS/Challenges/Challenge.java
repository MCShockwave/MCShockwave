package net.mcshockwave.MCS.Challenges;

import net.mcshockwave.MCS.SQLTable;

import org.bukkit.Bukkit;

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

	public int					id;

	public int					reward;

	public Challenge(int id, ChallengeType type, String extra, ChallengeModifier mod, int number, int reward,
			String progress) {
		this.type = type;
		this.extra = extra;
		this.mod = mod;
		this.number = number;
		this.progress = progress;
		this.id = id;
		this.reward = reward;
	}

	public HashMap<String, Integer> getProgress() {
		HashMap<String, Integer> ret = new HashMap<>();

		String[] players = progress.split("\\|");

		for (String s : players) {
			String[] ss = s.split(":");

			if (ss.length > 1) {
				ret.put(ss[0], Integer.parseInt(ss[1]));
			}
		}

		return ret;
	}

	private static Random	rand	= new Random();

	public static enum ChallengeType {
		Win_Team_Minigame(
			"Win # games of [E]",
			4,
			14,
			20,
			1000),
		Win_Solo_Minigame(
			"Win # games of [E]",
			3,
			8,
			25,
			1000),
		Kills(
			"Get # kills",
			50,
			100,
			4,
			30);

		public String	desc;
		public int		min, max;
		public double	baseXP, basePo;

		private ChallengeType(String desc, int min, int max, double baseXP, double basePo) {
			this.desc = desc;
			this.min = min;
			this.max = max;
			this.basePo = basePo;
			this.baseXP = baseXP;
		}

		public int randomAmount() {
			return rand.nextInt(max - min) + min;
		}

		public int getReward(int am) {
			boolean xp = rand.nextBoolean();
			int rew = (int) (xp ? am * -baseXP : am * basePo);
			return rew;
		}
	}

	public static enum ChallengeModifier {
		No_Team_Deaths(
			"without anyone on your team dying",
			ChallengeType.Win_Team_Minigame,
			0.1,
			50),
		Hub_Kills(
			"on the Hub PVP",
			ChallengeType.Kills,
			0.5,
			1),
		Mynerim_Kills(
			"on Mynerim",
			ChallengeType.Kills,
			0.5,
			1),
		Minigame_Kills(
			"on Minigames",
			ChallengeType.Kills,
			1.1,
			1.2);

		public String			desc;
		public ChallengeType	type;
		public double			mult	= 1;
		public double			multRe	= 1;

		private ChallengeModifier(String desc, ChallengeType type) {
			this.desc = desc;
			this.type = type;
		}

		private ChallengeModifier(String desc, ChallengeType type, double mult, double multRe) {
			this.desc = desc;
			this.type = type;
			this.mult = mult;
			this.multRe = multRe;
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
		String[] repl = { "#:" + number, "[E]:" + extra.replace('_', ' ') };

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

	public int getDone(String pl) {
		if (getProgress().containsKey(pl)) {
			return getProgress().get(pl);
		}
		String app = "|" + pl + ":0";
		progress += app;
		SQLTable.CurrentChallenges.set("Progress", progress, "ID", id + "");
		return 0;
	}

	public static boolean enoughPlayersOnline() {
		return Bukkit.getOnlinePlayers().length >= 8;
	}
}
