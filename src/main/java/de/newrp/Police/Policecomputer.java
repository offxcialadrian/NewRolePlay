package de.newrp.Police;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.House.House;
import de.newrp.Player.BeziehungCommand;
import de.newrp.Player.Mobile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Policecomputer implements CommandExecutor, Listener {

public static String PREFIX = "§8[§9Polizeicomputer§8] §9" + Messages.ARROW + " ";
public static HashMap<String, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getBeruf(p).equals(Beruf.Berufe.BUNDESNACHRICHTENDIENST)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/policecomputer [Name]");
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du musst im Dienst sein!");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§8[§9Polizeicomputer§8] §9" + tg.getName());
        inv.setItem(0, new ItemBuilder(Material.PLAYER_HEAD).setName("Personendaten").setLore("§8» §7Klicke um die Personendaten von " + Script.getName(tg) + " zu sehen.").build());
        inv.setItem(1, new ItemBuilder(Material.PAPER).setName("Lizenzen").setLore("§8» §7Klicke um die Lizenzen von " + Script.getName(tg) + " zu sehen.").build());
        inv.setItem(2, new ItemBuilder(Material.NETHER_STAR).setName("Gefährlichkeitsstufe").setLore("§8» §7Gefährlichkeitsstufe von " + Script.getName(tg) + "§8: §7" + getDangerLevel(tg)).build());
        if (Beruf.getBeruf(p) == Beruf.Berufe.BUNDESNACHRICHTENDIENST) inv.setItem(3, new ItemBuilder(Material.COMPASS).setName("Orten").setLore("§8» §7Klicke um " + Script.getName(tg) + " zu orten.").build());
        inv.setItem(Beruf.getBeruf(p) == Beruf.Berufe.BUNDESNACHRICHTENDIENST ? 4 : 3, new ItemBuilder(Material.OAK_SIGN).setName("Gesucht?").setLore("§8» " + (Fahndung.isFahnded(tg) ? "§cGesucht" : "§aNicht gesucht")).build());
        p.openInventory(inv);

        Beruf.Berufe.POLICE.sendMessage(PREFIX + Script.getName(p) + " fragt die Daten von " + Script.getName(tg) + " ab.");
        Beruf.Berufe.BUNDESNACHRICHTENDIENST.sendMessage(PREFIX + Script.getName(p) + " fragt die Daten von " + Script.getName(tg) + " ab.");

        return false;
    }

    public static int getDangerLevel(Player p) {
        return Script.getInt(p, "dangerlevel", "dangerlevel");
    }

    public static void setDangerLevel(Player p, int level) {
        if(getDangerLevel(p) == 0) Script.executeUpdate("INSERT INTO dangerlevel (nrp_id, dangerlevel) VALUES (" + Script.getNRPID(p) + ", " + level + ");");
        else Script.setInt(p, "dangerlevel", "dangerlevel", level);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getView().getTitle().startsWith("§8[§9Polizeicomputer§8] §9")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            if(e.getCurrentItem().getType() == Material.OAK_SIGN) {
                Player tg = Script.getPlayer(e.getView().getTitle().replace("§8[§9Polizeicomputer§8] §9", ""));
                String title = e.getCurrentItem().getItemMeta().getDisplayName();
                if(title.equals("Personendaten")) {
                    p.sendMessage(PREFIX + "Personendaten von " + Script.getName(tg));
                    p.sendMessage(PREFIX + "Name: " + Script.getName(tg));
                    p.sendMessage(PREFIX + " §8- §6Geburtsdatum: §c" + Script.getBirthday(Script.getNRPID(tg)) + " (" + Script.getAge(Script.getNRPID(tg)) + ")");
                    if (Script.getGender(tg).equals(Gender.MALE)) {
                        p.sendMessage(PREFIX + " §8- §6Geschlecht: §cMännlich");
                    } else if (Script.getGender(tg).equals(Gender.FEMALE)) {
                        p.sendMessage(PREFIX +" §8- §6Geschlecht: §cWeiblich");
                    }if(BeziehungCommand.isMarried(tg)) {
                        p.sendMessage(PREFIX + " §8- §6Verheiratet mit: §c" + BeziehungCommand.getPartner(tg).getName());
                    }
                    if (House.hasHouse(Script.getNRPID(tg))) {
                        StringBuilder houses = new StringBuilder();
                        for (House h : House.getHouses(Script.getNRPID(tg))) {
                            houses.append(", ").append(h.getID());
                        }
                        p.sendMessage(PREFIX + " §8- §6Wohnhaft:§6" + houses.substring(1));
                    } else {
                        p.sendMessage(PREFIX + " §8- §6Wohnhaft: §6Obdachlos");
                    }
                    p.closeInventory();
                    return;
                }

                if(title.equals("Lizenzen")) {
                    StringBuilder sb = new StringBuilder(PREFIX + "Lizenzen von " + Script.getName(tg) + "§7:\n");
                    Map<Licenses, Boolean> licenses = Licenses.getLicenses(Script.getNRPID(tg));

                    if (licenses.get(Licenses.PERSONALAUSWEIS)) {
                        sb.append(PREFIX + "  §7- §9Personalausweis§8: §bVorhanden\n");
                    } else {
                        sb.append(PREFIX + "  §7- §9Personalausweis§8: §bNicht vorhanden\n");
                    }

                    if (licenses.get(Licenses.FUEHRERSCHEIN)) {
                        sb.append(PREFIX + "  §7- §9Führerschein§8: §bVorhanden\n");
                    } else {
                        sb.append(PREFIX + "  §7- §9Führerschein§8: §bNicht vorhanden\n");
                    }

                    if (licenses.get(Licenses.WAFFENSCHEIN)) {
                        sb.append(PREFIX + "  §7- §9Waffenschein§8: §bVorhanden\n");
                    } else {
                        sb.append(PREFIX + "  §7- §9Waffenschein§8: §bNicht vorhanden\n");
                    }

                    if (licenses.get(Licenses.ANGELSCHEIN)) {
                        sb.append(PREFIX + "  §7- §9Angelschein§8: §bVorhanden\n");
                    } else {
                        sb.append(PREFIX + "  §7- §9Angelschein§8: §bNicht vorhanden\n");
                    }

                    if (licenses.get(Licenses.ERSTE_HILFE)) {
                        sb.append(PREFIX + "  §7- §9Erste Hilfe§8: §bVorhanden\n");
                    } else {
                        sb.append(PREFIX + "  §7- §9Erste Hilfe§8: §bNicht vorhanden\n");
                    }

                    p.sendMessage(sb.toString());
                    p.closeInventory();
                    return;
                }

                if(title.equals("Gefährlichkeitsstufe")) {
                    p.sendMessage(PREFIX + "Gefährlichkeitsstufe von " + Script.getName(tg) + "§8: §7" + getDangerLevel(tg));
                    p.sendMessage(Messages.INFO + "Du kannst die Gefährlichkeitsstufe mit §8/§6dangerlevel [Name] [Stufe] §rsetzen.");
                    p.closeInventory();
                    return;
                }

                if(title.equals("Orten")) {
                    if(!Mobile.hasPhone(tg) || !Mobile.mobileIsOn(tg)) {
                        p.sendMessage(Messages.ERROR + "Das Handy von " + Script.getName(tg) + " ist nicht eingeschaltet.");
                        return;
                    }

                    if(cooldown.containsKey(tg.getName()) && cooldown.get(tg.getName())>System.currentTimeMillis()) {
                        p.sendMessage(Messages.ERROR + "Du kannst " + Script.getName(tg) + " erst in " + Script.getRemainingTime(cooldown.get(tg.getName())) + " orten.");
                        return;
                    }

                    cooldown.put(tg.getName(), (System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(10)));
                    p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " geortet.");
                    Navi navi = Navi.getNextNaviLocation(tg.getLocation());
                    Script.sendClickableMessage(p, PREFIX  + Script.getName(tg) + " befindet sich bei " + navi.getName(), "/navi " + navi.getName(), "Klicke um dich zum nächsten Punkt zu navigieren.");
                    p.closeInventory();
                    return;
                }

                if(title.equals("Gesucht?")) {
                    p.sendMessage(PREFIX + Script.getName(tg) + " ist " + (Fahndung.isFahnded(tg) ? "§cGesucht!" : "§anicht gesucht"));
                    p.closeInventory();
                    return;
                }

            }
        }
    }

}
