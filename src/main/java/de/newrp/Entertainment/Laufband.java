package de.newrp.Entertainment;

import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Laufband implements CommandExecutor, Listener {

    private static ArrayList<Player> ontrain = new ArrayList<>();
    private static Location to = new Location(Script.WORLD, 455, 57.5, 762);
    private static Location from = new Location(Script.WORLD, 456, 57.5, 762);
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;
        p.sendMessage(Script.PREFIX + "Coming soon..");
        //ontrain.add(p);
        return false;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(ontrain.contains(p)) {
            if(e.getTo().distance(to) > 1D) {
                return;
            }
            e.setCancelled(true);
            Location playerCenterLocation = to;
            Location playerToThrowLocation = from;
            double x = playerToThrowLocation.getX() - playerCenterLocation.getX();
            double y = playerToThrowLocation.getY() - playerCenterLocation.getY();
            double z = playerToThrowLocation.getZ() - playerCenterLocation.getZ();
            Vector throwVector = new Vector(x, y, z);
            throwVector.normalize();
            throwVector.multiply(.5D);
            throwVector.setY(.1D);
            e.getPlayer().setVelocity(throwVector);
            Script.sendActionBar(p, "progress");
        }
    }
}
