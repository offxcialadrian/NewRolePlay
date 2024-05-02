package de.newrp.Forum;

import de.newrp.API.Achievement;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ForumCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if (args.length == 1 && args[0].equalsIgnoreCase("delete")) {
            if (Forum.getForumID(Script.getNRPID(p)) == 0) {
                p.sendMessage(Forum.prefix + "§cEs wurde keine Verbindung zum Forum gefunden.");
                return true;
            }

            Script.executeUpdate("DELETE FROM forum WHERE id=" + Script.getNRPID(p));
            p.sendMessage(Forum.prefix + "Deine Verbindung zum Forum wurde gelöscht.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("sync")) {
            if (Forum.getForumID(Script.getNRPID(p)) == 0) {
                p.sendMessage(Messages.ERROR + "Du bist nicht mit dem Forum verbunden.");
                return true;
            }

            Forum.syncPermission(p);
            return true;
        }

        if (Forum.getForumID(Script.getNRPID(p)) != 0) {
            p.sendMessage(Forum.prefix + "§cEs wurde bereits eine Verbindung zum Forum gefunden.");
            return true;
        }

        p.sendMessage(Forum.prefix + "§cEs wurde keine Verbindung zum Forum gefunden. Versuche Verifikation...");
        try (Statement stmt = main.getForumConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT userID, username FROM wcf1_user WHERE username='" + Script.getName(p) + "'")) {
            if (rs.next()) {
                Script.executeUpdate("INSERT INTO forum (id, forumID) VALUES (" + Script.getNRPID(p) + ", " + rs.getInt("userID") + ")");
                p.sendMessage(Forum.prefix + "Dein Account ist nun mit dem Forum Account §c" + rs.getString("username") + "§6 verbunden.");
                Forum.syncPermission(p);
                Achievement.FORUM.grant(p);
            } else {
                p.sendMessage(Forum.prefix + "Es wurde kein Forum Account mit dem Namen §c" + Script.getName(p) + "§6 gefunden.");
                p.sendMessage(Forum.prefix + "Du kannst dir unter https://forum.newrp.de/core/index.php?disclaimer/ einen Account erstellen!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            p.sendMessage(Messages.ERROR + "Verbindung zum Forum fehlgeschlagen. Bitte melde dies als Bug.");
        }
        return true;
    }
}
