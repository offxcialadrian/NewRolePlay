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
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

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
                Lagerarbeiter.TOTAL_SCORE.remove(p.getName());
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
                Kellner.TOTAL_SCORE.remove(p.getName());
                Kellner.time.remove(p.getName());
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
                Cache.loadInventory(p);
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Tellerwäscher §7verlassen.");
                break;
            case BURGERFRYER:
                BurgerFryer.BURGER.remove(p.getName());
                BurgerFryer.TOTAL_SCORE.remove(p.getName());
                BurgerFryer.NEEDED.remove(p.getName());
                BurgerFryer.SCORE.remove(p.getName());
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Burgerbrater §7verlassen.");
                break;
            case STRASSENWARTUNG:
                for (Location loc : Strassenwartung.construction.get(p.getName()).getLocations()) {
                    loc.getBlock().setType(Material.ANDESITE_SLAB);
                }
                Strassenwartung.construction.remove(p.getName());
                Strassenwartung.CONSTRUCTION.remove(Strassenwartung.construction.get(p.getName()));
                Strassenwartung.SCORE.remove(p.getName());
                Strassenwartung.TOTAL_SCORE.remove(p.getName());
                Cache.loadInventory(p);
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Straßenwartung §7verlassen.");
                break;
            case IMKER:
                Imker.honeys.remove(p.getName());
                Imker.TOTAL_SCORE.remove(p.getName());
                Imker.ON_JOB.remove(p.getName());
                Cache.loadInventory(p);
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Imker §7verlassen.");
                break;
            case TABAKPLANTAGE:
                Cache.loadInventory(p);
                for (ItemStack is : p.getInventory().getContents()) {
                    if (is != null) {
                        if (is.getItemMeta() != null) {
                            if (is.getItemMeta().hasDisplayName()) {
                                if (is.getItemMeta().getDisplayName().equals("§7Tabakschere")) {
                                    p.getInventory().remove(is);
                                }
                            }
                        }
                    }
                }
                p.sendMessage(GFB.PREFIX + "Du hast den Job §6Tabakplantage §7verlassen.");
                break;
        }

        return false;
    }
}
