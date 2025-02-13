package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Wahlen;
import de.newrp.House.House;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.Banken;
import de.newrp.Police.Fahndung;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Map;

public class CheckPlayerCommand implements CommandExecutor {

    DecimalFormat df = new DecimalFormat("#.#");

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (Script.getRank(p) == Rank.DEVELOPER) {
            if(args.length == 0) {
                p.sendMessage(Messages.ERROR + "/checkplayer [Spieler]");
                return true;
            }

            OfflinePlayer offtg = Script.getOfflinePlayer(args[0]);
            if(offtg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            p.sendMessage("§e§l=== §6" + offtg.getName() + " §e§l===");
            p.sendMessage("§7ID §8× §e" + Script.getNRPID(offtg));

        }

        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
            if (Script.getRank(p) != Rank.DEVELOPER)
                p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/checkplayer [Spieler]");
            return true;
        }

        if (Script.isInt(args[0]) && Script.getOfflinePlayer(Integer.parseInt(args[0])) != null) {
            OfflinePlayer offtg = Script.getOfflinePlayer(Integer.parseInt(args[0]));
            if (!offtg.isOnline()) {
                p.sendMessage("§e§l=== §6" + offtg.getName() + " §8(§cOffline§8) §e§l===");
                p.sendMessage("§7ID §8× §e" + Script.getNRPID(offtg));
                if (Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    p.sendMessage("§7BackUpCode §8× §e" + (Script.getBackUpCode(offtg) == null ? "Kein BackUpCode" : Script.getBackUpCode(offtg)));
                }
                if (Punish.getBanUntil(offtg) > System.currentTimeMillis() || Punish.getBanUntil(offtg) == 0)
                    p.sendMessage("§cGebannt §8× §c" + (Punish.getBanUntil(offtg) != 0 ? "bis " + Script.dateFormat.format(Punish.getBanUntil(offtg)) + " Uhr" : "Lebenslang") + " §8(§c" + Punish.getBanReason(offtg) + "§8)");
                if (Checkpoints.hasCheckpoints(offtg)) {
                    p.sendMessage("§7Checkpoints §8× §e" + Checkpoints.getCheckpoints(offtg));
                }
                if (Wahlen.wahlenActive()) {
                    p.sendMessage("§7Gewählt §8× §e" + (Wahlen.hasVoted(offtg) ? (Script.hasRank(p, Rank.ADMINISTRATOR, false) ? Script.getOfflinePlayer(Wahlen.getVote(offtg)).getName() : "Ja") : "Nein"));
                }
                p.sendMessage("§7Premium §8× §e" + (Premium.hasPremium(offtg) ? "Ja §8(§e" + Script.dateFormat.format(Premium.getPremiumTime(offtg)) + " Uhr§8)" : "Nein"));
                p.sendMessage("§7Warns §8× §e" + Punish.getWarns(offtg) + "/3");
                if (!Punish.getWarnsMap(offtg).isEmpty()) {
                    for (Map.Entry<Long, String> entry : Punish.getWarnsMap(offtg).entrySet()) {
                        p.sendMessage("  §7» §e" + Script.dateFormat2.format(entry.getKey()) + " §8× §e" + entry.getValue());
                    }
                }
                p.sendMessage("§7Offline §8× §eseit: " + Script.dateFormat.format(Script.getLastDisconnect(offtg)) + " Uhr");
                p.sendMessage("§7Geld §8× §e" + Script.getMoney(offtg, PaymentType.CASH) + "€ | " + Script.getMoney(offtg, PaymentType.BANK) + "€" + (Banken.hasBank(offtg) ? " §8(§e" + Banken.getBankByPlayer(offtg).getName() + "§8)" : " §8(§c" + "Keine Bank" + "§8)"));
                p.sendMessage("§7Fahndung §8× §e" + (Fahndung.isFahnded(offtg) ? Fahndung.getWanteds(offtg) + " WantedPunkte" : "Nein"));
                p.sendMessage("§7Geschlecht §8× §e" + Script.getGender(offtg).getName());
                p.sendMessage("§7Team §8× §e" + (Team.getTeam(offtg) != null ? Team.getTeam(offtg).getName() + (Team.isTeamLeader(p) ? " §8(§eTL§8)" : "") : "Kein Team"));
                p.sendMessage("§7PlayTime §8× §e" + Script.getPlayTime(offtg, true) + ":§e" + String.format("%02d", Script.getPlayTime(offtg, false)) + " Stunden" + " §8(§e" + Script.getActivePlayTime(offtg, true) + ":§e" + String.format("%02d", Script.getActivePlayTime(offtg, false)) + " Stunden§8)");
                p.sendMessage("§7Level §8× §e" + Script.getLevel(offtg));
                p.sendMessage("§7Beruf §8× §e" + (Beruf.hasBeruf(offtg) ? Beruf.getBeruf(offtg).getName() : "Kein Beruf") + (Beruf.isLeader(offtg, true) ? " §8(§eLeader§8)" : ""));
                p.sendMessage("§7Organisation §8× §e" + (Organisation.hasOrganisation(offtg) ? Organisation.getOrganisation(offtg).getName() + (Organisation.isLeader(offtg, true) ? " §8(§eLeader§8)" : " §8(§e" + Organisation.getRankName(offtg) + "§8)") : "Keine Organisation"));
                StringBuilder houses = new StringBuilder();
                for (House house : House.getHouses(Script.getNRPID(offtg))) {
                    if (House.getHouses(Script.getNRPID(offtg)).size() == 1) {
                        houses.append("Haus " + house.getID());
                        break;
                    }
                    houses.append(house.getID()).append(house.getID() == House.getHouses(Script.getNRPID(offtg)).get(House.getHouses(Script.getNRPID(offtg)).size() - 1).getID() ? "" : ", ");
                }
                if (houses.toString().equalsIgnoreCase("")) houses = new StringBuilder("Keine Häuser");
                p.sendMessage("§7Häuser §8× §e" + houses);
                return true;
            }

            Player tg = Script.getPlayer(Integer.parseInt(args[0]));
            p.sendMessage("§e§l=== §6" + Script.getName(tg) + " §e§l===");
            p.sendMessage("§7ID §8× §e" + Script.getNRPID(tg));
            if (Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                p.sendMessage("§7BackUpCode §8× §e" + (Script.getBackUpCode(tg) == null ? "Kein BackUpCode" : Script.getBackUpCode(tg)));
                p.sendMessage("§7IP §8× §e" + tg.getAddress().getAddress().getHostAddress());
            }
            p.sendMessage("§7Warns §8× §e" + Punish.getWarns(tg) + "/3");
            if (Wahlen.wahlenActive()) {
                p.sendMessage("§7Gewählt §8× §e" + (Wahlen.hasVoted(tg) ? (Script.hasRank(p, Rank.ADMINISTRATOR, false) ? Script.getOfflinePlayer(Wahlen.getVote(tg)).getName() : "Ja") : "Nein"));
            }
            if (!Punish.getWarnsMap(tg).isEmpty()) {
                for (Map.Entry<Long, String> entry : Punish.getWarnsMap(tg).entrySet()) {
                    p.sendMessage("  §7» §e" + Script.dateFormat2.format(entry.getKey()) + " §8× §e" + entry.getValue());
                }
            }
            if (Checkpoints.hasCheckpoints(tg)) {
                p.sendMessage("§7Checkpoints §8× §e" + Checkpoints.getCheckpoints(tg) + " §8(§e" + (int) Checkpoints.getLocation(tg).getX() + "§8/§e" + (int) Checkpoints.getLocation(tg).getY() + "§8/§e" + (int) Checkpoints.getLocation(tg).getZ() + "§8)");
            }
            p.sendMessage("§7Rang §8× §e" + Script.getRank(tg).getName(tg));
            p.sendMessage("§7Premium §8× §e" + (Premium.hasPremium(tg) ? "Ja §8(§e" + Script.dateFormat2.format(Premium.getPremiumTime(tg)) + "§8)" : "Nein"));
            p.sendMessage("§7Geld §8× §e" + Script.getMoney(tg, PaymentType.CASH) + "€ | " + Script.getMoney(tg, PaymentType.BANK) + "€" + (Banken.hasBank(tg) ? " §8(§e" + Banken.getBankByPlayer(tg).getName() + "§8)" : " §8(§c" + "Keine Bank" + "§8)"));
            p.sendMessage("§7Fahndung §8× §e" + (Fahndung.isFahnded(tg) ? Fahndung.getWanteds(tg) + " WantedPunkte" : "Nein"));
            p.sendMessage("§7UUID §8× §e" + tg.getUniqueId());
            p.sendMessage("§7Geschlecht §8× §e" + Script.getGender(tg).getName());
            p.sendMessage("§7Team §8× §e" + (Team.getTeam(tg) != null ? Team.getTeam(tg).getName() : "Kein Team"));
            p.sendMessage("§7PlayTime §8× §e" + Script.getPlayTime(tg, true) + ":§e" + String.format("%02d", Script.getPlayTime(tg, false)) + " Stunden" + " §8(§e" + Script.getActivePlayTime(tg, true) + ":§e" + String.format("%02d", Script.getActivePlayTime(tg, false)) + " Stunden§8)");
            p.sendMessage("§7PayDay §8× §e" + PayDay.getPayDayTime(tg) + "/60 Minuten");
            p.sendMessage("§7Level §8× §e" + Script.getLevel(tg));
            p.sendMessage("§7Exp §8× §e" + Script.getExp(tg) + "/" + Script.getLevelCost(tg) + " Exp §8(§e" + Script.getPercentage(Script.getExp(tg), Script.getLevelCost(tg)) + "%§8)");
            p.sendMessage("§7Health §8× §e" + df.format(tg.getHealth() / 2) + "/" + df.format(tg.getMaxHealth() / 2) + " HP");
            p.sendMessage("§7Beruf §8× §e" + (Beruf.hasBeruf(tg) ? Beruf.getBeruf(tg).getName() : "Kein Beruf") + (Beruf.isLeader(tg, true) ? " §8(§eLeader§8)" : ""));
            p.sendMessage("§7Organisation §8× §e" + (Organisation.hasOrganisation(tg) ? Organisation.getOrganisation(tg).getName() + (Organisation.isLeader(tg, true) ? " §8(§eLeader§8)" : " §8(§e" + Organisation.getRankName(tg) + "§8)") : "Keine Organisation"));
            StringBuilder houses = new StringBuilder();
            for (House house : House.getHouses(Script.getNRPID(tg))) {
                if (House.getHouses(Script.getNRPID(tg)).size() == 1) {
                    houses.append("Haus " + house.getID());
                    break;
                }
                houses.append(house.getID()).append(house.getID() == House.getHouses(Script.getNRPID(tg)).get(House.getHouses(Script.getNRPID(tg)).size() - 1).getID() ? "" : ", ");
            }
            if (houses.toString().equalsIgnoreCase("")) houses = new StringBuilder("Keine Häuser");
            p.sendMessage("§7Häuser §8× §e" + houses);
            return false;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null && Script.getNRPID(args[0]) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (tg == null && Script.getNRPID(args[0]) != 0) {
            OfflinePlayer offtg = Script.getOfflinePlayer(Script.getNRPID(args[0]));
            p.sendMessage("§e§l=== §6" + offtg.getName() + " §8(§cOffline§8) §e§l===");
            p.sendMessage("§7ID §8× §e" + Script.getNRPID(offtg));
            if (Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                p.sendMessage("§7BackUpCode §8× §e" + (Script.getBackUpCode(offtg) == null ? "Kein BackUpCode" : Script.getBackUpCode(offtg)));
            }
            if (Punish.getBanUntil(offtg) > System.currentTimeMillis() || Punish.getBanUntil(offtg) == 0)
                p.sendMessage("§cGebannt §8× §c" + (Punish.getBanUntil(offtg) != 0 ? "bis " + Script.dateFormat.format(Punish.getBanUntil(offtg)) + " Uhr" : "Lebenslang") + " §8(§c" + Punish.getBanReason(offtg) + "§8)");
            if (Checkpoints.hasCheckpoints(offtg)) {
                p.sendMessage("§7Checkpoints §8× §e" + Checkpoints.getCheckpoints(offtg));
            }
            if (Wahlen.wahlenActive()) {
                p.sendMessage("§7Gewählt §8× §e" + (Wahlen.hasVoted(offtg) ? (Script.hasRank(p, Rank.ADMINISTRATOR, false) ? Script.getOfflinePlayer(Wahlen.getVote(offtg)).getName() : "Ja") : "Nein"));
            }
            p.sendMessage("§7Premium §8× §e" + (Premium.hasPremium(offtg) ? "Ja §8(§e" + Script.dateFormat.format(Premium.getPremiumTime(offtg)) + " Uhr§8)" : "Nein"));
            p.sendMessage("§7Warns §8× §e" + Punish.getWarns(offtg) + "/3");
            if (!Punish.getWarnsMap(offtg).isEmpty()) {
                for (Map.Entry<Long, String> entry : Punish.getWarnsMap(offtg).entrySet()) {
                    p.sendMessage("  §7» §e" + Script.dateFormat2.format(entry.getKey()) + " §8× §e" + entry.getValue());
                }
            }
            p.sendMessage("§7Offline §8× §eseit: " + Script.dateFormat.format(Script.getLastDisconnect(offtg)) + " Uhr");
            p.sendMessage("§7Geld §8× §e" + Script.getMoney(offtg, PaymentType.CASH) + "€ | " + Script.getMoney(offtg, PaymentType.BANK) + "€" + (Banken.hasBank(offtg) ? " §8(§e" + Banken.getBankByPlayer(offtg).getName() + "§8)" : " §8(§c" + "Keine Bank" + "§8)"));
            p.sendMessage("§7Fahndung §8× §e" + (Fahndung.isFahnded(offtg) ? Fahndung.getWanteds(offtg) + " WantedPunkte" : "Nein"));
            p.sendMessage("§7Geschlecht §8× §e" + Script.getGender(offtg).getName());
            p.sendMessage("§7Team §8× §e" + (Team.getTeam(offtg) != null ? Team.getTeam(offtg).getName() + (Team.isTeamLeader(p) ? " §8(§eTL§8)" : "") : "Kein Team"));
            p.sendMessage("§7PlayTime §8× §e" + Script.getPlayTime(offtg, true) + ":§e" + String.format("%02d", Script.getPlayTime(offtg, false)) + " Stunden" + " §8(§e" + Script.getActivePlayTime(offtg, true) + ":§e" + String.format("%02d", Script.getActivePlayTime(offtg, false)) + " Stunden§8)");
            p.sendMessage("§7Level §8× §e" + Script.getLevel(offtg));
            p.sendMessage("§7Beruf §8× §e" + (Beruf.hasBeruf(offtg) ? Beruf.getBeruf(offtg).getName() : "Kein Beruf") + (Beruf.isLeader(offtg, true) ? " §8(§eLeader§8)" : ""));
            p.sendMessage("§7Organisation §8× §e" + (Organisation.hasOrganisation(offtg) ? Organisation.getOrganisation(offtg).getName() + (Organisation.isLeader(offtg, true) ? " §8(§eLeader§8)" : " §8(§e" + Organisation.getRankName(offtg) + "§8)") : "Keine Organisation"));
            StringBuilder houses = new StringBuilder();
            for (House house : House.getHouses(Script.getNRPID(offtg))) {
                if (House.getHouses(Script.getNRPID(offtg)).size() == 1) {
                    houses.append("Haus " + house.getID());
                    break;
                }
                houses.append(house.getID()).append(house.getID() == House.getHouses(Script.getNRPID(offtg)).get(House.getHouses(Script.getNRPID(offtg)).size() - 1).getID() ? "" : ", ");
            }
            if (houses.toString().equalsIgnoreCase("")) houses = new StringBuilder("Keine Häuser");
            p.sendMessage("§7Häuser §8× §e" + houses);
            return true;
        }

        p.sendMessage("§e§l=== §6" + Script.getName(tg) + " §e§l===");
        p.sendMessage("§7ID §8× §e" + Script.getNRPID(tg));
        if (Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage("§7BackUpCode §8× §e" + (Script.getBackUpCode(tg) == null ? "Kein BackUpCode" : Script.getBackUpCode(tg)));
            p.sendMessage("§7IP §8× §e" + tg.getAddress().getAddress().getHostAddress());
        }
        p.sendMessage("§7Warns §8× §e" + Punish.getWarns(tg) + "/3");
        if (Wahlen.wahlenActive()) {
            p.sendMessage("§7Gewählt §8× §e" + (Wahlen.hasVoted(tg) ? (Script.hasRank(p, Rank.ADMINISTRATOR, false) ? Script.getOfflinePlayer(Wahlen.getVote(tg)).getName() : "Ja") : "Nein"));
        }
        if (!Punish.getWarnsMap(tg).isEmpty()) {
            for (Map.Entry<Long, String> entry : Punish.getWarnsMap(tg).entrySet()) {
                p.sendMessage("  §7» §e" + Script.dateFormat2.format(entry.getKey()) + " §8× §e" + entry.getValue());
            }
        }
        if (Checkpoints.hasCheckpoints(tg)) {
            p.sendMessage("§7Checkpoints §8× §e" + Checkpoints.getCheckpoints(tg) + " §8(§e" + (int) Checkpoints.getLocation(tg).getX() + "§8/§e" + (int) Checkpoints.getLocation(tg).getY() + "§8/§e" + (int) Checkpoints.getLocation(tg).getZ() + "§8)");
        }
        p.sendMessage("§7Rang §8× §e" + Script.getRank(tg).getName(tg));
        p.sendMessage("§7Premium §8× §e" + (Premium.hasPremium(tg) ? "Ja §8(§e" + Script.dateFormat2.format(Premium.getPremiumTime(tg)) + "§8)" : "Nein"));
        p.sendMessage("§7Geld §8× §e" + Script.getMoney(tg, PaymentType.CASH) + "€ | " + Script.getMoney(tg, PaymentType.BANK) + "€" + (Banken.hasBank(tg) ? " §8(§e" + Banken.getBankByPlayer(tg).getName() + "§8)" : " §8(§c" + "Keine Bank" + "§8)"));
        p.sendMessage("§7Fahndung §8× §e" + (Fahndung.isFahnded(tg) ? Fahndung.getWanteds(tg) + " WantedPunkte" : "Nein"));
        p.sendMessage("§7UUID §8× §e" + tg.getUniqueId());
        p.sendMessage("§7Geschlecht §8× §e" + Script.getGender(tg).getName());
        p.sendMessage("§7Team §8× §e" + (Team.getTeam(tg) != null ? Team.getTeam(tg).getName() : "Kein Team"));
        p.sendMessage("§7PlayTime §8× §e" + Script.getPlayTime(tg, true) + ":§e" + String.format("%02d", Script.getPlayTime(tg, false)) + " Stunden" + " §8(§e" + Script.getActivePlayTime(tg, true) + ":§e" + String.format("%02d", Script.getActivePlayTime(tg, false)) + " Stunden§8)");
        p.sendMessage("§7PayDay §8× §e" + PayDay.getPayDayTime(tg) + "/60 Minuten");
        p.sendMessage("§7Level §8× §e" + Script.getLevel(tg));
        p.sendMessage("§7Exp §8× §e" + Script.getExp(tg) + "/" + Script.getLevelCost(tg) + " Exp §8(§e" + Script.getPercentage(Script.getExp(tg), Script.getLevelCost(tg)) + "%§8)");
        p.sendMessage("§7Health §8× §e" + df.format(tg.getHealth() / 2) + "/" + df.format(tg.getMaxHealth() / 2) + " HP");
        p.sendMessage("§7Beruf §8× §e" + (Beruf.hasBeruf(tg) ? Beruf.getBeruf(tg).getName() : "Kein Beruf") + (Beruf.isLeader(tg, true) ? " §8(§eLeader§8)" : ""));
        p.sendMessage("§7Organisation §8× §e" + (Organisation.hasOrganisation(tg) ? Organisation.getOrganisation(tg).getName() + (Organisation.isLeader(tg, true) ? " §8(§eLeader§8)" : " §8(§e" + Organisation.getRankName(tg) + "§8)") : "Keine Organisation"));
        StringBuilder houses = new StringBuilder();
        for (House house : House.getHouses(Script.getNRPID(tg))) {
            if (House.getHouses(Script.getNRPID(tg)).size() == 1) {
                houses.append("Haus " + house.getID());
                break;
            }
            houses.append(house.getID()).append(house.getID() == House.getHouses(Script.getNRPID(tg)).get(House.getHouses(Script.getNRPID(tg)).size() - 1).getID() ? "" : ", ");
        }
        if (houses.toString().equalsIgnoreCase("")) houses = new StringBuilder("Keine Häuser");
        p.sendMessage("§7Häuser §8× §e" + houses);


        return false;
    }
}
