package de.newrp.Player;

import de.newrp.API.Achievement;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class APIKey implements CommandExecutor {
    public static final String PREFIX = "§8[§aAPIKey§8] §a" + de.newrp.API.Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            if(Script.getAPIKey(p) == null) {
                String code = generateCode();
                p.sendMessage(PREFIX + "Du hast nun den API-Key §6" + code + "§7.");
                p.sendMessage(Messages.INFO + "Bitte bewahre diesen Key sicher auf. Er ist nur einmalig einsehbar und kann nicht zurückgesetzt werden.");
                Script.executeAsyncUpdate("INSERT INTO api_key (nrp_id, personal_key) VALUES ('" + Script.getNRPID(p) + "', '" + code + "')");
                return true;
            }

            p.sendMessage(PREFIX + "Du hast bereits einen API-Key. Du kannst diesen nicht zurücksetzen.");
            return true;
        }

        if(args.length == 1) {
            if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }


            if(!SDuty.isSDuty(p)) {
                p.sendMessage(Messages.NO_SDUTY);
                return true;
            }

            OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
            if(Script.getNRPID(tg) == 0) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }


            if(Script.hasRank(tg, Rank.ADMINISTRATOR, false)) {
                p.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if(Script.getBackUpCode(tg) == null) {
                p.sendMessage(PREFIX + "Der Spieler hat keinen API-Key.");
                return true;
            }

            p.sendMessage(PREFIX + "Der BackupCode von " + tg.getName() + " ist §6" + Script.getAPIKey(tg) + "§7.");
            return true;
        }

        return false;
    }

    private static String generateCode() {
        String code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            sb.append(code.charAt((int) (Math.random() * code.length())));
        }
        return sb.toString();
    }
}
