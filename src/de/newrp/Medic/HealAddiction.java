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

import java.util.ArrayList;

public class HealAddiction implements CommandExecutor {

    public static String PREFIX = "§8[§c§lRettungsdienst§8] §7" + Messages.ARROW + "§7";
    public static ArrayList<String> cooldown = new ArrayList<>();

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

        if(cooldown.contains(tg.getName())) {
            p.sendMessage(Messages.ERROR + "Der Spieler wurde bereits behandelt.");
            return true;
        }

        cooldown.add(tg.getName());
        Drogen.healAddiction(p);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " wegen seiner Abhängigkeit behandelt (" + Drogen.getAddictionHeal(tg)+1 + "/3).");
        tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " wegen deiner Abhängigkeit behandelt (" + Drogen.getAddictionHeal(tg)+1 + "/3).");

        return false;
    }
}
