package de.newrp.Berufe;

import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Forum.Forum;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.Annehmen;
import de.newrp.TeamSpeak.TeamSpeak;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§eInvite§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p) && !Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/invite [Spieler]");
            return true;
        }

        if (!Beruf.isLeader(p, true) && !Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst einladen.");
            return true;
        }

        if (p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return true;
        }

        if (Beruf.hasBeruf(tg) || Organisation.hasOrganisation(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat bereits einen Beruf oder ist bereits in einer Organisation.");
            return true;
        }

        if(!Licenses.PERSONALAUSWEIS.hasLicense(Script.getNRPID(tg))) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keinen Personalausweis.");
            return true;
        }

        if(Script.getActivePlayTime(tg, true) < 1 && !SDuty.isSDuty(p)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat noch keine 60 Minuten aktive Spielzeit.");
            return true;
        }

        if(!TeamSpeak.isVerified(Script.getNRPID(tg))) {
            p.sendMessage(Messages.ERROR + "Der Spieler muss erst seinen TeamSpeak-Account verknüpfen.");
            return true;
        }

        if(Forum.getForumID(Script.getNRPID(tg)) == 0) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keinen Forum-Account.");
            return true;
        }

        if (Beruf.hasBeruf(p)) {
            Annehmen.offer.put(tg.getName() + ".joinberuf", p.getName());
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " in " + Beruf.getBeruf(p).getName() + " eingeladen.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " in " + Beruf.getBeruf(p).getName() + " eingeladen.");
            Script.sendAcceptMessage(tg);
            return true;
        }


        if (Organisation.getOrganisation(p).getAllMembers().size() >= slots(Organisation.getOrganisation(p))) {
            p.sendMessage(Messages.ERROR + "Deine Organisation ist voll.");
            return true;
        }


        Annehmen.offer.put(tg.getName() + ".joinorganisation", p.getName());
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " in deine Organisation " + Organisation.getOrganisation(p).getName() + " eingeladen.");
        tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " in die Organisation " + Organisation.getOrganisation(p).getName() + " eingeladen.");
        Script.sendAcceptMessage(tg);


        return false;
    }

    public static int slots(Organisation o) {
        return Math.min(30, Math.max(15, o.getLevel() * 3));
    }

}
