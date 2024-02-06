package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Schwarzmarkt;
import de.newrp.API.Script;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

public class ButcherCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§cButcher§8] §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        if(!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            for(Entity e : p.getWorld().getEntities()) {
                if(e.getLocation().distance(p.getLocation()) <10)
                    e.remove();
            }
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/removeentities");
            return true;
        }

        for(Entity e : p.getWorld().getEntities()) {
            if(e instanceof Player) continue;
            if(e instanceof Item && ((Item) e).getItemStack().getType() == Material.PLAYER_HEAD) continue;
            if(e instanceof ItemFrame) continue;
            if(e instanceof ArmorStand) continue;
            if(e instanceof Painting) continue;
            if(e.getEntityId() == CitizensAPI.getNPCRegistry().getById(Schwarzmarkt.SCHWARZMARKT_ID).getEntity().getEntityId()) continue;
            e.remove();
        }

        p.sendMessage(PREFIX + "Alle Entities wurden entfernt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat alle Entities entfernt.", true);

        return false;
    }
}
