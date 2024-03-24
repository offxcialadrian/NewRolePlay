package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.GoTo;
import de.newrp.Administrator.Notifications;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import de.newrp.Government.Stadtkasse;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Banner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "§8[§bBank§8] §b» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        ATM atm = ATM.getNearATM(p);

        if(atm == null && (!(args.length >= 2 && args[0].equalsIgnoreCase("überweisen")&&Premium.hasPremium(p)))) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe eines Bankautomats.");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(PREFIX + "Du kannst folgende Befehle nutzen:");
            p.sendMessage(PREFIX + "/bank einzahlen [Betrag]");
            p.sendMessage(PREFIX + "/bank auszahlen [Betrag]");
            p.sendMessage(PREFIX + "/bank überweisen [Spieler] [Betrag]");
            p.sendMessage(PREFIX + "/bank info");
            return true;
        }


        if(!Banken.hasBank(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Bankkonto.");
            p.sendMessage(Messages.INFO + "Du kannst eins an der Bank erstellen mit /banken.");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
            p.sendMessage(PREFIX + "§8=== §6" + Banken.getBankByPlayer(p).getName() + " §8===");
            p.sendMessage(PREFIX + "Kontostand: " + Script.getMoney(p, PaymentType.BANK) + "€");
            p.sendMessage(PREFIX + "§8=========");
            return true;
        }





        if(args.length == 2) {
            if(!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "Du hast keinen gültigen Betrag angegeben.");
                return true;
            }
            int betrag = Integer.parseInt(args[1]);

            if(betrag <= 0) {
                p.sendMessage(Messages.ERROR + "Du kannst nicht mit negativen Beträgen arbeiten.");
                return true;
            }

            if(betrag > Banken.getBankByPlayer(p).getTransactionLimit()) {
                p.sendMessage(Messages.ERROR + "Deine Bank lässt nur Transaktionen bis " + Banken.getBankByPlayer(p).getTransactionLimit() + "€ zu.");
                return true;
            }

            if (args[0].equalsIgnoreCase("einzahlen")) {
                if (Script.getMoney(p, PaymentType.CASH) < betrag) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    return true;
                }

                Script.removeMoney(p, PaymentType.CASH, betrag);
                Script.addMoney(p, PaymentType.BANK, betrag);
                p.sendMessage(PREFIX + "§8=== §6" + Banken.getBankByPlayer(p).getName() + " §8===");
                p.sendMessage(PREFIX + "Alter Kontostand§8: §c" + (Script.getMoney(p, PaymentType.BANK) - betrag) + "€");
                p.sendMessage(PREFIX + "Eingezahlt§8: §a" + betrag + "€");
                p.sendMessage(PREFIX + "Neuer Kontostand§8: §a" + Script.getMoney(p, PaymentType.BANK) + "€");
                p.sendMessage(PREFIX + "§8=========");
                atm.addCash(betrag);
                Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + betrag + "€ eingezahlt.");
                Log.NORMAL.write(p, "hat " + betrag + "€ eingezahlt.");
                Cashflow.addEntry(p, betrag, "Einzahlung an ATM " + atm.getID());
                return true;
            }


            if (args[0].equalsIgnoreCase("auszahlen") || args[0].equalsIgnoreCase("abheben") || args[0].equalsIgnoreCase("abbuchen")) {
                if(!Script.isInt(args[1])) {
                    p.sendMessage(Messages.ERROR + "Du hast keinen gültigen Betrag angegeben.");
                    return true;
                }
                if (atm.getCash() < betrag) {
                    p.sendMessage(Messages.ERROR + "Der Automat hat nicht genug Geld.");
                    return true;
                }

                if (betrag + Script.getMoney(p, PaymentType.BANK) < Banken.getBankByPlayer(p).getTransactionKosten()) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld um die Transaktionskosten zu decken.");
                    return true;
                }

                if (Script.getMoney(p, PaymentType.BANK) < betrag) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    return true;
                }

                Script.removeMoney(p, PaymentType.BANK, betrag+Banken.getBankByPlayer(p).getTransactionKosten());
                Script.addMoney(p, PaymentType.CASH, betrag);
                p.sendMessage(PREFIX + "§8=== §6" + Banken.getBankByPlayer(p).getName() + " §8===");
                p.sendMessage(PREFIX + "Alter Kontostand§8: §c" + (Script.getMoney(p, PaymentType.BANK) + betrag) + "€");
                p.sendMessage(PREFIX + "Abgehoben§8: §c" + betrag + "€");
                p.sendMessage(PREFIX + "Neuer Kontostand§8: §a" + Script.getMoney(p, PaymentType.BANK) + "€");
                p.sendMessage(PREFIX + "§8=========");
                atm.removeCash(betrag);
                p.sendMessage(Messages.INFO + "Es wurden " + Banken.getBankByPlayer(p).getTransactionKosten() + "€ Transaktionskosten abgezogen.");
                Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + betrag + "€ ausgezahlt.");
                Log.NORMAL.write(p, "hat " + betrag + "€ ausgezahlt.");
                Cashflow.addEntry(p, -betrag, "Auszahlung an ATM " + atm.getID());
                return true;
            }
        }

        if(args[0].equalsIgnoreCase("überweisen")) {
            if(Script.getLevel(p) < 2) {
                p.sendMessage(Messages.ERROR + "Du musst mindestens Level 2 sein, um Geld überweisen zu können.");
                return true;
            }

            if(args[1].equalsIgnoreCase("stadt") || args[1].equalsIgnoreCase("stadtkasse")) {
                if(!Script.isInt(args[2])) {
                    p.sendMessage(Messages.ERROR + "Du hast keinen gültigen Betrag angegeben.");
                    return true;
                }

                int betrag = Integer.parseInt(args[2]);

                if(betrag <= 0) {
                    p.sendMessage(Messages.ERROR + "Du kannst nicht mit negativen Beträgen arbeiten.");
                    return true;
                }

                StringBuilder reason = new StringBuilder();
                for(int i = 3; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }

                if(reason.length() == 0) reason = new StringBuilder("Kein Verwendungszweck angegeben.");

                if(betrag > Banken.getBankByPlayer(p).getTransactionLimit()) {
                    p.sendMessage(Messages.ERROR + "Deine Bank lässt nur Transaktionen bis " + Banken.getBankByPlayer(p).getTransactionLimit() + "€ zu.");
                    return true;
                }

                if(Script.getMoney(p, PaymentType.BANK) < betrag) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    return true;
                }

                int totalcost = (betrag+Banken.getBankByPlayer(p).getTransactionKosten());

                if(Script.getMoney(p, PaymentType.BANK) < totalcost) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld um die Transaktionskosten zu decken.");
                    return true;
                }

                Script.removeMoney(p, PaymentType.BANK, (totalcost));
                Stadtkasse.addStadtkasse(betrag, "Einzahlung von " + Script.getName(p) + " Verwendungszweck: " + reason, null);
                p.sendMessage(PREFIX + "Du hast " + betrag + "€ an die Stadtkasse überwiesen. Verwendungszweck: " + reason);
                p.sendMessage(Messages.INFO + "Es wurden " + Banken.getBankByPlayer(p).getTransactionKosten() + "€ Transaktionskosten abgezogen.");
                Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + betrag + "€ an Stadtkasse überwiesen. Verwendungszweck: " + reason);
                Log.NORMAL.write(p, "hat " + betrag + "€ an " + "Stadtkasse" + " überwiesen. Verwendungszweck: " + reason);
                Cashflow.addEntry(p, -totalcost, "Überweisung an Stadtkasse Verwendungszweck: " + reason);
                Me.sendMessage(p, "tätigt eine Überweisung.");
                return true;
            }

            if(args[1].equalsIgnoreCase("news") || args[1].equalsIgnoreCase("nachrichten")) {
                if(!Script.isInt(args[2])) {
                    p.sendMessage(Messages.ERROR + "Du hast keinen gültigen Betrag angegeben.");
                    return true;
                }

                int betrag = Integer.parseInt(args[2]);

                if(betrag <= 0) {
                    p.sendMessage(Messages.ERROR + "Du kannst nicht mit negativen Beträgen arbeiten.");
                    return true;
                }

                StringBuilder reason = new StringBuilder();
                for(int i = 3; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }

                if(reason.length() == 0) reason = new StringBuilder("Kein Verwendungszweck angegeben.");

                if(betrag > Banken.getBankByPlayer(p).getTransactionLimit()) {
                    p.sendMessage(Messages.ERROR + "Deine Bank lässt nur Transaktionen bis " + Banken.getBankByPlayer(p).getTransactionLimit() + "€ zu.");
                    return true;
                }

                if(Script.getMoney(p, PaymentType.BANK) < betrag) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    return true;
                }

                int totalcost = (betrag+Banken.getBankByPlayer(p).getTransactionKosten());

                if(Script.getMoney(p, PaymentType.BANK) < totalcost) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld um die Transaktionskosten zu decken.");
                    return true;
                }

                Script.removeMoney(p, PaymentType.BANK, (totalcost));
                Beruf.Berufe.NEWS.addKasse(betrag);
                Beruf.Berufe.NEWS.sendMessage(PREFIX + "Die News-Agency hat " + betrag + "€ von " + Script.getName(p) + " erhalten. Verwendungszweck: " + reason);
                p.sendMessage(PREFIX + "Du hast " + betrag + "€ an die Stadtkasse überwiesen. Verwendungszweck: " + reason);
                p.sendMessage(Messages.INFO + "Es wurden " + Banken.getBankByPlayer(p).getTransactionKosten() + "€ Transaktionskosten abgezogen.");
                Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + betrag + "€ an Stadtkasse überwiesen. Verwendungszweck: " + reason);
                Log.NORMAL.write(p, "hat " + betrag + "€ an " + "Stadtkasse" + " überwiesen. Verwendungszweck: " + reason);
                Cashflow.addEntry(p, -totalcost, "Überweisung an Stadtkasse Verwendungszweck: " + reason);
                Me.sendMessage(p, "tätigt eine Überweisung.");
                return true;
            }

            OfflinePlayer tg = Script.getOfflinePlayer(args[1]);
            if(Script.getNRPID(tg) == 0) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }

            if(!Script.isInt(args[2])) {
                p.sendMessage(Messages.ERROR + "Du hast keinen gültigen Betrag angegeben.");
                return true;
            }

            int betrag = Integer.parseInt(args[2]);

            if(betrag <= 0) {
                p.sendMessage(Messages.ERROR + "Du kannst nicht mit negativen Beträgen arbeiten.");
                return true;
            }

            StringBuilder reason = new StringBuilder();
            for(int i = 3; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }

            if(reason.length() == 0) reason = new StringBuilder("Kein Verwendungszweck angegeben.");

            if(reason.toString().contains("drogen")) {
                Beruf.Berufe.POLICE.sendMessage(PREFIX + "§c" + Script.getName(p) + " hat eine Überweisung mit dem Verwendungszweck 'Drogen' an " + Script.getName(tg) + " getätigt.");
            }

            if(betrag > Banken.getBankByPlayer(p).getTransactionLimit()) {
                p.sendMessage(Messages.ERROR + "Deine Bank lässt nur Transaktionen bis " + Banken.getBankByPlayer(p).getTransactionLimit() + "€ zu.");
                return true;
            }

            if(Script.getMoney(p, PaymentType.BANK) < betrag) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                return true;
            }

            int totalcost = (betrag+Banken.getBankByPlayer(p).getTransactionKosten());

            if(Script.getMoney(p, PaymentType.BANK) < totalcost) {
                p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld um die Transaktionskosten zu decken.");
                return true;
            }

            if (tg == p) {
                p.sendMessage(Messages.ERROR + "Du kannst dir nicht selbst Geld überweisen.");
                return true;
            }

            Script.removeMoney(p, PaymentType.BANK, (totalcost));
            Script.addMoney(Script.getNRPID(tg), PaymentType.BANK, betrag);
            p.sendMessage(PREFIX + "Du hast " + betrag + "€ an " + tg.getName() + " überwiesen. Verwendungszweck: " + reason);
            p.sendMessage(Messages.INFO + "Es wurden " + Banken.getBankByPlayer(p).getTransactionKosten() + "€ Transaktionskosten abgezogen.");
            Notifications.sendMessage(Notifications.NotificationType.PAYMENT, Script.getName(p) + " hat " + betrag + "€ an " + tg.getName() + " überwiesen. Verwendungszweck: " + reason);
            if(tg.isOnline()) {
                tg.getPlayer().sendMessage(PREFIX + "Du hast " + betrag + "€ von " + Script.getName(p) + " erhalten. Verwendungszweck: " + reason);
            } else {
                Script.addOfflineMessage(tg, PREFIX + "Du hast " + betrag + "€ von " + Script.getName(p) + " erhalten. Verwendungszweck: " + reason);
            }
            Log.NORMAL.write(p, "hat " + betrag + "€ an " + tg.getName() + " überwiesen. Verwendungszweck: " + reason);
            Log.NORMAL.write(tg, "hat " + betrag + "€ von " + Script.getName(p) + " erhalten. Verwendungszweck: " + reason);
            Cashflow.addEntry(p, -totalcost, "Überweisung an " + tg.getName() + " Verwendungszweck: " + reason);
            Cashflow.addEntry(tg, betrag, "Überweisung von " + Script.getName(p) + " Verwendungszweck: " + reason);
            Me.sendMessage(p, "tätigt eine Überweisung.");
            return true;
        }

        p.sendMessage(PREFIX + "Du kannst folgende Befehle nutzen:");
        p.sendMessage(PREFIX + "/bank einzahlen [Betrag]");
        p.sendMessage(PREFIX + "/bank auszahlen [Betrag]");
        p.sendMessage(PREFIX + "/bank überweisen [Spieler] [Betrag]");
        p.sendMessage(PREFIX + "/bank info");

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("bank") || cmd.getName().equals("atm") || cmd.getName().equalsIgnoreCase("geldautomat")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            oneArgList.add("einzahlen");
            oneArgList.add("abheben");
            oneArgList.add("abbuchen");
            oneArgList.add("auszahlen");
            oneArgList.add("überweisen");
            oneArgList.add("info");

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            }

            if (args.length == 2) {
                return null;
            }
            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player p = e.getPlayer();
        if(BuildMode.isInBuildMode(p)) return;
        if(e.getClickedBlock() == null) return;
        if(!(e.getClickedBlock().getState() instanceof Banner)) return;
        ATM atm = ATM.getNearATM(p);
        if(atm == null) return;
        p.performCommand("bank info");
    }

}
