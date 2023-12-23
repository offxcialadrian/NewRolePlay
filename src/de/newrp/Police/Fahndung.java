package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Government.Straftat;
import de.newrp.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fahndung implements CommandExecutor, TabCompleter {

    public static String PREFIX = "§8[§9Fahndung§8] §9» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }


        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein, um jemanden zu fahnden.");
            return true;
        }


        if(args.length == 0) {
            p.sendMessage(Straftat.PREFIX + "Alle Fahndungen:");
            for(Player all : getList()) {
                if(SDuty.isSDuty(all)) continue;
                p.sendMessage("§8» §6" + Script.getName(all) + " §8» §6" + Straftat.getReason(getStraftatID(all)));
            }
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/fahndung [Spieler] [Grund]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }


        if(tg == p) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst fahnden.");
            return true;
        }

        if(isFahnded(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler wird bereits gefahndet.");
            return true;
        }

        if(Beruf.getBeruf(tg) == Beruf.Berufe.POLICE && Duty.isInDuty(tg)) {
            p.sendMessage(Messages.ERROR + "Du kannst keine Polizisten im Dienst fahnden.");
            return true;
        }

        if(!Straftat.straftatExists(args[1])) {
            p.sendMessage(Messages.ERROR + "Dieser Fahndungsgrund existiert nicht.");
            return true;
        }

        int id = Straftat.getReasonID(args[1]);
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Spieler §e" + Script.getName(tg) + " §7wird nun wegen §e" + args[1].replace("-"," ") + " §7gefahndet.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Beamter: §e" + Script.getName(p) + " §8(§7WantedPunkte: " + Straftat.getWanteds(id) + "§8)");
        tg.sendMessage(PREFIX + "Du wirst nun wegen §e" + args[1].replace("-"," ") + " §7gefahndet.");
        Script.executeAsyncUpdate("INSERT INTO wanted (nrp_id, copID, wantedreason, time) VALUES ('" + Script.getNRPID(tg) + "', '" + Script.getNRPID(p) + "', '" + id + "', '" + System.currentTimeMillis() + "')");

        return false;
    }

    public static boolean isFahnded(Player p) {
        return Script.getInt(p, "wanted", "id") != 0;
    }

    public static long getFahndedTime(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return (System.currentTimeMillis() - rs.getLong("time"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("fahndung") || cmd.getName().equalsIgnoreCase("wanted")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (String reason : Straftat.getAllReason()) {
                oneArgList.add(reason.replace(" ", "-"));
            }

            if (args.length == 1) {
                return null;
            }

            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], oneArgList, completions);
            }
            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

    public static void removeFahndung(Player p) {
        Script.executeAsyncUpdate("DELETE FROM wanted WHERE nrp_id = '" + Script.getNRPID(p) + "'");
    }

    public static List<Player> getList() {
        List<Player> list = new ArrayList<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted")) {
            if (rs.next()) {
                do {
                    list.add(Script.getPlayer(rs.getInt("nrp_id")));
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getStraftatID(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("wantedreason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
