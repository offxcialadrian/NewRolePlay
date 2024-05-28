package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.Administrator.Notifications;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrganisationKasse implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§8[§eOrganisationKasse§8] §e» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        if (!Organisation.isLeader(p, true) && !args[0].equalsIgnoreCase("einzahlen")) {
            if(Organisation.getRank(p) < 4) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }
        }

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/organisationkasse [Auszahlen/Einzahlen/Info] <Betrag>");
            return true;
        }

        ATM atm = ATM.getNearATM(p);
        if (atm == null) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe eines Geldautomaten.");
            return true;
        }

        if(Bankautomaten.cooldownATM.containsKey(atm) && Bankautomaten.cooldownATM.get(atm) > System.currentTimeMillis()) {
            p.sendMessage(Messages.ERROR + "Der Automat ist derzeit nicht verfügbar.");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("info")) {
                p.sendMessage(PREFIX + "Die Kasse der " + Organisation.getOrganisation(p).getName() + " hat §6" + Organisation.getOrganisation(p).getKasse() + "€");
                return true;
            }

            if (args[0].equalsIgnoreCase("auszahlen")) {
                p.sendMessage(Messages.ERROR + "/organisationkasse auszahlen <Betrag>");
                return true;
            }

            if (args[0].equalsIgnoreCase("einzahlen")) {
                p.sendMessage(Messages.ERROR + "/organisationkasse einzahlen <Betrag>");
                return true;
            }

            p.sendMessage(Messages.ERROR + "/organisationkasse [Auszahlen/Einzahlen/Info] <Betrag>");
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("auszahlen")) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    if (amount < 0) {
                        p.sendMessage(Messages.ERROR + "Der Betrag muss positiv sein.");
                        return true;
                    }

                    if (amount > Organisation.getOrganisation(p).getKasse()) {
                        p.sendMessage(Messages.ERROR + "Die Kasse hat nicht genug Geld.");
                        return true;
                    }

                    if (atm.getCash() < amount) {
                        p.sendMessage(Messages.ERROR + "Der Geldautomat hat nicht genug Geld.");
                        return true;
                    }

                    final int expToLose = Math.max(1, (int) Math.floor((double) amount / 1000));
                    Organisation.getOrganisation(p).removeExp(expToLose);
                    atm.removeCash(amount);
                    Organisation.getOrganisation(p).removeKasse(amount);
                    Script.addMoney(p, PaymentType.CASH, amount);
                    Organisation.getOrganisation(p).sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + amount + "€ §7aus der Kasse ausgezahlt.");
                    Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + amount + "€ aus der " + Organisation.getOrganisation(p).getName() + "-Kasse ausgezahlt.");
                    Log.HIGH.write(Script.getName(p) + " hat " + amount + "€ aus der " + Organisation.getOrganisation(p).getName() + "-Kasse ausgezahlt.");
                    return true;
                } catch (NumberFormatException e) {
                    p.sendMessage(Messages.ERROR + "Der Betrag muss eine Zahl sein.");
                    return true;
                }
            }

            if (args[0].equalsIgnoreCase("einzahlen")) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    if (amount < 0) {
                        p.sendMessage(Messages.ERROR + "Der Betrag muss positiv sein.");
                        return true;
                    }

                    if (amount > Script.getMoney(p, PaymentType.CASH)) {
                        p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                        return true;
                    }


                    final int expToGain = (int) Math.floor((double) amount / 1000);
                    Organisation.getOrganisation(p).addExp(expToGain);
                    atm.addCash(amount);
                    Organisation.getOrganisation(p).addKasse(amount);
                    Script.removeMoney(p, PaymentType.CASH, amount);
                    Organisation.getOrganisation(p).sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + amount + "€ §7in die Kasse eingezahlt.");
                    Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + amount + "€ in die " + Organisation.getOrganisation(p).getName() + "-Kasse eingezahlt.");
                    Log.NORMAL.write(Script.getName(p) + " hat " + amount + "€ in die " + Organisation.getOrganisation(p).getName() + "-Kasse eingezahlt.");
                    Activity.addActivity(Script.getNRPID(p), 0, Activities.GELD.getName(), Activities.GELD.getPoints() * ((float) amount / 100));
                    return true;
                } catch (NumberFormatException e) {
                    p.sendMessage(Messages.ERROR + "Der Betrag muss eine Zahl sein.");
                    return true;
                }
            }

            p.sendMessage(Messages.ERROR + "/organisationkasse [Auszahlen/Einzahlen/Info] <Betrag>");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> args1 = Arrays.asList("einzahlen", "auszahlen", "info");
        List<String> completions = new ArrayList<>();
        if (args.length == 1) for (String string : args1) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        return completions;
    }
}