package de.newrp.GFB;

import de.newrp.API.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Lagerarbeiter implements CommandExecutor, Listener {

    private static Location LAGER = new Location(Script.WORLD, 1, 70, 1);
    private static String PREFIX = "§8[§6Lagerarbeiter§8] §8» §6 ";
    private static HashMap<String, ItemStack[]> ON_JOB = new HashMap<>();
    private static HashMap<String, Integer> SCORE = new HashMap<>();
    public static HashMap<String, Long> cooldown = new HashMap<>();

    public enum Type {
        OBSTGEMUESE("Obst", new Location(Script.WORLD, 1, 70, 1)),
        GETRAENKE("Getränke", new Location(Script.WORLD, 1, 70, 1));

        String name;
        Location loc;

        Type(String name, Location loc) {
            this.name = name;
            this.loc = loc;
        }

        public String getName() {
            return name;
        }
    }

    public enum Waren {
        ITEM1(1, Material.POTION, Type.OBSTGEMUESE),
        ITEM2(2, Material.MILK_BUCKET, Type.GETRAENKE),
        ITEM3(3, Material.BREAD, Type.OBSTGEMUESE),
        ITEM4(4, Material.PAPER, Type.OBSTGEMUESE),
        ITEM5(5, Material.APPLE, Type.OBSTGEMUESE),
        ITEM6(6, Material.MAP, Type.OBSTGEMUESE),
        ITEM7(7, Material.CLOCK, Type.OBSTGEMUESE);

        int id;
        Material is;
        Type type;

        Waren(int id, Material is, Type type) {
            this.id = id;
            this.is = is;
            this.type = type;
        }

        public Material getMaterial() {
            return is;
        }

        public int getID() {
            return id;
        }

        public Type getType() {
            return type;
        }

        public static Material getMaterialByID(int id) {
            for (Waren waren : Waren.values()) {
                if (waren.getID() == id) return waren.getMaterial();
            }
            return null;
        }

        public static int getAmount() {
            int i = 0;
            for (Waren waren : Waren.values()) {
                i++;
            }
            return i;
        }

        public static Waren getWareByMaterial(Material material) {
            for (Waren waren : Waren.values()) {
                if (waren.getMaterial() == material) return waren;
            }
            return null;
        }
    }


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du kannst den Job nur alle 10 Minuten machen.");
                return true;
            }
        }

        p.sendMessage(PREFIX + "Gehe ins Lager, hole dir eine Palette und fang an deinen Job zu machen.");
        p.sendMessage(Messages.INFO + "Klicke Rechtsklick auf das Schild \"Palette\".");
        Cache.saveInventory(p);
        p.getInventory().clear();
        SCORE.put(p.getName(), (GFB.LAGERARBEITER.getLevel(p) * 10));
        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 1000L);

        return false;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (ON_JOB.containsKey(p.getName())) {
            int amount = SCORE.get(p.getName());
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
                    Sign s = (Sign) e.getClickedBlock().getState();
                    if (s.getLine(1).equalsIgnoreCase("Palette")) {
                        if (amount > 0) {
                            for (ItemStack is : p.getInventory()) {
                                if (is != null) {
                                    p.sendMessage(PREFIX + "Bring erstmal das aktuelle Zeug weg.");
                                    return;
                                }
                            }
                            int randomID = Script.getRandom(1, Waren.getAmount());
                            p.getInventory().addItem(new ItemBuilder(Waren.getMaterialByID(randomID)).build());
                            SCORE.replace(p.getName(), amount - 1);
                            p.sendMessage(PREFIX + "Bring das nun in die " + Waren.getWareByMaterial(p.getInventory().getItemInMainHand().getType()).getType().getName() + "-Abteilung.");
                        } else
                            p.sendMessage(PREFIX + "Bring das Zeug weg was du hast!");
                    } else {
                        if (p.getInventory().getItemInMainHand().getType() == Material.AIR)
                            return;
                        if (s.getLine(1).equalsIgnoreCase(Waren.getWareByMaterial(p.getInventory().getItemInMainHand().getType()).getType().getName())) {
                            Inventory inv = Bukkit.createInventory(null, 9 * 5, "Produkt einsortieren");
                            inv.setItem(inv.getSize() - 1, new ItemBuilder(Material.GREEN_WOOL).setName("§aBestätigen").build());
                            p.openInventory(inv);
                            p.setItemOnCursor(new ItemBuilder(p.getInventory().getItemInMainHand().getType()).setAmount(inv.getSize() - 1).build());
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (ON_JOB.containsKey(e.getPlayer().getName())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Messages.ERROR + "Du kannst während des Lagerarbeiter-Jobs nichts Droppen.");
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPickUp(EntityPickupItemEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (ON_JOB.containsKey(p.getName())) {
            e.setCancelled(true);
            Script.sendActionBar(p, Messages.ERROR + "Du kannst nichts aufheben während deines Jobs.");
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) return;
        if (e.getView().getTitle().equalsIgnoreCase("Produkt einsortieren")) {
            Inventory inv = e.getClickedInventory();
            for (int i = 0; i < inv.getSize() - 1; i++) {
                if (inv.getItem(i) == null)
                    return;
            }
            p.closeInventory();
            p.getInventory().remove(p.getInventory().getItemInMainHand());

            int amount = SCORE.get(p.getName());
            if (amount == 0) {
                p.sendMessage(PREFIX + "§aFertig");
                ON_JOB.remove(p.getName());
                SCORE.remove(p.getName());
                Cache.loadScoreboard(p);
                GFB.LAGERARBEITER.addExp(p, GFB.LAGERARBEITER.getLevel(p) * 10);
                PayDay.addPayDay(p, GFB.LAGERARBEITER.getLevel(p) * 5 * (Script.getRandom(1,2)));
            } else {
                p.sendMessage(PREFIX + "§aRichtig! §6Suche nun das nächste Produkt raus und Sortiere es ein.");
            }
        }
    }

}
