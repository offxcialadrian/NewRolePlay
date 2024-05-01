package de.newrp.Player;

import de.newrp.API.Chair;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SitCommand implements CommandExecutor {

    public final Material[] BLOCKED_MATERIAL = new Material[]{Material.OAK_DOOR, Material.IRON_DOOR, Material.IRON_TRAPDOOR, Material.GLASS, Material.GLASS_PANE};

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (!canSit(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst dich gerade nicht hinsetzen.");
        } else {
            Chair.spawnChair(p.getLocation().clone().add(0, -.55, 0), p, p.getLocation().getYaw(), false);
        }
        return true;
    }

    public boolean canSit(Player p) {
        if (p.isInsideVehicle()) return false;
        if (Script.isInAir(p)) return false;
        if (p.getFallDistance() > 2) return false;
        if (p.getLocation().getBlock().getType().isSolid()) return false;
        if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) return false;
        if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType().isSolid())
            return false;

        List<Location> blocks = Script.getBlocksAroundLocation(p.getLocation(), 3, 3, false, false, 0);
        for (Location l : blocks) {
            Material l_mat = l.getBlock().getType();
            for (Material blocked : BLOCKED_MATERIAL) {
                if (l_mat == blocked) {
                    return false;
                }
            }
        }
        return true;
    }
}