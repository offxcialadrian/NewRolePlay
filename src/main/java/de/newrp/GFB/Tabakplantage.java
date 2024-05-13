package de.newrp.GFB;

import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tabakplantage implements CommandExecutor, Listener {

    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static HashMap<String, Long> time = new HashMap<>();
    public static Location acceptJob = new Location(Script.WORLD, 94, 66, 628, 29.992523f, 7.2927737f);
    public static HashMap<String, Integer> freshTobacco = new HashMap<>();
    public static HashMap<String, Integer> driedTobacco = new HashMap<>();
    public static HashMap<String, Integer> mixedTobacco = new HashMap<>();
    public static String PREFIX = "§7[§9Tabakplantage§7] §9" + Messages.ARROW + " §7";
    public static final HashMap<String, Long> DRYING = new HashMap<>();
    static final HashMap<Location, ArrayList<Item>> ITEMS = new HashMap<>();
    static final HashMap<String, Location> LOCATION = new HashMap<>();
    private static final List<Block> BLOCKS_BETWEEN = Script.getBlocksBetween(new Location(Script.WORLD, 111, 66, 627), new Location(Script.WORLD, 112, 67, 644));

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;
        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/tabakplantage");
            return true;
        }

        if(GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if(p.getLocation().distance(acceptJob) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe der Tabakplantage.");
            return true;
        }

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        int i = (Premium.hasPremium(p) ? GFB.TABAKPLANTAGE.getLevel(p)+10 : GFB.TABAKPLANTAGE.getLevel(p)+6);
        p.getInventory().addItem(Script.setNameAndLore(Material.SHEARS, "§7Tabakschere", "§9" + i + "/" + i));
        p.sendMessage(PREFIX + "Ernte Tabak und lege es zum trocknen auf die Steintische.\n" +
                Messages.INFO + "Halte die linke Maustaste mit der Schere zum Tabakabbau lange gedrückt.");

        GFB.CURRENT.put(p.getName(), GFB.TABAKPLANTAGE);


        return false;
    }

    public static void openGUI(Player p) {
        Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> {
            final Inventory inv = p.getServer().createInventory(null, InventoryType.HOPPER, "§6Tabak");
            inv.setItem(0, Script.setName(Material.MELON, "§cWassermelone"));
            inv.setItem(1, Script.setName(new ItemStack(Material.INK_SAC, 1, (byte) 5), "§5Traube"));
            inv.setItem(2, Script.setName(Material.APPLE, "§cDoppelapfel"));
            inv.setItem(3, Script.setName(new ItemStack(Material.INK_SAC, 1, (byte) 11), "§eZitrone"));
            inv.setItem(4, Script.setName(new ItemStack(Material.INK_SAC, 1, (byte) 14), "§6Pfirsich-Minze"));
            p.openInventory(inv);
        });
    }

    public static void drop(Player p) {
        int tabak = freshTobacco.get(p.getName());
        if (tabak > 0) {
            for (Block b : BLOCKS_BETWEEN) {
                if (b.getRelative(BlockFace.DOWN).getType() == Material.AIR) continue;
                Location loc = b.getLocation();
                if (ITEMS.containsKey(loc)) continue;
                ArrayList<Item> items = new ArrayList<>();
                for (int i = 0; i < tabak; i++) {
                    ItemStack itemStack = new ItemStack(Material.GRASS, 1, (byte) 2);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(p.getName());
                    itemStack.setItemMeta(itemMeta);

                    Item item = p.getWorld().dropItemNaturally(loc.clone().add(0.5, 0.5, .5), itemStack);
                    item.setCustomName(p.getName());
                    item.setCustomNameVisible(false);
                    item.setVelocity(item.getVelocity().zero());
                    item.setPickupDelay(Integer.MAX_VALUE);
                    items.add(item);
                }
                ITEMS.put(loc, items);
                DRYING.put(p.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(210));
                LOCATION.put(p.getName(), loc);
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                    if (p.isOnline()) {
                        p.sendMessage(PREFIX + "Der Tabak ist nun getrocknet.\n" + PREFIX + "Hol den Tabak nun ab für den nächsten Schritt.");
                        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), new Location(p.getWorld(), 112, 66, 646), PREFIX + "Du hast den Tabak aufgesammelt.", () -> pickUp(p)).start();
                        Location loc1 = LOCATION.get(p.getName());
                        ArrayList<Item> items1 = ITEMS.get(loc1);
                        int tabak1 = items1.size();
                        for (Item i : items1) {
                            i.remove();
                        }
                        ITEMS.remove(loc1);
                        for (int i = 0; i < tabak1; i++) {
                            ItemStack itemStack = new ItemStack(Material.DEAD_BUSH);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName(p.getName());
                            itemStack.setItemMeta(itemMeta);

                            Item item = p.getWorld().dropItemNaturally(loc1.clone().add(.5, 0, .5), itemStack);

                            item.setVelocity(item.getVelocity().zero());
                            item.setCustomName(p.getName());
                            item.setCustomNameVisible(false);
                            item.setPickupDelay(Integer.MAX_VALUE);
                            items1.add(item);
                        }
                        ITEMS.put(loc1, items1);
                    } else {
                        Location loc1 = LOCATION.get(p.getName());
                        ArrayList<Item> items1 = ITEMS.get(loc1);
                        for (Item i : items1) {
                            i.remove();
                        }
                        LOCATION.remove(p.getName());
                        ITEMS.remove(loc1);
                    }
                    DRYING.remove(p.getName());
                }, 210 * 20L);
                break;
            }
        }
    }

    public static void respawnPlantage(final Location from, final Location to)  {
        final List<Block> grassBlock = new ArrayList<>();
        for (final Block block : Script.getBlocksBetween(from, to)) {
            if(block.getType() == Material.GRASS_BLOCK) {
                grassBlock.add(block);
            }
        }

        for (final Block block : grassBlock) {
            final Block firstLargeFern = block.getRelative(BlockFace.UP);
            firstLargeFern.setType(Material.LARGE_FERN, false);

            final Block secondLargeFern = firstLargeFern.getRelative(BlockFace.UP);
            secondLargeFern.setType(Material.LARGE_FERN, false);
        }
    }

    public static void pickUp(Player p) {
        Location loc = LOCATION.get(p.getName());
        ArrayList<Item> items = ITEMS.get(loc);
        int tabak = 0;
        for (Item i : items) {
            i.remove();
            tabak++;
        }
        tabak = tabak / 2;
        LOCATION.remove(p.getName());
        ITEMS.remove(loc);
        DRYING.remove(p.getName());
        driedTobacco.put(p.getName(), tabak);
        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
            p.sendMessage(PREFIX + "Bring den getrockneten Tabak nun zum Mischen für den Geschmack.");
            new Route(p.getName(), Script.getNRPID(p), p.getLocation(), new Location(p.getWorld(), 99, 66, 628), PREFIX + "Wähle die Geschmacksrichtung.", () -> openGUI(p)).start();
        }, 20);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if(!GFB.CURRENT.containsKey(e.getPlayer().getName())) return;
        if(GFB.CURRENT.get(e.getPlayer().getName()) != GFB.TABAKPLANTAGE) return;
        if (b.getType().equals(Material.LARGE_FERN)) {
            Player p = e.getPlayer();
            ItemStack is = p.getInventory().getItemInMainHand();
            if (is == null || !is.getType().equals(Material.SHEARS) || !is.hasItemMeta() || !is.getItemMeta().getDisplayName().equals("§7Tabakschere")) {
                return;
            }

            if (!Script.isInRegion(b.getLocation(), new Location(p.getWorld(), 121, 69, 621), new Location(p.getWorld(), 100, 66, 662))) {
                return;
            }

            String[] raw = ChatColor.stripColor(is.getItemMeta().getLore().get(0)).split("/");
            if (!Script.isInt(raw[0]) || !Script.isInt(raw[1])) return;
            if (new CooldownAPI(p, CooldownAPI.CooldownTime.MEDIUM).checkInput(Tabakplantage.class)) {
                int left = Integer.parseInt(raw[0]);
                int total = Integer.parseInt(raw[1]);
                if(!freshTobacco.containsKey(p.getName())) freshTobacco.put(p.getName(), 1);
                freshTobacco.put(p.getName(), freshTobacco.get(p.getName()) + 1);
                Block upperBlock = b.getRelative(BlockFace.UP);
                if (upperBlock.getType() == Material.AIR) {
                    b.setType(Material.AIR);
                    b.getRelative(BlockFace.DOWN).setType(Material.AIR);
                } else {
                    upperBlock.setType(Material.AIR);
                    b.setType(Material.AIR);
                }
                if (left == 1) {
                    is.setAmount(0);

                    new Route(p.getName(), Script.getNRPID(p), p.getLocation(), new Location(p.getWorld(), 112, 66, 646), PREFIX + "Du hast den frischen Tabak zum Trocknen ausgelegt.", () -> Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> drop(p))).start();
                    p.sendMessage(PREFIX + "Bring die " + total + " Tabakpflanzen nun zum Trocknen.");
                } else {
                    short r = (short) (is.getType().getMaxDurability() / 8);
                    is.setDurability((short) (is.getDurability() - r));
                    ItemMeta meta = is.getItemMeta();
                    meta.setLore(Collections.singletonList("§9" + (left - 1) + "/" + total));
                    is.setItemMeta(meta);
                    p.sendMessage(PREFIX + "Du hast eine Tabakpflanze geerntet, " + (left - 1) + " Stück " + (left - 1 == 1 ? "ist" : "sind") + " verbleibend");
                }
            } else {
                e.setCancelled(true);
                e.getBlock().getState().update();
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§6Tabak")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                e.setCancelled(true);
                e.getView().close();

                String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                ShishaType type;
                switch (name) {
                    case "Wassermelone":
                        type = ShishaType.WASSERMELONE;
                        break;
                    case "Traube":
                        type = ShishaType.TRAUBE;
                        break;
                    case "Doppelapfel":
                        type = ShishaType.DOPPELAPFEL;
                        break;
                    case "Zitrone":
                        type = ShishaType.ZITRONE;
                        break;
                    case "Pfirsich-Minze":
                        type = ShishaType.PFIRSICH_MINZE;
                        break;
                    default:
                        return;
                }

                Player p = (Player) e.getWhoClicked();
                int tabak = driedTobacco.get(p.getName());
                p.sendMessage(PREFIX + "Du hast " + tabak + "g Tabak mit der Geschmacksrichtung " + type.getName() + " hergestellt.\n" +
                        PREFIX + "Bringe es nun weg und gib es mit §8/§6droptabak §7ab.");
                mixedTobacco.put(p.getName(), tabak);
                new Route(p.getName(), Script.getNRPID(p), p.getLocation(), HologramList.DROPTABAK.getLocation()).start();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!GFB.CURRENT.containsKey(p.getName())) return;
        if(GFB.CURRENT.get(p.getName()) != GFB.TABAKPLANTAGE) return;
        freshTobacco.remove(p.getName());
        driedTobacco.remove(p.getName());
        mixedTobacco.remove(p.getName());
        GFB.CURRENT.remove(p.getName());

    }

}
