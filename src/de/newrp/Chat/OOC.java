package de.newrp.Chat;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.AntiCheatSystem;
import de.newrp.Administrator.Notications;
import de.newrp.Administrator.Punish;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OOC implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/ooc [Nachricht]");
            return true;
        }

        if(Punish.isMuted(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gemuted.");
            return true;
        }

        String msg = "";
        for(int i = 0; i < args.length; i++) {
            msg += args[i] + " ";
        }

        for(String arg : args) {
            if(arg.contains("http://") || arg.contains("https://") || arg.contains("www.") || arg.contains(".de")  || arg.contains(".eu")) {
                Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdacht auf Fremdwerbung bei " + Script.getName(p) + " (Level " + p.getLevel() + ") §8» §c" + msg);
                if(Script.getNRPTeam().isEmpty()) {
                    p.sendMessage(AntiCheatSystem.PREFIX + "Es liegt ein Verdacht auf Fremdwerbung vor. Die Nachricht wurde nicht gesendet. Wenn du denkst, dass es sich um einen Fehler handelt, melde ihn bitte im Forum.");
                    return true;
                }
            }

            if(Script.isIP(arg)) {
                Script.sendTeamMessage(AntiCheatSystem.PREFIX + "Verdacht auf Fremdwerbung bei " + Script.getName(p) + " (Level " + p.getLevel() + ") §8» §c" + msg);
                if(Script.getNRPTeam().isEmpty()) {
                    p.sendMessage(AntiCheatSystem.PREFIX + "Es liegt ein Verdacht auf Fremdwerbung vor. Die Nachricht wurde nicht gesendet. Wenn du denkst, dass es sich um einen Fehler handelt, melde ihn bitte im Forum.");
                    return true;
                }
            }
        }

        for(Player all : p.getWorld().getPlayers()) {
            if(all.getLocation().distance(p.getLocation()) > 10) continue;
            all.sendMessage("§c((§lOOC §c" + Script.getName(p) + " §8» §c" + msg + "))");
        }

        Notications.sendMessage(Notications.NotificationType.CHAT, "§c((§lOOC §c" + Script.getName(p) + " §8» §c" + msg + "))");

        return false;
    }
}
