package de.newrp.Police;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Chat.Me;
import de.newrp.House.House;
import de.newrp.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Ramm implements CommandExecutor {

    private static String PREFIX = "§8[§9Ramm§8] §9" + Messages.ARROW + " §7";
    public static HashMap<House, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Dienst.");
            return true;
        }

        House h = House.getNearHouse(p.getLocation(), 5);
        if(h == null) {
            p.sendMessage(PREFIX + "Du bist an keinem Haus.");
            return true;
        }

        int cops = 0;
        for(Player near : Bukkit.getOnlinePlayers()) {
            if(near.getLocation().distance(p.getLocation()) < 5) {
                if(Beruf.hasBeruf(near)) {
                    if(Beruf.getBeruf(near).equals(Beruf.Berufe.POLICE) && Duty.isInDuty(near)) {
                        cops++;
                    }
                }
            }
        }

        if(cops < 2) {
            p.sendMessage(PREFIX + "Es müssen mindestens 2 Polizisten in der Nähe sein.");
            return true;
        }

        if(h.getOwner() == 0) {
            p.sendMessage(PREFIX + "Dieses Haus gehört niemanden.");
            return true;
        }

        if(cooldown.containsKey(h)) {
            if(System.currentTimeMillis() - cooldown.get(h) < 60 * 1000L) {
                p.sendMessage(PREFIX + "Du kannst dieses Haus erst in " + ((60 * 1000L - (System.currentTimeMillis() - cooldown.get(h))) / 1000L) + " Sekunden wieder aufbrechen.");
                return true;
            }
        }

        p.sendMessage(PREFIX + "Du hast begonnen die Tür aufzubrechen.");
        Me.sendMessage(p, "tritt gegen die Haustür");

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            Script.playLocalSound(h.getSignLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 5);
            for (Location loc : h.getDoors()) {
                if (loc.distance(p.getLocation()) < 4) {
                    if (loc.getBlock().getRelative(BlockFace.UP).getType().equals(Material.OAK_DOOR)) {
                        Block b = loc.getBlock();
                        Door d = (Door) b.getBlockData();
                        if (d.getHalf().equals(Door.Half.TOP)) {
                            b = b.getRelative(BlockFace.DOWN);
                        }

                        if (!d.isOpen()) {
                            d.setOpen(true);
                            b.setBlockData(d);
                        }

                        Log.NORMAL.write(p, "hat die Tür von Haus " + h.getID() + " aufgebrochen (Besitzer: " + Script.getOfflinePlayer(h.getOwner()).getName() + ")");
                        cooldown.put(h, System.currentTimeMillis());
                        p.sendMessage(PREFIX + "Du hast die Tür aufgebrochen.");
                        OfflinePlayer owner = Script.getOfflinePlayer(h.getOwner());
                        if(owner.isOnline()) {
                            owner.getPlayer().sendMessage(PREFIX + "Deine Tür wurde von der Polizei aufgebrochen.");
                            owner.getPlayer().sendMessage(Messages.INFO + "Die Reparatur der Haustür hat 50€ gekostet.");
                        } else {
                            Script.addOfflineMessage(owner, PREFIX + "Deine Tür wurde von der Polizei aufgebrochen.");
                            Script.addOfflineMessage(owner, Messages.INFO + "Die Reparatur der Haustür hat 50€ gekostet.");
                        }
                        Script.removeMoney(owner, PaymentType.BANK, 50);
                    }
                } else {
                    p.sendMessage(Messages.ERROR + "Du bist zu weit weg.");
                }
            }
        }, 15 * 20L);


        return false;
    }
}
