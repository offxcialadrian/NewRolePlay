package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Navi;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.House.House;
import de.newrp.Medic.FeuerwehrEinsatz;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MolotovCocktail implements CommandExecutor, Listener {
    final HashMap<String, Long> cooldowns = new HashMap<>();
    public static HashMap<String, Long> create_time = new HashMap<>();
    public static HashMap<String, Integer> player_progress = new HashMap<>();
    private static Location molotov = new Location(Script.WORLD, 713, 71, 544);
    private static String prefix = "§8[§cMolotov Cocktail§8] §c" + Messages.ARROW + " §7";

    @EventHandler
    public void onDrop(final PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().equals(Script.molotov())) {
            long time = System.currentTimeMillis();
            Long lastUsage = cooldowns.get(e.getPlayer().getName());
            if (cooldowns.containsKey(e.getPlayer().getName())) {
                if (lastUsage + 10 * 1000 > time) {
                    e.setCancelled(true);
                    return;
                }
            }
            cooldowns.put(e.getPlayer().getName(), time);
            e.getItemDrop().setPickupDelay(Integer.MAX_VALUE);
            Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                startFire(e.getPlayer().getLocation());
                e.getItemDrop().remove();
            }, 2 * 27L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().equals(Script.molotov())) {
                long time = System.currentTimeMillis();
                Long lastUsage = cooldowns.get(e.getPlayer().getName());
                if (cooldowns.containsKey(e.getPlayer().getName())) {
                    if (lastUsage + 10 * 1000 > time) {
                        e.setCancelled(true);
                        return;
                    }
                }
                cooldowns.put(e.getPlayer().getName(), time);
                e.getPlayer().getInventory().getItemInMainHand().setAmount(e.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                final Item i = e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Script.molotov());
                i.setVelocity(e.getPlayer().getLocation().getDirection().multiply(1.2F));
                i.setPickupDelay(Integer.MAX_VALUE);
                Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
                    startFire(i.getLocation());
                    i.remove();
                }, 2 * 27L);
            }
        }
    }

    private static void setHouseOnFire(Location loc) {
        House house = House.getNearHouse(loc, 5);
        if (house == null)
            return;
        int random = Script.getRandom(1, 100);
        if (random <= 10) {
            new FeuerwehrEinsatz(house);
            House.Mieter m = house.getMieterByID(house.getOwner());
            m.setNebenkosten(house, m.getNebenkosten() + Script.getRandom(750, 2000));
        }
    }

    private static void startFire(Location loc) {
        Abteilung.Abteilungen.FEUERWEHR.sendMessage(FeuerwehrEinsatz.PREFIX + "§4Brand 2, " + Navi.getNextNaviLocation(loc).getName() + ", Alle verfügbaren Einheiten!");
        Beruf.Berufe.POLICE.sendMessage("§9HQ: Es wurde eine Brandstiftung gemeldet. Begeben Sie sich umgehend zu " + Navi.getNextNaviLocation(loc).getName() + ", over.");
        setHouseOnFire(loc);
        sendRoute(Abteilung.Abteilungen.FEUERWEHR, loc);
        ArrayList<Block> fire = new ArrayList<>();
        int radius = Script.getRandom(3, 6);
        Block block = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()).getBlock();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        int minX = x - radius;
        int minY = y - radius;
        int minZ = z - radius;
        int maxX = x + radius;
        int maxY = y + radius;
        int maxZ = z + radius;
        for (int counterX = minX; counterX <= maxX; counterX++) {
            for (int counterY = minY; counterY <= maxY; counterY++) {
                for (int counterZ = minZ; counterZ <= maxZ; counterZ++) {
                    Block blockName = loc.getWorld().getBlockAt(counterX, counterY, counterZ);
                    fire.add(blockName);
                }
            }
        }
        int count = 0;
        for (Block b : fire) {
            if (count == 40) break;
            Location l = b.getLocation();
            count = 0;
            if (Script.getRandom(1, 3) == 1) {
                while (!b.getType().equals(Material.AIR)) {
                    if (count > 6) break;
                    l.add(0, 1, 0);
                    count++;
                }
                if (l.getBlock().getType().equals(Material.AIR) && (l.getBlockY() - loc.getBlockY() < 5)) {
                    Bukkit.getScheduler().runTask(main.getInstance(), () -> {
                        Block setFire = b.getWorld().getBlockAt(l);
                        setFire.setType(Material.FIRE);
                        if (setFire.getType().equals(Material.FIRE)) {
                            FeuerwehrEinsatz.onFire.add(setFire);
                        }
                    });
                }
            }
        }
    }

    public static void sendRoute(Abteilung.Abteilungen sf, Location loc) {
        for (Player p : sf.getOnlineMembers()) {
            Script.sendClickableMessage(p, "§6Route", "/navi " + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ(), "§7Klicke hier um die Route zu sehen.");
        }
    }

    public static void openGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 5, "§6Molotov Cocktail");
        ItemStack is = new ItemStack(Material.POTION);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName("§cFalsch");
        is.setItemMeta(meta);

        ItemStack right = new ItemStack(Material.PAPER);
        ItemMeta rightMeta = right.getItemMeta();
        rightMeta.setDisplayName("§aRichtig");
        right.setItemMeta(rightMeta);

        Random r = new Random();
        int random = r.nextInt(inv.getSize());

        for (int i = 0; i < inv.getSize(); i++) {
            if (i == random) continue;
            inv.setItem(i, is);
        }
        inv.setItem(random, right);
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Random r = new Random();
        int random = r.nextInt(100) + 1;
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        if (e.getView().getTitle().equals("§6Molotov Cocktail")) {
            e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§cFalsch") && e.getCurrentItem().getType().equals(Material.POTION)) {
                p.closeInventory();
                create_time.remove(p.getName());
                player_progress.remove(p.getName());
                p.sendMessage(prefix + "Das Herstellen ist fehlgeschlagen" + (random <= 30 && Abteilung.Abteilungen.FEUERWEHR.getOnlineMembers().size() >= 3 ? " und ein Feuer ist entstanden!" : "."));
                if (random <= 30) {
                    startFire(p.getLocation(), Organisation.getOrganisation(p));
                }
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§aRichtig") && e.getCurrentItem().getType().equals(Material.PAPER)) {
                p.closeInventory();
                long time = System.currentTimeMillis();
                long test = create_time.get(p.getName());
                int i = (int) (time - test) / 33;
                if (random >= i) {
                    if (player_progress.get(p.getName()) == 3) {
                        p.sendMessage(prefix + "Du hast erfolgreich ein Molotov Cocktail hergestellt!");
                        p.getInventory().addItem(Script.molotov());
                        create_time.remove(p.getName());
                        player_progress.remove(p.getName());
                    } else {
                        player_progress.replace(p.getName(), player_progress.get(p.getName()) + 1);
                        openGUI(p);
                        create_time.remove(p.getName());
                        create_time.put(p.getName(), time);
                    }
                } else {
                    player_progress.remove(p.getName());
                    p.sendMessage(prefix + "Das Herstellen ist fehlgeschlagen" + (random <= 30 && Abteilung.Abteilungen.FEUERWEHR.getOnlineMembers().size() >= 3 ? " und ein Feuer ist entstanden!" : "."));
                    if (random <= 30) {
                        startFire(p.getLocation(), Organisation.getOrganisation(p));
                    }
                    create_time.remove(p.getName());
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if (Organisation.hasOrganisation(p)) {
            if (Organisation.getRank(p) >= 2) {
                if (p.getLocation().distance(molotov) < 5) {
                    if (hasRequierements(p)) {
                        p.getInventory().remove(Material.PAPER);
                        p.getInventory().remove(Material.POTION);
                        openGUI(p);
                        player_progress.put(p.getName(), 1);
                        create_time.put(p.getName(), System.currentTimeMillis());
                    } else
                        p.sendMessage(Messages.ERROR + "Dir fehlen Ressourcen!");
                } else
                    p.sendMessage(Messages.ERROR + "Hier kannst du keinen Molotov Cocktail herstellen.");
            } else
                p.sendMessage(Messages.ERROR + "Du hast nicht die nötigen Rechte.");
        } else
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
        return false;
    }

    private static boolean hasRequierements(Player p) {
        Inventory inv = p.getInventory();
        int vodkaE = 0;
        int papier = 0;
        for (ItemStack is : inv.getContents()) {
            if (is == null) continue;
            if (is.getType().equals(Material.POTION)) {
                vodkaE++;
            } else if (is.getType().equals(Material.PAPER)) {
                papier = papier + is.getAmount();
            }
        }
        return vodkaE >= 10 && papier >= 5;
    }

    private static void startFire(Location loc, Organisation o) {
        if (Abteilung.Abteilungen.FEUERWEHR.getOnlineMembers().size() >= 3) {
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(FeuerwehrEinsatz.PREFIX + "§4Brand 4, " + Navi.getNextNaviLocation(loc).getName() + ", Alle verfügbaren Einheiten!");
            sendRoute(Abteilung.Abteilungen.FEUERWEHR, loc);
            Beruf.Berufe.NEWS.sendMessage("§6Es wurde ein Großbrand im Irischen Viertel gemeldet!");
            int i = Script.getRandom(1500, 2500);
            o.removeKasse(i);
            o.sendMessage(prefix + "Der Brand hat " + i + "€ Schaden verursacht.");
            ArrayList<Block> fire = new ArrayList<>();
            int radius = Script.getRandom(10, 15);
            Block block = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()).getBlock();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            int minX = x - radius;
            int minY = y - 5;
            int minZ = z - radius;
            int maxX = x + radius;
            int maxY = y + 5;
            int maxZ = z + radius;
            for (int counterX = minX; counterX <= maxX; counterX++) {
                for (int counterY = minY; counterY <= maxY; counterY++) {
                    for (int counterZ = minZ; counterZ <= maxZ; counterZ++) {
                        Block blockName = loc.getWorld().getBlockAt(counterX, counterY, counterZ);
                        fire.add(blockName);
                    }
                }
            }
            int count = 0;
            for (Block b : fire) {
                if (count == 65) break;
                Location l = b.getLocation();
                count = 0;
                if (Script.getRandom(1, 3) == 1) {
                    while (!b.getType().equals(Material.AIR)) {
                        if (count > 6) break;
                        l.add(0, 1, 0);
                        count++;
                    }
                    if (l.getBlock().getType().equals(Material.AIR) && (l.getBlockY() - loc.getBlockY() < 5)) {
                        Bukkit.getScheduler().runTask(main.getInstance(), () -> {
                            Block setFire = b.getWorld().getBlockAt(l);
                            setFire.setType(Material.FIRE);
                            if (setFire.getType().equals(Material.FIRE)) {
                                FeuerwehrEinsatz.onFire.add(setFire);
                            }
                        });
                    }
                }
            }
        }
    }
}
