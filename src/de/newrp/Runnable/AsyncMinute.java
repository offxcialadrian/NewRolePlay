package de.newrp.Runnable;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Entertainment.Lotto;
import de.newrp.Government.Wahlen;
import de.newrp.Player.AFK;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AsyncMinute extends BukkitRunnable {

    private static String[] advertises = new String[] {
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Nutze bei deinem LabyMod-Einkauf den Code §cNEWRP §7und erhalte 10% Rabatt!",
        "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unseren §9Discord §7vorbei: §9https://discord.gg/newroleplay",
        "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unseren §fTeamspeak §7vorbei: §fnewrp.de",
        "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Keine Lust mehr auf Werbung? Kaufe dir §bPremium §7und erhalte viele Vorteile!",
        "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal in unserem Forum vorbei: §9https://forum.newrp.de/",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unserer Webseite vorbei: §9https://newrp.de/",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Kennst du schon unseren TikTok Account?: §chttps://www.tiktok.com/@newroleplay/"};

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
        }  else if (Wahlen.neuWahlen && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
            Wahlen.getWahlResult();
        } else if (Wahlen.neuWahlen && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 20 && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Wahlen.extend) {
            Wahlen.getWahlResult();
        }

        if(Calendar.getInstance().get(Calendar.MINUTE) == 0 || Calendar.getInstance().get(Calendar.MINUTE) == 20 || Calendar.getInstance().get(Calendar.MINUTE) == 40) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(Premium.hasPremium(p)) continue;
                p.sendMessage(advertises[Script.getRandom(0, advertises.length-1)]);
            }
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
