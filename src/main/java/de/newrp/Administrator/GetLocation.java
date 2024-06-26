package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.API.Team;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class GetLocation implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§eGetLocation§8] §7";
    private static final ArrayList<Player> location = new ArrayList<>();
    private static final HashMap<String, Long> cooldown = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.DEVELOPER, false) && Team.getTeam(p) != Team.Teams.BAU) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p) && Team.getTeam(p) != Team.Teams.BAU) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length == 1) {
            if(location.contains(p)) {
                location.remove(p);
                p.sendMessage(PREFIX + "Du sammelst nun keine Locations mehr.");
                return true;
            }

            p.sendMessage(PREFIX + "Du sammelst nun Locations.");
            location.add(p);
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/getlocation");
            return true;
        }


        DecimalFormat df = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));
        p.sendMessage(PREFIX + "Die Position lautet: " + df.format(p.getLocation().getX()) + "/" + df.format(p.getLocation().getY()) + "/" + df.format(p.getLocation().getZ()));
        if(Script.hasRankExact(p, Rank.ADMINISTRATOR, Rank.OWNER, Rank.DEVELOPER) || Script.isInTestMode())
            Script.sendCopyMessage(p, Messages.INFO + "Klicke hier um die Location zu kopieren.", "new Location(Script.WORLD, " + df.format(p.getLocation().getX()) + ", " + df.format(p.getLocation().getY()) + ", " + df.format(p.getLocation().getZ()) + ", " + df.format(p.getLocation().getYaw()) + "f, " + df.format(p.getLocation().getPitch()) + "f)", "§aKlicke um die Location zu kopieren.");

        return false;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!location.contains(p)) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock() == null) return;
        if(cooldown.containsKey(p.getName()) && cooldown.get(p.getName()) > System.currentTimeMillis()) return;
        cooldown.put(p.getName(), System.currentTimeMillis() + 10);
        p.sendMessage(PREFIX + "Die Position lautet: " + e.getClickedBlock().getLocation().getBlockX() + "/" + e.getClickedBlock().getLocation().getBlockY() + "/" + e.getClickedBlock().getLocation().getBlockZ());
        if(Script.hasRank(p, Rank.ADMINISTRATOR, false))
            Script.sendCopyMessage(p, Messages.INFO + "Klicke hier um die Location zu kopieren.", "new Location(Script.WORLD, " + e.getClickedBlock().getLocation().getBlockX() + ", " + e.getClickedBlock().getLocation().getBlockY() + ", " + e.getClickedBlock().getLocation().getBlockZ() + ")", "§aKlicke um die Location zu kopieren.");
    }


}
