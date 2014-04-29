package net.mcshockwave.MCS.Commands;

import net.mcshockwave.MCS.DefaultListener;
import net.mcshockwave.MCS.MCShockwave;
import net.mcshockwave.MCS.SQLTable;
import net.mcshockwave.MCS.SQLTable.Rank;
import net.mcshockwave.MCS.Currency.LevelUtils;
import net.mcshockwave.MCS.Currency.PointsUtils;
import net.mcshockwave.MCS.Menu.ItemMenu;
import net.mcshockwave.MCS.Menu.ItemMenu.Button;
import net.mcshockwave.MCS.Stats.Statistics;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.WordUtils;

public class DataCommand implements Listener, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			ItemMenu dm = getDataMenu(p);
			dm.open(p);
		}

		return false;
	}

	public ItemMenu getDataMenu(Player p) {
		ItemMenu dm = new ItemMenu("MCShockwave Data", 27);

		Button points = new Button(false, Material.GOLD_NUGGET, 1, 0, "Points", "Current points: §a"
				+ PointsUtils.getPoints(p));
		dm.addButton(points, 0);

		int xp = LevelUtils.getXP(p);

		ArrayList<String> lvlLore = new ArrayList<>(Arrays.asList("Current Level: §e" + LevelUtils.getLevelFromXP(xp),
				"Progress to Level " + (LevelUtils.getLevelFromXP(xp) + 1) + ": §e" + LevelUtils.getOffsetXP(xp)
						+ " / " + LevelUtils.getXPPerLevel(LevelUtils.getLevelFromXP(xp)), "Percent Progress: §e"
						+ ((int) LevelUtils.getPercentDone(xp)) + "%", "",
				"XP Multiplier: §e" + LevelUtils.getMultiplier(p) + "x"));

		float bm = LevelUtils.getBoughtMultiplier(p.getName());
		if (bm > 1) {
			lvlLore.add("");
			lvlLore.add("Boost Multiplier: §e" + bm + "x");
			lvlLore.add("Time Left: §e" + LevelUtils.getTimeLeftBoost(p.getName()) + " mins");
		}
		Button level = new Button(false, Material.EXP_BOTTLE, 1, 0, "Level Info", lvlLore.toArray(new String[0]));
		dm.addButton(level, 2);

		ArrayList<String> topLore = new ArrayList<>();
		topLore.add("");
		int n = 1;
		for (String s : LevelUtils.getTop(5)) {
			int le = LevelUtils.getLevelFromXP(SQLTable.Level.getInt("Username", s, "XP"));
			topLore.add(n + ". §a" + s + "§7 - Level §o" + le);
			n++;
		}
		topLore.add("");
		topLore.add("§e§nTop Points:");
		topLore.add("");
		n = 1;
		for (String s : PointsUtils.getTop(5)) {
			int po = SQLTable.Points.getInt("Username", s, "Points");
			topLore.add(n + ". §a" + s + "§7 - §o" + po + "§7 points");
			n++;
		}
		Button top = new Button(false, Material.GOLD_HELMET, 5, 0, "§e§nTop Levels:", topLore.toArray(new String[0]));
		dm.addButton(top, 4);

		String pre = DefaultListener.getPrefix(p);
		if (pre == null || pre.equals("")) {
			pre = "None";
		}
		Button rank = new Button(false, Material.BEACON, 1, 0, "§bCurrent rank: §a" + getRankName(p),
				"§d§nStats for rank:", "", "Points Multiplier: §e" + PointsUtils.getMultiplier(p) + "x",
				"XP Multiplier: §e" + LevelUtils.getMultiplier(p) + "x", "Prefix: §e" + pre, "Max Friends: §e"
						+ FriendCommand.getMaxFriends(p));
		dm.addButton(rank, 6);

		Button servers = new Button(false, Material.EYE_OF_ENDER, 1, 0, "Server List", "Click to view all servers",
				"/hub to return to hub");
		dm.addButton(servers, 8);

		ItemMenu serM = MCShockwave.getServerMenu(p);
		dm.addSubMenu(serM, servers, true);

		// stats

		int totKills = Statistics.getStat(p.getName(), "Kills_Total");
		int totDeaths = Statistics.getStat(p.getName(), "Deaths_Total");
		double totKD = (double) totKills / ((double) totDeaths + 1);
		Button genStats = new Button(false, Material.DIAMOND, 1, 0, "Stats - General", "",
				"Total Kills: §e" + totKills, "Total Deaths: §e" + totDeaths, "Total K/D Ratio: §e" + totKD, "",
				"Stats for all modes", "started being recorded", "on March 20, 2014");
		dm.addButton(genStats, 19);

		int mgKills = Statistics.getStat(p.getName(), "Kills_MG");
		int mgDeaths = Statistics.getStat(p.getName(), "Deaths_MG");
		double mgKD = (double) mgKills / ((double) mgDeaths + 1);

		int winsSoloMG = Statistics.getStat(p.getName(), "Wins_MG_Solo");
		int winsTeamMG = Statistics.getStat(p.getName(), "Wins_MG_Team");
		Button mgStats = new Button(false, Material.DIAMOND_SWORD, 1, 0, "Stats - Minigames", "",
				"Kills: §e" + mgKills, "Deaths: §e" + mgDeaths, "K/D Ratio: §e" + mgKD, "", "Wins (Total): §e"
						+ (winsSoloMG + winsTeamMG), "Wins (Solo): §e" + winsSoloMG, "Wins (Team): §e" + winsTeamMG);
		dm.addButton(mgStats, 21);

		int sgKills = Statistics.getStat(p.getName(), "Kills_SG");
		int sgDeaths = Statistics.getStat(p.getName(), "Deaths_SG");
		double sgKD = (double) sgKills / ((double) sgDeaths + 1);

		int winsSoloSG = Statistics.getStat(p.getName(), "Wins_SG");
		Button sgStats = new Button(false, Material.DRAGON_EGG, 1, 0, "Stats - Mynerim", "", "Kills: §e" + sgKills,
				"Deaths: §e" + sgDeaths, "K/D Ratio: §e" + sgKD, "", "Wins: §e" + winsSoloSG);
		dm.addButton(sgStats, 23);

	    int bbKills = Statistics.getStat(p.getName(), "Kills_BB");
	    int bbDeaths = Statistics.getStat(p.getName(), "Deaths_BB");
	    double bbKD = bbKills / (bbDeaths + 1.0D);
	    int winsBB = Statistics.getStat(p.getName(), "Wins_BB");
	    
	    Button bbStats = new Button(false, Material.NETHER_STAR, 1, 0, "Stats - §kBattle Bane", new String[] { "", "Kills: §e" + 
	      bbKills, "Deaths: §e" + bbDeaths, "K/D Ratio: §e" + bbKD, "", "Wins: §e" + winsBB });
	    dm.addButton(bbStats, 25);

		return dm;
	}

	public String getRankName(Player p) {
		Rank r = MCShockwave.getRankForPlayer(p);

		if (r == null) {
			return "Default";
		} else
			return WordUtils.capitalizeFully(r.name().replace('_', ' '));
	}
}
