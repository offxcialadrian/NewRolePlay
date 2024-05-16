package de.newrp.API;

import de.newrp.Player.Fesseln;
import de.newrp.Police.Handschellen;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Stairs;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Chair implements Listener {
    public static final HashMap<Location, ArmorStand> CHAIRS = new HashMap<>();
    public static final Map<String, Location> ENTER_LOCATION = new HashMap<>();
    public static final ArrayList<String> NO_TELEPORT = new ArrayList<>();

    public static boolean sitsOnChair(Player p) {
        for (ArmorStand stand : CHAIRS.values()) {
            if (stand.getPassengers().contains(p)) {
                return true;
            }
        }
        return false;
    }

    public static void spawnChair(Location loc, Player p, float yaw, boolean chair) {
        Location originalLocation = p.getLocation();

        Location block_location = loc.clone();
        loc.setYaw(yaw);
        if (chair) {
            loc.add(.5, -1.15, .5);
        } else {
            loc.add(0, -1.1, 0);
        }
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setVisible(false);
        //stand.setCanMove(false);
        if (chair) {
            stand.setCustomName("chair");
            stand.setCustomNameVisible(false);
        }
        stand.addPassenger(p);

        CHAIRS.put(block_location, stand);
        ENTER_LOCATION.put(p.getName(), originalLocation);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        Player p = e.getPlayer();
        if (p.isSneaking()) return;

        Block b = e.getClickedBlock();
        if (!canSit(p, b) || !Script.isInRange(p.getLocation(), b.getLocation(), 1.9D)) return;
        if(b instanceof Stairs) {

        }
        Stairs u = (Stairs) b.getState().getData();
        BlockFace F = u.getFacing().getOppositeFace();
        if (chairIsUsed(b.getLocation())) return;
        if (F == BlockFace.EAST) {
            spawnChair(b.getLocation(), p, 90F, true);
        } else if (F == BlockFace.SOUTH) {
            spawnChair(b.getLocation(), p, 180F, true);
        } else if (F == BlockFace.WEST) {
            spawnChair(b.getLocation(), p, -90F, true);
        } else if (F == BlockFace.NORTH) {
            spawnChair(b.getLocation(), p, 0F, true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getEntity();
            if (sitsOnChair(p)) {
                ArmorStand stand = (ArmorStand) p.getVehicle();
                if (stand != null) {
                    removeChair(stand);
                    if (stand.getCustomName() != null) {
                        teleportFromChair(p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (sitsOnChair(p)) {
            ENTER_LOCATION.remove(p.getName());
            p.leaveVehicle();
            ArmorStand stand = (ArmorStand) p.getVehicle();
            if (stand != null) {
                removeChair(stand);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (sitsOnChair(p)) {
            ArmorStand stand = (ArmorStand) p.getVehicle();
            if (stand != null) {
                removeChair(stand);
                if (stand.getCustomName() != null) {
                    teleportFromChair(p);
                }
            }
        }
        NO_TELEPORT.remove(p.getName());
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (e.getDismounted() == null) return;
        if (!e.getEntity().getType().equals(EntityType.PLAYER)) return;
        if (!e.getDismounted().getType().equals(EntityType.ARMOR_STAND)) return;

        ArmorStand stand = (ArmorStand) e.getDismounted();
        if (stand == null) return;

        removeChair(stand);
        if (stand.getCustomName() == null) return;

        Player p = (Player) e.getEntity();
        teleportFromChair(p);
    }

    public void teleportFromChair(Player p) {
        if (NO_TELEPORT.remove(p.getName())) return;

        final Location l = p.getLocation().add(0, 1.2D, 0);
        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
            Location loc = l;

            // UC-104: check whether a block will block the player
            Block obstructingBlock = p.getEyeLocation().add(0, 1.2D, 0).getBlock();
            if (obstructingBlock != null && obstructingBlock.getType().isSolid()) {
                Location originalLocation = ENTER_LOCATION.remove(p.getName());
                if (originalLocation != null) {
                    loc = originalLocation;
                }
            }

            loc.setYaw(p.getLocation().getYaw());
            loc.setPitch(p.getLocation().getPitch());
            p.teleport(loc);
        }, 3L);
    }

    public boolean chairIsUsed(Location loc) {
        return CHAIRS.containsKey(loc);
    }

    public void removeChair(ArmorStand stand) {
        Iterator<Entry<Location, ArmorStand>> it = CHAIRS.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Location, ArmorStand> ent = it.next();
            ArmorStand current = ent.getValue();
            if (stand.getEntityId() == current.getEntityId()) {
                current.remove();
                it.remove();
                return;
            }
        }
    }

    @SuppressWarnings("deprecation")
    public boolean canSit(Player p, Block b) {
        if (p.isInsideVehicle()) return false;
        if (Script.isInAir(p)) return false;
        if (b.getData() > 3) return false;
        if (Friedhof.isDead(p)) return false;
        if (CHAIRS.containsKey(b.getLocation())) return false;
        if (Handschellen.isCuffed(p) || Fesseln.isTiedUp(p)) return false;
        //if (!(b.getBlockData() instanceof Stairs)) return false;
        //if (((Stairs) b.getBlockData()).getFacing() == BlockFace.DOWN) return false;
        if (!b.getRelative(BlockFace.UP).getType().equals(Material.AIR) || b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR))
            return false;
        if (!p.isOnGround()) return false;
        return true;
    }
}