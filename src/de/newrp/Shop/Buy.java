package de.newrp.Shop;

import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Police.Fahndung;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Buy implements CommandExecutor {

    public static final HashMap<String, Shops> current = new HashMap<>();
    public static final HashMap<String, Integer> amount = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        Location ploc = p.getLocation();
        Shops s = null;
        for (Shops shop : Shops.values()) {
            Location l = shop.getBuyLocation();
            if (l != null) {
                if (ploc.distance(l) < 4) {
                    s = shop;
                    break;
                }
            }
        }

        if (s == null) {
            p.sendMessage(Messages.ERROR + "§cDu bist nicht in der Nähe von einem Shop.");
            return true;
        }

        if(s.getType() == ShopType.GUNSHOP && Fahndung.isFahnded(p)) {
            p.sendMessage(Messages.ERROR + "§cDu kannst keine Waffen kaufen, da du gesucht wirst.");
            return true;
        }

        if(s.getType() == ShopType.GYM) {
            if(isGymMember(p) && Script.getLong(p, "gym", "until") > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "§cDu bist bereits Mitglied im Fitnessstudio.");
                p.sendMessage(Messages.INFO + "Du kannst dein Abo in " + Script.getRemainingTime(Script.getLong(p, "gym", "until")) + " beenden.");
                return true;
            }

            if(isGymMember(p)) {
                p.sendMessage(Messages.INFO + "Du hast deine Mitgliedschaft im Fitnessstudio beendet.");
                Script.executeUpdate("DELETE FROM gym WHERE nrp_id=" + Script.getNRPID(p));
                return true;
            }

            p.sendMessage(Script.PREFIX + "Du bist nun Mitglied im Fitnessstudio.");
            Script.executeUpdate("INSERT INTO gym (nrp_id, shopID, until) VALUES (" + Script.getNRPID(p) + ", " + s.getID() + ", " + (System.currentTimeMillis() + 1000L * 60 * 60 * 24) + ")");
            return true;
        }

        current.put(p.getName(), s);
        HashMap<Integer, ItemStack> c = s.getItems();
        int size = (c.size() > 9 ? 3 : 2) * 9;
        Inventory inv = p.getServer().createInventory(null, size, "§6" + s.getPublicName());
        int i = 0;

        for (Map.Entry<Integer, ItemStack> n : c.entrySet()) {
            ItemStack is = n.getValue();
            if (is == null) {
                continue;
            }
            inv.setItem(i++, is);
        }

        if(i == 0) {
            p.sendMessage(Messages.ERROR + "Dieser Shop bietet derzeit nichts an.");
            return true;
        }

        inv.setItem(((size / 9) <= 2 ? 13 : 22), Script.setName(Material.BARRIER, "§cSchließen"));
        p.openInventory(inv);

        if(args.length == 1) {
            if(!Script.isInt(args[0])) {
                p.sendMessage(Messages.ERROR + "§cBitte gib eine gültige Zahl an.");
                return true;
            }

            int a = Integer.parseInt(args[0]);
            if(a < 1) {
                p.sendMessage(Messages.ERROR + "§cBitte gib eine gültige Zahl an.");
                return true;
            }

            amount.put(p.getName(), a);
            p.sendMessage(Messages.INFO + "Du kaufst nun " + a + "x.");
        }

        return false;
    }

    public static boolean isGymMember(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM gym WHERE nrp_id=" + Script.getNRPID(p))  ) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Shops getGym(Player p) {
        return Shops.getShop(Script.getInt(p, "gym", "shopID"));
    }

}
