package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class AntiCheatSystem implements Listener {

    public static String PREFIX = "§8[§cAntiCheat§8] §c" + Messages.ARROW + " ";
    public static final HashMap<UUID, HashMap<Cheat, CheatWarning>> WARNING = new HashMap<>();
    public static final int MAX_WARNING = 5;
    public static final int TIME_BETWEEN_WARNINGS = 100;

    public static void warn(Player p, Cheat cheat) {
        if (WARNING.containsKey(p.getUniqueId())) {
            HashMap<Cheat, CheatWarning> map = WARNING.get(p.getUniqueId());
            if (map.containsKey(cheat)) {
                CheatWarning cw = map.get(cheat);
                cw.addCounter();
                if (cw.getCounter() >= MAX_WARNING) {
                    punish(p, cheat);
                }
            } else {
                map.put(cheat, new CheatWarning(p, 1, System.currentTimeMillis()));
            }
        } else {
            HashMap<Cheat, CheatWarning> map = new HashMap<>();
            map.put(cheat, new CheatWarning(p, 1, System.currentTimeMillis()));
            WARNING.put(p.getUniqueId(), map);
        }
    }

    public static void punish(Player p, Cheat cheat) {
        Script.sendTeamMessage(PREFIX + Script.getName(p) + " wurde aufgrund des Verdachts auf Fly-Cheating vom Server gekickt.");
        p.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKICK §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §e" + "Verdacht auf Cheating" + "\n\n§8§m------------------------------");
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        WARNING.remove(e.getPlayer().getUniqueId());
    }

    public enum Cheat {
        FLY()
    }

    public static class CheatWarning {

        private final Player p;
        private int counter;
        private long last_warning;

        public CheatWarning(Player p, int counter, long last_warning) {
            this.p = p;
            this.counter = counter;
            this.last_warning = last_warning;
        }

        public Player getPlayer() {
            return this.p;
        }

        public int getCounter() {
            return this.counter;
        }

        public void addCounter() {
            long current = System.currentTimeMillis();
            if (current - this.last_warning < TIME_BETWEEN_WARNINGS) return;
            this.counter = this.counter + 1;
            this.last_warning = current;
        }

        public long getLastWarning() {
            return this.last_warning;
        }
    }
}
