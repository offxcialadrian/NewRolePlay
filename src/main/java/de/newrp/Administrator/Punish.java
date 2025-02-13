package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Forum.Forum;
import de.newrp.Organisationen.Organisation;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.NewRoleplayMain;
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

        if (args.length < 2) {
            p.sendMessage(Messages.ERROR + "/punish [Spieler] [Verstoß]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        OfflinePlayer offtg = Script.getOfflinePlayer(Script.getNRPID(args[0]));
        if (Script.getNRPID(offtg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }



        for(String arg : args) {
            if(arg.equalsIgnoreCase(args[0])) continue;
            v = Violation.getViolationByArg(arg);

            if (v != Violation.SICHERHEITSBANN && v != Violation.SPAM && !Script.hasRank(p, Rank.MODERATOR, false)) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (v == null) {
                p.sendMessage(Messages.ERROR + "Verstoß nicht gefunden.");
                return true;
            }

            if (tg == null && offtg != null) {
                Punish.punish(p, offtg, v);
                return true;
            }

            if (p == tg && !Script.isInTestMode()) {
                p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst bestrafen.");
                return true;
            }

            if (Script.hasRank(tg, Rank.SUPPORTER, false) && !Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                p.sendMessage(Messages.ERROR + "Du kannst keine Teammitglieder bestrafen.");

                for (Player team : Script.getNRPTeam()) {
                    if (Script.hasRank(team, Rank.ADMINISTRATOR, false)) {
                        team.sendMessage(AntiCheatSystem.PREFIX + "§c" + Script.getName(p) + " §chat versucht ein Teammitglied zu bestrafen.");
                    }
                }

                return true;
            }


            punish(p, tg, v);
        }

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

            if (args.length >= 2) {
                StringUtil.copyPartialMatches(args[args.length - 1], oneArgList, completions);
            }

            if (args.length == 1) {
                return null;
            }
            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

    public enum Violation {

        AFK_FARMING(1, Punishment.BAN, null, TimeUnit.HOURS.toMillis(24), 0, "AFK-Farming", "Das Benutzen von externen Programmen, Bugs oder Ähnlichem verhindert, in den AFK-Modus zu wechseln oder diesen automatisch wieder zu verlassen."),
        ACCOUNT_SHARING(2, Punishment.CHECKPOINTS, null, 0, 75, "Account Sharing", "Das Nutzen eines Accounts mit mehreren Personen. Eine Anmeldung als Multiaccount im Forum rechtfertigt kein Account-Sharing."),
        ACCOUNT_SELLING(3, Punishment.BAN, null, 0, 0, "Account Selling", "Das Verschenken/Verkaufen eines Accounts."),
        BAD_NEWS(4, null, null, 0, 0, "Bad /news", "Werbungen (/news) dürfen nur für das Werben oder das Suchen von bzw. nach Dienstleistungen, Produkten oder Veranstaltungen verwendet werden. "),
        BAD_TICKET(5, Punishment.CHECKPOINTS, null, 0, 50, "Bad /ticket", "Unkooperatives oder unanständiges Verhalten im Ticket, aber auch das vorsätzliche Erstellen überflüssiger Tickets."),
        REPEATING_BAD_TICKET(6, Punishment.BAN, null, TimeUnit.DAYS.toMillis(3), 0, "Bad /ticket (Wiederholt)", "Wiederholtes Unkooperatives oder unanständiges Verhalten im Report, aber auch das vorsätzliche Erstellen überflüssiger Reports."),
        BELEIDIGUNG(7, Punishment.CHECKPOINTS, null, 0, 25, "Beleidigung", "Eine beleidigende Aussage, die nicht im Sinne des Roleplays ist und/oder einen Spieler in Reallife beleidigt."),
        REPEATING_BELEIDIGUNG(8, Punishment.BAN, Punishment.CHECKPOINTS, TimeUnit.HOURS.toMillis(12), 50, "Beleidigung (Wiederholt)", "Wiederholte beleidigende Aussagen, die nicht im Sinne des Roleplays sind und/oder einen Spieler in Reallife beleidigen."),
        BUGUSE(9, Punishment.CHECKPOINTS, null, 0, 100, "Buguse", "Das Ausnutzen eines Fehlers im System und/oder der Welt ohne diesen zu melden."),
        BUGUSE_EIGENERTRAG(10, Punishment.BAN, Punishment.CHECKPOINTS, TimeUnit.DAYS.toMillis(7), 100, "Buguse mit Eigenertrag", "Das Ausnutzen eines Fehlers im System und/oder der Welt, um sich selbst einen Vorteil zu verschaffen."),
        CHEATEN(11,Punishment.BAN, Punishment.WARN, TimeUnit.DAYS.toMillis(7), 0,"Cheaten","Betreten des Servers mit und/oder Nutzen von unerlaubten Spielmodifikationen oder externen Programmen, die dem Spieler einen Vorteil bringen und/oder die Physik des Servers umgehen."),
        CHEATEN_SCHWERWIEGEND(12,Punishment.BAN, Punishment.WARN, TimeUnit.DAYS.toMillis(30),0,"Schwerwiegender Cheat","Betreten des Servers mit und/oder Nutzen von unerlaubten Spielmodifikationen oder externen Programmen, die dem Spieler einen Vorteil bringen und/oder die Physik des Servers umgehen."),
        FREMDWERBUNG(13, Punishment.CHECKPOINTS, Punishment.WARN, TimeUnit.DAYS.toMillis(14), 100,"Fremdwerbung","Das Verbreiten von Plattformen, Verweisen oder ähnlichen Dingen, die nicht mit New RolePlay in Verbindung stehen. Dazu zählen auch bspw. andere TeamSpeak- sowie Discord-Server."),
        HANDEL_MIT_SPIELEXTERNEN_DINGEN(14, Punishment.BAN, Punishment.WARN, TimeUnit.DAYS.toMillis(7), 0, "Handel mit spielexternen Dingen", "Tauschen von Ingameinhalten gegen etwas Spielexternes mit Wert, obgleich virtuell oder real."),
        METAGAMING(15, Punishment.CHECKPOINTS, null, 0, 100, "Metagaming", "Das Nutzen von Informationen, die der Spieler nicht durch sein Roleplay erfahren hat."),
        CYBERMOBBING(16, Punishment.BAN, null, 0, 0, "Cybermobbing", " Das Mobben eines Spielers, das sich auf sein Reallife ausweitet."),
        RASSISMUSS(17, Punishment.BAN, null, 0, 0, "Rassismus", "Das Verbreiten von rassistischen Äußerungen."),
        RUFMORD(18, Punishment.BAN, null, TimeUnit.DAYS.toMillis(7), 0, "Rufmord", "Das Verbreiten von unwahren Behauptungen über einen Spieler."),
        RDM(19, Punishment.CHECKPOINTS, null, 0, 50, "Random Deathmatch", "Das Töten eines Spielers ohne einen RP-Grund."),
        SPAM(20, Punishment.KICK, null, 0, 0, "Spam", "Das wiederholte Senden von Nachrichten, die den Chat stören."),
        SPAM_WIEDERHOLT(21, Punishment.CHECKPOINTS, null, 0, 10, "Spam (Wiederholt)", "Das wiederholte Senden von Nachrichten, die den Chat stören."),
        SUPPORTER_VERWEIGERUNG(22, Punishment.CHECKPOINTS, null, 0, 50, "Supporter-Verweigerung", "Das Missachten von Anweisungen eines Supporters oder das Belügen eines Supporters, welches im administrativen Zusammenhang geschieht."),
        EROTIK_ROLEPLAY(23, Punishment.CHECKPOINTS, null, 0, 50, "Erotik-Roleplay", "Das Ausleben von sexuellen Handlungen im Roleplay."),
        FOLTER_ROLEPLAY(24, Punishment.CHECKPOINTS, null, 0, 50, "Folter-Roleplay", "Das Ausleben von Folterhandlungen im Roleplay."),
        RECHTE_AUSNUTZUNG(25, Punishment.CHECKPOINTS, Punishment.WARN, 0, 100, "Rechte-Ausnutzung", "Das Ausnutzen von Rechten, die einem Spieler zustehen."),
        SUPPORT_AUSNUTZUNG(26, Punishment.BAN, null, TimeUnit.DAYS.toMillis(1), 0, "Support-Ausnutzung", "Das Ausnutzen von Supportern, um sich einen Vorteil zu verschaffen."),
        BAD_GOV(27, Punishment.CHECKPOINTS, null, 0, 50, "Bad /gov", "Unkooperatives oder unanständiges Verhalten im /gov, aber auch das vorsätzliche Erstellen überflüssiger /govs."),
        BUENDNIS(28, Punishment.CHECKPOINTS, null, 0, 50, "Unerlaubtes Bündnis", "Das Eingehen eines Bündnisses mit einer anderen Organisation, ohne dies im Forum zu melden."),
        FRAKTIONSFLUCHT(29, Punishment.CHECKPOINTS, null, 0, 50, "Fraktionsflucht", "Das Flüchten in das HQ um einer RolePlay-Situation zu entgehen."),
        REPEATING_FRAKTIONSFLUCHT(30, Punishment.CHECKPOINTS, Punishment.WARN, 0, 75, "Fraktionsflucht (Wiederholt)", "Wiederholtes Flüchten in das HQ um einer RolePlay-Situation zu entgehen."),
        MISSACHTEN_DER_PLANTAGENREGELN(31, Punishment.CHECKPOINTS, null, 0, 50, "Missachten der Plantagenregel", "Das Missachten der Plantagenregel."),
        MISSACHTEN_ÜBERFALLREGEL(32, Punishment.CHECKPOINTS, null, 0, 100, "Missachten der Überfallregel", "Das Missachten der Überfallregel."),
        UNREALISTISCHES_SPIELVERHALTEN(33, Punishment.CHECKPOINTS, null, 0, 100, "Unrealistisches Spielverhalten", "Das Ausleben von unrealistischen Handlungen im Roleplay."),
        REPEATING_UNREALISTISCHES_SPIELVERHALTEN(34, Punishment.CHECKPOINTS, Punishment.WARN, 0, 200, "Unrealistisches Spielverhalten (Wiederholt)", "Wiederholtes Ausleben von unrealistischen Handlungen im Roleplay."),
        MISSACHTEN_KABELBINDERREGEL(35, Punishment.CHECKPOINTS, null, 0, 50, "Missachten der Kabelbinderregel", "Das Missachten der Kabelbinderregel."),
        OFFLINE_FLUCHT(36, Punishment.CHECKPOINTS, null, 0, 50, "Offline-Flucht", "Das Flüchten in den Offline-Modus um einer RolePlay-Situation zu entgehen."),
        AFK_FLUCHT(37, Punishment.CHECKPOINTS, null, 0, 50, "AFK-Flucht", "Das Flüchten in den AFK-Modus um einer RolePlay-Situation zu entgehen."),
        TELEPORT_FLUCHT(38, Punishment.CHECKPOINTS, null, 0, 50, "Teleport-Flucht", "Das Flüchten durch einen Teleport um einer RolePlay-Situation zu entgehen."),
        POWERGAMING(39, Punishment.CHECKPOINTS, null, 0, 50, "Powergaming", "Das Ausnutzen von Mechaniken, die im Roleplay nicht möglich wären."),
        MISSACHTEN_DER_ROLEPLAYCHATREGEL(40, Punishment.KICK, null, 0, 0, "Missachten der Roleplaychatregel", "Das Missachten der Roleplaychatregel."),
        REPEATING_MISSACHTEN_DER_ROLEPLAYCHATREGEL(41, Punishment.CHECKPOINTS, null, 0, 25, "Missachten der Roleplaychatregel (Wiederholt)", "Wiederholtes Missachten der Roleplaychatregel."),
        UMVERTEILUNG_DER_PERSÖNLICHKEITSRECHTE(42, Punishment.CHECKPOINTS, null, 0, 50, "Umverteilung der Persönlichkeitsrechte", "Umverteilung der Persönlichkeitsrechte."),
        SICHERHEITSBANN(98 ,Punishment.BAN, null,0,0,"Sicherheitsbann","Du wurdest zur Sicherheit gebannt. Sollte dir der Grund nicht bekannt sein, melde dich bei uns im Support."),
        RDM_SCHWERWIEGEND(43, Punishment.BAN, Punishment.WARN, TimeUnit.DAYS.toMillis(3),0,"Schwerwiegender Random Deathmatch","Das Töten eines Spielers ohne einen RP-Grund."),
        MISSBRAUCH_VON_TRAGEN(44, Punishment.TRAGEN_SPERRE, null, 0, 0, "Missbrauch von /tragen", "Das Missbrauchen des Tragens."),

        UNZUREICHENDE_EIGNUNG(99,Punishment.BAN, null,0,0,"Unzureichende Eignung","Du hast unzureichende Eignung gezeigt.");


        int id;
        String name;
        Punishment punishment;
        Punishment secondaryPunishment;
        Long duration;
        int checkpoints;

        String description;


        Violation(int id, Punishment punishment, Punishment secondaryPunishment, long duration, int checkpoints, String name, String description) {
            this.id = id;
            this.name = name;
            this.punishment = punishment;
            this.secondaryPunishment = secondaryPunishment;
            this.duration = duration;
            this.checkpoints = checkpoints;
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

        public int getCheckpoints() {
            return checkpoints;
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
        new Straflog(tg, p, v);
        Punishment punishment = v.getPunishment();
        Punishment secondaryPunishment = v.getSecondaryPunishment();
        Date until = new Date(new Date().getTime() + v.getDuration());
        long untilLong = new Date().getTime() + v.getDuration();

        if(v == Violation.RDM) {
            Sperre.WAFFENSPERRE.setSperre(Script.getNRPID(tg), (60*24*3));
        }
        if(v == Violation.RDM_SCHWERWIEGEND) {
            Sperre.WAFFENSPERRE.setSperre(Script.getNRPID(tg), (60*24*7));
        }

        if (punishment == Punishment.BAN || secondaryPunishment == Punishment.BAN) {
            if (v.getDuration() == 0) {
                tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " für " + v.getName() + " gebannt.");
                tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
                tg.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cBANN §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gebannt§8.\n\n§7Grund §8× §e" + v.getName() + "\n§7Gebannt bis §8× §e" + "Lebenslang" + "\n\n§7Einen Entbannungsantrag kannst du auf der Webseite stellen.\n\n§8§m------------------------------");
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " für " + v.getName() + " gebannt.");
                Log.HIGH.write(p, "hat " + Script.getName(tg) + " für " + v.getName() + " gebannt.");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde von " + Messages.RANK_PREFIX(p) + " für §l" + v.getName() + "§c gebannt.");
            } else {
                tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
                tg.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cBANN §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gebannt§8.\n\n§7Grund §8× §e" + v.getName() + "\n§7Gebannt bis §8× §e" + dateFormat.format(until) + "\n\n§7Einen Entbannungsantrag kannst du auf der Webseite stellen.\n\n§8§m------------------------------");
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', " + (v.getDuration() > 0 ? untilLong : "NULL") + ", '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
                Log.WARNING.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                Log.HIGH.write(p, "hat " + Script.getName(tg) + " bis zum " + dateFormat.format(until) + " Uhr für " + v.getName() + " gebannt.");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde von " + Messages.RANK_PREFIX(p) + " bis zum " + dateFormat.format(until) + " Uhr für §l" + v.getName() + " §cgebannt.");
            }

            if(Script.hasRank(p, Rank.SUPPORTER, false)) {
                Script.executeUpdate("DELETE FROM ranks WHERE nrp_id=" + Script.getNRPID(tg));
                Script.executeAsyncUpdate("DELETE FROM ticket_greeting WHERE nrp_id=" + Script.getNRPID(tg));
                Script.executeAsyncUpdate("DELETE FROM ticket_farewell WHERE nrp_id=" + Script.getNRPID(tg));
                Script.executeAsyncUpdate("DELETE from notifications WHERE nrp_id=" + Script.getNRPID(tg));
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
            tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " für " + v.getName() + " gekickt.");
            tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
            Script.executeUpdate("INSERT INTO `kick` (id, kick_id, nrp_id, time, reason, kicked_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', '" + v.getName() + "', '" + Script.getNRPID(p) + "');");
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
            if (getWarns(tg) >= 3) {
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', NULL, 'maximale Anzahl an Warns überschritten', '0');");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde automatisch gebannt (3/3 Warns)");
            }
        }

        if (punishment == Punishment.TRAGEN_SPERRE || secondaryPunishment == Punishment.TRAGEN_SPERRE) {
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " eine Tragensperre für " + 180 + " Minuten gegeben.");
            tg.sendMessage(PREFIX + "Du hast von " + Script.getName(p) + " eine Tragensperre für " + 180 + " Minuten bekommen.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " eine Tragensperre für " + 180 + " Minuten gegeben.", true);
            Log.WARNING.write(tg, "hat von " + Messages.RANK_PREFIX(p) + " eine Tragensperre für " + 180 + " Minuten bekommen.");
            Log.HIGH.write(p, "hat " + tg.getName() + " eine Tragensperre für " + 180 + " Minuten gegeben.");
            Sperre.TRAGENSPERRE.setSperre(Script.getNRPID(tg), 180);
        }

        if (punishment == Punishment.CHECKPOINTS || secondaryPunishment == Punishment.CHECKPOINTS) {
            int checkpoints = v.getCheckpoints();
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            tg.sendMessage(PREFIX + "Grund: " + v.getDescription());
            Checkpoints.setCheckpoints(tg, checkpoints, true);
            Log.WARNING.write(p, "hat " + Script.getName(tg) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Log.HIGH.write(tg, "wurde von " + tg.getName() + " zu " + checkpoints + " Checkpoints eingesperrt.");
            if(v != Violation.EROTIK_ROLEPLAY) Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(tg) + " wurde von " + Messages.RANK_PREFIX(p) + " wegen §l" + v.getName() + "§c zu " + v.getCheckpoints() + " Checkpoints eingesperrt.");
            Checkpoints.start(tg, checkpoints);
        }
    }

    public static void punish(Player p, OfflinePlayer tg, Violation v) {
        new Straflog(tg, p, v);
        Punishment punishment = v.getPunishment();
        Punishment secondaryPunishment = v.getSecondaryPunishment();
        Date until = new Date(new Date().getTime() + v.getDuration());
        long untilLong = new Date().getTime() + v.getDuration();
        if (punishment == Punishment.BAN || secondaryPunishment == Punishment.BAN) {
            if (v.getDuration() == 0) {
                if(getBanUntil(tg) == 0) {
                    p.sendMessage(Messages.ERROR + "Der Spieler hat bereits einen permanenten Bann!");
                    return;
                }

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
            if(Script.hasRank(p, Rank.SUPPORTER, false)) {
                Script.executeUpdate("DELETE FROM ranks WHERE nrp_id=" + Script.getNRPID(tg));
                Script.executeAsyncUpdate("DELETE FROM ticket_greeting WHERE nrp_id=" + Script.getNRPID(tg));
                Script.executeAsyncUpdate("DELETE FROM ticket_farewell WHERE nrp_id=" + Script.getNRPID(tg));
                Script.executeAsyncUpdate("DELETE from notifications WHERE nrp_id=" + Script.getNRPID(tg));
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
                Script.addOfflineMessage(tg, "Du wurdest von " + Script.getName(p) + " für §l" + v.getName() + "§c gemutet.");
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
            if (getWarns(tg) >= 3) {
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + generatePunishID() + "', '" + Script.getNRPID(tg) + "', '" + System.currentTimeMillis() + "', NULL, 'maximale Anzahl an Warns überschritten', '0');");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + tg.getName() + " wurde automatisch gebannt (3/3 Warns)");
            }
        }

        if (punishment == Punishment.TRAGEN_SPERRE || secondaryPunishment == Punishment.TRAGEN_SPERRE) {
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " eine Tragensperre für " + 180 + " Minuten gegeben.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + tg.getName() + " eine Tragensperre für " + 180 + " Minuten gegeben.", true);
            Log.WARNING.write(tg, "hat von " + Messages.RANK_PREFIX(p) + " eine Tragensperre für " + 180 + " Minuten bekommen.");
            Log.HIGH.write(p, "hat " + tg.getName() + " eine Tragensperre für " + 180 + " Minuten gegeben.");
            Sperre.TRAGENSPERRE.setSperre(Script.getNRPID(tg), 180);
        }


        if (punishment == Punishment.CHECKPOINTS || secondaryPunishment == Punishment.CHECKPOINTS) {
            int checkpoints = v.getCheckpoints();
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Script.addOfflineMessage(tg, PREFIX + "Du wurdest von " + Script.getName(p) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Script.addOfflineMessage(tg, PREFIX + "Grund: " + v.getDescription());
            Script.executeAsyncUpdate("INSERT INTO checkpoints (id, checkpoints) VALUES (" + Script.getNRPID(tg) + ", " + checkpoints + ") ON DUPLICATE KEY UPDATE checkpoints = checkpoints + " + checkpoints + ";");
            Log.WARNING.write(p, "hat " + tg.getName() + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Log.HIGH.write(tg, "wurde von " + Messages.RANK_PREFIX(p) + " zu " + checkpoints + " Checkpoints eingesperrt.");
            Bukkit.broadcastMessage(Script.PREFIX + "§c" + tg.getName() + " wurde von " + Messages.RANK_PREFIX(p) + " wegen §l" + v.getName() + "§c zu " + v.getCheckpoints() + " Checkpoints eingesperrt.");
        }
    }


    public static boolean isMuted(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
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
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ban WHERE nrp_id=" + Script.getNRPID(p) + " ORDER BY id DESC LIMIT 1;")) {
            if (rs.next()) {
                return rs.getString("reason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (getBanUntil(p) == -1) return;
        if (getBanUntil(p) > System.currentTimeMillis() || getBanUntil(p) == 0) {
            p.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cBann §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gebannt§8.\n\n§7Grund §8× §e" + getBanReason(p) + "\n§7Gebannt bis §8× §e" + (getBanUntil(p) != 0 ? Script.dateFormat.format(getBanUntil(p)) : "Lebenslang") + "\n\n§7Einen Entbannungsantrag kannst du auf der Webseite stellen.\n\n§8§m------------------------------");
        }
        Notifications.sendMessage(Notifications.NotificationType.JOIN, "§e" + Script.getName(e.getPlayer()) + " §7hat den Server betreten.");
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
