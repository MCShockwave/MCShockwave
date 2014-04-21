package net.mcshockwave.MCS.Utils;

import net.minecraft.server.v1_7_R2.ChunkProviderGenerate;
import net.minecraft.server.v1_7_R2.StructureBoundingBox;
import net.minecraft.server.v1_7_R2.StructureStart;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Random;

public class StructureGeneratorUtils {

	public static void generateVillage(Location l, int radius) {
		try {
			Block block = l.getBlock();

			CraftWorld craftworld = (CraftWorld) l.getWorld();
			ChunkProviderGenerate chunkProvider = (ChunkProviderGenerate) craftworld.getHandle().worldProvider
					.getChunkProvider();

			Field randField = ChunkProviderGenerate.class.getDeclaredField("k");
			randField.setAccessible(true);
			Random random = (Random) randField.get(chunkProvider);

			Class<?> clazz = Class.forName("net.minecraft.server.WorldGenVillageStart");
			Constructor<?> constructor = clazz.getConstructor(net.minecraft.server.v1_7_R2.World.class, Random.class,
					int.class, int.class, int.class);
			constructor.setAccessible(true);
			StructureStart start = (StructureStart) constructor.newInstance(craftworld.getHandle(), random, block
					.getChunk().getX(), block.getChunk().getZ(), 0);

			int i = (block.getChunk().getX() << 4) + 8;
			int j = (block.getChunk().getZ() << 4) + 8;

			start.a(craftworld.getHandle(), random, new StructureBoundingBox(i - radius, j - radius, i + radius, j
					+ radius));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
