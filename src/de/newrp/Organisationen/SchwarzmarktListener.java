package de.newrp.Organisationen;

import de.newrp.API.PaymentType;
import de.newrp.API.Schwarzmarkt;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
    public void onInteract(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.VILLAGER)) {
            Villager v = (Villager) e.getRightClicked();
            e.setCancelled(true);
            if (v.getCustomName() != null && v.getCustomName().equals("Schwarzmarkt")) {
                Player p = e.getPlayer();

                if (!Script.isInRange(Schwarzmarkt.CURRENT_LOCATION.getLocation(), v.getLocation(), 10)) {
                    v.remove();
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
                        String[] text = new String[]{"Besorg mir erst " + item.getAmount() + " " + item.getName() + ". Danach können wir über das Geschäftliche reden.",
                                "Gib mir " + item.getAmount() + " " + item.getName() + ". Dann reden wir über das Geschäftliche.",
                                "Hör zu. Besorg mir erstmal " + item.getAmount() + " " + item.getName() + ". Danach reden wir weiter."};
                        p.sendMessage(Schwarzmarkt.PREFIX + text[Script.getRandom(0, text.length - 1)]);
                    }
                }
            }
        }
    }

    public void openGUI(Player p) {
        int[] amount = Schwarzmarkt.getSchwarzmarkt().getItemAmounts();
        Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, "§cSchwarzmarkt");
        int i = 0;
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.BEETROOT_SEEDS, amount[0]), "§aKräuter Samen", "§c80$"));
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.BEETROOT_SEEDS, amount[1]), "§7Pulver Samen", "§c100$"));
        if (amount[2] > 0) {
            inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.INK_SAC, 1, (short) 15), "§bSpezial-Dünger", "§c150$"));
        }
        inv.setItem(i++, Script.setNameAndLore(new ItemStack(Material.BLAZE_ROD, 1), "§7Brechstange", "§c600$"));
        p.openInventory(inv);
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
                    boolean badFrak = o!=null;
                    switch (name) {
                        case "§aKräuter Samen":
                            if (badFrak) {
                                int price = 80;
                                if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                    p.getInventory().addItem(Script.setName(new ItemStack(Material.BEETROOT_SEEDS, is.getAmount()), "§aKräuter Samen"));
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                    Script.removeMoney(p, PaymentType.CASH, price);
                                } else {
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                                }
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_TRADE[Script.getRandom(0, TEXT_NO_TRADE.length - 1)]);
                            }
                            break;
                        case "§7Pulver Samen":
                            if (badFrak) {
                                int price = 100;
                                if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                    p.getInventory().addItem(Script.setName(new ItemStack(Material.BEETROOT_SEEDS, is.getAmount()), "§7Pulver Samen"));
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                    Script.removeMoney(p, PaymentType.CASH, price);
                                } else {
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                                }
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_TRADE[Script.getRandom(0, TEXT_NO_TRADE.length - 1)]);
                            }
                            break;
                        case "§bSpezial-Dünger":
                            if (badFrak) {
                                int price = 150;
                                if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                    p.getInventory().addItem(Script.setName(new ItemStack(Material.INK_SAC, is.getAmount(), (byte) 15), "§bSpezial-Dünger"));
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                    Script.removeMoney(p, PaymentType.CASH, price);
                                } else {
                                    p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_MONEY[Script.getRandom(0, TEXT_NO_MONEY.length - 1)]);
                                }
                            } else {
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_NO_TRADE[Script.getRandom(0, TEXT_NO_TRADE.length - 1)]);
                            }
                            break;
                        case "§7Brechstange": {
                            int price = 600;
                            if (Script.getMoney(p, PaymentType.CASH) >= price) {
                                p.getInventory().addItem(Script.brechstange());
                                p.sendMessage(Schwarzmarkt.PREFIX + TEXT_POST_TRADE[Script.getRandom(0, TEXT_POST_TRADE.length - 1)]);
                                Script.removeMoney(p, PaymentType.CASH, price);
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
}
