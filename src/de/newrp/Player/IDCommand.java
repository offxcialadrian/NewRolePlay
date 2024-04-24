package de.newrp.Player;

import de.newrp.API.Friedhof;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.Checkpoints;
import de.newrp.Administrator.SDuty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IDCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§6ID§8] ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/id [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht online.");
            return true;
        }

        p.sendMessage(PREFIX);
        p.sendMessage("§8" + Messages.ARROW + " §6Name: §6" + Script.getName(tg));
        p.sendMessage("§8" + Messages.ARROW + " §6Level: §6" + Script.getLevel(tg));
        p.sendMessage("§8" + Messages.ARROW + " §6Ping: §6" + tg.getPing() + "ms");
        p.sendMessage("§8" + Messages.ARROW + " §6AFK: §6" + (AFK.isAFK(tg) ? "Ja (seit " + AFK.getAFKTime(tg) + " Uhr)" : "Nein"));
        if(SDuty.isSDuty(tg)) p.sendMessage("§8" + Messages.ARROW + " §6Supporter-Dienst: §6Ja");
        if(Friedhof.isDead(tg)) p.sendMessage("§8" + Messages.ARROW + " §6Tod: §6Ja");
        if(Checkpoints.hasCheckpoints(tg)) p.sendMessage("§8" + Messages.ARROW + " §6Checkpoints: §6Ja");

        return false;
    }
}
