package de.newrp.Vehicle;

import de.newrp.API.Messages;
import de.newrp.API.Navi;
import de.newrp.API.Premium;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bouncycastle.asn1.cms.MetaData;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.comphenix.protocol.PacketType.Play.Server.COMMANDS;

public class CarCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();

            if (args.length > 0) {
                List<Car> cars;
                switch (args[0]) {
                    case "start":
                        if (Objects.requireNonNull(player).isInsideVehicle()) {
                            Car.getCarByEntityID(Objects.requireNonNull(player.getVehicle()).getEntityId()).setLocked(false);
                            player.sendMessage(Component.text(Car.PREFIX).append(Component.text("Du hast deinen Motor gestartet.")
                                    .color(TextColor.color(Color.SILVER.asRGB()))));
                        } else {
                            player.sendMessage(Component.text(Car.PREFIX).append(Component.text("Du befindest dich nicht in einem Auto!")
                                    .color(TextColor.color(Color.SILVER.asRGB()))));
                        }
                        break;
                    case "stop":
                        if (Objects.requireNonNull(player).isInsideVehicle()) {
                            if (player.getVehicle() instanceof Boat) {
                                Car.getCarByEntityID(Objects.requireNonNull(player.getVehicle()).getEntityId()).setLocked(true);
                                player.sendMessage(Component.text(Car.PREFIX).append(Component.text("Du hast deinen Motor angehalten.")
                                        .color(TextColor.color(Color.SILVER.asRGB()))));
                            }
                        } else {
                            player.sendMessage(Component.text(Car.PREFIX).append(Component.text("Du befindest dich nicht in einem Auto!")
                                    .color(TextColor.color(Color.SILVER.asRGB()))));
                        }
                        break;
                    case "lock":
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
                                        lock = new ItemStack(Material.RED_WOOL);
                                    } else {
                                        lock = new ItemStack(Material.GREEN_WOOL);
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
                    default:
                        Objects.requireNonNull(player).sendMessage(Component.text(Messages.ERROR + "/car [start/stop/lock/find]"));
                        break;
                }
            } else {
                Objects.requireNonNull(player).sendMessage(Component.text(Messages.ERROR + "/car [start/stop/lock/find]"));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String[] args1 = new String[] {"start", "stop", "lock", "find"};
        List<String> completions = new ArrayList<>();
        if (args.length == 1) for (String string : args1) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        return completions;
    }
}
