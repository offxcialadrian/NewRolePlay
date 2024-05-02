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

public class BackupCode implements CommandExecutor {

    public static final String PREFIX = "§8[§aBackupCode§8] §a" + de.newrp.API.Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            if(Script.getBackUpCode(p) == null) {
                String code = generateCode();
                p.sendMessage(PREFIX + "Du hast nun den BackupCode §6" + code + "§7.");
                p.sendMessage(Messages.INFO + "Bitte bewahre diesen Code sicher auf. Er ist nur einmalig einsehbar und kann nicht zurückgesetzt werden.");
                Achievement.BACKUPCODE.grant(p);
                Script.executeAsyncUpdate("INSERT INTO backupcodes (nrp_id, code) VALUES ('" + Script.getNRPID(p) + "', '" + code + "')");
                return true;
            }

            p.sendMessage(PREFIX + "Du hast bereits einen BackupCode.");
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
                p.sendMessage(PREFIX + "Der Spieler hat keinen BackupCode.");
                return true;
            }

            p.sendMessage(PREFIX + "Der BackupCode von " + tg.getName() + " ist §6" + Script.getBackUpCode(tg) + "§7.");
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
