package de.newrp.Shop;

import de.newrp.API.Messages;
import de.newrp.API.Navi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Sellfisch implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(p.getLocation().distance(Navi.FISCH.getLocation())>5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht am Fischladen.");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/sellfisch");
            return true;
        }

        int i = 0;
        for(ItemStack is : p.getInventory().getContents()) {
            if(is==null) continue;
            Fish fish = (Fish) is.getData();
            if(fish==null) continue;
            i++;
        }

        if(i==0) {
            p.sendMessage(Messages.ERROR + "Du hast keine Fische im Inventar.");
            return true;
        }

        int price = i*2;
        for(ItemStack is : p.getInventory().getContents()) {
            if(is==null) continue;
            Fish fish = (Fish) is.getData();
            if(fish==null) continue;
            p.getInventory().remove(is);
        }

        p.sendMessage(Messages.INFO + "Du hast " + i + " Fische für " + price + "€ verkauft.");


        return false;
    }


}
