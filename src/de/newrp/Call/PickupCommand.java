package de.newrp.Call;

import de.newrp.API.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PickupCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/p");
            return true;
        }

        if(!Call.isWaitingForCall(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Anruf.");
            return true;
        }

        Call.accept(p);

        return false;
    }
}
