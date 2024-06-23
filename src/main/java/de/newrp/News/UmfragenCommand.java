package de.newrp.News;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Player.Mobile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.Map;

public class UmfragenCommand implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§6Umfrage§8] §6» §7";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Beruf.hasBeruf(p) || Beruf.getBeruf(p) != Beruf.Berufe.NEWS) {
            Location location = new Location(Script.WORLD, 277, 68, 785);
            if(p.getLocation().distance(location) < 5) {
                Mobile.openUmfrage(p);
                return true;
            }

            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            if (Umfrage.getActiveUmfrage() != null) {
                Umfrage u = Umfrage.getActiveUmfrage();
                p.sendMessage(PREFIX + "§7=== §6" + u.getFrage() + " §7===");
                for (String antwort : u.getAntworten().keySet()) {
                    p.sendMessage(PREFIX + "§6" + antwort + " §8× §6" + u.getAntworten().get(antwort) + " Stimmen");
                }
            } else {
                p.sendMessage(Messages.ERROR + "Es gibt keine aktive Umfrage.");
            }
            return true;
        }

        if (args.length > 3 && args[0].equalsIgnoreCase("create")) {
            if (Umfrage.getActiveUmfrage() != null) {
                p.sendMessage(Messages.ERROR + "Es gibt bereits eine aktive Umfrage.");
                p.sendMessage(Messages.INFO + "Du kannst diese mit /umfrage end beenden.");
                return true;
            }


            String msg = String.join(" ", args);
            msg = msg.replace("create ", "");
            String[] str = msg.split(",");

            String frage = str[0];

            String[] antworten = Arrays.copyOfRange(str, 1, str.length);
            for (int i = 0; i < antworten.length; i++) {
                antworten[i] = antworten[i].replace(",", "");
            }

            Umfrage.createUmfrage(frage.replace(",", ""), antworten);
            Beruf.Berufe.NEWS.sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat eine neue Umfrage erstellt.");
            Script.addEXP(p, Script.getRandom(4, 8), true);
            Beruf.Berufe.NEWS.sendMessage(PREFIX + "§7Frage: §6" + frage.replace(",", ""));
            for (String antwort : antworten) {
                Beruf.Berufe.NEWS.sendMessage(PREFIX + "§7Antwort: §6" + antwort);
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("end")) {
            if (Umfrage.getActiveUmfrage() == null) {
                p.sendMessage(Messages.ERROR + "Es gibt keine aktive Umfrage.");
                return true;
            }
            Umfrage u = Umfrage.getActiveUmfrage();
            Umfrage.endUmfrage(u);
            Beruf.Berufe.NEWS.sendMessage(PREFIX + "§6" + Script.getName(p) + " §7hat die Umfrage beendet.");
            Beruf.Berufe.NEWS.sendMessage(PREFIX + "§7Frage: §6" + u.getFrage());
            for (Map.Entry<String, Integer> entry : u.getAntworten().entrySet()) {
                Beruf.Berufe.NEWS.sendMessage(PREFIX + "§7Antwort: §6" + entry.getKey() + " §8× §6" + entry.getValue() + " Stimmen");
            }

            return true;
        }

        p.sendMessage(Messages.ERROR + "/umfrage [create/end] [Frage/Antworten]");

        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith("§8[§6Umfrage§8]")) return;
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        String antwort = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().replace("§8» §6",""));
        Umfrage u = Umfrage.getActiveUmfrage();

        if (Umfrage.players.contains(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du kannst nur einmal am Tag abstimmen.");
            p.closeInventory();
            return;
        }

        if (u == null) {
            e.getWhoClicked().sendMessage(Messages.ERROR + "Es gibt keine aktive Umfrage.");
            e.getWhoClicked().closeInventory();
            return;
        }

        if(antwort.isEmpty() || !u.getAntworten().containsKey(antwort)) {
            Script.sendBugReport(p, "Umfrage -> Antwort: \"" + antwort + "\" ist nicht vorhanden.");
            return;
        }

        u.vote(antwort);
        e.getWhoClicked().sendMessage(PREFIX + "§7Du hast für §6" + antwort + " §7gestimmt.");
        p.sendMessage(PREFIX + "§7Danke für deine Stimme.");
        p.closeInventory();
        Umfrage.players.add(p.getName());
    }


}

