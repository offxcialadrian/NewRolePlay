package de.newrp.Organisationen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Player.AFK;
import de.newrp.Police.Fahndung;
import de.newrp.NewRoleplayMain;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HackPoliceComputer implements CommandExecutor, Listener {
    public static final String prefix = "§8[§9Polizeicomputer§8] §9" + Messages.ARROW + " §7";
    public static final Location LOCATION = new Location(Script.WORLD, 448, 32, 855, -88.80542f, 24.899044f);
    public static final long TIMEOUT = TimeUnit.HOURS.toMillis(3);
    private static final Location doorOne = new Location(Script.WORLD, 412, 67, 818);
    private static final Location doorTwo = new Location(Script.WORLD, 411, 67, 818);

    private static Player hacker;
    public static long lastTime = 0;
    private static int schedulerID = -1;
    private static int doors = 0;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if (p.getLevel() <= 3) {
            return false;
        }

        Organisation f = Organisation.getOrganisation(p);

        if (f == null) return false;

        if (args.length == 0) {
            p.sendMessage(Messages.ERROR + "/hackpolicecomputer [Spieler...]");
            return true;
        }

        long now = System.currentTimeMillis();
        if (lastTime + TIMEOUT > now) {
            p.sendMessage(Messages.ERROR + "Man kann erst in  " + TimeUnit.MILLISECONDS.toMinutes(lastTime + TIMEOUT - now) + " Minuten den Polizeicomputer wieder hacken.");
            return true;
        }

        if (LOCATION.distance(p.getLocation()) > 3) {
            p.sendMessage(Messages.ERROR + "Du befindest nicht in der Nähe des Polizeicomputers.");
            return true;
        }

        if (schedulerID != -1) {
            p.sendMessage(Messages.ERROR + "Der Polizeicomputer wird gerade bereits gehackt.");
            return true;
        }

        List<Player> cops = Beruf.Berufe.POLICE.getMembers().stream()
                .filter(Beruf::hasBeruf)
                .filter(nearbyPlayer -> Beruf.getBeruf(nearbyPlayer).equals(Beruf.Berufe.POLICE))
                .filter(Duty::isInDuty)
                .filter(nearbyPlayer -> !SDuty.isSDuty(nearbyPlayer))
                .filter(nearbyPlayer -> !AFK.isAFK(nearbyPlayer)).collect(Collectors.toList());

        if (cops.size() < 4) {
            p.sendMessage(Messages.ERROR + "§cEs sind nicht 4 Personen der Staatsexekutive anwesend.");
            return true;
        }

        List<Player> players = new ArrayList<>();
        for (String playerName : args) {
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                p.sendMessage(Messages.ERROR + "§c" + playerName + " wurde nicht gefunden.");
                return true;
            }

            if (player.getLocation().distance(LOCATION) > 20) {
                p.sendMessage(Messages.ERROR + Script.getNRPID(player) + " ist zu weit entfernt.");
                return true;
            }

            players.add(player);
        }


        int wanteds = 0;
        for(Player all : players) {
            wanteds += Fahndung.getWanteds(all);
        }

        int lengthInSeconds = calculateDuration(wanteds);

        p.sendMessage(prefix + "Du hast einen Hackversuch gestartet. Geschätzte Dauer: " + lengthInSeconds + " Sekunden.");
        p.sendMessage(Messages.INFO + "Bewege dich nicht mehr als 10 Meter vom Computer weg.");
        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Polizeicomputer wird gehackt! Überprüfe die Personen in der Nähe.");
        hacker = p;

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (schedulerID == -1) return;

                for (Player player : players) {
                    if (!player.isOnline()) continue;
                    if (player.getLocation().distance(LOCATION) > 20) continue;

                    Beruf.Berufe.POLICE.sendMessage(prefix + "Der Polizeicomputer hat die Fahndung von " + Script.getName(player) + " gelöscht.");
                    Organisation.getOrganisation(player).sendMessage(prefix + "Der Polizeicomputer hat die Fahndung von " + Script.getName(player) + " gelöscht.");
                    Fahndung.removeFahndung(player);
                    player.sendMessage(prefix + "Der Polizeicomputer hat deine Akten gelöscht.");
                }

                p.sendMessage(prefix + "Du hast die Akten der Personen erfolgreich gelöscht.");

                schedulerID = -1;
                lastTime = now;
                hacker = null;
            }
        }.runTaskLater(NewRoleplayMain.getInstance(), lengthInSeconds * 20L);
        schedulerID = task.getTaskId();
        return true;
    }

    private int calculateDuration(int wanteds) {
        return (int) (100D + Math.pow(wanteds, 0.7));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (hacker == null) return;
        Player p = e.getPlayer();
        if (p != hacker) return;
        if (p.getLocation().distance(LOCATION) <= 10) return;

        p.sendMessage(prefix + "§cDu hast dich zu weit vom Polizeicomputer entfernt.");

        Bukkit.getScheduler().cancelTask(schedulerID);
        hacker = null;
        schedulerID = -1;
        lastTime = System.currentTimeMillis();

        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
        Organisation.getOrganisation(p).sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (hacker == null) return;

        Player p = e.getPlayer();
        if (p != hacker) return;
        if (e.getTo().distance(LOCATION) <= 10) return;

        p.sendMessage(prefix + "§cDu hast dich zu weit vom Polizeicomputer entfernt.");

        Bukkit.getScheduler().cancelTask(schedulerID);
        hacker = null;
        schedulerID = -1;
        lastTime = System.currentTimeMillis();
        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
        Organisation.getOrganisation(p).sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (hacker == null) return;

        Player p = e.getPlayer();
        if (p != hacker) return;

        Bukkit.getScheduler().cancelTask(schedulerID);
        hacker = null;
        schedulerID = -1;
        lastTime = System.currentTimeMillis();

        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
        Organisation.getOrganisation(p).sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (hacker == null) return;

        Player p = e.getEntity();
        if (p != hacker) return;

        Bukkit.getScheduler().cancelTask(schedulerID);
        hacker = null;
        schedulerID = -1;
        lastTime = System.currentTimeMillis();

        Beruf.Berufe.POLICE.sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
        Organisation.getOrganisation(p).sendMessage(prefix + "Der Hacker konnte sich nicht vollständig in das System einloggen!");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.IRON_DOOR) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
                if (event.getBlock().getLocation().distance(doorOne) < 1 || event.getBlock().getLocation().distance(doorTwo) < 1) {
                    if (Organisation.hasOrganisation(event.getPlayer())) {
                        doors += 1;
                        if (doors >= 2) {
                            toggleDoorState(doorOne.getBlock(), true, true);
                            toggleDoorState(doorTwo.getBlock(), true, true);
                            doors = 0;

                            event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);

                            Beruf.Berufe.POLICE.sendMessage(prefix + "Es wurde ein Einbruch beim Polizeicomputer gemeldet!");
                            Organisation.getOrganisation(event.getPlayer()).sendMessage(prefix + "Die Tür beim Polizeicomputer wurde aufgebrochen!");

                            Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), HackPoliceComputer::repairDoors, TIMEOUT / 2);
                        }
                    }
                }
            }
        }
    }

    public static void repairDoors() {
        toggleDoorState(doorOne.getBlock(), false, false);
        toggleDoorState(doorTwo.getBlock(), false, false);
    }

    public static void toggleDoorState(Block block, boolean open, boolean playSound) {
        BlockState state = block.getState();
        Door door = (Door) state.getBlockData();
        door.setOpen(open);
        state.setBlockData(door);
        state.update();
        Debug.debug("Closing policecomputer door");
        if (playSound) {
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
        }
    }
}