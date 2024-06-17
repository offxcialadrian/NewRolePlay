package de.newrp.Police;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.WantedInformation;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Government.Straftat;
import de.newrp.Player.AFK;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Fahndung implements CommandExecutor, TabCompleter {

    public static String PREFIX = "§8[§9Fahndung§8] §9» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length == 0) {
            if (Fahndung.isFahnded(p)) {
                if(!getStraftatIDs(p).isEmpty()) {
                    p.sendMessage(Straftat.PREFIX + "Fahndungen von " + Script.getName(p) + ":");
                    for(int i : getStraftatIDs(p)) {
                        p.sendMessage("§8» §6" + Script.getName(p) + " §8× §6" + Straftat.getWanteds(i) + " WantedPunkte " + " §8× §6" + Straftat.getReason(i).replace("-"," ") + (getCop(Script.getNRPID(p), i) > 0 ? " §8(§7" + Objects.requireNonNull(Script.getOfflinePlayer(getCop(Script.getNRPID(p), i))).getName() + "§8)" : ""));
                    }
                } else {
                    p.sendMessage(Messages.ERROR + "Du wirst nicht gesucht.");
                }
            } else {
                if (Beruf.hasBeruf(p) && (Beruf.getBeruf(p) == Beruf.Berufe.POLICE || Beruf.getBeruf(p) == Beruf.Berufe.BUNDESKRIMINALAMT)) {
                    Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                        List<WantedInformation> wantedInformations = new ArrayList<>();
                        for(Player all : Bukkit.getOnlinePlayers()) {
                            if(SDuty.isSDuty(all)) continue;
                            if(getStraftatIDs(all).isEmpty()) continue;
                            int wanteds = 0;
                            for(int i : getStraftatIDs(all)) {
                                wanteds += Straftat.getWanteds(i);
                            }
                            if(wanteds == 0) continue;
                            wantedInformations.add(new WantedInformation(all, wanteds));
                        }

                        wantedInformations.sort((o1, o2) -> Integer.compare(o2.getWantedPoints(), o1.getWantedPoints()));
                        if(wantedInformations.isEmpty()) {
                            p.sendMessage(Messages.ERROR + "Es gibt keine Fahndungen.");
                            return;
                        }

                        p.sendMessage(Straftat.PREFIX + "Alle Fahndungen:");
                        for (WantedInformation wantedInformation : wantedInformations) {
                            p.sendMessage("§8» §6" + Script.getName(wantedInformation.getPlayer()) + " §8× §6" + wantedInformation.getWantedPoints() + " WantedPunkte" + (AFK.isAFK(wantedInformation.getPlayer()) ? " §8× §6AFK" : ""));
                        }
                    });
                } else {
                    p.sendMessage(Messages.ERROR + "Du wirst nicht gesucht.");
                }
            }
            return true;
        }

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getBeruf(p).equals(Beruf.Berufe.BUNDESKRIMINALAMT)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }


        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein, um jemanden zu fahnden.");
            return true;
        }

        if(args.length == 1) {
            Player tg = Script.getPlayer(args[0]);
            if(tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(!getStraftatIDs(tg).isEmpty()) {
                p.sendMessage(Straftat.PREFIX + "Fahndungen von " + Script.getName(tg) + ":");
                for(int i : getStraftatIDs(tg)) {
                    p.sendMessage("§8» §6" + Script.getName(tg) + " §8× §6" + Straftat.getWanteds(i) + " WantedPunkte " + " §8× §6" + Straftat.getReason(i).replace("-"," ") + (getCop(Script.getNRPID(p), i) > 0 ? " §8(§7" + Objects.requireNonNull(Script.getOfflinePlayer(getCop(Script.getNRPID(p), i))).getName() + "§8)" : ""));
                }
            } else {
                p.sendMessage(Messages.ERROR + "Dieser Spieler wird nicht gefahndet.");
            }

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

        if(Beruf.getBeruf(tg) == Beruf.Berufe.POLICE && Duty.isInDuty(tg)) {
            p.sendMessage(Messages.ERROR + "Du kannst keine Polizisten im Dienst fahnden.");
            return true;
        }

        if(SDuty.isSDuty(tg)) {
            p.sendMessage(Messages.ERROR + "Du kannst keine Spieler im Supporter-Dienst fahnden.");
            return true;
        }

        if(Beruf.getBeruf(tg) == Beruf.Berufe.GOVERNMENT && Duty.isInDuty(tg)) {
            p.sendMessage(Messages.ERROR + "Du kannst keine Regierungsmitglieder fahnden.");
            return true;
        }

        ArrayList<Straftat> straftaten = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(String arg : args) {
            if(arg.equalsIgnoreCase(args[0])) continue;
            if(!Straftat.straftatExists(arg.replace("-"," "))) {
                p.sendMessage(Messages.ERROR + "Die Straftat §e" + arg.replace("-"," ") + " §cexistiert nicht.");
                continue;
            }
            if(getStraftatIDs(tg).contains(Straftat.getReasonID(arg))) {
                p.sendMessage(Messages.ERROR + "Der Spieler wird bereits wegen §e" + arg.replace("-"," ") + " §cgefahndet.");
                continue;
            }
            sb.append(arg).append(" & ");
            Script.executeAsyncUpdate("INSERT INTO wanted (nrp_id, copID, wantedreason, time) VALUES ('" + Script.getNRPID(tg) + "', '" + Script.getNRPID(p) + "', '" + Straftat.getReasonID(arg) + "', '" + System.currentTimeMillis() + "')");
        }

        if(sb.toString().isEmpty()) {
            p.sendMessage(Messages.ERROR + "Du musst mindestens eine Straftat angeben.");
            return true;
        }

        String substring = sb.toString().substring(0, sb.toString().length() - 3);
        Log.NORMAL.write(p, "hat eine Fahdung auf " + Script.getName(tg) + " ausgeschrieben (" + substring + ")");
        Log.NORMAL.write(tg, "hat von " + Script.getName(p) + " eine Fahndung ausgeschrieben bekommen (" + substring + ")");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Spieler §e" + Script.getName(tg) + " §7wird nun wegen §e" + substring + " §7gefahndet.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Beamter: §e" + Script.getName(p) + " §8(§7WantedPunkte: " + Fahndung.getWanteds(tg) + "§8)");
        Beruf.Berufe.BUNDESKRIMINALAMT.sendMessage(PREFIX + "Der Spieler §e" + Script.getName(tg) + " §7wird nun wegen §e" + substring + " §7gefahndet.");
        Beruf.Berufe.BUNDESKRIMINALAMT.sendMessage(PREFIX + "Beamter: §e" + Script.getName(p) + " §8(§7WantedPunkte: " + Fahndung.getWanteds(tg) + "§8)");
        tg.sendMessage(PREFIX + "Du wirst nun wegen §e" + substring + " §7gefahndet.");
        Script.updateFahndungSubtitle(tg);

        return false;
    }

    public static boolean isFahnded(Player p) {
        return Script.getInt(p, "wanted", "id") != 0;
    }

    public static boolean isFahnded(OfflinePlayer p) {
        return Script.getInt(p, "wanted", "id") != 0;
    }

    public static long getFahndedTime(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id ASC LIMIT 1")) {
            if (rs.next()) {
                return (System.currentTimeMillis() - rs.getLong("time"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getWanteds(Player p) {
        int wanteds = 0;
        for(int i : getStraftatIDs(p)) {
            wanteds += Straftat.getWanteds(i);
        }
        return Math.min(wanteds, 100);
    }

    public static int getWanteds(OfflinePlayer p) {
        int wanteds = 0;
        for(int i : getStraftatIDs(p)) {
            wanteds += Straftat.getWanteds(i);
        }
        return Math.min(wanteds, 100);
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

            if (args.length >= 2) {
                StringUtil.copyPartialMatches(args[args.length - 1], oneArgList, completions);
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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

    public static List<Integer> getStraftatIDs(Player p) {
        List<Integer> list = new ArrayList<>();
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted WHERE nrp_id=" + Script.getNRPID(p))) {
            while (rs.next()) {
               list.add(rs.getInt("wantedreason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Integer> getStraftatIDs(OfflinePlayer p) {
        List<Integer> list = new ArrayList<>();
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted WHERE nrp_id=" + Script.getNRPID(p))) {
            while (rs.next()) {
                list.add(rs.getInt("wantedreason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getStraftatID(OfflinePlayer p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("wantedreason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCop(int nrp_id, int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wanted WHERE wantedreason=" + id + " AND nrp_id=" + nrp_id)) {
            if (rs.next()) {
                return rs.getInt("copID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
