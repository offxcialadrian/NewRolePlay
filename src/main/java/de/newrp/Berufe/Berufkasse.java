package de.newrp.Berufe;

import de.newrp.API.*;
import de.newrp.Administrator.Notifications;
import de.newrp.Organisationen.Bankautomaten;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Berufkasse implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§8[§eBerufskasse§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if(!Beruf.getBeruf(p).hasKasse()) {
            p.sendMessage(Messages.ERROR + "Dieser Beruf hat keine Kasse.");
            return true;
        }

        if(!Beruf.isLeader(p, true) &&  !args[0].equalsIgnoreCase("einzahlen")) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/berufskasse [Auszahlen/Einzahlen/Info] <Betrag>");
            return true;
        }

        if(!Beruf.getBeruf(p).hasKasse()) {
            p.sendMessage(Messages.ERROR + "Dieser Beruf hat keine Kasse.");
            return true;
        }

        ATM atm = ATM.getNearATM(p);
        if(atm == null) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe eines Geldautomaten.");
            return true;
        }

        if(Bankautomaten.cooldownATM.containsKey(atm) && Bankautomaten.cooldownATM.get(atm) > System.currentTimeMillis()) {
            p.sendMessage(Messages.ERROR + "Der Automat ist derzeit nicht verfügbar.");
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("info")) {
                p.sendMessage(PREFIX + "Die Kasse der " + Beruf.getBeruf(p).getName() + " hat §6" + Beruf.getBeruf(p).getKasse() + "€");
                return true;
            }

            if(args[0].equalsIgnoreCase("auszahlen")) {
                p.sendMessage(Messages.ERROR + "/berufskasse auszahlen <Betrag>");
                return true;
            }

            if(args[0].equalsIgnoreCase("einzahlen")) {
                p.sendMessage(Messages.ERROR + "/berufskasse einzahlen <Betrag>");
                return true;
            }

            p.sendMessage(Messages.ERROR + "/berufskasse [Auszahlen/Einzahlen/Info] <Betrag>");
            return true;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("auszahlen")) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    if(amount < 0) {
                        p.sendMessage(Messages.ERROR + "Der Betrag muss positiv sein.");
                        return true;
                    }

                    if(amount > Beruf.getBeruf(p).getKasse()) {
                        p.sendMessage(Messages.ERROR + "Die Kasse hat nicht genug Geld.");
                        return true;
                    }

                    if(atm.getCash() < amount) {
                        p.sendMessage(Messages.ERROR + "Der Geldautomat hat nicht genug Geld.");
                        return true;
                    }

                    atm.removeCash(amount);
                    Beruf.getBeruf(p).removeKasse(amount);
                    Script.addMoney(p, PaymentType.CASH, amount);
                    Beruf.getBeruf(p).sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + amount + "€ §7aus der Kasse ausgezahlt.");
                    Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + amount + "€ aus der " + Beruf.getBeruf(p).getName() + "-Kasse ausgezahlt.");
                    Log.HIGH.write(Script.getName(p) + " hat " + amount + "€ aus der " + Beruf.getBeruf(p).getName() + "-Kasse ausgezahlt.");
                    return true;
                } catch (NumberFormatException e) {
                    p.sendMessage(Messages.ERROR + "Der Betrag muss eine Zahl sein.");
                    return true;
                }
            }

            if(args[0].equalsIgnoreCase("einzahlen")) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    if(amount < 0) {
                        p.sendMessage(Messages.ERROR + "Der Betrag muss positiv sein.");
                        return true;
                    }

                    if(amount > Script.getMoney(p, PaymentType.CASH)) {
                        p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                        return true;
                    }

                    atm.addCash(amount);
                    Beruf.getBeruf(p).addKasse(amount);
                    Script.removeMoney(p, PaymentType.CASH, amount);
                    Beruf.getBeruf(p).sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat §6" + amount + "€ §7in die Kasse eingezahlt.");
                    Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + amount + "€ in die " + Beruf.getBeruf(p).getName() + "-Kasse eingezahlt.");
                    Log.NORMAL.write(Script.getName(p) + " hat " + amount + "€ in die " + Beruf.getBeruf(p).getName() + "-Kasse eingezahlt.");
                    Activity.addActivity(Script.getNRPID(p), 0, Activities.GELD.getName(), Activities.GELD.getPoints() * ((float) amount / 100));
                    return true;
                } catch (NumberFormatException e) {
                    p.sendMessage(Messages.ERROR + "Der Betrag muss eine Zahl sein.");
                    return true;
                }
            }

            p.sendMessage(Messages.ERROR + "/berufskasse [Auszahlen/Einzahlen/Info] <Betrag>");
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
