package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Particle;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlantageCommand implements CommandExecutor, Listener, TabCompleter {
    public static void openGUI(Player p, Plantage plant) {
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((plant.getTime() - System.currentTimeMillis()));
        StringBuilder sb = new StringBuilder(" §8»§6 Reif in ");
        if (minutes > 60) {
            int hour = minutes / 60;
            minutes = minutes - (hour * 60);
            sb.append("§e§l").append(hour).append(" §r§6Stunden und ").append("§e§l").append(minutes).append(" §r§6Minuten.");
        } else {
            sb.append("§e§l").append(minutes).append(" §r§6Minuten.");
        }

        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

        Date last_fertilize = new Date(plant.getLastFertilize());
        Date last_water = new Date(plant.getLastWater());

        Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, "§aPlantage");
        inv.setItem(0, Script.setNameAndLore(new ItemStack(Material.CHORUS_PLANT, 1, (byte) 3), "§aPlantage", " §8»§6 Typ§7:§6 " + plant.getType().getItem().getName()));
        inv.setItem(1, Script.setNameAndLore(Material.CLOCK, "§9Zeit", sb.toString()));
        inv.setItem(2, Script.setNameAndLore(Material.CHEST, "§eErtrag", " §8»§6 " + plant.getErtrag() + "g"));
        inv.setItem(3, Script.setNameAndLore(new ItemStack(Material.INK_SAC, 1, (byte) 15), "§7Dünger", " §8»§6 Gedüngt§7:§6 " + (plant.getFertilize() ? "Ja" : "Nein"), " §8»§6 Letztes Mal gedüngt§7:§6 " + (plant.getLastFertilize() == 0 ? "Nie" : df.format(last_fertilize))));
        inv.setItem(4, Script.setNameAndLore(Material.WATER_BUCKET, "§9Wasser", " §8»§6 Gewässert§7:§6 " + (plant.getWater() ? "Ja" : "Nein"), " §8»§6 Letztes Mal gewässert§7:§6 " + (plant.getLastWater() == 0 ? "Nie" : df.format(last_water))));
        p.openInventory(inv);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        Organisation f = Organisation.getOrganisation(p);
        if(f == null) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }
            if (args.length == 0) {
                Plantage plant = Plantage.getNextPlantage(p, f);
                if (plant != null) {
                    openGUI(p, plant);
                } else {
                    p.sendMessage(Plantage.PREFIX + "Du bist nicht in der Nähe einer Plantage deiner Organisation.");
                }
            } else {
                if (args[0].equalsIgnoreCase("plant") || args[0].equalsIgnoreCase("legen")) {
                    Block b = p.getLocation().getBlock();
                    if (b.getType().equals(Material.AIR) || b.getType().equals(Material.GRASS)) {
                        if (b.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS) || b.getRelative(BlockFace.DOWN).getType().equals(Material.DIRT) || b.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS_BLOCK)) {

                            Plantage.PlantageType type = getPlantageType(p);
                            if (type != null) {
                                int count = Plantage.getPlantageCount(f);
                                if (count == Organisation.getOrganisation(p).getLevel()+1) {
                                    p.sendMessage(Plantage.PREFIX + "Deine Organisation hat bereits das Limit an Plantagen erreicht.");
                                    return true;
                                }
                                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                if (count < Organisation.getOrganisation(p).getLevel()+1) {
                                    f.sendMessage(Plantage.PREFIX + Script.getName(p) + " hat eine " + type.getItem().getName() + " Plantage gelegt. §8[§6" + (count + 1) + "/" + (Organisation.getOrganisation(p).getLevel()+1) + "§8]");
                                    final Organisation final_f = f;
                                    final Plantage.PlantageType final_type = type;
                                    Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                                        int i = 0;
                                        //TODO umändern auf returned key query
                                        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                                             ResultSet rs = stmt.executeQuery("SELECT AUTO_INCREMENT AS max FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'minecraft' AND TABLE_NAME = 'plantage'")) {
                                            if (rs.next()) {
                                                i = rs.getInt("max");
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        if (i != 0) {
                                            Location pLoc = p.getLocation();
                                            Plantage plant = new Plantage(i, final_f, new Location(p.getWorld(), pLoc.getBlockX(), pLoc.getBlockY(), pLoc.getBlockZ()), final_type);
                                            plant.register();
                                        } else {
                                            p.sendMessage(Plantage.PREFIX + "Es konnte keine Plantage gelegt werden.");
                                        }
                                    });
                                }
                            } else {
                                p.sendMessage(Plantage.PREFIX + "Du hast keine Samen für eine Plantage dabei.");
                            }
                        } else {
                            p.sendMessage(Plantage.PREFIX + "Der Untergrund eignet sich nicht für eine Plantage.");
                        }
                    } else {
                        p.sendMessage(Plantage.PREFIX + "Du kannst hier keine Plantage legen.");
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    Plantage plant = Plantage.getNextPlantage(p, f);
                    if (plant != null) {
                        openGUI(p, plant);
                    } else {
                        p.sendMessage(Plantage.PREFIX + "Du bist nicht in der Nähe einer Plantage deiner Organisation.");
                    }
                } else if (args[0].equalsIgnoreCase("harvest") || args[0].equalsIgnoreCase("ernten") || args[0].equalsIgnoreCase("get")) {
                    Plantage plant = Plantage.getNextPlantage(p, f);
                    if (plant != null) {
                        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((plant.getTime() - System.currentTimeMillis()));
                        if (minutes < 60) {
                            p.sendMessage(Plantage.PREFIX + "Du hast " + plant.getErtrag() + "g " + plant.getType().getItem().getName() + " geerntet.");
                            f.sendMessage(Plantage.PREFIX + "Eine " + plant.getType().getItem().getName() + "-Plantage wurde von " + Script.getName(p) + " geerntet. §8[§6" + plant.getErtrag() + "g§8]");
                            plant.harvest(p);
                        } else {
                            p.sendMessage(Plantage.PREFIX + "Die Plantage kann noch nicht geerntet werden.");
                        }
                    } else {
                        p.sendMessage(Plantage.PREFIX + "Du bist nicht in der Nähe einer Plantage deiner Organisation.");
                    }
                }
            }
        return true;
    }

    private static Plantage.PlantageType getPlantageType(Player p) {
        Plantage.PlantageType type = null;
        ItemStack hand = p.getInventory().getItemInMainHand();
        //if (hand.getType().equals(Material.BEETROOT_SEEDS)) {
            if (hand.hasItemMeta()) {
                if (hand.getItemMeta().getDisplayName().equals("§aKräuter Samen")) {
                    type = Plantage.PlantageType.KRÄUTER;
                } else if (hand.getItemMeta().getDisplayName().equals("§7Pulver Samen")) {
                    type = Plantage.PlantageType.PULVER;
                }
            }
        //}
        return type;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.LARGE_FERN)) {
                Plantage plant = Plantage.getNextPlantage(p, Organisation.getOrganisation(p));
                if (plant != null) {
                    openGUI(e.getPlayer(), plant);
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§aPlantage")) {
            ItemStack is = e.getCurrentItem();
            if (is != null && !is.getType().equals(Material.AIR) && is.hasItemMeta()) {
                e.setCancelled(true);
                if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory())) {
                    Player p = (Player) e.getWhoClicked();
                    Plantage plant = Plantage.getNextPlantage(p, Organisation.getOrganisation(p));
                    if (plant == null) return;
                    switch (is.getItemMeta().getDisplayName()) {
                        case "§aPlantage": {
                            StringBuilder sb = new StringBuilder(" §a==== §2Plantagen Information §a====\n");
                            sb.append("  §8» §6Typ§7:§6 ").append(plant.getType().getItem().getName()).append("\n");
                            sb.append("  §8» §6Reif in§7:§6 ");
                            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((plant.getTime() - System.currentTimeMillis()));
                            if (minutes <= 0) {
                                sb.append("Die Plantage ist reif zur Ernte");
                            } else if (minutes > 60) {
                                int hour = minutes / 60;
                                minutes = minutes - (hour * 60);
                                sb.append(hour).append((hour == 1 ? " Stunde und " : " Stunden und ")).append(minutes).append(" Minuten\n");
                            } else {
                                sb.append(minutes).append(" Minuten\n");
                            }

                            sb.append("  §8» §6Ertrag§7:§6 ").append(plant.getErtrag()).append("g\n");
                            DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

                            Date last_fertilize = new Date(plant.getLastFertilize());
                            sb.append("  §8» §6Gedüngt§7:§6 ").append((plant.getFertilize() ? "Ja\n" : "Nein\n"));
                            sb.append("  §8» §6Letztes Mal gedüngt§7:§6 ").append((plant.getLastFertilize() == 0 ? "Nie\n" : df.format(last_fertilize))).append("\n");

                            Date last_water = new Date(plant.getLastWater());
                            sb.append("  §8» §6Gewässert§7:§6 ").append((plant.getWater() ? "Ja\n" : "Nein\n"));
                            sb.append("  §8» §6Letztes Mal gewässert§7:§6 ").append((plant.getLastWater() == 0 ? "Nie\n" : df.format(last_water))).append("\n");
                            Location plant_loc = plant.getLocation();
                            sb.append("  §8» §6Position§7:§6 ").append(plant_loc.getBlockX()).append("/").append(plant_loc.getBlockY()).append("/").append(plant_loc.getBlockZ());

                            p.sendMessage(sb.toString());
                            e.getView().close();
                            break;
                        }
                        case "§9Zeit": {
                            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes((plant.getTime() - System.currentTimeMillis()));
                            StringBuilder sb = new StringBuilder(Plantage.PREFIX);
                            if (minutes <= 0) {
                                sb.append("Die Plantage ist reif zur Ernte.");
                            } else if (minutes > 60) {
                                sb.append("Die Plantage ist in ");
                                int hour = minutes / 60;
                                minutes = minutes - (hour * 60);
                                sb.append("§l").append(hour).append(" Stunden und ").append(minutes).append(" Minuten§r§2 reif zur Ernte.");
                            } else {
                                sb.append("Die Plantage ist in ");
                                sb.append("§l").append(minutes).append(" Minuten §r§7 reif zur Ernte.");
                            }
                            p.sendMessage(sb.toString());
                            e.getView().close();
                            break;
                        }
                        case "§eErtrag":
                            p.sendMessage(Plantage.PREFIX + "Die Plantage hat derzeit einen Ertrag von §l" + plant.getErtrag() + "g§r§2.");
                            e.getView().close();
                            break;
                        case "§7Dünger":
                            if (e.getClick().equals(ClickType.RIGHT)) {
                                DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                                Date last_fertilize = new Date(plant.getLastFertilize());
                                if (!plant.getFertilize() || plant.getLastFertilize() == 0) {
                                    p.sendMessage(Plantage.PREFIX + "Die Plantage muss gedüngt werden.\n" + Plantage.PREFIX + (plant.getLastFertilize() == 0 ? "Die Plantage wurde noch nicht gedüngt." : "Letztes Mal gedüngt: " + df.format(last_fertilize) + "."));
                                } else {
                                    p.sendMessage(Plantage.PREFIX + "Die Plantage muss nicht gedüngt werden.\n" + Plantage.PREFIX + "Letztes Mal gedüngt: " + df.format(last_fertilize) + ".");
                                }
                            } else {
                                if (!plant.getFertilize() || plant.getLastFertilize() == 0) {
                                    boolean dünger = false, spezialDünger = false;
                                    for (ItemStack item : p.getInventory().getContents()) {
                                        if (item == null) continue;
                                        if (!item.getType().equals(Material.INK_SAC)) continue;
                                        if (!item.hasItemMeta()) continue;
                                        if (item.getItemMeta().getDisplayName().equals("§7Dünger")) {
                                            ItemMeta meta = item.getItemMeta();
                                            if (meta.getLore() != null && meta.getLore().get(0) != null) {
                                                String raw = ChatColor.stripColor(meta.getLore().get(0));
                                                String uses = raw.split("/")[0];
                                                if (Script.isInt(uses)) {
                                                    dünger = true;
                                                    int i = Integer.parseInt(uses);
                                                    if (i == 1) {
                                                        item.setAmount(item.getAmount()-1);
                                                    } else {
                                                        meta.setLore(Collections.singletonList("§6" + (i - 1) + "/5"));
                                                        item.setItemMeta(meta);
                                                    }
                                                }
                                                break;
                                            } else {
                                                p.sendMessage(Plantage.PREFIX + "Du hast keinen Dünger bei dir.");
                                            }
                                        } else if (item.getItemMeta().getDisplayName().equals("§bSpezial-Dünger")) {
                                            spezialDünger = true;
                                            item.setAmount(item.getAmount()-1);
                                            break;
                                        }
                                    }
                                    if (dünger || spezialDünger) {
                                        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - plant.getLastFertilize());
                                        if (spezialDünger) {
                                            if (minutes < 2) {
                                                plant.setPurity(plant.getCurrentPurity() + 20);
                                            } else if (minutes < 5) {
                                                plant.setPurity(plant.getCurrentPurity() + 14);
                                            } else if (minutes < 8) {
                                                plant.setPurity(plant.getCurrentPurity() + 4);
                                            } else if (minutes > 10) {
                                                plant.setPurity(plant.getCurrentPurity() + 2);
                                            }
                                            if (minutes < 2) {
                                                plant.setPurity(plant.getCurrentPurity() + Script.getRandom(19, 21));
                                            } else if (minutes < 8) {
                                                plant.setPurity(plant.getCurrentPurity() + Script.getRandom(13, 15));
                                            } else if (minutes < 15) {
                                                plant.setPurity(plant.getCurrentPurity() + Script.getRandom(3, 5));
                                            } else {
                                                plant.setPurity(plant.getCurrentPurity() + Script.getRandom(2, 3));
                                            }
                                        } else {
                                            if (minutes < 2) {
                                                plant.setPurity(plant.getCurrentPurity() + Script.getRandom(9, 11));
                                            } else if (minutes < 8) {
                                                plant.setPurity(plant.getCurrentPurity() + Script.getRandom(6, 8));
                                            } else if (minutes < 15) {
                                                plant.setPurity(plant.getCurrentPurity() + Script.getRandom(1, 3));
                                            } else {
                                                plant.setPurity(plant.getCurrentPurity() - Script.getRandom(0, 5));
                                            }
                                        }
                                        plant.setFertilize(true);
                                        plant.setLastFertilize(System.currentTimeMillis());
                                        plant.getOrganisation().sendMessage(Plantage.PREFIX + "Eine " + plant.getType().getItem().getName() + "-Plantage wurde von " + Script.getName(p) + " gedüngt.");
                                        Script.addEXP(p, Script.getRandom(2, 5));
                                        new Particle(org.bukkit.Particle.CRIT, plant.getLocation().clone().add(0, .5, 0), false, 0.01F, 0.01F, 0.01F, 0.01F, Script.getRandom(4, 9)).sendAll();
                                        new BukkitRunnable() {
                                            private int runs;

                                            public void run() {
                                                if (runs++ >= 10) {
                                                    this.cancel();
                                                    return;
                                                }
                                                new Particle(org.bukkit.Particle.CRIT, plant.getLocation().clone().add(0, .5, 0), false, 0.01F, 0.01F, 0.01F, 0.01F, Script.getRandom(4, 9)).sendAll();
                                            }
                                        }.runTaskTimer(NewRoleplayMain.getInstance(), 10L, 10L);
                                    } else {
                                        p.sendMessage(Plantage.PREFIX + "Du hast keinen Dünger bei dir.");
                                    }
                                } else {
                                    p.sendMessage(Plantage.PREFIX + "Die Plantage muss nicht gedüngt werden.");
                                }
                            }
                            break;
                        case "§9Wasser":
                            if (e.getClick().equals(ClickType.RIGHT)) {
                                if (!plant.getWater() || plant.getLastWater() == 0) {
                                    DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                                    Date last_water = new Date(plant.getLastWater());
                                    p.sendMessage(Plantage.PREFIX + "Die Plantage muss gewässert werden. \n" + Plantage.PREFIX + (plant.getLastWater() == 0 ? "Die Plantage wurde noch nicht gewässert." : "Letztes Mal gewässert: " + df.format(last_water) + "."));
                                } else {
                                    DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                                    Date last_water = new Date(plant.getLastWater());
                                    p.sendMessage(Plantage.PREFIX + "Die Plantage muss nicht gewässert werden.\n" + Plantage.PREFIX + "Letztes Mal gewässert: " + df.format(last_water) + ".");
                                }
                            } else {
                                if (!plant.getWater() || plant.getLastWater() == 0) {
                                    boolean b = false;
                                    for (ItemStack item : p.getInventory().getContents()) {
                                        if (item == null) continue;
                                        if (!item.getType().equals(Material.WATER_BUCKET)) continue;
                                        if (!item.hasItemMeta()) continue;
                                        if (!item.getItemMeta().getDisplayName().equals("§9Wasser")) continue;

                                        ItemMeta meta = item.getItemMeta();
                                        if (meta.getLore() != null && meta.getLore().get(0) != null) {
                                            String raw = ChatColor.stripColor(meta.getLore().get(0));
                                            String uses = raw.split("/")[0];
                                            if (Script.isInt(uses)) {
                                                b = true;
                                                int i = Integer.parseInt(uses);
                                                if (i == 1) {
                                                    item.setAmount(0);
                                                } else {
                                                    meta.setLore(Collections.singletonList("§6" + (i - 1) + "/5"));
                                                    item.setItemMeta(meta);
                                                }
                                            }
                                            break;
                                        } else {
                                            p.sendMessage(Plantage.PREFIX + "Du hast kein Wasser bei dir.");
                                        }
                                    }
                                    if (b) {
                                        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - plant.getLastWater());
                                        if (minutes < 2) {
                                            plant.setPurity(plant.getCurrentPurity() + Script.getRandom(9, 11));
                                        } else if (minutes < 8) {
                                            plant.setPurity(plant.getCurrentPurity() + Script.getRandom(6, 8));
                                        } else if (minutes < 15) {
                                            plant.setPurity(plant.getCurrentPurity() + Script.getRandom(1, 3));
                                        } else {
                                            plant.setPurity(plant.getCurrentPurity() - Script.getRandom(0, 5));
                                        }
                                        plant.setWater(true);
                                        plant.setLastWater(System.currentTimeMillis());
                                        plant.getOrganisation().sendMessage(Plantage.PREFIX + "Eine " + plant.getType().getItem().getName() + "-Plantage wurde von " + Script.getName(p) + " gewässert.");
                                        Script.addEXP(p, Script.getRandom(2, 5));
                                        new Particle(org.bukkit.Particle.WATER_DROP, plant.getLocation().clone().add(0, .8, 0), false, 0.01F, 0.01F, 0.01F, 0.01F, Script.getRandom(4, 9)).sendAll();
                                        new BukkitRunnable() {
                                            private int runs;

                                            public void run() {
                                                if (runs++ >= 10) {
                                                    this.cancel();
                                                    return;
                                                }
                                                new Particle(org.bukkit.Particle.WATER_DROP, plant.getLocation().clone().add(0, .8, 0), false, 0.01F, 0.01F, 0.01F, 0.01F, Script.getRandom(4, 9)).sendAll();
                                            }
                                        }.runTaskTimer(NewRoleplayMain.getInstance(), 10L, 10L);
                                    } else {
                                        p.sendMessage(Plantage.PREFIX + "Du hast kein Wasser bei dir.");
                                    }
                                } else {
                                    p.sendMessage(Plantage.PREFIX + "Die Plantage muss nicht gewässert werden.");
                                }
                            }
                            break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock().getType() == Material.SOUL_SOIL) {
            e.setCancelled(true);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("plant") || cmd.getName().equalsIgnoreCase("pl") || cmd.getName().equalsIgnoreCase("plantage")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            oneArgList.add("plant");
            oneArgList.add("info");
            oneArgList.add("harvest");

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            }

            if (args.length == 2) {
                return null;
            }
            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

}