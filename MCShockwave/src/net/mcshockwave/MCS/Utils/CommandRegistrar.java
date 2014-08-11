package net.mcshockwave.MCS.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CommandRegistrar {

	public static void registerCommand(JavaPlugin plugin, String cmd, String... aliases) {
		PluginCommand command = getCommand(plugin, cmd);

		command.setAliases(Arrays.asList(aliases));
		getCommandMap().register(plugin.getDescription().getName(), command);
	}

	private static PluginCommand getCommand(JavaPlugin plugin, String name) {
		PluginCommand command = null;

		try {
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);

			command = c.newInstance(name, plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return command;
	}

	private static CommandMap getCommandMap() {
		CommandMap commandMap = null;

		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);

				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return commandMap;
	}

}
