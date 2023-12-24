package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Player.Notruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShareNotruf implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!AcceptNotruf.accept.containsKey(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Notruf angenommen.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/sharenotruf [Name]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst den Notruf nicht mit dir selbst teilen.");
            return true;
        }

        if(!Beruf.hasBeruf(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen Beruf.");
            return true;
        }

        if(!Beruf.getBeruf(tg).equals(Beruf.getBeruf(p))) {
            p.sendMessage(Messages.ERROR + "Du kannst den Notruf nicht mit diesem Spieler teilen.");
            return true;
        }

        if(AcceptNotruf.accept.containsKey(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler hat bereits einen Notruf angenommen.");
            return true;
        }


        Player caller = AcceptNotruf.accept.get(p);
        Beruf.getBeruf(p).sendMessage(Notruf.PREFIX + "Der Notruf von " + Script.getName(caller) + " wurde mit " + Script.getName(tg) + " geteilt.");
        Script.sendClickableMessage(p, Notruf.PREFIX + "Route anzeigen", "/navi " + caller.getLocation().getBlockX() + "/" + caller.getLocation().getBlockY() + "/" + caller.getLocation().getBlockZ(), "Klicke um die Route anzeigen zu lassen.");


        return false;
    }
}
