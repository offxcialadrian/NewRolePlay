package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RegisterBanner implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§eBanner§8]§e " + Messages.ARROW + " §7";
    public static ArrayList<Player> banner = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, true) && !BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(banner.contains(p)) {
            banner.remove(p);
            p.sendMessage(PREFIX + "Du hast den Banner-Modus deaktiviert.");
        } else {
            banner.add(p);
            p.sendMessage(PREFIX + "Du hast den Banner-Modus aktiviert.");
            p.sendMessage(Messages.INFO + "Klicke nun auf die obere Hälfte eines Banners, um es zu registrieren.");
        }

        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!banner.contains(p)) return;
        if(e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType() != Material.WHITE_WALL_BANNER) return;
        Script.executeUpdate("INSERT INTO graffiti (x, y, z, org) VALUES (" + (int) e.getClickedBlock().getX() + ", " + (int) e.getClickedBlock().getY() + ", " + (int) e.getClickedBlock().getZ() + ", " + "NULL" + ")");
        p.sendMessage(PREFIX + "Banner registriert.");
    }

}
