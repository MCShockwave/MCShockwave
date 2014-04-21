package net.mcshockwave.MCS.Entities;

import net.minecraft.server.v1_7_R2.Entity;
import net.minecraft.server.v1_7_R2.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.Map;

public class CustomEntityRegistrar {

	protected static Field	mapStringToClassField, mapClassToStringField, mapClassToIdField, mapStringToIdField;

	static {
		try {
			mapStringToClassField = net.minecraft.server.v1_7_R2.EntityTypes.class.getDeclaredField("c");
			mapClassToStringField = net.minecraft.server.v1_7_R2.EntityTypes.class.getDeclaredField("d");
			mapClassToIdField = net.minecraft.server.v1_7_R2.EntityTypes.class.getDeclaredField("f");
			mapStringToIdField = net.minecraft.server.v1_7_R2.EntityTypes.class.getDeclaredField("g");

			mapStringToClassField.setAccessible(true);
			mapClassToStringField.setAccessible(true);
			mapClassToIdField.setAccessible(true);
			mapStringToIdField.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public static void addCustomEntity(Class aiClass, String name, EntityType typeShown) {
		// protected static Field mapIdToClassField;

		short id = typeShown.getTypeId();

		if (mapStringToClassField == null || mapStringToIdField == null || mapClassToStringField == null
				|| mapClassToIdField == null) {
			return;
		} else {
			try {
				Map mapStringToClass = (Map) mapStringToClassField.get(null);
				Map mapStringToId = (Map) mapStringToIdField.get(null);
				Map mapClasstoString = (Map) mapClassToStringField.get(null);
				Map mapClassToId = (Map) mapClassToIdField.get(null);

				mapStringToClass.put(name, aiClass);
				mapStringToId.put(name, Integer.valueOf(id));
				mapClasstoString.put(aiClass, name);
				mapClassToId.put(aiClass, Integer.valueOf(id));

				mapStringToClassField.set(null, mapStringToClass);
				mapStringToIdField.set(null, mapStringToId);
				mapClassToStringField.set(null, mapClasstoString);
				mapClassToIdField.set(null, mapClassToId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static <E> Entity spawnCustomEntity(Class<? extends net.minecraft.server.v1_7_R2.Entity> entClass, Location loc) {
		try {
			World w = ((CraftWorld) loc.getWorld()).getHandle();
			Entity ent = entClass.getConstructor(World.class).newInstance(w);
			ent.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			w.addEntity(ent);
			return ent;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
