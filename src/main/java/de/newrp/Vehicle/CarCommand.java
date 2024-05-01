package de.newrp.Vehicle;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CarCommand implements @Nullable CommandExecutor {

    private static Component PREFIX = Component.text("[").color(TextColor.color(Color.GRAY.asRGB()))
                                .append(Component.text("Auto").color(TextColor.color(Color.ORANGE.asRGB())))
                                .append(Component.text("] ").color(TextColor.color(Color.GRAY.asRGB())));

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            if (args.length > 0) {
                switch (args[0]) {
                    case "start":
                        if (Objects.requireNonNull(player).isInsideVehicle()) {
                            CarHandler.cars.put((Boat) player.getVehicle(), true);
                            player.sendMessage(PREFIX.append(Component.text("Du hast deinen Motor gestartet.")
                                    .color(TextColor.color(Color.SILVER.asRGB()))));
                        } else {
                            player.sendMessage(PREFIX.append(Component.text("Du befindest dich nicht in einem Auto!")
                                    .color(TextColor.color(Color.SILVER.asRGB()))));
                        }
                        return true;
                    case "stop":
                        if (Objects.requireNonNull(player).isInsideVehicle()) {
                            if (player.getVehicle() instanceof Boat) {
                                CarHandler.cars.put((Boat) player.getVehicle(), false);
                                player.sendMessage(PREFIX.append(Component.text("Du hast deinen Motor angehalten.")
                                        .color(TextColor.color(Color.SILVER.asRGB()))));
                            }
                        } else {
                            player.sendMessage(PREFIX.append(Component.text("Du befindest dich nicht in einem Auto!")
                                    .color(TextColor.color(Color.SILVER.asRGB()))));
                        }
                    default:
                        // Fehlermeldung für falsches Argument anzeigen
                        return true;
                }
            } else {
                // Nur exemplarisch, ansonsten Fehlermeldung für fehlende Argumente anzeigen
                Boat car = (Boat) Objects.requireNonNull(player).getWorld().spawnEntity(player.getLocation().add(0, 0.5, 0), EntityType.BOAT);
                car.setCustomName(player.getName());
                CarHandler.cars.putIfAbsent(car, null);
            }
        }

        return true;
    }
}
