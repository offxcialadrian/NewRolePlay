package de.newrp.API;

import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class BreakOutHandler implements Listener {

    public static final String PREFIX = "§8[§aGefängnis§8] §a" + Messages.ARROW  + " §7";
    private static final Location door = new Location(Script.WORLD, 1034, 69, 577);

    public static long lastTime = 0;


    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.OAK_DOOR) {
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
                if (event.getBlock().getLocation().distance(door) < 1) {
                    if (System.currentTimeMillis() - lastTime < TimeUnit.HOURS.toMillis(1)) {
                        player.sendMessage(PREFIX + "Du kannst die Tür gerade nicht aufbrechen.");
                        return;
                    }

                    toggleDoorState(door.getBlock(), true, true);

                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

                    lastTime = System.currentTimeMillis();
                    Beruf.Berufe.POLICE.sendMessage(PREFIX + "Im Gefängnis wurde ein Alarm verzeichnet!");
                    for (Player target : Script.WORLD.getNearbyPlayers(player.getLocation(), 60))
                        target.sendMessage(PREFIX + "Die Gefängnistür wurde aufgebrochen!");

                    Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), BreakOutHandler::repairDoor, (long) (Script.getRandomFloat(3, 5) * 60 * 20L));
                }
            }
        }
    }

    @EventHandler
    public void onDoor(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.OAK_DOOR) {
                if (event.getClickedBlock().getLocation().distance(door.getBlock().getLocation()) < 1) {
                    BlockState state = event.getClickedBlock().getState();
                    Door door = (Door) state.getBlockData();
                    if (!door.isOpen()) {
                        if (event.getItem().getType() == Material.BLAZE_ROD) {
                            if (System.currentTimeMillis() - lastTime < TimeUnit.HOURS.toMillis(1)) {
                                if (event.getItem() != null) {
                                    event.getPlayer().sendMessage(PREFIX + "Du kannst die Tür gerade nicht aufbrechen.");
                                }
                            } else {
                                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 50 * 20, 1));
                            }
                        }
                    }
                }
            }
        }
    }

    public static void repairDoor() {
        toggleDoorState(door.getBlock(), false, false);
    }

    public static void toggleDoorState(Block block, boolean open, boolean playSound) {
        BlockState state = block.getState();
        Door door = (Door) state.getBlockData();
        door.setOpen(open);
        state.setBlockData(door);
        state.update();
        Debug.debug("Closing jail door");
        if (playSound) {
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
        }
    }
}
