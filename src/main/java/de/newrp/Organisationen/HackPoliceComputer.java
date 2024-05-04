package de.newrp.Organisationen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Player.AFK;
import de.newrp.Police.Fahndung;
import de.newrp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HackPoliceComputer implements CommandExecutor, Listener {
    public static final String prefix = "§8[§9Polizeicomputer§8] §9" + Messages.ARROW + " §7";
    public static final Location LOCATION = new Location(Script.WORLD, 448, 32, 855, -88.80542f, 24.899044f);
    private static final long TIMEOUT = TimeUnit.HOURS.toMillis(6);

    private static Player hacker;
    private static long lastTime;
    private static int schedulerID = -1;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if (p.getLevel() <= 3) {
            return false;
        }

        Organisation f = Organisation.getOrganisation(p);

        if (f == null) return false;

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/hackpolicecomputer [Spieler...]");
            return true;
        }

        long now = System.currentTimeMillis();
        if (lastTime + TIMEOUT > now) {
            p.sendMessage(Messages.ERROR + "Man kann erst in  " + TimeUnit.MILLISECONDS.toMinutes(lastTime + TIMEOUT - now) + " den Polizeicomputer wieder hacken.");
            return true;
        }

        if (LOCATION.distance(p.getLocation()) > 3) {
            p.sendMessage(Messages.ERROR + "Du befindest nicht in der Nähe des Polizeicomputers.");
            return true;
        }

        if (schedulerID != -1) {
            p.sendMessage(Messages.ERROR + "Der Polizeicomputer wird gerade bereits gehackt.");
            return true;
        }

        List<Player> cops = Beruf.Berufe.POLICE.getMembers().stream()
                .filter(Beruf::hasBeruf)
                .filter(nearbyPlayer -> Beruf.getBeruf(nearbyPlayer).equals(Beruf.Berufe.POLICE))
                .filter(Duty::isInDuty)
                .filter(nearbyPlayer -> !SDuty.isSDuty(nearbyPlayer))
                .filter(nearbyPlayer -> !AFK.isAFK(nearbyPlayer)).collect(Collectors.toList());

        if (cops.size() < 4) {
            p.sendMessage(Messages.ERROR + "§cEs sind nicht 4 Personen der Staatsexekutive anwesend.");
            return true;
        }

        List<Player> players = new ArrayList<>();
        for (String playerName : args) {
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                p.sendMessage(Messages.ERROR + "§c" + playerName + " wurde nicht gefunden.");
                return true;
            }

            if (player.getLocation().distance(LOCATION) > 20) {
                p.sendMessage(Messages.ERROR + Script.getNRPID(player) + " ist zu weit entfernt.");
                return true;
            }

            players.add(player);
        }


        int wanteds = 0;
        for(Player all : players) {
            wanteds += Fahndung.getWanteds(all);
        }

        int lengthInSeconds = calculateDuration(wanteds);

        p.sendMessage(prefix + "Du hast einen Hackversuch gestartet. Geschätzte Dauer: " + lengthInSeconds + " Sekunden.");
        p.sendMessage(Messages.INFO + "Bewege dich nicht mehr als 10 Meter vom Computer weg.");
        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Polizeicomputer wird gehackt! Überprüfe die Personen in der Nähe.");
        hacker = p;

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (schedulerID == -1) return;

                for (Player player : players) {
                    if (!player.isOnline()) continue;
                    if (player.getLocation().distance(LOCATION) > 20) continue;

                    Beruf.Berufe.POLICE.sendMessage(prefix + "Der Polizeicomputer hat die Fahndung von " + Script.getName(player) + " gelöscht.");
                    Organisation.getOrganisation(player).sendMessage(prefix + "Der Polizeicomputer hat die Fahndung von " + Script.getName(player) + " gelöscht.");
                    Fahndung.removeFahndung(player);
                    player.sendMessage(prefix + "Der Polizeicomputer hat deine Akten gelöscht.");
                }

                p.sendMessage(prefix + "Du hast die Akten der Personen erfolgreich gelöscht.");

                schedulerID = -1;
                lastTime = now;
                hacker = null;
            }
        }.runTaskLater(Main.getInstance(), lengthInSeconds * 20L);
        schedulerID = task.getTaskId();
        return true;
    }

    private int calculateDuration(int wanteds) {
        return (int) (100D + Math.pow(wanteds, 0.7));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (hacker == null) return;
        Player p = e.getPlayer();
        if (p != hacker) return;
        if (p.getLocation().distance(LOCATION) <= 10) return;

        p.sendMessage(prefix + "§cDu hast dich zu weit vom Polizeicomputer entfernt.");

        Bukkit.getScheduler().cancelTask(schedulerID);
        hacker = null;
        schedulerID = -1;
        lastTime = System.currentTimeMillis();

        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
        Organisation.getOrganisation(p).sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (hacker == null) return;

        Player p = e.getPlayer();
        if (p != hacker) return;
        if (e.getTo().distance(LOCATION) <= 10) return;

        p.sendMessage(prefix + "§cDu hast dich zu weit vom Polizeicomputer entfernt.");

        Bukkit.getScheduler().cancelTask(schedulerID);
        hacker = null;
        schedulerID = -1;
        lastTime = System.currentTimeMillis();
        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
        Organisation.getOrganisation(p).sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (hacker == null) return;

        Player p = e.getPlayer();
        if (p != hacker) return;

        Bukkit.getScheduler().cancelTask(schedulerID);
        hacker = null;
        schedulerID = -1;
        lastTime = System.currentTimeMillis();

        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
        Organisation.getOrganisation(p).sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (hacker == null) return;

        Player p = e.getEntity();
        if (p != hacker) return;

        Bukkit.getScheduler().cancelTask(schedulerID);
        hacker = null;
        schedulerID = -1;
        lastTime = System.currentTimeMillis();

        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
        Organisation.getOrganisation(p).sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
    }
}