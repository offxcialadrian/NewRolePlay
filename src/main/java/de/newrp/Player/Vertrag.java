package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Vertrag implements CommandExecutor {
    public static final String PREFIX = "§8[§6Vertrag§8] §6" + Messages.ARROW + " §7";

    public static void saveVertrag(Player from, Player to, String vertrag, boolean accept) {
        try (PreparedStatement statement = Main.getConnection().prepareStatement("INSERT INTO vertrag (userID_from, userID_to, bedingung, accept, time) VALUES (?, ?, ?, ?, ?);")) {
            statement.setInt(1, Script.getNRPID(from));
            statement.setInt(2, Script.getNRPID(to));
            statement.setString(3, vertrag);
            statement.setBoolean(4, accept);
            statement.setLong(5, System.currentTimeMillis());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if(args.length < 2) {
            p.sendMessage(Messages.ERROR + "/vertrag [Spieler] [Bedingungen]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dir selber keinen Vertrag anbieten.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.PLAYER_FAR);
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            if(i > 1) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }

        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " ein Vertrag angeboten.\n" + PREFIX + "§6Bedingungen: " + sb);
        tg.sendMessage(PREFIX + Script.getName(p) + " hat dir ein Vertrag angeboten.\n" + PREFIX + "§6Bedingungen: " + sb);
        Script.sendAcceptMessage(tg);

        Annehmen.offer.put(tg.getName() + ".vertrag.from", p.getName());
        Annehmen.offer.put(tg.getName() + ".vertrag.condition", sb.toString());
        return true;
    }

}
