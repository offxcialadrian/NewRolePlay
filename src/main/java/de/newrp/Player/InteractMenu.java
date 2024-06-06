package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Drone;
import de.newrp.Berufe.Duty;
import de.newrp.Berufe.Equip;
import de.newrp.Chat.Me;
import de.newrp.GFB.GFB;
import de.newrp.House.House;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Drogen;
import de.newrp.Organisationen.Organisation;
import de.newrp.Police.Handschellen;
import de.newrp.Ticket.TicketCommand;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.addiction.IAddictionService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class InteractMenu implements Listener {

    public static HashMap<String, String> interacting = new HashMap<>();
    public static String PREFIX = "§8[§6Interaktion§8] §6» §7";
    public static ArrayList<String> addiction_cooldown = new ArrayList<>();
    public static HashMap<String, Long> cooldown = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();


    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        if (!(e.getRightClicked() instanceof Player)) return;
        Player tg = (Player) e.getRightClicked();
        if (!p.isSneaking()) return;
        if (Friedhof.isDead(p)) return;
        if (Drone.isDrone(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst als Drohne nicht interagieren.");
            return;
        }

        if (Drone.isDrone(tg)) {
            p.sendMessage(Messages.ERROR + "Du kannst nicht mit einer Drohne interagieren.");
            return;
        }


        if (SDuty.isSDuty(tg)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst nicht mit " + Script.getName(tg) + " interagieren, da " + (Script.getGender(tg) == Gender.MALE ? "er" : "sie") + " sich im Supporter-Dienst befindet.");
            return;
        }

        if (AFK.isAFK(tg)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst nicht mit " + Script.getName(tg) + " interagieren, da " + (Script.getGender(tg) == Gender.MALE ? "er" : "sie") + " sich im AFK-Modus befindet.");
            return;
        }

        interacting.put(p.getName(), tg.getName());

        Inventory inv = Bukkit.createInventory(null, 6 * 9, "§8» §eInteraktion");

        if (Beruf.hasBeruf(p) && (Beruf.getBeruf(p) == Beruf.Berufe.POLICE || Beruf.getBeruf(p) == Beruf.Berufe.RETTUNGSDIENST) && Duty.isInDuty(p)) {
            inv.setItem(12, new ItemBuilder(Material.END_ROD).setName("§6Drogentest").setLore("§8× §7Führe einen Drogentest bei §6" + Script.getName(tg) + "§7 durch.").build());
            inv.setItem(14, new ItemBuilder(Material.END_ROD).setName("§6Alkoholtest").setLore("§8× §7Führe einen Alkoholtest bei §6" + Script.getName(tg) + "§7 durch.").build());
        }

        if (Fesseln.isTiedUp(tg)) {
            inv.setItem(13, new ItemBuilder(Material.IRON_DOOR).setName("§6Fesseln öffnen").setLore("§8× §7Öffne die Fesseln von §6" + Script.getName(tg) + "§7.").build());
        }

        inv.setItem(20, new ItemBuilder(Material.BOOK).setName("§6Personalausweis zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deinen Personalausweis.").build());
        inv.setItem(21, new ItemBuilder(Material.BOOK).setName("§6Lizenzen zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deine Lizenzen.").build());
        inv.setItem(22, new ItemBuilder(Material.CHEST).setName("§6Tasche zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deine Tasche.").build());
        inv.setItem(23, new ItemBuilder(Material.BOOK).setName("§6Finanzen zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deine Finanzen.").build());
        inv.setItem(24, new ItemBuilder(Material.PLAYER_HEAD).setName("§6Gesundheit zeigen").setLore("§8× §7Zeige §6" + Script.getName(tg) + " §7deine Gesundheit.").build());
        if (Organisation.hasOrganisation(p) || Beruf.hasBeruf(p)) {
            inv.setItem(29, new ItemBuilder(Material.GOLD_INGOT).setName("§6Kommunikationsmittel zerstören").setLore("§8× §7Zerstöre die Kommunikationsmittel von §6" + Script.getName(tg) + "§7.").build());
        }
        inv.setItem(30, new ItemBuilder(Material.RED_TULIP).setName("§6Küssen").setLore("§8× §7Küsse §6" + Script.getName(tg) + "§7.").build());
        inv.setItem(31, new ItemBuilder(Material.LEAD).setName("§6Tragen").setLore("§8× §7Trage §6" + Script.getName(tg) + "§7.").build());
        inv.setItem(32, new ItemBuilder(Material.BARRIER).setName("§6Zeige Ticket").setLore("§8× §7Zeige §6" + Script.getName(tg) + "§7, dass du im Ticket bist.").build());

        if ((Handschellen.isCuffed(tg) && Beruf.getBeruf(p) == Beruf.Berufe.POLICE && Duty.isInDuty(p)) || p.getInventory().contains(Script.brechstange())) {
            inv.setItem(40, new ItemBuilder(Material.IRON_DOOR).setName("§6Handschellen öffnen").setLore("§8× §7Öffne die Handschellen von §6" + Script.getName(tg) + "§7.").build());
        }

        if (Beruf.hasBeruf(p)) {
            if (Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && Duty.isInDuty(p)) {
                inv.setItem(39, new ItemBuilder(Material.IRON_DOOR).setName("§6Durchsuchen").setLore("§8× §7Durchsuche §6" + Script.getName(tg) + "§7.").build());
                inv.setItem(41, new ItemBuilder(Material.NETHER_STAR).setName("§6Marke zeigen").setLore("§8× §7Zeige deine Marke").build());
            }

            if (Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST) && Duty.isInDuty(p)) {
                inv.setItem(39, new ItemBuilder(Material.PLAYER_HEAD).setName("§6Abhängigkeit behandeln").setLore("§8× §7Behandle die Abhängigkeit von §6" + Script.getName(tg) + "§7.").build());
            }
        }

        Script.fillInv(inv);
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§8» §eInteraktion")) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        if (cooldown.containsKey(p.getName()) && cooldown.get(p.getName()) > System.currentTimeMillis()) return;
        if (!interacting.containsKey(p.getName())) return;
        Player tg = Script.getPlayer(interacting.get(p.getName()));
        if (tg == null) return;
        if(Drone.isDrone(p) || Drone.isDrone(tg)) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (p.getLocation().distance(tg.getLocation()) > 2) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return;
        }
        e.getView().close();
        interacting.remove(p.getName());
        cooldown.put(p.getName(), System.currentTimeMillis() + 20L);

        switch (e.getCurrentItem().getItemMeta().getDisplayName().replace("§6", "")) {
            case "Personalausweis zeigen":
                if (!Licenses.PERSONALAUSWEIS.hasLicense(Script.getNRPID(p))) {
                    if (Licenses.PERSONALAUSWEIS.isLocked(Script.getNRPID(p))) {
                        p.sendMessage(Messages.ERROR + "Du wurdest ausgebürgert.");
                        tg.sendMessage(PREFIX + "Dieser Spieler wurde ausgebürgert.");
                        return;
                    }
                    p.sendMessage(Messages.ERROR + "Du hast keinen Personalausweis.");
                    return;
                }
                tg.sendMessage(PREFIX + Script.getName(p) + "s Personalien:");
                tg.sendMessage(PREFIX + " §8× §6Name: " + Script.getName(p));
                tg.sendMessage(PREFIX + " §8× §6Geburtsdatum: §c" + Script.getBirthday(Script.getNRPID(p)) + " (" + Script.getAge(Script.getNRPID(p)) + ")");
                if (Script.getGender(p).equals(Gender.MALE)) {
                    tg.sendMessage(PREFIX + " §8× §6Geschlecht: §cMännlich");
                } else if (Script.getGender(p).equals(Gender.FEMALE)) {
                    tg.sendMessage(PREFIX + " §8× §6Geschlecht: §cWeiblich");
                }
                if(BeziehungCommand.isMarried(p)) {
                    tg.sendMessage(PREFIX + " §8- §6Verheiratet mit: §c" + BeziehungCommand.getPartner(p).getName());
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
                Me.sendMessage(p, "zeigt " + Script.getName(tg) + " " + (Script.getGender(p) == Gender.MALE ? "seinen" : "ihren") + " Personalausweis.");
                break;
            case "Lizenzen zeigen":
                tg.sendMessage(PREFIX + Script.getName(p) + "s Lizenzen:");
                Me.sendMessage(p, "zeigt " + Script.getName(tg) + " " + (Script.getGender(p) == Gender.MALE ? "seine" : "ihre") + " Lizenzen.");
                for (Licenses license : Licenses.values()) {
                    if (license.hasLicense(Script.getNRPID(p))) {
                        tg.sendMessage(PREFIX + " §8× §6" + license.getName());
                    }
                }
                break;
            case "Tasche zeigen":
                Annehmen.offer.put(tg.getName() + ".tasche", p.getName());
                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " angeboten deine Tasche zu sehen.");
                tg.sendMessage(PREFIX + Script.getName(p) + " hat dir angeboten " + (Script.getGender(p) == Gender.MALE ? "seine" : "ihre") + " Tasche zu sehen.");
                Script.sendAcceptMessage(tg);
                break;
            case "Küssen":
                for (Krankheit krankheit : Krankheit.values()) {
                    if (krankheit.isInfected(Script.getNRPID(p)) || krankheit.isInfected(Script.getNRPID(tg))) {
                        if (krankheit.isTransmittable() && Script.getRandom(1, 10) >= 1) {
                            if (!krankheit.isImpfed(Script.getNRPID(tg))) {
                                krankheit.add(Script.getNRPID(tg));
                            }
                        }
                    }
                }
                Me.sendMessage(p, "gibt " + Script.getName(tg) + " einen Kuss.");
                new Particle(org.bukkit.Particle.HEART, p.getEyeLocation(), false, 0.001F, 0.001F, 0.001F, 0.001F, 1).sendAll();
                new Particle(org.bukkit.Particle.HEART, tg.getEyeLocation(), false, 0.001F, 0.001F, 0.001F, 0.001F, 1).sendAll();
                break;
            case "Tragen":

                if (Fesseln.isTiedUp(p)) {
                    Script.sendActionBar(p, Messages.ERROR + "Du bist gefesselt.");
                    return;
                }

                if(GFB.CURRENT.containsKey(tg.getName()) && tg.getLocation().distance(GFB.CURRENT.get(tg.getName()).getLocation()) < 10) {
                    p.sendMessage(Messages.ERROR + "Du kannst den Spieler nicht tragen.");
                    return;
                }

                if (Tragen.cooldown.containsKey(p)) {
                    if (Tragen.cooldown.get(p) + Tragen.TIMEOUT > System.currentTimeMillis()) {
                        p.sendMessage(Messages.ERROR + "Du kannst erst in " + Script.getRemainingTime(Tragen.cooldown.get(p) + Tragen.TIMEOUT) + " wieder einen Spieler tragen.");
                        return;
                    }
                }

                if (Tragen.cooldown.containsKey(tg)) {
                    if (Tragen.cooldown.get(tg) + Tragen.TIMEOUT > System.currentTimeMillis()) {
                        p.sendMessage(Messages.ERROR + "Der Spieler kann erst in " + Script.getRemainingTime(Tragen.cooldown.get(tg) + Tragen.TIMEOUT) + " wieder getragen werden.");
                        Tragen.cooldown.put(p, System.currentTimeMillis() - (Tragen.TIMEOUT + TimeUnit.SECONDS.toMillis(30)));
                        p.sendMessage(Messages.INFO + "Aufgrund einem fehlgeschlagenen Tragen-Versuch wurde dein Tragen-Cooldown auf 30 Sekunden gesetzt");
                        return;
                    }
                }

                if (Sperre.TRAGENSPERRE.isActive(Script.getNRPID(p))) {
                    p.sendMessage(Messages.ERROR + "Du darfst derzeit keine Spieler tragen.");
                    Tragen.cooldown.put(p, System.currentTimeMillis() - (Tragen.TIMEOUT + TimeUnit.SECONDS.toMillis(30)));
                    p.sendMessage(Messages.INFO + "Aufgrund einem fehlgeschlagenen Tragen-Versuch wurde dein Tragen-Cooldown auf 30 Sekunden gesetzt");
                    return;
                }

                if (p.getPassenger() != null) {
                    p.sendMessage(Messages.ERROR + "Du trägst bereits einen Spieler.");
                    Tragen.cooldown.put(p, System.currentTimeMillis() - (Tragen.TIMEOUT + TimeUnit.SECONDS.toMillis(30)));
                    p.sendMessage(Messages.INFO + "Aufgrund einem fehlgeschlagenen Tragen-Versuch wurde dein Tragen-Cooldown auf 30 Sekunden gesetzt");
                    return;
                }

                if (Tragen.tragen.containsKey(tg)) {
                    p.sendMessage(Messages.ERROR + "Der Spieler wird bereits getragen.");
                    Tragen.cooldown.put(p, System.currentTimeMillis() - (Tragen.TIMEOUT + TimeUnit.SECONDS.toMillis(30)));
                    p.sendMessage(Messages.INFO + "Aufgrund einem fehlgeschlagenen Tragen-Versuch wurde dein Tragen-Cooldown auf 30 Sekunden gesetzt");
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
                }.runTaskLater(NewRoleplayMain.getInstance(), 5L);
                break;
            case "Handschellen öffnen":
                if (Fesseln.isTiedUp(p)) {
                    Script.sendActionBar(p, Messages.ERROR + "Du bist gefesselt.");
                    return;
                }
                if (Handschellen.isCuffed(tg)) {
                    Handschellen.uncuff(tg);
                    Me.sendMessage(p, "nimmt " + Script.getName(tg) + " die Handschellen ab.");
                    if (Beruf.hasBeruf(p) && Beruf.getBeruf(p) == Beruf.Berufe.POLICE && Duty.isInDuty(p)) {
                        ItemStack is = Equip.Stuff.HANDSCHELLEN.getItem();
                        is.setAmount(1);
                        p.getInventory().addItem(is);
                    }
                    Script.unfreeze(tg);
                    p.sendMessage(Messages.INFO + "Du hast " + Script.getName(tg) + " die Handschellen abgenommen.");
                    tg.sendMessage(Messages.INFO + Script.getName(p) + " hat dir die Handschellen abgenommen.");
                } else {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in Handschellen.");
                }
                break;
            case "Durchsuchen":
                if (Fesseln.isTiedUp(p)) {
                    Script.sendActionBar(p, Messages.ERROR + "Du bist gefesselt.");
                    return;
                }

                if (!Beruf.hasBeruf(p)) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return;
                }

                if (Beruf.getBeruf(p) != Beruf.Berufe.POLICE) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return;
                }

                if (!Duty.isInDuty(p)) {
                    p.sendMessage(Messages.ERROR + "Du bist nicht im Dienst.");
                    return;
                }

                if (p == tg) {
                    p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst durchsuchen.");
                    return;
                }

                if (p.getLocation().distance(tg.getLocation()) > 5) {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
                    return;
                }

                Inventory inv2 = Bukkit.createInventory(null, 9 * 4, "§8[§9Frisk§8] §e» §9" + tg.getName());
                for (ItemStack is : tg.getInventory().getContents()) {
                    if (is == null) continue;
                    inv2.addItem(is);
                }
                p.openInventory(inv2);
                Me.sendMessage(p, "durchsucht " + Script.getName(tg) + ".");
                p.sendMessage(Messages.INFO + "Du hast " + Script.getMoney(tg, PaymentType.CASH) + "€ Bargeld gefunden.");
                break;
            case "Marke zeigen":
                Me.sendMessage(p, "zeigt " + Script.getName(tg) + " seine Marke.");
                tg.sendMessage(PREFIX + "Marke von " + Script.getName(p) + ":");
                tg.sendMessage(PREFIX + " §8× §6" + Beruf.getAbteilung(p).getName());
                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " deine Marke gezeigt.");
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
                final IAddictionService addictionService = DependencyContainer.getContainer().getDependency(IAddictionService.class);
                if (addictionService.isAddictedToAnything(tg)) {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist nicht abhängig.");
                    return;
                }

                if (addiction_cooldown.contains(tg.getName())) {
                    p.sendMessage(Messages.ERROR + "Der Spieler wurde bereits behandelt.");
                    return;
                }

                addiction_cooldown.add(tg.getName());
                final int neededHealAmount = Premium.hasPremium(tg) ? 1 : 3;
                final int healAfterSession = addictionService.healPlayer(tg);
                final boolean healed = healAfterSession >= neededHealAmount;

                final String prefix = "§8[§4Abhängigkeit§8] §4» §7";
                if (healed) {
                    p.sendMessage(prefix + "Du hast §4" + Script.getName(tg) + " §7erfolgreich behandelt.");
                    tg.sendMessage(prefix + "Du wurdest von §4" + Script.getName(p) + " §7erfolgreich behandelt.");
                    tg.sendMessage(Messages.INFO + "Du bist nun nicht mehr abhängig");
                    addictionService.clearAddiction(tg);
                } else {
                    p.sendMessage(prefix + "Du hast §4" + Script.getName(tg) + " §7behandelt (Heilung: §4" + healAfterSession + " §7von §4" + neededHealAmount + "§7)");
                    tg.sendMessage(prefix + "Du wurdest von §4" + Script.getName(p) + " §7behandelt (Heilung: §4" + healAfterSession + " §7von §4" + neededHealAmount + "§7)");
                }
                Beruf.Berufe.RETTUNGSDIENST.sendMessage(prefix + "Der Spieler §4" + Script.getName(tg) + " §7wurde von §4" + Script.getName(p) + " §7behandelt (Heilung: §4" + healAfterSession + " §7von §4" + neededHealAmount + "§7)");
                break;
            case "Zeige Ticket":
                if (TicketCommand.getTicket(p) == null) {
                    p.sendMessage(Messages.ERROR + "Du bist in keinem Ticket.");
                    return;
                }

                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " gezeigt, dass du im Ticket bist.");
                tg.sendMessage(PREFIX + Script.getName(p) + " ist derzeit im Ticket.");
                break;
            case "Fesseln öffnen":
                if (Fesseln.isTiedUp(p)) {
                    Script.sendActionBar(p, Messages.ERROR + "Du bist gefesselt.");
                    return;
                }

                if (!Fesseln.isTiedUp(tg)) {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist nicht gefesselt.");
                    return;
                }

                p.sendMessage(PREFIX + "Du hast begonnen, die Fesseln von " + Script.getName(tg) + " zu öffnen.");
                p.sendMessage(Messages.INFO + "Bleibe nun eng bei " + Script.getName(tg) + " um die Fesseln zu öffnen.");
                Me.sendMessage(p, "öffnet die Fesseln von " + Script.getName(tg) + ".");
                LEVEL.put(p.getName(), 0);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!Fesseln.isTiedUp(tg)) {
                            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht mehr gefesselt.");
                            cancel();
                            return;
                        }
                        if (p.getLocation().distance(tg.getLocation()) > 2) {
                            p.sendMessage(Messages.ERROR + "Du bist zu weit entfernt.");
                            cancel();
                            return;
                        }
                        if (LEVEL.get(p.getName()) >= 15) {
                            Fesseln.untie(tg);
                            Me.sendMessage(p, "hat " + Script.getName(tg) + " die Fesseln abgenommen.");
                            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " die Fesseln abgenommen.");
                            tg.sendMessage(PREFIX + "Dir wurden die Fesseln abgenommen.");
                            cancel();
                            return;
                        }
                        progressBar(16, p);
                        LEVEL.replace(p.getName(), LEVEL.get(p.getName()) + 1);
                    }
                }.runTaskTimer(NewRoleplayMain.getInstance(), 0L, 20L);
                break;
            case "Drogentest":
                p.performCommand("drugtest " + tg.getName());
                break;
            case "Alkoholtest":
                p.performCommand("alktest " + tg.getName());
                break;
            case "Kommunikationsmittel zerstören":
                if(AFK.isAFK(tg)) {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist AFK.");
                    break;
                }

                if(SDuty.isSDuty(tg)) {
                    p.sendMessage(Messages.ERROR + "Der Spieler ist im Support-Dienst.");
                    break;
                }

                if(!Mobile.hasPhone(tg)) {
                    p.sendMessage(Messages.ERROR + "Der Spieler hat kein Handy.");
                    break;
                }

                if(Mobile.getPhone(tg).isDestroyed(tg)) {
                    p.sendMessage(Messages.ERROR + "Die Kommunikationsmittel sind bereits zerstört.");
                    break;
                }

                if(p.getLocation().distance(tg.getLocation()) > 5) {
                    p.sendMessage(Messages.ERROR + "Du bist zu weit entfernt.");
                    break;
                }

                if (tg.isInsideVehicle()) {
                    if (tg.getVehicle() instanceof Player) {
                        p.sendMessage(Messages.ERROR + "Die Person wird gerade gepackt.");
                        break;
                    }
                }

                p.sendMessage(PREFIX + "Du hast die Kommunikationsmittel von " + tg.getName() + " zerstört.");
                tg.sendMessage(PREFIX + "Deine Kommunikationsmittel wurden von " + p.getName() + " zerstört.");
                Me.sendMessage(p, "zerstört die Kommunikationsmittel von " + Script.getName(tg) + ".");
                Mobile.getPhone(tg).setDestroyed(tg, true);
                Mobile.getPhone(tg).setOff(tg);
                break;
        }
    }

    @EventHandler
    public void clickInventoryUtil(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith("§8[§9Tasche§8] §e» §9") || e.getView().getTitle().startsWith("§8[§9Frisk§8] §e» §9")) {
            e.setCancelled(true);
            e.getView().close();
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        interacting.remove(e.getPlayer().getName());
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = LEVEL.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§a▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cFesseln öffnen.. §8» §a" + sb.toString());
    }

    @EventHandler
    public static void onBed(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getBlockData() instanceof Bed) {
                event.setCancelled(true);
            }
        }
    }
}
