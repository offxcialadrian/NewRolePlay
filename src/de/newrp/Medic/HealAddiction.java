package de.newrp.Medic;

import de.newrp.API.Krankheit;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Drogen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealAddiction implements CommandExecutor {

    public static String PREFIX = "§8[§c§lRettungsdienst§8] §7" + Messages.ARROW + "§7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getBeruf(p) != Beruf.Berufe.RETTUNGSDIENST) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/healaddiction [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Krankheit.ABHAENGIGKEIT.isInfected(Script.getNRPID(tg))) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht abhängig.");
            return true;
        }

        Drogen.healAddiction(p);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " wegen seiner Abhängigkeit behandelt (" + Drogen.getAddictionHeal(tg) + "/3).");
        tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " wegen deiner Abhängigkeit behandelt (" + Drogen.getAddictionHeal(tg) + "/3).");

        return false;
    }
}
