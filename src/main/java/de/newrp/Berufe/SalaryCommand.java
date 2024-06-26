package de.newrp.Berufe;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Organisationen.Organisation;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SalaryCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§eGehalt§8] §e» ";
    private static final int max = 2500;
    private static final int min = 0;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p) && !Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Beruf oder in keiner Organisation.");
            return true;
        }

        if (args.length > 2) {
            p.sendMessage(Messages.ERROR + "/salary [Spieler] [Gehalt]");
            return true;
        }

        if (!Beruf.isLeader(p, true) && !Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        if(Beruf.hasBeruf(p)) {
            if(args.length == 0) {
                p.sendMessage(PREFIX + "Alle Gehälter der " + Beruf.getBeruf(p).getName() + ":");
                for(OfflinePlayer all : Beruf.getBeruf(p).getAllMembers()) {
                    p.sendMessage("§8» §6" + all.getName() + " §8[§6" + Beruf.getAbteilung(all).getName() + "§8]: §6" + Beruf.getSalary(all) + "€");
                }
                return true;
            }

            if(args.length == 1) {

                OfflinePlayer tg = Script.getOfflinePlayer(args[0]);

                if(Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    if(!SDuty.isSDuty(p)) {
                        p.sendMessage(Messages.NO_SDUTY);
                        return true;
                    }

                    if(Script.getNRPID(args[0]) != 0) {
                        p.sendMessage(PREFIX + "§6" + tg.getName() + " hat ein Gehalt von §6" + Beruf.getSalary(tg) + "€");
                        return true;
                    }

                    Beruf.Berufe b = Beruf.Berufe.getBeruf(args[0]);
                    if(b == null) {
                        p.sendMessage(Messages.ERROR + "Der Beruf wurde nicht gefunden.");
                        return true;
                    }

                    if(Beruf.getBeruf(p) != b && !SDuty.isSDuty(p)) {
                        p.sendMessage(Messages.ERROR + "Du bist nicht in diesem Beruf.");
                        return true;
                    }

                    p.sendMessage(PREFIX + "Alle Gehälter der " + b.getName() + ":");
                    for(OfflinePlayer all : b.getAllMembers()) {
                        p.sendMessage("§8» §6" + all.getName() + " §8[§6" + Beruf.getAbteilung(all).getName() + "§8]: §6" + Beruf.getSalary(all) + "€");
                    }
                    return true;
                } else if(Beruf.isLeader(p, true)){
                    p.sendMessage(Messages.ERROR + "/salary [Spieler] [Gehalt]");
                    return true;
                }
            }

            OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
            if (Script.getNRPID(tg) == 0) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(Beruf.getBeruf(tg) != Beruf.getBeruf(p)) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deinem Beruf.");
                return true;
            }

            int salary = 0;
            try {
                salary = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl an.");
                return true;
            }

            if(salary > max) {
                p.sendMessage(Messages.ERROR + "Das Gehalt darf nicht höher als " + max + " sein.");
                return true;
            }

            if(salary < min) {
                p.sendMessage(Messages.ERROR + "Das Gehalt darf nicht niedriger als " + min + " sein.");
                return true;
            }

            Log.WARNING.write(p, "hat das Gehalt von " + Script.getName(tg) + " auf " + salary + "€ gesetzt.");
            Script.setInt(tg, "berufe", "salary", salary);
            p.sendMessage(PREFIX + "Du hast das Gehalt von " + Script.getName(tg) + " auf " + salary + "€ gesetzt.");
            if(p!=tg && tg.getPlayer()!=null) tg.getPlayer().sendMessage(PREFIX + "Dein Gehalt wurde von " + Script.getName(p) + " auf " + salary + "€ gesetzt.");
            Beruf.getBeruf(p).sendLeaderMessage("§8[§e" + Beruf.getBeruf(p).getName() + "§8] §e» " + "Das Gehalt von " + Script.getName(tg) + " wurde von " + Script.getName(p) + " auf " + salary + "€ gesetzt.");
            Script.sendTeamMessage(PREFIX + "Das Gehalt von " + Script.getName(tg) + " wurde von " + Script.getName(p) + " auf " + salary + "€ gesetzt. [" + Beruf.getBeruf(p).getName() + "]");
            if(Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT) Beruf.Berufe.NEWS.sendMessage(PREFIX + "Das Gehalt des Regierungsmitgliedes " + Script.getName(tg) + " wurde auf " + salary + "€ gesetzt.");
            return true;
        }

        if(Organisation.hasOrganisation(p)) {
            if (args.length == 0) {
                p.sendMessage(PREFIX + "Alle Gehälter der " + Organisation.getOrganisation(p).getName() + ":");
                for (OfflinePlayer all : Organisation.getOrganisation(p).getAllMembers()) {
                    p.sendMessage("§8» §6" + all.getName() + " §8[§6" + Organisation.getRank(all) + "§8]: §6" + Organisation.getSalary(all) + "€");
                }
                return true;
            }

            if (args.length == 1) {

                OfflinePlayer tg = Script.getOfflinePlayer(args[0]);

                if (Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    if (!SDuty.isSDuty(p)) {
                        p.sendMessage(Messages.NO_SDUTY);
                        return true;
                    }

                    if (Script.getNRPID(args[0]) != 0) {
                        p.sendMessage(PREFIX + "§6" + tg.getName() + " hat ein Gehalt von §6" + Organisation.getSalary(tg) + "€");
                        return true;
                    }

                    Organisation b = Organisation.getOrganisation(args[0]);
                    if (b == null) {
                        p.sendMessage(Messages.ERROR + "Die Organisation wurde nicht gefunden.");
                        return true;
                    }

                    if (Organisation.getOrganisation(p) != b && !SDuty.isSDuty(p)) {
                        p.sendMessage(Messages.ERROR + "Du bist nicht in dieser Organisation.");
                        return true;
                    }

                    p.sendMessage(PREFIX + "Alle Gehälter der " + b.getName() + ":");
                    for (OfflinePlayer all : b.getAllMembers()) {
                        p.sendMessage("§8» §6" + all.getName() + " §8[§6" + Organisation.getRank(all) + "§8]: §6" + Organisation.getSalary(all) + "€");
                    }
                    return true;
                } else if (Organisation.isLeader(p, true)) {
                    p.sendMessage(Messages.ERROR + "/salary [Spieler] [Gehalt]");
                    return true;
                }
            }

            OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
            if (Script.getNRPID(tg) == 0) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if (Organisation.getOrganisation(tg) != Organisation.getOrganisation(p)) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deiner Organisation.");
                return true;
            }

            int salary = 0;
            try {
                salary = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl an.");
                return true;
            }

            if (salary > max) {
                p.sendMessage(Messages.ERROR + "Das Gehalt darf nicht höher als " + max + " sein.");
                return true;
            }

            if (salary < min) {
                p.sendMessage(Messages.ERROR + "Das Gehalt darf nicht niedriger als " + min + " sein.");
                return true;
            }

            Log.WARNING.write(p, "hat das Gehalt von " + Script.getName(tg) + " auf " + salary + "€ gesetzt.");
            Script.setInt(tg, "organisation", "salary", salary);
            p.sendMessage(PREFIX + "Du hast das Gehalt von " + Script.getName(tg) + " auf " + salary + "€ gesetzt.");
            if (p != tg)
                if(tg.getPlayer() != null) tg.getPlayer().sendMessage(PREFIX + "Dein Gehalt wurde von " + Script.getName(p) + " auf " + salary + "€ gesetzt.");
            Organisation.getOrganisation(p).sendLeaderMessage("§8[§e" + Organisation.getOrganisation(p).getName() + "§8] §e» " + "Das Gehalt von " + Script.getName(tg) + " wurde von " + Script.getName(p) + " auf " + salary + "€ gesetzt.");
            Script.sendTeamMessage(PREFIX + "Das Gehalt von " + Script.getName(tg) + " wurde von " + Script.getName(p) + " auf " + salary + "€ gesetzt. [" + Organisation.getOrganisation(p).getName() + "]");
            return true;

        }

        return true;
    }
}
