package de.newrp.Votifier;

import de.newrp.API.*;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import de.newrp.main;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

public enum Votekiste {
    NORMAL(1, "Normale Votekiste", 5, new Items[][]{new Items[]{Items.PREMIUM_1, Items.BASEBALLSCHLAEGER, Items.CHANGE_TOKEN_PERSO,
            Items.SONNENBLUMENKERNE, Items.PISTOLE_MUNITION_50, Items.MP5_MUNITION_50,Items.FEUERWERK, Items.REZEPT}, new Items[]{Items.EXP_500, Items.EXP_750}}),

    SPECIAL(2, "Special Votekiste", 10, new Items[][]{new Items[]{Items.FEUERWERK, Items.REZEPT},
            new Items[]{Items.PREMIUM_3, Items.PREMIUM_7},
            new Items[]{Items.EXP_1000, Items.EXP_1250, Items.EXP_1500},
            new Items[]{Items.KEVLAR_LEICHT, Items.KEVLAR_SCHWER}, new Items[]{Items.PISTOLE_MUNITION_50, Items.PISTOLE_MUNITION_75},
            new Items[]{Items.MP5_MUNITION_50, Items.MP5_MUNITION_75}}),

    ULTIMATE(3, "Ultimate Votekiste", 25, new Items[][]{new Items[]{Items.CHANGE_TOKEN_PERSO, Items.KEVLAR_SCHWER, Items.REZEPT}, new Items[]{Items.PREMIUM_7, Items.PREMIUM_14},
            new Items[]{Items.EXP_1500, Items.EXP_2000, Items.EXP_2500}, new Items[]{Items.PISTOLE_MUNITION_75, Items.PISTOLE_MUNITION_100},
            new Items[]{Items.MP5_MUNITION_75, Items.MP5_MUNITION_100}});

    public static final HashMap<String, Integer[]> tasks = new HashMap<>();
    private final int id;
    private final String name;
    private final int price;
    private final Items[][] items;

    Votekiste(int id, String name, int price, Items[][] items) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.items = items;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public Items[][] getItems() {
        return this.items;
    }

    public void open(Player p) {
        p.closeInventory();
        VoteListener.removeVotepoints(p, getPrice());
        Debug.debug("opened votekiste");
        ItemStack[] random = new ItemStack[]{
                Script.setName(new ItemStack(Material.COOKED_BEEF, Script.getRandom(1, 10)), "§6§lGebratenes Rindfleisch"),
                Script.setName(new ItemStack(Material.PORKCHOP, Script.getRandom(1, 10)), "§6§lGebratenes Schweinefleisch"),
                Script.setName(new ItemStack(Material.COOKED_CHICKEN, Script.getRandom(1, 10)), "§6§lGebratenes Hühnchen"),
                Script.setName(new ItemStack(Material.POTION, Script.getRandom(1, 3)), "§9Trinkwasser"),
                Script.setName(new ItemStack(Material.PAPER, Script.getRandom(1, 5)), "§7Verband"),
                Script.setName(new ItemStack(Material.PAPER, Script.getRandom(1, 10)), "§7Lottoschein"),
                Script.setName(new ItemStack(Material.GLASS_BOTTLE, Script.getRandom(1, 3)), "§7Pfandflasche"),
        };
        ArrayList<ItemStack> items = new ArrayList<>();
        Items[][] raw = this.getItems();
        int x = 0;
        for (Items[] item : raw) {
            if (x == 0) {
                for (Items is : item) {
                    if (p.getLevel() == 1 && is.equals(Items.BASEBALLSCHLAEGER)) continue;
                    if (Script.getRandom(1, (this == NORMAL ? 3 : this == SPECIAL ? 4 : this == ULTIMATE ? 5 : 3)) == 2) {
                        items.add(random[Script.getRandom(0, random.length - 1)]);
                    } else {
                        items.add(Script.setName(is.getItem(), "§6§l" + is.getName()));
                    }
                }
                x = 1;
            } else {
                items.add(item[Script.getRandom(0, item.length - 1)].getItem());
            }
        }
        Collections.shuffle(items);

        Debug.debug("reached another step");

        Inventory inv = p.getServer().createInventory(null, 27, "§6Votekiste");
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, Script.setName(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), "§r"));
        }
        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, Script.setName(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), "§r"));
        }
        inv.setItem(4, Script.setName(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "§c§l▼"));
        for (int i = 9; i <= 17; i++) {
            inv.setItem(i, items.get((i - 9 + items.size()) % items.size()));
        }
        inv.setItem(22, Script.setName(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "§c§l▲"));
        p.openInventory(inv);

        Debug.debug("reached another another step");

        final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main.getInstance(), new Runnable() {
            int interval = 2;
            int count = 0;
            int tick = 0;

            public void run() {
                count++;
                tick++;
                if (tick == 20) {
                    interval = 3;
                } else if (tick == 80) {
                    interval = 4;
                } else if (tick == 135) {
                    interval = 5;
                } else if (tick == 185) {
                    interval = 6;
                } else if (tick == 210) {
                    interval = 7;
                } else if (tick == 230) {
                    interval = 8;
                } else if (tick == 245) {
                    interval = 9;
                } else if (tick == 260) {
                    interval = 10;
                } else if (tick == 275) {
                    interval = 11;
                } else if (tick == 290) {
                    interval = 12;
                } else if (tick == 300) {
                    interval = 13;
                }
                if (count == interval) {
                    count = 0;
                    Inventory view = p.getOpenInventory().getTopInventory();
                    ItemStack[] cache = new ItemStack[9];
                    for (int i = 9; i < 18; i++) {
                        cache[(i - 9)] = view.getItem(i);
                    }
                    for (int i = 0; i < 9; i++) {
                        if (i == 0) {
                            ItemStack n = items.get(Script.getRandom(0, items.size() - 1));
                            view.setItem(9, Script.setName(n, "§6§l" + n.getItemMeta().getDisplayName()));
                        } else {
                            view.setItem((i + 9), cache[i - 1]);
                        }
                    }
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1F, 1F);
                }
            }
        }, 1L, 1L);

        final BukkitTask taskId1 = Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
            Bukkit.getScheduler().cancelTask(taskId);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
            tasks.remove(p.getName());

            Inventory view = p.getOpenInventory().getTopInventory();
            ItemStack price = view.getItem(13);
            Items i = null;
            for (Items items1 : Items.values()) {
                if (items1.getItem().isSimilar(price) || items1.getName().equals(ChatColor.stripColor(price.getItemMeta().getDisplayName()))) {
                    i = items1;
                    break;
                }
            }
            if (i != null) {
                int id = Script.getNRPID(p);
                switch (i) {
                    case PREMIUM_1:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 1 Tag Premium gewonnen!");
                        Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(1), true);
                        break;
                    case PREMIUM_3:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 3 Tage Premium gewonnen!");
                        Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(3), true);
                        break;
                    case PREMIUM_7:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 7 Tage Premium gewonnen!");
                        Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(7), true);
                        break;
                    case PREMIUM_14:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 14 Tage Premium gewonnen!");
                        Premium.addPremiumStorage(p, TimeUnit.DAYS.toMillis(14), true);
                        break;
                    case CHANGE_TOKEN_PERSO:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 1 ChangeToken (Personalausweis) gewonnen!");
                        Token.PERSONALAUSWEIS.add(Script.getNRPID(p), 1);
                        break;
                    case BASEBALLSCHLAEGER:
                        p.sendMessage(VoteShop.PREFIX + "Du hast einen Baseballschläger gewonnen!");
                        p.getInventory().addItem(Waffen.setAmmo(Baseballschlaeger.getItem(), 500, 500));
                        break;
                    case EXP_500:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 500 Exp gewonnen!");
                        Script.addEXP(p, 500);
                        break;
                    case EXP_750:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 750 Exp gewonnen!");
                        Script.addEXP(p, 750);
                        break;
                    case EXP_1000:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 1000 Exp gewonnen!");
                        Script.addEXP(p, 1000);
                        break;
                    case EXP_1250:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 1250 Exp gewonnen!");
                        Script.addEXP(p, 1250);
                        break;
                    case EXP_1500:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 1500 Exp gewonnen!");
                        Script.addEXP(p, 1500);
                        break;
                    case EXP_2000:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 2000 Exp gewonnen!");
                        Script.addEXP(p, 2000);
                        break;
                    case EXP_2500:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 2500 Exp gewonnen!");
                        Script.addEXP(p, 2500);
                        break;
                    case KEVLAR_LEICHT:
                        p.sendMessage(VoteShop.PREFIX + "Du hast eine leichte Kevlar gewonnen!");
                        p.getInventory().addItem(Script.kevlar(1));
                        break;
                    case KEVLAR_SCHWER:
                        p.sendMessage(VoteShop.PREFIX + "Du hast eine schwere Kevlar gewonnen!");
                        p.getInventory().addItem(Script.kevlar(2));
                        break;
                    /*case LOTTOSCHEIN:
                        p.sendMessage(VoteShop.PREFIX + "Du hast einen Lottoschein gewonnen!");
                        p.sendMessage(Messages.INFO + "Du kannst den Lottoschein mit \"/lotto\" einlösen.");
                        p.getInventory().addItem(Script.setName(Material.PAPER, "§7Lottoschein"));
                        break;*/
                    case SONNENBLUMENKERNE:
                        p.sendMessage(VoteShop.PREFIX + "Du hast Sonnenblumenkerne gewonnen!");
                        p.getInventory().addItem(price);
                        break;
                    case PISTOLE_MUNITION_50:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 50 Munition für die Glory gewonnen!");
                        Weapon.PISTOLE.addMunition(id, 50);
                        break;
                    case PISTOLE_MUNITION_75:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 75 Munition für die Glory gewonnen!");
                        Weapon.PISTOLE.addMunition(id, 75);
                        break;
                    case PISTOLE_MUNITION_100:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 100 Munition für die Glory gewonnen!");
                        Weapon.PISTOLE.addMunition(id, 100);
                        break;
                    case MP5_MUNITION_50:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 50 Munition für die Peacekeeper gewonnen!");
                        Weapon.AK47.addMunition(id, 50);
                        break;
                    case MP5_MUNITION_75:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 75 Munition für die Peacekeeper gewonnen!");
                        Weapon.AK47.addMunition(id, 75);
                        break;
                    case MP5_MUNITION_100:
                        p.sendMessage(VoteShop.PREFIX + "Du hast 100 Munition für die Peacekeeper gewonnen!");
                        Weapon.AK47.addMunition(id, 100);
                        break;
                    case FEUERWERK:
                        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET, Script.getRandom(3, 10));
                        FireworkMeta fm = (FireworkMeta) item.getItemMeta();
                        List<Color> c = Arrays.asList(Color.BLUE, Color.LIME);
                        FireworkEffect effect = FireworkEffect.builder()
                                .flicker(true)
                                .withColor(c)
                                .withFade(c)
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .trail(true)
                                .build();
                        int r = Script.getRandom(0, 9);
                        switch (r) {
                            case 0:
                                c = Arrays.asList(Color.BLUE, Color.LIME);
                                effect = FireworkEffect.builder()
                                        .flicker(true)
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .trail(true)
                                        .build();
                                fm.setPower(2);
                                break;
                            case 1:
                                c = Collections.singletonList(Color.RED);
                                effect = FireworkEffect.builder()
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.STAR)
                                        .trail(true)
                                        .build();
                                fm.setPower(3);
                                break;
                            case 2:
                                c = Collections.singletonList(Color.GREEN);
                                effect = FireworkEffect.builder()
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.CREEPER)
                                        .trail(true)
                                        .build();
                                fm.setPower(2);
                                break;
                            case 3:
                                c = Arrays.asList(Color.BLUE, Color.WHITE);
                                effect = FireworkEffect.builder()
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.BALL)
                                        .trail(true)
                                        .build();
                                fm.setPower(3);
                                break;
                            case 4:
                                c = Arrays.asList(Color.BLUE, Color.NAVY);
                                effect = FireworkEffect.builder()
                                        .flicker(true)
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.STAR)
                                        .trail(true)
                                        .build();
                                fm.setPower(3);
                                break;
                            case 5:
                                c = Collections.singletonList(Color.YELLOW);
                                effect = FireworkEffect.builder()
                                        .flicker(true)
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.STAR)
                                        .trail(true)
                                        .build();
                                fm.setPower(3);
                                break;
                            case 6:
                                c = Collections.singletonList(Color.ORANGE);
                                effect = FireworkEffect.builder()
                                        .flicker(true)
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .trail(true)
                                        .build();
                                fm.setPower(3);
                                break;
                            case 7:
                                c = Collections.singletonList(Color.RED);
                                effect = FireworkEffect.builder()
                                        .flicker(true)
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .trail(true)
                                        .build();
                                fm.setPower(2);
                                break;
                            case 8:
                                c = Arrays.asList(Color.PURPLE, Color.RED, Color.LIME);
                                effect = FireworkEffect.builder()
                                        .flicker(true)
                                        .withColor(c)
                                        .withFade(c)
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .trail(true)
                                        .build();
                                fm.setPower(3);
                                break;
                        }
                        fm.addEffect(effect);
                        item.setItemMeta(fm);
                        p.sendMessage(VoteShop.PREFIX + "Du hast Feuerwerkskörper gewonnen!");
                        p.getInventory().addItem(item);
                        p.closeInventory();
                        break;
                    default:
                        break;
                }
            } else {
                if (price.getType().equals(Material.COOKED_BEEF) || price.getType().equals(Material.COOKED_PORKCHOP) || price.getType().equals(Material.COOKED_CHICKEN)) {
                    p.getInventory().addItem(new ItemStack(price.getType(), price.getAmount()));
                    p.sendMessage(VoteShop.PREFIX + "Du hast etwas zu essen gewonnen!");
                } else if (price.getType().equals(Material.POTION)) {
                    p.getInventory().addItem(price);
                    p.sendMessage(VoteShop.PREFIX + "Du hast Trinkwasser gewonnen!");
                }
            }
            p.closeInventory();

        }, 10 * 20L);

        Integer[] task = new Integer[2];
        task[0] = taskId;
        task[1] = taskId1.getTaskId();
        tasks.put(p.getName(), task);
    }


    public enum Items {
        PREMIUM_1(1, Script.setName(Script.addGlow(new ItemStack(Material.DIAMOND)), "Premium 1 Tag"), 2F),
        PREMIUM_3(2, Script.setName(Script.addGlow(new ItemStack(Material.DIAMOND)), "Premium 3 Tage"), 1.8F),
        PREMIUM_7(3, Script.setName(Script.addGlow(new ItemStack(Material.DIAMOND)), "Premium 7 Tage"), 1.5F),
        PREMIUM_14(4, Script.setName(Script.addGlow(new ItemStack(Material.DIAMOND)), "Premium 14 Tage"), 1F),
        CHANGE_TOKEN_PERSO(9, Script.setName(Material.BOOK, "ChangeToken (Personalausweis)"), 3F),
        BASEBALLSCHLAEGER(11, Baseballschlaeger.getItem(), 7F),
        EXP_500(12, Script.setName(new ItemStack(Material.EXPERIENCE_BOTTLE), "+ 500 Exp"), 20F),
        EXP_750(13, Script.setName(new ItemStack(Material.EXPERIENCE_BOTTLE), "+ 750 Exp"), 18F),
        EXP_1000(14, Script.setName(new ItemStack(Material.EXPERIENCE_BOTTLE), "+ 1000 Exp"), 16F),
        EXP_1250(15, Script.setName(new ItemStack(Material.EXPERIENCE_BOTTLE), "+ 1250 Exp"), 13F),
        EXP_1500(16, Script.setName(new ItemStack(Material.EXPERIENCE_BOTTLE), "+ 1500 Exp"), 10F),
        EXP_2000(17, Script.setName(new ItemStack(Material.EXPERIENCE_BOTTLE), "+ 2000 Exp"), 7F),
        EXP_2500(18, Script.setName(new ItemStack(Material.EXPERIENCE_BOTTLE), "+ 2500 Exp"), 4F),
        KEVLAR_LEICHT(28, Script.kevlar(1), 10F),
        KEVLAR_SCHWER(29, Script.kevlar(2), 8F),
        REZEPT(30, Script.setName(new ItemStack(Material.PAPER), "§rRezept"), 5F),
        //LOTTOSCHEIN(31, Script.setName(Material.PAPER, "§7Lottoschein"), 20F),
        SONNENBLUMENKERNE(32, Script.setName(new ItemStack(Material.PUMPKIN_SEEDS), "Sonnenblumenkerne"), 20F),
        PISTOLE_MUNITION_50(33, Script.setName(Material.ARROW, "50 Munition"), 15F),
        PISTOLE_MUNITION_75(34, Script.setName(Material.ARROW, "75 Munition"), 11F),
        PISTOLE_MUNITION_100(35, Script.setName(Material.ARROW, "100 Munition"), 8F),
        MP5_MUNITION_50(36, Script.setName(Material.ARROW, "50 Munition"), 15F),
        MP5_MUNITION_75(37, Script.setName(Material.ARROW, "75 Munition"), 11F),
        MP5_MUNITION_100(38, Script.setName(Material.ARROW, "100 Munition"), 8F),
        FEUERWERK(41, Script.setName(new ItemStack(Material.FIREWORK_ROCKET), "Feuerwerkskörper"), 25F);

        private final int id;
        private final ItemStack item;
        private final float chance;

        Items(int id, ItemStack item, float chance) {
            this.id = id;
            this.item = item;
            this.chance = chance;
        }

        public int getID() {
            return this.id;
        }

        public String getName() {
            return ChatColor.stripColor(getItem().getItemMeta().getDisplayName());
        }

        public ItemStack getItem() {
            return this.item;
        }

        public float getChance() {
            return this.chance;
        }
    }
}

