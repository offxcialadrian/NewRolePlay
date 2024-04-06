package de.newrp.GFB;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.main;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.builder.EqualsExclude;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;

public class Lagerarbeiter implements CommandExecutor, Listener {

    private static String PREFIX = "§8[§6Lagerarbeiter§8] §8» §6";
    public static HashMap<String, Waren> ON_JOB = new HashMap<>();
    public static HashMap<String, Integer> SCORE = new HashMap<>();
    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();
    public static HashMap<String, Long> small_cooldown = new HashMap<>();

    public enum Type {
        OBST("Obst"),
        GEMUESE("Gemüse"),
        BACKWAREN("Backwaren"),
        FLEISCH("Fleisch"),

        GETRAENKE("Getränke"),
        FLOWERS("Blumen"),
        MOEBEL("Möbel"),
        TEPPICH("Teppich"),
        MUSIK("Musik"),
        WOLLE("Wolle");

        String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Waren {

        APFEL(1, "Äpfel", new ItemBuilder(Material.APPLE).setName("§fApfel").build(), Type.OBST),
        MELONE(2, "Melone", new ItemBuilder(Material.MELON).setName("§fMelone").build(), Type.GEMUESE),
        KAROTTE(3, "Karotte", new ItemBuilder(Material.CARROT).setName("§fKarotte").build(), Type.GEMUESE),
        KARTOFFEL(4, "Kartoffel", new ItemBuilder(Material.POTATO).setName("§fKartoffel").build(), Type.GEMUESE),
        BROT(5, "Brot", new ItemBuilder(Material.BREAD).setName("§fBrot").build(), Type.BACKWAREN),
        FLEISCH(6, "Fleisch", new ItemBuilder(Material.PORKCHOP).setName("§fFleisch").build(), Type.FLEISCH),
        WASSER(8, "Wasser", new ItemBuilder(Material.POTION).setName("§fWasser").build(), Type.GETRAENKE),
        LIMO(9, "Limo", new ItemBuilder(Material.POTION).setName("§fLimo").build(), Type.GETRAENKE),
        MILCH(10, "Milch", new ItemBuilder(Material.POTION).setName("§fMilch").build(), Type.GETRAENKE),
        BIER(11, "Bier", new ItemBuilder(Material.POTION).setName("§fBier").build(), Type.GETRAENKE),
        WEIN(12, "Wein", new ItemBuilder(Material.POTION).setName("§fWein").build(), Type.GETRAENKE),
        KUCHEN(13, "Kuchen", new ItemBuilder(Material.CAKE).setName("§fKuchen").build(), Type.BACKWAREN),
        ROSEN(15, "Rosen", new ItemBuilder(Material.POPPY).setName("§fRosen").build(), Type.FLOWERS),
        BETT(16, "Bett", new ItemBuilder(Material.RED_BED).setName("§fBett").build(), Type.MOEBEL),
        WORKBENCH(17, "Werkbank", new ItemBuilder(Material.CRAFTING_TABLE).setName("§fWerkbank").build(), Type.MOEBEL),
        TEPPICH(18, "Teppich", new ItemBuilder(Material.RED_CARPET).setName("§fTeppich").build(), Type.TEPPICH),
        TEPPICH_BLAU(19, "Teppich", new ItemBuilder(Material.BLUE_CARPET).setName("§fTeppich").build(), Type.TEPPICH),
        TEPPICH_GELB(20, "Teppich", new ItemBuilder(Material.YELLOW_CARPET).setName("§fTeppich").build(), Type.TEPPICH),
        TEPPICH_GRUEN(21, "Teppich", new ItemBuilder(Material.GREEN_CARPET).setName("§fTeppich").build(), Type.TEPPICH),
        TEPPICH_ORANGE(22, "Teppich", new ItemBuilder(Material.ORANGE_CARPET).setName("§fTeppich").build(), Type.TEPPICH),
        NOTENBLOCK(24, "Notenblock", new ItemBuilder(Material.NOTE_BLOCK).setName("§fNotenblock").build(), Type.MUSIK),
        JUKEBOX(25, "Jukebox", new ItemBuilder(Material.JUKEBOX).setName("§fJukebox").build(), Type.MUSIK),
        SCHALLPLATTE(26, "Schallplatte", new ItemBuilder(Material.MUSIC_DISC_11).setName("§fSchallplatte").build(), Type.MUSIK),
        SCHALLPLATTE_2(27, "Schallplatte", new ItemBuilder(Material.MUSIC_DISC_13).setName("§fSchallplatte").build(), Type.MUSIK),
        WOLLE(28, "Wolle", new ItemBuilder(Material.WHITE_WOOL).setName("§fWolle").build(), Type.WOLLE),
        WOLLE_BLAU(29, "Wolle", new ItemBuilder(Material.BLUE_WOOL).setName("§fWolle").build(), Type.WOLLE),
        WOLLE_GELB(30, "Wolle", new ItemBuilder(Material.YELLOW_WOOL).setName("§fWolle").build(), Type.WOLLE),
        WOLLE_GRUEN(31, "Wolle", new ItemBuilder(Material.GREEN_WOOL).setName("§fWolle").build(), Type.WOLLE);





        int id;
        String name;
        ItemStack is;
        Type type;

        Waren(int id, String name, ItemStack is, Type type) {
            this.id = id;
            this.name = name;
            this.is = is;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public ItemStack getMaterial() {
            return is;
        }

        public int getID() {
            return id;
        }

        public Type getType() {
            return type;
        }

        public static Waren getWareByName(String name) {
            for (Waren waren : Waren.values()) {
                if (waren.getName().equalsIgnoreCase(name)) return waren;
            }
            return null;
        }

        public static Waren getWareByID(int id) {
            for (Waren waren : Waren.values()) {
                if (waren.getID() == id) return waren;
            }
            return null;
        }

        public static ItemStack getMaterialByID(int id) {
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

        public static Waren getWareByMaterial(ItemStack material) {
            for (Waren waren : Waren.values()) {
                if (waren.getMaterial() == material) return waren;
            }
            return null;
        }
    }


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if(ON_JOB.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du bist bereits im Job.");
            return true;
        }

        if(SCORE.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du bist bereits im Job.");
            return true;
        }

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 995, 69, 1260, 176.5424f, 15.150127f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du musst dich im Lager befinden.");
            return true;
        }

        if(BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst diesen Befehl nicht im BuildMode nutzen.");
            return true;
        }

        GFB.CURRENT.put(p.getName(), GFB.LAGERARBEITER);
        p.sendMessage(PREFIX + "Gehe ins Lager, hole dir eine Palette und fang an deinen Job zu machen.");
        int totalscore = GFB.LAGERARBEITER.getLevel(p) + Script.getRandom(7, 12);
        p.sendMessage(Messages.INFO + "Klicke Rechtsklick auf das Schild \"Ware\".");
        p.sendMessage(Messages.INFO + "Du musst insgesamt " + (totalscore+1) + " Waren verräumen.");
        SCORE.put(p.getName(), totalscore);
        TOTAL_SCORE.put(p.getName(), totalscore);
        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 2000L);

        return false;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
                    Sign s = (Sign) e.getClickedBlock().getState();
                    if(s.getLine(2).equalsIgnoreCase("§lWare")) {
                        if(!SCORE.containsKey(p.getName())) {
                            p.sendMessage(PREFIX + "Nimm erstmal den Job an!");
                            return;
                        }

                        if(ON_JOB.containsKey(p.getName())) {
                            p.sendMessage(PREFIX + "Bring erstmal " + ON_JOB.get(p.getName()).getName() + " weg.");
                            return;
                        }

                        Waren ware = Waren.getWareByID(Script.getRandom(1, Waren.values().length));
                        p.sendMessage(PREFIX + "Sortiere nun " + ware.getName() + " ein.");
                        ON_JOB.put(p.getName(), ware);
                    } else {
                        if(!ON_JOB.containsKey(p.getName())) return;
                        if(s.getLine(2).equalsIgnoreCase("§l" + ON_JOB.get(p.getName()).getType().getName())) {
                            if(!ON_JOB.containsKey(p.getName())) {
                                p.sendMessage(PREFIX + "Du hast keine Ware zum einordnen.");
                                return;
                            }


                            Inventory inv = Bukkit.createInventory(null, 9*5, "Produkt einsortieren");
                            inv.setItem(inv.getSize() - 1, new ItemBuilder(Material.GREEN_WOOL).setName("§aBestätigen").build());
                            if(small_cooldown.containsKey(p.getName())) {
                                if (small_cooldown.get(p.getName()) < System.currentTimeMillis()) Cache.saveInventory(p);
                            } else {
                                Cache.saveInventory(p);
                            }
                            small_cooldown.put(p.getName(), System.currentTimeMillis() + 5L);
                            p.getInventory().clear();
                            p.openInventory(inv);
                            p.setItemOnCursor(new ItemBuilder(ON_JOB.get(p.getName()).getMaterial().getType()).setAmount(inv.getSize() - 1).build());
                        } else {
                            p.sendMessage(PREFIX + "Du musst erstmal " + ON_JOB.get(p.getName()).getType().getName() + " wegbringen.");
                        }
                    }
                }
            }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null || e.getClickedInventory() == null) return;

        Inventory inv = e.getClickedInventory();

        if (e.getView().getTitle().equalsIgnoreCase("Produkt einsortieren")) {
            if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aBestätigen")) {
                e.setCancelled(true);
                return;
            }

            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                    return;
                }
            }

            p.closeInventory();
        }
    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if(ON_JOB.containsKey(p.getName())) {
            e.setCancelled(true);
            p.sendMessage(PREFIX + "Du kannst gerade keine Items droppen.");
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(!SCORE.containsKey(p.getName())) return;
        if (e.getView().getTitle().equalsIgnoreCase("Produkt einsortieren")) {
            Inventory inv = e.getInventory();
            for (int i = 0; i < inv.getSize() - 1; i++) {
                if (inv.getItem(i) == null)
                    return;
            }
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            ON_JOB.remove(p.getName());
            p.getInventory().clear();
            int amount = SCORE.get(p.getName());
            if (amount == 0) {
                GFB.CURRENT.remove(p.getName());
                p.sendMessage(PREFIX + "§aFertig");
                SCORE.remove(p.getName());
                GFB.LAGERARBEITER.addExp(p, GFB.LAGERARBEITER.getLevel(p) + TOTAL_SCORE.get(p.getName())/2);
                PayDay.addPayDay(p, (GFB.LAGERARBEITER.getLevel(p) + (TOTAL_SCORE.get(p.getName())))*2);
                Script.addEXP(p, GFB.LAGERARBEITER.getLevel(p) + TOTAL_SCORE.get(p.getName())*2);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.getInventory().clear();
                        Cache.loadInventory(p);
                    }
                }.runTaskLater(main.getInstance(), 20L);
            } else {
                SCORE.replace(p.getName(), amount - 1);
                p.sendMessage(PREFIX + "§aRichtig! §6Hole nun das nächste Produkt aus \"Ware\" und sortiere es ein (" + (TOTAL_SCORE.get(p.getName())-SCORE.get(p.getName())) + "/" + (TOTAL_SCORE.get(p.getName())+1) + ")");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(!SCORE.containsKey(p.getName())) return;
        ON_JOB.remove(p.getName());
        SCORE.remove(p.getName());
        GFB.CURRENT.remove(p.getName());
        Cache.loadInventory(p);
    }

}
