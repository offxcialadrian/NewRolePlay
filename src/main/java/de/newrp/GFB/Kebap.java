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

public class Kebap implements Listener, CommandExecutor {

    enum Ingredients {
        BROT("Dönerbrot", createItem(29884, "§6Dönerbrot")),
        KALBFLEISCH("Kalbfleisch", new ItemBuilder(Material.COOKED_BEEF).setName("§6Kalbfleisch").build()),
        SALAT("Salat", createItem(35715, "§6Salat")),
        TOMATE("Tomate", createItem(32587, "§6Tomate")),
        GURKE("Gurke", createItem(63694, "§6Gurke")),
        ZWIEBEL("Zwiebel", createItem(33323, "§6Zwiebel")),
        ROTKOHL("Rotkohl", createItem(51580, "§6Rotkohl")),
        FETA("Feta", createItem(43474, "§6Feta")),
        KNOBLAUCH("Knoblauch", createItem(40491, "§6Knoblauchsauce")),
        KRAEUTERSAUCE("Kräutersauce", createItem(40490, "§6Kräutersauce")),
        SCHARFESAUCE("Scharfe Sauce", createItem(40394, "§6Scharfe Sauce"));



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

    enum Kebaps {

        KOMBI_1("Kombi 1", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.SALAT, Ingredients.TOMATE, Ingredients.GURKE, Ingredients.ZWIEBEL, Ingredients.KNOBLAUCH}),

        KOMBI_2("Kombi 2", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.SALAT, Ingredients.GURKE, Ingredients.ZWIEBEL, Ingredients.KRAEUTERSAUCE}),

        KOMBI_3("Kombi 3", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.SALAT, Ingredients.GURKE, Ingredients.ZWIEBEL, Ingredients.KRAEUTERSAUCE, Ingredients.FETA}),

        KOMBI_4("Kombi 4", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.SALAT, Ingredients.GURKE, Ingredients.ZWIEBEL, Ingredients.KRAEUTERSAUCE, Ingredients.SCHARFESAUCE}),

        KOMBI_5("Kombi 5", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.KRAEUTERSAUCE}),

        KOMBI_6("Kombi 6", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.SALAT, Ingredients.TOMATE, Ingredients.GURKE, Ingredients.ZWIEBEL, Ingredients.ROTKOHL, Ingredients.FETA, Ingredients.KRAEUTERSAUCE, Ingredients.SCHARFESAUCE}),

        KOMBI_7("Kombi 7", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.SALAT, Ingredients.TOMATE, Ingredients.GURKE, Ingredients.ZWIEBEL, Ingredients.SALAT, Ingredients.ROTKOHL, Ingredients.FETA, Ingredients.KRAEUTERSAUCE, Ingredients.SCHARFESAUCE, Ingredients.KNOBLAUCH}),

        KOMBI_8("Kombi 8", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.SALAT, Ingredients.TOMATE, Ingredients.GURKE, Ingredients.SALAT, Ingredients.ROTKOHL, Ingredients.FETA, Ingredients.KRAEUTERSAUCE, Ingredients.SCHARFESAUCE, Ingredients.KNOBLAUCH}),

        KOMBI_9("Kombi 9", new Ingredients[]{Ingredients.BROT, Ingredients.SALAT, Ingredients.TOMATE, Ingredients.GURKE, Ingredients.ZWIEBEL, Ingredients.SALAT, Ingredients.ROTKOHL, Ingredients.FETA, Ingredients.KRAEUTERSAUCE, Ingredients.SCHARFESAUCE, Ingredients.KNOBLAUCH}),

        KOMBI_10("Kombi 10", new Ingredients[]{Ingredients.BROT, Ingredients.KALBFLEISCH, Ingredients.FETA, Ingredients.KNOBLAUCH});


        private final String name;
        private final Ingredients[] ingredients;

        Kebaps(String name, Ingredients[] ingredients) {
            this.name = name;
            this.ingredients = ingredients;
        }

        public String getName() {
            return name;
        }

        public Ingredients[] getIngredients() {
            return ingredients;
        }

        public static Kebaps getKebap(String name) {
            for (Kebaps b : values()) {
                if (b.getName().equalsIgnoreCase(name)) {
                    return b;
                }
            }
            return null;
        }

        public static boolean isKebap(String name) {
            return getKebap(name) != null;
        }

        public static Kebaps getRandomKebap() {
            return values()[new Random().nextInt(values().length)];
        }

    }

    public static String PREFIX = "§8[§6Kebap§8] §6» §7";
    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static HashMap<String, Kebaps> KEBAP = new HashMap<>();
    public static HashMap<String, Integer> SCORE = new HashMap<>();
    public static HashMap<String, Integer> TOTAL_SCORE = new HashMap<>();
    public static HashMap<String, ArrayList> NEEDED = new HashMap<>();
    public final String[] TEXT = new String[]{"Moin Moin, ich hätte gerne einen ", "Laber mich mal jetzt nicht voll und mach mir einen ", "Hallo! Darf ich bitte einen ", "Hallo! Bitt'schön ein "};

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/dönermann");
            return true;
        }

        if (GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        if (p.getLocation().distance(Navi.DOENERBUDE.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht in der Nähe der Dönerbude.");
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
        GFB.CURRENT.put(p.getName(), GFB.DOENERMANN);
        int count = GFB.DOENERMANN.getLevel(p) + Script.getRandom(5, 7);
        SCORE.put(p.getName(), count);
        TOTAL_SCORE.put(p.getName(), count);
        p.sendMessage(PREFIX + "Du hast " + count + " Döner zuzubereiten.");
        Kebaps kebap = Kebaps.getRandomKebap();
        StringBuilder sb = new StringBuilder("§8[§c" + "NPC" + "§8] §f" + "Kunde" + " sagt: §f" + TEXT[Script.getRandom(0, TEXT.length - 1)] + "Döner mit ");
        for (Ingredients i : kebap.getIngredients()) {
            sb.append(i.getName());
            if (i != kebap.getIngredients()[kebap.getIngredients().length - 1]) {
                sb.append(", ");
            }
        }
        p.sendMessage(sb.toString());
        p.sendMessage(Messages.INFO + "Klicke nun Rechtsklick auf die Zutaten.");
        KEBAP.put(p.getName(), kebap);
        ArrayList<Ingredients> ingredients = new ArrayList<>();
        Collections.addAll(ingredients, kebap.getIngredients());
        NEEDED.put(p.getName(), ingredients);
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (p.getOpenInventory().getTitle().startsWith("§8[§6Döner§8] §6» §7")) return;
        if (!KEBAP.containsKey(p.getName())) return;
        if (!(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 447, 65, 666))) && !(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 447, 65, 665))) &&
                !(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 447, 65, 664))) && !(e.getClickedBlock().getLocation().equals(new Location(Script.WORLD, 447, 65, 663)))) return;

        Inventory inv = Bukkit.createInventory(null, 18, "§8[§6Döner§8] §6» §7" + KEBAP.get(p.getName()).getName());
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
        if (!e.getView().getTitle().startsWith("§8[§6Döner§8] §6» §7")) return;
        if (!KEBAP.containsKey(p.getName())) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        if (e.getClickedInventory() == null || e.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (!NEEDED.containsKey(p.getName())) return;
        if (SCORE.get(p.getName()) < 1) {
            p.sendMessage(PREFIX + "Du hast nun alle Döner zubereitet.");
            GFB.DOENERMANN.addExp(p, GFB.DOENERMANN.getLevel(p) + Script.getRandom(5, 7) / 2);
            PayDay.addPayDay(p, (GFB.DOENERMANN.getLevel(p) + (TOTAL_SCORE.get(p.getName()))) * 3);
            KEBAP.remove(p.getName());
            SCORE.remove(p.getName());
            NEEDED.remove(p.getName());
            GFB.CURRENT.remove(p.getName());
            Script.addEXP(p, GFB.DOENERMANN.getLevel(p) + TOTAL_SCORE.get(p.getName()) * 2);
            TOTAL_SCORE.remove(p.getName());
            p.closeInventory();
        }
        if (e.getCurrentItem().getType().equals(Material.GREEN_WOOL)) {
            e.getView().close();
            if (SCORE.get(p.getName()) >= 1) {
                if (NEEDED.get(p.getName()).isEmpty()) {
                    Kebaps kebaps = Kebaps.getRandomKebap();
                    StringBuilder sb = new StringBuilder("§8[§c" + "NPC" + "§8] §f" + "Kunde" + " sagt: §f" + TEXT[Script.getRandom(0, TEXT.length - 1)] + "Döner mit ");
                    for (Ingredients i : kebaps.getIngredients()) {
                        sb.append(i.getName());
                        if (i != kebaps.getIngredients()[kebaps.getIngredients().length - 1]) {
                            sb.append(", ");
                        }
                    }
                    p.sendMessage(sb.toString());
                    p.sendMessage(Messages.INFO + "Klicke nun Rechtsklick auf die Zutaten.");
                    KEBAP.replace(p.getName(), kebaps);
                    NEEDED.remove(p.getName());
                    ArrayList<Ingredients> ingredients = new ArrayList<>();
                    Collections.addAll(ingredients, kebaps.getIngredients());
                    NEEDED.put(p.getName(), ingredients);
                    SCORE.replace(p.getName(), SCORE.get(p.getName()) - 1);

                } else {
                    p.sendMessage(Messages.ERROR + "Du hast noch nicht alle Zutaten hinzugefügt.");
                }
                return;
            } else {
                if (NEEDED.get(p.getName()).isEmpty()) {
                    e.getView().close();
                    p.sendMessage(PREFIX + "Du hast nun alle Döner zubereitet.");
                    GFB.DOENERMANN.addExp(p, GFB.DOENERMANN.getLevel(p) + Script.getRandom(5, 7) / 2);
                    PayDay.addPayDay(p, (GFB.DOENERMANN.getLevel(p) + (TOTAL_SCORE.get(p.getName()))) * 2);
                    KEBAP.remove(p.getName());
                    SCORE.remove(p.getName());
                    NEEDED.remove(p.getName());
                    GFB.CURRENT.remove(p.getName());
                    Script.addEXP(p, GFB.DOENERMANN.getLevel(p) + Script.getRandom(5, 7) * 2);
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
            if (GFB.DOENERMANN.getLevel(p) > 1) {
                e.getView().close();
                p.sendMessage(Messages.INFO + "Klicke nun Rechtsklick auf die Zutaten.");
                Kebaps kebaps = Kebaps.getRandomKebap();
                StringBuilder sb = new StringBuilder("§8[§c" + "NPC" + "§8] §f" + "Kunde" + " sagt: §f" + TEXT[Script.getRandom(0, TEXT.length - 1)] + "Döner mit ");
                for (Ingredients i : kebaps.getIngredients()) {
                    sb.append(i.getName());
                    if (i != kebaps.getIngredients()[kebaps.getIngredients().length - 1]) {
                        sb.append(", ");
                    }
                }
                p.sendMessage(sb.toString());
                KEBAP.replace(p.getName(), kebaps);
                NEEDED.remove(p.getName());
                ArrayList<Ingredients> ingredients = new ArrayList<>();
                Collections.addAll(ingredients, kebaps.getIngredients());
                NEEDED.put(p.getName(), ingredients);
                SCORE.replace(p.getName(), SCORE.get(p.getName()) - 1);
                TOTAL_SCORE.replace(p.getName(), TOTAL_SCORE.get(p.getName()) - 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getView().getTitle().startsWith("§8[§6Döner§8] §6» §7")) {
            Cache.loadInventory(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!KEBAP.containsKey(p.getName())) return;
        KEBAP.remove(p.getName());
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
