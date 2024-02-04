package de.newrp.Player;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Banken implements CommandExecutor, Listener {


    private static final String PREFIX = "§8[§bBank§8] §b» §7";
    private static final Location LOCATION = new Location(Script.WORLD, 949, 77, 934);

    public enum Bank {
        BANK1(1, "Spaßkasse", 1000, 1000, 1000, 25, 0.00006),
        BANK2(2, "Deutsche Zentralbank", 1000, 1000, 1000, 50, 0.00005),
        BANK3(3, "Bank3", 1000, 1000, 1000, 30, 0.00003);

        private final int id;
        private final String name;
        private final int einrichtigungsKosten;
        private final int transactionKosten;
        private final int transactionLimit;
        private final int kontoKosten;
        private final double interest;

        Bank(int id, String name, int einrichtigungsKosten, int transactionKosten, int transactionLimit, int kontoKosten, double interest) {
            this.id = id;
            this.name = name;
            this.einrichtigungsKosten = einrichtigungsKosten;
            this.transactionKosten = transactionKosten;
            this.transactionLimit = transactionLimit;
            this.kontoKosten = kontoKosten;
            this.interest = interest;
        }

        public int getID() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getEinrichtigungsKosten() {
            return einrichtigungsKosten;
        }

        public int getTransactionKosten() {
            return transactionKosten;
        }

        public int getTransactionLimit() {
            return transactionLimit;
        }

        public int getKontoKosten() {
            return kontoKosten;
        }

        public double getInterest() {
            return interest;
        }

        public String getInterestString() {
            return String.valueOf(interest).replace(".", ",");
        }

        public static Bank getBankByID(int id) {
            for (Bank b : Bank.values()) {
                if (b.getID() == id) {
                    return b;
                }
            }
            return null;
        }

        public static Bank getBankByPlayer(Player p) {
            try (PreparedStatement preparedStatement = main.getConnection().prepareStatement("SELECT * FROM banks WHERE nrp_id=" + Script.getNRPID(p))) {
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return Bank.getBankByID(rs.getInt("bank_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static Bank getBankByName(String name) {
            for (Bank b : Bank.values()) {
                if (b.getName().equals(name)) {
                    return b;
                }
            }
            return null;
        }


    }

    public static boolean hasBank(Player p) {
        try (PreparedStatement preparedStatement = main.getConnection().prepareStatement("SELECT * FROM banks WHERE nrp_id=" + Script.getNRPID(p))) {
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean hasBank(OfflinePlayer p) {
        try (PreparedStatement preparedStatement = main.getConnection().prepareStatement("SELECT * FROM banks WHERE nrp_id=" + Script.getNRPID(p))) {
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }




    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (p.getLocation().distance(LOCATION) > 10) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe einer Bank.");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§aBanken");
        int i = 0;
        for (Bank bank : Bank.values()) {
            inv.setItem(i, new ItemBuilder(Material.CHEST).setName("§9" + bank.getName()).setLore(" §7 " + Messages.ARROW + " Einrichtungsgebühr: §e" + bank.getEinrichtigungsKosten() + "€",
                    " §7 " + Messages.ARROW + " Transktionsgebühr: §e" + bank.getTransactionKosten() + "€",
                    " §7 " + Messages.ARROW + " Transaktionslimit: §e" + bank.getTransactionLimit() + "€",
                    " §7 " + Messages.ARROW + " Kontoführungsgebühr: §e" + bank.getKontoKosten() + "€",
                    " §7 " + Messages.ARROW + " Zinsen: §e" + bank.getInterestString() + "%").build());
            i++;
        }
        p.openInventory(inv);
        return false;
    }

    public static Bank getBankByPlayer(Player p) {
        try (PreparedStatement preparedStatement = main.getConnection().prepareStatement("SELECT * FROM banks WHERE nrp_id=" + Script.getNRPID(p))) {
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Bank.getBankByID(rs.getInt("bank_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bank getBankByPlayer(OfflinePlayer p) {
        try (PreparedStatement preparedStatement = main.getConnection().prepareStatement("SELECT * FROM banks WHERE nrp_id=" + Script.getNRPID(p))) {
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Bank.getBankByID(rs.getInt("bank_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("§aBanken")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || !e.getCurrentItem().hasItemMeta())
                return;
            e.getView().close();
            Bank bank = Bank.getBankByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
            if (bank == null) return;
            if (Bank.getBankByPlayer(p) == bank) return;

            if (Script.getMoney(p, PaymentType.CASH) < bank.getEinrichtigungsKosten()) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld dabei um die Einrichtungsgebühr zu decken.");
                return;
            }

            if (hasBank(p)) {
                Script.executeUpdate("UPDATE banks SET bank_id=" + bank.getID() + " WHERE nrp_id=" + Script.getNRPID(p));
            } else {
                Script.executeUpdate("INSERT INTO banks (nrp_id, bank_id) VALUES (" + Script.getNRPID(p) + ", " + bank.getID() + ")");
                Script.executeUpdate("UPDATE money SET bank = 0 WHERE nrp_id=" + Script.getNRPID(p));
            }

            p.sendMessage(PREFIX + "Du hast die " + bank.getName() + " als deine Bank ausgewählt.");
            p.sendMessage(Messages.INFO + "Um deine Bank zu wechseln, nutze /bank change");
            Script.removeMoney(p, PaymentType.CASH, bank.getEinrichtigungsKosten());
        }
    }
}

