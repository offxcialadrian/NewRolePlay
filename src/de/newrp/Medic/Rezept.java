package de.newrp.Medic;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Player.Annehmen;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Rezept implements CommandExecutor {

    public static final String PREFIX = "§8[§cRezept§8] §c» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein.");
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/rezept [Name] [Medikament]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.PLAYER_FAR);
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 281, 75, 1239))> 5) {
            p.sendMessage(Messages.ERROR + "Du kannst hier kein Rezept ausstellen.");
            return true;
        }

        Medikamente m = Medikamente.getMedikament(args[1]);
        if(m == null) {
            p.sendMessage(Messages.ERROR + "Das Medikament wurde nicht gefunden.");
            p.sendMessage(Messages.INFO + "Verfügbare Medikamente:");
            for(Medikamente med : Medikamente.values()) {
                p.sendMessage(Messages.INFO + "§8- " + med.getName());
            }
            return true;
        }

        if (tg.getInventory().firstEmpty() == -1) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keinen freien Inventarplatz.");
            return true;
        }

        Annehmen.offer.put(tg.getName() + ".rezept", p.getName());
        Annehmen.offer.put(tg.getName() + ".rezept.medikament", m.getName());
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " ein Rezept für " + m.getName() + " angeboten.");
        tg.sendMessage(PREFIX + Script.getName(p) + " hat dir ein Rezept für " + m.getName() + " angeboten.");
        Script.sendAcceptMessage(tg);

        return false;
    }

    public static boolean hasRezept(Player p, Medikamente m) {
        for(ItemStack is : p.getInventory().getContents()) {
            if(is != null && is == m.getRezept()) {
                return true;
            }
        }
        return false;
    }

    public static void removeRezept(Player p, Medikamente m) {
        for(ItemStack is : p.getInventory().getContents()) {
            if(is != null && is == m.getRezept()) {
                is.setAmount(is.getAmount() - 1);
                return;
            }
        }
    }
}
