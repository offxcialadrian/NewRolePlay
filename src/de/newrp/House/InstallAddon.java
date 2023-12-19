package de.newrp.House;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Shop.PayShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InstallAddon implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        House house = House.getNearHouse(p.getLocation(), 5);
        if (house == null) {
            p.sendMessage(Messages.ERROR + " §cDu bist in keinem Haus.");
            return true;
        }

        if (house.getOwner() != Script.getNRPID(p)) {
            p.sendMessage(Messages.ERROR + " §cDu bist nicht der Besitzer dieses Hauses.");
            return true;
        }

        HouseAddon addon = PayShop.houseaddon.get(p.getName());
        if (addon == null) {
            p.sendMessage(Messages.ERROR + " Du hast kein Addon gekauft.");
            return true;
        }

        if (house.hasAddon(addon)) {
            p.sendMessage(Messages.ERROR + " Dieses Haus hat bereits dieses Addon.");
            return true;
        }

        if(addon == HouseAddon.SLOT) {
            house.setSlots(house.getSlots() + 1);
            Log.NORMAL.write(p, "hat das Addon " + addon.getName() + " installiert.");
            p.sendMessage( "§8[§6Haus§8] §6" + Messages.ARROW + " Du hast das Addon " + addon.getName() + " installiert.");
            PayShop.houseaddon.remove(p.getName());
            return true;
        }

        house.addAddon(addon);
        Log.NORMAL.write(p, "hat das Addon " + addon.getName() + " installiert.");
        p.sendMessage( "§8[§6Haus§8] §6" + Messages.ARROW + " Du hast das Addon " + addon.getName() + " installiert.");
        PayShop.houseaddon.remove(p.getName());

        return false;
    }
}
