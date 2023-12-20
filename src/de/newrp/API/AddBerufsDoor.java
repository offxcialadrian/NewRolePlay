package de.newrp.API;

import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.House.House;
import de.newrp.House.HouseRegister;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;

public class AddBerufsDoor implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§eBerufsdoor§8] §e» ";
    private static HashMap<String, Beruf.Berufe> changing = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/addberufsdoor [Beruf]");
            return true;
        }

        Beruf.Berufe beruf = Beruf.Berufe.getBeruf(args[0]);
        if(beruf == null) {
            p.sendMessage(Messages.ERROR + "Beruf nicht gefunden.");
            return true;
        }

        if(changing.containsKey(p.getName())) {
            p.sendMessage(PREFIX + "Du hörst nun auf, Türen für " + changing.get(p.getName()).getName() + " hinzuzufügen.");
            changing.remove(p.getName());
        }

        changing.put(p.getName(), beruf);
        p.sendMessage(PREFIX + "Du fügst nun Türen für " + beruf.getName() + " hinzu.");

        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if(!changing.containsKey(e.getPlayer().getName())) return;
        Action action = e.getAction();
        Player p = e.getPlayer();
        p.getInventory().getItemInMainHand();
            if (p.getInventory().getItemInMainHand().hasItemMeta()) return;
            if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) return;
            if(!SDuty.isSDuty(p)) return;
            e.setCancelled(true);
            if (action == Action.RIGHT_CLICK_BLOCK) {
                Block b = e.getClickedBlock();
                Location loc = b.getLocation();
                if (b.getType().equals(Material.OAK_DOOR)) {
                    boolean top;
                    top = b.getRelative(BlockFace.DOWN).getType().equals(b.getType());
                    Location loc2 = loc.clone().add(0, (top ? -1 : +1), 0);
                    Script.executeAsyncUpdate("INSERT INTO berufsdoor (berufID, x, y, z) VALUES (" + changing.get(p.getName()).getID() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ");");
                    Script.executeAsyncUpdate("INSERT INTO berufsdoor (berufID, x, y, z) VALUES (" + changing.get(p.getName()).getID() + ", " + loc2.getBlockX() + ", " + loc2.getBlockY() + ", " + loc2.getBlockZ() + ");");
                    p.sendMessage(PREFIX + "Du hast die Tür hinzugefügt.");
                    changing.remove(p.getName());
                }
            }
    }
}
