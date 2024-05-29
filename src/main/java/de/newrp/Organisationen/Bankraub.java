package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import net.minecraft.server.v1_16_R3.BlockKelp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Bankraub implements CommandExecutor, Listener {

    public static long lastTime = 0L;
    private static long cooldown;
    public static final long BANKROB_COOLDOWN = TimeUnit.HOURS.toMillis(5);
    private static final long BLOCK_CLICK_DELAY = TimeUnit.SECONDS.toMillis(30);
    private static ArrayList<Location> blocks = new ArrayList<>();
    public static String PREFIX = "§8[§9Bankraub§8] §9» §7";
    private static Location[] locs = new Location[]{
            // Wall West
            new Location(Script.WORLD, 984, 70, 961, 90, 0),
            new Location(Script.WORLD, 984, 71, 961, 90, 0),
            new Location(Script.WORLD, 984, 71, 962, 90, 0),
            new Location(Script.WORLD, 984, 70, 962, 90, 0),
            new Location(Script.WORLD, 984, 70, 963, 90, 0),
            new Location(Script.WORLD, 984, 71, 963, 90, 0),
            new Location(Script.WORLD, 984, 71, 964, 90, 0),
            new Location(Script.WORLD, 984, 71, 965, 90, 0),
            new Location(Script.WORLD, 984, 70, 965, 90, 0),

            // Wall East
            new Location(Script.WORLD, 1004, 70, 965, -90, 0),
            new Location(Script.WORLD, 1004, 71, 965, -90, 0),
            new Location(Script.WORLD, 1004, 71, 964, -90, 0),
            new Location(Script.WORLD, 1004, 70, 964, -90, 0),
            new Location(Script.WORLD, 1004, 71, 963, -90, 0),
            new Location(Script.WORLD, 1004, 70, 963, -90, 0),
            new Location(Script.WORLD, 1004, 71, 962, -90, 0),
            new Location(Script.WORLD, 1004, 71, 962, -90, 0),
            new Location(Script.WORLD, 1004, 71, 961, -90, 0),
            new Location(Script.WORLD, 1004, 70, 961, -90, 0),

            // Wall South
            new Location(Script.WORLD, 986, 70, 967, 0, 0),
            new Location(Script.WORLD, 986, 71, 967, 0, 0),
            new Location(Script.WORLD, 988, 70, 967, 0, 0),
            new Location(Script.WORLD, 988, 71, 967, 0, 0),
            new Location(Script.WORLD, 989, 70, 967, 0, 0),
            new Location(Script.WORLD, 989, 71, 967, 0, 0),
            new Location(Script.WORLD, 990, 70, 967, 0, 0),
            new Location(Script.WORLD, 990, 71, 967, 0, 0),
            new Location(Script.WORLD, 991, 71, 967, 0, 0),
            new Location(Script.WORLD, 991, 70, 967, 0, 0),
            new Location(Script.WORLD, 992, 70, 967, 0, 0),
            new Location(Script.WORLD, 992, 71, 967, 0, 0),
            new Location(Script.WORLD, 993, 70, 967, 0, 0),
            new Location(Script.WORLD, 993, 71, 967, 0, 0),
            new Location(Script.WORLD, 994, 71, 967, 0, 0),
            new Location(Script.WORLD, 994, 70, 967, 0, 0),
            new Location(Script.WORLD, 995, 71, 967, 0, 0),
            new Location(Script.WORLD, 995, 70, 967, 0, 0),
            new Location(Script.WORLD, 996, 71, 967, 0, 0),
            new Location(Script.WORLD, 996, 70, 967, 0, 0),
            new Location(Script.WORLD, 997, 71, 967, 0, 0),
            new Location(Script.WORLD, 997, 70, 967, 0, 0),
            new Location(Script.WORLD, 998, 71, 967, 0, 0),
            new Location(Script.WORLD, 998, 70, 967, 0, 0),
            new Location(Script.WORLD, 999, 71, 967, 0, 0),
            new Location(Script.WORLD, 999, 70, 967, 0, 0),
            new Location(Script.WORLD, 1000, 71, 967, 0, 0),
            new Location(Script.WORLD, 1000, 70, 967, 0, 0),
            new Location(Script.WORLD, 1001, 71, 967, 0, 0),
            new Location(Script.WORLD, 1001, 70, 967, 0, 0),
            new Location(Script.WORLD, 1002, 71, 967, 0, 0),
            new Location(Script.WORLD, 1002, 70, 967, 0, 0)
    };
    private static boolean miniGameIsActive;
    private static Player bankRobberPlayer;
    private static Location bankraub = new Location(Script.WORLD, 994, 70, 963, -0.6021996f, 20.250021f);
    private static Location loc = null;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        long time = System.currentTimeMillis();
        final Organisation organisation = Organisation.getOrganisation(p);

        if (organisation == null) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/bankraub");
            return true;
        }

        if (bankRobberPlayer != null) {
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

        if (cops.size() < 5 && !Script.isInTestMode()) {
            p.sendMessage(Messages.ERROR + "Es sind zu wenig Polizisten online um einen Bankraub zu starten.");
            return true;
        }

        if (p.getLocation().distance(bankraub) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht an der Staatsbank.");
            return true;
        }

        if (lastTime + BANKROB_COOLDOWN > time) {
            p.sendMessage(Messages.ERROR + "Du kannst die Staatsbank erst in " + TimeUnit.MILLISECONDS.toMinutes(lastTime + BANKROB_COOLDOWN - time) + " Minuten ausrauben.");
            return true;
        }

        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
        p.sendMessage(PREFIX + "Du hast den Bankraub gestartet.");
        for (final Beruf.Berufe faction : Beruf.Berufe.values()) {
            if(faction == Beruf.Berufe.NEWS) continue;

            faction.sendMessage(PREFIX + "Die Staatsbank wird ausgeraubt!");
            if(faction != Beruf.Berufe.POLICE) continue;
            for (UUID uuid : faction.getMember()) {
                final Player player = Bukkit.getPlayer(uuid);
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
                player.sendTitle("§9§lBankraub", "§eEs wurde ein Bankraub gestartet! Versucht die Bankräuber zu stoppen!", 10, 80, 40);
            }
        }

        resetAllBlocks();
        organisation.sendMessage(PREFIX + Script.getName(p) + " hat einen Bankraub gestartet!");
        Log.NORMAL.write(p, "hat einen Bankraub gestartet");
        bankRobberPlayer = p;
        cooldown = time;
        lastTime = time;
        miniGameIsActive = true;
        blocks.addAll(Arrays.asList(locs));
        startMiniGame();
        p.sendMessage(Messages.INFO + "Klicke nun möglichst schnell den jeweils anders aussehenden Tresor an.");
        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(bankRobberPlayer == null) return;

        if (e.getPlayer().getUniqueId().toString().equalsIgnoreCase(bankRobberPlayer.getUniqueId().toString())) {
            stopMiniGame(false);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) return;
        final Player player = e.getPlayer();

        final long currentTimeMillis = System.currentTimeMillis();
        if (bankRobberPlayer != player) return;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(Material.DISPENSER)) {
                updateBlockFacingOnYaw(loc, Material.DISPENSER);
                stopMiniGame(false);
            } else if (e.getClickedBlock().getType().equals(Material.DROPPER)) {
                if (cooldown + BLOCK_CLICK_DELAY < currentTimeMillis) {
                    updateBlockFacingOnYaw(loc, Material.DISPENSER);
                    cooldown = currentTimeMillis;
                    Script.sendActionBar(player, "§aDu hast den richtigen Tresor angeklickt.");

                    calcLoc();
                    Debug.debug("Clicked the right block.. starting next, " + blocks.size() + " left.");
                    startMiniGame();
                } else
                    Script.sendActionBar(player, "§cBitte warte noch " + TimeUnit.MILLISECONDS.toSeconds(cooldown + BLOCK_CLICK_DELAY - currentTimeMillis) + " Sekunden bis du den nächsten Tresor anklickst.");
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
        loc =  blocks.remove(index);
        System.out.println("remaining: " + blocks.size());
        startMiniGame();
    }

    private static void startMiniGame() {
        if (!blocks.isEmpty()) {
            if (loc == null) {
                calcLoc();
                return;
            }

            loc.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            updateBlockFacingOnYaw(loc, Material.DROPPER);
        } else {
            stopMiniGame(true);
        }
    }

    private static void stopMiniGame(boolean win) {
        miniGameIsActive = false;
        blocks.clear();
        Player p = bankRobberPlayer;
        loc = null;
        Organisation faction = Organisation.getOrganisation(p);
        if (win) {
            faction.sendMessage(PREFIX + "Der Bankraub war erfolgreich! Versuche nun zu fliehen.");
            for (final Beruf.Berufe berufe : Beruf.Berufe.values()) {
                if(berufe == Beruf.Berufe.NEWS) continue;

                berufe.sendMessage(PREFIX + "Der Staatsbankraub konnte nicht verhindert werden!");
                for (UUID uuid : berufe.getMember()) {
                    Bukkit.getPlayer(uuid).sendTitle("§c§lBankraub nicht verhindert", "§7Die Räuber wurden nicht gestoppt und die Staatsbank erfolgreich überfallen", 10, 80, 40);
                }
            }

            int result = Script.getRandom(8000, 12000);
            faction.addExp(result / 100);
            Script.addMoney(Script.getNRPID(p), PaymentType.CASH, result);
            Log.HIGH.write(p, "hat einen Bankraub erfolgreich abgeschlossen und " + result + " € erbeutet.");
            Stadtkasse.removeStadtkasse(result, "Bankraub");
            bankraub.getWorld().playSound(bankraub, Sound.UI_TOAST_CHALLENGE_COMPLETE, 5f, 1f);
        } else {
            for (UUID uuid : faction.getMember()) {
                Bukkit.getPlayer(uuid).sendTitle("§c§lBankraub fehlgeschlagen", "§e" + Script.getName(bankRobberPlayer) + " §7hat den Bankraub nicht geschafft.", 20, 80, 40);
            }
            faction.sendMessage(PREFIX + "Der Bankraub ist gescheitert!");
            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Staatsbankraub wurde verhindert!");
            Beruf.Berufe.RETTUNGSDIENST.sendMessage(PREFIX + "Der Staatsbankraub wurde verhindert!");
            Script.sendActionBar(p, "§cDer Bankraub ist gescheitert!");
            bankraub.getWorld().playSound(bankraub, Sound.ENTITY_WITHER_DEATH, 5f, 1f);
        }
        bankRobberPlayer = null;
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if(bankRobberPlayer == null) return;

        if (bankRobberPlayer.getUniqueId() == event.getEntity().getUniqueId()) {
            stopMiniGame(false);
        }
    }

    private static BlockFace getBlockFaceBasedOnYaw(final int yaw) {
        switch (yaw) {
            case 0:
                Debug.debug("Yaw is 0, defaulting to NORTH.");
                return BlockFace.NORTH;
            case -90:
                Debug.debug("Yaw is -90, defaulting to SOUTH.");
                return BlockFace.WEST;
            case 90:
                Debug.debug("Yaw is 90, defaulting to EAST.");
                return BlockFace.EAST;
            default:
                Debug.debug("Couldnot find a block face for yaw " + yaw + "! Defaulting to DOWN.");
                return BlockFace.DOWN;
        }
    }

    private static void updateBlockFacingOnYaw(final Location location, final Material material) {
        final Block block = location.getBlock();
        block.setType(material);
        final BlockData blockData = block.getBlockData();
        if(blockData instanceof Directional) {
            ((Directional) blockData).setFacing(getBlockFaceBasedOnYaw((int) location.getYaw()));
            block.setBlockData(blockData);
        }
    }

    private static void resetAllBlocks() {
        if(blocks == null) return;

        for (Location block : blocks) {
            updateBlockFacingOnYaw(block, Material.DISPENSER);
        }
    }
}