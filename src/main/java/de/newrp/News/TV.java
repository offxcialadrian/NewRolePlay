package de.newrp.News;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.SlotLimit;
import de.newrp.House.House;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TV implements CommandExecutor, Listener {

    public static HashMap<Player, Location> tvs = new HashMap<>();
    public static String PREFIX = "§8[§6TV§8] §6" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/tv");
            return true;
        }

        if (tvs.containsKey(p)) {
            p.sendMessage(PREFIX + "Du hast den Fernseher ausgeschaltet.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.setSpectatorTarget(null);
                    p.teleport(tvs.get(p));
                    tvs.remove(p);
                    p.setGameMode(GameMode.SURVIVAL);
                }
            }.runTaskLater(NewRoleplayMain.getInstance(), 5L);
            return true;
        }

        if(!House.isInHouse(p)) {
            p.sendMessage(Messages.ERROR + "Du musst in einem Haus sein, um den Fernseher zu benutzen.");
            return true;
        }

        if(House.getHouses(Script.getNRPID(p)).size() > SlotLimit.HOUSE.get(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du wohnst in " + House.getHouses(Script.getNRPID(p)).size() + " Häusern. Du kannst nur " + SlotLimit.HOUSE.get(Script.getNRPID(p)) + " Häuser besitzen.");
            p.sendMessage(Messages.INFO + "Du kannst einen weiteren Hausslot im Shop erwerben.");
            return true;
        }

        Player camera = KameraCommand.camera;
        if (camera == null) {
            p.sendMessage(Messages.ERROR + "Gerade läuft keine Sendung.");
            return true;
        }

        tvs.put(p, p.getLocation());
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(camera.getLocation().add(0, 3, 0));
        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> p.setSpectatorTarget(camera), 5L);

        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(tvs.containsKey(p)) {
            p.sendMessage(PREFIX + "Du hast den Fernseher ausgeschaltet.");
            p.teleport(tvs.get(p));
            tvs.remove(p);
            p.setSpectatorTarget(null);
            p.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if(e.getCause() != PlayerTeleportEvent.TeleportCause.SPECTATE) {
            return;
        }

        final Player player = e.getPlayer();
        if(tvs.containsKey(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if(player.getGameMode() != GameMode.SPECTATOR) {
            return;
        }

        if(tvs.containsKey(player)) {
            player.sendMessage(PREFIX + "Du hast den Fernseher ausgeschaltet.");
            player.teleport(tvs.get(player));
            tvs.remove(player);
            player.setSpectatorTarget(null);
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(player.getGameMode() != GameMode.SPECTATOR) {
            return;
        }

        if(tvs.containsKey(player)) {
            if(KameraCommand.camera == null) {
                player.sendMessage(PREFIX + "Du hast den Fernseher ausgeschaltet.");
                player.teleport(tvs.get(player));
                tvs.remove(player);
                player.setSpectatorTarget(null);
                player.setGameMode(GameMode.SURVIVAL);
            }

            player.setSpectatorTarget(KameraCommand.camera);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
        }
    }




}
