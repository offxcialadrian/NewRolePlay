package de.newrp.Shop.gym;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Shop.Shops;
import de.newrp.Shop.generic.GenericBuyHandler;
import de.newrp.main;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class GymBuyHandler implements GenericBuyHandler {
    @Override
    public boolean buyItem(Player player, Shops shop, final Object... args) {
        if(isGymMember(player) && Script.getLong(player, "gym", "until") > System.currentTimeMillis()) {
            player.sendMessage(Messages.ERROR + "§cDu bist bereits Mitglied im Fitnessstudio.");
            player.sendMessage(Messages.INFO + "Du kannst dein Abo in " + Script.getRemainingTime(Script.getLong(player, "gym", "until")) + " beenden.");
            return true;
        }

        if(isGymMember(player)) {
            player.sendMessage(Messages.INFO + "Du hast deine Mitgliedschaft im Fitnessstudio beendet.");
            Script.executeUpdate("DELETE FROM gym WHERE nrp_id=" + Script.getNRPID(player));
            return true;
        }

        player.sendMessage(Script.PREFIX + "Du bist nun Mitglied im Fitnessstudio.");
        Script.executeUpdate("INSERT INTO gym (nrp_id, shopID, until) VALUES (" + Script.getNRPID(player) + ", " + shop.getID() + ", " + (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)) + ")");
        return true;
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
