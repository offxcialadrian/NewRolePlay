package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.newrp.Berufe.Beruf.Berufe.*;

public class Abteilung implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§8[§eAbteilung§8] §e» ";

    public enum Abteilungen {
        GOVERNMENT_NONE(0, GOVERNMENT, "Regierungsmitglied"),
        INNENMINISTERIUM(2, GOVERNMENT, "Innenministerium"),
        FINANZAMT(3, GOVERNMENT, "Finanzministerium"),
        JUSTIZMINISTERIUM(4, GOVERNMENT, "Justizministerium"),
        VIZEKANZLER(5, GOVERNMENT, "Vizekanzler"),
        STAATSOBERHAUPT(6, GOVERNMENT, "Bundeskanzler"),
        STREIFENPOLIZEI(0, POLICE, "Streifenpolizist"),
        ZIVILPOLIZEI(1, POLICE, "Zivilpolizei"),
        KRIMINALPOLIZEI(2, POLICE, "Kriminalpolizei"),
        SEK(3, POLICE, "SEK"),
        AUSBILDER(4, POLICE, "Ausbilder"),
        PRESSE(5, POLICE, "Presse"),
        ABTEILUNGSLEITUNG(6, POLICE, "Abteilungsleiter"),
        POLIZEIVIZE(7, POLICE, "Polizeivizepräsident"),
        POLIZEIPRÄSIDENT(8, POLICE, "Polizeipräsident"),
        VOLONTAER(0, NEWS, "Volontäre"),
        JOURNALIST(1, NEWS, "Journalismus"),
        TV(2, NEWS, "TV-Moderation"),
        CHEFREDAKTION(3, NEWS, "Chefredaktion"),
        MEDIZINSTUDENT(0, RETTUNGSDIENST, "Medizinstudent"),
        ASSISTENZARZT(1, RETTUNGSDIENST, "Assistenzarzt"),
        ORTHOPAEDIE(2, RETTUNGSDIENST, "Orthopädie"),
        CHIRURGIE(3, RETTUNGSDIENST, "Chirurgie"),
        NOTFALLMEDIZIN(4, RETTUNGSDIENST, "Notfallmedizin"),
        ALLGEMEINMEDIZIN(5, RETTUNGSDIENST, "Allgemeinmedizin"),
        OBERARZT(6, RETTUNGSDIENST, "Oberarzt"),
        CHEFARZT(7, RETTUNGSDIENST, "Chefarzt"),
        DIREKTOR(8, RETTUNGSDIENST, "Ärztliches-Direktorium"),
        FEUERWEHR(9, RETTUNGSDIENST, "Feuerwehr"),
        Agent(0, BUNDESNACHRICHTENDIENST, "Agent"),
        ANALYST(1, BUNDESNACHRICHTENDIENST, "Analyst"),
        OPERATOR(2, BUNDESNACHRICHTENDIENST, "Operator"),
        LEITER(3, BUNDESNACHRICHTENDIENST, "Leiter"),
        VIZEPRÄSIDENT(4, BUNDESNACHRICHTENDIENST, "Vizepräsident"),
        PRÄSIDENT(5, BUNDESNACHRICHTENDIENST, "Präsident");


        private final int id;
        private final Beruf.Berufe beruf;
        private final String name;

        Abteilungen(int id, Beruf.Berufe beruf, String name) {
            this.id = id;
            this.beruf = beruf;
            this.name = name;
        }

        public int getID() {
            return id;
        }

        public Beruf.Berufe getBeruf() {
            return beruf;
        }

        public String getName() {
            return name.replace("_", " ");
        }

        public String getArgName() {
            return name;
        }

        public static Abteilungen getAbteilung(int id, Beruf.Berufe beruf) {
            for (Abteilungen abteilung : Abteilungen.values()) {
                if (abteilung.getID() == id && abteilung.getBeruf() == beruf) {
                    return abteilung;
                }
            }
            return null;
        }

        public static Abteilungen getAbteilung(String name, Beruf.Berufe beruf) {
            for (Abteilungen abteilung : Abteilungen.values()) {
                if (abteilung.getArgName().equalsIgnoreCase(name) && abteilung.getBeruf() == beruf) {
                    return abteilung;
                }
            }
            return null;
        }

        public static List<Abteilungen> getAbteilungen(Beruf.Berufe beruf) {
            List<Abteilungen> abteilungen = new ArrayList<>();
            for (Abteilungen abteilung : Abteilungen.values()) {
                if (abteilung.getBeruf() == beruf) {
                    abteilungen.add(abteilung);
                }
            }
            return abteilungen;
        }

        public List<Player> getOnlineMembers() {
            List<Player> list = new ArrayList<>();
            for (Player all : Bukkit.getOnlinePlayers()) {
                if(Beruf.getAbteilung(all) == this)
                    list.add(all.getPlayer());
            }
            return list;
        }

        public boolean hasAbteilung(OfflinePlayer p) {
            return Script.getInt(p, "berufe", "abteilung") == this.getID();
        }

        public void sendMessage(String message) {
            for (OfflinePlayer all : getOnlineMembers()) {
                if (all.isOnline()) {
                    all.getPlayer().sendMessage(message);
                }
            }
        }

    }



    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (args.length > 2 || args.length == 0) {
            p.sendMessage(Messages.ERROR + "/abteilung [Spieler] [Abteilung]");
            return true;
        }

        if (!Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        if(args.length == 1) {

            if(args[0].equalsIgnoreCase("list")) {
                p.sendMessage(PREFIX + "Alle Abteilungen der " + Beruf.getBeruf(p).getName() + ":");
                for(Abteilung.Abteilungen abteilung : Abteilung.Abteilungen.getAbteilungen(Beruf.getBeruf(p))) {
                    p.sendMessage("§8» §6" + abteilung.getName());
                }
                return true;
            }

            OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
            if(Script.getNRPID(args[0]) != 0) {
                p.sendMessage(PREFIX + "§6" + tg.getName() + " ist in der Abteilung §6" + Beruf.getAbteilung(tg).getName());
                return true;
            }

            if(Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                if(!SDuty.isSDuty(p)) {
                    p.sendMessage(Messages.NO_SDUTY);
                    return true;
                }

                Beruf.Berufe b = Beruf.Berufe.getBeruf(args[0]);
                if(b == null) {
                    try {
                        b = Beruf.Berufe.getBeruf(Integer.parseInt(args[0]));
                    } catch (NumberFormatException e) {
                        p.sendMessage(Messages.ERROR + "Der Beruf wurde nicht gefunden.");
                        return true;
                    }

                }

                p.sendMessage(PREFIX + "Alle Abteilungen der " + b.getName() + ":");
                for(OfflinePlayer all : b.getAllMembers()) {
                    p.sendMessage("§8» §6" + all.getName() + "§8: §6" + Beruf.getAbteilung(all).getName());
                }
                return true;
            } else if(Beruf.isLeader(p, true)){
                p.sendMessage(Messages.ERROR + "/abteilung [Spieler] [Abteilung]");
                return true;
            }
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(Beruf.getBeruf(tg) != Beruf.getBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deinem Beruf.");
            return true;
        }

        Abteilungen abteilung = Abteilungen.getAbteilung(args[1], Beruf.getBeruf(p));
        if(abteilung == null) {
            try {
                abteilung = Abteilungen.getAbteilung(Integer.parseInt(args[1]), Beruf.getBeruf(p));
            } catch (NumberFormatException e) {
                p.sendMessage(Messages.ERROR + "Die Abteilung wurde nicht gefunden.");
                return true;
            }
        }

        Script.setInt(tg, "berufe", "abteilung", abteilung.getID());
        p.sendMessage(PREFIX + "Du hast die Abteilung von " + Script.getName(tg) + " auf " + abteilung.getName() + " gesetzt.");
        if(p!=tg) tg.sendMessage(PREFIX + "Deine Abteilung wurde von " + Script.getName(p) + " auf " + abteilung.getName() + " gesetzt.");
        Beruf.getBeruf(p).sendLeaderMessage("§8[§e" + Beruf.getBeruf(p).getName() + "§8] §e» " + "Die Abteilung von " + Script.getName(tg) + " wurde von " + Script.getName(p) + " auf " + abteilung.getName() + " gesetzt.");
        Script.sendTeamMessage(PREFIX + "Die Abteilung von " + Script.getName(tg) + " wurde von " + Script.getName(p) + " auf " + abteilung.getName() + " gesetzt. [" + Beruf.getBeruf(p).getName() + "]");


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("abteilung")) {
            if(!Beruf.isLeader(p, true) && !SDuty.isSDuty(p)) return Collections.EMPTY_LIST;
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Abteilungen abteilung : Abteilungen.getAbteilungen(Beruf.getBeruf(p))) {
                oneArgList.add(abteilung.getArgName());
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

}