package de.newrp.GFB;

import de.newrp.API.*;
import de.newrp.Shop.Shops;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropTabak implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if(!GFB.CURRENT.containsKey(p.getName()) || !GFB.CURRENT.get(p.getName()).equals(GFB.TABAKPLANTAGE)) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Job.");
            return true;
        }

        if(!Tabakplantage.mixedTobacco.containsKey(p.getName())) {
            p.sendMessage(Tabakplantage.PREFIX + "Du hast keinen Tabak bei dir.");
            return true;
        }
        int tabak = Tabakplantage.mixedTobacco.get(p.getName());
        if (tabak < 1) {
            p.sendMessage(Tabakplantage.PREFIX + "Du hast keinen Tabak bei dir.");
            return true;
        }

        if (p.getLocation().distance(HologramList.DROPTABAK.getLocation()) >= 5) {
            p.sendMessage(Tabakplantage.PREFIX + "Du bist noch nicht an der Shishabar.");
            return true;
        }
        p.sendMessage(Tabakplantage.PREFIX + "Du hast " + tabak + "g Tabak abgegeben.");
        Shops.WHITE_LOUNGE.addLager(tabak / 2);
        int money = (int) (tabak * 2.5);
        int exp = (int) (tabak * 2.5);
        PayDay.addPayDay(p, money);
        GFB.TABAKPLANTAGE.addExp(p, exp);
        Script.addEXP(p, exp);
        Tabakplantage.freshTobacco.remove(p.getName());
        Tabakplantage.driedTobacco.remove(p.getName());
        Tabakplantage.mixedTobacco.remove(p.getName());
        GFB.CURRENT.remove(p.getName());
        return true;
    }
}
