package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Route;
import de.newrp.API.Script;
import de.newrp.Player.AFK;
import de.newrp.Player.Notruf;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

public class AcceptNotruf implements CommandExecutor, Listener {

    public static HashMap<Player, Player> accept = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE) && !Beruf.getBeruf(p).equals(Beruf.Berufe.RETTUNGSDIENST)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/acceptnotruf [Name]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(!Notruf.call2.containsKey(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen Notruf abgesetzt.");
            return true;
        }

        if(!Notruf.call2.get(tg).contains(Beruf.getBeruf(p))) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen Notruf abgesetzt.");
            return true;
        }

        if(accept.containsKey(p)) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Notruf angenommen.");
            return true;
        }

        if(accept.containsValue(tg)) {
            p.sendMessage(Messages.ERROR + "Der Notruf wurde bereits angenommen.");
            return true;
        }

        new Route(p.getName(), Script.getNRPID(p), p.getLocation(), Notruf.call.get(tg)).start();
        Beruf.getBeruf(p).sendMessage(Notruf.PREFIX  + "§6" + Script.getName(p) + " §7hat den Notruf von §6" + Script.getName(tg) + " §7angenommen.");
        tg.sendMessage(Notruf.PREFIX + "Die Hilfe ist in ca. " + calcETA(p.getLocation().distance(Notruf.call.get(tg))) + " Sekunden bei Ihnen, bitte warten Sie vorort.");
        accept.put(p, tg);

        if(Notruf.call2.get(tg).size() == 1) {
            Notruf.call.remove(tg);
            Notruf.call2.remove(tg);
            Notruf.call3.remove(tg);
        } else {
            ArrayList<Beruf.Berufe> berufe = new ArrayList<>();
            for(Beruf.Berufe b : Notruf.call2.get(tg)) {
                if(!b.equals(Beruf.getBeruf(p))) {
                    berufe.add(b);
                }
            }
            Notruf.call2.replace(tg, berufe);
        }

        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(accept.containsKey(p)) {
            Player tg = accept.get(p);
            Beruf.getBeruf(p).sendMessage(Notruf.PREFIX + "Der Notruf von §6" + Script.getName(tg) + " §7wurde wieder geöffnet");
            accept.remove(p);
            List<Beruf.Berufe> berufe = Notruf.call2.get(tg);
            berufe.add(Beruf.getBeruf(p));
            Notruf.call2.replace(tg, berufe);
            return;
        }

        if(Notruf.call.containsKey(p)) {
            deleteNotruf(p);
        }
    }


    private static int calcETA(double meter) {
        return (int) (meter / 6.0);
    }

    public static void reOpenNotruf(final Player player, final Player emergency) {
        if(!accept.containsKey(player)) {
            player.sendMessage(Messages.ERROR + "Du hast keinen Notruf angenommen!");
            return;
        }

        Beruf.getBeruf(player).sendMessage(Notruf.PREFIX + "Der Notruf von §6" + Script.getName(emergency) + " §7wurde wieder geöffnet");
        accept.remove(player);
        List<Beruf.Berufe> berufe = Notruf.call2.getOrDefault(emergency, new ArrayList<>());
        berufe.add(Beruf.getBeruf(player));
        Notruf.call2.replace(emergency, berufe);
    }

    public static void deleteNotruf(Player p) {
        if(Notruf.call.containsKey(p)) {
            for(Player all : accept.keySet()) {
                if(accept.get(all).equals(p)) {
                    all.sendMessage(Notruf.PREFIX + "Der Notruf von §6" + Script.getName(p) + " §7wurde gelöscht.");
                    return;
                }
            }
            Notruf.call.remove(p);
            Notruf.call2.remove(p);
            Notruf.call3.remove(p);
        }
    }



}
