package de.newrp.Medic;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.GoTo;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Player.Annehmen;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rezept implements CommandExecutor, TabCompleter {

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

        if(Beruf.getAbteilung(p, true) == Abteilung.Abteilungen.MEDIZINSTUDENT) {
            p.sendMessage(Messages.ERROR + "Du kannst keine Rezepte ausstellen.");
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein.");
            return true;
        }

        if(args.length != 2 && args.length != 3) {
            p.sendMessage(Messages.ERROR + "/rezept [Name] [Medikament] [Anzahl]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(p == tg && !Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du kannst dir selbst kein Rezept ausstellen.");
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

        Medikamente m = Medikamente.getMedikament(args[1].replace("-", " "));
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

        int amount = 0;
        if(args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if(amount < 1) {
                    p.sendMessage(Messages.ERROR + "Die Anzahl muss mindestens 1 sein.");
                    return true;
                }
                if(amount > 64) {
                    p.sendMessage(Messages.ERROR + "Die Anzahl darf maximal 64 sein.");
                    return true;
                }
            } catch (NumberFormatException e) {
                p.sendMessage(Messages.ERROR + "Die Anzahl muss eine Zahl sein.");
                return true;
            }
        }

        Annehmen.offer.put(tg.getName() + ".rezept", p.getName());
        Annehmen.offer.put(tg.getName() + ".rezept.medikament", m.getName());
        if(args.length == 3) {
            Annehmen.offer.put(tg.getName() + ".rezept.anzahl", args[2]);
        }
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " " + amount + "x Rezepte für " + m.getName() + " angeboten.");
        tg.sendMessage(PREFIX + Script.getName(p) + " hat dir " + amount + "x Rezepte für " + m.getName() + " angeboten.");
        Script.sendAcceptMessage(tg);

        return false;
    }

    public static boolean hasRezept(Player p, Medikamente m) {
        for(ItemStack is : p.getInventory().getContents()) {
            if(is == null || is.getType() == Material.AIR) continue;
            if(is.getItemMeta() == null) continue;;

            if(is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.stripColor(m.getRezept().getItemMeta().getDisplayName()))) {
                return true;
            }
        }
        return false;
    }

    public static int getAmountOfRecipes(final Player player, final Medikamente medikamente) {
        for(ItemStack is : player.getInventory().getContents()) {
            if(is == null || is.getType() == Material.AIR) continue;
            if(is.getItemMeta() == null) continue;;

            if(is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.stripColor(medikamente.getRezept().getItemMeta().getDisplayName()))) {
                return is.getAmount();
            }
        }
        return 0;
    }

    public static void removeRezept(Player p, Medikamente m) {
        for(ItemStack is : p.getInventory().getContents()) {
            if(is != null && is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.stripColor(m.getRezept().getItemMeta().getDisplayName()))) {
                is.setAmount(is.getAmount() - 1);
                return;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("rezept") || cmd.getName().equalsIgnoreCase("rezepte")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Medikamente medikamente : Medikamente.values()) {
                oneArgList.add(medikamente.getName().replace(" ","-"));
            }

            if (args.length == 1) {
                return null;
            }

            if (args.length == 2) {
                StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            }

            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }
}
