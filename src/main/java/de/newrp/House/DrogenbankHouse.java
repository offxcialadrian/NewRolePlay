package de.newrp.House;

import de.newrp.API.Debug;
import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Organisationen.Drogen;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DrogenbankHouse implements CommandExecutor, Listener {

    public static HashMap<String, Integer> drug_amount = new HashMap<>();
    public static HashMap<String, House> h = new HashMap<>();
    public static String PREFIX = "§8[§cDrogenbank§8] §c» §7";


    public static int getDrogenAmount(House h, Drogen droge, Drogen.DrugPurity purity) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT amount FROM drugbank_house WHERE house = '" + h.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.debug("SQLException -> " + e.getMessage());
        }
        return 0;
    }

    public static int getDrogenAmount(House h, Drogen droge) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT amount FROM drugbank_house WHERE house = '" + h.getID() + "' AND drug = '" + droge.getID() + "'");
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void addDrogen(House h, Drogen droge, Drogen.DrugPurity purity, int amount) {
        if (getDrogenAmount(h, droge, purity) == 0) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
                stmt.executeUpdate("INSERT INTO drugbank_house (house, drug, purity, amount) VALUES ('" + h.getID() + "', '" + droge.getID() + "', '" + purity.getID() + "', '" + amount + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
                stmt.executeUpdate("UPDATE drugbank_house SET amount = amount + " + amount + " WHERE house = '" + h.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeDrogen(House h, Drogen droge, Drogen.DrugPurity purity, int amount) {
        if (getDrogenAmount(h, droge, purity) == 0) return;
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE drugbank_house SET amount = amount - " + amount + " WHERE house = '" + h.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setDrogen(House h, Drogen droge, Drogen.DrugPurity purity, int amount) {
        if (getDrogenAmount(h, droge, purity) == 0) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
                stmt.executeUpdate("INSERT INTO drugbank_house (house, drug, purity, amount) VALUES ('" + h.getID() + "', '" + droge.getID() + "', '" + purity.getID() + "', '" + amount + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
                stmt.executeUpdate("UPDATE drugbank_house SET amount = " + amount + " WHERE house = '" + h.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearDrogen(House h) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM drugbank_house WHERE house = '" + h.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearDrogen(House h, Drogen droge) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM drugbank_house WHERE house = '" + h.getID() + "' AND drug = '" + droge.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearDrogen(House h, Drogen.DrugPurity purity) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM drugbank_house WHERE house = '" + h.getID() + "' AND purity = '" + purity.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearDrogen(House h, Drogen droge, Drogen.DrugPurity purity) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM drugbank_house WHERE house = '" + h.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        House house = House.getInsideHouse(p);
        if (house == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich in keinem Haus.");
            return true;
        }

        if (!House.getHouses(Script.getNRPID(p)).contains(house)) {
            p.sendMessage(Messages.ERROR + "Du wohnst nicht in diesem Haus.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            p.sendMessage(PREFIX + "Drogenbank:");
            for (Drogen drug : Drogen.values()) {
                StringBuilder sb = new StringBuilder(PREFIX + drug.getName() + ":");
                for (Drogen.DrugPurity purity : Drogen.DrugPurity.values()) {
                    sb.append("\n§6" + purity.getText() + "§8: §6" + getDrogenAmount(house, drug, purity) + drug.getSuffix());
                }
                p.sendMessage(sb.toString());
            }
            return true;
        }

        if (args.length == 1 && (args[0].equalsIgnoreCase("put") || args[0].equalsIgnoreCase("add"))) {
            Inventory inv = Bukkit.createInventory(null, 9, "§cDrogenbank");
            p.openInventory(inv);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("get")) {
            if (!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "/drogenbank get [Gramm]");
                return true;
            }

            int amount = Integer.parseInt(args[1]);
            drug_amount.put(p.getName(), amount);
            h.put(p.getName(), house);

            Inventory inv = Bukkit.createInventory(null, 9, "§cDrogenbank (Inhalt)");
            for (Drogen droge : Drogen.values()) {
                if (getDrogenAmount(house, droge) == 0) continue;
                inv.addItem(new ItemBuilder(Material.PAPER).setName("§9" + droge.getName()).setLore("§7Menge: " + getDrogenAmount(house, droge) + droge.getSuffix()).build());
            }
            p.openInventory(inv);
            return true;
        }

        p.sendMessage(Messages.ERROR + "/drogenbank [add/get] {menge bei get}");


        return false;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals("§cDrogenbank")) return;
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            if (i > 9) continue;
            if (inv.getItem(i) == null) continue;
            Drogen droge = Drogen.getItemByName(inv.getItem(i).getItemMeta().getDisplayName());
            if (droge == null) continue;
            Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByName(inv.getItem(i).getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", ""));
            int amount = inv.getItem(i).getAmount();
            addDrogen(h.get(p.getName()), droge, purity, amount);
            p.sendMessage(PREFIX + "Du hast " + amount + "g " + droge.getName() + " mit dem Reinheitsgrad " + purity.getText() + " in die Drogenbank gelegt.");
        }
    }

    public static void openGUI(Player p, Drogen drogen) {
        Inventory inv = Bukkit.createInventory(null, 9, "§cDrogenbank (" + drogen.getName() + ")");
        for (Drogen.DrugPurity purity : Drogen.DrugPurity.values()) {
            if (getDrogenAmount(h.get(p.getName()), drogen, purity) == 0) continue;
            inv.addItem(new ItemBuilder(drogen.getMaterial()).setName("§9" + drogen.getName()).setLore("§7Reinheitsgrad: " + purity.getText(), "§7Menge: " + getDrogenAmount(h.get(p.getName()), drogen, purity) + "g").build());
        }
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick1(InventoryClickEvent e) {
        if (!e.getView().getTitle().equalsIgnoreCase("§cDrogenbank (Inhalt)")) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || !e.getCurrentItem().hasItemMeta())
            return;
        Drogen droge = Drogen.getItemByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
        if (droge == null) return;
        openGUI(p, droge);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith("§cDrogenbank (") || e.getView().getTitle().equalsIgnoreCase("§cDrogenbank (Inhalt)"))
            return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || !e.getCurrentItem().hasItemMeta())
            return;
        Drogen droge = Drogen.getItemByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
        if (droge == null) return;
        Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", "")));
        int amount = getDrogenAmount(h.get(p.getName()), droge, purity);
        if (amount == 0) return;
        p.closeInventory();
        if (drug_amount.containsKey(p.getName())) {
            int amount2 = drug_amount.get(p.getName());
            if (amount2 > amount) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug " + droge.getName() + " mit dem Reinheitsgrad " + purity.getText() + " in der Drogenbank.");
                return;
            }
            removeDrogen(h.get(p.getName()), droge, purity, amount2);
            p.sendMessage(PREFIX + "Du hast " + amount2 + "g " + droge.getName() + " mit dem Reinheitsgrad " + purity.getText() + " aus der Drogenbank genommen.");
            p.getInventory().addItem(new ItemBuilder(droge.getMaterial()).setName(droge.getName()).setLore("§7Reinheitsgrad: " + purity.getText()).setAmount(amount2).build());
            drug_amount.remove(p.getName());
        }
    }
}
