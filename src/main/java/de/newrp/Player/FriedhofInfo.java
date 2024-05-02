package de.newrp.Player;

import de.newrp.API.Friedhof;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriedhofInfo implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (Friedhof.isDead(p)) {
            Friedhof f = Friedhof.getDead(p);
            int left = f.getDeathtimeLeft();
            if (left < 60) {
                p.sendMessage("§7Du bist noch " + left + " Sekunden auf dem Friedhof.");
            } else if (left == 60) {
                p.sendMessage("§7Du bist noch eine Minute auf dem Friedhof.");
            } else {
                int min = left / 60;
                int sec = left - (min * 60);
                p.sendMessage("§7Du bist noch " + min + " " + (min == 1 ? "Minute" : "Minuten") + " und " + sec + " Sekunden auf dem Friedhof.");
            }
        } else {
            if (Script.isInRegion(p, new Location(Script.WORLD, 245, 82, 668, 60.599842f, 26.550053f), new Location(Script.WORLD, 212, 71, 689, 219.15007f, 5.700036f))) {
                p.teleport(new Location(p.getWorld(), 333, 78, 1159));
            }
        }
        return true;
    }
}
