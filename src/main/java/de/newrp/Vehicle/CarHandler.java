package de.newrp.Vehicle;

import de.newrp.API.Cache;
import de.newrp.API.ScoreboardManager;
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
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class CarHandler implements Listener {

    @EventHandler
    public static void onDrive(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Boat) {
            Car car = Car.getCarByEntityID(event.getVehicle().getEntityId());
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
                        car.crash(speed * 2);
                        car.setVelocity(direction.multiply(0));
                    }

                    if (car.getFallDistance() > 0) {
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> car.setVelocity(car.getLocation().getDirection().setY(-1)), 2L);
                    }

                    car.setMileage(car.getMileage() + speed);
                    if (car.getFuel() > 0) car.fill((float) (speed * -0.001 * carType.getConsumption()));
                }
            }
        }
    }

    @EventHandler
    public static void onEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Boat) {
            if (event.getEntered() instanceof Player) {
                Car car = Car.getCarByEntityID(event.getVehicle().getEntityId());
                if (car.isLocked()) {
                    Player player = (Player) event.getEntered();
                    if (car.isCarOwner(player)) {
                        event.getEntered().sendMessage(Component.text(Car.PREFIX + "Dein " + car.getCarType().getName() + " ist abgeschlossen!"));
                    } else {
                        event.getEntered().sendMessage(Component.text(Car.PREFIX + "Der " + car.getCarType().getName() + " ist abgeschlossen!"));
                    }

                    event.setCancelled(true);
                } else {
                    Cache.saveScoreboard(((Player) event.getEntered()));
                    car.setCarSidebar();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            //noinspection ConstantValue
                            if (car != null && car.getDriver() != null) {
                                car.updateCarSidebar();
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimer(NewRoleplayMain.getInstance(), 5L, 5L);

                    Player player = (Player) event.getEntered();
                    if (car.isCarOwner(player)) {
                        event.getEntered().sendMessage(Component.text(Car.PREFIX + "Du bist in deinen " + car.getCarType().getName() + " eingestiegen!"));
                    } else {
                        event.getEntered().sendMessage(Component.text(Car.PREFIX + "Du bist in den " + car.getCarType().getName() + " eingestiegen!"));
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
                ((Player) event.getExited()).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
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
            String plate = Objects.requireNonNull(((TextComponent) Objects.requireNonNull(event.getCurrentItem()).getItemMeta().displayName())).content();
            Car car = Car.getCarByLicenseplateCheckOwner(plate, player);
            if (car != null) {
                assert item != null;
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
            } // Hier könnte man noch eine automatische Bugmeldung hinzufügen
            event.getInventory().close();
        }
    }

    @EventHandler
    public static void onClick(PlayerInteractEntityEvent event) {
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
                        player.sendMessage(Car.PREFIX + "Dein " + car.getCarType().getName() + " nun " + car.getInsurance() + "x versichert.");
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
                        car.setCarHeal(heal + 1);
                        tool.setDurability((short) (tool.getDurability() + 1));
                        if ((int) Math.round(heal) % 20 == 0) player.getWorld().playSound(car.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
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
