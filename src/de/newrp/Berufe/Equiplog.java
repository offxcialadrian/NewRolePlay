package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Equiplog implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 0 || args.length > 2) {
            p.sendMessage(Messages.ERROR + "/equiplog [Stunden]");
            return true;
        }

        if(args.length == 1) {
            if(!Script.isInt(args[0])) {
                p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl an.");
                return true;
            }

            int hours = Integer.parseInt(args[0]);
            p.sendMessage(Equip.PREFIX + "EquipLog des Berufs " + Beruf.getBeruf(p).getName() + " für die letzten " + hours + " Stunden:");
            sendEquiplog(p, hours);
            return true;
        }

        if (!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl an.");
            return true;
        }

        int hours = Integer.parseInt(args[1]);
        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Beruf.hasBeruf(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen Beruf.");
            return true;
        }

        if(Beruf.getBeruf(tg).getID() != Beruf.getBeruf(p).getID()) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler hat nicht den gleichen Beruf wie du.");
            return true;
        }

        p.sendMessage(Equip.PREFIX + "EquipLog von " + Script.getName(tg) + " für die letzten " + hours + " Stunden §8[§7" + getTotalOfPlayer(tg, hours) + "€§8]:");
        sendEquiplog(p, tg, hours);
        return true;

    }

    public static void sendEquiplog(Player p, int hours) {
        HashMap<Integer, Integer> equiplog = new HashMap<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)))) {
            while (rs.next()) {
                int item = rs.getInt("nrp_id");
                int cost = Equip.Stuff.getStuff(rs.getInt("stuffID")).getCost();
                if (equiplog.containsKey(item)) {
                    equiplog.put(item, equiplog.get(item) + cost);
                } else {
                    equiplog.put(item, cost);
                }
            }

            for (int id : equiplog.keySet()) {
                p.sendMessage(Equip.PREFIX + Script.getOfflinePlayer(id).getName() + " §8× §7" + equiplog.get(id) + "€");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEquiplog(Player p, Player tg, int hours) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(tg))) {
            while (rs.next()) {
                p.sendMessage(Equip.PREFIX + Script.getName(tg) + " §8× §7" + Equip.Stuff.getStuff(rs.getInt("stuffID")).getName() + " §8× §7" + Equip.Stuff.getStuff(rs.getInt("stuffID")).getCost() + "€");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getTotalOfPlayer(Player p, int hours) {
        int total = 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM equiplog WHERE beruf=" + Beruf.getBeruf(p).getID() + " AND time>" + (System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours)) + " AND nrp_id=" + Script.getNRPID(p))) {
            while (rs.next()) {
                total += Equip.Stuff.getStuff(rs.getInt("stuffID")).getCost();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public static void addToEquipLog(Player p, Equip.Stuff stuff) {
        Script.executeUpdate("INSERT INTO equiplog (nrp_id, beruf, stuffID, time) VALUES (" + Script.getNRPID(p) + ", " + Beruf.getBeruf(p).getID() + ", " + stuff.getID() + ", " + System.currentTimeMillis() + ");");
    }

}
