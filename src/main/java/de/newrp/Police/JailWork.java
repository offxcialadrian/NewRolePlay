package de.newrp.Police;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Government.Stadtkasse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class JailWork implements CommandExecutor, Listener {

    public static HashMap<String, String> license = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Jail.isInJail(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Gefängnis.");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 1034, 61, 572, 273.69238f, 5.6780663f))>5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht am Arbeitsplatz.");
            return true;
        }

        p.sendMessage(Messages.INFO + "Erstelle nun das Kennzeichen.");
        p.sendMessage(Messages.INFO + "Klicke auf die Buchstaben und Zahlen um das Kennzeichen zu erstellen.");

        license.put(p.getName(), getRandomLicensePlate());
        Inventory inv = Bukkit.createInventory(null, 9, "§8» §6" + license.get(p.getName()));
        inv.setItem(0, new ItemBuilder(Material.PAPER).setName("N").setLore("§bRechtsklick für den nächsten Buchstaben", "§bLinksklick um einen Buchstaben zurückzugehen").build());
        inv.setItem(1, new ItemBuilder(Material.PAPER).setName("§8-").build());
        inv.setItem(2, new ItemBuilder(Material.PAPER).setName("A").setLore("§bLinksklick für den nächsten Buchstaben", "§bRechtsklick um einen Buchstaben zurückzugehen").build());
        inv.setItem(3, new ItemBuilder(Material.PAPER).setName("A").setLore("§bLinksklick für den nächsten Buchstaben", "§bRechtsklick um einen Buchstaben zurückzugehen").build());
        inv.setItem(4, new ItemBuilder(Material.PAPER).setName("§8-").build());
        inv.setItem(5, new ItemBuilder(Material.PAPER).setName("0").setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
        inv.setItem(6, new ItemBuilder(Material.PAPER).setName("0").setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
        inv.setItem(7, new ItemBuilder(Material.PAPER).setName("0").setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
        inv.setItem(8, new ItemBuilder(Material.EMERALD_BLOCK).setName("§aBestätigen").build());
        p.openInventory(inv);
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(!license.containsKey(p.getName())) return;
        if(!e.getView().getTitle().equalsIgnoreCase("§8» §6" + license.get(p.getName()))) return;
        e.setCancelled(true);
        if(e.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getType() == Material.AIR) return;
        Inventory inv = e.getClickedInventory();

        if(e.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
            if(inv.getItem(0).getItemMeta().getDisplayName().equalsIgnoreCase(license.get(p.getName()).substring(0, 1)) &&
                    inv.getItem(2).getItemMeta().getDisplayName().equalsIgnoreCase(license.get(p.getName()).substring(2, 3)) &&
                    inv.getItem(3).getItemMeta().getDisplayName().equalsIgnoreCase(license.get(p.getName()).substring(3, 4)) &&
                    inv.getItem(5).getItemMeta().getDisplayName().equalsIgnoreCase(license.get(p.getName()).substring(5, 6)) &&
                    inv.getItem(6).getItemMeta().getDisplayName().equalsIgnoreCase(license.get(p.getName()).substring(6, 7)) &&
                    inv.getItem(7).getItemMeta().getDisplayName().equalsIgnoreCase(license.get(p.getName()).substring(7, 8))) {
                p.sendMessage(Messages.INFO + "Du hast das Kennzeichen erfolgreich erstellt.");
                Stadtkasse.addStadtkasse(15, "PrisonIndustries", null);
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0F, 1.0F);
                p.sendMessage(Messages.INFO + "Deine Haftzeit hat sich um " + (int) (Script.getPercent(5, Jail.getJail(p).getJailtimeLeft())) + " Sekunden verringert.");
                Jail.removeJailTime(p, (int) Script.getPercent(5, Jail.getJail(p).getJailtimeLeft()));
                p.closeInventory();
                license.remove(p.getName());
            } else {
                p.sendMessage(Messages.ERROR + "Das Kennzeichen ist nicht korrekt.");
            }
            return;
        }

        if(e.getSlot() == 0) {
            if(!e.isRightClick()) {
                inv.setItem(0, new ItemBuilder(Material.PAPER).setName(getNextLetter(inv.getItem(0).getItemMeta().getDisplayName())).setLore("§bRechtsklick für den nächsten Buchstaben", "§bLinksklick um einen Buchstaben zurückzugehen").build());
            } else {
                inv.setItem(0, new ItemBuilder(Material.PAPER).setName(getPreviousLetter(inv.getItem(0).getItemMeta().getDisplayName())).setLore("§bRechtsklick für den nächsten Buchstaben", "§bLinksklick um einen Buchstaben zurückzugehen").build());
            }
        } else if(e.getSlot() == 2) {
            if(!e.isRightClick()) {
                inv.setItem(2, new ItemBuilder(Material.PAPER).setName(getNextLetter(inv.getItem(2).getItemMeta().getDisplayName())).setLore("§bRechtsklick für den nächsten Buchstaben", "§bLinksklick um einen Buchstaben zurückzugehen").build());
            } else {
                inv.setItem(2, new ItemBuilder(Material.PAPER).setName(getPreviousLetter(inv.getItem(2).getItemMeta().getDisplayName())).setLore("§bRechtsklick für den nächsten Buchstaben", "§bLinksklick um einen Buchstaben zurückzugehen").build());
            }
        } else if(e.getSlot() == 3) {
            if(!e.isRightClick()) {
                inv.setItem(3, new ItemBuilder(Material.PAPER).setName(getNextLetter(inv.getItem(3).getItemMeta().getDisplayName())).setLore("§bRechtsklick für den nächsten Buchstaben", "§bLinksklick um einen Buchstaben zurückzugehen").build());
            } else {
                inv.setItem(3, new ItemBuilder(Material.PAPER).setName(getPreviousLetter(inv.getItem(3).getItemMeta().getDisplayName())).setLore("§bRechtsklick für den nächsten Buchstaben", "§bLinksklick um einen Buchstaben zurückzugehen").build());
            }
        } else if(e.getSlot() == 5) {
            if(!e.isRightClick()) {
                inv.setItem(5, new ItemBuilder(Material.PAPER).setName(getNextNumber(inv.getItem(5).getItemMeta().getDisplayName())).setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
            } else {
                inv.setItem(5, new ItemBuilder(Material.PAPER).setName(getPreviousNumber(inv.getItem(5).getItemMeta().getDisplayName())).setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
            }
        } else if(e.getSlot() == 6) {
            if(!e.isRightClick()) {
                inv.setItem(6, new ItemBuilder(Material.PAPER).setName(getNextNumber(inv.getItem(6).getItemMeta().getDisplayName())).setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
            } else {
                inv.setItem(6, new ItemBuilder(Material.PAPER).setName(getPreviousNumber(inv.getItem(6).getItemMeta().getDisplayName())).setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
            }
        } else if(e.getSlot() == 7) {
            if(!e.isRightClick()) {
                inv.setItem(7, new ItemBuilder(Material.PAPER).setName(getNextNumber(inv.getItem(7).getItemMeta().getDisplayName())).setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
            } else {
                inv.setItem(7, new ItemBuilder(Material.PAPER).setName(getPreviousNumber(inv.getItem(7).getItemMeta().getDisplayName())).setLore("§bLinksklick für die nächste Zahl", "§bRechtsklick um eine Zahl zurückzugehen").build());
            }
        } else {
        }

    }

    public static String getRandomLicensePlate() {
        StringBuilder license = new StringBuilder("N-");
        for (int i = 0; i < 2; i++) {
            license.append((char) (Math.random() * 26 + 65));
        }
        license.append("-");
        for (int i = 0; i < 3; i++) {
            license.append((int) (Math.random() * 10));
        }
        return license.toString();
    }

    public static String getNextLetter(String letter) {
        char c = letter.charAt(0);
        if(c == 'Z') return "A";
        return String.valueOf((char) (c + 1));
    }

    public static String getPreviousLetter(String letter) {
        char c = letter.charAt(0);
        if(c == 'A') return "Z";
        return String.valueOf((char) (c - 1));
    }

    public static String getNextNumber(String number) {
        int n = Integer.parseInt(number);
        if(n == 9) return "0";
        return String.valueOf(n + 1);
    }

    public static String getPreviousNumber(String number) {
        int n = Integer.parseInt(number);
        if(n == 0) return "9";
        return String.valueOf(n - 1);
    }

}
