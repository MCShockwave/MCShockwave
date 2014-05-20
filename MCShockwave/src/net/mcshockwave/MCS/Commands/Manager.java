package net.mcshockwave.MCS.Commands;
 
import java.util.ArrayList;
import java.util.HashMap;
 
import org.bukkit.entity.Player;
 
public class Manager {
 
        private Manager() { }
       
        static Manager instance = new Manager();
       
        public static Manager getInstance() {
                return instance;
        }
       
        HashMap<Player, ArrayList<Player>> ignore = new HashMap<Player, ArrayList<Player>>();
       
        public HashMap<Player, ArrayList<Player>> getIgnore() {
                return ignore;
        }
}