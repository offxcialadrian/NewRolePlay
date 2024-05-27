package de.newrp.Waffen;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Police.Handschellen;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropGuns implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(GangwarCommand.isInGangwar(p) || DependencyContainer.getContainer().getDependency(IBizWarService.class).isMemberOfBizWar(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst im Gangwar keine Waffen droppen.");
            return true;
        }

        if(!Script.hasWeapons(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keine Waffe bei dir.");
            return true;
        }

        if(Handschellen.isCuffed(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst deine Waffen nicht droppen, da du Handschellen an hast.");
            return true;
        }

        //Me.sendMessage(p, "lässt eine Waffe fallen.");
        Me.sendMessage(p, "lässt etwas metallisches fallen.");
        Script.removeWeapons(p);
        Log.NORMAL.write(p, "hat seine Waffen fallen gelassen.");


        return false;
    }
}
