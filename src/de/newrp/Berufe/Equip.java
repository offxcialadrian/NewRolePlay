package de.newrp.Berufe;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Government.Stadtkasse;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Equip implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§eEquip§8] §e» ";

    public enum Stuff {
        PISTOLE("Pistole", new ItemBuilder(Material.IRON_HORSE_ARMOR).setName("§7Pistole").build(), 1000, 75, null, Beruf.Berufe.POLICE),
        SCHUTZWESTE("Schutzweste", Script.kevlar(1), 500, 0, null, Beruf.Berufe.POLICE),
        HANDSCHELLEN("Handschellen", Script.setName(new ItemStack(Material.LEAD, 2), "§7Handschellen"), 100, 0, null, Beruf.Berufe.POLICE),
        TAZER("Tazer", Script.setName(new ItemStack(Material.WOODEN_HOE, 1), "§7Tazer"), 100, 0, null, Beruf.Berufe.POLICE),
        DONUT("Donut", Script.setName(new ItemStack(Material.COOKIE, 1), "§7Donut"), 1, 0, null, Beruf.Berufe.POLICE),
        MP7("MP7", new ItemBuilder(Material.GOLDEN_HORSE_ARMOR).setName("§7MP7").build(), 2900, 400, null, Beruf.Berufe.POLICE),
        EINSATZSCHILD("Einsatzschild", Script.einsatzschild(1), 1000, 0, Abteilung.Abteilungen.SEK, Beruf.Berufe.POLICE),
        EINSAZTZSCHILD_2("Einsatzschild", Script.einsatzschild(2), 1500, 0, Abteilung.Abteilungen.SEK, Beruf.Berufe.POLICE),
        RAUCHGRANATE("Rauchgranate", Script.rauchgranate(), 100, 0, Abteilung.Abteilungen.SEK, Beruf.Berufe.POLICE),
        FLASHBANG("Flashbang", Script.flashbang(), 100, 0, Abteilung.Abteilungen.SEK, Beruf.Berufe.POLICE),
        FALLSCHIRM("Fallschirm", Script.fallschirm(), 100, 0, Abteilung.Abteilungen.SEK, Beruf.Berufe.POLICE),
        ZEITUNG("Zeitung", Script.setName(new ItemStack(Material.WRITABLE_BOOK), "§7Zeitung"), 100, 0, Abteilung.Abteilungen.CHEFREDAKTION, Beruf.Berufe.NEWS),
        VERBAND("Verband", Script.setName(new ItemStack(Material.PAPER), "§7Verband"), 5, 0, null, Beruf.Berufe.RETTUNGSDIENST),
        GIPS("Gips", Script.setName(new ItemStack(Material.PAPER), "§7Gips"), 50, 0, null, Beruf.Berufe.RETTUNGSDIENST),
        SCHWERE_SCHUTZWESTE("Schwere Schutzweste", Script.kevlar(2), 1000, 0, Abteilung.Abteilungen.SEK, Beruf.Berufe.POLICE);

        private String name;
        private ItemStack item;
        private int cost;
        private int ammo;
        private Abteilung.Abteilungen abteilung;
        private Beruf.Berufe beruf;


        Stuff(String name, ItemStack item, int cost, int ammo, Abteilung.Abteilungen abteilung, Beruf.Berufe beruf) {
            this.name = name;
            this.item = item;
            this.cost = cost;
            this.ammo = ammo;
            this.abteilung = abteilung;
            this.beruf = beruf;
        }

        public String getName() {
            return name;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getCost() {
            return cost;
        }

        public Abteilung.Abteilungen getAbteilung() {
            return abteilung;
        }

        public Beruf.Berufe getBeruf() {
            return beruf;
        }

        public int getAmmo() {
            return ammo;
        }

        public static Stuff getStuff(String name) {
            for (Stuff stuff : values()) {
                if (stuff.getName().equalsIgnoreCase(name)) {
                    return stuff;
                }
            }
            return null;
        }

        public static boolean isEquip(ItemStack is) {
            for (Stuff stuff : values()) {
                if (stuff.getItem().equals(is)) {
                    return true;
                }
            }
            return false;
        }

    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/equip");
            return true;
        }

        if(BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht im BuildMode ausrüsten.");
            return true;
        }

        Beruf.Berufe beruf = Beruf.getBeruf(p);
        Inventory inv = Bukkit.createInventory(null, (beruf == Beruf.Berufe.POLICE && Beruf.getAbteilung(p) == Abteilung.Abteilungen.SEK ? 18 : 9), "§8» §7Equip");

        if (!beruf.hasEquip()) {
            p.sendMessage(Messages.ERROR + "Dein Beruf hat kein Equip.");
            return true;
        }

        if (!Duty.isInDuty(p) && beruf.hasDuty()) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein.");
            return true;
        }

        if (beruf == Beruf.Berufe.POLICE && p.getLocation().distance(new Location(Script.WORLD, 405, 71, 824)) > 10) {
            p.sendMessage(Messages.ERROR + "Du musst dich in der Polizeistation befinden.");
            return true;
        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.RETTUNGSDIENST && p.getLocation().distance(new Location(Script.WORLD,267,75,1253)) > 5) {
            p.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
            return true;
        }

        if(Beruf.getBeruf(p) == Beruf.Berufe.NEWS && p.getLocation().distance(new Location(Script.WORLD,289, 67, 788)) > 5) {
            p.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
            return true;
        }

        for (Stuff stuff : Stuff.values()) {
            if (stuff.getBeruf() == beruf) {
                if (stuff.getAbteilung() == null) {
                    inv.addItem(stuff.getItem());
                } else {
                    if (Beruf.getAbteilung(p) == stuff.getAbteilung()) {
                        inv.addItem(stuff.getItem());
                    }
                }
            }
        }
        p.openInventory(inv);
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getView().getTitle().equalsIgnoreCase("§8» §7Equip")) {
            e.setCancelled(true);
            Stuff stuff = Stuff.getStuff(e.getCurrentItem().getItemMeta().getDisplayName().replace("§7", "").replace("§8", "").replace("§9", "").replace("§e", "").replace("§6", "").replace("§c", "").replace("§a", "").replace("§b", "").replace("§d", "").replace("§f", ""));
            if (stuff == null) return;
            if (Beruf.getBeruf(p) != stuff.getBeruf()) {
                p.sendMessage(Messages.ERROR + "Du kannst dir nur Items von deinem Beruf ausrüsten.");
                return;
            }
            if (stuff.getAbteilung() != null) {
                if (Beruf.getAbteilung(p) != stuff.getAbteilung()) {
                    p.sendMessage(Messages.ERROR + "Du kannst dir nur Items von deiner Abteilung ausrüsten.");
                    return;
                }
            }
            if (!stuff.getBeruf().hasKasse()) {
                if (Stadtkasse.getStadtkasse() < stuff.getCost()) {
                    p.sendMessage(Messages.ERROR + "Die Stadtkasse hat nicht genug Geld.");
                    return;
                }
            }

            if (stuff.getBeruf().hasKasse()) {
                if (stuff.getBeruf().getKasse() < stuff.getCost()) {
                    p.sendMessage(Messages.ERROR + "Die Kasse hat nicht genug Geld.");
                    return;
                }
            }

            if (p.getInventory().firstEmpty() == -1) {
                p.sendMessage(Messages.ERROR + "Dein Inventar ist voll.");
                return;
            }

            if (!stuff.getBeruf().hasKasse()) {
                if (Stadtkasse.getStadtkasse() < stuff.getCost()) {
                    if (stuff != Stuff.HANDSCHELLEN && stuff != Stuff.PISTOLE) {
                        p.sendMessage(Messages.ERROR + "Die Stadtkasse hat nicht genug Geld.");
                        p.closeInventory();
                        return;
                    }
                }
                Stadtkasse.removeStadtkasse(stuff.getCost());
            } else {
                stuff.getBeruf().removeKasse(stuff.getCost());
            }

            for (Weapon w : Weapon.values()) {
                if (w.getName().equalsIgnoreCase(stuff.getName())) {
                    p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), w.getMagazineSize(), 400));
                    p.sendMessage(PREFIX + "Du hast dich mit einer " + w.getName() + " ausgerüstet.");
                    Beruf.getBeruf(p).sendLeaderMessage("§8[§e" + Beruf.getBeruf(p).getName() + "§8] §e» " + Script.getName(p) + " hat sich mit einer " + w.getName() + " ausgerüstet.");
                    Log.LOW.write(p, "hat sich mit einer " + w.getName() + " ausgerüstet.");
                    p.closeInventory();
                    return;
                }
            }

            p.getInventory().addItem(stuff.getItem());
            p.sendMessage(PREFIX + "Du hast dich mit " + stuff.getName() + " ausgerüstet.");
            Beruf.getBeruf(p).sendLeaderMessage("§8[§e" + Beruf.getBeruf(p).getName() + "§8] §e» " + Script.getName(p) + " hat sich mit " + stuff.getName() + " ausgerüstet.");
            Log.LOW.write(p, "hat sich mit " + stuff.getName() + " ausgerüstet.");
            p.closeInventory();

        }
    }


}
