package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Treuebonus implements CommandExecutor, Listener {
    public static final HashMap<UUID, Long> logout = new HashMap<>();
    public static final HashMap<String, Integer> time = new HashMap<>();
    public static ArrayList<String> wasDuty = new ArrayList<>();
    public static final String prefix = "§8[§bTreuebonus§8]§b " + Messages.ARROW + " §7";

    public static void addTime() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            addTime(p);
        }
    }

    public static void addTime(Player p) {
        if (!AFK.isAFK(p)) {
            if (!time.containsKey(p.getName())) {
                Treuebonus.time.put(p.getName(), 0);
            }
            int old = time.get(p.getName());
            if (old >= 120) {
                p.sendMessage(prefix + "NRP × New RolePlay dankt dir für deine Treue und schenkt dir einen Treuepunkt! §8[§c" + (getPunkte(p) + 1) + "§8]");
                p.sendMessage(Messages.INFO + "Mit /treuebonus kannst du dir tolle Geschenke aussuchen.");

                add(p, true);
            } else {
                time.put(p.getName(), (time.get(p.getName()) + 1));
            }
        }
    }

    public static int getMinutesToBonus(Player p) {
        if (time.containsKey(p.getName())) {
            return 120 - time.get(p.getName());
        } else {
            Treuebonus.time.put(p.getName(), 0);
            return 120;
        }
    }

    public static int getPunkte(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT punkte FROM treuebonus WHERE id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("punkte");
            } else {
                Script.executeUpdate("INSERT INTO treuebonus (id) VALUES (" + Script.getNRPID(p) + ");");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTotalPunkte(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT total FROM treuebonus WHERE id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("total");
            } else {
                Script.executeUpdate("INSERT INTO treuebonus (id) VALUES (" + Script.getNRPID(p) + ");");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void add(Player p, boolean updateTime) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            int unicaID = Script.getNRPID(p);

            try (PreparedStatement updateStatement = main.getConnection().prepareStatement("UPDATE treuebonus SET punkte=punkte+1, total=total+1 WHERE id=?");) {
                updateStatement.setInt(1, unicaID);
                // check if nothing was updated
                if (updateStatement.executeUpdate() == 0) {
                    // create new entry
                    Script.executeUpdate("INSERT INTO treuebonus(id, punkte, total) VALUES (" + unicaID + ", 1, 1)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if (updateTime) {
            Treuebonus.time.put(p.getName(), 0);
        }
    }

    public static void remove(Player p, int amount) {
        int i = (getPunkte(p) - amount);
        if (i < 0) i = 0;
        Script.executeUpdate("UPDATE treuebonus SET punkte=" + i + " WHERE id=" + Script.getNRPID(p));
        if (!Treuebonus.time.containsKey(p.getName())) Treuebonus.time.put(p.getName(), 0);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (args.length != 0) {
            p.sendMessage(prefix + "Du hast in " + getMinutesToBonus(p) + "min deinen nächsten Treuebonus. §8[§c" + getPunkte(p) + " Punkte§8]");
        } else {
            Inventory inv = p.getServer().createInventory(null, InventoryType.HOPPER, "§bTreuebonus §8[§c" + getPunkte(p) + "§8]");
            int price = 15 * ((Script.getLevel(p) / 5) + 1);
            inv.setItem(0, Script.setNameAndLore(Material.EXPERIENCE_BOTTLE, "§6+1000 Exp", "§c10 Treuepunkte"));
            inv.setItem(1, Script.setNameAndLore(Material.GOLD_INGOT, "§6+2500$", "§c12 Treuepunkte"));
            inv.setItem(2, Script.setNameAndLore(Material.DIAMOND, "§67 Tage Premium", "§c24 Treuepunkte"));
            inv.setItem(3, Script.setNameAndLore(Material.DIAMOND, "§6+1 Level", "§c" + price + " Treuepunkte"));

            p.openInventory(inv);
        }
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (Treuebonus.logout.containsKey(p.getUniqueId())) {
            long logout = Treuebonus.logout.get(p.getUniqueId());
            long offtime = System.currentTimeMillis() - logout;
            int sec = (int) (offtime / 1000);
            if (sec <= 180) {
                if(!Duty.isInDuty(p) && wasDuty.contains(p.getName())) {
                    Duty.setDuty(p);
                    wasDuty.remove(p.getName());
                }
                p.sendMessage(Treuebonus.prefix + "Da du innerhalb von 3 Minuten wieder eingeloggt hast, läuft dein Treuebonus weiter.");
            } else {
                Treuebonus.time.put(p.getName(), 0);
            }
        } else {
            Treuebonus.time.put(p.getName(), 0);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Treuebonus.logout.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        if(Duty.isInDuty(e.getPlayer())) {
            wasDuty.add(e.getPlayer().getName());
            Duty.removeDuty(e.getPlayer());
            Beruf.getBeruf(e.getPlayer()).changeDuty(e.getPlayer(), false);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith("§bTreuebonus")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                ItemStack is = e.getCurrentItem();
                e.setCancelled(true);
                e.getView().close();
                Player p = (Player) e.getWhoClicked();
                is.getItemMeta().getDisplayName();
                switch (is.getItemMeta().getDisplayName()) {
                    case "§6+1000 Exp": {
                        int price = 10;
                        int punkte = Treuebonus.getPunkte(p);
                        if (punkte >= price) {
                            Treuebonus.remove(p, price);
                            p.sendMessage(Treuebonus.prefix + "Du hast +1000 Exp eingelöst.");
                            Script.addEXP(p, 1000);
                        } else {
                            p.sendMessage(Treuebonus.prefix + "Das kostet " + price + " Treuepunkte! (Dir fehlen §9" + (price - punkte) + "§b Punkte)");
                        }
                        break;
                    }
                    case "§6+2500$": {
                        int price = 12;
                        int punkte = Treuebonus.getPunkte(p);
                        if (punkte >= price) {
                            Treuebonus.remove(p, price);
                            p.sendMessage(Treuebonus.prefix + "Du hast 2500$ eingelöst.");
                            Script.addMoney(p, PaymentType.BANK, 2500);
                        } else {
                            p.sendMessage(Treuebonus.prefix + "Das kostet " + price + " Treuepunkte! (Dir fehlen §9" + (price - punkte) + "§b Punkte)");
                        }
                        break;
                    }
                    case "§67 Tage Premium": {
                        int price = 24;
                        int punkte = Treuebonus.getPunkte(p);
                        if (punkte >= price) {
                            Treuebonus.remove(p, price);
                            p.sendMessage(Treuebonus.prefix + "Du hast 7 Tage Premium eingelöst.");
                            Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(7), true);
                        } else {
                            p.sendMessage(Treuebonus.prefix + "Das kostet " + price + " Treuepunkte! (Dir fehlen §9" + (price - punkte) + "§b Punkte)");
                        }
                        break;
                    }
                    case "§6+1 Level": {
                        int price = 15 * ((Script.getLevel(p) / 5) + 1);
                        int punkte = Treuebonus.getPunkte(p);
                        if (punkte >= price) {
                            Treuebonus.remove(p, price);
                            p.sendMessage(Treuebonus.prefix + "Du hast +1 Level eingelöst.");
                            Script.setLevel(p, Script.getLevel(p) + 1, 0);
                            if (!Premium.hasPremium(Script.getNRPID(p))) {
                                Script.setEXP(Script.getNRPID(p), 0);
                            }
                        } else {
                            p.sendMessage(Treuebonus.prefix + "Das kostet " + price + " Treuepunkte! (Dir fehlen §9" + (price - punkte) + "§b Punkte)");
                        }
                        break;
                    }
                }
            }
        }
    }
}
