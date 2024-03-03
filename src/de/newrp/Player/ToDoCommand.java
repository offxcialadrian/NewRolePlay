package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.Statement;

public class ToDoCommand implements CommandExecutor {

    public static String PREFIX = "§8[§bToDo§8] §b» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            sendToDos(p);
            return true;
        }

        if(!Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du benötigst Premium, um ToDos zu benutzen.");
            p.sendMessage(Messages.INFO + "Du kannst Premium im Shop unter https://shop.newrp.de erwerben.");
            return true;
        }

        if(args[0].equalsIgnoreCase("add")) {
            if(args.length == 1) {
                p.sendMessage(PREFIX + "/todo add [Text]");
                return true;
            }

            StringBuilder todo = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                todo.append(args[i]).append(" ");
            }

            if(todo.length() > 512) {
                p.sendMessage(PREFIX + "Der ToDo-Text darf maximal 512 Zeichen lang sein.");
                return true;
            }

            try (Statement stmt = main.getConnection().createStatement()) {
                stmt.executeUpdate("INSERT INTO todos (nrp_id, todo) VALUES (" + Script.getNRPID(p) + ", '" + todo.toString() + "')");
                p.sendMessage(PREFIX + "ToDo hinzugefügt.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(args[0].equalsIgnoreCase("remove")) {
            if(args.length == 1) {
                p.sendMessage(PREFIX + "/todo remove [ID]");
                return true;
            }

            if(!Script.isInt(args[1])) {
                p.sendMessage(PREFIX + "Ungültige ID.");
                return true;
            }

            try (Statement stmt = main.getConnection().createStatement()) {
                stmt.executeUpdate("DELETE FROM todos WHERE nrp_id=" + Script.getNRPID(p) + " AND id=" + getDatabaseID(p, Integer.parseInt(args[1])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            p.sendMessage(PREFIX + "/todo [add / remove]");
        }
        return false;

    }

    public static void sendToDos(Player p) {
        int i = 1;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM todos WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id ASC")) {

            if(rs.next()) {
                p.sendMessage(PREFIX + "Deine ToDos:");
                do {
                    p.sendMessage("§8[§b" + i + "§8] §7" + rs.getString("todo"));
                    i++;
                } while (rs.next());
            } else {
                p.sendMessage(PREFIX + "Du hast keine ToDos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int getDatabaseID(Player p, int id) {
        int i = 1;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM todos WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id ASC")) {
            if(rs.next()) {
                do {
                    if(i == id) {
                        return rs.getInt("id");
                    }
                    i++;
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
