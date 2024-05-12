package de.newrp.Organisationen;

import de.newrp.NewRoleplayMain;
import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Directional;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Bankraub implements CommandExecutor, Listener {

    public static long lastTime;
    private static long cooldown;
    private static final long TIMEOUT = TimeUnit.HOURS.toMillis(5);
    private static final long TIMEOUT_2 = TimeUnit.SECONDS.toMillis(45);
    private static ArrayList<Location> blocks = new ArrayList<>();
    public static String PREFIX = "§8[§9Bankraub§8] §9» §7";
    private static Location[] locs = new Location[]{
            new Location(Script.WORLD, 984, 70, 961),
            new Location(Script.WORLD, 984, 71, 961),
            new Location(Script.WORLD, 984, 71, 962),
            new Location(Script.WORLD, 984, 70, 962),
            new Location(Script.WORLD, 984, 70, 963),
            new Location(Script.WORLD, 984, 71, 963),
            new Location(Script.WORLD, 984, 71, 964),
            new Location(Script.WORLD, 984, 71, 965),
            new Location(Script.WORLD, 984, 70, 965),
            new Location(Script.WORLD, 986, 70, 967),
            new Location(Script.WORLD, 986, 71, 967),
            new Location(Script.WORLD, 988, 70, 967),
            new Location(Script.WORLD, 988, 71, 967),
            new Location(Script.WORLD, 989, 70, 967),
            new Location(Script.WORLD, 989, 71, 967),
            new Location(Script.WORLD, 990, 70, 967),
            new Location(Script.WORLD, 990, 71, 967),
            new Location(Script.WORLD, 991, 71, 967),
            new Location(Script.WORLD, 991, 70, 967),
            new Location(Script.WORLD, 992, 70, 967),
            new Location(Script.WORLD, 992, 71, 967),
            new Location(Script.WORLD, 993, 70, 967),
            new Location(Script.WORLD, 993, 71, 967),
            new Location(Script.WORLD, 994, 71, 967),
            new Location(Script.WORLD, 994, 70, 967),
            new Location(Script.WORLD, 995, 71, 967),
            new Location(Script.WORLD, 995, 70, 967),
            new Location(Script.WORLD, 996, 71, 967),
            new Location(Script.WORLD, 996, 70, 967),
            new Location(Script.WORLD, 997, 71, 967),
            new Location(Script.WORLD, 997, 70, 967),
            new Location(Script.WORLD, 998, 71, 967),
            new Location(Script.WORLD, 998, 70, 967),
            new Location(Script.WORLD, 999, 71, 967),
            new Location(Script.WORLD, 999, 70, 967),
            new Location(Script.WORLD, 1000, 71, 967),
            new Location(Script.WORLD, 1000, 70, 967),
            new Location(Script.WORLD, 1001, 71, 967),
            new Location(Script.WORLD, 1001, 70, 967),
            new Location(Script.WORLD, 1002, 71, 967),
            new Location(Script.WORLD, 1002, 70, 967),
            new Location(Script.WORLD, 1004, 70, 965),
            new Location(Script.WORLD, 1004, 71, 965),
            new Location(Script.WORLD, 1004, 71, 964),
            new Location(Script.WORLD, 1004, 70, 964),
            new Location(Script.WORLD, 1004, 71, 963),
            new Location(Script.WORLD, 1004, 70, 963),
            new Location(Script.WORLD, 1004, 71, 962),
            new Location(Script.WORLD, 1004, 71, 962),
            new Location(Script.WORLD, 1004, 71, 961),
            new Location(Script.WORLD, 1004, 70, 961)
    };
    private static boolean miniGameIsActive;
    private static Player auslöser;
    private static Location bankraub = new Location(Script.WORLD, 994, 70, 963, -0.6021996f, 20.250021f);
    private static Location loc = null;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        long time = System.currentTimeMillis();

        if (!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/bankraub");
            return true;
        }

        if (auslöser != null) {
            p.sendMessage(Messages.ERROR + "Du kannst gerade nicht die Staatsbank ausrauben.");
            return true;
        }

        if (!p.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) {
            p.sendMessage(Messages.ERROR + "Du hast keine Brechstange in der Hand.");
            return true;
        }

        List<Player> cops = Beruf.Berufe.POLICE.getMembers().stream()
                .filter(Beruf::hasBeruf)
                .filter(nearbyPlayer -> Beruf.getBeruf(nearbyPlayer).equals(Beruf.Berufe.POLICE))
                .filter(Duty::isInDuty)
                .filter(nearbyPlayer -> !AFK.isAFK(nearbyPlayer)).collect(Collectors.toList());

        if (cops.size() < 5) {
            p.sendMessage(Messages.ERROR + "Es sind zu wenig Polizisten online um einen Bankraub zu starten.");
            return true;
        }

        if (p.getLocation().distance(bankraub) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht an der Staatsbank.");
            return true;
        }

        if (lastTime + TIMEOUT > time) {
            p.sendMessage(Messages.ERROR + "Du kannst die Staatsbank erst in " + TimeUnit.MILLISECONDS.toMinutes(lastTime + TIMEOUT - time) + " Minuten ausrauben.");
            return true;
        }

        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
        p.sendMessage(PREFIX + "Du hast den Bankraub gestartet.");
        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Die Staatsbank wird ausgeraubt!");
        Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + "Die Staatsbank wird ausgeraubt!");
        Organisation o = Organisation.getOrganisation(p);
        o.sendMessage(PREFIX + Script.getName(p) + " hat einen Bankraub gestartet!");
        Log.NORMAL.write(p, "hat einen Bankraub gestartet");
        auslöser = p;
        cooldown = time;
        miniGameIsActive = true;
        blocks.addAll(Arrays.asList(locs));
        calcLoc();
        startMiniGame();
        p.sendMessage(Messages.INFO + "Klicke nun möglichst schnell den jeweils anders aussehenden Tresor an.");
        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e.getPlayer() == auslöser) {
            stopMiniGame(false);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        long time2 = System.currentTimeMillis();
        Player p = e.getPlayer();
        if (auslöser != p) return;
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.DISPENSER)) {
                loc.getBlock().setType(Material.DISPENSER);
                Block block = loc.getBlock();
                BlockState state = block.getState();
                Directional data = (Directional) state.getData();
                data.setFacingDirection(BlockFace.SOUTH);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        state.update();
                    }
                }.runTaskLater(NewRoleplayMain.getInstance(), 1L);
                stopMiniGame(false);
            } else if (e.getClickedBlock().getType().equals(Material.DROPPER)) {
                if (cooldown + TIMEOUT_2 < time2) {
                    e.getClickedBlock().setType(Material.DISPENSER);
                    Block block = e.getClickedBlock();
                    BlockState state = block.getState();
                    Directional data = (Directional) state.getData();
                    data.setFacingDirection(BlockFace.SOUTH);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            state.update();
                        }
                    }.runTaskLater(NewRoleplayMain.getInstance(), 1L);
                    cooldown = time2;
                    Script.sendActionBar(p, "§aDu hast den richtigen Tresor angeklickt.");
                    calcLoc();
                    startMiniGame();
                } else
                    Script.sendActionBar(p, "§cBitte warte noch " + TimeUnit.MILLISECONDS.toSeconds(cooldown + TIMEOUT_2 - time2) + " Sekunden bis du den nächsten Tresor anklickst.");
            }
        }
    }

    private static void calcLoc() {
        int random = Script.getRandom(0, blocks.size());
        if (blocks.isEmpty()) {
            return;
        }
        int index;
        if (random != 0) {
            index = random - 1;
        } else
            index = random;
        loc = blocks.get(index);
        blocks.remove(index);
        startMiniGame();
    }

    private static void startMiniGame() {
        if (!blocks.isEmpty()) {
            if (loc == null) {
                calcLoc();
                return;
            }
            loc.getBlock().setType(Material.DROPPER);
            Block block = loc.getBlock();
            BlockState state = block.getState();
            Directional data = (Directional) state.getData();
            data.setFacingDirection(BlockFace.SOUTH);
            new BukkitRunnable() {
                @Override
                public void run() {
                    state.update();

                }
            }.runTaskLater(NewRoleplayMain.getInstance(), 1L);
        } else {
            stopMiniGame(true);
        }
    }

    private static void stopMiniGame(boolean win) {
        miniGameIsActive = false;
        blocks.clear();
        Player p = auslöser;
        loc = null;
        Organisation faction = Organisation.getOrganisation(p);
        if (win) {
            p.sendMessage(PREFIX + "Der Bankraub war erfolgreich! Versuche nun zu fliehen.");
            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Staatsbankraub konnte nicht verhindert werden!");
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + "Der Staatsbankraub konnte nicht verhindert werden!");
            faction.sendMessage(PREFIX + "§aDer Bankraub war erfolgreich!");
            Script.sendActionBar(p, "§aDer Bankraub war erfolgreich!");
            int result = Script.getRandom(8000, 12000);
            faction.addExp(result / 100);
            Script.addMoney(Script.getNRPID(p), PaymentType.CASH, result);
            Log.HIGH.write(p, "hat einen Bankraub erfolgreich abgeschlossen und " + result + " € erbeutet.");
            Stadtkasse.removeStadtkasse(result, "Bankraub");
        } else {
            p.sendMessage(PREFIX + "Der Bankraub ist gescheitert!");
            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Staatsbankraub wurde verhindert!");
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + "Der Staatsbankraub wurde verhindert!");
            faction.sendMessage(PREFIX + "§cDer Bankraub ist gescheitert!");
            Script.sendActionBar(p, "§cDer Bankraub ist gescheitert!");
        }
        auslöser = null;
    }
}