package de.newrp.Vehicle;

import de.newrp.API.Cache;
import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Police.Handschellen;
import de.newrp.NewRoleplayMain;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CarEnter implements Listener {
    @EventHandler
    public void onEnter(VehicleEnterEvent e) {
        if (e.getVehicle().getType().equals(EntityType.MINECART)) {
            Minecart mc = (Minecart) e.getVehicle();
            Player p = (Player) e.getEntered();
            if (mc.getCustomName() != null) return;
            Car car = Car.getCarByEntityID(mc.getEntityId());

            if (car == null) return;

            if (Kennzeichen.kfz.containsKey(p)) {
                e.setCancelled(true);
                if (!car.hasLicenseplate() && car.isCarOwner(p)) {
                    try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT id FROM vehicle WHERE kennzeichen='" + Kennzeichen.kfz.get(p) + "'")) {
                        if (rs.next()) {
                            p.sendMessage(Kennzeichen.prefix + "Das Kennzeichen ist bereits vergeben.");
                        } else {
                            car.setLicenseplate(Kennzeichen.kfz.get(p));
                            p.sendMessage(Kennzeichen.prefix + "Das Fahrzeug hat nun das Kennzeichen §e" + Kennzeichen.kfz.get(p));
                            Kennzeichen.kfz.remove(p);
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    p.sendMessage(Kennzeichen.prefix + "Das Fahrzeug hat bereits ein Kennzeichen.");
                    Kennzeichen.kfz.remove(p);
                }
            } else if (car.getStrafzettel() != null) {
                if (car.isCarOwner(p)) {

                    Strafzettel strafzettel = car.getStrafzettel();

                    p.sendMessage(Car.PREFIX + "Dein Fahrzeug hat ein Strafzettel §8[§6" + strafzettel.getReason() + "§7 | §6" + strafzettel.getPrice() + "$§8].");
                    p.sendMessage(Messages.INFO + "Du kannst ihn mit /payticket bezahlen.");
                    e.setCancelled(true);
                } else {
                    p.sendMessage(Car.PREFIX + "Das Fahrzeug ist abgeschlossen.");
                    e.setCancelled(true);
                }
            } else if (car.isLocked()) {
/*
                if (p.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD) && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§7Brechstange") && Fraktion.getFraktion(p).equals(Fraktion.LE_MILIEU)) {
                    if (car.getOwner().getLevel() >= 5) {
                        if (!SDuty.isAduty(car.getOwner())) {
                            if (!car.isCarOwner(p)) {
                                long time = System.currentTimeMillis();
                                p.sendMessage(Car.PREFIX + "Du versuchst nun das Auto aufzubrechen!");
                                p.sendMessage(Messages.INFO + "Klicke möglichst schnell den nicht leuchtenden Kürbis an.");
                                CarTheft.theft_time.put(p.getName(), time);
                                CarTheft.car_steal.put(p.getName(), car);
                                CarTheft.openGUI(p);
                                Script.sendLocalMessage(7, p, "§a§o* " + (Script.isAdmin(p) ? "[UC]" + p.getName() : p.getName()) + " versucht ein Auto aufzubrechen");
                            } else
                                p.sendMessage(Messages.ERROR + "Du kannst nicht dein eigenes Auto aufbrechen!");
                            e.setCancelled(true);
                            return;
                        } else
                            p.sendMessage(Messages.ERROR + "Der Autobesitzer ist im Admin-Dienst!");
                        e.setCancelled(true);
                        return;
                    } else
                        p.sendMessage(Messages.ERROR + "Der Autobesitzer ist unter Level 5!");
                }

                if (car.isCarOwner(p)) {
                    p.sendMessage(Car.PREFIX + "Dein Fahrzeug ist abgeschlossen.");
                    e.setCancelled(true);
                } else {
                    p.sendMessage(Car.PREFIX + "Das Fahrzeug ist abgeschlossen.");
                    e.setCancelled(true);
                }*/
            } else if (Handschellen.isCuffed(p)) {
                p.sendMessage(Messages.ERROR + "Du bist gefesselt.");
                e.setCancelled(true);
            } else if (!Licenses.FUEHRERSCHEIN.hasLicense(Script.getNRPID(p))) {
                p.sendMessage(Car.PREFIX + "Du hast keinen Führerschein.");
                e.setCancelled(true);
            } else if (!car.isActivated()) {
                p.sendMessage(Car.PREFIX + "Das Fahrzeug ist deaktiviert weil du deine KFZ-Steuer nicht gezahlt hast.");
            } else if (p.isInsideVehicle() && p.getVehicle().getType().equals(EntityType.ARMOR_STAND)) {
                p.sendMessage(Car.PREFIX + "Du kannst nicht in ein Fahrzeug einsteigen während du sitzt.");
            } else {
                if (CheckKFZ.isChecking(p)) {
                    CheckKFZ.check(p, car);
                    CheckKFZ.kfz_check.remove(p);
                    e.setCancelled(true);
                    return;
                }

                CarExit.cache.put(p, p.getScoreboard());
                Cache.loadScoreboard(p);
                e.setCancelled(false);
                mc.setMaxSpeed((car.getCarType() != null ? car.getCarType().getMaxSpeed() : CarType.ALGERARI.getMaxSpeed()));
                Drive.cars.put(p, car);
            }
        }
    }
}