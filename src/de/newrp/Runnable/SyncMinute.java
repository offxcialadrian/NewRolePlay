package de.newrp.Runnable;

import de.newrp.API.Debug;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;
import de.newrp.main;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SyncMinute extends BukkitRunnable {
    @Override
    public void run() {
        Sign sign = (Sign) Script.WORLD.getBlockAt(447, 80, 840).getState();
        String lastdead = "seit ";
        if(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Script.getLastDeadOfficer()) > 7) {
            lastdead += TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Script.getLastDeadOfficer())/7 + " Wochen";
        } else  if(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - Script.getLastDeadOfficer()) > 24) {
            lastdead += TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Script.getLastDeadOfficer()) + " Tage";
        } else if(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Script.getLastDeadOfficer()) > 60) {
            lastdead += TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - Script.getLastDeadOfficer()) + " Stunden";
        } else {
            lastdead += TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Script.getLastDeadOfficer()) + " Minuten";
        }
        sign.setLine(2, lastdead);
        sign.update();

        if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 23 && Calendar.getInstance().get(Calendar.MINUTE) == 59) {
            Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in einer Minute neu!");
            Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 30 Sekunden neu!");
                Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                    Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 10 Sekunden neu!");
                    Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                        Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 5 Sekunden neu!");
                        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                            Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 4 Sekunden neu!");
                            Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                                Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 3 Sekunden neu!");
                                Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                                    Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 2 Sekunden neu!");
                                    Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                                        Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet in 1 Sekunde neu!");
                                        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                                            Bukkit.broadcastMessage(Script.PREFIX + "§4§lACHTUNG: §cDer Server startet jetzt neu!");
                                            Bukkit.getServer().shutdown();
                                        }, 20);
                                    }, 20);
                                }, 20);
                            }, 20);
                        }, 20);
                    }, 20*5);
                }, 20*15);
            }, 20*15);
        }
    }
}
