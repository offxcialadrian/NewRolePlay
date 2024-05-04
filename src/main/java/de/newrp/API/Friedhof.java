package de.newrp.API;

import de.newrp.Administrator.Checkpoints;
import de.newrp.Berufe.AcceptNotruf;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Berufe.Equip;
import de.newrp.GFB.Schule;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Player.Notruf;
import de.newrp.Player.Spawnchange;
import de.newrp.Police.StartTransport;
import de.newrp.Main;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Friedhof {

    public static final HashMap<String, Friedhof> FRIEDHOF = new HashMap<>();
    private static final HashMap<String, Double> progress = new HashMap<>();

    private final int userID;
    private final String username;
    private final Location deathLocation;
    private final long deathTime;
    private final int cash;
    private final ItemStack[] inventory;
    private int duration; //In Seconds
    private int taskID;
    private int skullTaskID;
    private int helpCounter;

    public Friedhof(int userID, String username, Location deathLocation, long deathTime, int duration, int cash, ItemStack[] inventory) {
        this.userID = userID;
        this.username = username;
        this.deathLocation = deathLocation;
        this.deathTime = deathTime;
        this.duration = duration;
        this.cash = cash;
        this.inventory = inventory;
    }

    public static void setDead(Player p, Friedhof f) {
        if (FRIEDHOF.containsKey(p.getName())) {
            Friedhof old_f = FRIEDHOF.get(p.getName());
            if (old_f.getTaskID() != 0) {
                Bukkit.getScheduler().cancelTask(old_f.getTaskID());
            }
        }
        if(Schule.STUDIYING.containsKey(p)) {
            p.sendMessage(Schule.PREFIX + "Du hast den Kurs nicht bestanden.");
            Schule.STUDIYING.remove(p);
            Schule.STARTED.remove(p);
        }

        final int taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            if (p.isOnline()) revive(p, null);
        }, f.getDeathtimeLeft() * 20L);
        f.setTaskID(taskID);


        if(Beruf.getBeruf(p) == Beruf.Berufe.POLICE) {
            Script.setLastDeadOfficer(System.currentTimeMillis());
        }

        if(StartTransport.executor != null) {
            if(StartTransport.executor == p) {
                Bukkit.broadcastMessage("§8[§6News§8] §6" + Messages.ARROW + "Der Waffentransport der Polizei ist fehlgeschlagen. Bitte passen Sie auf sich auf.");
                Beruf.getBeruf(p).sendMessage("§8[§6Waffentransport§8] §6" + Messages.ARROW + "Der Waffentransport ist fehlgeschlagen.");
                StartTransport.executor = null;
                StartTransport.isActive = false;
                StartTransport.LEVEL = 0;
                StartTransport.add = 0;
                Player killer = p.getKiller();
                killer.getInventory().addItem(Equip.Stuff.SCHWERE_SCHUTZWESTE.getItem());
                killer.getInventory().addItem(Equip.Stuff.EINSATZSCHILD.getItem());
                killer.getInventory().addItem(Equip.Stuff.EINSAZTZSCHILD_2.getItem());
                killer.getInventory().addItem(Equip.Stuff.TAZER.getItem());
            }
        }

        if(AcceptNotruf.accept.containsKey(p)) {
            AcceptNotruf.reOpenNotruf(p);
        }

        if(Notruf.call.containsKey(p)) {
            AcceptNotruf.deleteNotruf(p);
        }

        FRIEDHOF.put(p.getName(), f);
        Location[] locs;

        Debug.debug("dead: " + p.getName() + " " + p.getKiller() + " " + p.getLastDamageCause().getCause().name());
        p.getKiller().sendMessage(Messages.INFO + "§c§lKILL! §6" + Script.getName(p) + " §fdu hast getötet!");
        World w = p.getWorld();
            p.setPlayerWeather(WeatherType.DOWNFALL);
            locs = new Location[]{new Location(Script.WORLD, 222, 75, 673, 92.5503f, -2.699904f)};

        Location loc = locs[Script.getRandom(0, locs.length - 1)];
        Debug.debug("loc: " + loc);
        p.teleport(loc);

        /*p.getKiller().sendMessage(Messages.INFO + "§c§lKILL! §6" + Script.getName(p) + " §fdu hast getötet!");

        if (Organisation.hasOrganisation(p) && Organisation.hasOrganisation(p.getKiller()) && Organisation.getOrganisation(p) == Organisation.getOrganisation(p.getKiller())) {
            Organisation.getOrganisation(p).sendMessage(Messages.INFO + "§c§lMATEKILL! §6" + Script.getName(p.getKiller()) + " §fhat §6" + Script.getName(p) + " §fgetötet.");
        }
        if (Beruf.hasBeruf(p) && Beruf.hasBeruf(p.getKiller()) && Beruf.getBeruf(p) == Beruf.getBeruf(p.getKiller())) {
            Beruf.getBeruf(p).sendMessage(Messages.INFO + "§c§lMATEKILL! §6" + Script.getName(p.getKiller()) + " §fhat §6" + Script.getName(p) + " §fgetötet.");
        }*/

        p.setPlayerWeather(WeatherType.DOWNFALL);
        Script.resetPotionEffects(p);
        p.setFireTicks(0);
        p.setNoDamageTicks((f.getDuration() * 20));
        int left = f.getDeathtimeLeft();
        if (left > 60) {
            int min = left / 60;
            int sec = left - (min * 60);
            if (sec == 0) {
                p.sendMessage("§8Du bist nun für " + min + " " + (min == 1 ? "Minute" : "Minuten") + " auf dem Friedhof.");
            } else {
                p.sendMessage("§8Du bist nun für " + min + " " + (min == 1 ? "Minute" : "Minuten") + " und " + sec + " Sekunden auf dem Friedhof.");
            }
        } else if (left == 60) {
            p.sendMessage("§8Du bist nun für eine Minute auf dem Friedhof.");
        } else {
            p.sendMessage("§8Du bist nun für " + left + " Sekunden auf dem Friedhof.");
        }
        Route.invalidate(p);
        int min = left / 60;
        int sec = left - (min * 60);
        Script.updateListname(p);
        progress.put(p.getName(), 0.0);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isDead(p)) {
                    progress.replace(p.getName(), progress.get(p.getName()) + 1);
                    progressBar((GangwarCommand.isInGangwar(p)?4*60:16*60), p, f);
                } else {
                    progress.remove(p.getName());
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    public static void revive(Player p, Location teleportLoc) {
        Friedhof f = getDead(p);
        if (f == null) return;
        if(Corpse.npcMap.containsKey(p)) Corpse.removeNPC(p);
        if(teleportLoc == null) Duty.removeDuty(p);

        Bukkit.getScheduler().cancelTask(f.getTaskID());
        FRIEDHOF.remove(p.getName());

        int id = f.getUserID();
        Script.executeAsyncUpdate("DELETE FROM friedhof WHERE id = " + id);

        Script.sendActionBar(p, "§eDu lebst nun wieder.");
        p.resetPlayerWeather();
        Script.resetPotionEffects(p);

        Chair.NO_TELEPORT.add(p.getName());
        if (p.isInsideVehicle()) p.leaveVehicle();

        p.setSaturation(20f);
        p.setFoodLevel(20);
        p.setNoDamageTicks(0);
        p.setFireTicks(0);
        Log.NORMAL.write(p, "lebt nun wieder.");
            if (teleportLoc != null) {
                p.setHealth(20D);
                p.teleport(teleportLoc);

                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 12 * 20, 2, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 12 * 20, 2, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 15 * 20, 2, false, false));
            } else {
                if(GangwarCommand.isInGangwar(p)) {
                    p.teleport(GangwarCommand.getRandomSpawnLocFromGangwar(p));
                    GangwarCommand.giveGangwarEquip(p);
                    p.sendMessage(Messages.INFO + "Du wurdest automatisch zum Gangwar teleportiert.");
                } else if(Spawnchange.getSpawnLoc(p) != null) {
                    p.teleport(Spawnchange.getSpawnLoc(p));
                } else {
                    p.teleport(new Location(p.getWorld(), 278,75,1232,270.05463f,0.84689033f));
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(Messages.INFO + "Du lebst nun wieder!");
                    }
                }.runTaskLater(Main.getInstance(), 20L * 2);
                p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

                if(!GangwarCommand.isInGangwar(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 70, 1, false, false));
                }

            }
            if(Checkpoints.hasCheckpoints(p)) {
                p.teleport(new Location(Script.WORLD, 485, 9, 562, -269.20435f, 6.000005f));
            }
        }

    public static Friedhof getDead(Player p) {
        return FRIEDHOF.get(p.getName());
    }

    public static boolean isDead(Player p) {
        if (FRIEDHOF.containsKey(p.getName())) {
            Friedhof f = FRIEDHOF.get(p.getName());
            return (f.getDeathTime() + TimeUnit.SECONDS.toMillis(f.getDuration())) >= System.currentTimeMillis();
        }
        return false;
    }

    public static void removeDeathTime(Player p, int seconds) {
        Friedhof f = getDead(p);
        if (f == null) return;

        int left = f.getDeathtimeLeft();
        if (left > seconds) {
            int taskID = f.getTaskID();
            Bukkit.getScheduler().cancelTask(taskID);

            taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                if (p.isOnline()) revive(p, null);
            }, (left - seconds) * 20L);
            f.setTaskID(taskID);
            f.setDuration(f.getDuration() - seconds);
        } else {
            revive(p, null);
        }
    }

    public void addDeathTime(Player p, int seconds) {
        if (seconds <= 0) return;
        int left = this.getDeathtimeLeft();

        Bukkit.getScheduler().cancelTask(taskID);
        taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            if (p.isOnline()) revive(p, null);
        }, (left + seconds) * 20L);


        int secondsOnGraveyard = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - deathTime);

        duration += seconds;
    }

    public static int getDeathtimeDatabase(Player p) {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT time FROM friedhof WHERE id=" + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*public static Friedhof getDeathByItem(Item i) {
        if (i == null) return null;
        for (Friedhof f : FRIEDHOF.values()) {
            Item skull = f.getSkull();

            if (i.equals(skull)) return f;

            ItemMeta itemMeta = i.getItemStack().getItemMeta();
            if (itemMeta == null) continue;

            String displayName = itemMeta.getDisplayName();
            if (displayName == null) continue;
            if (displayName.length() < 3) continue;

            if (displayName.substring(3).equalsIgnoreCase(f.getUsername()))
                return f;
        }
        return null;
    }*/

    public static Friedhof getDeathByLocation(Location loc) {
        for (Friedhof f : FRIEDHOF.values()) {
            if (Script.isInRange(loc, f.getDeathLocation(), 3.0)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Friedhof{" +
                "userID=" + userID +
                ", username='" + username + '\'' +
                ", deathLocation=" + deathLocation +
                ", deathTime=" + deathTime +
                ", cash=" + cash +
                ", inventory=" + Arrays.toString(inventory) +
                ", duration=" + duration +
                ", taskID=" + taskID +
                ", skullTaskID=" + skullTaskID +
                ", helpCounter=" + helpCounter +
                '}';
    }

    public int getUserID() {
        return this.userID;
    }

    public String getUsername() {
        return this.username;
    }

    public Location getDeathLocation() {
        return this.deathLocation;
    }

    public long getDeathTime() {
        return this.deathTime;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int seconds) {
        this.duration = seconds;
    }


    public int getCash() {
        return this.cash;
    }

    public ItemStack[] getInventoryContent() {
        return this.inventory;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getSkullTaskID() {
        return this.skullTaskID;
    }

    public void setSkullTaskID(int taskID) {
        this.skullTaskID = taskID;
    }

    public int getHelpCounter() {
        return this.helpCounter;
    }

    public void setHelpCounter(int helpCounter) {
        this.helpCounter = helpCounter;
    }

    public int getDeathtimeLeft() {
        long current = System.currentTimeMillis();
        return this.duration - (int) TimeUnit.MILLISECONDS.toSeconds(current - this.deathTime);
    }

    private static void progressBar(double required_progress, Player p, Friedhof f) {
        double current_progress = progress.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§a▉");
            } else {
                sb.append("§8▉");
            }
        }
        String lefttime;
        int left = f.getDeathtimeLeft();
        if (left > 60) {
            int min = left / 60;
            int sec = left - (min * 60);
            if (sec == 0) {
                lefttime = min + " " + (min == 1 ? "Minute" : "Minuten");
            } else {
                lefttime = min + " " + (min == 1 ? "Minute" : "Minuten") + " und " + sec + " Sekunden";
            }
        } else if (left == 60) {
            lefttime = "eine Minute";
        } else {
            lefttime = left + " Sekunden";
        }
        progress.replace(p.getName(), progress.get(p.getName())+1.0);
        Script.sendActionBar(p, "§cTOT! §8» §a" + sb.toString() + " §8× §7" + lefttime);
    }
}
