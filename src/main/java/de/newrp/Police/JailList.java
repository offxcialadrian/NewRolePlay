package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class JailList implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if(Jail.JAIL.isEmpty()) {
            p.sendMessage(Jail.PREFIX + "Es sind derzeit keine Gefangenen im Gefängnis.");
            return true;
        }


        StringBuilder sb = new StringBuilder(Jail.PREFIX).append("Es sind folgende Gefangene im Gefängnis:\n");
        Iterator<Map.Entry<String, Jail>> it = Jail.JAIL.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Jail> ent = it.next();
            String name = ent.getKey();
            Jail jail = ent.getValue();
            Player p1 = Script.getPlayer(name);
            if (p1 == null) {
                it.remove();
            } else {
                int left = jail.getJailtimeLeft();
                if (left < 60) {
                    sb.append(" §8»§7 ").append(name).append(" | ").append(left).append(" Sekunden.\n");
                } else if (left == 60) {
                    sb.append(" §8»§7 ").append(name).append(" | 1 Minute.\n");
                } else {
                    int min = left / 60;
                    int sec = left - (min * 60);
                    sb.append(" §8»§7 ").append(name).append(" | ").append(min).append((min == 1 ? " Minute und " : " Minuten und ")).append(sec).append(" Sekunden.\n");
                }
            }
        }
        p.sendMessage(sb.toString());
        return true;
    }
}
