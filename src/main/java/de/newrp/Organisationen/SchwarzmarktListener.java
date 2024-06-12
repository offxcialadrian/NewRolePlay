package de.newrp.Organisationen;

import de.newrp.API.*;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SchwarzmarktListener implements Listener {
    public static final ArrayList<String> VALID_PLAYER = new ArrayList<>();

    public final String[] TEXT_TRUST = new String[]{"Du siehst mir nicht vertrauenswürdig aus.", "Ich kenne dich nicht, also vertraue ich dir nicht.", "Ich vertraue dir nicht."};

    public final String[] TEXT_NO_TRADE = new String[]{"Was willst du denn damit? Verschwinde!", "Ich verkaufe dir nichts.", "Ich verkaufe dir nichts, du bist mir nicht geheuer."};

    public final String[] TEXT_NO_MONEY = new String[]{"Du hast nicht genug Geld.", "Du hast nicht genug Geld, um dir das zu leisten.", "Du hast nicht genug Geld, um dir das zu leisten."};

    public final String[] TEXT_POST_TRADE = new String[]{"Danke fürs Geschäft.", "Danke fürs Geschäft.", "Danke fürs Geschäft."};

    @EventHandler
    public void onInteract(NPCRightClickEvent e) {
        if (e.getNPC().getId() != Schwarzmarkt.SCHWARZMARKT_ID) return;
        e.setCancelled(true);
        Player p = e.getClicker();

        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Schwarzmarkt.PREFIX + "Du musst in einer Organisation sein, um hier einkaufen zu können.");
            return;
        }

        if (VALID_PLAYER.contains(p.getName())) {
            openGUI(p);
        } else {
            Schwarzmarkt.TradeItem item = Schwarzmarkt.getSchwarzmarkt().getTradeItem();
            if (hasItem(p, item, true)) {
                VALID_PLAYER.add(p.getName());
                p.sendMessage(Schwarzmarkt.PREFIX + "Alles klar, dir kann ich vertrauen.");
            } else {
                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_TRUST[Script.getRandom(0, TEXT_TRUST.length - 1)]);
                String[] text = new String[]{"Besorg mir erst " + (item.getAmount() > 1 ? item.getAmount() + "x " : "ein " ) + item.getName() + ". Danach können wir über das Geschäftliche reden.",
                        "Gib mir " + (item.getAmount() > 1 ? item.getAmount() + "x " : "ein " ) + item.getName() + ". Dann reden wir über das Geschäftliche.",
                        "Hör zu. Besorg mir erstmal " + (item.getAmount() > 1 ? item.getAmount() + "x " : "ein " ) + item.getName() + ". Danach reden wir weiter."};
                p.sendMessage(Schwarzmarkt.PREFIX + text[Script.getRandom(0, text.length - 1)]);
            }
        }
    }

    public void openGUI(Player p) {
        int[] amount = Schwarzmarkt.getSchwarzmarkt().getItemAmounts();
        int bribery = getBribery(p);
        Inventory inv = Bukkit.getServer().createInventory(null, 9, "§cSchwarzmarkt");
        int i = 0;
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.BEETROOT_SEEDS, amount[0]), "§aKräuter Samen", "§c" + getMax(125, bribery) + "€"));
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.BEETROOT_SEEDS, amount[1]), "§7Pulver Samen", "§c" + getMax(100, bribery) + "€"));
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.INK_SAC, 1), "§bSpezial-Dünger", "§c" + getMax(55, bribery) + "€"));
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.BLAZE_ROD, 1), "§7Brechstange", "§c" + getMax(200, bribery) + "€"));
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.END_ROD, 1), "§7Testosteron-Spritze", "§c" + getMax(500, bribery) + "€"));
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.LEVER, 1), "§eGraffiti", "§c" + getMax(25, bribery) + "€"));
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.TNT, 1), "§cSprengstoff", "§c" + getMax(1000, bribery) + "€"));
        inv.setItem(i, Script.setNameAndLore(new ItemStack(Material.IRON_SWORD, 1), "§7Machete", "§c" + getMax(2300, bribery) + "€"));
        Script.fillInv(inv);
        p.openInventory(inv);
    }

    private int getMax(int price, int bribery) {
        return Math.max(0, price - bribery);
    }

    public boolean hasItem(Player p, Schwarzmarkt.TradeItem item, boolean remove) {
        Material mat = item.getItemStack().getType();
        int amount = item.getAmount();
        int i = 0;
        boolean b = false;
        for (ItemStack is : p.getInventory().getContents()) {
            if (is != null && !is.getType().equals(Material.AIR)) {
                if (is.getType() == mat) {
                    i += is.getAmount();
                    if (i >= amount) {
                        b = true;
                        break;
                    }
                }
            }
        }
        if (b && remove) {
            i = 0;
            for (ItemStack is : p.getInventory().getContents()) {
                if (is != null && !is.getType().equals(Material.AIR)) {
                    if (is.getType() == mat) {
                        if (i + is.getAmount() >= amount) {
                            int left = amount - i;
                            is.setAmount(is.getAmount() - left);
                            break;
                        } else {
                            i += is.getAmount();
                            is.setAmount(0);
                        }
                    }
                }
            }
        }
        return b;
    }

    private int getBribery(Player player) {
        if (player.getInventory().getItemInMainHand().getType() == Material.SUGAR || player.getInventory().getItemInMainHand().getType() == Material.GREEN_DYE) {
            Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByName(player.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", ""));
            if (purity != null) if (purity.getID() == 0) return 15 * player.getInventory().getItemInMainHand().getAmount();
        }
        return 0;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§cSchwarzmarkt")) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
                    e.setCancelled(true);
                    e.getView().close();
                    ItemStack is = e.getCurrentItem();
                    Player p = (Player) e.getWhoClicked();
                    String name = is.getItemMeta().getDisplayName();
                    Organisation o = Organisation.getOrganisation(p);
                    boolean badFrak = o != null;
                    int bribery = getBribery(p);
                    switch (name) {
                        case "§aKräuter Samen":
                            if (badFrak) {
                                int original = 125;
                                int price = getMax(original, bribery);
                                if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                    p.getInventory().addItem(Script.setName(new ItemStack(Material.BEETROOT_SEEDS, is.getAmount()), "§aKräuter Samen"));
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                    Script.removeMoney(p, PaymentType.CASH, price);
                                    removeBribery(p, original, price, bribery);
                                } else {
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                                }
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_TRADE[Script.getRandom(0, TEXT_NO_TRADE.length - 1)]);
                            }
                            break;
                        case "§7Pulver Samen":
                            if (badFrak) {
                                int original = 100;
                                int price = getMax(original, bribery);
                                if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                    p.getInventory().addItem(Script.setName(new ItemStack(Material.BEETROOT_SEEDS, is.getAmount()), "§7Pulver Samen"));
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                    Script.removeMoney(p, PaymentType.CASH, price);
                                    removeBribery(p, original, price, bribery);
                                } else {
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                                }
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_TRADE[Script.getRandom(0, TEXT_NO_TRADE.length - 1)]);
                            }
                            break;
                        case "§bSpezial-Dünger":
                            if (badFrak) {
                                int original = 55;
                                int price = getMax(original, bribery);
                                if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                    p.getInventory().addItem(Script.setName(new ItemStack(Material.INK_SAC, is.getAmount(), (byte) 15), "§bSpezial-Dünger"));
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                    Script.removeMoney(p, PaymentType.CASH, price);
                                    removeBribery(p, original, price, bribery);
                                } else {
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                                }
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_TRADE[Script.getRandom(0, TEXT_NO_TRADE.length - 1)]);
                            }
                            break;
                        case "§7Brechstange": {
                            int original = 200;
                            int price = getMax(original, bribery);
                            if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                p.getInventory().addItem(Script.brechstange());
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                Script.removeMoney(p, PaymentType.CASH, price);
                                removeBribery(p, original, price, bribery);
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                            }
                            break;
                        }
                        case "§7Testosteron-Spritze" : {
                            int original = 500;
                            int price = getMax(original, bribery);
                            if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                p.getInventory().addItem(Script.setName(new ItemStack(Material.END_ROD, is.getAmount()), "§7Testosteron-Spritze"));
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                Script.removeMoney(p, PaymentType.CASH, price);
                                removeBribery(p, original, price, bribery);
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                            }
                            break;
                        }
                        case "§eGraffiti" : {
                            int original = 25;
                            int price = getMax(original, bribery);
                            if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                p.getInventory().addItem(new ItemBuilder(Material.LEVER).setName("§eGraffiti").build());
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                Script.removeMoney(p, PaymentType.CASH, price);
                                removeBribery(p, original, price, bribery);
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                            }
                            break;
                        }
                        case "§cSprengstoff" : {
                            int original = 1000;
                            int price = getMax(original, bribery);
                            if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                p.getInventory().addItem(new ItemBuilder(Material.TNT).setName("§cSprengstoff").build());
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                Script.removeMoney(p, PaymentType.CASH, price);
                                removeBribery(p, original, price, bribery);
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                            }
                            break;
                        }
                        case "§7Machete" : {
                            int original = 2300;
                            int price = getMax(original, bribery);
                            if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                p.getInventory().addItem(Machete.getItem());
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                Script.removeMoney(p, PaymentType.CASH, price);
                                removeBribery(p, original, price, bribery);
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void removeBribery(Player player, int original, int price, int bribery) {
        if (bribery > 0) {
            int a;
            if (price == 0) {
                a = (int) Math.ceil((double) original / 15);
            } else {
                a = bribery / 15;
            }
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - a);
        }
    }
}
