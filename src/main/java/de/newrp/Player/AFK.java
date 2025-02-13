package de.newrp.Player;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.GFB.Schule;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

public class AFK implements CommandExecutor, Listener {


    public static final HashMap<UUID, String> afk = new HashMap<>();
    static final HashMap<Player, Location> loc = new HashMap<>();
    static final ArrayList<Player> counter = new ArrayList<>();

    public static String PREFIX = "§8[§6AFK§8] §6» ";

    public static boolean isAFK(Player p) {
        return afk.containsKey(p.getUniqueId());
    }

    public static boolean isAFK(UUID p) {
        return afk.containsKey(Objects.requireNonNull(Bukkit.getPlayer(p)).getUniqueId());
    }

    public static String getAFKTime(Player p) {
        return afk.get(p.getUniqueId());
    }

    public static final HashMap<String, Long> lastDmg = new HashMap<>();
    public static final Set<String> lastActions = new HashSet<>();


    public static void setAFK(Player p, boolean b) {
        if (b) {
            afk.put(p.getUniqueId(), new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
            p.setCollidable(false);
            p.setCanPickupItems(false);
            if (p.isFlying()) p.setFlying(false);
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("zzznopush").addEntry(p.getName());
            if(Schule.STUDIYING.containsKey(p)) {
                p.sendMessage(Schule.PREFIX + "§cDu hast den Kurs nicht bestanden.");
                Schule.STUDIYING.remove(p);
                Schule.STARTED.remove(p);
                Schule.taskID.get(p).cancel();
            }
        } else {
            afk.remove(p.getUniqueId());
            p.setCollidable(true);
            p.setCanPickupItems(true);
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("zzznopush").removeEntry(p.getName());
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").addEntry(p.getName());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                SDuty.updateScoreboard();
            }
        }.runTaskLater(NewRoleplayMain.getInstance(), 20L);
        Script.updateListname(p);
    }

    public static void updateAFK(Player p) {
        if (!counter.contains(p)) {
            lastActions.remove(p.getName());
            loc.put(p, p.getLocation());
            counter.add(p);
            return;
        }


        if (!loc.containsKey(p)) {
            loc.put(p, p.getLocation());
            lastActions.remove(p.getName());
            return;
        }

        if (!p.getLocation().equals(loc.get(p)) || lastActions.remove(p.getName())) {
            counter.remove(p);
            loc.remove(p);
            return;
        }
        Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> AFK.setAFK(p, true));
        if(!SDuty.isSDuty(p)) Script.sendLocalMessage(5, p, "§a§o  " + Script.getName(p) + " ist nun abwesend.");
        p.sendMessage(PREFIX + "Du bist nun im AFK-Modus.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (isAFK(p)) {
            setAFK(p, false);
            p.sendMessage(PREFIX + "Du bist nun nicht mehr im AFK-Modus.");
            Script.sendLocalMessage(5, p, "§a§o  " + Script.getName(p) + " ist wieder anwesend.");
        } else {
            long time = System.currentTimeMillis();
            Long lastUsage = lastDmg.get(p.getName());
            if (p.hasPotionEffect(PotionEffectType.WITHER) || (lastUsage != null && lastUsage + 15 * 1000 > time)) {
                p.sendMessage(Messages.ERROR + "Du kannst noch nicht in den AFK Modus wechseln.");
                return true;
            }
            setAFK(p, true);
            p.sendMessage(PREFIX + "Du bist nun im AFK-Modus.");
            Script.sendLocalMessage(5, p, "§a§o  " + Script.getName(p) + " ist nun abwesend.");
        }
        return true;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(Script.isInTestMode()) {
            //Script.sendActionBar(p, Messages.INFO + "WORK IN PROGRESS " + p.getName());
        }
        if (AFK.isAFK(p)) {
            boolean x = e.getFrom().getBlockX() != e.getTo().getBlockX();
            boolean y = Math.abs(e.getFrom().getY() - e.getTo().getY()) > 2;
            boolean z = e.getFrom().getBlockZ() != e.getTo().getBlockZ();
            if (y || x || z) {
                AFK.setAFK(p, false);
                p.sendMessage(PREFIX + "Du bist nun nicht mehr im AFK-Modus.");
                if(!SDuty.isSDuty(p)) Script.sendLocalMessage(5, p, "§a§o  " + Script.getName(p) + " ist wieder anwesend.");
            }
        }
    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if(Script.isInTestMode()) {
                //Script.sendActionBar((((Player) e.getEntity()).getPlayer()), Messages.INFO + "WORK IN PROGRESS " + ((Player) e.getEntity()).getName());
            }
            if(!(e.getDamager() instanceof Player)) {
                return;
            }
            Player damager = (Player) e.getDamager();
            if(isAFK((Player) e.getEntity())) e.setCancelled(true);
            if(e.isCancelled()) return;
            Player p = (Player) e.getEntity();
            lastDmg.put(p.getName(), System.currentTimeMillis());
            lastDmg.put(damager.getName(), System.currentTimeMillis());
            lastActions.add(p.getName());
            lastActions.add(damager.getName());
        }
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        lastActions.add(p.getName());
        if(Script.isInTestMode()) {
            //Script.sendActionBar(p, Messages.INFO + "WORK IN PROGRESS " + p.getName());
        }
        if (AFK.isAFK(p)) {
            AFK.setAFK(p, false);
            p.sendMessage(PREFIX + "Du bist nun nicht mehr im AFK-Modus.");
            if(!SDuty.isSDuty(p)) Script.sendLocalMessage(5, p, "§a§o  " + Script.getName(p) + " ist wieder anwesend.");
        }
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        lastActions.add(p.getName());

        if (AFK.isAFK(p)) {
            AFK.setAFK(p, false);
            p.sendMessage(PREFIX+ "Du bist nun nicht mehr im AFK-Modus.");
            if(!SDuty.isSDuty(p)) Script.sendLocalMessage(5, p, "§a§o  " + Script.getName(p) + " ist wieder anwesend.");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) return;

        Player p = e.getPlayer();
        lastActions.add(p.getName());

        if (AFK.isAFK(p)) {
            AFK.setAFK(p, false);
            p.sendMessage(PREFIX + "Du bist nun nicht mehr im AFK-Modus.");
            if(!SDuty.isSDuty(p)) Script.sendLocalMessage(5, p, "§a§o  " + Script.getName(p) + " ist wieder anwesend.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (AFK.isAFK(p)) AFK.setAFK(p, false);
        lastDmg.remove(p.getName());
    }


}