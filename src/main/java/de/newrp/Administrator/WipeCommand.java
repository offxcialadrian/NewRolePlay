package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WipeCommand implements CommandExecutor, TabCompleter {

    private final String prefix = "§8[§cWipe§8] §c» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;
        if(!Script.hasRank(player, Rank.ADMINISTRATOR, false)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }
        if(!SDuty.isSDuty(player)) {
            player.sendMessage(Messages.NO_SDUTY);
            return true;
        }
        if(args.length != 2) {
            player.sendMessage(prefix + "Nutze: §c/wipe org/beruf [name]");
            return true;
        }
        if(args[0].equalsIgnoreCase("org")) {
            Organisation organisation = Organisation.getOrganisation(args[1]);
            if(organisation == null) {
                player.sendMessage(prefix + "Die Organisation " + args[1] + " existiert nicht.");
                return true;
            }
        }
        if(args[0].equalsIgnoreCase("beruf")) {
            Beruf.Berufe beruf = Beruf.Berufe.getBeruf(args[1]);
            if(beruf == null) {
                player.sendMessage(prefix + "Der Beruf " + args[1] + " existiert nicht.");
                return true;
            }
        }


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0) {
            return new ArrayList<>(Arrays.asList("org","beruf"));
        }
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("org")) {
                return getOrganisationNames();
            }
            if(args[0].equalsIgnoreCase("beruf")) {
                return getBerufNames();
            }
        }
        return null;
    }

    private ArrayList<String> getOrganisationNames() {
        ArrayList<String> orgas = new ArrayList<>();
        for(Organisation org : Organisation.values()) {
            orgas.add(org.getName());
        }
        return orgas;
    }

    private ArrayList<String> getBerufNames() {
        ArrayList<String> berufe = new ArrayList<>();
        for(Beruf.Berufe beruf : Beruf.Berufe.values()) {
            berufe.add(beruf.getName());
        }
        return berufe;
    }

}
