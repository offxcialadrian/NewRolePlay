package de.newrp.Police;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Government.Stadtkasse;
import de.newrp.Government.Straftat;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.Mobile;
import org.bukkit.Material;
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

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if (!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein, um jemanden zu verhaften.");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/arrest [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (!Fahndung.isFahnded(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler ist nicht gefahndet.");
            return true;
        }

        if (!Script.isInRange(p.getLocation(), tg.getLocation(), 5)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return true;
        }

        if (!Handschellen.isCuffed(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in Handschellen.");
            return true;
        }

        long time = Fahndung.getFahndedTime(tg);
        int wanteds = Fahndung.getWanteds(tg);

        if (wanteds < 5) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat zu wenig Wanteds.");
            return true;
        }

        Stadtkasse.addStadtkasse(wanteds * 2, "Verhaftung von " + Script.getName(tg) + " durch " + Script.getName(p) + " (" + wanteds + " Wanteds)", null);
        p.sendMessage(Fahndung.PREFIX + "Du hast " + Script.getName(tg) + " verhaftet.");
        Jail.arrest(tg, wanteds * 5, true);
        new FahndungLog(tg, p, wanteds);
        if (Organisation.hasOrganisation(tg)) {
            Organisation o = Organisation.getOrganisation(tg);
            int add = Fahndung.getWanteds(tg) / 4;
            o.addExp(add, false);
        }
        int minute = 0;
        int hour = (int) TimeUnit.MILLISECONDS.toHours(time);

        if (hour <= 0) {
            minute = (int) TimeUnit.MILLISECONDS.toMinutes(time);
        }
        String message;
        if (hour > 0) {
            Beruf.Berufe.POLICE.sendMessage(Fahndung.PREFIX + Script.getName(tg) + " wurde von " + Script.getName(p) + " eingesperrt. Fahndungszeit: " + hour + " Stunden.");
            for (int i : Fahndung.getStraftatIDs(tg)) {
                Beruf.Berufe.POLICE.sendMessage(Fahndung.PREFIX + "Fahndungsgrund: " + Straftat.getReason(i) + " | WantedPunkte: " + Straftat.getWanteds(i));
            }
        } else {
            Beruf.Berufe.POLICE.sendMessage(Fahndung.PREFIX + Script.getName(tg) + " wurde von " + Script.getName(p) + " eingesperrt. Fahndungszeit: " + minute + " Minuten.");
            for (int i : Fahndung.getStraftatIDs(tg)) {
                Beruf.Berufe.POLICE.sendMessage(Fahndung.PREFIX + "Fahndungsgrund: " + Straftat.getReason(i) + " | WantedPunkte: " + Straftat.getWanteds(i));
            }
        }


        if (Mobile.hasPhone(tg)) {
            Mobile.getPhone(tg).setOff(tg);
        }

        Activity.grantActivity(Script.getNRPID(p), Activities.ARREST);

        Log.NORMAL.write(p, "hat " + Script.getName(tg) + " verhaftet (" + wanteds + ")");
        Log.NORMAL.write(tg, "wurde von " + Script.getName(p) + " verhaftet (" + wanteds + ")");
        Script.addEXP(p, Math.abs(wanteds / 6), true);
        Script.removeWeapons(tg);
        Handschellen.uncuff(tg);
        p.getInventory().addItem(Script.setName(new ItemStack(Material.LEAD), "§7Handschellen"));
        Script.updateFahndungSubtitle(tg);
        return false;
    }


}
