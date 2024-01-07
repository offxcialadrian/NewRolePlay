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
    public static void place(Player p) {
        Block cp;

        World w = p.getWorld();

        while (true) {
            int i = Script.getRandom(1, 3);
            if (i == 1) {
                int max_x = 657;
                int min_x = 621;

                int x = Script.getRandom(min_x, max_x);

                int y = 9;

                int max_z = 161;
                int min_z = 125;

                int z = Script.getRandom(min_z, max_z);

                cp = new Location(w, -x, y, z).getBlock();
                if (cp.getType().equals(Material.AIR) && !cp.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                    if (!CHECKPOINT_LOCATION.containsKey(cp.getLocation())) {
                        placeCheckpoint(p, cp.getLocation());
                        break;
                    }
                }
            } else if (i == 2) {
                int max_x = 657;
                int min_x = 621;

                int x = Script.getRandom(min_x, max_x);

                int y = 16;

                int max_z = 161;
                int min_z = 125;

                int z = Script.getRandom(min_z, max_z);

                cp = new Location(w, -x, y, z).getBlock();
                if (cp.getType().equals(Material.AIR) && !cp.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                    if (!CHECKPOINT_LOCATION.containsKey(cp.getLocation())) {
                        placeCheckpoint(p, cp.getLocation());
                        break;
                    }
                }
            } else if (i == 3) {
                int max_x = 663;
                int min_x = 615;

                int x = Script.getRandom(min_x, max_x);

                int y = 23;

                int max_z = 167;
                int min_z = 119;

                int z = Script.getRandom(min_z, max_z);

                cp = new Location(w, -x, y, z).getBlock();
                if (cp.getType().equals(Material.AIR) && !cp.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                    if (!CHECKPOINT_LOCATION.containsKey(cp.getLocation())) {
                        placeCheckpoint(p, cp.getLocation());
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    public static void placeCheckpoint(Player p, Location loc) {
        loc.getChunk().load();
        loc.getBlock().setType(Material.PLAYER_HEAD);
        Skull head = loc.getBlock().getState() instanceof Skull ? (Skull) loc.getBlock().getState() : null;
        head.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("7e9a87d4-8caf-40ed-b8b3-98644e8af867")));
        head.update();

        CHECKPOINT_LOCATION.put(loc, p.getName());
    }


    public static void start(Player p, int checkpoints) {
        if (Friedhof.isDead(p)) {
            Friedhof.revive(p, null);
        }

        if (Jail.isInJail(p)) Jail.unarrest(p);

        Location loc = new Location(p.getWorld(), -639, 9, 143);
        loc.getChunk().load();
        p.teleport(loc);

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
        Script.executeAsyncUpdate("DELETE FROM checkpoints WHERE id = " + Script.getNRPID(p));
        clearCheckpoints(p);
        Cache.resetScoreboard(p);
        p.teleport(new Location(Script.WORLD, 587, 69, 991, -268.28235f, -3.7367816f));
        CHECKPOINTS.remove(p.getName());
    }

    public static void clear(OfflinePlayer p) {
        Script.executeAsyncUpdate("DELETE FROM checkpoints WHERE id = " + Script.getNRPID(p));
        CHECKPOINTS.remove(p.getName());
    }

    public static void clearCheckpoints(Player p) {
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
            setScoreboard(p, getCheckpoints(p));
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
                p.sendMessage(Messages.ERROR + "Das ist kein Checkpoint von dir.");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> Checkpoints.checkCheckpoints(e.getPlayer()));
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
