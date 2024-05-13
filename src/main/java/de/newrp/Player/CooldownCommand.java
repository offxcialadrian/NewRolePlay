package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.Organisationen.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class CooldownCommand implements CommandExecutor {

    public static final String PREFIX = "§8[§5Cooldown§8] §5" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            boolean cd = false;
            if (Organisation.hasOrganisation(player)) {
                Organisation orga = Organisation.getOrganisation(player);

                player.sendMessage(PREFIX + "Aktuelle Cooldowns für §5" + orga.getName() + "§7:");
                if (RobCommand.cooldownsP.containsKey(player.getUniqueId())) {
                    if (RobCommand.cooldownsP.get(player.getUniqueId()) - System.currentTimeMillis() > 0) {
                        cd = true;
                        player.sendMessage(PREFIX + "Rob: §5" + TimeUnit.MILLISECONDS.toMinutes(RobCommand.cooldownsP.get(player.getUniqueId()) - System.currentTimeMillis()) + "min");
                    }
                }
                if (LabBreakIn.cooldown != 0) {
                    if (System.currentTimeMillis() - LabBreakIn.cooldown > 0) {
                        cd = true;
                        player.sendMessage(PREFIX + "Labor: §5" + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - LabBreakIn.cooldown) + "min");
                    }
                }
                if (BreakinCommand.cooldowns.containsKey(orga)) {
                    if (BreakinCommand.cooldowns.get(orga) - System.currentTimeMillis() > 0) {
                        cd = true;
                        player.sendMessage(PREFIX + "Break-In: §5" + TimeUnit.MILLISECONDS.toMinutes(BreakinCommand.cooldowns.get(orga) - System.currentTimeMillis()) + "min");
                    }
                }
                if (Bankraub.lastTime != 0) {
                    if (System.currentTimeMillis() - Bankraub.lastTime > 0) {
                        cd = true;
                        player.sendMessage(PREFIX + "Staatsbank: §5" + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - Bankraub.lastTime) + "min");
                    }
                }

                if (!cd) player.sendMessage(PREFIX + "Es gibt aktuell keine Cooldowns!");
                return true;
            }

            player.sendMessage(Messages.ERROR + "Du hast keine Cooldowns!");
        }
        return true;
    }
}
