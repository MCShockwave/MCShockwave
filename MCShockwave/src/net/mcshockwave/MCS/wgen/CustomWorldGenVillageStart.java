package net.mcshockwave.MCS.wgen;

import net.minecraft.server.v1_7_R2.NBTTagCompound;
import net.minecraft.server.v1_7_R2.StructurePiece;
import net.minecraft.server.v1_7_R2.StructureStart;
import net.minecraft.server.v1_7_R2.World;
import net.minecraft.server.v1_7_R2.WorldGenVillagePieces;
import net.minecraft.server.v1_7_R2.WorldGenVillageRoadPiece;
import net.minecraft.server.v1_7_R2.WorldGenVillageStartPiece;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CustomWorldGenVillageStart extends StructureStart {
	private boolean	c;

	public CustomWorldGenVillageStart() {
	}

	@SuppressWarnings("unchecked")
	public CustomWorldGenVillageStart(World world, Random random, int i, int j, int k) {
		super(i, j);
		List<?> list = WorldGenVillagePieces.a(random, k);
		WorldGenVillageStartPiece worldgenvillagestartpiece = new WorldGenVillageStartPiece(
				world.getWorldChunkManager(), 0, random, (i << 4) + 2, (j << 4) + 2, list, k);

		this.a.add(worldgenvillagestartpiece);
		worldgenvillagestartpiece.a(worldgenvillagestartpiece, this.a, random);
		List<?> list1 = worldgenvillagestartpiece.j;
		List<?> list2 = worldgenvillagestartpiece.i;
		while ((!list1.isEmpty()) || (!list2.isEmpty())) {
			if (list1.isEmpty()) {
				int l = random.nextInt(list2.size());
				StructurePiece structurepiece = (StructurePiece) list2.remove(l);
				structurepiece.a(worldgenvillagestartpiece, this.a, random);
			} else {
				int l = random.nextInt(list1.size());
				StructurePiece structurepiece = (StructurePiece) list1.remove(l);
				structurepiece.a(worldgenvillagestartpiece, this.a, random);
			}
		}
		c();
		int l = 0;
		Iterator<?> iterator = this.a.iterator();
		while (iterator.hasNext()) {
			StructurePiece structurepiece1 = (StructurePiece) iterator.next();
			if (!(structurepiece1 instanceof WorldGenVillageRoadPiece)) {
				l++;
			}
		}
		this.c = (l > 2);
	}

	public boolean d() {
		return this.c;
	}

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		nbttagcompound.setBoolean("Valid", this.c);
	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		this.c = nbttagcompound.getBoolean("Valid");
	}
}
