package de.newrp.Runnable;

import de.newrp.API.Script;
import de.newrp.Entertainment.Lotto;
import de.newrp.Government.Wahlen;
import de.newrp.Player.AFK;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;

public class AsyncMinute extends BukkitRunnable {

    int i = 0;

    @Override
    public void run() {
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
                Lotto.start();
            }
        }


        if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JANUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
            Wahlen.getWahlResult();
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JANUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 20 && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Wahlen.extend) {
            Wahlen.getWahlResult();
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
            Wahlen.getWahlResult();
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 20 && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Wahlen.extend) {
            Wahlen.getWahlResult();
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JULY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
            Wahlen.getWahlResult();
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JULY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 20 && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Wahlen.extend) {
            Wahlen.getWahlResult();
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
            Wahlen.getWahlResult();
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 20 && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Wahlen.extend) {
            Wahlen.getWahlResult();
        }

        if(i == 5) {
            for(Entity e : Script.WORLD.getEntities()) {
                if(e instanceof Player) continue;
                if(e instanceof Item && ((Item) e).getItemStack().getType() == Material.PLAYER_HEAD) continue;
                if(e instanceof ItemFrame) continue;
                if(e instanceof ArmorStand) continue;
                if(e instanceof Painting) continue;
                e.remove();
            }
        } else
            i++;


        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!AFK.isAFK(p)) AFK.updateAFK(p);
            if (!AFK.isAFK(p)) Script.increaseActivePlayTime(p);
            Script.increasePlayTime(p);
            Script.sendTabTitle(p);
        }
    }
}
