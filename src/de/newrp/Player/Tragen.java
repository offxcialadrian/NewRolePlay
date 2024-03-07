package de.newrp.Player;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import de.newrp.main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Tragen implements CommandExecutor, Listener {

    public static HashMap<Player, Player> tragen = new HashMap<>();
    public static HashMap<Player, BukkitRunnable> tragenTasks = new HashMap<>();
    public static HashMap<Player, Long> cooldown = new HashMap<>();
    public static long TIMEOUT = TimeUnit.MINUTES.toMillis(5);

    public static String PREFIX = "§8[§aTragen§8] §a" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (tragen.containsKey(p) && p.getPassenger() != null) {
            Player tg = tragen.get(p);
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " abgesetzt.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " abgesetzt.");
            Me.sendMessage(p, "hat " + Script.getName(tg) + " abgesetzt.");
            tg.sendMessage(Messages.INFO + "Sneake nun um von " + Script.getName(p) + " abgesetzt zu werden.");
            p.getPassenger().eject();
            tragen.remove(p);
            return true;
        }

        p.sendMessage(Messages.ERROR + "Du trägst niemanden.");

        return false;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (!tragen.containsValue(e.getPlayer())) return;
        Player p = e.getPlayer();
        Player tg = null;
        for (Player player : tragen.keySet()) {
            if (tragen.get(player).equals(p)) {
                tg = player;
                break;
            }
        }



        if (Tragen.tragenTasks.containsKey(tg)) {
            Tragen.tragenTasks.get(tg).cancel();
            Tragen.tragenTasks.remove(tg);
        }


        Player finalTg = tg;
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!Tragen.tragen.containsKey(finalTg)) {
                    cancel();
                    Tragen.tragenTasks.remove(finalTg);
                    return;
                }
                finalTg.setPassenger(p);
            }
        };

        Tragen.tragenTasks.put(tg, task);

        task.runTaskLater(main.getInstance(), 5L);
    }



    @EventHandler
    public void onHit(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!Tragen.tragen.containsKey(p)) return;
        Player tg = Tragen.tragen.get(p);
        Tragen.tragen.remove(p);
        Tragen.tragen.remove(tg);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " abgesetzt.");
        tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " abgesetzt.");
        Me.sendMessage(p, "hat " + Script.getName(tg) + " abgesetzt.");
        p.eject();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!Tragen.tragen.containsKey(p)) return;
        Player tg = Tragen.tragen.get(p);
        Tragen.tragen.remove(p);
        Tragen.tragen.remove(tg);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " abgesetzt.");
        tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " abgesetzt.");
        Me.sendMessage(p, "hat " + Script.getName(tg) + " abgesetzt.");
        p.eject();
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!Tragen.tragen.containsKey(p)) return;
        Player tg = Tragen.tragen.get(p);
        Tragen.tragen.remove(p);
        Tragen.tragen.remove(tg);
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " abgesetzt.");
        tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " abgesetzt.");
        Me.sendMessage(p, "hat " + Script.getName(tg) + " abgesetzt.");
        p.eject();
    }
}
