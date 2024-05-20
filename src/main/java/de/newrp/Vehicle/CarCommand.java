package de.newrp.Vehicle;

import de.newrp.API.*;
import de.newrp.Shop.gasstations.GasStationBuyHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CarCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            if (args.length > 0) {
                List<Car> cars;
                switch (args[0].toLowerCase()) {
                    case "start":
                        if (Objects.requireNonNull(player).isInsideVehicle()) {
                            if (player.getVehicle() instanceof Boat) {
                                if (GasStationBuyHandler.refuels.containsKey(player)) {
                                    player.sendMessage(Component.text(Car.PREFIX + "Du hast noch nicht bezahlt!"));
                                } else {
                                    Car car = Car.getCarByEntityID(Objects.requireNonNull(player.getVehicle()).getEntityId());
                                    car.setStarted(true);
                                    car.setVelocity(car.getLocation().getDirection().multiply(0.2));
                                    player.sendMessage(Component.text(Car.PREFIX + "Du hast deinen Motor gestartet."));
                                }
                            }
                        } else {
                            player.sendMessage(Component.text(Car.PREFIX + "Du befindest dich nicht in einem Auto!"));
                        }
                        break;
                    case "stop":
                        if (Objects.requireNonNull(player).isInsideVehicle()) {
                            if (player.getVehicle() instanceof Boat) {
                                Car.getCarByEntityID(Objects.requireNonNull(player.getVehicle()).getEntityId()).setStarted(false);
                                player.sendMessage(Component.text(Car.PREFIX + "Du hast deinen Motor angehalten."));
                            }
                        } else {
                            player.sendMessage(Component.text(Car.PREFIX + "Du befindest dich nicht in einem Auto!"));
                        }
                        break;
                    case "lock":
                    case "open":
                        cars = Car.getCars(player);
                        if (cars.isEmpty()) {
                            Objects.requireNonNull(player).sendMessage(Component.text(Messages.ERROR + "Du besitzt keine Autos!"));
                        } else {
                            Inventory gui = Bukkit.createInventory(player, 9, "Fahrzeuge");
                            assert player != null;
                            for (Car car : cars) {
                                double distance = car.getLocation().distance(player.getLocation());
                                if (Premium.hasPremium(player) ? distance < 10 : distance < 5) {
                                    ItemStack lock;
                                    if (car.isLocked()) {
                                        lock = new ItemStack(Material.RED_DYE);
                                    } else {
                                        lock = new ItemStack(Material.LIME_DYE);
                                    }
                                    ItemMeta lockMeta = lock.getItemMeta();
                                    lockMeta.displayName(Component.text(car.getLicenseplate()).color(TextColor.color(Color.ORANGE.asRGB())));
                                    lock.setItemMeta(lockMeta);
                                    gui.addItem(lock);
                                }
                            }
                            Objects.requireNonNull(player).openInventory(gui);
                        }
                        break;
                    case "find":
                        cars = Car.getCars(player);
                        if (cars.isEmpty()) {
                            Objects.requireNonNull(player).sendMessage(Component.text(Messages.ERROR + "Du besitzt keine Autos!"));
                        } else {
                            Inventory gui = Bukkit.createInventory(player, 9, "Fahrzeuge");
                            for (Car car : cars) {
                                ItemStack icon = new ItemStack(car.getCarType().getMaterial());
                                ItemMeta iconMeta = icon.getItemMeta();
                                iconMeta.displayName(Component.text(car.getLicenseplate()).color(TextColor.color(Color.ORANGE.asRGB())));
                                icon.setItemMeta(iconMeta);
                                gui.addItem(icon);
                            }
                            Objects.requireNonNull(player).openInventory(gui);
                        }
                        break;
                    case "fill":
                        assert player != null;
                        player.performCommand("tanken");
                        break;
                    case "sell":
                        if (Objects.requireNonNull(player).isInsideVehicle()) {
                            if (player.getVehicle() instanceof Boat) {
                                if (player.getLocation().distance(HologramList.KFZSTELLE.getLocation()) <= 10) {
                                    Car car = Car.getCarByEntityID(Objects.requireNonNull(Objects.requireNonNull(player).getVehicle()).getEntityId());
                                    if (car.isCarOwner(player)) {
                                        if (args.length >= 2 && Objects.equals(args[1], "confirm")) {
                                            car.destroy(false);
                                            player.sendMessage(Component.text(Car.PREFIX + "Du hast deinen " + car.getCarType().getName() + " verkauft."));
                                            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                                            // To-Do: Geld hinzufügen
                                            Cache.loadScoreboard(player);
                                        } else {
                                            player.sendMessage(Component.text(Car.PREFIX + "Verwende §6/car sell confirm §7um den Verkauf zu bestätigen!"));
                                        }
                                    } else {
                                        player.sendMessage(Component.text(Car.PREFIX + "Du kannst kein fremdes Auto verkaufen!"));
                                    }
                                } else {
                                    player.sendMessage(Component.text(Car.PREFIX + "Du kannst dein Auto nur bei einer KFZ-Anmeldestelle verkaufen!"));
                                }
                            }
                        } else {
                            player.sendMessage(Component.text(Car.PREFIX + "Du befindest dich nicht in einem Auto!"));
                        }
                        break;
                    case "teleport":
                        assert player != null;
                        if (Script.hasRank(player, Rank.DEVELOPER, false)) {
                            if (args.length >= 2) {
                                if (!args[1].startsWith("N-")) args[1] = "N-" + args[1];
                                Car car = Car.getCarByLicenseplate(args[1]);
                                if (car != null) {
                                    car.getBoatEntity().teleport(player.getLocation());
                                    player.sendMessage(Component.text(Car.PREFIX + "Du hast das Auto N-" + args[1].replaceFirst("N-", "") + " zu dir teleportiert."));
                                } else {
                                    player.sendMessage(Component.text(Messages.ERROR + "N-" + args[1].replaceFirst("N-", "") + " ist kein gültiges Kennzeichen!"));
                                }
                            } else {
                                player.sendMessage(Component.text(Messages.ERROR + "Du musst ein Kennzeichen angeben!"));
                            }
                            break;
                        }
                    default:
                        Objects.requireNonNull(player).sendMessage(Component.text(Messages.ERROR + "/car [start/stop/lock/find/fill/sell]"));
                        break;
                }
            } else {
                Objects.requireNonNull(player).sendMessage(Component.text(Messages.ERROR + "/car [start/stop/lock/find/fill/sell]"));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> args1 = Arrays.asList("start", "stop", "lock", "open", "find", "fill", "sell", "teleport");
        List<String> args2 = new ArrayList<>();
        List<String> completions = new ArrayList<>();
        if (args.length == 1) for (String string : args1) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("teleport")) {
                if (Script.hasRank((Player) sender, Rank.SUPPORTER, false)) {
                    for (Car car : Car.CARS) {
                        if (args[1].startsWith("N-") || args[1].isEmpty()) args2.add(car.getLicenseplate());
                        else if (args[1].startsWith("N") && args[1].length() == 1) args2.add(car.getLicenseplate());
                        else args2.add(car.getLicenseplate().replaceFirst("N-", ""));
                    }
                    for (String string : args2) if (string.toLowerCase().startsWith(args[1].toLowerCase())) completions.add(string);
                }
            }
        }
        return completions;
    }
}
