package de.newrp.Police;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Bussgeld implements CommandExecutor {

    public static String PREFIX = "§8[§9Bussgeld§8] §9» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/bussgeld [Spieler] [Betrag]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "/bussgeld [Spieler] [Betrag]");
            return true;
        }

        int amount = Integer.parseInt(args[1]);
        if(amount < 1) {
            p.sendMessage(Messages.ERROR + "Der Betrag muss mindestens 1€ betragen.");
            return true;
        }

        if(amount > Script.getLevel(tg)*500) {
            p.sendMessage(Messages.ERROR + "Der Betrag darf maximal " + Script.getLevel(tg)*500 + "€ betragen.");
            return true;
        }

        if(Script.getMoney(tg, PaymentType.BANK) < amount) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat nicht genug Geld.");
            return true;
        }

        if(SDuty.isSDuty(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist im Supporter-Dienst.");
            return true;
        }

        if(p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dir selbst kein Bussgeld ausstellen.");
            return true;
        }

        if(AFK.isAFK(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist AFK.");
            return true;
        }

        Script.removeMoney(tg, PaymentType.BANK, amount);
        Stadtkasse.addStadtkasse(amount, "Bussgeld von " + Script.getName(tg) + " erhalten.", null);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " ein Bussgeld in Höhe von " + amount + "€ ausgestellt.");
        tg.sendMessage(PREFIX + "Du hast ein Bussgeld in Höhe von " + amount + "€ erhalten.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Spieler " + Script.getName(tg) + " hat ein Bussgeld in Höhe von " + amount + "€ von " + Script.getName(p) + " erhalten.");
        Beruf.Berufe.GOVERNMENT.sendMessage(PREFIX + "Der Spieler " + Script.getName(tg) + " hat ein Bussgeld in Höhe von " + amount + "€ von " + Script.getName(p) + " erhalten.");
        Log.NORMAL.write(p, "hat " + Script.getName(tg) + " ein Bussgeld in Höhe von " + amount + "€ ausgestellt.");


        return false;
    }
}
