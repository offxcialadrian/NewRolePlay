package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.House.House;
import org.bukkit.ChatColor;
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

import java.util.HashMap;

public class AddHouseDoor implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§eHaustür§8] §e» ";
    private static HashMap<String, House> changing = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.MODERATOR, false) && !Script.hasRankExact(p, Rank.DEVELOPER)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/addhousedoor [Hausnummer]");
            return true;
        }

        if(!Script.isInt(args[0])) {
            p.sendMessage(Messages.ERROR + "Bitte gebe eine Hausnummer an.");
            return true;
        }

        House house = House.getHouseByID(Integer.parseInt(args[0]));
        if(house == null) {
            p.sendMessage(Messages.ERROR + "Haus nicht gefunden.");
            return true;
        }

        if(changing.containsKey(p.getName())) {
            p.sendMessage(PREFIX + "Du hörst nun auf, Türen für Haus " + changing.get(p.getName()).getID() + " hinzuzufügen.");
            changing.remove(p.getName());
            return true;
        }

        changing.put(p.getName(), house);
        p.sendMessage(PREFIX + "Du fügst nun Türen für Haus " + house.getID() + " hinzu.");

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
        if (!Script.hasRank(p, Rank.MODERATOR, false)) return;
        if(!SDuty.isSDuty(p)) return;
        e.setCancelled(true);
        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            Location loc = b.getLocation();
            if (b.getType().equals(Material.OAK_DOOR)) {
                boolean top;
                int house = changing.get(p.getName()).getID();
                top = b.getRelative(BlockFace.DOWN).getType().equals(b.getType());
                Location loc2 = loc.clone().add(0, (top ? -1 : +1), 0);
                Script.executeAsyncUpdate("INSERT INTO house_door (houseID, x, y, z) VALUES (" + house + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ");");
                Script.executeAsyncUpdate("INSERT INTO house_door (houseID, x, y, z) VALUES (" + house + ", " + loc2.getX() + ", " + loc2.getY() + ", " + loc2.getZ() + ");");
                p.sendMessage(PREFIX + "Du hast die Tür hinzugefügt.");
                Script.sendTeamMessage(p, ChatColor.WHITE, "hat eine Haustüre zu Haus " + house + " registriert!", false);
                changing.remove(p.getName());
            }
        }
    }

}
