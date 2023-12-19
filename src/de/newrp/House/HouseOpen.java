package de.newrp.House;

import de.newrp.API.Debug;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HouseOpen implements Listener {
    public static final Set<Integer> OPENED_DOORS = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            if (b == null) return;
            if (b.getType().equals(Material.OAK_DOOR)) {
                Player p = e.getPlayer();
                e.setCancelled(!canOpen(p, e.getClickedBlock()));
            }
        }
    }

    public boolean canOpen(Player p, Block b) {
        if(SDuty.isSDuty(p)) return true;
        if(BuildMode.isInBuildMode(p)) return true;
        if (isPlayersDoor(p, b)) return true;
        return berufsDoor(p, b);
    }

    public boolean isPlayersDoor(Player p, Block b) {
        House house = House.getHouseByDoor(b.getLocation());
        List<House> houses = House.getHouses(Script.getNRPID(p));
        return houses.contains(house);
    }

    public boolean berufsDoor(Player p, Block b) {
        Beruf.Berufe beruf = Beruf.getBeruf(p);
        if(beruf == null) return false;
        ArrayList<Location> locs = beruf.getDoors();
        if (locs != null) {
            for (Location l : locs) {
                if (b.getLocation().equals(l)) return true;
            }
        }
        return false;
    }
}