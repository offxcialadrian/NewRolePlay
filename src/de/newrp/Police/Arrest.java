package de.newrp.Police;

import de.newrp.API.Chair;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Government.Straftat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class Arrest implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein, um jemanden zu verhaften.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/arrest [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Fahndung.isFahnded(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht gefahndet.");
            return true;
        }

        if(!Script.isInRange(p.getLocation(), tg.getLocation(), 5)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return true;
        }

        if(!Handschellen.isCuffed(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in Handschellen.");
            return true;
        }

        int fahndungID = Fahndung.getStraftatID(tg);
        int wanteds = Straftat.getWanteds(fahndungID);
        long time = Fahndung.getFahndedTime(tg);

        if(wanteds < 2) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat zu wenig Wanteds.");
            return true;
        }

        if(!Straftat.isStraftat(fahndungID)) {
            p.sendMessage(Messages.ERROR + "Diese Straftat existiert nicht.");
            return true;
        }

        p.sendMessage(Fahndung.PREFIX + "Du hast " + Script.getName(tg) + " verhaftet.");
        Script.addEXP(p, Script.getRandom(5, 15));
        Jail.arrest(tg, wanteds*30, true);
        Fahndung.removeFahndung(tg);
        int minute = 0;
        int hour = (int) TimeUnit.MILLISECONDS.toHours(time);

        if (hour <= 0) {
            minute = (int) TimeUnit.MILLISECONDS.toMinutes(time);
        }
        String message;
        if (hour > 0) {
            message = Fahndung.PREFIX + Script.getName(tg) + " wurde von " + Script.getName(p) + " eingesperrt.\n"
                    + Fahndung.PREFIX + "Fahndungsgrund: " + Straftat.getReason(fahndungID) + " | Fahndungszeit: " + hour + " Stunden.";
        } else {
            message = Fahndung.PREFIX + Script.getName(tg) + " wurde von " + Script.getName(p) + " eingesperrt.\n"
                    + Fahndung.PREFIX + "Fahndungsgrund: " + Straftat.getReason(fahndungID) + " | Fahndungszeit: " + minute + " Minuten.";
        }
        Beruf.Berufe.POLICE.sendMessage(message);
        return false;
    }



}
