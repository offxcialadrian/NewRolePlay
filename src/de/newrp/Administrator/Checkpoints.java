package de.newrp.Administrator;

import de.newrp.Player.AFK;
import de.newrp.main;
import de.newrp.API.*;
import de.newrp.Police.Jail;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Checkpoints implements Listener {

    public static final HashMap<Location, String> CHECKPOINT_LOCATION = new HashMap<>();
    public static final HashMap<String, Integer> CHECKPOINTS = new HashMap<>();

    public static String PREFIX = "§8[§cCheckpoints§8] §c" + Messages.ARROW + " §7";

    private static Location[] locs = new Location[] {
            new Location(Script.WORLD, 490, 11, 535),
            new Location(Script.WORLD, 495, 7, 543),
            new Location(Script.WORLD, 506, 10, 543),
            new Location(Script.WORLD, 508, 16, 533),
            new Location(Script.WORLD, 502, 22, 537),
            new Location(Script.WORLD, 503, 21, 542),
            new Location(Script.WORLD, 505, 16, 545),
            new Location(Script.WORLD, 502, 19, 548),
            new Location(Script.WORLD, 510, 18, 555),
            new Location(Script.WORLD, 505, 17, 560),
            new Location(Script.WORLD, 508, 19, 567),
            new Location(Script.WORLD, 503, 22, 565),
            new Location(Script.WORLD, 498, 18, 564),
            new Location(Script.WORLD, 493, 11, 573),
            new Location(Script.WORLD, 472, 11, 580),
            new Location(Script.WORLD, 472, 9, 585),
            new Location(Script.WORLD, 484, 8, 585),
            new Location(Script.WORLD, 487, 7, 593),
            new Location(Script.WORLD, 493, 9, 585),
            new Location(Script.WORLD, 499, 11, 581),
            new Location(Script.WORLD, 504, 12, 578),
            new Location(Script.WORLD, 501, 12, 570),
            new Location(Script.WORLD, 499, 10, 567),
            new Location(Script.WORLD, 495, 13, 552),
            new Location(Script.WORLD, 485, 14, 546),
            new Location(Script.WORLD, 479, 14, 544),
            new Location(Script.WORLD, 477, 10, 540),
            new Location(Script.WORLD, 474, 13, 542),
            new Location(Script.WORLD, 469, 9, 541),
            new Location(Script.WORLD, 467, 13, 542),
            new Location(Script.WORLD, 460, 8, 544),
            new Location(Script.WORLD, 461, 12, 547),
            new Location(Script.WORLD, 457, 11, 552),
            new Location(Script.WORLD, 456, 11, 562),
            new Location(Script.WORLD, 464, 10, 572),
            new Location(Script.WORLD, 463, 12, 578),
            new Location(Script.WORLD, 470, 15, 579),
            new Location(Script.WORLD, 477, 16, 586),
            new Location(Script.WORLD, 465, 18, 590),
            new Location(Script.WORLD, 462, 16, 593),
            new Location(Script.WORLD, 460, 23, 590),
            new Location(Script.WORLD, 459, 24, 583),
            new Location(Script.WORLD, 464, 22, 568),
            new Location(Script.WORLD, 467, 22, 561),
            new Location(Script.WORLD, 469, 17, 564),
            new Location(Script.WORLD, 470, 15, 571),
            new Location(Script.WORLD, 461, 25, 574),
            new Location(Script.WORLD, 469, 24, 591),
            new Location(Script.WORLD, 477, 26, 594),
            new Location(Script.WORLD, 487, 24, 589),
            new Location(Script.WORLD, 493, 20, 577),
            new Location(Script.WORLD, 487, 20, 581),
            new Location(Script.WORLD, 485, 15, 576),
            new Location(Script.WORLD, 499, 8, 554),
            new Location(Script.WORLD, 498, 12, 547),
            new Location(Script.WORLD, 471, 13, 534),
            new Location(Script.WORLD, 461, 11, 556),
            new Location(Script.WORLD, 470, 14, 558),
            new Location(Script.WORLD, 478, 15, 548),
            new Location(Script.WORLD, 480, 13, 535),
    };
    public static void place(Player p) {
        Block cp;

        World w = p.getWorld();

        while (true) {
            Location loc = locs[Script.getRandom(0, locs.length - 1)];
            if(CHECKPOINT_LOCATION.containsKey(loc)) continue;
            if(loc.getBlock().getType() != Material.AIR) continue;
            if(loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) continue;
            placeCheckpoint(p, loc);
            break;
        }
    }

    public static void placeCheckpoint(Player p, Location loc) {
        loc.getChunk().load();
        loc.getBlock().setType(Material.REDSTONE_BLOCK);
        CHECKPOINT_LOCATION.put(loc, p.getName());
    }


    public static void start(Player p, int checkpoints) {
        if (Friedhof.isDead(p)) {
            Friedhof.revive(p, null);
        }

        if (Jail.isInJail(p)) Jail.unarrest(p);

        Location loc = new Location(Script.WORLD, 484, 8, 561, 87.723145f, 7.387953f);
        loc.getChunk().load();
        p.teleport(loc);
        p.sendMessage(Messages.INFO + "Herzlich Willkommen im Checkpoints-Gefängnis.");
        p.sendMessage(Messages.INFO + "Du musst den dir zugewiesenen Redstone-Block finden und anklicken, um ihn zu entfernen.");
        p.sendMessage(Messages.INFO + "Du hast " + checkpoints + " Checkpoints.");

        setScoreboard(p, checkpoints);
        setCheckpoints(p, checkpoints, false);
    }

    public static void setScoreboard(Player p, int checkpoints) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.getObjective("Checkpoints");
        if (obj == null) {
            obj = board.registerNewObjective("Checkpoints", "dummy");
        }
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "§cNRP × Checkpoints");
        Score score1 = obj.getScore(ChatColor.GRAY + "§bCheckpoints§8:");
        Score score2 = obj.getScore(ChatColor.DARK_AQUA + " §8» §a" + checkpoints);
        Score blank = obj.getScore(" ");

        blank.setScore(2);
        score1.setScore(1);
        score2.setScore(0);
        p.setScoreboard(board);
    }

    public static boolean hasCheckpoints(Player p) {
        return getCheckpoints(p) > 0;
    }

    public static boolean hasCheckpoints(OfflinePlayer p) {
        return getCheckpoints(p) > 0;
    }

    public static int getCheckpoints(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT checkpoints FROM checkpoints WHERE id=" + id)) {
            if (rs.next()) {
                int checkpoints = rs.getInt("checkpoints");
                if (checkpoints <= 0) {
                    Script.executeAsyncUpdate("DELETE FROM checkpoints WHERE id = " + id);
                    return 0;
                } else {
                    return checkpoints;
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return 0;
    }

    public static int getCheckpoints(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT checkpoints FROM checkpoints WHERE id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                int checkpoints = rs.getInt("checkpoints");
                if (checkpoints <= 0) {
                    Script.executeAsyncUpdate("DELETE FROM checkpoints WHERE id = " + Script.getNRPID(p));
                    return 0;
                } else {
                    return checkpoints;
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return 0;
    }

    public static int getCheckpoints(OfflinePlayer p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT checkpoints FROM checkpoints WHERE id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                int checkpoints = rs.getInt("checkpoints");
                if (checkpoints <= 0) {
                    Script.executeAsyncUpdate("DELETE FROM checkpoints WHERE id = " + Script.getNRPID(p));
                    return 0;
                } else {
                    return checkpoints;
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return 0;
    }

    public static void setCheckpoints(Player p, int amount, boolean updateScoreboard) {
        place(p);
        CHECKPOINTS.put(p.getName(), amount);
        Script.executeAsyncUpdate("INSERT INTO checkpoints (id, checkpoints) VALUES (" + Script.getNRPID(p) + ", " + amount + ") ON DUPLICATE KEY UPDATE checkpoints = " + amount);
        if (updateScoreboard) {
            setScoreboard(p, amount);
        }
    }

    public static void clear(Player p) {
        clearCheckpoints(p);
        Script.executeAsyncUpdate("DELETE FROM checkpoints WHERE id = " + Script.getNRPID(p));
        Cache.loadScoreboard(p);
        p.teleport(new Location(Script.WORLD, 587, 69, 991, -268.28235f, -3.7367816f));
        CHECKPOINTS.remove(p.getName());
    }

    public static void clear(OfflinePlayer p) {
        clearCheckpoints(p);
        Script.executeAsyncUpdate("DELETE FROM checkpoints WHERE id = " + Script.getNRPID(p));
        CHECKPOINTS.remove(p.getName());
    }

    public static void clearCheckpoints(Player p) {
        if (!hasCheckpoints(p)) return;
        Cache.loadScoreboard(p);

        Iterator<Map.Entry<Location, String>> it = CHECKPOINT_LOCATION.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Location, String> ent = it.next();
            if (ent.getValue().equals(p.getName())) {
                ent.getKey().getBlock().setType(Material.AIR);
                it.remove();
            }
        }
    }

    public static void clearCheckpoints(OfflinePlayer p) {
        if (!hasCheckpoints(p)) return;

        Iterator<Map.Entry<Location, String>> it = CHECKPOINT_LOCATION.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Location, String> ent = it.next();
            if (ent.getValue().equals(p.getName())) {
                ent.getKey().getBlock().setType(Material.AIR);
                it.remove();
            }
        }
    }

    public static void checkCheckpoints(Player p) {
        if (hasCheckpoints(p)) {
            p.sendMessage(PREFIX + "Du hast noch " + getCheckpoints(p) + " Checkpoints.");
            place(p);
            CHECKPOINTS.put(p.getName(), getCheckpoints(p));
            setScoreboard(p, getCheckpoints(p));
            Location loc = new Location(Script.WORLD, 484, 8, 561, 87.723145f, 7.387953f);
            loc.getChunk().load();
            p.teleport(loc);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        Player p = e.getPlayer();
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) && !(e.getAction() == Action.LEFT_CLICK_BLOCK)) return;
        Block b = e.getClickedBlock();
        if(b == null) return;
        Location loc = b.getLocation();
        if (Checkpoints.CHECKPOINT_LOCATION.containsKey(loc)) {
            String name = Checkpoints.CHECKPOINT_LOCATION.get(loc);
            if (name.equalsIgnoreCase(p.getName())) {
                Checkpoints.CHECKPOINT_LOCATION.remove(loc);
                b.setType(Material.AIR);
                int checkpoints = Checkpoints.getCheckpoints(p) - 1;
                if (checkpoints == 0) {
                    Log.NORMAL.write(p, "hat seine Checkpoint Strafe beendet.");
                    Checkpoints.clear(p);
                    p.sendMessage(PREFIX + "Du hast deine Checkpoint Strafe beendet.");
                } else {
                    Checkpoints.setCheckpoints(p, checkpoints, true);
                    p.playSound(loc, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1F, 1F);
                    p.setFoodLevel(20);
                    Health.THIRST.set(Script.getNRPID(p), Health.THIRST.getMax());
                }
            } else {
                if (Script.getPlayer(name) == null) b.setType(Material.AIR);
                p.sendMessage(PREFIX + "Dieser Checkpoint gehört §c" + name + "§c.");
            }
        }
    }

    public static Player getOwner(Location loc) {
        for(Map.Entry<Location, String> entry : CHECKPOINT_LOCATION.entrySet()) {
            if(entry.getKey().equals(loc)) {
                return Script.getPlayer(entry.getValue());
            }
        }
        return null;
    }

    public static Location getLocation(Player p) {
        for(Map.Entry<Location, String> entry : CHECKPOINT_LOCATION.entrySet()) {
            if(entry.getValue().equals(p.getName())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTask(main.getInstance(), () -> Checkpoints.checkCheckpoints(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Checkpoints.clearCheckpoints(p);
        Checkpoints.CHECKPOINTS.remove(p.getName());

        String name = p.getName();
        Checkpoints.CHECKPOINT_LOCATION.values().removeIf(entry -> entry.equals(name));
    }

    @EventHandler
    public void onDMG(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (Checkpoints.hasCheckpoints(p)) {
                e.setCancelled(true);
            }
        }
    }

}
