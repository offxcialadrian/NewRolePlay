package de.newrp.Berufe;

import com.comphenix.protocol.PacketType;
import de.newrp.API.Messages;
import de.newrp.Organisationen.Drogen;
import de.newrp.Organisationen.Organisation;
import de.newrp.Organisationen.Stuff;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EquipPriceCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(Messages.ERROR + "/equippreis [equip] ([preis])");
                return true;
            }

            if (Beruf.hasBeruf(player)) {
                Equip.Stuff stuff = Equip.Stuff.getStuff(args[0].replace("-", " "));

                if (stuff == null) {
                    player.sendMessage(Messages.ERROR + "Das Equip wurde nicht gefunden.");
                    return true;
                }

                if (args.length == 1 || !Beruf.getAbteilung(player).isLeader()) {
                    player.sendMessage(Equip.PREFIX + "Preis für " + stuff.getName() + ": " + stuff.getPrice(Beruf.getBeruf(player).getID()) + "€");
                    return true;
                }

                int p;
                try {
                    p = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    player.sendMessage(Messages.ERROR + "Ungültiger Preis.");
                    return true;
                }

                stuff.setPrice(Beruf.getBeruf(player).getID(), p);
                player.sendMessage(Equip.PREFIX + "Preis für " + stuff.getName() + " auf " + p + "€ gesetzt.");
            } else if (Organisation.hasOrganisation(player)) {
                Stuff stuff = Stuff.getStuff(args[0].replace("-", " "));

                if (stuff == null) {
                    player.sendMessage(Messages.ERROR + "Das Equip wurde nicht gefunden.");
                    return true;
                }

                if (args.length == 1 || Organisation.getRank(player) < 5) {
                    player.sendMessage(Equip.PREFIX + "Preis für " + stuff.getName() + ": " + stuff.getPrice(Organisation.getOrganisation(player).getID()) + "€");
                    return true;
                }

                int p;
                try {
                    p = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    player.sendMessage(Messages.ERROR + "Ungültiger Preis.");
                    return true;
                }

                stuff.setPrice(Organisation.getOrganisation(player).getID(), p);
                player.sendMessage(Equip.PREFIX + "Preis für " + stuff.getName() + " auf " + p + "€ gesetzt.");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Player player = (Player) sender;
        List<String> arg = new ArrayList<>();
        if (Beruf.hasBeruf(player)) {
            for (Equip.Stuff equip : Equip.Stuff.values()) {
                if (equip.getBeruf() == Beruf.getBeruf(player)) {
                    arg.add(equip.getName().replaceAll(" ", "-"));
                }
            }
        } else if (Organisation.hasOrganisation(player)) {
            for (Stuff equip : Stuff.values()) {
                if (equip.getLevel() <= Organisation.getOrganisation(player).getLevel()) {
                    arg.add(equip.getName().replaceAll(" ", "-"));
                }
            }
        }
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (String string : arg) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        }
        if (args.length == 2) {
            completions.add("0");
        }
        return completions;
    }
}
