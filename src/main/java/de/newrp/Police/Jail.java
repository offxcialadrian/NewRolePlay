package de.newrp.Police;

import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Jail {

    public static final HashMap<String, Jail> JAIL = new HashMap<>();
    public static final String PREFIX = "§8[§6Gefängnis§8] §6" + Messages.ARROW  + " §7";

    private final int userID;
    private final String username;
    private long arresttime;
    private int duration;
    private int taskID;

    public Jail(int userID, String username, long arresttime, int duration) {
        this.userID = userID;
        this.username = username;
        this.arresttime = arresttime;
        this.duration = duration;
        this.taskID = 0;
    }

    public static Jail getJail(Player p) {
        return JAIL.get(p.getName());
    }

    public static boolean isInJail(Player p) {
        if (JAIL.containsKey(p.getName())) {
            Jail j = getJail(p);
            if ((j.getArrestTime() + TimeUnit.SECONDS.toMillis(j.getDuration())) >= System.currentTimeMillis()) {
                return p.getLocation().distance(new Location(Script.WORLD, 1031, 60, 553)) < 50;
            }
        }
        return false;
    }

    public static int getJailtimeDatabase(Player p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT time FROM jail WHERE nrp_id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void removeJailTime(Player p, int seconds) {
        Jail j = getJail(p);
        if (j == null) return;

        int left = j.getJailtimeLeft();
        if (left > seconds) {
            int taskID = j.getTaskID();
            Bukkit.getScheduler().cancelTask(taskID);

            taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(NewRoleplayMain.getInstance(), () -> {
                if (p.isOnline()) if (isInJail(p)) unarrest(p);
            }, (left - seconds) * 20L);
            j.setTaskID(taskID);
            j.setDuration(j.getDuration() - seconds);
        } else {
            unarrest(p);
        }
    }

    public static void arrest(Player p, int time, boolean msg) {
        if (JAIL.containsKey(p.getName())) {
            Jail j = JAIL.get(p.getName());
            if (j.getTaskID() != 0) Bukkit.getScheduler().cancelTask(j.getTaskID());
            JAIL.remove(p.getName());
        }
        final int taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(NewRoleplayMain.getInstance(), () -> {
            if (p.isOnline()) if (isInJail(p)) unarrest(p);
        }, time * 20L);
        Jail j = new Jail(Script.getNRPID(p), p.getName(), System.currentTimeMillis(), time);
        j.setTaskID(taskID);
        JAIL.put(p.getName(), j);

        Chair.NO_TELEPORT.add(p.getName());
        if (p.isInsideVehicle()) p.getVehicle().leaveVehicle();
        Script.removeWeapons(p);

        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.teleport(new Location(Script.WORLD, 1032, 69, 556, -180.42438f, 6.574746f));


        if (msg) p.sendMessage(PREFIX + "Du bist nun für " + (time / 60) + " Minuten im Gefängnis.");
        p.sendMessage(Messages.INFO + "Mit \"/jailtime\" kannst du sehen, wie lange du noch im Gefängnis bist.");
        Script.unfreeze(p);
        Route.invalidate(p);
    }

    public static void unarrest(Player p) {
        Jail j = getJail(p);
        if (j == null) return;
        Script.executeAsyncUpdate("DELETE FROM jail WHERE nrp_id=" + Script.getNRPID(p));
        p.sendMessage(PREFIX + "Du bist wieder frei!");
        p.getInventory().remove(Material.COBBLESTONE);
        p.getInventory().remove(Material.STONE);
        JAIL.remove(p.getName());

        Chair.NO_TELEPORT.add(p.getName());
        if (p.getVehicle() != null) p.leaveVehicle();
        if (Friedhof.isDead(p)) {
            World w = p.getWorld();
            Location[] locs = locs = new Location[]{new Location(Script.WORLD, 1018, 68, 548, 358.74432f, -1.3718445f)};
            Location loc = locs[Script.getRandom(0, 0)];
            loc.getChunk().load();

            Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> p.teleport(loc), 5);
        } else {
            p.teleport(new Location(Script.WORLD, 1018, 68, 548, 358.74432f, -1.3718445f));
        }

    }

    public int getUserID() {
        return this.userID;
    }

    public String getUsername() {
        return this.username;
    }

    public long getArrestTime() {
        return this.arresttime;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int seconds) {
        this.duration = seconds;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getJailtimeLeft() {
        long arresttime = this.arresttime;
        long current = System.currentTimeMillis();

        return this.duration - (int) TimeUnit.MILLISECONDS.toSeconds(current - arresttime);
    }

    public void setArresttime(long time) {
        this.arresttime = time;
    }


}
