package de.newrp.Medic;

import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Player.Annehmen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ErsteHilfeSchein implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getAbteilung(p, true) == Abteilung.Abteilungen.MEDIZINSTUDENT || Beruf.getAbteilung(p, true) == Abteilung.Abteilungen.ASSISTENZARZT) {
            p.sendMessage(Messages.ERROR + "Du kannst keine Erste-Hilfe-Scheine ausstellen.");
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein!");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/erstehilfeschein [Name]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (Licenses.ERSTE_HILFE.isLocked(Script.getNRPID(tg))) {
            p.sendMessage(Messages.ERROR + "Der Erste-Hilfe-Ausweis der Person wurde gesperrt.");
            return true;
        }

        if(tg.getLocation().distance(p.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return true;
        }

        if(Licenses.ERSTE_HILFE.hasLicense(Script.getNRPID(tg))) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat bereits einen Erste-Hilfe-Schein.");
            return true;
        }

        Annehmen.offer.put(tg.getName() + ".erstehilfeschein", p.getName());
        p.sendMessage(Messages.INFO + "Du hast " + Script.getName(tg) + " einen Erste-Hilfe-Schein angeboten.");
        tg.sendMessage(Messages.INFO + Script.getName(p) + " hat dir einen Erste-Hilfe-Schein angeboten.");
        Script.sendAcceptMessage(tg);

        return false;
    }
}
