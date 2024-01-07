package de.newrp.Government;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.API.VertragAPI;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Player.Vertrag;
import de.newrp.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Erstattung implements CommandExecutor {

    public static String generateAktenzeichen() {
        String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(possible.charAt((int) Math.floor(Math.random() * possible.length())));
        }
        return sb.toString();
    }

    public static List<String> getAktenzeichen() {
        List<String> list = new ArrayList<>();
        try (PreparedStatement statement = main.getConnection().prepareStatement(
                "SELECT aktenzeichen FROM erstattung")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("aktenzeichen"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static VertragAPI getVertragByAktenzeichen(String aktenzeichen) {
        try (PreparedStatement statement = main.getConnection().prepareStatement(
                "SELECT aktenzeichen FROM erstattung WHERE aktenzeichen = ?")) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return VertragAPI.getVertrag(rs.getInt("vertragID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getErstattungByAktenzeichen(String aktenzeichen) {
        try (PreparedStatement statement = main.getConnection().prepareStatement(
                "SELECT * FROM erstattung WHERE aktenzeichen = ?")) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("erstattung");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getErstattungForByAktenzeichen(String aktenzeichen) {
        try (PreparedStatement statement = main.getConnection().prepareStatement(
                "SELECT * FROM erstattung WHERE aktenzeichen = ?")) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("userID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE) {
            if (Beruf.getAbteilung(p) != Abteilung.Abteilungen.KRIPO) {
                p.sendMessage(Messages.ERROR + "Nur die Kriminalpolizei kann Erstattungen aufnehmen.");
                return true;
            }

            if(!Duty.isInDuty(p)) {
                p.sendMessage(Messages.ERROR + "Du musst im Dienst sein.");
                return true;
            }

            if (args.length < 3) {
                p.sendMessage(Messages.ERROR + "/erstattung [Spieler] [Summe] [Vertrag-ID]");
                return true;
            }

            Player tg = Script.getPlayer(args[0]);
            if (tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            int summe;
            try {
                summe = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(Messages.ERROR + "Die Summe muss eine Zahl sein.");
                return true;
            }

            int id;
            try {
                id = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                p.sendMessage(Messages.ERROR + "Die Vertrag-ID muss eine Zahl sein.");
                return true;
            }

            VertragAPI v = VertragAPI.getVertrag(id);
            if (v == null) {
                p.sendMessage(Messages.ERROR + "Der Vertrag wurde nicht gefunden.");
                return true;
            }

            if (v.getFrom() != Script.getNRPID(tg)) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht der Vertragsinhaber.");
                return true;
            }

            if (summe <= 0) {
                p.sendMessage(Messages.ERROR + "Die Summe muss größer als 0€ sein.");
                return true;
            }

            String aktenzeichen = generateAktenzeichen();
            Script.executeAsyncUpdate("INSERT INTO erstattung (userID, erstattung, vertragID, time, aktenzeichen) VALUES (" + Script.getNRPID(tg) + ", " + summe + ", " + id + ", " + System.currentTimeMillis() + ", '" + aktenzeichen + "')");
            p.sendMessage(Vertrag.PREFIX + "Du hast die Erstattung aufgenommen.");
            p.sendMessage(Vertrag.PREFIX + "§6Aktenzeichen: " + aktenzeichen);
            tg.sendMessage(Vertrag.PREFIX + "§6Deine Erstattung wurde von " + Script.getName(p) + " aufgenommen.");
            Beruf.Berufe.GOVERNMENT.sendMessage(Vertrag.PREFIX + "§6" + Script.getName(p) + " hat einen Erstattungsantrag gestellt.");
            return true;
        }

        if (Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) {
            if (Beruf.getAbteilung(p) != Abteilung.Abteilungen.FINANZAMT && !Beruf.isLeader(p, true)) {
                p.sendMessage(Messages.ERROR + "Nur das Finanzamt kann Erstattungen bearbeiten.");
                return true;
            }

            if (args.length == 0) {
                if (getAktenzeichen().isEmpty()) {
                    p.sendMessage(Vertrag.PREFIX + "Derzeit gibt es keine Anträge.");
                    return true;
                }

                p.sendMessage(Vertrag.PREFIX + "§6Aktenzeichen:");
                for (String aktenzeichen : getAktenzeichen()) {
                    p.sendMessage(Vertrag.PREFIX + "§8× §6" + aktenzeichen);
                }
                return true;

            } else if(args.length == 1) {
                String aktenzeichen = args[0];
                VertragAPI v = getVertragByAktenzeichen(aktenzeichen);
                if(v == null) {
                    p.sendMessage(Messages.ERROR + "Der Antrag wurde nicht gefunden.");
                    p.sendMessage(Messages.INFO + "Nutze /erstattung [Aktenzeichen] um ein Fall genauer anzusehen.");
                    return true;
                }

                p.sendMessage(Vertrag.PREFIX + "§8=== §6" + aktenzeichen + " §8===");
                p.sendMessage(Vertrag.PREFIX + "§6Vertrags-ID: " + v.getID());
                p.sendMessage(Vertrag.PREFIX + "§6Vertrag von: " + (Script.getOfflinePlayer(v.getFrom())).getName());
                p.sendMessage(Vertrag.PREFIX + "§6Vertrag an: " + (Script.getOfflinePlayer(v.getTo())).getName());
                p.sendMessage(Vertrag.PREFIX + "§6Inhalt: " + v.getBedingung());
                p.sendMessage(Vertrag.PREFIX + "§6Datum: " + Script.dateFormat.format(v.getTime()) + " Uhr");
                p.sendMessage(Vertrag.PREFIX + "§6Erstattung: " + getErstattungByAktenzeichen(aktenzeichen) + "€");
                p.sendMessage(Vertrag.PREFIX + "§6Erstattung an: " + (Script.getOfflinePlayer(getErstattungForByAktenzeichen(aktenzeichen))).getName());
                return true;

            } else if(args.length == 2) {
                if(!Beruf.isLeader(p, true)) {
                    p.sendMessage(Messages.ERROR + "Nur das Staatsoberhaupt kann Erstattungen bearbeiten.");
                    return true;
                }
                String aktenzeichen = args[0];
                VertragAPI v = getVertragByAktenzeichen(aktenzeichen);
                if(v == null) {
                    p.sendMessage(Messages.ERROR + "Der Antrag wurde nicht gefunden.");
                    p.sendMessage(Messages.INFO + "Nutze /erstattung [Aktenzeichen] um ein Fall genauer anzusehen.");
                    return true;
                }

                if(!args[1].equalsIgnoreCase("annehmen") && !args[1].equalsIgnoreCase("ablehnen")) {
                    p.sendMessage(Messages.ERROR + "/erstattung [Aktenzeichen] [annehmen/ablehnen]");
                    return true;
                }

                if(args[1].equalsIgnoreCase("annehmen")) {
                    Script.executeAsyncUpdate("DELETE FROM erstattung WHERE aktenzeichen = '" + aktenzeichen + "'");
                    Beruf.Berufe.GOVERNMENT.sendMessage(Vertrag.PREFIX + "§6" + Script.getName(p) + " hat eine Erstattung in Höhe von " + getErstattungByAktenzeichen(aktenzeichen) + "€ an " + (Script.getOfflinePlayer(getErstattungForByAktenzeichen(aktenzeichen))).getName() + " genehmigt.");
                    Stadtkasse.removeStadtkasse(getErstattungByAktenzeichen(aktenzeichen));
                    Script.addMoney(getErstattungForByAktenzeichen(aktenzeichen), PaymentType.BANK, getErstattungByAktenzeichen(aktenzeichen));
                    if(Script.getOfflinePlayer(getErstattungForByAktenzeichen(aktenzeichen)) != null) {
                        Script.getPlayer(getErstattungForByAktenzeichen(aktenzeichen)).sendMessage(Vertrag.PREFIX + "Deine Erstattung wurde genehmigt.");
                    } else {
                        Script.addOfflineMessage(getErstattungForByAktenzeichen(aktenzeichen), Vertrag.PREFIX + "Deine Erstattung wurde genehmigt.");
                    }
                    return true;
                } else if(args[1].equalsIgnoreCase("ablehnen")) {
                    Script.executeAsyncUpdate("DELETE FROM erstattung WHERE aktenzeichen = '" + aktenzeichen + "'");
                    Beruf.Berufe.GOVERNMENT.sendMessage(Vertrag.PREFIX + "§6" + Script.getName(p) + " hat eine Erstattung in Höhe von " + getErstattungByAktenzeichen(aktenzeichen) + "€ an " + (Script.getOfflinePlayer(getErstattungForByAktenzeichen(aktenzeichen))).getName() + " abgelehnt.");
                    if(Script.getOfflinePlayer(getErstattungForByAktenzeichen(aktenzeichen)) != null) {
                        Script.getPlayer(getErstattungForByAktenzeichen(aktenzeichen)).sendMessage(Vertrag.PREFIX + "Deine Erstattung wurde abgelehnt.");
                    } else {
                        Script.addOfflineMessage(getErstattungForByAktenzeichen(aktenzeichen), Vertrag.PREFIX + "Deine Erstattung wurde abgelehnt.");
                    }
                    return true;
                }

            }
        }


        return false;
    }
}

