package de.newrp.House;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.Player.Hotel;
import de.newrp.Player.Mobile;
import de.newrp.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AkkuCommand implements CommandExecutor {

    private static final String AKKU = "§8[§6Akku§8] §6" + Messages.ARROW + " ";


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/akku");
            return true;
        }

        if(!Mobile.hasPhone(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy.");
            return true;
        }

        if(!House.isInHouse(p) && !Hotel.isInHotelRoom(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Haus.");
            return true;
        }

        House h = House.getInsideHouse(p);
        if(h == null && !Hotel.isInHotelRoom(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keinem Haus.");
            return true;
        }

        if(h != null) {
            if(!h.isInside(p)) {
                p.sendMessage(Messages.ERROR + "Du bist nicht in diesem Haus.");
                return true;
            }
        } else {
            if(!Hotel.isInHotelRoom(p)) {
                p.sendMessage(Messages.ERROR + "Du bist nicht in diesem Haus.");
                return true;
            }
        }

        p.sendMessage(AKKU + "Du hast begonnen, dein Handy aufzuladen.");
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!Mobile.hasPhone(p)) {
                    p.sendMessage(AKKU + "Du hast aufgehört, dein Handy aufzuladen.");
                    cancel();
                    return;
                }

                if(!House.isInHouse(p) && !Hotel.isInHotelRoom(p)) {
                    p.sendMessage(AKKU + "Du hast aufgehört, dein Handy aufzuladen.");
                    cancel();
                    return;
                }

                House h = House.getInsideHouse(p);
                if(h == null && !Hotel.isInHotelRoom(p)) {
                    p.sendMessage(AKKU + "Du hast aufgehört, dein Handy aufzuladen.");
                    cancel();
                    return;
                }

                if(h != null) {
                    if(!h.isInside(p)) {
                        p.sendMessage(AKKU + "Du hast aufgehört, dein Handy aufzuladen.");
                        cancel();
                        return;
                    }
                } else {
                    if(!Hotel.isInHotelRoom(p)) {
                        p.sendMessage(AKKU + "Du hast aufgehört, dein Handy aufzuladen.");
                        cancel();
                        return;
                    }
                }

                if(Mobile.getPhone(p).getAkku(p) >= Mobile.getPhone(p).getMaxAkku()) {
                    p.sendMessage(AKKU + "Dein Handy ist vollständig aufgeladen.");
                    cancel();
                    if (h != null){
                        House.Mieter m = h.getMieterByID(h.getOwner());
                        m.setNebenkosten(h, m.getNebenkosten() + Script.getRandom(5, 10));
                    }
                    return;
                }

                Mobile.getPhone(p).addAkku(p, 1);
                double current_progress = Mobile.getPhone(p).getAkku(p);
                double progress_percentage = current_progress / Mobile.getPhone(p).getMaxAkku();
                StringBuilder sb = new StringBuilder();
                int bar_length = 10;
                for (int i = 0; i < bar_length; i++) {
                    if (i < bar_length * progress_percentage) {
                        sb.append("§e▉");
                    } else {
                        sb.append("§8▉");
                    }
                }
                Script.sendActionBar(p, "§eAufladen... §8» §a" + sb);

            }
        }. runTaskTimerAsynchronously(main.getInstance(), Premium.hasPremium(p) ? 3L : 5L, Premium.hasPremium(p) ? 3L : 5L);


        return false;
    }
}
