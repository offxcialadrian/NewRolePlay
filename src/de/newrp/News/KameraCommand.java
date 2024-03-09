package de.newrp.News;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class KameraCommand implements CommandExecutor, Listener {

    public static Player camera =  null;
    public static String PREFIX = "§8[§6Kamera§8] §6" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getBeruf(p) != Beruf.Berufe.NEWS) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getAbteilung(p) == Abteilung.Abteilungen.VOLONTAER) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/kamera");
            return true;
        }

        if(camera == p) {
            camera = null;
            p.sendMessage(PREFIX + "Du hast nun nicht mehr die Kamera-Steuerung.");
            Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getName(p) + " hat die Kamera-Steuerung verlassen.");
            return true;
        }

        if(camera != null) {
            p.sendMessage(PREFIX + "Die Kamera wird bereits von " + Script.getName(camera)+ " gesteuert.");
            return true;
        }

        camera = p;
        p.sendMessage(PREFIX + "Du steuerst nun die Kamera.");
        Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getName(p) + " steuert nun die Kamera.");
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(camera == e.getPlayer()) {
            camera = null;
            Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getName(e.getPlayer()) + " hat die Kamera-Steuerung verlassen.");
        }
    }

}
