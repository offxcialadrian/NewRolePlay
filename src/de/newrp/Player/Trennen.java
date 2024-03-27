package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Particle;
import de.newrp.API.Script;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Trennen implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!BeziehungCommand.hasRelationship(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keine Beziehung.");
            return true;
        }

        OfflinePlayer tg = BeziehungCommand.getPartner(p);
        BeziehungCommand.breakup(p);
        BeziehungCommand.breakup(tg);

        p.sendMessage(BeziehungCommand.PREFIX + "Du hast die Beziehung zu " + tg.getName() + " beendet.");
        if(tg.isOnline()) {
            tg.getPlayer().sendMessage(BeziehungCommand.PREFIX + "Die Beziehung zu " + p.getName() + " wurde beendet.");
        } else {
            Script.addOfflineMessage(tg, BeziehungCommand.PREFIX + "Die Beziehung zu " + p.getName() + " wurde beendet.");
        }

        return false;
    }
}
