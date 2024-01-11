package de.newrp.Call;

import de.newrp.API.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HangupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/hangup");
            return true;
        }

        if(!Call.isOnCall(p) && !Call.isWaitingForCall(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Gespräch.");
            return true;
        }

        if(Call.isWaitingForCall(p)) {
            Call.deny(p);
            p.sendMessage(Messages.INFO + "Du hast den Anruf abgelehnt.");
            return true;
        }

        if(Call.getParticipants(Call.getCallIDByPlayer(p)).size() == 1) {
            Call.abort(p);
            return true;
        }

        Call.hangup(p);

        return false;
    }
}
