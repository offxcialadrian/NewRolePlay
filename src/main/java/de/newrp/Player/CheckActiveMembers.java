package de.newrp.Player;

import com.github.theholywaffle.teamspeak3.api.wrapper.Message;
import de.newrp.API.Messages;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CheckActiveMembers implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/checkactivemembers");
            return true;
        }

        StringBuilder sb = new StringBuilder("§8[§6Aktive Mitglieder§8] §6" + Messages.ARROW);
        for(Beruf.Berufe b : Beruf.Berufe.values()) {
            ArrayList<Player> members = new ArrayList<>(b.getBeruf().keySet());
            members.removeIf(member -> member == null);
            members.removeIf(AFK::isAFK);
            members.removeIf(member -> !Duty.isInDuty(member));
            members.removeIf(SDuty::isSDuty);
            sb.append("\n§8» §e").append(b.getName()).append("§8: §7").append(members.size());
        }
        for(Organisation o : Organisation.values()) {
            ArrayList<Player> members = new ArrayList<>(o.getMember());
            members.removeIf(member -> member == null);
            members.removeIf(AFK::isAFK);
            members.removeIf(SDuty::isSDuty);
            sb.append("\n§8» §e").append(o.getName()).append("§8: §7").append(members.size());
        }

        p.sendMessage(sb.toString());


        return false;
    }
}
