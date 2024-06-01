package de.newrp.Berufe;

import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SperrLizenzCommand implements CommandExecutor, TabCompleter {

    private static String PREFIX = "§8[§cSperre§8] §c" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Beruf.hasBeruf(player) && !SDuty.isSDuty(player)) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (Beruf.hasBeruf(player)) {
                if (Beruf.hasBeruf(player, Beruf.Berufe.GOVERNMENT)) {
                    if (!Beruf.hasAbteilung(player, Abteilung.Abteilungen.JUSTIZMINISTERIUM) && !Beruf.isLeader(player, true)) {
                        player.sendMessage(Messages.NO_PERMISSION);
                        return true;
                    }
                }

                if (Beruf.hasBeruf(player, Beruf.Berufe.POLICE)) {
                    if (!Beruf.getAbteilung(player).isLeader() && !Beruf.isLeader(player, true)) {
                        player.sendMessage(Messages.NO_PERMISSION);
                        return true;
                    }
                }
            }

            if (args.length == 0) {
                player.sendMessage(Messages.ERROR + "/sperrlizenz [player] ([license])");
                return true;
            }

            if (args.length == 1) {
                OfflinePlayer target = Script.getOfflinePlayer(args[0]);
                player.sendMessage(PREFIX + "Lizenz-Sperren von " + target.getName() + ":");
                Map<Licenses, Boolean> locked = Licenses.getLocked(Script.getNRPID(target));
                for (Licenses license : Licenses.values()) {
                    player.sendMessage("     §8" + Messages.ARROW + " §7" + license.getName() + ": " + (locked.get(license) ? "§cJa" : "§aNein"));
                }
                return true;
            }

            Licenses license = Licenses.getByName(args[1]);
            if (license == null) {
                player.sendMessage(Messages.ERROR + "Lizenz nicht gefunden.");
                return true;
            }

            OfflinePlayer target = Script.getOfflinePlayer(args[0]);
            license.updateLocked(Script.getNRPID(target));
            player.sendMessage(PREFIX + "Lizenz " + license.getName() + " von " + target.getName() + (license.isLocked(Script.getNRPID(player)) ? " gesperrt" : " entsperrt") + ".");
            if (target.isOnline()) {
                target.getPlayer().sendMessage(PREFIX + "Deine Lizenz " + license.getName() + " wurde " + (license.isLocked(Script.getNRPID(player)) ? " gesperrt" : " entsperrt") + ".");
            } else {
                Script.addOfflineMessage(target, PREFIX + "Deine Lizenz " + license.getName() + " wurde " + (license.isLocked(Script.getNRPID(player)) ? " gesperrt" : " entsperrt") + ".");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arg = new ArrayList<>();
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (Player target : Bukkit.getOnlinePlayers()) arg.add(target.getName());
            for (String string : arg) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        } else if (args.length == 2) {
            for (Licenses license : Licenses.values()) arg.add(license.getName());
            for (String string : arg) if (string.toLowerCase().startsWith(args[1].toLowerCase())) completions.add(string);
        }
        return completions;
    }
}
