package de.newrp.Waffen;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import de.newrp.Police.Handschellen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropGuns implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasWeapons(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keine Waffe bei dir.");
            return true;
        }

        if(Handschellen.isCuffed(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst deine Waffen nicht dropen, da du Handschellen um hast.");
            return true;
        }

        Me.sendMessage(p, "lässt eine Waffe fallen.");
        Script.removeWeapons(p);
        Log.NORMAL.write(p, "hat seine Waffen fallen gelassen.");


        return false;
    }
}
