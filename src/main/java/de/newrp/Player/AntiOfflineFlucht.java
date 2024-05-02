package de.newrp.Player;

import de.newrp.API.Friedhof;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class AntiOfflineFlucht implements Listener {
    public static final HashMap<String, Long> cooldowns = new HashMap<>();
    public final HashMap<String, Long> kick = new HashMap<>();
    public final ArrayList<Player> oflucht = new ArrayList<>();
    private static String PREFIX = "§8[§cAntiOfflineFlucht§8] §c" + Messages.ARROW + " ";

    public static void offlinefluchtLog(final Player p, String reason) {
        String location = p.getLocation().getBlockX() + "/" + p.getLocation().getBlockY() + "/" + p.getLocation().getBlockZ();
        Script.executeUpdate("INSERT INTO offlineflucht(nrp_id, location, time, reason) VALUES(" + Script.getNRPID(p) + ", '" + location + "', '" + System.currentTimeMillis() + "', '" + reason + "');");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        long time = System.currentTimeMillis();
        if (cooldowns.containsKey(p.getName())) {
            Long lastUsage = cooldowns.get(p.getName());
            if (lastUsage + 30 * 1000L > time && !Friedhof.isDead(p)) {
                if (!oflucht.contains(p)) oflucht.add(p);
                if (wasKicked(p)) {
                    Script.sendLocalMessage(30, p, PREFIX + Script.getName(p) + " hat den Server verlassen. (Kick)");
                    Script.sendTeamMessage(PREFIX + Script.getName(p) + " steht unter Offlineflucht Verdacht. (Kick)");
                    offlinefluchtLog(p, "Kick");
                } else if (hadTimeout(p)) {
                    Script.sendLocalMessage(30, p, PREFIX + Script.getName(p) + " hat den Server verlassen. (Timeout/Crash)");
                    Script.sendTeamMessage(PREFIX + Script.getName(p) + " steht unter Offlineflucht Verdacht. (Timeout/Crash)");
                    offlinefluchtLog(p, "Timeout/Crash");
                } else {
                    Script.sendLocalMessage(30, p, PREFIX + Script.getName(p) + " hat den Server verlassen. (Disconnect/Others)");
                    Script.sendTeamMessage(PREFIX + Script.getName(p) + " steht unter Offlineflucht Verdacht. (Disconnect/Others)");
                    offlinefluchtLog(p, "Disconnect/Others");
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (oflucht.contains(e.getPlayer())) {
            Player p = e.getPlayer();
            oflucht.remove(p);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 2));
        }
    }

    @EventHandler
    public void onQuit(PlayerKickEvent e) {
        if (!e.getReason().toLowerCase().startsWith("§")) {
            kick.put(e.getPlayer().getName() + ".timeout", System.currentTimeMillis());
        } else {
            kick.put(e.getPlayer().getName() + ".kick", System.currentTimeMillis());
        }
    }

    public boolean wasKicked(Player p) {
        if (kick.containsKey(p.getName() + ".kick")) {
            return kick.get(p.getName() + ".kick") + 500 > System.currentTimeMillis();
        } else {
            return false;
        }
    }

    public boolean hadTimeout(Player p) {
        if (kick.containsKey(p.getName() + ".timeout")) {
            return kick.get(p.getName() + ".timeout") + 500 > System.currentTimeMillis();
        } else {
            return false;
        }
    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && (e.getDamager() instanceof Player)) {
            Player p = (Player) e.getEntity();
            Long time = System.currentTimeMillis();
            cooldowns.put(p.getName(), time);
        } else if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
            Player p = (Player) e.getEntity();
            Long time = System.currentTimeMillis();
            cooldowns.put(p.getName(), time);
        }
    }
}