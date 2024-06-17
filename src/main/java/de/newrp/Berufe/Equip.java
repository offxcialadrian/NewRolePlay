package de.newrp.Berufe;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.Notifications;
import de.newrp.Government.Stadtkasse;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import de.newrp.Police.StartTransport;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        PISTOLE(1, "Glory", new ItemBuilder(Material.IRON_HORSE_ARMOR).setName("§7Glory").build(), 300, 0, null, new Beruf.Berufe[]{Beruf.Berufe.POLICE, Beruf.Berufe.BUNDESKRIMINALAMT}, false),
        SCHUTZWESTE(2, "Schutzweste", Script.kevlar(1), 500, 0, null, new Beruf.Berufe[]{Beruf.Berufe.POLICE, Beruf.Berufe.BUNDESKRIMINALAMT}, false),
        HANDSCHELLEN(3, "Handschellen", Script.setName(new ItemStack(Material.LEAD, 2), "§7Handschellen"), 10, 0, null, new Beruf.Berufe[]{Beruf.Berufe.POLICE, Beruf.Berufe.BUNDESKRIMINALAMT}, true),
        TAZER(4, "Tazer", Script.tazer(), 100, 0, null, new Beruf.Berufe[]{Beruf.Berufe.POLICE, Beruf.Berufe.BUNDESKRIMINALAMT}, true),
        DONUT(5, "Donut", new ItemBuilder(Material.COOKIE).setAmount(16).setName("§7Donut").build(), 1, 0, null, new Beruf.Berufe[]{Beruf.Berufe.POLICE, Beruf.Berufe.BUNDESKRIMINALAMT}, false),
        MP7(6, "Striker", new ItemBuilder(Material.GOLDEN_HORSE_ARMOR).setName("§7Striker").build(), 600, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE, Beruf.Berufe.BUNDESKRIMINALAMT}, true),
        EINSATZSCHILD(7, "Einsatzschild", Script.einsatzschild(1), 1000, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, true),
        EINSATZSCHILD_2(8, "Schweres Einsatzschild", Script.einsatzschild(2), 1300, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, true),
        RAUCHGRANATE(9, "Rauchgranate", Script.rauchgranate(), 200, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, true),
        FLASHBANG(10, "Flashbang", Script.flashbang(), 150, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, true),
        FALLSCHIRM(11, "Fallschirm", Script.fallschirm(), 300, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, false),
        ZEITUNG(12, "Buch und Stift", Script.setName(new ItemStack(Material.WRITABLE_BOOK), "§7Buch und Stift"), 20, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.JOURNALIST}, new Beruf.Berufe[]{Beruf.Berufe.NEWS}, false),
        VERBAND(13, "Verband", new ItemBuilder(Material.PAPER).setName("§7Verband").setAmount(5).build(), 50, 0, null, new Beruf.Berufe[]{Beruf.Berufe.RETTUNGSDIENST}, true),
        GIPS(14, "Gips", Script.setName(new ItemStack(Material.PAPER), "§7Gips"), 50, 0, null, new Beruf.Berufe[]{Beruf.Berufe.RETTUNGSDIENST}, true),
        SCHWERE_SCHUTZWESTE(15, "Schwere Schutzweste", Script.kevlar(2), 1200, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, true),
        SPRITZE(16, "Spritze", Script.setName(new ItemStack(Material.END_ROD), "§7Spritze"), 10, 0, null, new Beruf.Berufe[]{Beruf.Berufe.RETTUNGSDIENST}, true),
        HUSTEN_IMPFUNG(17, "Husten Impfung", Script.setName(new ItemStack(Material.END_ROD), "§7Husten Impfung"), 50, 0, null, new Beruf.Berufe[]{Beruf.Berufe.RETTUNGSDIENST}, true),
        KAFFEE(18, "Kaffee", Script.setName(new ItemStack(Material.POTION), "§7Kaffee"), 2, 0, null, new Beruf.Berufe[]{Beruf.Berufe.NEWS}, false),
        BROT(19, "Brot", new ItemBuilder(Material.BREAD).setAmount(16).setName("§7Brot").build(), 3, 0, null, new Beruf.Berufe[]{Beruf.Berufe.RETTUNGSDIENST}, false),
        KEKSE(20, "Keks", new ItemBuilder(Material.COOKIE).setAmount(16).setName("§7Keks").build(), 1, 0, null, new Beruf.Berufe[]{Beruf.Berufe.NEWS}, false),
        SNIPER(21, Weapon.SNIPER.getName(), Weapon.SNIPER.getWeapon(), 2900, 30, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, true),
        DROHNE_COPS(22, "Drohne [Polizei]", new ItemBuilder(Material.WITHER_SKELETON_SKULL).setName("§7Drohne [Polizei]").build(), 1000, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG}, new Beruf.Berufe[]{Beruf.Berufe.POLICE, Beruf.Berufe.BUNDESKRIMINALAMT}, true),
        DROHNE_NEWS(23, "Drohne [News]", new ItemBuilder(Material.WITHER_SKELETON_SKULL).setName("§7Drohne [News]").build(), 500, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.CHEFREDAKTION}, new Beruf.Berufe[]{Beruf.Berufe.NEWS}, true),
        DROHNE_RETTUNGSDIENST(24, "Drohne [Rettungsdienst]", new ItemBuilder(Material.WITHER_SKELETON_SKULL).setName("§7Drohne [Rettungsdienst]").build(), 1000, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.NOTFALLMEDIZIN, Abteilung.Abteilungen.OBERARZT}, new Beruf.Berufe[]{Beruf.Berufe.RETTUNGSDIENST}, true),
        MUNITION_PISTOLE(25, Weapon.PISTOLE.getAmmoType().getName(), new ItemBuilder(Material.ARROW).setName(Weapon.PISTOLE.getAmmoType().getName()).setAmount(Weapon.PISTOLE.getMagazineSize()).build(), 10, 0, null, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, true),
        MUNITION_MP7(26, Weapon.MP7.getAmmoType().getName(), new ItemBuilder(Material.ARROW).setName(Weapon.MP7.getAmmoType().getName()).setAmount(Weapon.MP7.getMagazineSize()).build(), 15, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.SEK}, new Beruf.Berufe[]{Beruf.Berufe.POLICE}, true),
        //BROT_2(27, "Brot", new ItemBuilder(Material.BREAD).setAmount(16).setName("§7Brot").build(), 3, 0, null, Beruf.Berufe.GOVERNMENT, false),
        TRINKWASSER(28, "Trinkwasser", new ItemBuilder(Material.POTION).setName("§7Trinkwasser").build(), 2, 0, null, new Beruf.Berufe[]{Beruf.Berufe.RETTUNGSDIENST}, false),
        //TRINKWASSER_MEDIC(29, "Trinkwasser", new ItemBuilder(Material.POTION).setName("§7Trinkwasser").build(), 2, 0, null, Beruf.Berufe.RETTUNGSDIENST, false),
        KEVLAR(29, "Kevlar", Script.kevlar(1), 2900, 0, null, new Beruf.Berufe[]{Beruf.Berufe.GOVERNMENT}, false),
        APPLE(30, "Apfel", new ItemBuilder(Material.APPLE).setAmount(16).setName("§7Apfel").build(), 1, 0, null, new Beruf.Berufe[]{Beruf.Berufe.GOVERNMENT}, false),
        LATTE_MACCHIATO(31, "Latte Macchiato", new ItemBuilder(Material.FLOWER_POT).setName("§rLatte Macchiato").build(), 2, 0, null, new Beruf.Berufe[]{Beruf.Berufe.GOVERNMENT}, false),
        FEUERLOESCHER(32, "Feuerlöscher", Script.feuerloescher(Script.feuerloescher(), 800), 300, 0, new Abteilung.Abteilungen[]{Abteilung.Abteilungen.FEUERWEHR, Abteilung.Abteilungen.OBERARZT}, new Beruf.Berufe[]{Beruf.Berufe.RETTUNGSDIENST}, false);

        private String name;
        private int id;
        private ItemStack item;
        private int cost;
        private int ammo;
        private Abteilung.Abteilungen[] abteilung;
        private Beruf.Berufe[] beruf;
        private boolean remove;


        Stuff(int id, String name, ItemStack item, int cost, int ammo, Abteilung.Abteilungen[] abteilung, Beruf.Berufe[] beruf, boolean remove) {
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

        public int getPrice(int beruf) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equip WHERE id=" + beruf + " AND equip=" + this.id)) {
                if (rs.next()) return rs.getInt("price");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public void setPrice(int beruf, int price) {
            if (getPrice(beruf) == 0) {
                try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM equip WHERE id=" + beruf + " AND equip=" + this.id)) {
                    if (!rs.next()) Script.executeUpdate("INSERT INTO equip (id, equip, price) VALUES (" + beruf + ", " + this.id + ", 0)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            Script.executeUpdate("UPDATE equip SET price=" + price + " WHERE id=" + beruf + " AND equip=" + this.id);
        }

        public Abteilung.Abteilungen[] getAbteilung() {
            return abteilung;
        }

        public Beruf.Berufe[] getBeruf() {
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
                if (stuff.getName().equalsIgnoreCase(name.replaceAll("§7", ""))) {
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

        if (!Beruf.hasBeruf(p) && !Organisation.hasOrganisation(p)) {
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

        if (Beruf.hasBeruf(p)) {
            Beruf.Berufe beruf = Beruf.getBeruf(p);
            Inventory inv = Bukkit.createInventory(null, (beruf == Beruf.Berufe.POLICE && (Beruf.getAbteilung(p) == Abteilung.Abteilungen.SEK || Beruf.getAbteilung(p) == Abteilung.Abteilungen.ABTEILUNGSLEITUNG || Beruf.isLeader(p, true)) ? 18 : 9), "§8» §7Equip");

            if (!beruf.hasEquip()) {
                p.sendMessage(Messages.ERROR + "Dein Beruf hat kein Equip.");
                return true;
            }

            if (beruf.hasDuty() && !Duty.isInDuty(p)) {
                p.sendMessage(Messages.ERROR + "Du musst im Dienst sein.");
                return true;
            }

            if (p.getLocation().distance(beruf.getEquipLoc()) > 7) {
                p.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
                return true;
            }

            for (Stuff stuff : Stuff.values()) {
                if (stuff.getBeruf() != null) {
                    for (Beruf.Berufe b : stuff.getBeruf()) {
                        if (b == beruf) {
                            if (stuff.getAbteilung() != null && !Beruf.isLeader(p, true)) {
                                boolean hasAbteilung = false;
                                for (Abteilung.Abteilungen abteilung : stuff.getAbteilung()) {
                                    if (abteilung == Beruf.getAbteilung(p)) {
                                        hasAbteilung = true;
                                    }
                                }
                                if (!hasAbteilung) {
                                    continue;
                                }
                            }
                            inv.addItem(stuff.getItem().clone());
                        }
                    }
                }
            }

            p.openInventory(inv);
        } else if (Organisation.hasOrganisation(p)) {
            Organisation orga = Organisation.getOrganisation(p);
            Inventory inv = Bukkit.createInventory(p, 9, "§8» §7Equip");

            if (p.getLocation().distance(orga.getEquipLoc()) > 7) {
                p.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
                return true;
            }

            for (de.newrp.Organisationen.Stuff stuff : de.newrp.Organisationen.Stuff.values()) {
                if (orga.getLevel() >= stuff.getLevel()) {
                    inv.addItem(Script.setNameAndLore(stuff.getItem().clone(), "§7" + stuff.getItem().clone().getItemMeta().getDisplayName(), "§c" + stuff.getPrice(orga.getID()) + "€"));
                }
            }

            p.openInventory(inv);
        }
        return true;
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
            if (Beruf.hasBeruf(p)) {
                Beruf.Berufe beruf = Beruf.getBeruf(p);
                Stuff stuff = Stuff.getStuff(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                if (stuff == null) return;
                if (stuff.getBeruf() != null) {
                    boolean hasBeruf = false;
                    for (Beruf.Berufe b : stuff.getBeruf()) {
                        if (b == Beruf.getBeruf(p)) {
                            hasBeruf = true;
                        }
                    }
                    if (!hasBeruf) {
                        p.sendMessage(Messages.ERROR + "Du kannst dir nur Items von deinem Beruf ausrüsten.");
                        return;
                    }
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

                if (!beruf.hasKasse()) {
                    if (Stadtkasse.getStadtkasse() < stuff.getCost() - stuff.getPrice(beruf.getID())) {
                        p.sendMessage(Messages.ERROR + "Die Stadtkasse hat nicht genug Geld.");
                        return;
                    }
                }
                if ((Beruf.getBeruf(p) == Beruf.Berufe.POLICE || Beruf.getBeruf(p) == Beruf.Berufe.BUNDESKRIMINALAMT) && getAvailableEquip() <= 0) {
                    p.sendMessage(Messages.ERROR + "Es sind keine weiteren Equip-Gegenstände verfügbar.");
                    return;
                }

                if (Beruf.getBeruf(p) == Beruf.Berufe.NEWS) {
                    if (Beruf.getBeruf(p).getKasse() < stuff.getCost() - stuff.getPrice(beruf.getID())) {
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

                if (stuff == Stuff.EINSATZSCHILD || stuff == Stuff.EINSATZSCHILD_2) {
                    if (p.getInventory().contains(Material.SHIELD)) {
                        p.sendMessage(Messages.ERROR + "Du kannst nur ein Einsatzschild tragen.");
                        return;
                    }
                }

                if (Script.getMoney(p, PaymentType.BANK) < stuff.getPrice(beruf.getID())) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    p.closeInventory();
                    return;
                } else {
                    Script.removeMoney(p, PaymentType.BANK, stuff.getPrice(beruf.getID()));
                }
                if (beruf.hasKasse()) {
                    beruf.removeKasse(stuff.getCost() - stuff.getPrice(beruf.getID()));
                } else {
                    Stadtkasse.removeStadtkasse(stuff.getCost() - stuff.getPrice(beruf.getID()), "Equip " + stuff.getName() + " von " + p.getName() + " für " + (stuff.getCost() - stuff.getPrice(beruf.getID())) + "€");
                }
                Equiplog.addToEquipLog(p, stuff.getID());

                if (beruf == Beruf.Berufe.POLICE || beruf == Beruf.Berufe.BUNDESKRIMINALAMT) {
                    Script.executeUpdate("UPDATE city SET equip = equip - 1");
                }

                if (beruf != Beruf.Berufe.NEWS) {
                    if (Stadtkasse.getStadtkasse() < stuff.getCost()) {
                        if (stuff != Stuff.HANDSCHELLEN && stuff != Stuff.PISTOLE) {
                            p.sendMessage(Messages.ERROR + "Die Stadtkasse hat nicht genug Geld.");
                            p.closeInventory();
                            return;
                        }
                    }
                }

                if (stuff == Stuff.MUNITION_MP7 || stuff == Stuff.MUNITION_PISTOLE) {
                    Weapon w = null;
                    if (stuff == Stuff.MUNITION_MP7) {
                        w = Weapon.MP7;
                    } else {
                        w = Weapon.PISTOLE;
                    }

                    for (ItemStack item : p.getInventory()) {
                        if (item.getType() == w.getWeapon().getType()) {
                            int ammo = Waffen.getAmmo(item);
                            int total = Waffen.getAmmoTotal(item);
                            p.getInventory().remove(item);
                            p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), ammo, total + w.getMagazineSize()));
                            p.sendMessage(PREFIX + "Du hast dich mit " + stuff.getName() + "-Munition ausgerüstet.");
                            beruf.sendLeaderMessage("§8[§e" + beruf.getName() + "§8] §e» " + Script.getName(p) + " hat sich mit " + stuff.getName() + "-Munition ausgerüstet.");
                            Log.LOW.write(p, "hat sich mit " + w.getName() + "-Munition ausgerüstet.");
                            Notifications.sendMessage(Notifications.NotificationType.DEBUG, "§a" + Script.getName(p) + " hat sich mit " + w.getName() + "-Munition ausgerüstet.");
                            return;
                        }
                    }
                }

                for (Weapon w : Weapon.values()) {
                    if (w.getName().equalsIgnoreCase(stuff.getName())) {
                        p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), w.getMagazineSize(), 400));
                        p.sendMessage(PREFIX + "Du hast dich mit einer " + w.getName() + " ausgerüstet.");
                        beruf.sendLeaderMessage("§8[§e" + beruf.getName() + "§8] §e» " + Script.getName(p) + " hat sich mit einer " + w.getName() + " ausgerüstet.");
                        Log.LOW.write(p, "hat sich mit einer " + w.getName() + " ausgerüstet.");
                        Notifications.sendMessage(Notifications.NotificationType.DEBUG, "§a" + Script.getName(p) + " hat sich mit einer " + w.getName() + " ausgerüstet.");
                        p.closeInventory();
                        return;
                    }
                }

                ItemStack is = stuff.getItem().clone();
                p.getInventory().addItem(is);
                p.sendMessage(PREFIX + "Du hast dich mit " + stuff.getName() + " ausgerüstet.");
                beruf.sendLeaderMessage("§8[§e" + beruf.getName() + "§8] §e» " + Script.getName(p) + " hat sich mit " + stuff.getName() + " ausgerüstet.");
                Log.LOW.write(p, "hat sich mit " + stuff.getName() + " ausgerüstet.");
                Notifications.sendMessage(Notifications.NotificationType.DEBUG, "§a" + Script.getName(p) + " hat sich mit " + stuff.getName() + " ausgerüstet.");
            } else if (Organisation.hasOrganisation(p)) {
                Organisation orga = Organisation.getOrganisation(p);
                de.newrp.Organisationen.Stuff stuff = de.newrp.Organisationen.Stuff.getStuff(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
                if (stuff == null) return;
                if (stuff == de.newrp.Organisationen.Stuff.SCHUTZWESTE) {
                    if (p.getInventory().contains(Material.LEATHER_CHESTPLATE)) {
                        p.sendMessage(Messages.ERROR + "Du kannst nur eine Schutzweste tragen.");
                        return;
                    }
                }

                if (orga.getKasse() < stuff.getCost() - stuff.getPrice(orga.getID())) {
                    p.sendMessage(Messages.ERROR + "Die Kasse hat nicht genug Geld.");
                    p.closeInventory();
                    return;
                }

                if (Script.getMoney(p, PaymentType.BANK) < stuff.getPrice(orga.getID())) {
                    p.sendMessage(Messages.ERROR + "Du hast nicht genug Geld.");
                    p.closeInventory();
                    return;
                } else {
                    Script.removeMoney(p, PaymentType.BANK, stuff.getPrice(orga.getID()));
                }
                orga.removeKasse(stuff.getCost() - stuff.getPrice(orga.getID()));
                Equiplog.addToEquipLog(p, stuff.getId());


                if (stuff.getItem().getType() == Material.ARROW) {
                    Weapon w = Weapon.AK47;

                    for (ItemStack item : p.getInventory()) {
                        if (item.getType() == w.getWeapon().getType()) {
                            int ammo = Waffen.getAmmo(item);
                            int total = Waffen.getAmmoTotal(item);
                            p.getInventory().remove(item);
                            p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), ammo, total + w.getMagazineSize()));
                            p.sendMessage(PREFIX + "Du hast dich mit " + stuff.getName() + "-Munition ausgerüstet.");
                            orga.sendLeaderMessage("§8[§e" + orga.getName() + "§8] §e» " + Script.getName(p) + " hat sich mit " + stuff.getName() + "-Munition ausgerüstet.");
                            Log.LOW.write(p, "hat sich mit " + w.getName() + "-Munition ausgerüstet.");
                            Notifications.sendMessage(Notifications.NotificationType.DEBUG, "§a" + Script.getName(p) + " hat sich mit " + w.getName() + "-Munition ausgerüstet.");
                            return;
                        }
                    }
                }

                for (Weapon w : Weapon.values()) {
                    if (w.getName().equalsIgnoreCase(stuff.getName())) {
                        p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), w.getMagazineSize(), 400));
                        p.sendMessage(PREFIX + "Du hast dich mit einer " + w.getName() + " ausgerüstet.");
                        orga.sendLeaderMessage("§8[§e" + orga.getName() + "§8] §e» " + Script.getName(p) + " hat sich mit einer " + w.getName() + " ausgerüstet.");
                        Log.LOW.write(p, "hat sich mit einer " + w.getName() + " ausgerüstet.");
                        Notifications.sendMessage(Notifications.NotificationType.DEBUG, "§a" + Script.getName(p) + " hat sich mit einer " + w.getName() + " ausgerüstet.");
                        p.closeInventory();
                        return;
                    }
                }

                ItemStack is = stuff.getItem().clone();
                p.getInventory().addItem(is);
                p.sendMessage(PREFIX + "Du hast dich mit " + stuff.getName() + " ausgerüstet.");
                orga.sendLeaderMessage("§8[§e" + orga.getName() + "§8] §e» " + Script.getName(p) + " hat sich mit " + stuff.getName() + " ausgerüstet.");
                Log.LOW.write(p, "hat sich mit " + stuff.getName() + " ausgerüstet.");
                Notifications.sendMessage(Notifications.NotificationType.DEBUG, "§a" + Script.getName(p) + " hat sich mit " + stuff.getName() + " ausgerüstet.");
            }

            p.closeInventory();
        }
    }

    public static void removeEquip(Player p) {
        for (Stuff stuff : Stuff.values()) {
            if (stuff.removeOnUninvite()) {
                p.getInventory().remove(stuff.getItem());
            }
        }
    }

    public static int getAvailableEquip() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT equip FROM city")) {
            if (rs.next()) return rs.getInt("equip");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
