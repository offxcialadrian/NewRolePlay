package de.newrp.Entertainment;

import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static de.newrp.Entertainment.Cards.ACE;

public class Kartenspiel implements CommandExecutor, Listener {

    private static final HashMap<String, Integer> bet = new HashMap<>();
    private static final HashMap<String, Integer> cashier = new HashMap<>();
    private static final HashMap<String, String> member = new HashMap<>();
    private static final HashMap<String, Integer> player = new HashMap<>();
    private static final ArrayList<String> game = new ArrayList<>();
    private static final HashMap<String, Integer> win = new HashMap<>();
    private static Integer all = 0;
    private static final String PREFIX = "§8[§6Kartenspiel§8] §6" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (Organisation.hasOrganisation(p)) {
            if (Organisation.getOrganisation(p) == Organisation.FALCONE) {
                p.sendMessage(Messages.ERROR + "Mitarbeiter des Casinos können kein Glücksspiel betreiben.");
                return true;
            }
        }

        if (Script.getAge(Script.getNRPID(p)) < 18) {
            p.sendMessage(Messages.ERROR + "Diese Funktion ist für dich aktuell nicht verfügbar.");
            return true;
        }

        if(win.containsKey(p.getName()) && win.get(p.getName()) >= Casino.getLimit()) {
            p.sendMessage(Messages.ERROR + "Du hast heute schon zu viel gewonnen. Versuche es morgen erneut.");
            return true;
        }

        if (Casino.getMoney() <= 0 || all >= Casino.getMax()) {
            p.sendMessage(Messages.ERROR + "Das Casino hat heute keine Ressourcen mehr übrig. Komm morgen wieder zurück.");
            return true;
        }

        if (p.getLocation().distance(new Location(Script.WORLD, 792, 109, 858)) < 8) {
            if (!bet.containsKey(p.getName())) {
                if (args.length > 0) {
                    try {
                        int i = Integer.parseInt(args[0]);
                        if (Script.getMoney(p, PaymentType.CASH) >= i) {
                            if (i < 1) {
                                p.sendMessage(Messages.ERROR + "Du kannst nicht unter 1€ setzen.");
                                return true;
                            }

                            if (i > Casino.getBet()) {
                                p.sendMessage(Messages.ERROR + "Du kannst nicht mehr als " + Casino.getBet() + "€ setzen.");
                                return true;
                            }

                            if (Casino.getMoney() < i || i + all > Casino.getMax()) {
                                p.sendMessage(Messages.ERROR + "Das Casino hat nicht mehr so viele Ressourcen übrig.");
                                return true;
                            }

                            p.sendMessage(Messages.INFO + "Glücksspiel kann süchtig machen. Spiele verantwortungsbewusst.");
                            p.sendMessage(Messages.INFO + "Solltest du Hilfe benötigen, wende dich an: §6https://www.bzga.de/");
                            bet.put(p.getName(), i);
                            Script.removeMoney(p, PaymentType.CASH, i);
                            cashier.put(p.getName(), Cards.getRandomCard().getValue());
                            player.put(p.getName(), Cards.getRandomCard().getValue());
                            Player m = Casino.closestMember(p.getLocation(), 6);
                            if (m == null) {
                                member.put(p.getName(), "Croupier");
                            } else {
                                member.put(p.getName(), m.getName());
                                Activity.grantActivity(Script.getNRPID(m), Activities.CASINO);
                            }
                            openGUI(p);
                        } else
                            p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld für diesen Einsatz.");
                    } catch (NumberFormatException ex) {
                        p.sendMessage(Messages.ERROR + "/kartenspiel [Einsatz]");
                    }
                } else
                    p.sendMessage(Messages.ERROR + "/kartenspiel [Einsatz]");
            } else
                openGUI(p);
        } else
            p.sendMessage(Messages.ERROR + "Du bist nicht am Kartenspiel-Tisch!");
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        if (e.getView().getTitle().equalsIgnoreCase("§6Casino §8» §7Kartenspiel")) {
            e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§aHit")) {
                p.closeInventory();
                if (!game.contains(p.getName())) {
                    hit(p);
                } else
                    p.sendMessage(Messages.ERROR + "Du bist schon in einem aktiven Spiel!");
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§cStand")) {
                p.closeInventory();
                if (!game.contains(p.getName())) {
                    stand(p);
                    game.add(p.getName());
                } else
                    p.sendMessage(Messages.ERROR + "Du bist schon in einem aktiven Spiel!");
            }
        }
    }

    private static void hit(Player p) {
        int pc = player.get(p.getName());
        Cards card = Cards.getRandomCard();
        int npc = pc;
        if (!(card.equals(ACE))) {
            npc = npc + card.getValue();
        } else {
            if (pc < 11)
                npc = (pc + 11);
            if (pc >= 11)
                npc = (pc + 1);

            if (npc == 21) {
                int random = Script.getRandom(1, 100);
                if (random <= Casino.getP()) {
                    hit(p);
                    return;
                }
            }
        }


        p.sendMessage(PREFIX + "Du hast Folgendes gezogen: §6" + card.getName());

        if (npc < 22) {
            if (npc == 21) {
                player.replace(p.getName(), 21);
                p.sendMessage("                             §7Du: §6" + player.get(p.getName()));
                win(p);
            } else {
                player.replace(p.getName(), npc);
                p.sendMessage("                             §7Du: §6" + player.get(p.getName()));
                openGUI(p);
            }
            if (Script.getPlayer(member.get(p.getName())) != null)
                Script.getPlayer(member.get(p.getName())).sendMessage(PREFIX + "§6" + p.getName() + " §7hat §6" + card.getName() + " §7gezogen. §8(§7" + player.get(p.getName()) + "§8)");
        } else {
            lose(p, true);
        }
    }

    public static void stand(Player p) {
        int c = cashier.get(p.getName());
        int pc = player.get(p.getName());
        Cards card = Cards.getRandomCard();
        int nc = c;
        if (!(card.equals(ACE))) {
            nc = (card.getValue() + c);
        } else {
            if (c < 11) {
                nc = (nc + 11);
            } else {
                nc++;
            }
        }

        if (nc > 21) {
            int random = Script.getRandom(1, 100);
            if (random <= Casino.getP()) {
                stand(p);
                return;
            }
        }

        if (nc >= 17 && nc <= pc) {
            int random = Script.getRandom(1, 100);
            if (random <= Casino.getP()) {
                stand(p);
                return;
            }
        }

        p.sendMessage(PREFIX + (member.get(p.getName()).equals("Croupier") ? "Der " : "") + member.get(p.getName()) + " zieht jetzt...");
        cashier.replace(p.getName(), nc);

        if (Script.getPlayer(member.get(p.getName())) != null)
            Script.getPlayer(member.get(p.getName())).sendMessage(PREFIX + "Du hast §6" + card.getName() + " §7gezogen. §8(§7" + cashier.get(p.getName()) + "§8)");

        new BukkitRunnable() {
            @Override
            public void run() {
                int nc = cashier.get(p.getName());
                p.sendMessage(PREFIX + (member.get(p.getName()).equals("Croupier") ? "Der " : "") + member.get(p.getName()) + " hat Folgendes gezogen: §6" + card.getName());
                if (nc < 22) {
                    p.sendMessage("                             §7" + member.get(p.getName()) + ": §6" + cashier.get(p.getName()));
                    if (nc > pc) {
                        lose(p, false);
                    } else {
                        if (nc < 17) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    stand(p);
                                }
                            }.runTaskLater(NewRoleplayMain.getInstance(), 20L * 2);
                        } else {
                            if (nc == pc) {
                                tie(p);
                            } else
                                win(p);
                        }
                    }
                } else {
                    win(p);
                }
            }
        }.runTaskLater(NewRoleplayMain.getInstance(), 20L * 3);
    }

    private static void openGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 4, "§6Casino §8» §7Kartenspiel");
        ItemStack bankIS = new ItemStack(Material.PAPER);
        ItemMeta bankMETA = bankIS.getItemMeta();
        bankMETA.setDisplayName("§6" + member.get(p.getName()));
        List<String> clore = new ArrayList<>();
        clore.add("§8» §6" + cashier.get(p.getName()));
        bankMETA.setLore(clore);
        bankIS.setItemMeta(bankMETA);

        ItemStack playerIS = new ItemStack(Material.PAPER);
        ItemMeta playerMETA = playerIS.getItemMeta();
        playerMETA.setDisplayName("§6Du");
        List<String> plore = new ArrayList<>();
        plore.add("§8» §6" + player.get(p.getName()));
        playerMETA.setLore(plore);
        playerIS.setItemMeta(playerMETA);

        ItemStack ziehen = new ItemStack(Material.EMERALD_BLOCK, 1);
        ItemMeta ziehenM = ziehen.getItemMeta();
        ziehenM.setDisplayName("§aHit");
        ziehen.setItemMeta(ziehenM);

        ItemStack halten = new ItemStack(Material.REDSTONE_BLOCK, 1);
        ItemMeta haltenM = halten.getItemMeta();
        haltenM.setDisplayName("§cStand");
        halten.setItemMeta(haltenM);

        inv.setItem(13, bankIS);
        inv.setItem(20, halten);
        inv.setItem(22, playerIS);
        inv.setItem(24, ziehen);
        p.openInventory(inv);
    }

    private static void win(Player p) {
        p.sendMessage(PREFIX + "Du hast §agewonnen§7 und §6" + (bet.get(p.getName()) * 2) + "€§7 erhalten!");
        p.sendMessage("                         " + "  §8» §6Du§7: §6" + player.get(p.getName()));
        p.sendMessage("                         " + "  §8» §6" + member.get(p.getName()) + "§7: §6" + cashier.get(p.getName()));
        Script.addMoney(p, PaymentType.CASH, bet.get(p.getName()) * 2);
        if (Script.getPlayer(member.get(p.getName())) != null)
            Script.getPlayer(member.get(p.getName())).sendMessage(PREFIX + "§6" + p.getName() + " §7hat gegen dich §cgewonnen§7. §8(§6" + player.get(p.getName()) + "§8:§6" + cashier.get(p.getName()) + "§8)");
        win.putIfAbsent(p.getName(), 0);
        win.put(p.getName(), win.get(p.getName()) + bet.get(p.getName()));
        all += (int) Math.round(bet.get(p.getName()) * 0.75);
        Casino.removeMoney((int) Math.round(bet.get(p.getName()) * 0.75));
        for (UUID id : Organisation.FALCONE.getMember()) if (Bukkit.getOfflinePlayer(id).isOnline()) if (Organisation.getRank(Bukkit.getPlayer(id)) >= 3)
            Bukkit.getPlayer(id).sendMessage(Casino.PREFIX + p.getName() + " hat beim Kartenspiel" + (member.get(p.getName()).equals("Croupier") ? "" : " mit " + member.get(p.getName())) + " §c" + bet.get(p.getName()) + "€ §7gewonnen.");
        bet.remove(p.getName());
        player.remove(p.getName());
        cashier.remove(p.getName());
        game.remove(p.getName());
    }

    private static void tie(Player p) {
        p.sendMessage(PREFIX + "Das Spiel ist unentschieden geendet und du hast deinen Wetteinsatz zurückerhalten (Tie).");
        p.sendMessage("                         " + "  §8» §6Du§7: §6" + player.get(p.getName()));
        p.sendMessage("                         " + "  §8» §6" + member.get(p.getName()) + "§7: §6" + cashier.get(p.getName()));
        Script.addMoney(p, PaymentType.CASH, bet.get(p.getName()));
        if (Script.getPlayer(member.get(p.getName())) != null)
            Script.getPlayer(member.get(p.getName())).sendMessage(PREFIX + "§6" + p.getName() + " §7hat gegen dich unentschieden. §8(§6" + player.get(p.getName()) + "§8:§6" + cashier.get(p.getName()) + "§8)");
        for (UUID id : Organisation.FALCONE.getMember()) if (Bukkit.getOfflinePlayer(id).isOnline()) if (Organisation.getRank(Bukkit.getPlayer(id)) >= 3)
            Bukkit.getPlayer(id).sendMessage(Casino.PREFIX + p.getName() + " hat beim Kartenspiel" + (member.get(p.getName()).equals("Croupier") ? "" : " mit " + member.get(p.getName())) + " 0€ gewonnen.");
        bet.remove(p.getName());
        player.remove(p.getName());
        cashier.remove(p.getName());
        game.remove(p.getName());
    }

    private static void lose(Player p, Boolean bust) {
        p.sendMessage(PREFIX + "Du hast §cverloren§7!");
        if (bust) {
            p.sendMessage("                             §7" + "BUST!");
        } else {
            p.sendMessage("                         " + "  §8» §6Du§7: §6" + player.get(p.getName()));
            p.sendMessage("                         " + "  §8» §6" + member.get(p.getName()) + "§7: §6" + cashier.get(p.getName()));
        }
        if (Script.getPlayer(member.get(p.getName())) != null)
            Script.getPlayer(member.get(p.getName())).sendMessage(PREFIX + "§6" + p.getName() + " §7hat gegen dich §averloren§7. §8(§6" + player.get(p.getName()) + "§8:§6" + cashier.get(p.getName()) + "§8)");
        all -= bet.get(p.getName());
        Casino.addMoney(bet.get(p.getName()));
        for (UUID id : Organisation.FALCONE.getMember()) if (Bukkit.getOfflinePlayer(id).isOnline()) if (Organisation.getRank(Bukkit.getPlayer(id)) >= 3)
            Bukkit.getPlayer(id).sendMessage(Casino.PREFIX + p.getName() + " hat beim Kartenspiel" + (member.get(p.getName()).equals("Croupier") ? "" : " mit " + member.get(p.getName())) + " §a" + bet.get(p.getName()) + "€ §7verloren.");
        if (!member.get(p.getName()).equals("Croupier")) Organisation.FALCONE.addExp(Script.getRandom(4, 8));
        bet.remove(p.getName());
        player.remove(p.getName());
        cashier.remove(p.getName());
        game.remove(p.getName());
    }
}