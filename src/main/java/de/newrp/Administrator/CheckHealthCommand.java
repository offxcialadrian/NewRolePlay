package de.newrp.Administrator;

import de.newrp.API.Health;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Player.HealthCommand;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class CheckHealthCommand implements CommandExecutor {

    public static String PREFIX = "§8[§6Gesundheit§8] §6" + Messages.ARROW + " ";
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final Beruf.Berufe faction = Beruf.getBeruf(player);

        if(faction != Beruf.Berufe.RETTUNGSDIENST && !SDuty.isSDuty(player)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 0) {
            player.sendMessage(Messages.ERROR + "/checkhealth [Name]");
            return true;
        }

        final Player targetPlayer = Bukkit.getPlayer(args[0]);
        if(targetPlayer == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if (player.getLocation().distance(targetPlayer.getLocation()) > 3) {
            player.sendMessage(Messages.ERROR + "Der Spieler ist zu weit von dir entfernt!");
            return true;
        }

        player.sendMessage(PREFIX + Script.getName(targetPlayer) + "' Zustand:");
        player.spigot().sendMessage(HealthCommand.health(targetPlayer));
        player.spigot().sendMessage(HealthCommand.blood(targetPlayer));
        player.spigot().sendMessage(HealthCommand.hunger(targetPlayer));
        player.spigot().sendMessage(HealthCommand.thirst(targetPlayer));
        player.spigot().sendMessage(HealthCommand.fat(targetPlayer));
        player.spigot().sendMessage(HealthCommand.muscle(targetPlayer));
        return true;
    }
}
