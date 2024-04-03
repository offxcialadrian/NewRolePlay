package de.newrp.Player;

import com.github.theholywaffle.teamspeak3.commands.CMessageAdd;
import de.newrp.API.Messages;
import de.newrp.Administrator.MSG;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Reply implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/reply [Nachricht]");
            return true;
        }

        if(!MSG.may_reply.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast keine Nachrichten zum Antworten.");
            return true;
        }

        Player tg = p.getServer().getPlayer(MSG.may_reply.get(p.getName()));
        if(tg == null) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht mehr online.");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for(String arg : args) {
            sb.append(arg).append(" ");
        }

        String msg = sb.toString().trim();
        p.sendMessage(MSG.PREFIX + "Du §7» §e" + tg.getName() + "§7: §f" + msg);
        tg.sendMessage(MSG.PREFIX + "§e" + p.getName() + " §7» §7Dir: §f" + msg);
        MSG.may_reply.remove(p.getName());


        return false;
    }
}
