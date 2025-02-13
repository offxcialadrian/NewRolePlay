package de.newrp.API;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

public class Cache {

    private static final HashMap<String, Scoreboard> scoreboard = new HashMap<>();
    public static final HashMap<String, ItemStack[]> inventar = new HashMap<>();

    public static Scoreboard getScoreboard(Player p) {
        if (scoreboard.containsKey(p.getName()))
            return scoreboard.get(p.getName());
        return null;
    }

    public static void loadScoreboard(Player p) {
        /*if (getScoreboard(p) != null) {
            p.setScoreboard(getScoreboard(p));
        } else {
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }*/
    }

    public static void saveScoreboard(Player p) {
        if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
            scoreboard.put(p.getName(), p.getScoreboard());
    }

    public static void resetScoreboard(Player p) {
        scoreboard.remove(p.getName());
    }

    public static ItemStack[] getInventory(Player p) {
        if (inventar.containsKey(p.getName()))
            return inventar.get(p.getName());
        return null;
    }

    public static void loadInventory(Player p) {
        Debug.debug("loading inventory for " + p.getName());
        if (inventar.containsKey(p.getName())) {
            p.getInventory().clear();
            p.getInventory().setContents(inventar.get(p.getName()));
            inventar.remove(p.getName());
        }
    }

    public static void saveInventory(Player p) {
        Debug.debug("saving inventory for " + p.getName());
        if(inventar.containsKey(p.getName())) return;
        inventar.put(p.getName(), p.getInventory().getContents());
    }

    public static void resetInventory(Player p) {
        inventar.remove(p.getName());
        p.getInventory().clear();
    }

}
