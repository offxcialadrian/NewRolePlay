package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MemberActivity implements CommandExecutor {

    private static final String PREFIX = "§8[§eAktivität§8] §e» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(Beruf.hasBeruf(p)) {
            if(!Beruf.isLeader(p, true)) {
                p.sendMessage(Messages.ERROR + "Du bist kein Leader deines Berufs.");
                return true;
            }

            if(args.length != 0) {
                p.sendMessage(Messages.ERROR + "/memberactivity");
                return true;
            }

            for(OfflinePlayer members : Beruf.getBeruf(p).getAllMembers()) {
                if(members.isOnline()) p.sendMessage(PREFIX + "§6" + members.getName() + " §8× §aOnline");
                else p.sendMessage(PREFIX + "§6" + members.getName() + " §8× §cOffline seit: " + Script.dateFormat.format(Script.getLastDisconnect(members)) + " Uhr");
            }

            return true;
        }

        if(Organisation.hasOrganisation(p)) {
            if(!Organisation.isLeader(p, true)) {
                p.sendMessage(Messages.ERROR + "Du bist kein Leader deiner Organisation.");
                return true;
            }

            if(args.length != 0) {
                p.sendMessage(Messages.ERROR + "/memberactivity");
                return true;
            }

            for(OfflinePlayer members : Organisation.getOrganisation(p).getAllMembers()) {
                if(members.isOnline()) p.sendMessage(PREFIX + "§6" + members.getName() + " §8× §aOnline");
                else p.sendMessage(PREFIX + "§6" + Script.getNameInDB(members) + " §8× §cOffline seit: " + Script.dateFormat.format(Script.getLastDisconnect(members)) + " Uhr");
            }

            return true;
        }

        p.sendMessage(Messages.ERROR + "Du bist in keinem Beruf oder einer Organisation.");

        return false;
    }
}
