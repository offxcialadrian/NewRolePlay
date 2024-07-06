package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DangerLevel implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/gefahrenstufe [Name] [Level]");
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein!");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(tg == p) {
            p.sendMessage(Messages.ERROR + "Du kannst nicht deine eigene Gefährlichkeitsstufe ändern.");
            return true;
        }

        if(!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl ein.");
            return true;
        }

        int level = Integer.parseInt(args[1]);
        if(level < 0 || level > 10) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine Zahl zwischen 0 und 10 ein.");
            return true;
        }

        if(level == Policecomputer.getDangerLevel(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat bereits diese Gefährlichkeitsstufe.");
            return true;
        }

        Policecomputer.setDangerLevel(tg, level);
        Beruf.Berufe.POLICE.sendMessage(Policecomputer.PREFIX + Script.getName(p) + " hat die Gefährlichkeitsstufe von " + Script.getName(tg) + " auf " + level + " gesetzt.");

        return false;
    }
}
