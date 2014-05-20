package net.mcshockwave.MCS.Commands;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
 
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
 
public class PlayerChat implements Listener {
 
        HashMap<Player, ArrayList<Player>> i = Manager.getInstance().getIgnore();
       
        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent e) {
                Player sender = e.getPlayer();
                Set<Player> r = e.getRecipients();
                for (Player pls : Bukkit.getServer().getOnlinePlayers()) {
                        if (!i.containsKey(pls)) return;
                        if (i.get(pls).contains(sender)) {
                                r.remove(pls);
                        }
                }
        }
}