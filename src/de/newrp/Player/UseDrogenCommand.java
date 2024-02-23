package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.UseDrogen;
import de.newrp.Organisationen.Drogen;
import de.newrp.Police.Handschellen;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class UseDrogenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/use [Substanz] [Reinheitsgrad]");
            return true;
        }

        Drogen droge = Drogen.getItemByName(args[0]);
        if(droge == null) {
            p.sendMessage(Messages.ERROR + "Diese Substanz existiert nicht.");
            return true;
        }

        if(!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "Der Reinheitsgrad muss eine Zahl sein.");
            return true;
        }

        Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByID(Integer.parseInt(args[1]));
        if(purity == null) {
            p.sendMessage(Messages.ERROR + "Dieser Reinheitsgrad existiert nicht.");
            return true;
        }

        Long lastUsage = UseDrogen.DRUG_COOLDOWN.get(p.getName());
        if (lastUsage != null && lastUsage + TimeUnit.SECONDS.toMillis(30) > System.currentTimeMillis()) {
            long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.SECONDS.toMillis(30) - System.currentTimeMillis());
            Script.sendActionBar(p, Messages.ERROR + "Du bist gerade noch im Rausch. (" + cooldown + " Sekunden verbleibend)");
            return true;
        }

        if(Handschellen.isCuffed(p) || Fesseln.isTiedUp(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Drogen konsumieren, wenn du gefesselt bist.");
            return true;
        }

        for(ItemStack is : p.getInventory().getContents()) {
            if(is == null) continue;
            if(is.getItemMeta() == null) continue;
            is.getItemMeta().getDisplayName();
            if(is.getItemMeta().getDisplayName().equalsIgnoreCase(droge.getName()) && (Drogen.DrugPurity.getPurityByName(is.getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", "")) == purity)) {
                droge.consume(p, purity);
                UseDrogen.DRUG_COOLDOWN.put(p.getName(), System.currentTimeMillis());
                is.setAmount(is.getAmount() - 1);
                return true;
            }
        }

        p.sendMessage(Messages.ERROR + "Du hast kein " + droge.getName() + " mit dem Reinheitsgrad " + purity.getText() + " in deinem Inventar.");

        return false;
    }
}
