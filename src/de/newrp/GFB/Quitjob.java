package de.newrp.GFB;

import de.newrp.API.Cache;
import de.newrp.API.Messages;
import de.newrp.API.Route;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Quitjob implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/quitjob");
            return true;
        }

        if(!GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Job.");
            return true;
        }

        GFB gfb = GFB.CURRENT.get(p.getName());
        GFB.CURRENT.remove(p.getName());
        switch (gfb) {
            case LAGERARBEITER:
                Lagerarbeiter.SCORE.remove(p.getName());
                Lagerarbeiter.ON_JOB.remove(p.getName());
                Cache.loadInventory(p);
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Lagerarbeiter §7verlassen.");
                break;
            case TRANSPORT:
                Transport.SCORE.remove(p.getName());
                Transport.STARTED.remove(p.getName());
                Transport.SAFE_SCORE.remove(p.getName());
                Transport.SHOP.remove(p.getName());
                Transport.cooldown2.remove(p.getName());
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Transport §7verlassen.");
                break;
            case KELLNER:
                Kellner.SCORE.remove(p.getName());
                Kellner.CURRENT.remove(p.getName());
                Cache.loadInventory(p);
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Kellner §7verlassen.");
                break;
            case EISHALLE:
                Eishalle.CURRENT = null;
                for(Block block : Script.getBlocksBetween(new Location(Script.WORLD, 385, 66, 764), new Location(Script.WORLD, 370, 66, 741))) {
                    block.setType(Material.ICE);
                }
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Eishalle §7verlassen.");
                break;
            case PIZZALIEFERANT:
                Pizza.pizza.remove(p.getName());
                Pizza.house.remove(p.getName());
                Pizza.TOTAL_SCORE.remove(p.getName());
                Pizza.timer.remove(p.getName());
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Pizzalieferant §7verlassen.");
                Route.invalidate(p);
                break;
            case DISHWASHER:
                Dishwasher.dishes.remove(p.getName());
                Dishwasher.TOTAL_SCORE.remove(p.getName());
                Dishwasher.ON_JOB.remove(p.getName());
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Tellerwäscher §7verlassen.");
                break;
            case BURGERFRYER:
                BurgerFryer.BURGER.remove(p.getName());
                BurgerFryer.TOTAL_SCORE.remove(p.getName());
                BurgerFryer.NEEDED.remove(p.getName());
                BurgerFryer.SCORE.remove(p.getName());
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Burgerbrater §7verlassen.");
                break;
        }

        return false;
    }
}
