package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Forum.Forum;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.StringUtil;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Punish implements CommandExecutor, TabCompleter, Listener {

    public static String PREFIX = "§8[§cPunish§8] §c" + Messages.ARROW + " ";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.y HH:mm:ss", Locale.GERMANY);

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        Violation v;

        if (!Script.hasRank(p, Rank.MODERATOR, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/punish [Spieler] [Verstoß]");
            return true;
        }

        v = Violation.getViolationByArg(args[1]);

        if(v != Violation.SICHERHEITSBANN && v != Violation.SPAM && !Script.hasRank(p, Rank.MODERATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (v == null) {
            p.sendMessage(Messages.ERROR + "Verstoß nicht gefunden.");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        OfflinePlayer offtg = Script.getOfflinePlayer(Script.getNRPID(args[0]));
        if(Script.getNRPID(offtg) == 0) {
            Script.registerPlayer(offtg);
            p.sendMessage(Messages.INFO + "Der Spieler hat noch nie auf New RolePlay gespielt. Er wurde registriert.");
        }

        if (tg == null && offtg != null) {
            Punish.punish(p, offtg, v);
            return true;
        }

        if (p == tg && !Script.isInTestMode()) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst bestrafen.");
            return true;
        }

        if (Script.hasRank(tg, Rank.SUPPORTER, false) && !Script.isInTestMode() && !Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.ERROR + "Du kannst keine Teammitglieder bestrafen.");
            return true;
        }


        punish(p, tg, v);

        return false;
    }

    @Override
    public List onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("punish")) {
            if (!SDuty.isSDuty(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Violation v : Violation.values()) {
                oneArgList.add(v.getArgName());
            }

            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[1], oneArgList, completions);
            }

            if (args.length != 2) {
                return null;
            }
            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

    public enum Violation {
        CHEATEN(1, Punishment.BAN, Punishment.WARN, TimeUnit.DAYS.toMillis(7), "Cheaten", "Wir konnten bei dir Cheats feststellen."),
        BELEIDIGUNG(2, Punishment.BAN, null, TimeUnit.HOURS.toMillis(3), "Beleidigung", "Du hast einen Spieler beleidigt."),
        FREMDWERBUNG(3, Punishment.MUTE, null, TimeUnit.HOURS.toMillis(1), "Fremdwerbung", "Du hast Fremdwerbung verbreitet."),
        MOBBING_LEICHT(4, Punishment.BAN, null, TimeUnit.DAYS.toMillis(3), "Mobbing (Leicht)", "Du hast andere Spieler gemobbt."),
        MOBBING_SCHWER(5, Punishment.BAN, Punishment.WARN, 0, "Mobbing (Schwer)", "Du hast andere Spieler gemobbt."),
        RASSISMUS(6, Punishment.BAN, null, 0, "Rassismus", "Du hast rassistische Äußerungen getätigt."),
        EXTREMISMUS(7, Punishment.BAN, null, 0, "Extremismus", "Du hast extremistische Äußerungen getätigt."),
        DROHUNG(8, Punishment.BAN, null, TimeUnit.HOURS.toMillis(24), "Drohung", "Du hast anderen Spielern gedroht."),
        BUGUSE(9, Punishment.BAN, null, TimeUnit.HOURS.toMillis(12), "Buguse", "Du hast Spielfehler ausgenutzt."),
        SUPPORTABUSE(10, Punishment.BAN, null, TimeUnit.HOURS.toMillis(1), "Support Missbrauch", "Du hast das Ticket-System missbraucht."),
        RECHTEMISSBRAUCH(11, Punishment.BAN, Punishment.WARN, TimeUnit.DAYS.toMillis(7), "Rechte Missbrauch", "Du hast deine Rechte missbraucht."),
        SICHERHEITSBANN(12, Punishment.BAN, null, 0, "Sicherheitsbann", "Du wurdest zur Sicherheit gebannt. Sollte dir der Grund nicht bekannt sein, melde dich bei uns im Support."),
        BAD_NEWS(13, Punishment.BAN, null, TimeUnit.DAYS.toMillis(1), "Bad /news", "Du hast den Befehl /news missbraucht."),
        BAD_STAATSMELDUNG(14, Punishment.BAN, null, TimeUnit.DAYS.toMillis(1), "Bad /staatsmeldung", "Du hast den Befehl /staatsmeldung missbraucht."),
        MISSBRAUCH_TRAGEN(15, Punishment.TRAGEN_SPERRE, null, 120, "Missbrauch von /tragen", "Du hast den Befehl /tragen missbraucht."),
        REPEATING_MISSBRAUCH_TRAGEN(16, Punishment.BAN, null, TimeUnit.DAYS.toMillis(1), "Wiederholter Missbrauch von /tragen", "Du hast den Befehl /tragen wiederholt missbraucht."),
        EXTREMER_MISSBRAUCH_TRAGEN(17, Punishment.BAN, Punishment.WARN, TimeUnit.DAYS.toMillis(14), "Extremer Missbrauch von /tragen", "Du hast den Befehl /tragen extrem missbraucht."),
        SPAM(18, Punishment.KICK, null, 0, "Spam", "Du hast gespammt."),
        WIEDERHOLTER_SPAM(19, Punishment.BAN, null, TimeUnit.MINUTES.toMillis(30), "Wiederholter Spam", "Du hast wiederholt gespammt."),
        EXTREMER_SPAM(20, Punishment.BAN, null, TimeUnit.HOURS.toMillis(1), "Extremer Spam", "Du hast extrem gespammt."),
        WIEDERHOLTE_FREMDWERBUNG(21, Punishment.BAN, Punishment.WARN, TimeUnit.DAYS.toMillis(7), "Wiederholte Fremdwerbung", "Du hast wiederholt Fremdwerbung verbreitet.");



        int id;
        String name;
        Punishment punishment;
        Punishment secondaryPunishment;
        Long duration;

        String description;


        Violation(int id, Punishment punishment, Punishment secondaryPunishment, long duration, String name, String description) {
            this.id = id;
            this.name = name;
            this.punishment = punishment;
            this.secondaryPunishment = secondaryPunishment;
            this.duration = duration;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getArgName() {
            return name.replace(" ", "-");
        }

        public String getDescription() {
            return description;
        }

        public int getID() {
            return id;
        }

        public Punishment getPunishment() {
            return punishment;
        }

        public Punishment getSecondaryPunishment() {
            return secondaryPunishment;
        }

        public Long getDuration() {
            return duration;
        }

        public static Violation getViolationByID(int id) {
            for (Violation v : Violation.values()) {
                if (v.getID() == id) return v;
            }
            return null;
        }

        public static Violation getViolationByName(String name) {
            for (Violation v : Violation.values()) {
                if (v.getArgName().equalsIgnoreCase(name)) return v;
            }
            return null;
        }

        public static Violation getViolationByArg(String arg) {
            for (Violation v : Violation.values()) {
                if (v.getArgName().equalsIgnoreCase(arg)) return v;
            }
            return null;
        }

    }

    public enum Punishment {
        BAN("Bann"),
        MUTE("Mute"),
        KICK("Kick"),
        WARN("Warn"),
        CHECKPOINTS("Checkpoints"),
        TRAGEN_SPERRE("Tragensperre");

        String name;

        Punishment(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static String generatePunishID() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    public static void punish(Player p, Player tg, Violation v) {
        Punishment punishment = v.getPunishment();
        Punishment secondaryPunishment = v.getSecondaryPunishment();
        Date until = new Date(new Date().getTime() + v.getDuration());
        long untilLong = new Date().getTime() + v.getDuration();
        if (punishment == Punishment.BAN || secondaryPunishment == Punishment.BAN) {
            if (v.getDuration() == 0) {
                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " für " + v.getName() + " gebannt.");
                tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " für " + v.getName() + " gebannt.");
                tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
                tg.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cBANN §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gebannt§8.\n\n§7Grund §8× §e" + v.getName() + "\n§7Gebannt bis §8× §e" + "Lebenslang" + "\n\n§7Einen Entbannungsantrag kannst du auf der Webseite stellen.\n\n§8§m------------------------------");
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " für " + v.getName() + " gebannt.");
                Log.HIGH.write(p, "hat " + Script.getName(tg) + " für " + v.getName() + " gebannt.");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde von " + Messages.RANK_PREFIX(p) + " für §l" + v.getName() + "§c gebannt.");
                if(Beruf.isLeader(p)) Script.setInt(p, "berufe", "leader", 0);
            } else {
                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
                tg.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cBANN §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gebannt§8.\n\n§7Grund §8× §e" + v.getName() + "\n§7Gebannt bis §8× §e" + dateFormat.format(until) + "\n\n§7Einen Entbannungsantrag kannst du auf der Webseite stellen.\n\n§8§m------------------------------");
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                Log.HIGH.write(p, "hat " + Script.getName(tg) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde von " + Messages.RANK_PREFIX(p) + " bis zum " + dateFormat.format(until) + " Uhr für §l" + v.getName() + " §cgebannt.");
            }

            TeamSpeak.sync(Script.getNRPID(tg));
            Forum.syncPermission(tg);
        }

        if (punishment == Punishment.MUTE || secondaryPunishment == Punishment.MUTE) {
            if (v.getDuration() == 0) {
                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " für " + v.getName() + " gemutet.");
                tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " für " + v.getName() + " gemutet.");
                tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
                Script.sendTeamMessage(p, ChatColor.RED, "hat " + Script.getName(tg) + " für " + v.getName() + " gemutet.", true);
                Script.executeUpdate("INSERT INTO `mute` (id, mute_id, nrp_id, since, until, reason, muted_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " für " + v.getName() + " gemutet.");
                Log.HIGH.write(p, "hat " + Script.getName(tg) + " für " + v.getName() + " gemutet.");
            } else {
                p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.");
                tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.");
                tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
                Script.sendTeamMessage(p, ChatColor.RED, "hat " + Script.getName(tg) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.", true);
                Script.executeUpdate("INSERT INTO `mute` (id, mute_id, nrp_id, since, until, reason, muted_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.");
                Log.HIGH.write(p, "hat " + Script.getName(tg) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.");
            }
            int muteminutes = (int) (v.getDuration() / 1000) / 60;
            Sperre.MUTE.setSperre(Script.getNRPID(tg), muteminutes);
        }

        if (punishment == Punishment.KICK || secondaryPunishment == Punishment.KICK) {
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " für " + v.getName() + " gekickt.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " für " + v.getName() + " gekickt.");
            tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
            Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " für " + v.getName() + " gekickt.");
            Log.HIGH.write(p, "hat " + Script.getName(tg) + " für " + v.getName() + " gekickt.");
            tg.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKICK §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §e" + v.getDescription() + "\n\n§8§m------------------------------");
            Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde von " + Script.getName(p) + " für §l" + v.getName() + " §cgekickt.");
        }

        if (punishment == Punishment.WARN || secondaryPunishment == Punishment.WARN) {
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " für " + v.getName() + " verwarnt.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " für " + v.getName() + " verwarnt.");
            tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
            Script.executeUpdate("INSERT INTO `warns` (id, warn_id, nrp_id, since, until, reason, warned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', NULL, '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + Script.getName(tg) + " für " + v.getName() + " verwarnt.", true);
            Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " für " + v.getName() + " verwarnt.");
            Log.HIGH.write(p, "hat " + Script.getName(tg) + " für " + v.getName() + " verwarnt.");
            if(getWarns(tg) >= 3) {
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', NULL, 'maximale Anzahl an Warns überschritten', '0');");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde automatisch gebannt (3/3 Warns)");
            }
        }

        if(punishment == Punishment.TRAGEN_SPERRE || secondaryPunishment == Punishment.TRAGEN_SPERRE) {
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " eine Tragensperre für " + v.getDuration() + " Minuten gegeben.");
            tg.sendMessage(PREFIX + "Du hast von " + Script.getName(p) + " eine Tragensperre für " + v.getDuration() + " Minuten bekommen.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " eine Tragensperre für " + v.getDuration() + " Minuten gegeben.", true);
            Log.WARNING.write(tg, "hat von " + Messages.RANK_PREFIX(p) + " eine Tragensperre für " + v.getDuration() + " Minuten bekommen.");
            Log.HIGH.write(p, "hat " + tg.getName() + " eine Tragensperre für " + v.getDuration() + " Minuten gegeben.");
            Sperre.TRAGENSPERRE.setSperre(Script.getNRPID(tg), v.getDuration());
        }

        if(punishment == Punishment.CHECKPOINTS || secondaryPunishment == Punishment.CHECKPOINTS) {
            int checkpoints = Math.toIntExact(v.getDuration());
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
            Checkpoints.setCheckpoints(tg, checkpoints, true);
            Log.WARNING.write(p, "hat " + Script.getName(tg) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Log.HIGH.write(tg, "wurde von " + tg.getName() + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde wegen " + v.getName() + " von " + Messages.RANK_PREFIX(p) + " zu " + v.getDuration() + " Checkpoints eingesperrt.");
        }
    }

    public static void punish(Player p, OfflinePlayer tg, Violation v) {
        Punishment punishment = v.getPunishment();
        Punishment secondaryPunishment = v.getSecondaryPunishment();
        Date until = new Date(new Date().getTime() + v.getDuration());
        long untilLong = new Date().getTime() + v.getDuration();
        if (punishment == Punishment.BAN || secondaryPunishment == Punishment.BAN) {
            if (v.getDuration() == 0) {
                p.sendMessage(PREFIX + "Du hast " + tg.getName() + " für " + v.getName() + " gebannt.");
                Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " für " + v.getName() + " gebannt.", true);
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Script.getName(p) + " für " + v.getName() + " gebannt.");
                Log.HIGH.write(p, "hat " + tg.getName() + " für " + v.getName() + " gebannt.");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + tg.getName() + " wurde von " + Messages.RANK_PREFIX(p) + " für §l" + v.getName() + " §cgebannt.");
            } else {
                p.sendMessage(PREFIX + "Du hast " + tg.getName() + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.", true);
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Script.getName(p) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                Log.HIGH.write(p, "hat " + tg.getName() + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + tg.getName() + " wurde von " + Messages.RANK_PREFIX(p) + " bis zum " + dateFormat.format(until) + " Uhr für §l" + v.getName() + " §cgebannt.");
            }
            TeamSpeak.sync(Script.getNRPID(tg));
            Forum.syncPermission(tg);
        }

        if (punishment == Punishment.MUTE || secondaryPunishment == Punishment.MUTE) {
            if (v.getDuration() == 0) {
                p.sendMessage(PREFIX + "Du hast " + tg.getName() + " für " + v.getName() + " gemutet.");
                Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " für " + v.getName() + " gemutet.", true);
                Script.executeUpdate("INSERT INTO `mute` (id, mute_id, nrp_id, since, until, reason, muted_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " für " + v.getName() + " gemutet.");
                Log.HIGH.write(p, "hat " + tg.getName() + " für " + v.getName() + " gemutet.");
                Script.addOfflineMessage(tg,"Du wurdest von " + Script.getName(p) + " für §l" + v.getName() + "§c gemutet.");
                Script.addOfflineMessage(tg, "Grund: " + v.getDescription());
            } else {
                p.sendMessage(PREFIX + "Du hast " + tg.getName() + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.");
                Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.", true);
                Script.executeUpdate("INSERT INTO `mute` (id, mute_id, nrp_id, since, until, reason, muted_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.");
                Log.HIGH.write(p, "hat " + tg.getName() + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gemutet.");
                Script.addOfflineMessage(tg, PREFIX + "Du wurdest von " + Script.getName(p) + " bis zum " + dateFormat.format(until) + " Uhr für §l" + v.getName() + "§c gemutet.");
                Script.addOfflineMessage(tg, PREFIX + "Grund: " + v.getDescription());
            }

            int muteminutes = (int) (v.getDuration() / 1000) / 60;
            Sperre.MUTE.setSperre(Script.getNRPID(tg), muteminutes);
        }

        if (punishment == Punishment.KICK || secondaryPunishment == Punishment.KICK) {
            p.sendMessage(Messages.INFO + "Der Kick wurde nicht ausgeführt, da der Spieler nicht online ist.");
        }

        if (punishment == Punishment.WARN || secondaryPunishment == Punishment.WARN) {
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " für " + v.getName() + " verwarnt.");
            Script.executeUpdate("INSERT INTO `warns` (id, warn_id, nrp_id, since, until, reason, warned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', NULL, '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " für " + v.getName() + " verwarnt.", true);
            Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " für " + v.getName() + " verwarnt.");
            Log.HIGH.write(p, "hat " + tg.getName() + " für " + v.getName() + " verwarnt.");
            Script.addOfflineMessage(tg, PREFIX + "Du hast für " + v.getName() + " einen Warn erhalten.");
            Script.addOfflineMessage(tg, PREFIX + "Grund: " + v.getDescription());
            if(getWarns(tg) >= 3) {
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', NULL, 'maximale Anzahl an Warns überschritten', '0');");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + tg.getName() + " wurde automatisch gebannt (3/3 Warns)");
            }
        }

        if(punishment == Punishment.TRAGEN_SPERRE || secondaryPunishment == Punishment.TRAGEN_SPERRE) {
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " eine Tragensperre für " + v.getDuration() + " Minuten gegeben.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " eine Tragensperre für " + v.getDuration() + " Minuten gegeben.", true);
            Log.WARNING.write(tg, "hat von " + Messages.RANK_PREFIX(p) + " eine Tragensperre für " + v.getDuration() + " Minuten bekommen.");
            Log.HIGH.write(p, "hat " + tg.getName() + " eine Tragensperre für " + v.getDuration() + " Minuten gegeben.");
            Sperre.TRAGENSPERRE.setSperre(Script.getNRPID(tg), v.getDuration());
        }


        if(punishment == Punishment.CHECKPOINTS || secondaryPunishment == Punishment.CHECKPOINTS) {
            int checkpoints = Math.toIntExact(v.getDuration());
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Script.addOfflineMessage(tg, PREFIX + "Du wurdest von " + Script.getName(p) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Script.addOfflineMessage(tg, PREFIX + "Grund: " + v.getDescription());
            Script.executeAsyncUpdate("INSERT INTO checkpoints (id, checkpoints) VALUES (" + Script.getNRPID(tg) + ", " + checkpoints + ") ON DUPLICATE KEY UPDATE checkpoints = checkpoints + " + checkpoints + ";");
            Log.WARNING.write(p, "hat " + tg.getName() + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Log.HIGH.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Bukkit.broadcastMessage(Script.PREFIX + "§c" + tg.getName() + " wurde wegen " + v.getName() + " von " + Messages.RANK_PREFIX(p) + " zu " + checkpoints + " Checkpoints eingesperrt.");
        }
    }


    public static boolean isMuted(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM mute WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                if (rs.getLong("until") > System.currentTimeMillis()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getMuteUntil(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ban WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                if (rs.getLong("until") > System.currentTimeMillis()) {
                    return rs.getLong("until");
                }
                if (rs.wasNull()) {
                    return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long getBanUntil(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ban WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                if (rs.getLong("until") > System.currentTimeMillis()) {
                    return rs.getLong("until");
                }
                if (rs.wasNull()) {
                    return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long getBanUntil(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ban WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                if (rs.getLong("until") > System.currentTimeMillis()) {
                    return rs.getLong("until");
                }
                if (rs.wasNull()) {
                    return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getWarns(Player p) {
        int i = 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM warns WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                do {
                    i++;
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static int getWarns(OfflinePlayer p) {
        int i = 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM warns WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                do {
                    i++;
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static HashMap<Long, String> getWarnsMap(Player p) {
        HashMap<Long, String> warns = new HashMap<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM warns WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY since ASC;")) {
            if (rs.next()) {
                do {
                    warns.put(rs.getLong("since"), rs.getString("reason"));
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return warns;
    }

    public static HashMap<Long, String> getWarnsMap(OfflinePlayer p) {
        HashMap<Long, String> warns = new HashMap<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM warns WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY since ASC;")) {
            if (rs.next()) {
                do {
                    warns.put(rs.getLong("since"), rs.getString("reason"));
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return warns;
    }


    public static String getBanReason(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ban WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBanReason(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ban WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(getBanUntil(p) == -1) return;
        if (getBanUntil(p) > System.currentTimeMillis() || getBanUntil(p) == 0) {
            p.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cBann §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gebannt§8.\n\n§7Grund §8× §e" + getBanReason(p) + "\n§7Gebannt bis §8× §e" + (getBanUntil(p) != 0 ? Script.dateFormat.format(getBanUntil(p)) : "Lebenslang") + "\n\n§7Einen Entbannungsantrag kannst du auf der Webseite stellen.\n\n§8§m------------------------------");
        }
        Notications.sendMessage(Notications.NotificationType.JOIN, "§e" + Script.getName(e.getPlayer()) + " §7hat den Server betreten.");
    }

    public static void unban(OfflinePlayer p) {
        Script.executeUpdate("DELETE FROM ban WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;");
        Script.executeUpdate("DELETE FROM mute WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;");
    }

    public static void removeWarn(OfflinePlayer p) {
        Script.executeUpdate("DELETE FROM warns WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;");
    }

    public static void removeWarn(Player p) {
        Script.executeUpdate("DELETE FROM warns WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;");
    }

    public static void removeWarn(String id) {
        Script.executeUpdate("DELETE FROM warns WHERE warn_id='" + id + "';");
    }

    public static void removeMute(String id) {
        Script.executeUpdate("DELETE FROM mute WHERE mute_id='" + id + "';");
    }


}
