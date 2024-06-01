package de.newrp.Vehicle;

import de.newrp.API.*;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Steuern;
import de.newrp.NewRoleplayMain;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Fahrschule implements CommandExecutor, Listener {

    public static final String PREFIX = "§8[§bFahrschule§8] §b" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (Licenses.FUEHRERSCHEIN.isLocked(Script.getNRPID(player))) {
                player.sendMessage(Messages.ERROR + "Dein Führerschein wurde gesperrt.");
                return true;
            }

            if (player.getLocation().distance(HologramList.FAHRSCHULE.getLocation()) > 5) {
                player.sendMessage(Component.text(PREFIX + "Du bist nicht in der Fahrschule!"));
                return true;
            }

            if (Licenses.FUEHRERSCHEIN.hasLicense(Script.getNRPID(player))) {
                player.sendMessage(Component.text(PREFIX + "Du hast bereits deinen Führerschein!"));
                return true;
            }
            if (guis.containsKey(player.getUniqueId())) {
                player.openInventory(guis.get(player.getUniqueId()));
                return true;
            } else {
                if (index.containsKey(player.getUniqueId())) {
                    player.sendMessage(Component.text(PREFIX + "Du bist bereits in der Fahrschule!"));
                    return true;
                }
            }

            int level = Script.getLevel(player);
            if (level >= 3) {
                if (level > 48) level = 48;
                int amount = 3000 + level * 250;

                if (args.length > 0 && args[0].equalsIgnoreCase("confirm")) {
                    if (Script.getMoney(player, PaymentType.BANK) >= amount) {
                        index.put(player.getUniqueId(), 0);
                        player.playSound(HologramList.FAHRSCHULE.getLocation(), Sound.BLOCK_BELL_USE, 1.0F, 1.0F);
                        player.sendMessage(Component.text(PREFIX + "Willkommen zur Neustädter Fahrschule, bei uns bringen wir euch nicht nur zur Prüfung, sondern auch sicher ans Ziel!"));
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Heute werden wir uns mit den wichtigsten Regeln des Straßenverkehrs befassen.")), 6 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Es ist entscheidend, dass ihr diese Regeln versteht und befolgt, um sicher unterwegs zu sein.")), 10 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Erstens, die Grundregel: Fahrt immer defensiv.")), 16 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Das bedeutet, dass ihr eure Umgebung stets im Blick habt und bereit seid, auf unvorhergesehene Situationen zu reagieren.")), 20 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Zweitens, beachtet die Verkehrszeichen. Sie geben wichtige Hinweise zur Geschwindigkeit, Vorfahrt und anderen Verkehrsanweisungen.")), 26 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Wenn ihr diese Zeichen nicht kennt, könnt ihr schnell in Schwierigkeiten geraten.")), 30 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Drittens, haltet immer einen sicheren Abstand zum Fahrzeug vor euch.")), 36 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Das gibt euch genug Reaktionszeit, um zu bremsen, wenn nötig.")), 40 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Viertens, nutzt eure Blinker.")), 46 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Sie signalisieren anderen Verkehrsteilnehmern eure Absichten und tragen zur Sicherheit aller bei.")), 50 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Und schließlich, passt eure Geschwindigkeit den Straßenbedingungen an.")), 56 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Ob Regen, Schnee oder glatte Straßen, fahrt immer vorsichtig und mit angemessener Geschwindigkeit.")), 60 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Das sind nur einige der wichtigsten Regeln des Straßenverkehrs, die wir heute behandelt haben.")), 66 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Denkt immer daran, dass Sicherheit an erster Stelle steht.")), 70 * 20L);
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Gut, dann lasst uns nun überprüfen, was ihr heute verstanden habt!")), 75 * 20L);

                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                            questions(player);
                        }, 79 * 20L);
                    } else {
                        player.sendMessage(Component.text(PREFIX + "Du brauchst " + amount + "€ um mit der Fahrschule beginnen zu können!"));
                    }
                } else {
                    player.sendMessage(Component.text(PREFIX + "Deine Fahrschule kostet " + amount + "€ verwende §6/fahrschule confirm §7um sie zu beginnen."));
                }
            } else {
                player.sendMessage(Component.text(PREFIX + "Du musst mindestens Level-3 sein um die Fahrschule zu beginnen!"));
            }
        }
        return true;
    }

    private static void questions(Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        switch (index.get(player.getUniqueId())) {
            case 0:
                question(player, "Was bedeutet es, defensiv zu fahren?", "Schnell und aggressiv fahren, um Zeit zu sparen.", "Die Umgebung stets im Blick haben und bereit sein.", "Andere Verkehrsteilnehmer schikanieren und Vorfahrt erzwingen.");
                break;
            case 1:
                question(player, "Welche Bedeutung haben Verkehrszeichen im Straßenverkehr?", "Sie dienen lediglich zur Dekoration der Straßen.", "Sie sind nur für Fußgänger relevant.", "Sie geben wichtige Hinweise zu Verkehrsanweisungen.");
                break;
            case 2:
                question(player, "Warum ist es wichtig, einen sicheren Abstand einzuhalten?", "Um genug Reaktionszeit zu haben, um zu bremsen.", "Um schneller ans Ziel zu kommen.", "Um das andere Fahrzeug zu ärgern.");
                break;
            case 3:
                question(player, "Wofür dienen Blinker im Fahrzeug?", "Um die Innenbeleuchtung einzuschalten.", "Um den Motor zu starten.", "Um Anderen die eigenen Absichten zu signalisieren.");
                break;
            case 4:
                question(player, "Was tun Sie, wenn die Straßenbedingungen schwierig sind?", "Die Geschwindigkeit erhöhen, um schneller ans Ziel zu gelangen.", "Die Geschwindigkeit den Straßenbedingungen anpassen.", "In normaler Geschwindigkeit fahren um andere nicht zu behindern.");
                break;
            default:
                player.closeInventory();
                player.sendMessage(Component.text(PREFIX + "Bravo! Du bist bereit für den praktischen Teil."));

                player.teleport(new Location(player.getWorld(), 391, 77, 1131));
                Car car = Car.createCar(CarType.OPPEL, new Location(player.getWorld(), 391, 76.5, 1132, -90F, 0F), player);
                assert car != null;
                car.setLicenseplate("N-NO-OB");

                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Hier haben wir nur für dich den brandneuen Oppel!")), 2 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Mit diesem wirst du deine ersten Fahrerfahrungen sammeln.")), 7 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Verwende §6/car lock §7um dein Auto aufzuschließen.")), 14 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Nun kannst du dich in dieses hineinsetzen.")), 19 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Mit §6/car start §7kannst du nun vorsichtig deinen Motor starten.")), 24 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Du kannst das Auto mit A und D lenken.")), 29 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Schaust du nach unten/oben, bremst du ab.")), 34 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Geradeaus nimmst du die meiste Geschwindigkeit auf.")), 39 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Auch Steigungen schafft dieses Auto problemlos.")), 44 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Aber pass auf, wenn du vom Weg abkommst nimmt dein Auto schaden.")), 47 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Ebenso, wenn du andere Personen anfährst.")), 50 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Nur spezielles Werkzeug kann dein Auto reparieren.")), 53 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Selbstverständlich ist auch der Tank von Bedeutung.")), 58 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Unterschiedliche Autos haben einen anderen Verbrauch.")), 61 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Achte jedoch immer darauf, genug Kraftstoff über zu haben.")), 64 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Ansonsten kannst du an jeder Tankstelle mit §6/tanken (Menge) §7tanken.")), 67 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> player.sendMessage(Component.text(PREFIX + "Das war dann auch schon alles, du kannst dein Auto mit §6/car stop§7 bremsen, wenn du soweit bist.")), 74 * 20L);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                    player.teleport(new Location(player.getWorld(), 404, 77, 1120));
                    car.destroy(false);
                    int level = Script.getLevel(player);
                    if (level > 48) level = 48;
                    int amount = 3000 + level * 250;
                    if (Script.removeMoney(player, PaymentType.BANK, amount)) {
                        Stadtkasse.addStadtkasse(amount, "Fahrschule " + player.getName(), Steuern.Steuer.MEHRWERTSTEUER);
                        player.sendMessage(Component.text(PREFIX + "Herzlichen Glückwunsch, du hast deine Führerschein-Prüfung bestanden!"));
                        Script.addEXP(player, 10 + new Random().nextInt(20));
                        Licenses.FUEHRERSCHEIN.grant(Script.getNRPID(player));
                    } else {
                        player.sendMessage(PREFIX + "Du hast nicht genug Geld, um deinen Führerschein zu bezahlen!");
                    }
                    index.remove(player.getUniqueId());
                    guis.remove(player.getUniqueId());
                    //player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    Cache.loadScoreboard(player);
                }, 100 * 20L);
        }
    }

    private static final HashMap<UUID, Integer> index = new HashMap<>();

    private static void question(Player player, String q, String asw1, String asw2, String asw3) {
        Inventory gui = Bukkit.createInventory(player, 18, "Fahrschule");
        ItemStack question = new ItemStack(Material.ACACIA_SIGN);
        Script.setName(question, "§b" + q);
        gui.setItem(4, question);
        ItemStack answer1 = new ItemStack(Material.EMERALD);
        Script.setName(answer1, "§a" + asw1);
        gui.setItem(10, answer1);
        ItemStack answer2 = new ItemStack(Material.EMERALD);
        Script.setName(answer2, "§a" + asw2);
        gui.setItem(13, answer2);
        ItemStack answer3 = new ItemStack(Material.EMERALD);
        Script.setName(answer3, "§a" + asw3);
        gui.setItem(16, answer3);
        guis.put(player.getUniqueId(), gui);
        player.openInventory(gui);
    }

    private static final HashMap<UUID, Inventory> guis = new HashMap<>();

    @EventHandler
    public static void onAnswer(InventoryClickEvent event) {
        if (event.getWhoClicked().getOpenInventory().title().equals(Component.text("Fahrschule"))) {
            event.setCancelled(true);
            if (event.getSlot() == 10 || event.getSlot() == 13 || event.getSlot() == 16) {
                Player player = (Player) event.getWhoClicked();
                index.put(player.getUniqueId(), index.get(player.getUniqueId()) + 1);
                questions(player);
            }
        }
    }
}
