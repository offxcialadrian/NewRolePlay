package de.newrp.Entertainment;

import de.newrp.API.Health;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Shop.Buy;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class Boxen implements Listener {

    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();
    public static final HashMap<String, Long> onTesto = new HashMap<>();
    private static String PREFIX = "§8[§6Boxen§8] §6" + Messages.ARROW + " §7";

    public static Location[] locs = new Location[]{new Location(Script.WORLD, 464, 58, 762), new Location(Script.WORLD, 464, 59, 762), new Location(Script.WORLD, 464, 58, 759), new Location(Script.WORLD, 464, 59, 759),
            new Location(Script.WORLD, 464, 59, 756), new Location(Script.WORLD, 464, 58, 756), new Location(Script.WORLD, 468, 58, 756), new Location(Script.WORLD, 468, 59, 756), new Location(Script.WORLD, 468, 58, 759),
            new Location(Script.WORLD, 468, 59, 759), new Location(Script.WORLD, 468, 58, 762), new Location(Script.WORLD, 468, 59, 762)};


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Player p = e.getPlayer();
        boolean hittet = false;

        for (Location loc : locs) {
            if (loc.getBlock().equals(e.getClickedBlock())) {
                hittet = true;
                break;
            }
        }

        if (!hittet) return;

        if(!Buy.isGymMember(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du musst dich erst im Fitnessstudio anmelden.");
            return;
        }

        long time = System.currentTimeMillis();

        Long lastClick = LAST_CLICK.get(p.getName());
        if (lastClick == null) {
            LAST_CLICK.put(p.getName(), time);
            return;
        }

        long difference = time - lastClick;
        if (difference >= 900) LEVEL.remove(p.getName());

        int level = LEVEL.computeIfAbsent(p.getName(), k -> 0);

        LAST_CLICK.put(p.getName(), time);
        LEVEL.replace(p.getName(), level + 1);
        progressBar(250, p);

        if (level >= 250) {
            p.sendMessage(PREFIX + "§6Du hast dein Boxtraining beendet.");
            Health.MUSCLES.add(Script.getNRPID(p), (onTesto.containsKey(p.getName()) && onTesto.get(p.getName())>System.currentTimeMillis()?Script.getRandomFloat(.1F, .15F):Script.getRandomFloat(.05F, .1F)));
            Health.FAT.remove(Script.getNRPID(p), Script.getRandomFloat(1F, 2F));
            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
        }
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = LEVEL.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§a▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cBoxen.. §8» §a" + sb.toString());
    }

}
