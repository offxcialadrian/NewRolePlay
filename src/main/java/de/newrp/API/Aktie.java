package de.newrp.API;

import de.newrp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public enum Aktie {

    AEKI(0, "AEKI", 1000),
    CAFE_AM_X3(1, "Cafe am X3", 1000),
    CAFE_STADTHALLE(2, "Cafe Stadthalle", 1000),
    SUPERMARKT_STADTHALLE(3, "Supermarkt Stadthalle", 1000),
    AUTOHAENDLER_STADTHALLE(4, "Autohändler Stadthalle", 1000),
    APOTHEKE_STADTHALLE(5, "Apotheke Stadthalle", 1000),
    ELEKTROLADEN(6, "Elektroladen", 1000);

    int id;
    String name;
    int maxshares;

    Aktie(int id, String name, int maxshares) {
        this.id = id;
        this.name = name;
        this.maxshares = maxshares;
    }

    private static int mincap = 1;
    private static int maxcap = 1;

    private static boolean skipcalculation = false;

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getMaxShares() {
        return this.maxshares;
    }

    public int getPrice() {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM shares WHERE id = " + this.id)) {
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public void addAktie(Player p, int amount) {
        Cashflow.addEntry(p, -(amount*this.getPrice()), "Kauf von " + amount + "x " + this.getName() + "-Aktie");
        int id = Script.getNRPID(p);
        long time = System.currentTimeMillis();
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM player_shares WHERE id = " + id + " AND aktien_id = " + this.id)) {
            Script.executeUpdate("INSERT INTO player_shares_log (id, aktien_id, date, amount, price, bought) VALUES (" + Script.getNRPID(p) + ", " + this.id + ", " + time + ", " + amount + ", " + getPrice() + ", 1);");
            if (rs.next()) {
                if (rs.getInt("amount") > 0) {
                    Script.executeUpdate("UPDATE player_shares SET amount = " + (getAmountByPlayer(p) + amount) + " WHERE id = " + id + " AND aktien_id = " + this.id);
                    Script.executeUpdate("UPDATE shares SET amount = " + (getMaxShares() - getUsedShares() - amount) + " WHERE id = " + this.id);
                }
            } else {
                Script.executeUpdate("INSERT INTO player_shares (id, aktien_id, amount) VALUES (" + Script.getNRPID(p) + ", " + this.id + ", " + amount + ");");
                Script.executeUpdate("UPDATE shares SET amount = " + (getMaxShares() - getUsedShares() - amount) + " WHERE id = " + this.id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void removeAktie(Player p, int amount) {
        Cashflow.addEntry(p, (amount*this.getPrice()), "Verkauf von " + amount + "x " + this.getName() + "-Aktie");
        long time = System.currentTimeMillis();
        int id = Script.getNRPID(p);
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM player_shares WHERE id = " + id + " AND aktien_id = " + this.id)) {
            Script.executeUpdate("INSERT INTO player_shares_log (id, aktien_id, date, amount, price, bought) VALUES (" + Script.getNRPID(p) + ", " + this.id + ", " + time + ", " + amount + ", " + getPrice() + ", 0);");
            if (rs.next()) {
                if (rs.getInt("amount") <= amount) {
                    Script.executeUpdate("DELETE FROM player_shares WHERE id = " + id + " AND aktien_id = " + this.id);
                } else {
                    Script.executeUpdate("UPDATE player_shares SET amount = " + (getAmountByPlayer(p) - amount) + " WHERE id = " + id + " AND aktien_id = " + this.id);
                    Script.executeUpdate("UPDATE shares SET amount = " + (getMaxShares() - getUsedShares() + amount) + " WHERE id = " + this.id);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getAmountByPlayer(Player p) {
        int id = Script.getNRPID(p);
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM player_shares WHERE id = " + id + " AND aktien_id = " + this.id)) {
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getBoughtAmount(long hours) {
        long time = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours);
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `player_shares_log` WHERE aktien_id=" + this.id + " AND bought=1 AND date >" + time + ";")) {
            if (rs.next()) {
                return rs.getInt("COUNT(*)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoldAmount(long hours) {
        long time = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours);
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `player_shares_log` WHERE aktien_id=" + this.id + " AND bought=0 AND date >" + time + ";")) {
            if (rs.next()) {
                return rs.getInt("COUNT(*)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Aktie getAktieByName(String name) {
        for (Aktie aktie : Aktie.values()) {
            if (aktie.getName().equalsIgnoreCase(name)) return aktie;
        }
        return null;
    }

    public static Aktie getAktieByID(int id) {
        for (Aktie aktie : Aktie.values()) {
            if (aktie.getID() == id) return aktie;
        }
        return null;
    }

    public void setPrice(double price) {
        int i = (int) price;
        long time = System.currentTimeMillis();
        Script.executeUpdate("UPDATE shares SET price = " + i + " WHERE id = " + this.id);
        Script.executeUpdate("INSERT INTO shares_log (id, aktien_id, price, date) VALUES (NULL, " + this.id + ", " + getPrice() + ", " + time + ");");
    }

    public int getUsedShares() {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT amount FROM shares WHERE id = " + this.id)) {
            if (rs.next()) {
                return (getMaxShares() - rs.getInt("amount"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int freeShares() {
        int i = (getMaxShares() - getUsedShares());
        return i;
    }

    public static Boolean playerHasShare(Aktie aktie, Player p) {
        int i = 0;
        int id = Script.getNRPID(p);
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM player_shares WHERE id = " + id + " AND aktien_id = " + aktie.getID())) {
            if (rs.next()) {
                if (rs.getInt("amount") > 0) return true;
                return false;
            } else
                return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static Boolean sharesAvailable(Aktie aktie) {
        return (aktie.getMaxShares() - aktie.getUsedShares()) > 0;
    }

    public int getHistoryPriceDays(long days) {
        long time = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days);
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM shares_log WHERE date >" + time + " AND aktien_id=" + this.id + " ORDER BY date asc LIMIT 1;")) {
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getHistoryPriceHours(long hours) {
        long time = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours);
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM shares_log WHERE date >" + time + " AND aktien_id=" + this.id + " ORDER BY date asc LIMIT 1;")) {
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getLastBuyPrice(Player p) {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT price FROM player_shares_log WHERE aktien_id=" + this.id + " AND id=" + Script.getNRPID(p) + " AND bought=1 ORDER BY date desc LIMIT 1;")) {
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int calcHistoryDays(long days) {
        return calcDiffercence(getHistoryPriceDays(days), this.getPrice());
    }

    public int calcHistoryHours(long hours) {
        return calcDiffercence(getHistoryPriceHours(hours), this.getPrice());
    }

    public static void openGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 18, "§cAktienmarkt");
        int i = 0;
        for (Aktie aktie : Aktie.values()) {
            ItemStack is = new ItemStack(Material.CHEST);
            ItemMeta meta = is.getItemMeta();
            meta.setDisplayName("§6" + aktie.getName());
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§8» §6Preis: " + aktie.getPrice() + "€");
            lore.add("§8» §6Im Besitz: " + aktie.getAmountByPlayer(p) + " Aktien");
            lore.add("§8» §6Verfügbar: " + aktie.freeShares() + " Aktien");
            lore.add("§8» §6Letzte 7 Tage: " + ((aktie.calcHistoryDays(7) > 0) ? "§a" : "§c") + aktie.calcHistoryDays(7) + "%");
            lore.add("§8» §6Letzte Stunde: " + ((aktie.calcHistoryHours(1) > 0) ? "§a" : "§c") + aktie.calcHistoryHours(1) + "%");
            if (playerHasShare(aktie, p))
                lore.add("§8» §6Letzter Kauf: " + (aktie.getPrice() >= aktie.getLastBuyPrice(p) ? "§a" : "§c") + aktie.getLastBuyPrice(p) + "€");
            if (sharesAvailable(aktie))
                lore.add("§8» §6Drücke Linksklick zum Kaufen");
            if (playerHasShare(aktie, p))
                lore.add("§8» §6Drücke Rechtsklick zum Verkaufen");
            meta.setLore(lore);
            is.setItemMeta(meta);
            inv.setItem(i, is);
            i++;
        }
        p.openInventory(inv);
    }

    private int calcChange() {
        if (skipcalculation)
            return 0;
        int bought = getBoughtAmount(1);
        int sold = getSoldAmount(1);
        int i = 0;
        i = (calcDiffercence(bought, sold));

        return i;
    }

    public static void update() {
        for (Aktie aktie : Aktie.values()) {
            Debug.debug("Aktie " + aktie.getName() + " changed from " + aktie.getPrice() + "€ to " + (aktie.getPrice() + getPercent(aktie.calcChange(), aktie.getPrice())) + "€ (+" + aktie.calcChange() + "% | Diff. " + calcDiffercence(aktie.getBoughtAmount(1),aktie.getSoldAmount(1)) + ")");
            if (aktie.calcChange() <= maxcap && aktie.calcChange() >= mincap) {
                aktie.setPrice(aktie.getPrice() + getPercent(aktie.calcChange(), aktie.getPrice()));
            } else {
                if (aktie.calcChange() > 0) {
                    aktie.setPrice(aktie.getPrice() + getPercent(maxcap, aktie.getPrice()));
                } else {
                    aktie.setPrice(aktie.getPrice() + getPercent(mincap, aktie.getPrice()));
                }
            }
        }
    }

    private static int calcDiffercence(double oldint, double newint) {
        double result;
        result = ((newint - oldint) / oldint) * 100;
        return (int) result;

    }

    public static int getPercent(double percent, double total) {
        if (total == 0) return 0;
        double d = total / 100;
        d = d * percent;
        int i = (int) d;
        return i;
    }
}