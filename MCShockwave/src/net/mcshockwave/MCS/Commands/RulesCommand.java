package net.mcshockwave.MCS.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd,
            String commandLabel, String[] args) {
        Player p = (Player) sender;
        if (commandLabel.equalsIgnoreCase("rules")) {
        	p.sendMessage(ChatColor.AQUA + "MCShockwave Server Network Rules:");
        	p.sendMessage(ChatColor.AQUA + "1. No hacking in general. You know who you are.");
        	p.sendMessage(ChatColor.AQUA + "2. The only game-enhancing mod that is allowed is Optifine.");
        	p.sendMessage(ChatColor.AQUA + "3. No inappropriate skins.");
        	p.sendMessage(ChatColor.AQUA + "4. Respect staff/players.");
        	p.sendMessage(ChatColor.AQUA + "5. Do not log on to an alternative account after being banned or muted. Using alts whilst tempbanned=permaban.");
        	p.sendMessage(ChatColor.AQUA + "6. No excessive foul language.");
        	p.sendMessage(ChatColor.AQUA + "7. No excessive caps.");
        	p.sendMessage(ChatColor.AQUA + "8. Do not advertise non-MCShockwave links.");
        	p.sendMessage(ChatColor.AQUA + "9. No spam.");
        	p.sendMessage(ChatColor.AQUA + "10. No trolling.");
        	p.sendMessage(ChatColor.AQUA + "11. Lying about buying donor (and saying it didn't work) can result in a ban.");
        	p.sendMessage(ChatColor.AQUA + "12. Don't ask to be unmuted. You will be unmuted after the designated amount of time, usually 30 minutes.");
        	p.sendMessage(ChatColor.AQUA + "13. No racism, sexism, etc.");
        	p.sendMessage(ChatColor.AQUA + "14. Use common sense. Ask for help instead of raging.");
        	p.sendMessage(ChatColor.AQUA + "15. Don't ask donors to force games."
        }
    }
}
			