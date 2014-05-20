package net.mcshockwave.MCS.Commands;

 
import java.util.ArrayList;
import java.util.HashMap;
 
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
 
public class PrivmuteCommand extends JavaPlugin {
 
        HashMap<Player, ArrayList<Player>> i = Manager.getInstance().getIgnore();
       
        public void onEnable() {
                Bukkit.getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        }
       
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
               
                if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.RED + "Sender is not a player, what the hell are you trying to do?");
                        return true;
                }
               
                Player p = (Player) sender;
               
                if (cmd.getName().equalsIgnoreCase("privmute")) {
                        if (args.length == 0) {
                                p.sendMessage(ChatColor.RED + "Error: Who would you like to private mute?");
                                return true;
                        }
                        Player t = Bukkit.getServer().getPlayer(args[0]);
                        if (t == null) {
                                sender.sendMessage(ChatColor.RED + "Could not find " + args[0] + "!");
                                return true;
                        }
                        if (i.get(p) == null) {
                                ArrayList<Player> al = new ArrayList<Player>();
                                al.add(t);
                                i.put(p, al);
                                p.sendMessage(ChatColor.GREEN + "You have privately muted " + t.getName() + "!");
                                return true;
                        }
                        if (i.get(p).contains(t)) {
                                ArrayList<Player> al = i.get(p);
                                al.remove(t);
                                i.put(p, al);
                                p.sendMessage(ChatColor.GREEN + "You no longer have " + t.getName() + "privately muted!");
                                return true;
                        }
                        if (!i.get(p).contains(t)) {
                                ArrayList<Player> al = i.get(p);
                                al.add(t);
                                i.put(p, al);
                                p.sendMessage(ChatColor.GREEN + "You have privately muted " + t.getName() + "!");
                                return true;
                        }
                }
                return true;
        }
}