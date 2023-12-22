package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Uncuff implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE || SDuty.isSDuty(p)) {
            if (args.length == 0) {
                p.sendMessage(Messages.ERROR + "/uncuff [Spieler]");
            } else {
                Player tg = Script.getPlayer(args[0]);
                if (tg != null) {
                    if (p.getLocation().distance(tg.getLocation()) <= 4) {
                        if (Handschellen.isCuffed(tg)) {
                            Script.unfreeze(tg);
                            p.sendMessage(Handschellen.PREFIX + "Du hast "+  Script.getName(tg) + " die Handschellen abgenommen.");
                            Handschellen.uncuff(tg);
                            tg.sendMessage(Handschellen.PREFIX + Script.getName(p) + " hat deine Handschellen abgenommen.");
                            Me.sendMessage(p, "hat " + Script.getName(tg) + " die Handschellen abgenommen.");
                            p.getInventory().addItem(Script.setName(new ItemStack(Material.LEAD), "§7Handschellen"));
                        } else {
                            p.sendMessage(Handschellen.PREFIX + "Der Spieler ist nicht in Handschellen.");
                        }
                    } else {
                        p.sendMessage(Messages.ERROR + "Du bist zu weit entfernt.");
                    }
                } else {
                    p.sendMessage(Messages.PLAYER_NOT_FOUND);
                }
            }
        }
        return true;
    }
}
