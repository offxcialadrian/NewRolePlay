package de.newrp.Entertainment;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.main;
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

import static de.newrp.Entertainment.Cards.ACE;

public class BlackJack implements CommandExecutor, Listener {

    private static final HashMap<String, Integer> bet = new HashMap<>();
    private static final HashMap<String, Integer> cashier = new HashMap<>();
    private static final HashMap<String, Integer> player = new HashMap<>();
    private static final ArrayList<String> game = new ArrayList<>();
    private static final String PREFIX = "§8[§6BlackJack§8] §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (p.getLocation().distance(new Location(Script.WORLD, 790, 109, 858)) < 5) {
            if (!bet.containsKey(p.getName())) {
                if (args.length == 1) {
                    try {
                        int i = Integer.parseInt(args[0]);
                        if (Script.getMoney(p, PaymentType.CASH) >= i) {
                            if (i < 1) {
                                p.sendMessage(Messages.ERROR + "Du kannst nicht unter 1€ setzen.");
                                return true;
                            }
                            if (i > 3000) {
                                p.sendMessage(Messages.ERROR + "Du kannst nicht mehr als 3000€ setzen.");
                                return true;
                            }
                            bet.put(p.getName(), i);
                            Script.removeMoney(p, PaymentType.CASH, i);
                            cashier.put(p.getName(), Cards.getRandomCard().getValue());
                            player.put(p.getName(), Cards.getRandomCard().getValue());
                            openGUI(p);
                        } else
                            p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld für diesen Einsatz.");
                    } catch (NumberFormatException ex) {
                        p.sendMessage(Messages.ERROR + "/blackjack [Einsatz]");
                    }
                } else
                    p.sendMessage(Messages.ERROR + "/blackjack [Einsatz]");
            } else
                openGUI(p);
        } else
            p.sendMessage(Messages.ERROR + "Du bist nicht am Blackjack-Tisch!");
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        if (e.getView().getTitle().equalsIgnoreCase("§6Casino §8» §7BlackJack")) {
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
                if (random < 80) {
                    hit(p);
                    return;
                }
            }
        }


        p.sendMessage(PREFIX + "Du hast folgendes gezogen: §6" + card.getName());

        if (npc < 22) {
            if (npc == 21) {
                player.replace(p.getName(), 21);
                p.sendMessage(PREFIX + "Du: §6" + player.get(p.getName()));
                win(p);
            } else {
                player.replace(p.getName(), npc);
                p.sendMessage(PREFIX + "Du: §6" + player.get(p.getName()));
                openGUI(p);
            }
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
            if (random <= 95) {
                stand(p);
                return;
            }
        }

        if (nc >= 17 && nc <= pc) {
            int random = Script.getRandom(1, 100);
            if (random <= 95) {
                stand(p);
                return;
            }
        }

        p.sendMessage(PREFIX + "Der Croupier zieht jetzt...");
        cashier.replace(p.getName(), nc);

        new BukkitRunnable() {
            @Override
            public void run() {
                int nc = cashier.get(p.getName());
                p.sendMessage(PREFIX + "Der Croupier hat folgendes gezogen: §6" + card.getName());
                if (nc < 22) {
                    if (nc > pc) {
                        p.sendMessage(PREFIX + "Croupier: §6" + cashier.get(p.getName()));
                        lose(p, false);
                    } else {
                        p.sendMessage(PREFIX + "Croupier: §6" + cashier.get(p.getName()));
                        if (nc < 17) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    stand(p);
                                }
                            }.runTaskLater(main.getInstance(), 20L * 2);
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
        }.runTaskLater(main.getInstance(), 20L * 3);
    }

    private static void openGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 4, "§6Casino §8» §7BlackJack");
        ItemStack bankIS = new ItemStack(Material.PAPER);
        ItemMeta bankMETA = bankIS.getItemMeta();
        bankMETA.setDisplayName("§6Croupier");
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
        p.sendMessage(PREFIX + "  §8» §6Du§7: §6" + player.get(p.getName()));
        p.sendMessage(PREFIX + "  §8» §6Croupier§7: §6" + cashier.get(p.getName()));
        Script.addMoney(p, PaymentType.CASH, bet.get(p.getName()) * 2);
        bet.remove(p.getName());
        player.remove(p.getName());
        cashier.remove(p.getName());
        game.remove(p.getName());

    }

    private static void tie(Player p) {
        p.sendMessage(PREFIX + "Das Spiel ist unentschieden geendet und du hast deinen Wetteinsatz zurückerhalten (Tie).");
        p.sendMessage(PREFIX + "  §8» §6Du§7: §6" + player.get(p.getName()));
        p.sendMessage(PREFIX + "  §8» §6Croupier§7: §6" + cashier.get(p.getName()));
        Script.addMoney(p, PaymentType.CASH, bet.get(p.getName()));
        bet.remove(p.getName());
        player.remove(p.getName());
        cashier.remove(p.getName());
        game.remove(p.getName());
    }

    private static void lose(Player p, Boolean bust) {
        p.sendMessage(PREFIX + "Du hast §cverloren§7!");
        if (bust) {
            p.sendMessage(PREFIX + "BUST!");
        } else {
            p.sendMessage(PREFIX + "  §8» §6Du§7: §6" + player.get(p.getName()));
            p.sendMessage(PREFIX + "  §8» §6Croupier§7: §6" + cashier.get(p.getName()));
        }
        bet.remove(p.getName());
        player.remove(p.getName());
        cashier.remove(p.getName());
        game.remove(p.getName());
    }
}