package de.newrp.Organisationen;

import com.comphenix.protocol.PacketType;
import de.newrp.API.Navi;
import de.newrp.API.Route;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.NaviCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LockpickHandler implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getOpenInventory().title() instanceof TextComponent) {
            if (((TextComponent) event.getWhoClicked().getOpenInventory().title()).content().contains("Schloss")) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                if (event.getCurrentItem() != null) {
                    String display = Objects.requireNonNull(((TextComponent) Objects.requireNonNull(event.getCurrentItem()).getItemMeta().displayName())).content();
                    if (Objects.requireNonNull(event.getCurrentItem()).getType() == Material.HOPPER) {
                        String type = ((TextComponent) event.getWhoClicked().getOpenInventory().title()).content().replace("Schloss - ", "");
                        int n = Integer.parseInt(Objects.requireNonNull(((TextComponent) event.getCurrentItem().getItemMeta().displayName())).content().substring(2, 3));
                        if (n == a.get(player)) {
                            if (Objects.equals(a.get(player), c.get(player))) {
                                if (c.get(player) > 7) {
                                    Organisation.getOrganisation(player).sendMessage(BreakinCommand.PREFIX + player.getName() + " ist der Einbruch gelungen.");
                                    event.getInventory().close();
                                    win(player, type);
                                } else {
                                    a.put(player, 1);
                                    c.put(player, c.get(player) + 1);
                                    next(player, type);
                                }
                            } else {
                                a.put(player, a.get(player) + 1);
                                refresh(player, event.getSlot() - 10);
                            }
                        } else {
                            if (f.get(player) == 0) {
                                Beruf.Berufe.POLICE.sendMessage(BreakinCommand.PREFIX + "Es wurde ein Alarm bei " + Objects.requireNonNull(RobLocation.getRob(Organisation.getOrganisation(player), player.getLocation())).getName() + " verzeichnet.");
                                alarm.put(player, 0);
                                Location rob = Objects.requireNonNull(RobLocation.getRob(Organisation.getOrganisation(player), player.getLocation())).getLoc();
                                for (UUID cop : Beruf.Berufe.POLICE.getMember()) new Route(Objects.requireNonNull(Bukkit.getPlayer(cop)).getName(), Script.getNRPID(Objects.requireNonNull(Bukkit.getPlayer(cop))), Objects.requireNonNull(Bukkit.getPlayer(cop)).getLocation(), rob).start();
                                rob.add(0, 4, 0);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (alarm.get(player) < 30) {
                                            Script.WORLD.playSound(rob, Sound.ENTITY_GHAST_SCREAM, 3.0F, 1.0F);
                                            alarm.put(player, alarm.get(player) + 1);
                                        } else {
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(NewRoleplayMain.getInstance(), 0L, 2 * 20L);
                            }
                            if (f.get(player) == 2) {
                                Organisation.getOrganisation(player).sendMessage(BreakinCommand.PREFIX + player.getName() + " ist der Einbruch nicht gelungen.");
                                RobLocation rob = RobLocation.getRob(Organisation.getOrganisation(player), player.getLocation());
                                if (rob == null) {
                                    Beruf.Berufe.POLICE.sendMessage(BreakinCommand.PREFIX + "Der Raub wurde verhindert.");
                                } else {
                                    Beruf.Berufe.POLICE.sendMessage(BreakinCommand.PREFIX + "Der Raub bei " + rob.getName() + " wurde verhindert.");
                                }
                                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                                LockpickHandler.c.remove(player);
                                event.getInventory().close();
                            } else {
                                f.put(player, f.get(player) + 1);
                                a.put(player, 1);
                                c.put(player, c.get(player) - 1);
                                next(player, type);
                            }
                        }
                    } else if (display.endsWith("€")) {
                        int v = Integer.parseInt(display.substring(0, display.lastIndexOf("€")));
                        value.put(player, value.get(player) + v);
                        Script.sendActionBar(player, BreakinCommand.PREFIX + "Du hast " + v + "€ aus der Kasse genommen.");

                        player.getInventory().close();
                        checkout.get(player).setItem(event.getSlot(), air);
                        player.openInventory(checkout.get(player));
                    } else if (display.endsWith("g")) {
                        int g = Integer.parseInt(display.substring(0, display.lastIndexOf("g")));
                        if (event.getCurrentItem().getType() == Material.SUGAR) {
                            pulver.put(player, pulver.get(player) + g);
                            Script.sendActionBar(player, BreakinCommand.PREFIX + "Du hast " + g + "g Pulver aus dem Lager genommen.");
                        } else {
                            kraeuter.put(player, kraeuter.get(player) + g);
                            Script.sendActionBar(player, BreakinCommand.PREFIX + "Du hast " + g + "g Kräuter aus dem Lager genommen.");
                        }

                        player.getInventory().close();
                        checkout.get(player).setItem(event.getSlot(), air);
                        player.openInventory(checkout.get(player));
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onDrop(PlayerDropItemEvent event) {
        if (c.containsKey(event.getPlayer())) {
            if (event.getItemDrop().getItemStack().getType() == Material.BLAZE_ROD) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public static void onPickup(InventoryPickupItemEvent event) {
        if (((TextComponent) ((Player) Objects.requireNonNull(event.getInventory().getHolder())).getOpenInventory().title()).content().contains("Schloss")) {
            event.setCancelled(true);
        }
    }

    public static HashMap<Player, Integer> c = new HashMap<>();
    public static HashMap<Player, Integer> a = new HashMap<>();
    public static HashMap<Player, Integer> f = new HashMap<>();
    public static HashMap<Player, Integer> alarm = new HashMap<>();

    public static HashMap<Player, Integer> value = new HashMap<>();
    public static HashMap<Player, Integer> pulver = new HashMap<>();
    public static HashMap<Player, Integer> kraeuter = new HashMap<>();
    private static final HashMap<Player, Inventory> lock = new HashMap<>();
    private static final HashMap<Player, Inventory> checkout = new HashMap<>();
    private static final HashMap<Player, List<ItemStack>> upper = new HashMap<>();
    private static final HashMap<Player, List<ItemStack>> middle = new HashMap<>();
    private static final HashMap<Player, List<ItemStack>> lower = new HashMap<>();
    private static final HashMap<Player, List<Integer>> sequence = new HashMap<>();

    private static final ItemStack air = new ItemStack(Material.AIR, 1);
    private static final ItemStack keys = new ItemStack(Material.HOPPER, 1);
    private static final ItemStack frame = new ItemStack(Material.IRON_BARS, 1);

    private static void show(Player player) {
        for (int n = 0; n < 27; n++) {
            if (n > 0 && n < 9) {
                lock.get(player).setItem(n, upper.get(player).get(n - 1));
            }
            if (n == 9) {
                lock.get(player).setItem(n, middle.get(player).get(0));
            }
            if (n > 9 && n < 18) {
                if (sequence.get(player).size() > (n - 10)) {
                    if (middle.get(player).get(n - 9) == air) {
                        lock.get(player).setItem(n, air);
                    } else {
                        ItemStack key = middle.get(player).get(n - 9);
                        ItemMeta keysmeta = key.getItemMeta();
                        List<String> color = Arrays.asList("c", "6", "e", "2", "a", "b", "9", "d");
                        keysmeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&" + color.get(sequence.get(player).get(n - 10) - 1) + sequence.get(player).get(n - 10))));
                        key.setItemMeta(keysmeta);
                        lock.get(player).setItem(n, key);
                    }
                }
            }
            if (n > 18) {
                lock.get(player).setItem(n, lower.get(player).get(n - 19));
            }
        }

        player.openInventory(lock.get(player));
    }

    private static void refresh(Player player, int i) {
        ItemMeta keysmeta = keys.getItemMeta();
        keysmeta.displayName(Component.text("Zylinder").color(TextColor.color(Color.SILVER.asRGB())));
        keys.setItemMeta(keysmeta);
        if (upper.get(player).get(i).getType() == air.getType()) {
            upper.get(player).set(i, keys);
        } else {
            lower.get(player).set(i, keys);
        }
        middle.get(player).set(i + 1, air);

        show(player);
    }

    public static void next(Player player, String type) {
        lock.put(player, Bukkit.createInventory(player, 27, Component.text("Schloss - " + type)));

        upper.put(player, new ArrayList<>());
        middle.put(player, new ArrayList<>());
        lower.put(player, new ArrayList<>());
        sequence.put(player, new ArrayList<>());

        ItemStack block = new ItemStack(Material.IRON_BLOCK);
        ItemMeta blockmeta = block.getItemMeta();
        blockmeta.displayName(Component.text("Dietrich"));
        block.setItemMeta(blockmeta);
        middle.get(player).add(block);
        ItemMeta framemeta = frame.getItemMeta();
        framemeta.displayName(Component.text("Schloss").color(TextColor.color(Color.GRAY.asRGB())));
        frame.setItemMeta(framemeta);
        for (int l = 0; l < 8; l++) {
            upper.get(player).add(frame);
            if (l < c.get(player)) {
                middle.get(player).add(keys);
            } else {
                middle.get(player).add(air);
            }
            lower.get(player).add(frame);
        }
        for (int k = 0; k < c.get(player); k++) {
            if (new Random().nextInt(2) == 0) {
                upper.get(player).set(k, air);
            } else {
                lower.get(player).add(k, air);
            }

            sequence.get(player).add(k + 1);
        }
        Collections.shuffle(sequence.get(player));

        show(player);
    }

    private static void win(Player player, String type) {
        checkout.put(player, Bukkit.createInventory(player, 36, Component.text("Schloss - " + type)));
        if (Objects.equals(type, "Kasse")) {
            for (int m = 0; m < 36; m++) {
                float x = new Random().nextFloat();
                int v;
                ItemStack money;
                if (x < 0.30) {
                    v = 10;
                    money = new ItemStack(Material.RED_BANNER);
                } else if (x < 0.50) {
                    v = 20;
                    money = new ItemStack(Material.BLUE_BANNER);
                } else if (x < 0.70) {
                    v = 50;
                    money = new ItemStack(Material.ORANGE_BANNER);
                } else if (x < 0.85) {
                    v = 100;
                    money = new ItemStack(Material.GREEN_BANNER);
                } else if (x < 0.95) {
                    v = 200;
                    money = new ItemStack(Material.YELLOW_BANNER);
                } else {
                    v = 500;
                    money = new ItemStack(Material.PURPLE_BANNER);
                }

                ItemMeta moneymeta = money.getItemMeta();
                moneymeta.displayName(Component.text(v + "€").color(TextColor.color(Color.OLIVE.asRGB())));
                money.setItemMeta(moneymeta);
                checkout.get(player).setItem(m, money);
            }
        } else if (Objects.equals(type, "Lager")) {
            for (int m = 0; m < 36; m++) {
                float x = new Random().nextFloat();
                int g = 1 + new Random().nextInt(10);
                ItemStack drug;
                if (x < 0.25) {
                    drug = new ItemStack(Material.SUGAR);
                } else if (x < 0.50) {
                    drug = new ItemStack(Material.GREEN_DYE);
                } else {
                    drug = new ItemStack(Material.AIR);
                }

                if (drug.getType() != Material.AIR) {
                    ItemMeta drugmeta = drug.getItemMeta();
                    if (drug.getType() == Material.SUGAR)
                        drugmeta.displayName(Component.text(g + "g").color(TextColor.color(Color.SILVER.asRGB())));
                    if (drug.getType() == Material.GREEN_DYE)
                        drugmeta.displayName(Component.text(g + "g").color(TextColor.color(Color.GREEN.asRGB())));
                    drug.setItemMeta(drugmeta);
                }
                checkout.get(player).setItem(m, drug);
            }
        }

        player.openInventory(checkout.get(player));
    }

    @EventHandler
    public static void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getOpenInventory().title() instanceof TextComponent) {
                if (((TextComponent) player.getOpenInventory().title()).content().contains("Schloss")) {
                    player.closeInventory();
                }
            }
        }
    }
}
