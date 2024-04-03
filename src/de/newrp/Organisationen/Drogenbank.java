package de.newrp.Organisationen;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.main;
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

public class Drogenbank implements CommandExecutor, Listener {

    public static HashMap<String, Integer> drug_amount = new HashMap<>();


    public static int getDrogenAmount(Organisation o, Drogen droge, Drogen.DrugPurity purity) {
        try(Statement stmt = main.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT amount FROM drugbank WHERE organisation = '" + o.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
            if(rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getDrogenAmount(Organisation o, Drogen droge) {
        try(Statement stmt = main.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT amount FROM drugbank WHERE organisation = '" + o.getID() + "' AND drug = '" + droge.getID() + "'");
            if(rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void addDrogen(Organisation o, Drogen droge, Drogen.DrugPurity purity, int amount) {
        if(getDrogenAmount(o, droge, purity) == 0) {
            try(Statement stmt = main.getConnection().createStatement()) {
                stmt.executeUpdate("INSERT INTO drugbank (organisation, drug, purity, amount) VALUES ('" + o.getID() + "', '" + droge.getID() + "', '" + purity.getID() + "', '" + amount + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try(Statement stmt = main.getConnection().createStatement()) {
                stmt.executeUpdate("UPDATE drugbank SET amount = amount + " + amount + " WHERE organisation = '" + o.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeDrogen(Organisation o, Drogen droge, Drogen.DrugPurity purity, int amount) {
        if(getDrogenAmount(o, droge, purity) == 0) return;
        try(Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE drugbank SET amount = amount - " + amount + " WHERE organisation = '" + o.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setDrogen(Organisation o, Drogen droge, Drogen.DrugPurity purity, int amount) {
        if(getDrogenAmount(o, droge, purity) == 0) {
            try(Statement stmt = main.getConnection().createStatement()) {
                stmt.executeUpdate("INSERT INTO drugbank (organisation, drug, purity, amount) VALUES ('" + o.getID() + "', '" + droge.getID() + "', '" + purity.getID() + "', '" + amount + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try(Statement stmt = main.getConnection().createStatement()) {
                stmt.executeUpdate("UPDATE drugbank SET amount = " + amount + " WHERE organisation = '" + o.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearDrogen(Organisation o) {
        try(Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM drugbank WHERE organisation = '" + o.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearDrogen(Organisation o, Drogen droge) {
        try(Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM drugbank WHERE organisation = '" + o.getID() + "' AND drug = '" + droge.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearDrogen(Organisation o, Drogen.DrugPurity purity) {
        try(Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM drugbank WHERE organisation = '" + o.getID() + "' AND purity = '" + purity.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearDrogen(Organisation o, Drogen droge, Drogen.DrugPurity purity) {
        try(Statement stmt = main.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM drugbank WHERE organisation = '" + o.getID() + "' AND drug = '" + droge.getID() + "' AND purity = '" + purity.getID() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR  + "Du bist in keiner Organisation.");
            return true;
        }


        Organisation o = Organisation.getOrganisation(p);
        if(p.getLocation().distance(o.getDbank())>10) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe der Drogenbank.");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
            p.sendMessage(Organisation.PREFIX + "Drogenbank:");
            for(Drogen drug : Drogen.values()) {
                StringBuilder sb = new StringBuilder(Organisation.PREFIX + drug.getName());
                for(Drogen.DrugPurity purity : Drogen.DrugPurity.values()) {
                    sb.append("\n§6" + purity.getText() + "§8: §6" + getDrogenAmount(o, drug, purity) + " g");
                }
                p.sendMessage(sb.toString());
            }
        }

        if(args.length == 1 && (args[0].equalsIgnoreCase("put") || args[0].equalsIgnoreCase("add"))) {
            Inventory inv = Bukkit.createInventory(null, 9, "§eDrogenbank");
            p.openInventory(inv);
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("get")) {
            if(Organisation.getRank(p) < 3) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }
            if(!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "/drogenbank get [Gramm]");
                return true;
            }

            int amount = Integer.parseInt(args[1]);
            drug_amount.put(p.getName(), amount);

            Inventory inv = Bukkit.createInventory(null, 9, "§eDrogenbank (Inhalt)");
            for(Drogen droge : Drogen.values()) {
                if(getDrogenAmount(o, droge) == 0) continue;
                inv.addItem(new ItemBuilder(Material.PAPER).setName("§9" + droge.getName()).setLore("§7Menge: " + getDrogenAmount(o, droge) + "g").build());
            }
            p.openInventory(inv);
            return true;
        }

        p.sendMessage(Messages.ERROR + "/drogenbank [add/get] {menge bei get}");


        return false;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(!e.getView().getTitle().equals("§eDrogenbank")) return;
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        for(int i = 0; i < inv.getSize(); i++) {
            if(i > 9) continue;
            if(inv.getItem(i) == null) continue;
            Drogen droge = Drogen.getItemByName(inv.getItem(i).getItemMeta().getDisplayName());
            if(droge == null) continue;
            Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByName(inv.getItem(i).getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", ""));
            int amount = inv.getItem(i).getAmount();
            Drogenbank.addDrogen(Organisation.getOrganisation(p), droge, purity, amount);
            Organisation.getOrganisation(p).sendMessage(Organisation.PREFIX + "Es wurden " + amount + "g " + droge.getName() + " mit dem Reinheitsgrad " + purity.getText() + " von " + Script.getName(p) + " in die Drogenbank gelegt.");
        }
    }

    public static void openGUI(Player p, Drogen drogen) {
        Organisation o = Organisation.getOrganisation(p);
        Inventory inv = Bukkit.createInventory(null, 9, "§eDrogenbank (" + drogen.getName() + ")");
        for(Drogen.DrugPurity purity : Drogen.DrugPurity.values()) {
            if(getDrogenAmount(o, drogen, purity) == 0) continue;
            inv.addItem(new ItemBuilder(drogen.getMaterial()).setName("§9" + drogen.getName()).setLore("§7Reinheitsgrad: " + purity.getText(), "§7Menge: " + getDrogenAmount(o, drogen, purity) + "g").build());
        }
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick1(InventoryClickEvent e) {
        if(! e.getView().getTitle().equalsIgnoreCase("§eDrogenbank (Inhalt)")) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || !e.getCurrentItem().hasItemMeta()) return;
        Drogen droge = Drogen.getItemByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
        if(droge == null) return;
        openGUI(p, droge);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().startsWith("§eDrogenbank (") || e.getView().getTitle().equalsIgnoreCase("§eDrogenbank (Inhalt)")) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || !e.getCurrentItem().hasItemMeta()) return;
        Drogen droge = Drogen.getItemByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
        if(droge == null) return;
        Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByName(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", "")));
        int amount = getDrogenAmount(Organisation.getOrganisation(p), droge, purity);
        if(amount == 0) return;
        p.closeInventory();
        if(drug_amount.containsKey(p.getName())) {
            int amount2 = drug_amount.get(p.getName());
            if(amount2 > amount) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug " + droge.getName() + " mit dem Reinheitsgrad " + purity.getText() + " in der Drogenbank.");
                return;
            }
            removeDrogen(Organisation.getOrganisation(p), droge, purity, amount2);
            p.sendMessage(Organisation.PREFIX + "Du hast " + amount2 + "g " + droge.getName() + " mit dem Reinheitsgrad " + purity.getText() + " aus der Drogenbank genommen.");
            p.getInventory().addItem(new ItemBuilder(droge.getMaterial()).setName(droge.getName()).setLore("§7Reinheitsgrad: " + purity.getText()).setAmount(amount2).build());
            drug_amount.remove(p.getName());
        }
    }

}
