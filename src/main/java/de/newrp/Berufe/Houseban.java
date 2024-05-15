package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Houseban implements CommandExecutor, Listener, TabCompleter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static String PREFIX = "§8[§6Hausverbot§8] §7»§7 ";

    enum Reasons {
        LEICHENBEWACHUNG(1, "Leichenbewachung", new String[]{"leichenbewachung", "leichen", "leiche", "lb"}, 7),
        GEWALTANWENDUNG(2, "Gewaltanwendung", new String[]{"gewaltanwendung", "gewalt", "ga"}, 7),
        VANDALISMUS(3, "Vandalismus", new String[]{"vandalismus", "vanda", "va"}, 3),
        BELÄSTIGUNG(4, "Belästigung", new String[]{"belästigung", "bä"}, 3),
        UNANGEMESSENES_VERHALTEN(5, "Unangemessenes Verhalten", new String[]{"unangemessenes", "uv"}, 3),
        BELEIDIGUNGEN(6, "Beleidigungen", new String[]{"beleidigungen", "beleidigung", "be"}, 3),
        RESPEKTLOSES_VERHALTEN(7, "Respektloses Verhalten", new String[]{"respektloses", "respektlos", "respekt", "rv"}, 3),
        NICHT_BEFOLGEN_VON_ANWEISUNGEN(8, "Missachen von Anweisungen", new String[]{"nichtbefolgen", "missachten", "anweisungen", "befolgen", "na"}, 3),
        STÖREN_EINES_EVENTS(9, "Stören eines Events", new String[]{"stören", "event", "se"}, 3),
        DROHUNGEN(10, "Drohungen", new String[]{"drohungen", "drohung", "droh", "dr"}, 7),
        PROVOKANTES_VERHALTEN(11, "Provokantes Verhalten", new String[]{"provokation", "provokant", "provo", "pv"}, 3),
        GEWALTTÄTIGES_VERHALTEN(12, "Gewalttätiges Verhalten", new String[]{"gewalttätig", "gewaltverhalten", "verhalten", "gv"}, 7),
        PACKEN_EINES_MITGLIEDS(13, "Packen eines Mitglieds", new String[]{"packen", "pm"}, 14),
        SCHIESSEN_AUF_DEM_GELÄNDE(14, "Schießen auf dem Gelände", new String[]{"schiessen", "sg"}, 7),
        ERPRESSEN_VON_REZEPTEN(15, "Erpressen von Rezepten", new String[]{"Rezepte", "Rezept", "er"}, 30),
        ERPRESSEN_VON_DIENSTLEISTUNGEN(16, "Erpressen von Dienstleistungen", new String[]{"dienstleistungen", "dienstleistung", "dienst", "ed"}, 42),
        TÖTEN_EINER_PERSON(17, "Töten einer Person", new String[]{"eineperson", "einer", "ep"}, 14),
        TÖTEN_MEHRERER_PERSONEN(18, "Töten mehrerer Personen", new String[]{"mehrerepersonen", "mehrere", "mp"}, 21);

        private final int id;
        private final String name;
        private final String[] alt_names;
        private final int duration;

        Reasons(int id, String name, String[] alt_names, int duration) {
            this.id = id;
            this.name = name;
            this.alt_names = alt_names;
            this.duration = duration;
        }

        public int getID() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String[] getAltNames() {
            return alt_names;
        }

        public int getDuration() {
            return duration;
        }

        public String getTabName() {
            return name.replace(" ", "-");
        }

        public static Reasons getReason(String s) {
            for (Reasons r : Reasons.values()) {
                if (r.getName().equalsIgnoreCase(s)) return r;
                for (String s1 : r.getAltNames()) {
                    if (s1.equalsIgnoreCase(s)) return r;
                }
            }
            return null;
        }


        public static Reasons getReasonByID(int id) {
            for (Reasons r : Reasons.values()) {
                if (r.getID() == id) return r;
            }
            return null;
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        Beruf.Berufe b = Beruf.getBeruf(p);

        if (args.length == 0) {
            p.sendMessage("§8===§6 Hausverbote §8===");
            StringBuilder sb = new StringBuilder();
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!isHousebanned(all, b)) continue;
                sb.append("§7»§6 ").append(all.getName()).append(" §8×§6 ").append(getReason(all, b)).append(" §8×§6 ").append(DATE_FORMAT.format(new Date(getTime(all, b)))).append(" Uhr\n");
            }
            p.sendMessage(sb.toString());
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
            StringBuilder sb = new StringBuilder();
            p.sendMessage("§8===§6 Hausverbote §8===");
            for (int unicacid : getHousebannedNRPIDs(b)) {
                OfflinePlayer banned = Script.getOfflinePlayer(unicacid);
                sb.append("\n  §7»§6 ").append(Script.getNameInDB(banned)).append(" §8×§6 ").append(getReason(banned, b)).append(" §8×§6 ").append(DATE_FORMAT.format(new Date(getTime(banned, b))));
            }
            p.sendMessage(sb.toString());
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            OfflinePlayer tg = Script.getOfflinePlayer(args[1]);
            Reasons reason = Reasons.getReason(args[2].replace("-", " "));
            if (tg == null) {
                p.sendMessage(Messages.ERROR + "Spieler nicht gefunden.");
                return true;
            }

            if (reason == null) {
                p.sendMessage(Messages.ERROR + "Grund nicht gefunden.");
                return true;
            }

            if(Beruf.getBeruf(tg) == b) {
                p.sendMessage(Messages.ERROR + "Du kannst Spieler von deinem Beruf kein Hausverbot geben");
                return true;
            }

            if (isHousebanned(tg, b)) {
                p.sendMessage(Messages.INFO + "Der Spieler hat bereits Hausverbot. Das Hausverbot wurde verlängert.");

                long time = getTime(tg, b);
                final String reasonName = getReason(tg, b) + " & " + reason.getName();
                Script.executeUpdate("DELETE FROM housebans WHERE userID = " + Script.getNRPID(tg) + " AND beruf = " + b.getID());
                time += reason.getDuration() + ((long) reason.getDuration() * 24 * 60 * 60 * 1000);
                try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                        "INSERT INTO housebans(userID, reason, beruf, time) VALUES(?, ?, ?, ?)")) {
                    statement.setInt(1, Script.getNRPID(tg));
                    statement.setString(2, reasonName);
                    statement.setInt(3, b.getID());
                    statement.setLong(4, time);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                b.sendMessage(PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + " Hausverbot gegeben.\n" + PREFIX + "§6 " + reason.getName() + " §7»§6 " + DATE_FORMAT.format(new Date(time)) + " Uhr");
                if(tg.getPlayer() != null) tg.getPlayer().sendMessage(PREFIX + " Du hast Hausverbot bei " + b.getName() + " bekommen.\n" + PREFIX + " §6 " + reason.getName() + " §7»§6 " + DATE_FORMAT.format(new Date(time)) + " Uhr");

                return true;
            }

            int id = Script.getNRPID(tg);
            long time = System.currentTimeMillis() + ((long) reason.getDuration() * 24 * 60 * 60 * 1000);
            try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                    "INSERT INTO housebans(userID, reason, beruf, time) VALUES(?, ?, ?, ?)")) {
                statement.setInt(1, id);
                statement.setInt(2, reason.getID());
                statement.setInt(3, b.getID());
                statement.setLong(4, time);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            b.sendMessage(PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + " Hausverbot gegeben.\n" + PREFIX + "§6 " + reason.getName() + " §7»§6 " + DATE_FORMAT.format(new Date(time)) + " Uhr");
            if(tg.getPlayer() != null) tg.getPlayer().sendMessage(PREFIX + " Du hast Hausverbot bei " + b.getName() + " bekommen.\n" + PREFIX + " §6 " + reason.getName() + " §7»§6 " + DATE_FORMAT.format(new Date(time)) + " Uhr");

        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            Player tg = Bukkit.getPlayer(args[1]);
            if (tg == null) {
                p.sendMessage(Messages.ERROR + "Spieler nicht gefunden.");
                return true;
            }

            if (!isHousebanned(tg, b)) {
                p.sendMessage(Messages.ERROR + "Der Spieler hat kein Hausverbot.");
                return true;
            }

            int id = Script.getNRPID(tg);
            try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                    "DELETE FROM housebans WHERE userID = ? AND beruf = ?")) {
                statement.setInt(1, id);
                statement.setInt(2, b.getID());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            b.sendMessage(PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + "s Hausverbot aufgehoben.");
            tg.sendMessage(PREFIX + "Dein Hausverbot bei " + b.getName() + " wurde aufgehoben.");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {

            Player tg = Bukkit.getPlayer(args[1]);
            if (tg == null) {
                p.sendMessage(Messages.ERROR + "Spieler nicht gefunden.");
                return true;
            }

            if (!isHousebanned(tg, b)) {
                p.sendMessage(Messages.ERROR + "Der Spieler hat kein Hausverbot.");
                return true;
            }

            Reasons reason = Reasons.getReason(args[2].replace("-", " "));
            if (reason == null) {
                p.sendMessage(Messages.ERROR + "Grund nicht gefunden.");
                return true;
            }

            int id = Script.getNRPID(tg);
            long time = System.currentTimeMillis() + ((long) reason.getDuration() * 24 * 60 * 60 * 1000);
            try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                    "DELETE FROM housebans WHERE userID = ? AND beruf = ?")) {
                statement.setInt(1, id);
                statement.setInt(2, b.getID());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                    "INSERT INTO housebans(userID, reason, time, beruf) VALUES(?, ?, ?, ?)")) {
                statement.setInt(1, id);
                statement.setString(2, reason.getName());
                statement.setLong(3, time);
                statement.setInt(4, b.getID());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            b.sendMessage(PREFIX + Script.getName(p) + " hat " + Script.getName(tg) + "s Hausverbot bearbeitet. §7»§6 " + reason.getName() + " §7»§6 " + DATE_FORMAT.format(new Date(time)));
            tg.sendMessage(PREFIX + " Dein Hausverbot wurde bearbeitet. §7»§6 " + reason.getName() + " §7»§6 " + DATE_FORMAT.format(new Date(time)));
        } else {
            p.sendMessage(Messages.ERROR + "/houseban {add/remove/edit} {Spieler} {Grund}");
            return true;

        }

        return false;
    }


    public static boolean isHousebanned(Player p, Beruf.Berufe b) {
        int id = Script.getNRPID(p);
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT * FROM housebans WHERE userID = ? AND beruf = ?")) {
            statement.setInt(1, id);
            statement.setInt(2, b.getID());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    if (rs.getLong("time") > System.currentTimeMillis())
                        return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isHousebanned(OfflinePlayer p, Beruf.Berufe b) {
        int id = Script.getNRPID(p);
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT * FROM housebans WHERE userID = ? AND beruf = ?")) {
            statement.setInt(1, id);
            statement.setInt(2, b.getID());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    if (rs.getLong("time") > System.currentTimeMillis())
                        return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static long getTime(Player p, Beruf.Berufe b) {
        int id = Script.getNRPID(p);
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT time FROM housebans WHERE userID = ? AND beruf = ?")) {
            statement.setInt(1, id);
            statement.setInt(2, b.getID());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("time");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static long getTime(OfflinePlayer p, Beruf.Berufe b) {
        int id = Script.getNRPID(p);
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT time FROM housebans WHERE userID = ? AND beruf = ?")) {
            statement.setInt(1, id);
            statement.setInt(2, b.getID());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("time");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static List<Integer> getHousebannedNRPIDs(Beruf.Berufe b) {
        List<Integer> list = new ArrayList<>();
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT userID FROM housebans WHERE beruf = ? AND time > ?")) {
            statement.setInt(1, b.getID());
            statement.setLong(2, System.currentTimeMillis());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("userID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String  getReason(Player p, Beruf.Berufe b) {
        int id = Script.getNRPID(p);
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT reason FROM housebans WHERE userID = ? AND beruf = ?")) {
            statement.setInt(1, id);
            statement.setInt(2, b.getID());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("reason");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getReason(OfflinePlayer p, Beruf.Berufe b) {
        int id = Script.getNRPID(p);
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT reason FROM housebans WHERE userID = ? AND beruf = ?")) {
            statement.setInt(1, id);
            statement.setInt(2, b.getID());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("reason");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("houseban") || cmd.getName().equalsIgnoreCase("hv") || cmd.getName().equalsIgnoreCase("hausverbot")) {
            if (!Beruf.hasBeruf(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();

            for (Reasons reason : Reasons.values()) {
                oneArgList.add(reason.getName().replace(" ", "-"));
            }

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], List.of("add", "remove", "edit"), completions);
            }

            if (args.length == 2) {
                return null;
            }

            if (args.length == 3) {
                StringUtil.copyPartialMatches(args[2], oneArgList, completions);
            }

            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }
}
