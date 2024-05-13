package de.newrp.Runnable;

import de.newrp.API.*;
import de.newrp.Chat.Me;
import de.newrp.Entertainment.Lotto;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Government.Wahlen;
import de.newrp.News.BreakingNews;
import de.newrp.Player.*;
import de.newrp.Ticket.Ticket;
import de.newrp.Ticket.TicketCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static de.newrp.Ticket.TicketCommand.queue;

public class AsyncMinute extends BukkitRunnable {

    public static HashMap<String, Integer> battery = new HashMap<>();

    private static String[] advertises = new String[]{
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Nutze bei deinem LabyMod-Einkauf den Code §cNEWRP §7und erhalte 10% Rabatt!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unseren §9Discord §7vorbei: §9https://discord.gg/newroleplay",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unseren §fTeamspeak §7vorbei: §fnewrp.de",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Keine Lust mehr auf Werbung? Kaufe dir §bPremium §7und erhalte viele Vorteile!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Keine Lust mehr auf Werbung? Kaufe dir §bPremium §7und erhalte viele Vorteile!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Keine Lust mehr auf Werbung? Kaufe dir §bPremium §7und erhalte viele Vorteile!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal in unserem Forum vorbei: §9https://forum.newrp.de/",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal auf unserer Webseite vorbei: §9https://newrp.de/",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Schau mal in unserem Shop vorbei: §9https://shop.newrp.de/",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Vote für uns und erhalte tolle Belohnungen: §8/§6vote",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Der beste 10er im Monat! Kaufe dir §bPremium §7und erhalte viele Vorteile!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Der beste 10er im Monat! Kaufe dir §bPremium §7und erhalte viele Vorteile!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Der beste 10er im Monat! Kaufe dir §bPremium §7und erhalte viele Vorteile!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Nutzt du schon LabyMod? Du erhältst das beste Spielerlebnis mit LabyMod!",
            "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Kennst du schon unseren TikTok Account?: §chttps://www.tiktok.com/@newrpde/"};


    @Override
    public void run() {
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
                Lotto.start();
            }
        }

        Corpse.reloadNPCAll();

        if (GangwarCommand.gangwarIsActive()) {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 22) {
                GangwarCommand.endGangwar();
            } else {
                GangwarCommand.processGangwar();
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
        } else if (Wahlen.neuWahlen && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18 && Calendar.getInstance().get(Calendar.MINUTE) == 0) {
            Wahlen.getWahlResult();
        } else if (Wahlen.neuWahlen && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 20 && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Wahlen.extend) {
            Wahlen.getWahlResult();
        }

        if (Calendar.getInstance().get(Calendar.MINUTE) == 0 || Calendar.getInstance().get(Calendar.MINUTE) == 15 || Calendar.getInstance().get(Calendar.MINUTE) == 30 || Calendar.getInstance().get(Calendar.MINUTE) == 45) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Premium.hasPremium(p)) continue;
                String advert = advertises[Script.getRandom(0, advertises.length - 1)];
                p.sendMessage(advert);
                Title.sendTitle(p, 20, 100, 20, advert.split(Messages.ARROW)[0], advert.split(Messages.ARROW)[1]);
                Script.sendActionBar(p, "§8[§cWerbung§8] §c" + Messages.ARROW + " §7Mit Premium erhältst du keine Werbung.");
            }
        }

        for (Map.Entry<Integer, Ticket.Queue> ent : queue.entrySet()) {
            Player ticketer = ent.getValue().getReporter();
            ticketer.sendMessage(TicketCommand.PREFIX + "Bitte warte... Dein Ticket wird in Kürze bearbeitet.");
            int amount = 0;
            for (Map.Entry<Integer, Ticket.Queue> ent2 : queue.entrySet()) {
                if (ent2.getKey() < ent.getKey()) amount++;
            }
            if (amount == 0) {
                ticketer.sendMessage(TicketCommand.PREFIX + "Du bist als nächstes dran.");
            } else {
                ticketer.sendMessage(TicketCommand.PREFIX + "Du bist in der Warteschlange an Position " + amount + ".");
            }
        }

        if (Calendar.getInstance().get(Calendar.MINUTE) % 2 == 0) {
            int amount = 0;
            for (Map.Entry<Integer, Ticket.Queue> ignored : queue.entrySet()) {
                amount++;
            }
            if (amount > 0) {
                for (Player nrp : Script.getNRPTeam()) {
                    Title.sendTitle(nrp, 20, 100, 20, "§8[§6Tickets§8] §6" + Messages.ARROW + " §7Es sind noch " + amount + " Tickets offen.");
                    nrp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1, false, false));
                    nrp.sendMessage("§8[§6Tickets§8] §6" + Messages.ARROW + " §7Es sind noch " + amount + " Tickets offen.");
                    nrp.sendMessage(Messages.INFO + "Bitte beachte, dass die Bearbeitung von Tickets eine hohe Priorität hat.");
                }
            }
            /*for(Entity e : Script.WORLD.getEntities()) {
                if(e instanceof Player) continue;
                if(e instanceof Item && ((Item) e).getItemStack().getType() == Material.PLAYER_HEAD) continue;
                if(e instanceof ItemFrame) continue;
                if(e instanceof ArmorStand) continue;
                if(e instanceof Painting) continue;
                if(e instanceof Boat) continue;
                if(e.getEntityId() == CitizensAPI.getNPCRegistry().getById(Schwarzmarkt.SCHWARZMARKT_ID).getEntity().getEntityId()) continue;
                e.remove();
            }*/
        }

        if (Calendar.getInstance().get(Calendar.MINUTE) % 15 == 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if(Vote.hasVotedToday(Script.getNRPID(p))) continue;
                Script.sendClickableMessage(p, "§8[§6VoteShop§8]§6 " + Messages.ARROW + " §7Vote für uns und erhalte tolle Belohnungen: §8/§6vote", "/vote", "Vote für uns!");
                Script.sendActionBar(p, "§8[§6VoteShop§8]§6 " + Messages.ARROW + " §7Vote für uns und erhalte tolle Belohnungen: §8/§6vote");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                if(!Premium.hasPremium(p)) Title.sendTitle(p, 50, 100, 50, "§6 " + Messages.ARROW + " Vote für uns! «", "§8/§6vote");
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!AFK.isAFK(p) && !Passwort.isLocked(p)) {
                Script.increaseActivePlayTime(p);
            }
            Script.increasePlayTime(p);
            if (Script.getRandom(1, 10) == 1) {
                if (Krankheit.HUSTEN.isInfected(Script.getNRPID(p))) {
                    Me.sendMessage(p, "hustet.");
                    for (Player p2 : Bukkit.getOnlinePlayers()) {
                        if (p2.getLocation().distance(p.getLocation()) <= 5) {
                            if (!Krankheit.HUSTEN.isInfected(Script.getNRPID(p2)) && !Krankheit.HUSTEN.isImpfed(Script.getNRPID(p2)))
                                Krankheit.HUSTEN.add(Script.getNRPID(p2));
                        }
                    }
                }
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Mobile.hasPhone(p) && Mobile.mobileIsOn(p) && !AFK.isAFK(p)) {
                final Mobile.Phones phones = Mobile.getPhone(p);
                if (phones == null) return;

               phones.removeAkku(p, 1);
               final int akku = phones.getAkku(p);
                if (akku <= 0) {
                    p.sendMessage(Mobile.PREFIX + "Dein Handy ist ausgeschaltet, da der Akku leer ist.");
                    ItemStack is = Mobile.getPhone(p).getItem();
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT));
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT));
                    p.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT));
                    p.getInventory().addItem(is);
                    continue;
                }
                /*if (Script.getPercentage(akku, phones.getMaxAkku()) <= 10 && !battery.containsKey(p.getName()) && !battery.get(p.getName()).equals(10)) {
                    p.sendMessage(Mobile.PREFIX + "Dein Handy hat nur noch " + Mobile.getPhone(p).getAkku(p) + "% Akku.");
                    battery.put(p.getName(), 10);
                    continue;
                }
                if (Script.getPercentage(akku, Mobile.getPhone(p).getMaxAkku()) <= 10 && !battery.containsKey(p.getName()) && !battery.get(p.getName()).equals(20)) {
                    p.sendMessage(Mobile.PREFIX + "Dein Handy hat nur noch " + Mobile.getPhone(p).getAkku(p) + "% Akku.");
                    battery.put(p.getName(), 20);
                    continue;
                }*/
            }
            if (SMSCommand.waitingForMessage.contains(p.getName()) && Mobile.mobileIsOn(p) && Mobile.hasConnection(p)) {
                p.sendMessage(SMSCommand.PREFIX + "Du hast eine neue Nachricht erhalten.");
                p.sendMessage(Messages.INFO + "Schaue in deiner Nachrichten App nach.");
                if (!Mobile.getPhone(p).getLautlos(p)) p.playSound(p.getLocation(), Sound.ENTITY_SHEEP_AMBIENT, 1, 1);
                SMSCommand.waitingForMessage.remove(p.getName());
            } else if (BreakingNews.waitingForMessage.contains(p.getName()) && Mobile.mobileIsOn(p) && Mobile.hasConnection(p)) {
                p.sendMessage(BreakingNews.NEWS + "Es gibt eine neue Breaking News.");
                p.sendMessage(Messages.INFO + "Schaue in deiner Nachrichten App nach.");
                if (!Mobile.getPhone(p).getLautlos(p)) p.playSound(p.getLocation(), Sound.ENTITY_SHEEP_AMBIENT, 1, 1);
                BreakingNews.waitingForMessage.remove(p.getName());
            }
        }
    }
}
