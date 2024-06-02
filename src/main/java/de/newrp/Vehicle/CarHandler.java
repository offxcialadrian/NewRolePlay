package de.newrp.Vehicle;

import de.newrp.API.*;
import de.newrp.Administrator.Notifications;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.Shop.ShopItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class CarHandler implements Listener {

    @EventHandler
    public static void onDrive(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Boat) {
            Car car = Car.getCarByEntityID(event.getVehicle().getEntityId());
            if (car != null) {
                if (car.getDriver() != null) {
                    Player player = (Player) car.getDriver();
                    double speed = car.getSpeed();
                    double pitch;

                    Vector direction = car.getLocation().getDirection();

                    CarType carType = car.getCarType();
                    if (car.isStarted() && car.getFuel() > 0) {
                        if (speed < carType.getMaxSpeed()) {
                            speed += ((0.01 * carType.getMaxSpeed()) / ((0.1 * speed) + 1));
                        }
                        pitch = (5 - Math.pow(Math.abs(player.getLocation().getPitch()) / 90, 2)) / 5;
                        speed = speed * pitch;
                    } else {
                        if (speed > 0) {
                            speed -= 0.06;
                        } else {
                            car.setVelocity(direction.multiply(0));
                            return;
                        }
                    }
                    car.setSpeed(speed);

                    if (Math.round(speed * 100) > 5) {
                        double y = -1;
                        Location block = car.getBoatEntity().getLocation();
                        if (block.getBlock().getType() == Material.AIR) block.subtract(0, 1, 0);
                        Location next = car.getBoatEntity().getLocation().add(car.getBoatEntity().getLocation().getDirection()).add(0, 1, 0);
                        if (next.getBlock().getType() == Material.AIR) next.subtract(0, 1, 0);

                        if (block.getBlock().getBlockData() instanceof Slab) {
                            if (next.getBlock().getBlockData() instanceof Slab) {
                                Slab.Type blockType = ((Slab) block.getBlock().getBlockData()).getType();
                                Slab.Type nextType = ((Slab) next.getBlock().getBlockData()).getType();
                                if (blockType == Slab.Type.BOTTOM) {
                                    if (nextType == Slab.Type.TOP || nextType == Slab.Type.DOUBLE) {
                                        y = 0.5;
                                        speed = -speed;
                                    }
                                } else if (blockType == Slab.Type.TOP || blockType == Slab.Type.DOUBLE) {
                                    if (nextType == Slab.Type.BOTTOM) {
                                        y = 0.5;
                                        speed = -speed;
                                    }
                                }
                            }
                        }

                        if (block.getBlock().getBlockData() instanceof Slab) {
                            car.setVelocity(direction.multiply(speed));
                            double finalY = y;
                            if (finalY > 0 && car.getFallDistance() == 0) {
                                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> car.setVelocity(car.getLocation().getDirection().setY(finalY)), 1L);
                            }
                        } else if (block.getBlock().getType() != Material.AIR && block.getBlock().getType() != Material.IRON_TRAPDOOR) {
                            car.setSpeed(speed - 0.04);
                            car.crash(speed * 1.5);
                            car.setVelocity(direction.multiply(0));
                        }

                        if (car.getFallDistance() > 0) {
                            Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> car.setVelocity(car.getLocation().getDirection().setY(-1)), 2L);
                        }

                        car.addMileage(speed);
                        if (car.getFuel() > 0) car.removeFuel((float) (speed * 0.001 * carType.getConsumption()));
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Boat) {
            if (event.getEntered() instanceof Player) {
                Car car = Car.getCarByEntityID(event.getVehicle().getEntityId());
                if (car == null) {
                    event.setCancelled(true);
                    event.getVehicle().remove();
                    return;
                }

                Player player = (Player) event.getEntered();
                if (CheckKFZ.isChecking(player)) {
                    CheckKFZ.check(player, car);
                    CheckKFZ.kfz_check.remove(player);
                    event.setCancelled(true);
                } else if (Strafzettel.isTicketing(player)) {
                    if(Script.getNRPID(car.getOwner())==1) {
                        player.sendMessage(StrafzettelCommand.PREFIX + "Du kannst diesem Auto keinen Strafzettel geben.");
                        event.setCancelled(true);
                        return;
                    }
                    car.setStrafzettel(new Strafzettel(car.getCarID(), Strafzettel.reasons.get(player), Strafzettel.prices.get(player), Script.getNRPID(player)));
                    player.sendMessage(StrafzettelCommand.PREFIX + "Du hast den Strafzettel am Auto platziert.");
                    Beruf.Berufe.POLICE.sendMessage(StrafzettelCommand.PREFIX + Script.getName(player) + " hat ein Strafzettel an einem §e" + car.getCarType().getName() + " §7mit Kennzeichen §e" + car.getLicenseplate() + " §7platziert!" +
                            "\n" + StrafzettelCommand.PREFIX + "Grund: §e" + car.getStrafzettel().getReason() +
                            "\n" + StrafzettelCommand.PREFIX + "Preis: §e" + car.getStrafzettel().getPrice() + "€");
                    Activity.grantActivity(Script.getNRPID(player), Activities.STRAFZETTEL);
                    Debug.debug("Added strafzettel to " + car.getLicenseplate() + " by " + Script.getName(player));
                    Notifications.sendMessage(Notifications.NotificationType.PAYMENT, "§aStrafzettel an " + car.getLicenseplate() + " von " + player.getName() + " für " + Strafzettel.reasons.get(player) + " [" + Strafzettel.prices.get(player) + "€] gegeben.");
                    Strafzettel.reasons.remove(player);
                    Strafzettel.prices.remove(player);
                    event.setCancelled(true);
                } else if (Strafzettel.isRemoving(player)) {
                    if (car.getStrafzettel() != null) {
                        car.removeStrafzettel();
                        car.setStrafzettel(null);
                        player.sendMessage(StrafzettelCommand.PREFIX + "Du hast den Strafzettel vom Auto entfernt.");
                        Notifications.sendMessage(Notifications.NotificationType.PAYMENT, "§aStrafzettel an " + car.getLicenseplate() + " von " + player.getName() + " entfernt.");
                    } else {
                        player.sendMessage(StrafzettelCommand.PREFIX + "Dieses Auto hat keinen Strafzettel!");
                    }
                    Strafzettel.removes.remove(player);
                    event.setCancelled(true);
                } else {
                    if (SDuty.isSDuty(player)) {
                        event.setCancelled(true);
                        return;
                    }

                    if (car.getStrafzettel() != null) {
                        Strafzettel ticket = car.getStrafzettel();
                        if (car.isCarOwner(player)) {
                            event.getEntered().sendMessage(StrafzettelCommand.PREFIX + "Dein Auto hat einen Strafzettel!" +
                                    "\n" + StrafzettelCommand.PREFIX + "Grund: §e" + ticket.getReason() + "§7 | Preis: §e" + ticket.getPrice() + "€");
                            event.getEntered().sendMessage(Messages.INFO + "Bezahle dein Strafzettel mit /payticket");
                        } else {
                            event.getEntered().sendMessage(StrafzettelCommand.PREFIX + "Das Auto hat einen Strafzettel!");
                        }
                        event.setCancelled(true);
                        return;
                    }

                    if (car.isLocked()) {
                        if (car.isCarOwner(player)) {
                            event.getEntered().sendMessage(Component.text(Car.PREFIX + "Dein " + car.getCarType().getName() + " ist abgeschlossen!"));
                        } else {
                            event.getEntered().sendMessage(Component.text(Car.PREFIX + "Der " + car.getCarType().getName() + " ist abgeschlossen!"));
                            event.setCancelled(true);
                            return;
                        }

                        if(car.getCarType() != CarType.PAWSCHE && car.getCarType() != CarType.AWDI && car.getCarType() != CarType.MERCADAS) {
                            event.setCancelled(true);
                        } else if(car.isCarOwner(player)) {
                            event.getEntered().sendMessage(Component.text(Car.PREFIX + "Du hast deinen " + car.getCarType().getName() + " über Keyless-Go geöffnet!"));
                            car.setLocked(false);
                            Cache.saveScoreboard(player);
                            //player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                            car.setCarSidebar();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    //noinspection ConstantValue
                                    if (car != null && car.getPassengers().contains(player)) {
                                        car.updateCarSidebar();
                                    } else {
                                        //player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                                        cancel();
                                    }
                                }
                            }.runTaskTimer(NewRoleplayMain.getInstance(), 5L, 5L);

                            if (car.isCarOwner(player)) {
                                event.getEntered().sendMessage(Component.text(Car.PREFIX + "Du bist in deinen " + car.getCarType().getName() + " eingestiegen!"));
                            } else {
                                event.getEntered().sendMessage(Component.text(Car.PREFIX + "Du bist in den " + car.getCarType().getName() + " eingestiegen!"));
                            }
                        }
                    } else {
                        Cache.saveScoreboard(player);
                        //player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                        car.setCarSidebar();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                //noinspection ConstantValue
                                if (car != null && car.getPassengers().contains(player)) {
                                    car.updateCarSidebar();
                                } else {
                                    //player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                                    cancel();
                                }
                            }
                        }.runTaskTimer(NewRoleplayMain.getInstance(), 5L, 5L);

                        if (car.isCarOwner(player)) {
                            event.getEntered().sendMessage(Component.text(Car.PREFIX + "Du bist in deinen " + car.getCarType().getName() + " eingestiegen!"));
                        } else {
                            event.getEntered().sendMessage(Component.text(Car.PREFIX + "Du bist in den " + car.getCarType().getName() + " eingestiegen!"));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onCrash(VehicleEntityCollisionEvent event) {
        if (event.getVehicle() instanceof Boat) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                Car car = Car.getCarByEntityID(event.getVehicle().getEntityId());
                if (car != null) {
                    if (car.getSpeed() >= 0.1) {
                        player.setVelocity(player.getLocation().getDirection().multiply(-2));
                        player.damage(Math.floor(car.getSpeed() * 20));
                        car.crash(Math.floor(car.getSpeed() * 10));
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onExit(VehicleExitEvent event) {
        if (event.getVehicle() instanceof Boat) {
            Car car = Car.getCarByEntityID(event.getVehicle().getEntityId());
            if (car !=  null) {
                car.setSpeed(0D);
                car.setFuel(car.getFuel());
                car.setMileage(car.getMileage());
                //((Player) event.getExited()).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                Cache.loadScoreboard((Player) event.getExited());
                car.saveLocation(car.getLocation());
            }
        }
    }

    @EventHandler
    public static void onDamage(VehicleDamageEvent event) {
        if (event.getVehicle() instanceof Boat) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public static void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getOpenInventory().title().equals(Component.text("Fahrzeuge"))) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            String plate;
            if (item != null) {
                plate = Objects.requireNonNull(((TextComponent) item.getItemMeta().displayName())).content();
                Car car = Car.getCarByLicenseplateCheckOwner(plate, player);
                if (car != null) {
                    if (item.getType() == Material.LIME_DYE) {
                        car.setLocked(true);
                        player.getWorld().playSound(car.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0F, 0.9F);
                        player.sendMessage(Component.text(Car.PREFIX + "Du hast deinen " + car.getCarType().getName() + " abgeschlossen."));
                    } else if (item.getType() == Material.RED_DYE) {
                        car.setLocked(false);
                        player.getWorld().playSound(car.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0F, 1.0F);
                        player.sendMessage(Component.text(Car.PREFIX + "Du hast deinen " + car.getCarType().getName() + " aufgeschlossen."));
                    } else if (item.getType() == Material.ACACIA_BOAT || item.getType() == Material.BIRCH_BOAT || item.getType() == Material.JUNGLE_BOAT || item.getType() == Material.OAK_BOAT || item.getType() == Material.SPRUCE_BOAT || item.getType() == Material.DARK_OAK_BOAT) {
                        player.performCommand("navi " + car.getLocation().getBlockX() + "/" + car.getLocation().getBlockY() + "/" + car.getLocation().getBlockZ());
                    }
                }
                event.getInventory().close();
            }
        }
    }

    @EventHandler
    public static void onInsurance(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.PAPER) {
            if (event.getRightClicked() instanceof Boat) {
                Player player = event.getPlayer();
                Car car = Car.getCarByEntityID(event.getRightClicked().getEntityId());
                ItemStack paper = event.getPlayer().getInventory().getItemInMainHand();
                String text = paper.getItemMeta().getDisplayName();
                if (text.contains("Versicherung")) {
                    if (car.isCarOwner(player)) {
                        car.setInsurance(car.getInsurance() + 1);
                        paper.setAmount(paper.getAmount() - 1);
                        player.sendMessage(Car.PREFIX + "Dein " + car.getCarType().getName() + " ist nun " + car.getInsurance() + "x versichert.");
                    } else {
                        player.sendMessage(Car.PREFIX + "Dieses Auto gehört dir nicht!");
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public static void onRepair(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHEARS) {
            if (event.getRightClicked() instanceof Boat) {
                Player player = event.getPlayer();
                Car car = Car.getCarByEntityID(event.getRightClicked().getEntityId());
                ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
                String text = tool.getItemMeta().getDisplayName();
                if (text.contains("Werkzeug")) {
                    double heal = car.getCarheal();
                    if (heal < car.getCarType().getCarheal()) {
                        car.setCarHeal(heal + 2);
                        tool.setDurability((short) (tool.getDurability() + 1));
                        if ((int) tool.getDurability() % 20 == 0) player.getWorld().playSound(car.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
                        if (tool.getDurability() > 238) {
                            tool.setAmount(0);
                            player.getWorld().playSound(car.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                        }
                    } else {
                        player.sendMessage(Component.text(Car.PREFIX + "Dieses Auto hat keine Schäden!"));
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public static void onKanister(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == ShopItem.KANISTER.getItemStack().getType()) {
            if (event.getRightClicked() instanceof Boat) {
                Player player = event.getPlayer();
                Car car = Car.getCarByEntityID(event.getRightClicked().getEntityId());
                ItemStack kanister = event.getPlayer().getInventory().getItemInMainHand();
                String text = kanister.getItemMeta().getDisplayName();
                if (text.contains("Kanister")) {
                    if (car.getFuel() < 100) {
                        car.fill(10);
                        player.getWorld().playSound(car.getLocation(), Sound.AMBIENT_UNDERWATER_LOOP_ADDITIONS, 1.0F, 1.0F);
                        kanister.setAmount(kanister.getAmount() - 1);
                        player.sendMessage(Component.text(Car.PREFIX + "Du hast den Tank aufgefüllt."));
                    } else {
                        player.sendMessage(Component.text(Car.PREFIX + "Der Tank ist voll!"));
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
}
