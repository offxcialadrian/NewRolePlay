package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Drogenbank;
import de.newrp.Organisationen.Organisation;
import org.bukkit.OfflinePlayer;
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
    private String confirm = null;

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
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("confirm")) {
                if(confirm == null) {
                    player.sendMessage(Messages.ERROR + "Es wurde nichts zum bestätigen gefunden.");
                    return true;
                }
                Organisation organisation = Organisation.getOrganisation(confirm);
                if(organisation != null) {
                    int members = organisation.getAllMembers().size();
                    for(OfflinePlayer member : organisation.getAllMembers()) {
                        organisation.removeMember(member, player);
                        if(member.getPlayer() != null) {
                            organisation.deleteMember(member.getPlayer());
                        }
                    }
                    organisation.setKasse(35000);
                    Script.executeUpdate("UPDATE organisation_level SET exp='0' WHERE organisationID='" + organisation.getID() + "'");
                    organisation.setLevel(1);
                    Drogenbank.clearDrogen(organisation);
                    player.sendMessage(prefix + "Du hast die Organisation §e" + organisation.getName() + " §7gewiped.");
                    player.sendMessage(Messages.INFO + "Es wurden insgesamt " + members + " Mitglieder aus der Organisation geworfen.");
                    confirm = null;
                    return true;
                }
                Beruf.Berufe beruf = Beruf.Berufe.getBeruf(confirm);
                if(beruf != null) {
                    int members = beruf.getAllMembers().size();
                    for(OfflinePlayer member : beruf.getAllMembers()) {
                        beruf.removeMember(member, player);
                        if(member.getPlayer() != null) {
                            beruf.deleteMember(member.getPlayer());
                        }
                    }
                    beruf.setKasse(35000);
                    player.sendMessage(prefix + "Du hast den Beruf §e" + beruf.getName() + " §7gewiped.");
                    player.sendMessage(Messages.INFO + "Es wurden insgesamt " + members + " Mitglieder aus dem Beruf geworfen.");
                    confirm = null;
                    return true;
                }
                return true;
            }
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
            confirm = args[1];
            player.sendMessage(prefix + "Nutze §c/wipe confirm §7um §e" + args[1] + " §7wirklich zu wipen.");
            return true;
        }
        if(args[0].equalsIgnoreCase("beruf")) {
            Beruf.Berufe beruf = Beruf.Berufe.getBeruf(args[1]);
            if(beruf == null) {
                player.sendMessage(prefix + "Der Beruf " + args[1] + " existiert nicht.");
                return true;
            }
            confirm = args[1];
            player.sendMessage(prefix + "Nutze §c/wipe confirm §7um §e" + args[1] + " §7wirklich zu wipen.");
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;
        if(!Script.hasRank(player, Rank.ADMINISTRATOR, false)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return null;
        }
        if(args.length == 1) {
            return new ArrayList<>(Arrays.asList("org","beruf"));
        }
        if(args.length == 2) {
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
