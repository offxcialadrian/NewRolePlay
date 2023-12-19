package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.util.HashMap;
import java.util.Map.Entry;

public class Spectate implements CommandExecutor, Listener {
    public static final HashMap<String, Location> spawn = new HashMap<>();
    public static final HashMap<String, String> spectate = new HashMap<>();

    private static String PREFIX = "§8[§cSpectate§8] §8» §7";
    public static boolean isSpectating(Player admin) {
        if (admin == null) return false;
        return spectate.containsKey(admin.getName());
    }

    public static void vanish(Player p) {
        p.setGameMode(GameMode.SPECTATOR);
        spawn.put(p.getName(), p.getLocation());
        spectate.put(p.getName(), p.getName());
        for (Player online : Bukkit.getOnlinePlayers()) online.hidePlayer(main.getInstance(), p);
    }

    public static void unvanish(Player p) {
        if (spawn.get(p.getName()) != null) p.teleport(spawn.get(p.getName()));
        spawn.remove(p.getName());
        p.setGameMode((BuildMode.isInBuildMode(p) ? GameMode.CREATIVE : GameMode.SURVIVAL));
        for (Player online : Bukkit.getOnlinePlayers()) online.showPlayer(main.getInstance(), p);
        spectate.remove(p.getName());
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/spectate [Spieler]");
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            if (spawn.containsKey(p.getName())) {
                p.sendMessage(PREFIX + "Du beobachtest nun nicht mehr.");
                removeSpectate(p);
            } else {
                p.sendMessage(Messages.ERROR + "Du bist nicht am spectaten.");
            }
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(tg == p) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selber beobachten!");
            return true;
        }

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false) && Script.hasRank(tg, Rank.SUPPORTER, false)) {
            p.sendMessage(Messages.ERROR + "Du kannst kein Teammitglied beobachten.");
            return true;
        }

        if(Script.hasRank(p, Rank.ADMINISTRATOR, false) && Script.hasRank(tg, Rank.SUPPORTER, true)) {
            setSpectate(p, tg, false);
            p.sendMessage(PREFIX + "Du beobachtest nun " + Script.getName(tg) + ".");
            p.sendMessage(Messages.INFO + "Es wurde keine Meldung ans Team gesendet, da du ein Teammitglied beobachtest.");
            return true;
        }

        setSpectate(p, tg, true);
        return true;
    }

    public void setSpectate(Player admin, Player target, boolean message) {
        admin.setGameMode(GameMode.SPECTATOR);
        spawn.put(admin.getName(), admin.getLocation());
        admin.teleport(target.getLocation().add(0, 3, 0));
        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> admin.setSpectatorTarget(target), 5L);
        if (message) {
            admin.sendMessage(PREFIX + "Du beobachtest nun " + Script.getName(target) + ".");
            Script.sendTeamMessage(admin, ChatColor.RED, "beobachtet nun " + Script.getName(target) + ".", true);
        }
        spectate.put(admin.getName(), target.getName());
        for (Player online : Bukkit.getOnlinePlayers()) online.hidePlayer(main.getInstance(), admin);
    }

    public void removeSpectate(Player admin) {
        if (spawn.get(admin.getName()) != null) admin.teleport(spawn.get(admin.getName()));
        spawn.remove(admin.getName());
        admin.setSpectatorTarget(null);
        admin.setGameMode(GameMode.SURVIVAL);
        for (Player online : Bukkit.getOnlinePlayers()) online.showPlayer(main.getInstance(), admin);
        Script.sendTeamMessage(admin, ChatColor.RED, "beobachtet nun nicht mehr.", true);
        spectate.remove(admin.getName());
        if(Script.hasRank(admin, Rank.ADMINISTRATOR, false)) {
            admin.setAllowFlight(true);
            admin.setFlying(true);
        }
        if(BuildMode.wasBuildMode.contains(admin.getName())) {
            BuildMode.wasBuildMode.remove(admin.getName());
            BuildMode.setBuildMode(admin);
        }

    }

    public boolean isSpectated(Player target) {
        return spectate.containsValue(target.getName());
    }

    public Player getSpectator(Player target) {
        for (Entry<String, String> ent : spectate.entrySet()) {
            if (ent.getValue().equals(target.getName())) {
                return Script.getPlayer(ent.getKey());
            }
        }
        return null;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        if (isSpectating(p)) {
            p.sendMessage(PREFIX + "Du beobachtest nun nicht mehr.");
            removeSpectate(p);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Spectate spec = new Spectate();
        if (spec.isSpectated(p)) {
            spec.removeSpectate(spec.getSpectator(p));
            p.sendMessage(PREFIX + "Der beobachtete Spieler hat den Server verlassen.");
        } else if (Spectate.isSpectating(p)) {
            spec.removeSpectate(p);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        Spectate spec = new Spectate();
        if (!spec.isSpectated(p)) return;

        Player admin = spec.getSpectator(p);
        if (admin == null) return;
        if (admin.getSpectatorTarget() == null) return;

        admin.teleport(p);
        admin.setSpectatorTarget(p);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for (Entry<String, String> ent : Spectate.spectate.entrySet()) {
            p.hidePlayer(main.getInstance(), Script.getPlayer(ent.getKey()));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/passwort")) return;
        Player p = e.getPlayer();
        Spectate spec = new Spectate();
        if (spec.isSpectated(p)) {
            spec.getSpectator(p).sendMessage(PREFIX + Script.getName(p) + " hat den Befehl §7\"§6" + e.getMessage() + "§7\" benutzt.");
        }
    }
}
