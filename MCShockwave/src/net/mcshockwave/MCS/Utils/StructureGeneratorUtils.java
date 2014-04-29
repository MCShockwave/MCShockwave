package net.mcshockwave.MCS.Utils;

import net.mcshockwave.MCS.wgen.CustomWorldGenDungeons;
import net.mcshockwave.MCS.wgen.CustomWorldGenVillageStart;
import net.minecraft.server.v1_7_R2.StructureBoundingBox;
import net.minecraft.server.v1_7_R2.WorldServer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;

import java.util.Random;

public class StructureGeneratorUtils {
	public static WorldServer getFrom(World w) {
		return ((CraftWorld) w).getHandle();
	}

	public static Random	r	= new Random();

	public static void generateVillage(Location l, int radius) {
		int i = (l.getChunk().getX() << 4) + 8;
		int j = (l.getChunk().getZ() << 4) + 8;
		new CustomWorldGenVillageStart().a(getFrom(l.getWorld()), r, new StructureBoundingBox(i - radius, j - radius, i
				+ radius, j + radius));
	}

	public static void generateDungeon(Location l) {
		new CustomWorldGenDungeons().a(getFrom(l.getWorld()), r, l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
}
