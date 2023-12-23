package de.newrp.Player;

import de.newrp.API.Friedhof;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.Sperre;
import de.newrp.Administrator.SDuty;
import de.newrp.Chat.Me;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Tragen implements CommandExecutor, Listener {

    private static HashMap<Player, Player> tragen = new HashMap<>();
    private static HashMap<Player, Long> cooldown = new HashMap<>();
    private static long TIMEOUT = TimeUnit.MILLISECONDS.toMinutes(5);

    private static String PREFIX = "§8[§aTragen§8] §a" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR  + "/tragen [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht online.");
            return true;
        }

        if(p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst tragen.");
            return true;
        }

        if(tragen.containsKey(p) && tragen.get(p) == tg && p.getPassenger() != null) {
            tg.teleport(p.getLocation());
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " abgesetzt.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " abgesetzt.");
            Me.sendMessage(p, "hat " + Script.getName(tg) + " abgesetzt.");
            tragen.remove(p);
            tragen.remove(tg);
            return true;
        }

        if(cooldown.containsKey(p)) {
            if(cooldown.get(p) + TIMEOUT > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du kannst nur alle 5 Minuten einen Spieler tragen.");
                return true;
            }
        }

        if(Sperre.TRAGENSPERRE.isActive(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du darfst derzeit keine Spieler tragen.");
            return true;
        }

        if(cooldown.containsKey(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler kann derzeit nicht getragen werden.");
            return true;
        }

        if(p.getPassenger() == null) {
            p.sendMessage(Messages.ERROR + "Du trägst bereits einen Spieler.");
            return true;
        }

        if(tragen.containsKey(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler wird bereits getragen.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist zu weit entfernt.");
            return true;
        }

        if(AFK.isAFK(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist AFK.");
            return true;
        }

        if(SDuty.isSDuty(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist im Supporter-Dienst.");
            return true;
        }

        if(Friedhof.isDead(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist tot.");
            return true;
        }

        tragen.put(p, tg);
        cooldown.put(p, System.currentTimeMillis());
        cooldown.put(tg, System.currentTimeMillis());
        p.sendMessage(PREFIX + "Du trägst nun " + Script.getName(tg) + ".");
        tg.sendMessage(PREFIX + "Du wirst nun von " + Script.getName(p) + " getragen.");
        Me.sendMessage(p, "trägt nun " + Script.getName(tg) + ".");
        p.setPassenger(tg);

        return false;
    }
}
