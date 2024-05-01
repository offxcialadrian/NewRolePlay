package de.newrp.House;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HausAddonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        House h = House.getNearHouse(p.getLocation(), 5);
        if (h == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich nicht in der Nähe eines Hauses.");
            return true;
        }

        if (!h.livesInHouse(p)) {
            p.sendMessage(Messages.ERROR + "Du wohnst nicht in diesem Haus.");
            return true;
        }

        if (h.getOwner() != Script.getNRPID(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht der Besitzer dieses Hauses.");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/hausaddon");
            return true;
        }

        p.sendMessage("§6=== Haus Addons ===");
        for (HouseAddon addon : HouseAddon.values()) {
            if(addon == HouseAddon.SLOT) {
                p.sendMessage("§8» §6" + addon.getName() + " §7- §a" + h.getSlots());
                continue;
            }
            if (h.getAddons().contains(addon)) {
                p.sendMessage("§8» §6" + addon.getName() + " §7- §aGekauft");
            } else {
                p.sendMessage("§8» §6" + addon.getName() + " §7- §cnicht ausgerüstet");
            }
        }
        return true;
    }
}
