package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Berufe.Equip;
import de.newrp.Chat.Me;
import de.newrp.House.House;
import de.newrp.Organisationen.Drogen;
import de.newrp.Police.Fahndung;
import de.newrp.Police.Handschellen;
import de.newrp.Police.Policecomputer;
import de.newrp.Ticket.TicketCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class InteractMenu implements Listener {

    public static HashMap<String, String> interacting = new HashMap<>();
    public static String PREFIX = "§8[§6Interaktion§8] §6» §7";
    public static ArrayList<String> addiction_cooldown = new ArrayList<>();


    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        if(!(e.getRightClicked() instanceof Player)) return;
        Player tg = (Player) e.getRightClicked();
        if(!p.isSneaking()) return;
        if(Friedhof.isDead(p)) return;
        if(SDuty.isSDuty(tg)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst nicht mit " + Script.getName(tg) + " interagieren, da " + (Script.getGender(tg) == Gender.MALE ? "er" : "sie") + " sich im Supporter-Dienst befindet.");
            return;
        }

        if(AFK.isAFK(tg)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst nicht mit " + Script.getName(tg) + " interagieren, da " + (Script.getGender(tg) == Gender.MALE ? "er" : "sie") + " sich im AFK-Modus befindet.");
            return;
        }

        interacting.put(p.getName(), tg.getName());

        Inventory inv = Bukkit.createInventory(null, 6*9, "§8» §eInteraktion");
        inv.setItem(20, new ItemBuilder(Material.BOOK).setName("§6Personalausweis zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deinen Personalausweis.").build());
        inv.setItem(21, new ItemBuilder(Material.BOOK).setName("§6Lizenzen zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deine Lizenzen.").build());
        inv.setItem(22, new ItemBuilder(Material.CHEST).setName("§6Tasche zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deine Tasche.").build());
        inv.setItem(23, new ItemBuilder(Material.BOOK).setName("§6Finanzen zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deine Finanzen.").build());
        inv.setItem(24, new ItemBuilder(Material.PLAYER_HEAD).setName("§6Gesundheit zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deine Gesundheit.").build());
        inv.setItem(30, new ItemBuilder(Material.RED_TULIP).setName("§6Küssen").setLore("§8× §7Küsse §6" + Script.getName(tg) + "§7.").build());
        inv.setItem(31, new ItemBuilder(Material.LEAD).setName("§6Tragen").setLore("§8× §7Trage §6" + Script.getName(tg) + "§7.").build());
        inv.setItem(32, new ItemBuilder(Material.BARRIER).setName("§6Zeige Ticket").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7, dass du im Ticket bist.").build());

        if((Handschellen.isCuffed(tg) && Beruf.getBeruf(p) == Beruf.Berufe.POLICE && Duty.isInDuty(p)) || p.getInventory().contains(Script.brechstange())) {
            inv.setItem(40, new ItemBuilder(Material.IRON_DOOR).setName("§6Handschellen öffnen").setLore("§8× §7Öffne die Handschellen von §6" + Script.getName(tg) + "§7.").build());
        }

        if(Beruf.hasBeruf(p)) {
            if (Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && Duty.isInDuty(p)) {
                inv.setItem(39, new ItemBuilder(Material.IRON_DOOR).setName("§6Durchsuchen").setLore("§8× §7Durchsuche §6" + Script.getName(tg) + "§7.").build());
                inv.setItem(41, new ItemBuilder(Material.NETHER_STAR).setName("§6Polizeicomputer").setLore("§8× §7Öffne den Polizeicomputer.").build());
            }

            if (Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST) && Duty.isInDuty(p)) {
                inv.setItem(39, new ItemBuilder(Material.PLAYER_HEAD).setName("§4Abhängigkeit behandeln").setLore("§8× §7Behandle die Abhängigkeit von §6" + Script.getName(tg) + "§7.").build());
            }
        }

        Script.fillInv(inv);
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equals("§8» §eInteraktion")) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        if(!interacting.containsKey(p.getName())) return;
        Player tg = Script.getPlayer(interacting.get(p.getName()));
        if(tg == null) return;
        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return;
        }
        e.getView().close();
        interacting.remove(p.getName());

        switch (e.getCurrentItem().getItemMeta().getDisplayName().replace("§6","")) {
            case "Personalausweis zeigen":
                tg.sendMessage(PREFIX + Script.getName(p) + "s Personalien:");
                tg.sendMessage(PREFIX + " §8× §6Name: " + Script.getName(p));
                tg.sendMessage(PREFIX + " §8× §6Geburtsdatum: §c" + Script.getBirthday(Script.getNRPID(p)) + " (" + Script.getAge(Script.getNRPID(p)) + ")");
                if (Script.getGender(p).equals(Gender.MALE)) {
                    tg.sendMessage(PREFIX + " §8× §6Geschlecht: §cMännlich");
                } else if (Script.getGender(p).equals(Gender.FEMALE)) {
                    tg.sendMessage(PREFIX + " §8× §6Geschlecht: §cWeiblich");
                }
                if (House.hasHouse(Script.getNRPID(p))) {
                    StringBuilder houses = new StringBuilder();
                    for (House h : House.getHouses(Script.getNRPID(p))) {
                        houses.append(", ").append(h.getID());
                    }
                    tg.sendMessage(PREFIX + " §8× §6Wohnhaft:§6" + houses.substring(1));
                } else {
                    tg.sendMessage(PREFIX + " §8× §6Wohnhaft: §6Obdachlos");
                }
                Me.sendMessage(p, "zeigt " + Script.getName(tg) + " " + (Script.getGender(p)==Gender.MALE?"seinen":"ihren") + " Personalausweis.");
                break;
            case "Lizenzen zeigen":
                tg.sendMessage(PREFIX + Script.getName(p) + "s Lizenzen:");
                for(Licenses license : Licenses.values()) {
                    if(license.hasLicense(Script.getNRPID(p))) {
                        tg.sendMessage(PREFIX + " §8× §6" + license.getName());
                    } else {
                    }
                }
                break;
            case "Tasche zeigen":
                Annehmen.offer.put(tg.getName() + ".tasche", p.getName());
                p.sendMessage(PREFIX + "Du hast " +  Script.getName(tg) + " angeboten deine Tasche zu sehen.");
                tg.sendMessage(PREFIX + Script.getName(p) + " hat dir angeboten " + (Script.getGender(p)==Gender.MALE?"seine":"ihre") + " Tasche zu sehen.");
                Script.sendAcceptMessage(tg);
                break;
            case "Küssen":
                for(Krankheit krankheit : Krankheit.values()) {
                    if(krankheit.isInfected(Script.getNRPID(p)) || krankheit.isInfected(Script.getNRPID(tg))) {
                        if(krankheit.isTransmittable() && Script.getRandom(1, 100) >= 30) {
                            krankheit.add(Script.getNRPID(tg));
                        }
                    }
                }
                Me.sendMessage(p, "gibt " + Script.getName(tg) + " einen Kuss.");
                new Particle(org.bukkit.Particle.HEART, p.getEyeLocation(), false, 0.001F, 0.001F, 0.001F, 0.001F, 1).sendAll();
                new Particle(org.bukkit.Particle.HEART, tg.getEyeLocation(), false, 0.001F, 0.001F, 0.001F, 0.001F, 1).sendAll();
                break;
            case "Tragen":
                if(Tragen.cooldown.containsKey(p)) {
                    if(Tragen.cooldown.get(p) + Tragen.TIMEOUT > System.currentTimeMillis()) {
                        p.sendMessage(Messages.ERROR + "Du kannst nur alle 5 Minuten einen Spieler tragen.");
                        return;
                    }
                }

                if(Sperre.TRAGENSPERRE.isActive(Script.getNRPID(p))) {
                    p.sendMessage(Messages.ERROR + "Du darfst derzeit keine Spieler tragen.");
                    return;
                }

                if(Tragen.cooldown.containsKey(tg)) {
                    p.sendMessage(Messages.ERROR + "Der Spieler kann derzeit nicht getragen werden.");
                    return;
                }

                if(p.getPassenger() != null) {
                    p.sendMessage(Messages.ERROR + "Du trägst bereits einen Spieler.");
                    return;
                }

                if(Tragen.tragen.containsKey(tg)) {
                    p.sendMessage(Messages.ERROR + "Der Spieler wird bereits getragen.");
                    return;
                }
                tg.closeInventory();
                Tragen.tragen.put(p, tg);
                Tragen.cooldown.put(p, System.currentTimeMillis());
                Tragen.cooldown.put(tg, System.currentTimeMillis());
                p.sendMessage(PREFIX + "Du trägst nun " + Script.getName(tg) + ".");
                tg.sendMessage(PREFIX + "Du wirst nun von " + Script.getName(p) + " getragen.");
                Me.sendMessage(p, "trägt nun " + Script.getName(tg) + ".");
                p.sendMessage(Messages.INFO + "Du kannst " + Script.getName(tg) + " mit §8/§6tragen §fwieder absetzen.");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.setPassenger(tg);
                    }
                }.runTaskLater(de.newrp.main.getInstance(), 5L);
                break;
            case "Handschellen öffnen":
                if(Handschellen.isCuffed(tg)) {
                    Handschellen.uncuff(tg);
                    Me.sendMessage(p, "nimmt " + Script.getName(tg) + " die Handschellen ab.");
                    if(Beruf.hasBeruf(p) && Beruf.getBeruf(p) == Beruf.Berufe.POLICE && Duty.isInDuty(p)) {
                        p.getInventory().addItem(Equip.Stuff.HANDSCHELLEN.getItem());
                    }
                    Script.unfreeze(tg);
                    p.sendMessage(Messages.INFO + "Du hast " + Script.getName(tg) + " die Handschellen abgenommen.");
                    tg.sendMessage(Messages.INFO + Script.getName(p) + " hat dir die Handschellen abgenommen.");
                } else {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in Handschellen.");
                }
                break;
            case "Durchsuchen":
                if(!Beruf.hasBeruf(p)) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return;
                }

                if(Beruf.getBeruf(p) != Beruf.Berufe.POLICE) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return;
                }

                if(!Duty.isInDuty(p)) {
                    p.sendMessage(Messages.ERROR + "Du bist nicht im Dienst.");
                    return;
                }

                if(p == tg) {
                    p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst durchsuchen.");
                    return;
                }

                if(p.getLocation().distance(tg.getLocation()) > 5) {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
                    return;
                }

                Inventory inv2 = Bukkit.createInventory(null, 9*4, "§8[§9Frisk§8] §e» §9" + tg.getName());
                for(ItemStack is : tg.getInventory().getContents()) {
                    if(is == null) continue;
                    inv2.addItem(is);
                }
                p.openInventory(inv2);
                Me.sendMessage(p, "durchsucht " + Script.getName(tg) + ".");
                p.sendMessage(Messages.INFO + "Du hast " + Script.getMoney(tg, PaymentType.CASH) + "€ Bargeld gefunden.");
                break;
            case "Polizeicomputer":
                Inventory inventory = Bukkit.createInventory(null, 9, "§8[§9Polizeicomputer§8] §9" + Script.getName(tg));
                inventory.setItem(0, new ItemBuilder(Material.OAK_SIGN).setName("Personendaten").setLore("§8» §7Klicke um die Personendaten von " + Script.getName(tg) + " zu sehen.").build());
                inventory.setItem(1, new ItemBuilder(Material.OAK_SIGN).setName("Lizenzen").setLore("§8» §7Klicke um die Lizenzen von " + Script.getName(tg) + " zu sehen.").build());
                inventory.setItem(2, new ItemBuilder(Material.OAK_SIGN).setName("Gefährlichkeitsstufe").setLore("§8» §7Gefährlichkeitsstufe von " + Script.getName(tg) + "§8: §7" + Policecomputer.getDangerLevel(tg)).build());
                inventory.setItem(3, new ItemBuilder(Material.OAK_SIGN).setName("Orten").setLore("§8» §7Klicke um " + Script.getName(tg) + " zu orten.").build());
                inventory.setItem(4, new ItemBuilder(Material.OAK_SIGN).setName("Gesucht?").setLore("§8» " + (Fahndung.isFahnded(tg) ? "§cGesucht" : "§aNicht gesucht")).build());
                p.openInventory(inventory);
                Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " fragt die Daten von " + Script.getName(tg) + " ab.");
                break;
            case "Finanzen zeigen":
                ATM atm = ATM.getNearATM(p);
                Me.sendMessage(p, "zeigt " + Script.getName(tg) + " seine Finanzen.");
                tg.sendMessage(PREFIX + "Finanzen von " + Script.getName(p) + ":");
                tg.sendMessage(PREFIX + "Bargeld §8» §7" + Script.getMoney(p, PaymentType.CASH) + "€");
                if (atm != null) {
                    tg.sendMessage(PREFIX + "Kontostand §8» §7" + Script.getMoney(p, PaymentType.BANK) + "€");
                }
                break;
            case "Gesundheit zeigen":
                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " deinen Gesundheitszustand gezeigt.");
                tg.sendMessage(PREFIX + "Gesundheitszustand von " + Script.getName(p) + ":");
                tg.spigot().sendMessage(HealthCommand.health(p));
                tg.spigot().sendMessage(HealthCommand.blood(p));
                tg.spigot().sendMessage(HealthCommand.hunger(p));
                tg.spigot().sendMessage(HealthCommand.thirst(p));
                tg.spigot().sendMessage(HealthCommand.fat(p));
                tg.spigot().sendMessage(HealthCommand.muscle(p));
                break;
            case "Abhängigkeit behandeln":
                if(!Krankheit.ABHAENGIGKEIT.isInfected(Script.getNRPID(tg))) {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist nicht abhängig.");
                    return;
                }

                if(addiction_cooldown.contains(tg.getName())) {
                    p.sendMessage(Messages.ERROR + "Der Spieler wurde bereits behandelt.");
                    return;
                }

                addiction_cooldown.add(tg.getName());
                Drogen.healAddiction(tg);
                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " wegen " + (Script.getGender(tg)== Gender.MALE?"seiner":"ihrer") + " Abhängigkeit behandelt (" + (int) (Drogen.getAddictionHeal(tg)+1) + "/" + (Premium.hasPremium(tg) ? 1 : 3) + ").");
                tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " wegen deiner Abhängigkeit behandelt (" + (int) (Drogen.getAddictionHeal(tg)+1) + "/" + (Premium.hasPremium(tg) ? 1 : 3) + ").");
                break;
            case "Zeige Ticket":
                if(TicketCommand.getTicket(p) == null) {
                    p.sendMessage(Messages.ERROR + "Du bist in keinem Ticket.");
                    return;
                }

                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " gezeigt, dass du im Ticket bist.");
                tg.sendMessage(PREFIX + Script.getName(p) + " ist derzeit im Ticket.");
                break;
        }
    }

    @EventHandler
    public void clickInventoryUtil(InventoryClickEvent e) {
        if(e.getView().getTitle().startsWith("§8[§9Tasche§8] §e» §9") || e.getView().getTitle().startsWith("§8[§9Frisk§8] §e» §9")) {
            e.setCancelled(true);
            e.getView().close();
        }
    }

    @EventHandler
    public void onSneak(EntityDismountEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if(!(e.getDismounted() instanceof Player)) return;
        Player tg = (Player) e.getDismounted();
        if(!Tragen.tragen.containsKey(tg)) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!Tragen.tragen.containsKey(tg)) cancel();
                tg.setPassenger(p);
            }
        }.runTaskLater(de.newrp.main.getInstance(), 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        interacting.remove(e.getPlayer().getName());
    }


}
