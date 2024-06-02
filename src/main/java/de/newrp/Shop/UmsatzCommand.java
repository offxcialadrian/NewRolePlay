package de.newrp.Shop;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UmsatzCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            long time = TimeUnit.DAYS.toMillis(7);
            if (args.length > 0) {
                try {
                    time = TimeUnit.HOURS.toMillis(Integer.parseInt(args[0]));
                } catch (Exception e) {
                    player.sendMessage(Messages.ERROR + "Ungültige Stundenzahl.");
                    return true;
                }
            }

            Shops shop = Shops.getShopByLocation(player.getLocation());
            if (shop == null) {
                player.sendMessage(Messages.ERROR + "Du befindest dich bei keinem Shop.");
                return true;
            }

            if (shop.getOwner() != Script.getNRPID(player)) {
                if (!SDuty.isSDuty(player) && !Beruf.hasBeruf(player)) {
                    player.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                if (Beruf.hasBeruf(player, Beruf.Berufe.GOVERNMENT)) {
                    if (!Beruf.hasAbteilung(player, Abteilung.Abteilungen.FINANZAMT) && !Beruf.isLeader(player, true)) {
                        player.sendMessage(Messages.NO_PERMISSION);
                        return true;
                    }
                }
            }

            Map<Integer, Integer> buyer = new HashMap<>();
            Map<Integer, Integer> items = new HashMap<>();
            int a = 0;
            int b = 0;
            int n = 0;
            for (List<Integer> buy : getBuys(shop.getID(), time)) {
                if (buyer.containsKey(buy.get(0))) buyer.put(buy.get(0), buyer.get(buy.get(0)) + buy.get(2));
                else buyer.put(buy.get(0), buy.get(2));
                if (items.containsKey(buy.get(1))) items.put(buy.get(1), items.get(buy.get(1)) + buy.get(2));
                else items.put(buy.get(1), buy.get(2));
                a += buy.get(2);
                b += buy.get(3);
                n += buy.get(4);
            }

            player.sendMessage(Shop.PREFIX + "§7Umsatz von §6" + shop.getName() + "§7 in " + TimeUnit.MILLISECONDS.toHours(time) + " Stunden:");
            player.sendMessage("     §8" + Messages.ARROW + " §6Kasse: §7" + shop.getKasse() + "€");
            player.sendMessage("     §8" + Messages.ARROW + " §6Netto: §7" + n + "€ §8(§7" + ((double) Math.round(((float) n / b) * 1000) / 10) + "%§8)");
            player.sendMessage("     §8" + Messages.ARROW + " §6Brutto: §7" + b + "€");
            player.sendMessage("     §8" + Messages.ARROW + " §6Menge: §7" + a + "x");
            player.sendMessage("     §8" + Messages.ARROW + " §6Top-Artikel: §7" + (items.keySet().isEmpty() ? "Keiner" : Objects.requireNonNull(ShopItem.getItem(Collections.max(items.entrySet(), Map.Entry.comparingByValue()).getKey())).getName()));
            player.sendMessage("     §8" + Messages.ARROW + " §6Top-Käufer: §7" + (buyer.keySet().isEmpty() ? "Keiner" : Objects.requireNonNull(Script.getOfflinePlayer(Collections.max(buyer.entrySet(), Map.Entry.comparingByValue()).getKey())).getName()));
        }

        return true;
    }

    public static void addBuy(int shop, int nrp_id, int id, int amount, int brutto, int netto, long time) {
        Script.executeAsyncUpdate("INSERT INTO buys (shop, nrp_id, id, amount, brutto, netto, time) VALUES (" + shop + ", " + nrp_id + ", " + id + ", " + amount + ", " + brutto + ", " + netto + ", " + time + ")");
    }

    public static List<List<Integer>> getBuys(int shop, long time) {
        List<List<Integer>> list = new ArrayList<>();
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nrp_id, id, amount, brutto, netto FROM buys WHERE shop=" + shop + " AND time>" + (System.currentTimeMillis() - time))) {
            while (rs.next()) list.add(Arrays.asList(rs.getInt("nrp_id"), rs.getInt("id"), rs.getInt("amount"), rs.getInt("brutto"), rs.getInt("netto")));
            return list;
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
