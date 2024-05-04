package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
import de.newrp.House.House;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Personalausweis implements CommandExecutor, Listener {
    public static final HashMap<Player, Long> cooldown = new HashMap<>();
    public static HashMap<Player, Gender> cache = new HashMap<>();
    final List<String> save_close = new ArrayList<>();
    public static String PREFIX = "§8[§6Personalausweis§8] §6» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        int id = Script.getNRPID(p);
        if (Licenses.PERSONALAUSWEIS.hasLicense(id)) {
            Achievement.PERSONALAUSWEIS.grant(p);
            if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
                p.sendMessage(PREFIX + "Deine Personalien:");
                p.sendMessage(PREFIX + " §8- §6Name: " + Script.getName(p));
                p.sendMessage(PREFIX + " §8- §6Geburtsdatum: §c" + Script.getBirthday(id) + " (" + Script.getAge(Script.getNRPID(p)) + ")");
                if (Script.getGender(p).equals(Gender.MALE)) {
                    p.sendMessage(PREFIX + " §8- §6Geschlecht: §cMännlich");
                } else if (Script.getGender(p).equals(Gender.FEMALE)) {
                    p.sendMessage(PREFIX +" §8- §6Geschlecht: §cWeiblich");
                }
                if(BeziehungCommand.isMarried(p)) {
                    p.sendMessage(PREFIX + " §8- §6Verheiratet mit: §c" + BeziehungCommand.getPartner(p).getName());
                }
                if (House.hasHouse(id)) {
                    StringBuilder houses = new StringBuilder();
                    for (House h : House.getHouses(id)) {
                        houses.append(", ").append(h.getID());
                    }
                    p.sendMessage(PREFIX + " §8- §6Wohnhaft:§6" + houses.substring(1));
                } else {
                    p.sendMessage(PREFIX + " §8- §6Wohnhaft: §6Obdachlos");
                }
                return true;
            }
            int i = Token.PERSONALAUSWEIS.get(id);
            if (i >= 1) {
                if (args.length == 0) {
                    if(p.getLocation().distance(new Location(Script.WORLD, 554, 70, 984)) > 4) {
                        p.sendMessage(PREFIX + "Deine Personalien:");
                        p.sendMessage(PREFIX + " §8- §6Name: " + Script.getName(p));
                        p.sendMessage(PREFIX + " §8- §6Geburtsdatum: §c" + Script.getBirthday(id) + " (" + Script.getAge(Script.getNRPID(p)) + ")");
                        if (Script.getGender(p).equals(Gender.MALE)) {
                            p.sendMessage(PREFIX + " §8- §6Geschlecht: §cMännlich");
                        } else if (Script.getGender(p).equals(Gender.FEMALE)) {
                            p.sendMessage(PREFIX + " §8- §6Geschlecht: §cWeiblich");
                        }
                        if (House.hasHouse(id)) {
                            StringBuilder houses = new StringBuilder();
                            for (House h : House.getHouses(id)) {
                                houses.append(", ").append(h.getID());
                            }
                            p.sendMessage(PREFIX + " §8- §6Wohnhaft:§6" + houses.substring(1));
                        } else {
                            p.sendMessage(PREFIX + " §8- §6Wohnhaft: §6Obdachlos");
                        }
                        return true;
                    }
                    p.sendMessage(PREFIX + "Du hast bereits einen Personalausweis.");
                    p.sendMessage(Messages.INFO + "Mit /personalausweis change kannst du deinen Personalausweis ändern.");
                } else {
                    if (Script.isInRange(p.getLocation(), new Location(p.getWorld(), 554, 70, 984), 4)) {
                        Inventory inv = p.getServer().createInventory(null, 9, "§3Personalausweis");
                        inv.setItem(3, Script.setName(new ItemStack(Material.INK_SAC, 1, (byte) 12), "§bMännlich"));
                        inv.setItem(5, Script.setName(new ItemStack(Material.INK_SAC, 1, (byte) 13), "§cWeiblich"));
                        cache.remove(p);
                        p.openInventory(inv);
                    } else {
                        p.sendMessage("§7Du kannst deinen Personalausweis nur in der Stadthalle ummelden.");
                    }
                }
            } else {
                p.sendMessage(Messages.ERROR + "Du hast bereits einen Personalausweis.");
                p.sendMessage(Messages.ERROR + "Du hast keine ChangeToken für eine Personalausweisänderung.");
            }
        } else if (!Script.isInRange(p.getLocation(),new Location(Script.WORLD, 554, 70, 984), 4)) {
            p.sendMessage("§7Du kannst deinen Personalausweis nur in der Stadthalle anmelden.");
        } else {
            long time = System.currentTimeMillis();
            Long lastUsage = cooldown.get(p);
            if (cooldown.containsKey(p)) {
                if (lastUsage + 300 * 1000 > time) {
                    p.sendMessage(PREFIX + "Ihr Personalausweis ist noch nicht fertig.");
                    p.sendMessage(Messages.INFO + "Mit /sperrinfo siehst du deine Wartezeit.");
                    return false;
                } else {
                    Licenses.PERSONALAUSWEIS.grant(id);
                    Achievement.PERSONALAUSWEIS.grant(p);
                    p.sendMessage(PREFIX + "Dein Personalausweis ist fertig.");
                }
            } else {
                Inventory inv = p.getServer().createInventory(null, 9, "§3Personalausweis");
                inv.setItem(3, Script.setName(new ItemStack(Material.INK_SAC, 1, (byte) 12), "§bMännlich"));
                inv.setItem(5, Script.setName(new ItemStack(Material.INK_SAC, 1, (byte) 13), "§cWeiblich"));
                p.openInventory(inv);
            }
        }
        return true;
    }

    @EventHandler
    public void onInteract(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§3Personalausweis")) {
            e.setCancelled(true);
            ItemStack is = e.getCurrentItem();
            if (is != null && !is.getType().equals(Material.AIR) && is.hasItemMeta()) {
                Player p = (Player) e.getWhoClicked();
                int id = Script.getNRPID(p);
                if (is.getType().equals(Material.INK_SAC)) {
                    Debug.debug("clicked " + is.getItemMeta().getDisplayName());
                    if (is.getItemMeta().getDisplayName().equals("§bMännlich")) {
                        if (!Licenses.PERSONALAUSWEIS.hasLicense(id)) {
                            cache.put(p, Gender.MALE);
                            Debug.debug("put MALE into cache");
                        } else {
                            Script.setGender(p, Gender.MALE);
                            Debug.debug("put MALE into cache");
                        }
                        openAgeMenu(p);
                    } else if (is.getItemMeta().getDisplayName().equals("§cWeiblich")) {
                        if (Licenses.PERSONALAUSWEIS.hasLicense(id)) {
                            cache.put(p, Gender.FEMALE);
                            Debug.debug("put FEMALE into cache");
                        } else {
                            Script.setGender(p, Gender.FEMALE);
                            Debug.debug("put MALE into cache");
                        }
                        openAgeMenu(p);
                    }
                } else if (is.getType().equals(Material.PAPER)) {
                    int value = (e.getClick().equals(ClickType.LEFT) ? 1 : -1);

                    String s1 = ChatColor.stripColor(e.getInventory().getItem(1).getItemMeta().getDisplayName());
                    String s2 = ChatColor.stripColor(e.getInventory().getItem(3).getItemMeta().getDisplayName());
                    String s3 = ChatColor.stripColor(e.getInventory().getItem(5).getItemMeta().getDisplayName());

                    int day = Integer.parseInt(s1.substring(0, (s1.length() - 1)));
                    int month = Script.getMonth(s2);
                    int year = Integer.parseInt(s3);

                    if (e.getSlot() == 1) {
                        //Tag
                        if (day + value > getMaxDaysInMonth(month) || day + value < 1) {
                            day = 1;
                        } else {
                            day += value;
                        }
                    } else if (e.getSlot() == 3) {
                        //Monat
                        if (month + value > 12 || month + value < 1) {
                            month = 1;
                        } else {
                            month += value;
                        }
                        if (day > getMaxDaysInMonth(month)) {
                            day = 1;
                        }
                    } else if (e.getSlot() == 5) {
                        //Jahr
                        if (year + value > 2010 || year + value < 1960) {
                            year -= value;
                        } else {
                            year += value;
                        }
                    }
                    updateGUI(e.getInventory(), p, day, month, year);
                } else if (is.getType().equals(Material.STONE_BUTTON)) {
                    String s1 = ChatColor.stripColor(e.getInventory().getItem(1).getItemMeta().getDisplayName());
                    String s2 = ChatColor.stripColor(e.getInventory().getItem(3).getItemMeta().getDisplayName());
                    String s3 = ChatColor.stripColor(e.getInventory().getItem(5).getItemMeta().getDisplayName());

                    int day = Integer.parseInt(s1.substring(0, (s1.length() - 1)));
                    int month = Script.getMonth(s2);
                    int year = Integer.parseInt(s3);

                    Script.setBirthDay(p, day, month, year, Calendar.getInstance().get(Calendar.YEAR));
                    LocalDate today = LocalDate.now();
                    LocalDate birthday = LocalDate.of(year, Month.of(month), day);

                    Period period = Period.between(birthday, today);
                    int age = period.getYears();
                    if(age < 14) {
                        p.sendMessage(Messages.ERROR + "Du kannst kein nicht strafmündiger Bürger sein.");
                        return;
                    }

                    if (!Licenses.PERSONALAUSWEIS.hasLicense(id)) {
                        save_close.add(p.getName());
                        Gender g = Script.getGender(p);

                        p.sendMessage(PREFIX + "Alles klar. " + (g.equals(Gender.MALE) ? "Herr " : "Frau ") + p.getName() + ", Ihr Personalausweis ist in 5 Minuten fertig.");
                        p.sendMessage(PREFIX + "Kommen Sie dann wieder und geben Sie erneut §7/§6personalausweis§6 ein.");
                        p.sendMessage(Messages.INFO + "Mit /sperrinfo kannst du sehen, wie lange du noch warten musst.");
                        Sperre.PERSONALAUSWEIS.setSperre(id, 6);
                        Personalausweis.cooldown.put(p, System.currentTimeMillis());
                        e.getView().close();
                        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                            if (p.isOnline()) {
                                p.sendMessage(PREFIX + "Sie können Ihren Personalausweis nun in der Stadthalle abholen.");
                            }
                        }, 300 * 20L);
                    } else {
                        Gender g = Gender.MALE;
                        if (cache.containsKey(p)) {
                            g = cache.remove(p);
                        }

                        Script.setGender(p, g);
                        Script.setBirthDay(p, day, month, year, Calendar.getInstance().get(Calendar.YEAR));
                        p.sendMessage(PREFIX + "Dein Personalausweis wurde angepasst.");
                        Token.PERSONALAUSWEIS.remove(Script.getNRPID(p), 1);
                        e.getView().close();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals("§3Personalausweis")) {
            if (save_close.contains(e.getPlayer().getName())) {
                save_close.remove(e.getPlayer().getName());
                return;
            }
        }
    }

    public int getMaxDaysInMonth(int month) {
        return YearMonth.of(Calendar.getInstance().get(Calendar.YEAR), month).lengthOfMonth();
    }

    public void updateGUI(Inventory inv, Player p, int day, int month, int year) {
        p.setItemOnCursor(new ItemStack(Material.AIR));
        inv.setItem(1, Script.setNameAndLore(Material.PAPER, "§6" + day + ".", "§bLinks: +1 Tag §7|§b Rechts: -1 Tag"));
        inv.setItem(3, Script.setNameAndLore(Material.PAPER, "§6" + Script.getMonth(month, true) + "", "§bLinks: +1 Monat §7|§b Rechts: -1 Monat"));
        inv.setItem(5, Script.setNameAndLore(Material.PAPER, "§6" + year, "§bLinks: +1 Jahr §7|§b Rechts: -1 Jahr"));
        inv.setItem(8, Script.setNameAndLore(Material.STONE_BUTTON, "§aBestätigen", "§6" + day + "." + Script.getMonth(month, true) + "." + year + " §7(" + (Script.getGender(p).equals(Gender.MALE) ? "§bMännlich" : "§cWeiblich") + "§7)"));
    }

    public void openAgeMenu(Player p) {
        Inventory inv = p.getServer().createInventory(null, 9, "§3Personalausweis");
        inv.setItem(1, Script.setNameAndLore(Material.PAPER, "§61.", "§bLinks: +1 §7|§b Rechts: -1"));
        inv.setItem(3, Script.setNameAndLore(Material.PAPER, "§6Januar", "§bLinks: +1 §7|§b Rechts: -1"));
        inv.setItem(5, Script.setNameAndLore(Material.PAPER, "§62004", "§bLinks: +1 §7|§b Rechts: -1"));
        if (cache.containsKey(p)) {
            inv.setItem(8, Script.setNameAndLore(Material.STONE_BUTTON, "§aBestätigen", "§61.Januar.2004 §7(" + (cache.get(p).equals(Gender.MALE) ? "§bMännlich" : "§cWeiblich") + "§7)"));
        } else {
            inv.setItem(8, Script.setNameAndLore(Material.STONE_BUTTON, "§aBestätigen", "§61.Januar.2004 §7(" + (Script.getGender(p).equals(Gender.MALE) ? "§bMännlich" : "§cWeiblich") + "§7)"));
        }
        p.openInventory(inv);
    }

}
