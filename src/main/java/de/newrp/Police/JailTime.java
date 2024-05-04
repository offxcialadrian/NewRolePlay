package de.newrp.Police;

import de.newrp.API.Friedhof;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.TimeUnit;

public class JailTime implements CommandExecutor, Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            int time = Jail.getJailtimeDatabase(p);
            if (time > 0 && !Friedhof.isDead(p)) {
                p.sendMessage(Jail.PREFIX + "Deine Gefängnisstrafe ist noch nicht vorbei.");
                Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> Jail.arrest(p, time, false));
                Script.executeAsyncUpdate("DELETE FROM jail WHERE nrp_id=" + Script.getNRPID(p));
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Jail.isInJail(p)) {
            Jail j = Jail.getJail(p);
            long arresttime = j.getArrestTime();
            long current = System.currentTimeMillis();

            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(current - arresttime);
            if (seconds > 0) {
                int duration = j.getDuration() - seconds;
                if (duration > 0) {
                    Script.executeAsyncUpdate("INSERT INTO jail (nrp_id, time) VALUES (" + j.getUserID() + ", " + duration + ") ON DUPLICATE KEY UPDATE time = " + duration);
                }
            }
            if (j.getTaskID() != 0) Bukkit.getScheduler().cancelTask(j.getTaskID());
            Jail.JAIL.remove(p.getName());
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        Entity e = event.getEntity();
        Entity d = event.getDamager();
        if (e instanceof Player && d instanceof Player) {
            Player p = (Player) e;
            Player damager = (Player) d;
            if (Jail.isInJail(p) || Jail.isInJail(damager)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(Jail.isInJail((Player) e.getEntity())) e.setCancelled(true);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (Jail.isInJail(p)) {
            Jail j = Jail.getJail(p);
            int left = j.getJailtimeLeft();
            if (left < 60) {
                p.sendMessage(Jail.PREFIX + "Du bist noch " + left + " Sekunden im Gefängnis.");
            } else if (left == 60) {
                p.sendMessage(Jail.PREFIX + "Du bist noch eine Minute im Gefängnis.");
            } else {
                int min = left / 60;
                int sec = left - (min * 60);
                p.sendMessage(Jail.PREFIX + "Du bist noch " + min + " Minuten und " + sec + " Sekunden im Gefängnis.");
            }
        } else {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Gefängnis.");
        }
        return true;
    }
}
