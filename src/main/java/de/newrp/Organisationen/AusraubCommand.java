package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Player.Annehmen;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AusraubCommand implements CommandExecutor {

    public static String PREFIX = "§8[§2Ausraub§8] §2" + Messages.ARROW + " §7";

    public static Map<UUID, Long> robs = new HashMap<>();
    public static Map<UUID, UUID> offer = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(Messages.ERROR + "/ausraub [player]");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Messages.ERROR + "Der Spieler wurde nicht gefunden.");
                return true;
            }

            if (player.getName().equals(target.getName())) {
                player.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst ausgeraubt haben.");
                return true;
            }

            if (!Organisation.hasOrganisation(target)) {
                player.sendMessage(Messages.ERROR + "Der Spieler ist in keiner Organisation.");
                return true;
            }

            Annehmen.offer.put(target.getName() + ".rob", player.getName());
            offer.put(target.getUniqueId(), player.getUniqueId());
            player.sendMessage(PREFIX + "Du hast " + target.getName() + " eine Anfrage zum Eintragen des Ausraubs gemacht.");
            target.sendMessage(PREFIX + player.getName() + " hat dir eine Anfrage zum Eintragen des Ausraubs gemacht.");
            Script.sendAcceptMessage(target);
        }

        return true;
    }
}
