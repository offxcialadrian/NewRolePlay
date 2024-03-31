package de.newrp.Government;

import de.newrp.API.Messages;
import de.newrp.API.Particle;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Player.BeziehungCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Marry implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (Beruf.getAbteilung(p) != Abteilung.Abteilungen.INNENMINISTERIUM && !Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(Messages.ERROR + "/marry [Spieler 1] [Spieler 2]");
            return true;
        }

        Player p1 = Script.getPlayer(args[0]);
        Player p2 = Script.getPlayer(args[1]);

        if (Script.getMoney(p1, PaymentType.BANK) < 3500) {
            p.sendMessage(Messages.ERROR + "Der Spieler " + Script.getName(p1) + " hat nicht genügend Geld.");
            return true;
        }

        if (Script.getMoney(p2, PaymentType.BANK) < 3500) {
            p.sendMessage(Messages.ERROR + "Der Spieler " + Script.getName(p2) + " hat nicht genügend Geld.");
            return true;
        }

        Script.removeMoney(p1, PaymentType.BANK, 3500);
        Script.removeMoney(p2, PaymentType.BANK, 3500);
        Stadtkasse.addStadtkasse(7000, "Hochzeit von " + Script.getName(p1) + " und " + Script.getName(p2) + ".", null);

        p.sendMessage(BeziehungCommand.PREFIX + "Du hast " + Script.getName(p1) + " und " + Script.getName(p2) + " verheiratet.");
        p1.sendMessage(BeziehungCommand.PREFIX + "Du bist nun mit " + Script.getName(p2) + " verheiratet!");
        p2.sendMessage(BeziehungCommand.PREFIX + "Du bist nun mit " + Script.getName(p1) + " verheiratet!");
        BeziehungCommand.setMarried(p1, true);
        BeziehungCommand.setMarried(p2, true);

        new Particle(org.bukkit.Particle.HEART, p1.getEyeLocation(), false, 0.001F, 0.001F, 0.001F, 0.001F, 2).sendAll();
        new Particle(org.bukkit.Particle.HEART, p2.getEyeLocation(), false, 0.001F, 0.001F, 0.001F, 0.001F, 2).sendAll();

        return false;
    }
}
