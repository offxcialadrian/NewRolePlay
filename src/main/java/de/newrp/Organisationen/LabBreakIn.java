package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.AFK;
import org.bukkit.*;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LabBreakIn implements CommandExecutor, Listener {
    public static final Location[] locations = new Location[]{new Location(Script.WORLD, 363, 70, 1315), new Location(Script.WORLD, 363, 70, 1316), new Location(Script.WORLD, 363, 70, 1317),
                                                new Location(Script.WORLD, 364, 70, 1317), new Location(Script.WORLD, 365, 70, 1317), new Location(Script.WORLD, 366, 70, 1317),
                                                new Location(Script.WORLD, 366, 70, 1316), new Location(Script.WORLD, 366, 70, 1315), new Location(Script.WORLD, 365, 70, 1315),
                                                new Location(Script.WORLD, 364, 70, 1315), new Location(Script.WORLD, 363, 70, 1303), new Location(Script.WORLD, 363, 70, 1304),
                                                new Location(Script.WORLD, 363, 70, 1305), new Location(Script.WORLD, 364, 70, 1305), new Location(Script.WORLD, 365, 70, 1305),
                                                new Location(Script.WORLD, 366, 70, 1305), new Location(Script.WORLD, 366, 70, 1304), new Location(Script.WORLD, 366, 70, 1303),
                                                new Location(Script.WORLD, 365, 70, 1303), new Location(Script.WORLD, 364, 70, 1303)};
    private static final int NEEDED_PROGRESS = 30;
    public static final ItemStack POTION;
    public static int progress = 0;
    public static int schedulerID;
    public static Location putLocation;
    public static long lastPut;
    public static String PREFIX = "§8[§fLabor§8] §f" + Messages.ARROW + " §7";
    public static final Location doorOneLocation = new Location(Script.WORLD, 375, 76, 1312);
    public static final Location doorTwoLocation = new Location(Script.WORLD, 374, 76, 1312);
    public static final Map<Location, Long> DOOR_BROKE_TIMES = new HashMap<>();
    public static long cooldown = 0L;
    public static String brokeIn;

    static {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setDisplayName("§cZutat");
        potionMeta.setLore(Collections.singletonList("§7Füll das in den leuchtenden Braustand um"));
        potionMeta.setColor(Color.AQUA);
        potion.setItemMeta(potionMeta);
        POTION = Script.removeAttributes(potion);
    }

    public static void handleTake(Player p, BrewingStand brewingStand) {
        Inventory inv = p.getInventory();
        Inventory brewingStandInventory = brewingStand.getInventory();
        if (!isPotionInInventory(brewingStandInventory)) return;
        removePotion(brewingStandInventory);
        inv.addItem(POTION);
        putLocation = randomBrewingStand(brewingStand.getLocation()).getLocation();
        spawnParticles(putLocation);
    }

    public static void handlePut(Player p, BrewingStand brewingStand) {
        Inventory playerInventory = p.getInventory();
        Location brewingStandLocation = brewingStand.getLocation();
        if (!isPotionInInventory(playerInventory)) return;
        removePotion(playerInventory);
        Bukkit.getScheduler().cancelTask(schedulerID);
        if (!brewingStandLocation.equals(putLocation)) {
            progress = 0;
            lastPut = 0;
            schedulerID = 0;
            putLocation = null;
            brokeIn = null;
            Organisation.getOrganisation(p).sendMessage(Messages.ERROR + Script.getName(p) + " hat die Zutat in den falschen Braustand gefüllt.");
            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Es wurde ein Fehler bei der Herstellung von Exiyty gemacht");
            for (Entity nearbyEntity : brewingStandLocation.getNearbyEntities(15, 7, 15)) {
                if (!(nearbyEntity instanceof Player)) {
                    continue;
                }

                final Player nearbyPlayer = (Player) nearbyEntity;
                nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                nearbyPlayer.playSound(brewingStandLocation, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1f);
                nearbyPlayer.sendMessage("§8§l§k!!!§r §8§l§oEs wurde ein Fehler gemacht bei der Herstellung §8§l§k!!!§r");
                nearbyPlayer.damage(10f);
                if(Script.getRandom(1, 10) == 1) {
                    Krankheit.HUSTEN.add(Script.getNRPID(p));
                    nearbyPlayer.sendMessage("§8§l§oDu fühlst dich plötzlich krank");
                }
            }
            return;
        }
        long currentTime = System.currentTimeMillis();
        long timeDifference = Math.abs(currentTime - lastPut);
        if (lastPut != 0 && timeDifference > 30 * 1000L) {
            p.sendMessage(Messages.ERROR + " Du hast zu lange gebraucht um das Exiyty herzustellen.");
            Organisation.getOrganisation(p).sendMessage(PREFIX + "§c" + Script.getName(p) + " §7hat zu lange gebraucht um das Exiyty herzustellen.");
            return;
        }
        removePotion(brewingStand.getInventory());
        p.sendMessage(PREFIX + "Du hast die Zutat in den Braustand gefüllt.");
        p.sendMessage(PREFIX + progress + "/" + NEEDED_PROGRESS);
        if (++progress == NEEDED_PROGRESS) {

            Drogen.DrugPurity purity;
            if (timeDifference > 0 && timeDifference <= 20 * 1000L) {
                purity = Drogen.DrugPurity.HIGH;
            } else if (timeDifference > 10 * 1000L && timeDifference <= 30 * 1000L) {
                purity = Drogen.DrugPurity.GOOD;
            } else if (timeDifference > 20 * 1000L && timeDifference <= 40 * 1000L) {
                purity = Drogen.DrugPurity.MEDIUM;
            } else {
                purity = Drogen.DrugPurity.BAD;
            }

            progress = 0;
            lastPut = 0;
            schedulerID = 0;
            putLocation = null;
            brokeIn = null;
            giveDrug(p, purity);
            return;
        }
        lastPut = currentTime;
        BrewingStand randomBrewingStand = randomBrewingStand(brewingStandLocation);
        putIntoBrewingStand(randomBrewingStand);
    }

    private static void start() {
        cooldown = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(3);
        BrewingStand randomBrewingStand = randomBrewingStand();
        putIntoBrewingStand(randomBrewingStand);
        progress++;
    }

    private static void putIntoBrewingStand(BrewingStand b) {
        b.getInventory().setItem(1, POTION);
    }

    static void removePotion(Inventory inv) {
        for (ItemStack is : inv.getContents()) {
            if (is == null) continue;
            if(is.getItemMeta() == null) continue;
            if(!is.getItemMeta().hasDisplayName()) continue;
            if (is.getItemMeta().getDisplayName().equalsIgnoreCase(POTION.getItemMeta().getDisplayName())) {
                is.setAmount(0);
            }
        }
    }

    public static boolean isLocation(Location location) {
        for (Location l : locations) {
            if (l.equals(location)) return true;
        }
        return false;
    }

    private static void spawnParticles(Location loc) {
        final Location clonedLoc = loc.clone().add(0.5, 1, 0.5);
        schedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(NewRoleplayMain.getInstance(), () -> {
            new de.newrp.API.Particle(org.bukkit.Particle.ENCHANTMENT_TABLE, clonedLoc, false, 0.2F, 0.2F, 0.2F, .1F, Script.getRandom(4, 8)).sendAll();
            clonedLoc.getWorld().playSound(loc, Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
        }, 0L, 10L);
    }

    private static BrewingStand randomBrewingStand(Location lastLocation) {
        Location l = null;
        for (int i = 0; i < 2; i++) {
            l = locations[Script.getRandom(0, locations.length - 1)];
            if (l != lastLocation) break;
        }
        return (BrewingStand) l.getBlock().getState();
    }

    private static BrewingStand randomBrewingStand() {
        return (BrewingStand) locations[Script.getRandom(0, locations.length - 1)].getBlock().getState();
    }

    private static boolean isPotionInInventory(Inventory inv) {
        for (ItemStack is : inv.getContents()) {
            if (is == null) continue;

            if (is.isSimilar(POTION)) {
                return true;
            }
        }
        return false;
    }

    private static void giveDrug(Player p, Drogen.DrugPurity purity) {
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        int i = Script.getRandom(6, 7);
        ItemStack item = new ItemStack(Material.WARPED_BUTTON, i);
        p.getInventory().addItem(new ItemBuilder(Material.WARPED_BUTTON).setAmount(i).setName(Drogen.ECSTASY.getName()).setLore("§7Reinheitsgrad: " + purity.getText()).build());
        Organisation.getOrganisation(p).addExp(i * 4);
        p.sendMessage(PREFIX + "Du konntest " + i + " Pillen Exiyty herstellen.");
        Organisation.getOrganisation(p).sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat " + i + " Pillen Exiyty hergestellt.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Es wurde Exiyty hergestellt.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (!p.getName().equals(brokeIn)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht die Person, die eingebrochen ist.");
            return true;
        }
        if (p.getLocation().distance(new Location(Script.WORLD, 364, 69, 1310, -182.26685f, 27.894108f)) > 5) return true;
        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }
        boolean cooldown_check = progress != 0;
        long difference = 0;
        if (!cooldown_check) {
            difference = cooldown - System.currentTimeMillis();
            cooldown_check = difference >= 0;
        }
        if (cooldown_check) {
            p.sendMessage(Messages.ERROR + "Das Labor hat derzeit keine Materialien zur Verfügung. (" + TimeUnit.MILLISECONDS.toMinutes(cooldown - System.currentTimeMillis()) + " Minuten verbleibend)");
            return true;
        }
        p.sendMessage(PREFIX + "Schütte nun die Tränke in den richtigen Braustand.");
        p.sendMessage(Messages.INFO + "Mit Rechtsklick nimmst du einen Trank und mit Linksklick in einen Braustand.");
        start();
        brokeIn = null;
        return true;
    }

    public static void breakDoors(Player p) {
        brokeIn = p.getName();
        toggleDoorState(doorOneLocation.getBlock(), true, true);
        toggleDoorState(doorTwoLocation.getBlock(), true, false);

        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), new Location(Script.WORLD, 364, 69, 1310, -182.26685f, 27.894108f), PREFIX + "Schütte nun die Chemikalien zusammen um Exiyty zu bekommen.", () -> Script.performCommand(p, "mixingredients")).start();
        DOOR_BROKE_TIMES.clear();
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Es wurde ein Einbruch im Labor gemeldet!");
        Organisation.getOrganisation(p).sendMessage(PREFIX + "§6" + Script.getName(p) + " §7ist in das Labor eingebrochen.");
        for (UUID m : Organisation.getOrganisation(p).getMember()) if (Bukkit.getOfflinePlayer(m).isOnline()) if (!AFK.isAFK(m)) if (Objects.requireNonNull(Bukkit.getPlayer(m)).getLocation().distance(p.getLocation()) <= 60)
            Activity.grantActivity(Script.getNRPID(Bukkit.getPlayer(m)), Activities.LABOR);
    }

    public static void repairDoors(boolean complete) {
        toggleDoorState(doorOneLocation.getBlock(), false, false);
        toggleDoorState(doorTwoLocation.getBlock(), false, false);
        for (Location loc : locations) {
            BrewingStand brewingStand = (BrewingStand) loc.getBlock().getState();
            removePotion(brewingStand.getInventory());
        }
    }

    public static void toggleDoorState(Block block, boolean open, boolean playSound) {
        BlockState state = block.getState();
        Door door = (Door) state.getBlockData();
        door.setOpen(open);
        state.setBlockData(door);
        state.update();
        Debug.debug("Closing lab door");
        if (playSound) {
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (!block.getType().equals(Material.IRON_DOOR)) return;
        Player p = e.getPlayer();
        PlayerInventory inv = p.getInventory();
        ItemStack itemInHand = inv.getItemInMainHand();
        if (itemInHand == null) return;
        if (itemInHand.getType() != Material.BLAZE_ROD) return;
        if(!Organisation.hasOrganisation(p)) return;
        Location blockLocation = block.getLocation();
        Location doorLocation = getDoorLocation(blockLocation);
        if (doorLocation == null) return;

        if(DOOR_BROKE_TIMES.size() == 1) {
            final long timeAgo = DOOR_BROKE_TIMES.values().stream().findFirst().orElseGet(() -> 0L);
            if(System.currentTimeMillis() - timeAgo > TimeUnit.MINUTES.toMillis(3)) {
                Debug.debug("Restoring defaults, because buggy :(");
                DOOR_BROKE_TIMES.clear();
            }
        }

        Debug.debug("Breaking lab door " + (int) doorLocation.getX() + "/" + (int) doorLocation.getY() + "/" + (int) doorLocation.getZ());
        boolean cooldown_check = progress != 0;
        long difference = 0;
        if (!cooldown_check) {
            difference = cooldown - System.currentTimeMillis();
            cooldown_check = difference >= 0;
        }
        if (cooldown_check) {
            p.sendMessage(PREFIX + "Das Labor hat derzeit keine Materialien zur Verfügung. (" + TimeUnit.MILLISECONDS.toMinutes(cooldown - System.currentTimeMillis()) + " Minuten verbleibend)");
            return;
        }

        long currentTime = System.currentTimeMillis();
        DOOR_BROKE_TIMES.put(doorLocation, currentTime);
        Debug.debug("Door broke at " + currentTime + " by " + p.getName() + " size is now at " + DOOR_BROKE_TIMES.size());
        if (!checkDoorBreak(p)) {
            if (DOOR_BROKE_TIMES.size() > 1) {
                p.sendMessage(PREFIX + "Der Einbruch ist fehlgeschlagen.");
                Organisation.getOrganisation(p).sendMessage(PREFIX + "§c" + Script.getName(p) + " §7hat den Einbruch fehlgeschlagen.");
                DOOR_BROKE_TIMES.clear();
                return;
            }
        }

        e.setCancelled(true);
        inv.getItemInMainHand().setAmount(inv.getItemInMainHand().getAmount() - 1);
    }

    private boolean checkDoorBreak(Player p) {
        if (DOOR_BROKE_TIMES.size() != 2) return false;
        long doorOneBrokeTime = 0L;
        long doorTwoBrokeTime = 0L;
        for (long time : DOOR_BROKE_TIMES.values()) {
            if (doorOneBrokeTime == 0) {
                doorOneBrokeTime = time;
            } else {
                doorTwoBrokeTime = time;
            }
        }
        Debug.debug("Door one broke at " + doorOneBrokeTime + " and door two broke at " + doorTwoBrokeTime + ", difference is " + Math.abs(doorOneBrokeTime - doorTwoBrokeTime));
        long timeDifference = Math.abs(doorOneBrokeTime - doorTwoBrokeTime);
        if (timeDifference > 30000) {
            Debug.debug("Time difference is too high (" + timeDifference + ")");
            return false;
        }
        breakDoors(p);
        return true;
    }

    private Location getDoorLocation(Location loc) {
        return checkLocations(loc, doorOneLocation, doorTwoLocation);
    }

    private Location checkLocations(Location loc, Location... doorLocations) {
        return Arrays.stream(doorLocations).filter(doorLoc -> checkLocation(loc, doorLoc)).findFirst().orElse(null);
    }

    private boolean checkLocation(Location loc, Location doorLoc) {
        return loc.equals(doorLoc) || loc.clone().add(0, -1, 0).equals(doorLoc);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        boolean leftClickBlock = action == Action.LEFT_CLICK_BLOCK;
        if (!leftClickBlock && action != Action.RIGHT_CLICK_BLOCK) return;
        Player p = e.getPlayer();
        if (BuildMode.isInBuildMode(p)) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.BREWING_STAND) return;
        Location blockLocation = block.getLocation();
        if (!isLocation(blockLocation)) return;
        e.setCancelled(true);
        BrewingStand brewingStand = (BrewingStand) block.getState();
        if (leftClickBlock) {
            handlePut(p, brewingStand);
        } else {
            handleTake(p, brewingStand);
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase(brokeIn)) {
            Bukkit.getScheduler().cancelTask(schedulerID);
            progress = 0;
            lastPut = 0;
            schedulerID = 0;
            putLocation = null;
            brokeIn = null;
        }
    }

}