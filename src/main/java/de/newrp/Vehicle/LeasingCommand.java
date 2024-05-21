package de.newrp.Vehicle;

import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Government.Stadtkasse;
import de.newrp.NewRoleplayMain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LeasingCommand implements CommandExecutor, TabCompleter {

    public static String PREFIX = "§8[§2Leasing§8] §2" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (Beruf.hasBeruf(player)) {
                Beruf.Berufe beruf = Beruf.getBeruf(player);
                if (beruf == null) {
                    player.sendMessage(Messages.ERROR + "Dein Beruf konnte nicht ermittelt werden!");
                } else {
                    if (beruf.isLeader(player, true)) {
                        if (args.length > 0 ) {
                            switch (args[0].toLowerCase()) {
                                case "info":
                                    player.sendMessage(PREFIX + "Autos im Besitz: §2" + beruf.getCarAmount() + "x");
                                    player.sendMessage(PREFIX + "Vergebene Autos: §2" + beruf.getLeasedAmount() + "x");
                                    player.sendMessage(PREFIX + "Preis pro Auto: §2" + beruf.getCarType().getPrice() + "€");
                                    player.sendMessage(PREFIX + "Gebühren pro Stunde: §2" + beruf.getLeasedAmount() * (beruf.getCarType().getTax() / 2) + "€");
                                    break;
                                case "add":
                                case "hinzufügen":
                                    if (args.length >= 2 && args[1].equals("confirm")) {
                                        if (beruf.getCarAmount() < 99) {
                                            beruf.setCarAmount(beruf.getCarAmount() + 1);
                                            player.sendMessage(PREFIX + "Anzahl geleaster Wägen aufgestuft.");
                                            player.sendMessage(PREFIX + "Menge: " + beruf.getCarAmount());
                                            Stadtkasse.removeStadtkasse(beruf.getCarType().getPrice(), "Aufstufung Leasing " + beruf.getName());
                                        } else {
                                            player.sendMessage(PREFIX + "Ihr könnt nicht mehr als 99 Wägen leasen.");
                                        }
                                    } else {
                                        player.sendMessage(PREFIX + "Verwende §2confirm §7um deinen Kauf zu bestätigen.");
                                    }
                                    break;
                                case "remove":
                                case "entfernen":
                                    if (args.length >= 2 && args[1].equals("confirm")) {
                                        if (beruf.getCarAmount() > 0) {
                                            if (beruf.getLeasedAmount() == beruf.getCarAmount()) {
                                                player.sendMessage(PREFIX + "Du kannst kein Auto verkaufen, da alle vergeben sind.");
                                            } else {
                                                beruf.setCarAmount(beruf.getCarAmount() - 1);
                                                player.sendMessage(PREFIX + "Anzahl geleaster Wägen heruntergestuft.");
                                                player.sendMessage(PREFIX + "Menge: " + beruf.getCarAmount());
                                            }
                                        } else {
                                            player.sendMessage(PREFIX + "Ihr least bereits keine Autos mehr.");
                                        }
                                    } else {
                                        player.sendMessage(PREFIX + "Verwende §2confirm §7um deinen Verkauf zu bestätigen.");
                                    }
                                    break;
                                case "give":
                                case "geben":
                                    if (beruf.getLeasedAmount() >= beruf.getCarAmount()) {
                                        player.sendMessage(PREFIX + "Aktuell sind alle Wägen geleased.");
                                        return true;
                                    }

                                    if (args.length >= 2) {
                                        Player target = Script.getPlayer(args[1]);
                                        if (target == null) {
                                            player.sendMessage(Messages.ERROR + "Spieler " + args[1] + " konnte nicht gefunden werden!");
                                        } else {
                                            if (!Licenses.FUEHRERSCHEIN.hasLicense(Script.getNRPID(target))) {
                                                player.sendMessage(PREFIX + args[1] + " hat keinen Führerschein!");
                                                return true;
                                            }


                                            Car car = Car.createCar(beruf.getCarType(), beruf.getGarage(), target);
                                            if (car != null) {
                                                car.setLicenseplate("N-RP-" + String.format("%02d", beruf.getID()) + String.format("%02d", getC(beruf)));
                                                car.setActivated(true);
                                                beruf.setLeasedAmount(beruf.getLeasedAmount() + 1);
                                                player.sendMessage(PREFIX + "Du hast " + args[1] + " einen " + car.getCarType().getName() + " freigegeben. (" + beruf.getLeasedAmount() + "/" + beruf.getCarAmount() + ")");
                                                target.sendMessage(PREFIX + player.getName() + " hat dir einen " + car.getCarType().getName() + " freigegeben.");
                                            }
                                        }
                                    }
                                    break;
                                case "take":
                                case "nehmen":
                                    if (beruf.getLeasedAmount() <= 0) {
                                        player.sendMessage(PREFIX + "Aktuell sind keine Wägen geleased.");
                                        break;
                                    }

                                    if (args.length >= 2) {
                                        Player target = Script.getPlayer(args[1]);
                                        if (target == null) {
                                            Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                                                final List<LeasingData> allCars = getCarList(beruf);
                                                final Optional<LeasingData> leasingDataForArgPlayer = allCars.stream().filter(e -> e.userName().equalsIgnoreCase(args[1])).findFirst();
                                                if(!leasingDataForArgPlayer.isPresent()) {
                                                    player.sendMessage(Messages.ERROR + "Spieler " + args[1] + " hat keinen aktiven Leasingvertrag!");
                                                    return;
                                                }

                                                Script.executeAsyncUpdate("DELETE FROM vehicle WHERE id=" + leasingDataForArgPlayer.get().carId());
                                                beruf.setLeasedAmount(beruf.getLeasedAmount() - 1);
                                                player.sendMessage(PREFIX + "Du hast den " + beruf.getCarType().getName() + " von " + args[1] + " zurückgestellt. (" + beruf.getLeasedAmount() + "/" + beruf.getCarAmount() + ")");
                                            });
                                        } else {
                                            Car car = getCar(target, beruf);
                                            if (car != null) {
                                                car.destroy(false);
                                                beruf.setLeasedAmount(beruf.getLeasedAmount() - 1);
                                                player.sendMessage(PREFIX + "Du hast den " + car.getCarType().getName() + " von " + args[1] + " zurückgestellt. (" + beruf.getLeasedAmount() + "/" + beruf.getCarAmount() + ")");
                                                target.sendMessage(PREFIX + player.getName() + " hat deinen " + car.getCarType().getName() + " zurückgestellt.");
                                            } else {
                                                player.sendMessage(PREFIX + args[1] + " hat keinen Wagen geleast.");
                                            }
                                        }
                                    }
                                    break;
                                case "list":
                                    Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                                        final List<LeasingData> output = getCarList(beruf);
                                        if(output.isEmpty()) {
                                            player.sendMessage(PREFIX + "Aktuell sind keine Autos vergeben");
                                            return;
                                        }

                                        player.sendMessage(PREFIX + "Folgende Spieler haben ein aktives Leasing");
                                        for (LeasingData leasingData : output) {
                                            Script.sendClickableMessage(player, PREFIX + leasingData.userName() + " hat einen aktiven Leasingvertrag (" + leasingData.licensePlate() + ")", "/leasing take " + leasingData.userName(), "§cAuto wegnehmen");
                                        }
                                    });
                                    break;
                                default:
                                    player.sendMessage(PREFIX + args[0] + " ist als Argument ungültig!");
                                    break;
                            }
                        } else {
                            player.sendMessage(Messages.ERROR + "/leasing [info/list/add/remove/give/take]");
                        }
                    } else {
                        player.sendMessage(Messages.ERROR + "Du bist kein Leader!");
                    }
                }
            } else {
                player.sendMessage(Messages.ERROR + "Du hast keinen Beruf!");
            }
        }
        return true;
    }

    private static @Nullable Car getCar(Player target, Beruf.Berufe beruf) {
        Car car = null;
        for (Car cars : Car.CARS) {
            if (cars.getOwner() == target) {
                if (cars.getLicenseplate().startsWith("N-RP-")) {
                    if (Integer.parseInt(cars.getLicenseplate().replaceFirst("N-RP-", "").substring(0, 2)) == beruf.getID()) {
                        car = cars;
                    }
                }
            }
        }
        return car;
    }

    public static int getC(Beruf.Berufe beruf) {
        int c = 1;
        for (Car cars : Car.CARS) {
            if (cars.getLicenseplate().startsWith("N-RP-")) {
                if (Integer.parseInt(cars.getLicenseplate().replaceFirst("N-RP-", "").substring(0, 2)) == beruf.getID()) {
                c += 1;
                }
            }
        }
        return c;
    }

    private List<LeasingData> getCarList(final Beruf.Berufe faction) {
        final List<LeasingData> list = new ArrayList<>();
        try(final Statement statement = NewRoleplayMain.getConnection().createStatement();
            final ResultSet resultSet = statement.executeQuery("select v.id, nid.uuid, nid.name, v.kennzeichen from vehicle v left join nrp_id nid on nid.id=v.owner where kennzeichen like 'N-RP-0" + faction.getID() + "%'")) {
            while(resultSet.next()) {
                list.add(new LeasingData(resultSet.getString(3), UUID.fromString(resultSet.getString(2)), resultSet.getInt(1), resultSet.getString(4)));
            }
        } catch(final Exception exception) {
            NewRoleplayMain.handleError(exception);
        }
        return list;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String[] args1 = new String[] {"add", "hinzufügen", "remove", "entfernen", "give", "geben", "take", "nehmen", "info", "list"};
        List<String> args2 = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) args2.add(player.getName());
        List<String> completions = new ArrayList<>();
        if (args.length == 1) for (String string : args1) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        if (args.length == 2) for (String string : args2) if (string.toLowerCase().startsWith(args[1].toLowerCase())) completions.add(string);
        return completions;
    }

    @Data
    @Accessors(fluent = true)
    @AllArgsConstructor
    protected static class LeasingData {
        private final String userName;
        private final UUID uuid;
        private final int carId;
        private String licensePlate;
    }
}
