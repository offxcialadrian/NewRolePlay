package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class StartTransport implements CommandExecutor {

    public static boolean isActive = false;
    public static Player executor = null;
    public static boolean cooldown = false;
    public static int add = 0;
    public static int LEVEL = 0;
    public static String PREFIX = "§8[§6Waffentransport§8] §6" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if(!Beruf.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/starttransport");
            return true;
        }

        if(isActive) {
            p.sendMessage(Messages.ERROR + "Es ist bereits ein Transport aktiv.");
            return true;
        }

        if(cooldown) {
            p.sendMessage(Messages.ERROR + "Der Transport ist noch im Cooldown.");
            return true;
        }

        executor = p;
        cooldown = true;
        Bukkit.broadcastMessage("§8[§6News§8] §6" + Messages.ARROW + "Berichten zufolge kommt in kürze ein Waffentransport der Polizei am Hafen an. Bitte meiden Sie den Hafenbereich und halten Sie sich an die Anweisungen der Polizei.");
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("§8[§6News§8] §6" + Messages.ARROW + "Der Waffentransport der Polizei ist am Hafen angekommen. Bitte meiden Sie den Hafenbereich und halten Sie sich an die Anweisungen der Polizei.");
                StartTransport.executor = null;
                StartTransport.isActive = false;
                StartTransport.LEVEL = 0;
                StartTransport.add = 0;
            }
        }.runTaskLater(Main.getInstance(), 20*60*15);

        return false;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(p != executor) return;
        if(!isActive) return;
        if(p.getLocation().distance(new Location(Script.WORLD, 1056, 66, 1064, 107.58685f, 22.827269f))>10) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getClickedBlock().getType() != Material.CHEST) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if(LEVEL == 10) {
                    Script.sendActionBar(p, Beruf.PREFIX + "Du hast erfolgreich 3 Einheiten geladen.");
                    add += 3;

                    if(add >= 300) {
                        add = 300;
                        Beruf.Berufe.POLICE.sendMessage(PREFIX + "Es wurden 300 Einheiten geladen.");
                        p.sendMessage(Messages.INFO + "Bringe nun den Transport zur Polizeistation.");
                    }
                }
                LEVEL++;
                progressBar(p);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);

    }

    private static void progressBar(Player p) {
        double current_progress = LEVEL;
        double progress_percentage = current_progress / (double) 3;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§c▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cLaden.. §8» §a" + sb.toString());
    }

}
