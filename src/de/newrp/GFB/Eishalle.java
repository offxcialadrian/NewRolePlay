package de.newrp.GFB;

import de.newrp.API.Messages;
import de.newrp.API.PayDay;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Eishalle implements CommandExecutor, Listener {

    private static String PREFIX = "§8[§bEishalle§8] §b" + Messages.ARROW + " §7";
    public static HashMap<String, Long> cooldown = new HashMap<>();
    public static Player CURRENT = null;

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/eishalle");
            return true;
        }

        if(p.getLocation().distance(new Location(Script.WORLD, 372, 67, 768, 1.3254395f, 1.4999639f)) > 5) {
            p.sendMessage(Messages.ERROR + "Du musst dich in der Eishalle befinden.");
            return true;
        }

        if(CURRENT != null) {
            p.sendMessage(Messages.ERROR + "Das Eis wird gerade schon befahrbar gemacht.");
            return true;
        }

        if(cooldown.containsKey(p.getName())) {
            if(cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        if(GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du hast bereits einen Job.");
            return true;
        }

        CURRENT = p;
        cooldown.put(p.getName(), System.currentTimeMillis() + 10 * 60 * 2000L);
        for(Block block : Script.getBlocksBetween(new Location(Script.WORLD, 385, 66, 764), new Location(Script.WORLD, 370, 66, 741))) {
            block.setType(Material.PACKED_ICE);
        }

        p.sendMessage(PREFIX + "Mach das Eis nun wieder Befahrbar.");
        p.sendMessage(Messages.INFO + "Klicke nun auf Rechtsklick jeden Block auf der Bahn. Du hast maximal 5 Minuten Zeit.");
        GFB.CURRENT.put(p.getName(), GFB.EISHALLE);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(CURRENT == p) {
                    CURRENT = null;
                    p.sendMessage(Messages.ERROR + "Du hast zu lange gebraucht.");
                    for(Block block : Script.getBlocksBetween(new Location(Script.WORLD, 385, 66, 764), new Location(Script.WORLD, 370, 66, 741))) {
                        block.setType(Material.ICE);
                    }
                }
            }
        }.runTaskLater(main.getInstance(), 5 * 60 * 20L);
        return false;
    }


    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(CURRENT != p) return;
        if(e.getClickedBlock() == null) return;
        if(e.getClickedBlock().getType() != Material.PACKED_ICE) return;

        if(Script.getBlocksBetween(new Location(Script.WORLD, 385, 66, 764), new Location(Script.WORLD, 370, 66, 741)).contains(e.getClickedBlock())) {
            e.getClickedBlock().setType(Material.ICE);
        }

        for(Block block : Script.getBlocksBetween(new Location(Script.WORLD, 385, 66, 764), new Location(Script.WORLD, 370, 66, 741))) {
            if(block.getType() == Material.PACKED_ICE) return;
        }

        CURRENT = null;
        p.sendMessage(PREFIX + "Du hast das Eis wieder befahrbar gemacht.");
        GFB.CURRENT.remove(p.getName());
        GFB.EISHALLE.addExp(p, GFB.EISHALLE.getLevel(p) + Script.getRandom(5, 7)/2);
        PayDay.addPayDay(p, GFB.EISHALLE.getLevel(p) + Script.getRandom(5, 7));
        Script.addEXP(p, GFB.EISHALLE.getLevel(p)+ Script.getRandom(5, 7));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(CURRENT != p) return;
        CURRENT = null;
        GFB.CURRENT.remove(p.getName());
        for(Block block : Script.getBlocksBetween(new Location(Script.WORLD, 385, 66, 764), new Location(Script.WORLD, 370, 66, 741))) {
            block.setType(Material.ICE);
        }
    }


}
