package de.newrp.Gangwar;

import de.newrp.API.Messages;
import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;

public class GangwarCommand implements CommandExecutor {

    public static String PREFIX = "§8[§cGangwar§8] §c» §7";
    public static HashMap<GangwarZones, Organisation> gangwar = new HashMap<>();
    public static HashMap<Organisation, Integer> points = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/gangwar");
            return true;
        }

        if(!Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        GangwarZones zone = GangwarZones.getZoneByLocation(p.getLocation());
        if(zone == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich nicht in einer Gangwar-Zone.");
            return true;
        }

        if(zone.getOwner() == Organisation.getOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Diese Zone gehört bereits deiner Organisation.");
            return true;
        }

        if(zone.getOwner() == null) {
            p.sendMessage(Messages.ERROR + "Diese Zone gehört keiner Organisation.");
            return true;
        }

        if(zone.getOwner().getMembers().isEmpty()) {
            p.sendMessage(Messages.ERROR + "Die Organisation, die diese Zone besitzt, hat zu wenig Mitglieder.");
            return true;
        }

        if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            p.sendMessage(Messages.ERROR + "Der Gangwar kann nur Sonntags stattfinden.");
            return true;
        }

        if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 20 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 21) {
            p.sendMessage(Messages.ERROR + "Der Gangwar kann nur zwischen 20 und 21 Uhr stattfinden.");
            return true;
        }




        return false;
    }
}
