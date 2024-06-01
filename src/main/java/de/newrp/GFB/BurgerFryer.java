package de.newrp.GFB;

import de.newrp.API.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class BurgerFryer implements CommandExecutor, Listener {

    enum Ingredients {
        BUN("Bun", createItem(29861, "§6Bun")),
        CHEESE("Käse", createItem(1985, "§6Käse")),
        TOMATO("Tomate", createItem(32587, "§6Tomate")),
        SALAD("Salat", createItem(49122, "§6Salat")),
        ONION("Zwiebel", createItem(33323, "§6Zwiebel")),
        BEEF("Rindfleisch", new ItemBuilder(Material.COOKED_BEEF).setName("§6Rindfleisch").build()),
        BACON("Bacon", createItem(26, "§6Bacon")),
        FISH("Fisch", new ItemBuilder(Material.TROPICAL_FISH).setName("§6Fisch").build()),
        CUCUMBER("Gurke", createItem(20360, "§6Gurke")),
        BBQ_SAUCE("BBQ Sauce", createItem(4188, "§6BBQ Sauce")),
        CHICKEN("Hähnchen", new ItemBuilder(Material.COOKED_CHICKEN).setName("§6Hähnchen").build()),
        MUSHROOM("Pilze", new ItemBuilder(Material.BROWN_MUSHROOM).setName("§6Pilze").build()),
        KETCHUP("Ketchup", createItem(36441, "§6Ketchup")),
        MAYO("Mayo", createItem(65146, "§6Mayo"));


        private final String name;
        private final ItemStack item;

        Ingredients(String name, ItemStack item) {
            this.name = name;
            this.item = item;
        }

        public String getName() {
            return name;
        }

        public ItemStack getItem() {
            return item;
        }

        public static Ingredients getIngredient(String name) {
            for (Ingredients i : values()) {
                if (i.getName().equalsIgnoreCase(name)) {
                    return i;
                }
            }
            return null;
        }

        public static Ingredients getIngredient(ItemStack item) {
            for (Ingredients i : values()) {
                if (i.getItem().equals(item)) {
                    return i;
                }
            }
            return null;
        }
    }

    enum Burgers {

        CHEESEBURGER("Cheeseburger", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.CHEESE, Ingredients.BUN}),
        HAMBURGER("Hamburger", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.BUN}),
        FISHBURGER("Fishburger", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.FISH, Ingredients.BUN}),
        MACBIC("MacBig", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.CHEESE, Ingredients.SALAD, Ingredients.ONION, Ingredients.BUN}),
        KAESEROJAL("KäseRojal", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.CHEESE, Ingredients.CUCUMBER, Ingredients.ONION, Ingredients.BUN}),
        BACONBURGER("Baconburger", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.BACON, Ingredients.BUN}),
        VEGGIEBURGER("Veggieburger", new Ingredients[]{Ingredients.BUN, Ingredients.SALAD, Ingredients.TOMATO, Ingredients.CUCUMBER, Ingredients.ONION, Ingredients.BUN}),
        CHICKENBURGER("Chickenburger", new Ingredients[]{Ingredients.BUN, Ingredients.CHICKEN, Ingredients.CHEESE, Ingredients.SALAD, Ingredients.TOMATO, Ingredients.BUN}),
        DOUBLECHEESEBURGER("Double Cheeseburger", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.CHEESE, Ingredients.BEEF, Ingredients.CHEESE, Ingredients.BUN}),
        BBQBURGER("BBQ Burger", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.BACON, Ingredients.CHEESE, Ingredients.BBQ_SAUCE, Ingredients.BUN}),
        MUSHROOMSWISSBURGER("Mushroom Swiss Burger", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.CHEESE, Ingredients.MUSHROOM, Ingredients.BUN}),
        DELUXEBURGER("Deluxe Burger", new Ingredients[]{Ingredients.BUN, Ingredients.BEEF, Ingredients.CHEESE, Ingredients.SALAD, Ingredients.TOMATO, Ingredients.ONION, Ingredients.CUCUMBER, Ingredients.KETCHUP, Ingredients.MAYO, Ingredients.BUN});


        private final String name;
        private final Ingredients[] ingredients;

        Burgers(String name, Ingredients[] ingredients) {
            this.name = name;
            this.ingredients = ingredients;
        }

        public String getName() {
            return name;
        }

        public Ingredients[] getIngredients() {
            return ingredients;
        }

        public static Burgers getBurger(String name) {
            for (Burgers b : values()) {
                if (b.getName().equalsIgnoreCase(name)) {
                    return b;
                }
            }
            return null;
        }

        public static boolean isBurger(String name) {
            return getBurger(name) != null;
        }

        public static Burgers getRandomBurger() {
            return values()[new Random().nextInt(values().length)];
        }

    }

    public static String PREFIX = "§8[§6Burger§8] §6» §7";
    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static HashMap<String, Burgers> BURGER = new HashMap<>();
    public static HashMap<String, Integer> SCORE = new HashMap<>();
    public static HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();
    public static HashMap<String, ArrayList> NEEDED = new HashMap<>();
    public final String[] TEXT = new String[]{"Moin Moin, ich hätte gerne einen ", "Laber mich mal jetzt nicht voll und mach mir einen ", "Hallo! Darf ich bitte einen "};

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/burgerfryer");
            return true;
        }

        if (GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if (p.getLocation().distance(new Location(Script.WORLD, 459, 66, 765, -11.671753f, 10.054159f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe des Restaurants.");
            return true;
        }

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        if (Premium.hasPremium(p)) {
            cooldown.put(p.getName(), System.currentTimeMillis() + 15 * 60 * 1000L);
        } else {
            cooldown.put(p.getName(), System.currentTimeMillis() + 20 * 60 * 1000L);
        }
        GFB.CURRENT.put(p.getName(), GFB.BURGERFRYER);
        int count = GFB.BURGERFRYER.getLevel(p) + Script.getRandom(5, 7);
        SCORE.put(p.getName(), count);
        TOTAL_SCORE.put(p.getName(), count);
        p.sendMessage(PREFIX + "Du hast " + count + " Burger zu braten.");
        Burgers burger = Burgers.getRandomBurger();
        p.sendMessage("§8[§c" + "NPC" + "§8] §f" + "Kunde" + " sagt: §f" + TEXT[Script.getRandom(0, TEXT.length - 1)] + burger.getName());
        p.sendMessage(Messages.INFO + "Du brauchst dafür folgende Zutaten:");
        for (Ingredients i : burger.getIngredients()) {
            p.sendMessage(Messages.INFO + "§8× §7" + i.getName());
        }
        p.sendMessage(Messages.INFO + "Klicke nun Rechtsklick auf den Burger neben der Spüle.");
        BURGER.put(p.getName(), burger);
        ArrayList<Ingredients> ingredients = new ArrayList<>();
        Collections.addAll(ingredients, burger.getIngredients());
        NEEDED.put(p.getName(), ingredients);
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (p.getOpenInventory().getTitle().startsWith("§8[§6Burger§8] §6» §7")) return;
        if (!BURGER.containsKey(p.getName())) return;
        if (!(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 454, 68, 766))) &&(!(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 461, 68, 767))))) return;

        Inventory inv = Bukkit.createInventory(null, 18, "§8[§6Burger§8] §6» §7" + BURGER.get(p.getName()).getName());
        int count = 0;
        for (Ingredients i : Ingredients.values()) {
            inv.setItem(count++, i.getItem());
        }
        inv.setItem(17, new ItemBuilder(Material.GREEN_WOOL).setName("§aFertig").build());
        Script.fillInv(inv);
        Cache.saveInventory(p);
        p.getInventory().clear();
        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!e.getView().getTitle().startsWith("§8[§6Burger§8] §6» §7")) return;
        if (!BURGER.containsKey(p.getName())) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        if (e.getClickedInventory() == null || e.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (!NEEDED.containsKey(p.getName())) return;
        if (SCORE.get(p.getName()) < 1) {
            p.sendMessage(PREFIX + "Du hast nun alle Burger zubereitet.");
            GFB.BURGERFRYER.addExp(p, GFB.BURGERFRYER.getLevel(p) + Script.getRandom(5, 7) / 2);
            PayDay.addPayDay(p, (GFB.BURGERFRYER.getLevel(p) + (TOTAL_SCORE.get(p.getName()))) * 3);
            BURGER.remove(p.getName());
            SCORE.remove(p.getName());
            NEEDED.remove(p.getName());
            GFB.CURRENT.remove(p.getName());
            Script.addEXP(p, GFB.BURGERFRYER.getLevel(p) + TOTAL_SCORE.get(p.getName()) * 2);
            TOTAL_SCORE.remove(p.getName());
            p.closeInventory();
        }
        if (e.getCurrentItem().getType().equals(Material.GREEN_WOOL)) {
            e.getView().close();
            if (SCORE.get(p.getName()) > 1) {
                if (NEEDED.get(p.getName()).isEmpty()) {
                    p.sendMessage(PREFIX + "Du hast den Burger erfolgreich zubereitet.");
                    p.sendMessage(Messages.INFO + "Klicke nun Rechtsklick auf den Burger neben der Spüle.");
                    Burgers burger = Burgers.getRandomBurger();
                    p.sendMessage("§8[§c" + "NPC" + "§8] §f" + "Kunde" + " sagt: §f" + TEXT[Script.getRandom(0, TEXT.length - 1)] + burger.getName());
                    BURGER.replace(p.getName(), burger);
                    NEEDED.remove(p.getName());
                    ArrayList<Ingredients> ingredients = new ArrayList<>();
                    Collections.addAll(ingredients, burger.getIngredients());
                    NEEDED.put(p.getName(), ingredients);
                    SCORE.replace(p.getName(), SCORE.get(p.getName()) - 1);
                    p.sendMessage(Messages.INFO + "Du brauchst dafür folgende Zutaten:");
                    for (Ingredients i : burger.getIngredients()) {
                        p.sendMessage(Messages.INFO + "§8× §7" + i.getName());
                    }

                } else {
                    p.sendMessage(Messages.ERROR + "Du hast noch nicht alle Zutaten hinzugefügt.");
                }
                return;
            } else {
                if (NEEDED.get(p.getName()).isEmpty()) {
                    e.getView().close();
                    p.sendMessage(PREFIX + "Du hast nun alle Burger zubereitet.");
                    GFB.BURGERFRYER.addExp(p, GFB.BURGERFRYER.getLevel(p) + Script.getRandom(5, 7) / 2);
                    PayDay.addPayDay(p, (GFB.BURGERFRYER.getLevel(p) + (TOTAL_SCORE.get(p.getName()))) * 2);
                    BURGER.remove(p.getName());
                    SCORE.remove(p.getName());
                    NEEDED.remove(p.getName());
                    GFB.CURRENT.remove(p.getName());
                    Script.addEXP(p, GFB.BURGERFRYER.getLevel(p) + Script.getRandom(5, 7) * 2);
                    TOTAL_SCORE.remove(p.getName());
                } else {
                    p.sendMessage(Messages.ERROR + "Du hast noch nicht alle Zutaten hinzugefügt.");
                }
                return;
            }
        }

        Ingredients ingredient = Ingredients.getIngredient(e.getCurrentItem());
        if (ingredient == null) return;
        if (NEEDED.get(p.getName()).contains(ingredient)) {
            NEEDED.get(p.getName()).remove(ingredient);
            p.sendMessage(PREFIX + "Du hast die Zutat §6" + ingredient.getName() + " §7hinzugefügt.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        } else {
            p.sendMessage(Messages.ERROR + "Du brauchst diese Zutat nicht.");
            if (GFB.BURGERFRYER.getLevel(p) > 1) {
                e.getView().close();
                p.sendMessage(Messages.INFO + "Klicke nun Rechtsklick auf den Burger neben der Spüle.");
                Burgers burger = Burgers.getRandomBurger();
                p.sendMessage("§8[§c" + "NPC" + "§8] §f" + "Kunde" + " sagt: §f" + TEXT[Script.getRandom(0, TEXT.length - 1)] + burger.getName());
                for (Ingredients i : burger.getIngredients()) {
                    p.sendMessage(Messages.INFO + "§8× §7" + i.getName());
                }
                BURGER.replace(p.getName(), burger);
                NEEDED.remove(p.getName());
                ArrayList<Ingredients> ingredients = new ArrayList<>();
                Collections.addAll(ingredients, burger.getIngredients());
                NEEDED.put(p.getName(), ingredients);
                SCORE.replace(p.getName(), SCORE.get(p.getName()) - 1);
                TOTAL_SCORE.replace(p.getName(), TOTAL_SCORE.get(p.getName()) - 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getView().getTitle().startsWith("§8[§6Burger§8] §6» §7")) {
            Cache.loadInventory(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!BURGER.containsKey(p.getName())) return;
        BURGER.remove(p.getName());
        SCORE.remove(p.getName());
        TOTAL_SCORE.remove(p.getName());
        NEEDED.remove(p.getName());
        GFB.CURRENT.remove(p.getName());
        Cache.loadInventory(p);
    }

    public static ItemStack createItem(int headid, String name) {
        ItemStack item = Script.getHead(headid);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

}
