package net.mcshockwave.MCS.Utils;

import net.minecraft.server.v1_7_R1.NBTTagCompound;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.lang.reflect.Method;

public class NBTUtils {

	@SuppressWarnings("deprecation")
	public static <T extends Entity> NBTTagCompound getEntityNBT(T entity) {
	    NBTTagCompound compound = new NBTTagCompound();
	 
	    if(!(entity instanceof Entity))
	        return null;
	 
	    net.minecraft.server.v1_7_R1.Entity nms = ((CraftEntity) entity).getHandle();
	 
	    Class<? extends Object> clazz = nms.getClass();
	    Method[] methods = clazz.getMethods();
	    for (Method method : methods) {
	        if ((method.getName() == "b")
	                && (method.getParameterTypes().length == 1)
	                && (method.getParameterTypes()[0] == NBTTagCompound.class)) {
	            try {
	                method.setAccessible(true);
	                method.invoke(nms, compound);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	       }
	    }
	 
	    if(compound.getString("id") == null || compound.getString("id").isEmpty()) {
	        String id = entity.getType().getName();
	        compound.setString("id", id);
	    }
	 
	    return compound;
	}
	
	public static void setEntityNBT(Entity e, NBTTagCompound n) {
	    CraftEntity craft = ((CraftEntity) e);
	    net.minecraft.server.v1_7_R1.Entity nms = craft.getHandle();
	    Class<?> entityClass = nms.getClass();
	    Method[] methods = entityClass.getMethods();
	    for (Method method : methods) {
	        if ((method.getName() == "a")
	                && (method.getParameterTypes().length == 1)
	                && (method.getParameterTypes()[0] == NBTTagCompound.class)) {
	            try {
	                method.setAccessible(true);
	                method.invoke(nms, n);
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
	    }
	    craft.setHandle(nms);
	}
	
}
