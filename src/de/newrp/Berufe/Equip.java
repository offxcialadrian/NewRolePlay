package de.newrp.Berufe;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Government.Stadtkasse;
import de.newrp.Police.StartTransport;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Equip implements CommandExecutor, Listener {

    public static final String PREFIX = "§8[§eEquip§8] §e» §7";

    public enum Stuff {
        PISTOLE(1, "Glory", new ItemBuilder(Material.IRON_HORSE_ARMOR).setName("§7Glory").build(), 500, 0, null, Beruf.Berufe.POLICE, false),
        SCHUTZWESTE(2, "Schutzweste", Script.kevlar(1), 800, 0, null, Beruf.Berufe.POLICE, false),
        HANDSCHELLEN(3, "Handschellen", Script.setName(new ItemStack(Material.LEAD, 2), "§7Handschellen"), 50, 0, null, Beruf.Berufe.POLICE, true),
        TAZER(4, "Tazer", Script.tazer(), 250, 0, null, Beruf.Berufe.POLICE, true),
        DONUT(5, "Donut", new ItemBuilder(Material.COOKIE).setAmount(16).setName("§7Donut").build(), 1, 0, null, Beruf.Berufe.POLICE, false),
        MP7(6, "Striker", new ItemBuilder(Material.GOLDEN_HORSE_ARMOR).setName("§7Striker").build(), 800, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, false),
        EINSATZSCHILD(7, "Einsatzschild", Script.einsatzschild(1), 1000, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, true),
        EINSAZTZSCHILD_2(8, "Schweres Einsatzschild", Script.einsatzschild(2), 1700, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, true),
        RAUCHGRANATE(9, "Rauchgranate", Script.rauchgranate(), 300, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, true),
        FLASHBANG(10, "Flashbang", Script.flashbang(), 250, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, true),
        FALLSCHIRM(11, "Fallschirm", Script.fallschirm(), 500, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, false),
        ZEITUNG(12, "Buch und Stift", Script.setName(new ItemStack(Material.WRITABLE_BOOK), "§7Buch und Stift"), 20, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.JOURNALIST}, Beruf.Berufe.NEWS, false),
        VERBAND(13, "Verband", new ItemBuilder(Material.PAPER).setName("§7Verband").setAmount(5).build(), 50, 0, null, Beruf.Berufe.RETTUNGSDIENST, true),
        GIPS(14, "Gips", Script.setName(new ItemStack(Material.PAPER), "§7Gips"), 50, 0, null, Beruf.Berufe.RETTUNGSDIENST, true),
        SCHWERE_SCHUTZWESTE(15, "Schwere Schutzweste", Script.kevlar(2), 2490, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, true),
        SPRITZE(16, "Spritze", Script.setName(new ItemStack(Material.END_ROD), "§7Spritze"), 10, 0, null, Beruf.Berufe.RETTUNGSDIENST, true),
        HUSTEN_IMPFUNG(17, "Husten Impfung", Script.setName(new ItemStack(Material.END_ROD), "§7Husten Impfung"), 50, 0, null, Beruf.Berufe.RETTUNGSDIENST, true),
        KAFFEE(18, "Kaffee", Script.setName(new ItemStack(Material.POTION), "§7Kaffee"), 2, 0, null, Beruf.Berufe.NEWS, false),
        BROT(19, "Brot", new ItemBuilder(Material.BREAD).setAmount(16).setName("§7Brot").build(), 3, 0, null, Beruf.Berufe.RETTUNGSDIENST, false),
        KEKSE(20, "Keks", new ItemBuilder(Material.COOKIE).setAmount(16).setName("§7Keks").build(), 1, 0, null, Beruf.Berufe.NEWS, false),
        SNIPER(21, Weapon.SNIPER.getName(), Weapon.SNIPER.getWeapon(), 2900, 30, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, true),
        DROHNE_COPS(22, "Drohne [Polizei]", new ItemBuilder(Material.WITHER_SKELETON_SKULL).setName("§7Drohne [Polizei]").build(), 1000, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, Beruf.Berufe.POLICE, true),
        DROHNE_NEWS(23, "Drohne [News]", new ItemBuilder(Material.WITHER_SKELETON_SKULL).setName("§7Drohne [News]").build(), 1500, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.CHEFREDAKTION}, Beruf.Berufe.NEWS, true),
        DROHNE_RETTUNGSDIENST(24, "Drohne [Rettungsdienst]", new ItemBuilder(Material.WITHER_SKELETON_SKULL).setName("§7Drohne [Rettungsdienst]").build(), 1000, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.NOTFALLMEDIZIN}, Beruf.Berufe.RETTUNGSDIENST, true),

        MUNITION_PISTOLE(25, Weapon.PISTOLE.getAmmoType().getName(), new ItemBuilder(Material.ARROW).setName(Weapon.PISTOLE.getAmmoType().getName()).setAmount(Weapon.PISTOLE.getMagazineSize()).build(), 10, 0, null, Beruf.Berufe.POLICE, true),
        MUNITION_MP7(26, Weapon.MP7.getAmmoType().getName(), new ItemBuilder(Material.ARROW).setName(Weapon.MP7.getAmmoType().getName()).setAmount(Weapon.MP7.getMagazineSize()).build(), 15, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK}, Beruf.Berufe.POLICE, true),
        //BROT_2(27, "Brot", new ItemBuilder(Material.BREAD).setAmount(16).setName("§7Brot").build(), 3, 0, null, Beruf.Berufe.GOVERNMENT, false),
        TRINKWASSER(28, "Trinkwasser", new ItemBuilder(Material.POTION).setName("§7Trinkwasser").build(), 2, 0, null, Beruf.Berufe.RETTUNGSDIENST, false),
        //TRINKWASSER_MEDIC(29, "Trinkwasser", new ItemBuilder(Material.POTION).setName("§7Trinkwasser").build(), 2, 0, null, Beruf.Berufe.RETTUNGSDIENST, false),
        KEVLAR(29, "Kevlar", Script.kevlar(1), 2900, 0, null, Beruf.Berufe.GOVERNMENT, false),
        APPLE(30, "Apfel", new ItemBuilder(Material.APPLE).setAmount(16).setName("§7Apfel").build(), 1, 0, null, Beruf.Berufe.GOVERNMENT, false),
        LATTE_MACCHIATO(31, "Latte Macchiato", new ItemBuilder(Material.FLOWER_POT).setName("§rLatte Macchiato").build(), 2, 0, null, Beruf.Berufe.GOVERNMENT, false);

        private String name;
        private int id;
        private ItemStack item;
        private int cost;
        private int ammo;
        private Abteilung.Abteilungen[] abteilung;
        private Beruf.Berufe beruf;
        private boolean remove;


        Stuff(int id, String name, ItemStack item, int cost, int ammo, Abteilung.Abteilungen[] abteilung, Beruf.Berufe beruf, boolean remove) {
            this.id = id;
            this.name = name;
            this.item = item;
            this.cost = cost;
            this.ammo = ammo;
            this.abteilung = abteilung;
            this.beruf = beruf;
            this.remove = remove;
        }

        public String getName() {
            return name;
        }

        public int getID() {
            return id;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getCost() {
            return cost;
        }

        public Abteilung.Abteilungen[] getAbteilung() {
            return abteilung;
        }

        public Beruf.Berufe getBeruf() {
            return beruf;
        }

        public int getAmmo() {
            return ammo;
        }

        public boolean removeOnUninvite() {
            return remove;
        }

        public static Stuff getStuff(int id) {
            for (Stuff stuff : values()) {
                if (stuff.getID() == id) {
                    return stuff;
                }
            }
            return null;
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
                if (stuff.getItem().isSimilar(is)) {
                    return true;
                }
            }
            return false;
        }

    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (p == StartTransport.executor) {
            Beruf.getBeruf(p).sendMessage(StartTransport.PREFIX + Script.getName(p) + " hat den Transport abgegeben.");
            Script.executeUpdate("UPDATE city SET equip = equip + " + StartTransport.add);
            StartTransport.executor = null;
            StartTransport.isActive = false;
            StartTransport.LEVEL = 0;
            StartTransport.add = 0;
            Bukkit.broadcastMessage("§8[§6News§8] §6" + Messages.ARROW + "Der Waffentransport der Polizei ist erfolgreich beendet. Die Straßen sind wieder frei.");
            return true;
        }

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/equip");
            return true;
        }

        if (BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht im BuildMode ausrüsten.");
            return true;
        }

        if (Drone.isDrone(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht als Drohne ausrüsten.");
            return true;
        }

        Beruf.Berufe beruf = Beruf.getBeruf(p);
        Inventory inv = Bukkit.createInventory(null, (beruf == Beruf.Berufe.POLICE && (Beruf.getAbteilung(p) == Abteilung.Abteilungen.SEK || Beruf.getAbteilung(p) == Abteilung.Abteilungen.ABTEILUNGSLEITUNG || Beruf.isLeader(p, true)) ? 18 : 9) , "§8» §7Equip");

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

        if (Beruf.getBeruf(p) == Beruf.Berufe.RETTUNGSDIENST && p.getLocation().distance(new Location(Script.WORLD, 267, 75, 1253)) > 5) {
            p.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
            return true;
        }

        if (Beruf.getBeruf(p) == Beruf.Berufe.NEWS && p.getLocation().distance(new Location(Script.WORLD, 289, 67, 788)) > 5) {
            p.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
            return true;
        }

        if (Beruf.getBeruf(p) == Beruf.Berufe.GOVERNMENT && p.getLocation().distance(new Location(Script.WORLD, 540, 88, 981, -91.613525f, 5.674797f)) > 10) {
            p.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
            return true;
        }

        for (Stuff stuff : Stuff.values()) {
            if (stuff.getBeruf() == beruf) {
                if (stuff.getAbteilung() == null || Beruf.isLeader(p, true)) {
                    inv.addItem(stuff.getItem());
                } else {
                    for (Abteilung.Abteilungen abteilung : stuff.getAbteilung()) {
                        if (abteilung == Beruf.getAbteilung(p)) {
                            inv.addItem(stuff.getItem());
                        }
                    }
                }
            }
        }
        p.openInventory(inv);
        return false;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (Stuff.isEquip(e.getItemDrop().getItemStack()) && Stuff.getStuff(ChatColor.stripColor(e.getItemDrop().getItemStack().getItemMeta().getDisplayName())).removeOnUninvite()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getView().getTitle().equalsIgnoreCase("§8» §7Equip")) {
            e.setCancelled(true);
            Stuff stuff = Stuff.getStuff(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
            if (stuff == null) return;
            if (Beruf.getBeruf(p) != stuff.getBeruf()) {
                p.sendMessage(Messages.ERROR + "Du kannst dir nur Items von deinem Beruf ausrüsten.");
                return;
            }
            if (stuff.getAbteilung() != null && !Beruf.isLeader(p, true)) {
                boolean hasAbteilung = false;
                for (Abteilung.Abteilungen abteilung : stuff.getAbteilung()) {
                    if (abteilung == Beruf.getAbteilung(p)) {
                        hasAbteilung = true;
                    }
                }
                if (!hasAbteilung) {
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
            if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE && getAvailableEquip() <= 0) {
                p.sendMessage(Messages.ERROR + "Es sind keine weiteren Equip-Gegenstände verfügbar.");
                return;
            }

            if (stuff.getBeruf().hasKasse()) {
                if (stuff.getBeruf().getKasse() < stuff.getCost()) {
                    p.sendMessage(Messages.ERROR + "Die Kasse hat nicht genug Geld.");
                    return;
                }
            }


            if (stuff == Stuff.SCHUTZWESTE || stuff == Stuff.SCHWERE_SCHUTZWESTE) {
                if (p.getInventory().contains(Material.LEATHER_CHESTPLATE)) {
                    p.sendMessage(Messages.ERROR + "Du kannst nur eine Schutzweste tragen.");
                    return;
                }
            }

            if (stuff == Stuff.EINSATZSCHILD || stuff == Stuff.EINSAZTZSCHILD_2) {
                if (p.getInventory().contains(Material.SHIELD)) {
                    p.sendMessage(Messages.ERROR + "Du kannst nur ein Einsatzschild tragen.");
                    return;
                }
            }

            Equiplog.addToEquipLog(p, stuff);
            if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE) {
                Script.executeUpdate("UPDATE city SET equip = equip - 1");
            }

            if (!stuff.getBeruf().hasKasse()) {
                if (Stadtkasse.getStadtkasse() < stuff.getCost()) {
                    if (stuff != Stuff.HANDSCHELLEN && stuff != Stuff.PISTOLE) {
                        p.sendMessage(Messages.ERROR + "Die Stadtkasse hat nicht genug Geld.");
                        p.closeInventory();
                        return;
                    }
                }
                Stadtkasse.removeStadtkasse(stuff.getCost(), stuff.getName() + " für " + Script.getName(p) + " (" + Beruf.getBeruf(p).getName() + ")");
            } else {
                stuff.getBeruf().removeKasse(stuff.getCost());
            }

            if (stuff == Stuff.MUNITION_MP7 || stuff == Stuff.MUNITION_PISTOLE) {
                Weapon w = null;
                if (stuff == Stuff.MUNITION_MP7) {
                    w = Weapon.MP7;
                } else {
                    w = Weapon.PISTOLE;
                }
                p.getInventory().remove(w.getWeapon().getType());
                int ammunitionInWeapon = Waffen.getAmmo(w.getWeapon()) + Waffen.getAmmoTotal(w.getWeapon());

                int newAmmunitionInWeapon = ammunitionInWeapon + stuff.getItem().getAmount();

                int magazine;
                int total;
                if (newAmmunitionInWeapon > w.getMagazineSize()) {
                    magazine = w.getMagazineSize();
                    total = newAmmunitionInWeapon - w.getMagazineSize();
                } else {
                    magazine = newAmmunitionInWeapon;
                    total = 0;
                }

                p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), magazine, total));
                p.sendMessage(PREFIX + "Du hast dich mit " + stuff.getName() + " ausgerüstet.");
                Beruf.getBeruf(p).sendLeaderMessage("§8[§e" + Beruf.getBeruf(p).getName() + "§8] §e» " + Script.getName(p) + " hat sich mit " + stuff.getName() + " ausgerüstet.");
                Log.LOW.write(p, "hat sich mit " + w.getName() + " ausgerüstet.");
                return;
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

            ItemStack is = stuff.getItem().clone();
            p.getInventory().addItem(is);
            p.sendMessage(PREFIX + "Du hast dich mit " + stuff.getName() + " ausgerüstet.");
            Beruf.getBeruf(p).sendLeaderMessage("§8[§e" + Beruf.getBeruf(p).getName() + "§8] §e» " + Script.getName(p) + " hat sich mit " + stuff.getName() + " ausgerüstet.");
            Log.LOW.write(p, "hat sich mit " + stuff.getName() + " ausgerüstet.");
            p.closeInventory();

        }
    }

    public static void removeEquip(Player p) {
        for (Stuff stuff : Stuff.values()) {
            if (stuff.getBeruf() == Beruf.getBeruf(p)) {
                if (stuff.removeOnUninvite()) {
                    p.getInventory().remove(stuff.getItem());
                }
            }
        }
    }

    public static int getAvailableEquip() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT equip FROM city")) {
            if (rs.next()) return rs.getInt("equip");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
