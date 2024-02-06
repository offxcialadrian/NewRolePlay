package de.newrp.Runnable;

import de.newrp.API.*;
import de.newrp.Chat.Me;
import de.newrp.Commands.Test;
import de.newrp.Entertainment.Lotto;
import de.newrp.Government.Wahlen;
import de.newrp.News.BreakingNews;
import de.newrp.Player.AFK;
import de.newrp.Player.Mobile;
import de.newrp.Player.SMSCommand;
import de.newrp.Player.UBahn;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AsyncMinute extends BukkitRunnable {

    public static HashMap<String, Integer> battery = new HashMap<>();

    private static String[] advertises = new String[] {
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Nutze bei deinem LabyMod-Einkauf den Code §cNEWRP §7und erhalte 10% Rabatt!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unseren §9Discord §7vorbei: §9https://discord.gg/newroleplay",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unseren §fTeamspeak §7vorbei: §fnewrp.de",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Keine Lust mehr auf Werbung? Kaufe dir §bPremium §7und erhalte viele Vorteile!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal in unserem Forum vorbei: §9https://forum.newrp.de/",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unserer Webseite vorbei: §9https://newrp.de/",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal in unserem Shop vorbei: §9https://shop.newrp.de/",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Vote für uns und erhalte tolle Belohnungen: §8/§6vote",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Der Beste 10er im Monat! Kaufe dir §bPremium §7und erhalte viele Vorteile!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Kennst du schon unseren TikTok Account?: §chttps://www.tiktok.com/@newroleplay/"};

    int i = 0;

    @Override
    public void run() {
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
                Lotto.start();
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(Mobile.hasPhone(p) && Mobile.mobileIsOn(p) && !AFK.isAFK(p)) {
                Mobile.getPhone(p).removeAkku(p, 1);
                if(Mobile.getPhone(p).getAkku(p) <= 0) {
                    p.sendMessage(Mobile.PREFIX + "Dein Handy ist ausgeschaltet, da der Akku leer ist.");
                    Mobile.setPhone(p, Mobile.getPhone(p));
                    continue;
                }
                if(Script.getPercentage(Mobile.getPhone(p).getAkku(p), Mobile.getPhone(p).getMaxAkku()) <= 10 && !battery.containsKey(p.getName()) && !battery.get(p.getName()).equals(10)) {
                    p.sendMessage(Mobile.PREFIX + "Dein Handy hat nur noch " + Mobile.getPhone(p).getAkku(p) + "% Akku.");
                    battery.put(p.getName(), 10);
                    continue;
                }
                if(Script.getPercentage(Mobile.getPhone(p).getAkku(p), Mobile.getPhone(p).getMaxAkku()) <= 10 && !battery.containsKey(p.getName()) && !battery.get(p.getName()).equals(20)) {
                    p.sendMessage(Mobile.PREFIX + "Dein Handy hat nur noch " + Mobile.getPhone(p).getAkku(p) + "% Akku.");
                    battery.put(p.getName(), 20);
                    continue;
                }
            }
            if(SMSCommand.waitingForMessage.contains(p.getName()) && Mobile.mobileIsOn(p) && Mobile.hasConnection(p)) {
                p.sendMessage(SMSCommand.PREFIX + "Du hast eine neue Nachricht erhalten.");
                p.sendMessage(Messages.INFO + "Schaue in deiner Nachrichten App nach.");
                if(!Mobile.getPhone(p).getLautlos(p)) p.playSound(p.getLocation(), Sound.ENTITY_SHEEP_AMBIENT, 1, 1);
                SMSCommand.waitingForMessage.remove(p.getName());
            } else if(BreakingNews.waitingForMessage.contains(p.getName()) && Mobile.mobileIsOn(p) && Mobile.hasConnection(p)) {
                p.sendMessage(BreakingNews.NEWS + "Es gibt eine neue Breaking News.");
                p.sendMessage(Messages.INFO + "Schaue in deiner Nachrichten App nach.");
                if(!Mobile.getPhone(p).getLautlos(p)) p.playSound(p.getLocation(), Sound.ENTITY_SHEEP_AMBIENT, 1, 1);
                BreakingNews.waitingForMessage.remove(p.getName());
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
                String advert = advertises[Script.getRandom(0, advertises.length-1)];
                p.sendMessage(advert);
                Title.sendTitle(p, 20, 100, 20, advert);
                Script.sendActionBar(p, "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Mit Premium erhältst du keine Werbung.");
            }
        }


        if(i == 5) {
            for(Entity e : Script.WORLD.getEntities()) {
                if(e instanceof Player) continue;
                if(e instanceof Item && ((Item) e).getItemStack().getType() == Material.PLAYER_HEAD) continue;
                if(e instanceof ItemFrame) continue;
                if(e instanceof ArmorStand) continue;
                if(e instanceof Painting) continue;
                if(e.getEntityId() == CitizensAPI.getNPCRegistry().getById(Schwarzmarkt.SCHWARZMARKT_ID).getEntity().getEntityId()) continue;
                e.remove();
            }
        } else
            i++;


        for (Player p : Bukkit.getOnlinePlayers()) {

            if (!AFK.isAFK(p)) AFK.updateAFK(p);
            if (!AFK.isAFK(p)) Script.increaseActivePlayTime(p);
            Script.increasePlayTime(p);
            if(Script.getRandom(1, 10) == 1) {
                if(Krankheit.HUSTEN.isInfected(Script.getNRPID(p))) {
                    Me.sendMessage(p, "hustet.");
                    for(Player p2 : Bukkit.getOnlinePlayers()) {
                        if(p2.getLocation().distance(p.getLocation()) <= 5) {
                            if(!Krankheit.HUSTEN.isInfected(Script.getNRPID(p2)) && !Krankheit.HUSTEN.isImpfed(Script.getNRPID(p2)))
                                Krankheit.HUSTEN.add(Script.getNRPID(p2));
                        }
                    }
                }
            }
        }
    }
}
