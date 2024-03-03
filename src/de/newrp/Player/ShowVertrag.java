package de.newrp.Player;

import de.newrp.API.Gender;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.VertragAPI;
import de.newrp.Chat.Me;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowVertrag implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/vertrag [Spieler] [Vertrag-ID]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.PLAYER_FAR);
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            p.sendMessage(Messages.ERROR + "Die Vertrag-ID muss eine Zahl sein.");
            return true;
        }

        VertragAPI v = VertragAPI.getVertrag(id);
        if(v == null) {
            p.sendMessage(Messages.ERROR + "Der Vertrag wurde nicht gefunden.");
            return true;
        }

        if(v.getFrom() != Script.getNRPID(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht der Vertragsinhaber.");
            return true;
        }

        p.sendMessage(Vertrag.PREFIX + "Du hast " + Script.getName(tg) +  " deinen Vertrag mit " + v.getTo() + " gezeigt.");
        tg.sendMessage(Vertrag.PREFIX + Script.getName(p) + " hat dir " + (Script.getGender(p)== Gender.MALE?"seinen":"ihren") + " Vertrag mit " + Script.getOfflinePlayer(v.getTo()).getName() + " gezeigt.");
        tg.sendMessage(Vertrag.PREFIX + "§6Bedingungen: " + v.getBedingung());
        tg.sendMessage(Vertrag.PREFIX + "§6Von: " + Script.getOfflinePlayer(v.getFrom()).getName());
        tg.sendMessage(Vertrag.PREFIX + "§6An: " + Script.getOfflinePlayer(v.getTo()).getName());
        tg.sendMessage(Vertrag.PREFIX + "§6Datum: " + Script.dateFormat.format(v.getTime()) + " Uhr");
        tg.sendMessage(Vertrag.PREFIX + "§6Vertrags-ID: " + v.getID());
        Me.sendMessage(p, "zeigt " + Script.getName(tg) + " einen Vertrag.");

        return false;
    }
}
