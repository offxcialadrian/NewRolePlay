package de.newrp.Player;

import de.newrp.API.Aktie;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class AktienMarkt implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§6Aktie§8]§6 " + Messages.ARROW + " §7";

    private static final HashMap<String, Boolean> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        p.sendMessage(Messages.ERROR + "Der Aktienmarkt ist derzeit nicht verfügbar.");
        p.sendMessage(Messages.INFO + "Dadurch, dass wir erst beobachten müssen, wie sich die Wirtschaft verhält, wird der Aktienmarkt erst später verfügbar sein.");
        return true;

        /*if (p.getLocation().distance(new Location(Script.WORLD, 942, 77, 941)) < 5) {
            if (!cooldown.containsKey(p.getName())) {
                Aktie.openGUI(p);
            } else
                p.sendMessage(Messages.ERROR + "Die Bank bearbeitet noch deine Anfrage.");
        } else
            p.sendMessage(Messages.ERROR + "Du kannst nur am Schalter der Bank mit Aktien handeln.");
        return false;*/
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        if (e.getView().getTitle().equalsIgnoreCase("§cAktienmarkt")) {
            e.setCancelled(true);
            for (Aktie aktie : Aktie.values()) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§6" + aktie.getName())) {
                    if (e.getClick().equals(ClickType.LEFT)) {
                        if (Script.getMoney(p, PaymentType.BANK) > aktie.getPrice() + getPercentage(10, aktie.getPrice())) {
                            if (aktie.getUsedShares() < aktie.getMaxShares()) {
                                aktie.addAktie(p, 1);
                                p.closeInventory();
                                p.sendMessage(PREFIX + "Du hast dir eine §6" + aktie.getName() + " §7Aktie gekauft!");
                                Script.removeMoney(p, PaymentType.BANK, aktie.getPrice());
                                p.sendMessage(PREFIX + "Du hast eine Bearbeitungsgebühr gezahlt!");
                                Script.removeMoney(p, PaymentType.BANK, getPercentage(10, aktie.getPrice()));
                                p.closeInventory();
                                cooldown.put(p.getName(), true);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        cooldown.remove(p.getName());
                                    }
                                }.runTaskLater(NewRoleplayMain.getInstance(), 20L * 5);
                            } else
                                p.sendMessage(Messages.ERROR + "Es gibt keine §6" + aktie.getName() + " §cAktien!");
                        } else {
                            p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld!");
                            p.sendMessage(Messages.INFO + "Du benötigst " + (aktie.getPrice() + getPercentage(10, aktie.getPrice())) + "€.");
                        }
                    } else if (e.getClick().equals(ClickType.RIGHT)) {
                        if (aktie.getAmountByPlayer(p) < 1) return;
                        aktie.removeAktie(p, 1);
                        p.sendMessage(PREFIX + "Du hast eine §6" + aktie.getName() + "§7 Aktie verkauft!");
                        Script.addMoney(Script.getNRPID(p), PaymentType.BANK, aktie.getPrice());
                        p.sendMessage(PREFIX + "Du hast eine Bearbeitungsgebühr gezahlt!");
                        Script.removeMoney(p, PaymentType.BANK, getPercentage(10, aktie.getPrice()));
                        p.closeInventory();
                        cooldown.put(p.getName(), true);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                cooldown.remove(p.getName());
                            }
                        }.runTaskLater(NewRoleplayMain.getInstance(), 20L * 5);
                    }
                }
            }
        }
    }

    private static int getPercentage(int percentage, int value) {
        int i = value;
        i = value / 100;
        i = i * percentage;
        return i;
    }
}