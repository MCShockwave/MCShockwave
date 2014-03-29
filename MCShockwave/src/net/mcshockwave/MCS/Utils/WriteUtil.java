package net.mcshockwave.MCS.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.map.MapFont.CharacterSprite;
import org.bukkit.map.MinecraftFont;

public class WriteUtil {

	@SuppressWarnings("deprecation")
	public static void writeChar(Location l, CharacterSprite spr, Material m, int data) {
		for (int x = 0; x < spr.getWidth(); x++) {
			for (int y = 0; y < spr.getHeight(); y++) {
				if (spr.get(y, x)) {
					Block b = l.getWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + spr.getHeight() - y,
							l.getBlockZ());
					b.setType(m);
					b.setData((byte) data);
				}
			}
		}
	}

	public static void writeString(Location start, String write, Material m, int data) {
		int dx = start.getBlockX();

		MinecraftFont font = new MinecraftFont();

		for (int i = 0; i < write.length(); i++) {
			if (write.charAt(i) == ' ') {
				dx += 5;
				continue;
			} else {
				CharacterSprite csp = font.getChar(write.charAt(i));
				writeChar(new Location(start.getWorld(), dx, start.getBlockY(), start.getBlockZ()),
						csp, m, data);
				dx += csp.getWidth() + 1;
			}
		}
	}
}
