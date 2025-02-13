package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Police.Handschellen;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class BuildMode implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§eBuildMode§8] §e" + Messages.ARROW + " ";
    public static ArrayList<String> wasBuildMode = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.ADMINISTRATOR, true) && Team.getTeam(p) != Team.Teams.BAU) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p) && Team.getTeam(p) != Team.Teams.BAU) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false) && !Team.isTeamLeader(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length > 1) {
            p.sendMessage(Messages.ERROR + "/buildmode {Name}");
            return true;
        }

        if (args.length == 0) {

            if(Checkpoints.hasCheckpoints(p)) {
                p.sendMessage(Messages.ERROR + "Du hast noch Checkpoints.");
                return true;
            }

            if (isInBuildMode(p)) {
                removeBuildMode(p);
                p.sendMessage(PREFIX + "Du hast den BuildMode verlassen.");
                Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den BuildMode verlassen.", true);
            } else {
                if(Cache.getInventory(p) != null) {
                    p.sendMessage(Messages.ERROR + "Du kannst gerade nicht in den BuildMode wechseln, da du bereits ein Inventar gespeichert hast.");
                    return true;
                }
                setBuildMode(p);
                p.sendMessage(PREFIX + "Du hast den BuildMode betreten.");
                Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den BuildMode betreten.", true);
            }
            return true;
        }

        Player tg = Bukkit.getPlayer(args[0]);

        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(Team.getTeam(tg) != Team.Teams.BAU && !Script.isNRPTeam(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler ist kein Bau-Team Mitglied und kein NRP.");
            return true;
        }

        if (Checkpoints.hasCheckpoints(tg)) {
            p.sendMessage(Messages.ERROR + "Dieser Spieler hat noch Checkpoints.");
            return true;
        }

        if (isInBuildMode(tg)) {
            removeBuildMode(tg);
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " den BuildMode entfernt.");
            tg.sendMessage(PREFIX + Messages.RANK_PREFIX(p) + " hat dir den BuildMode entfernt.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat " + Script.getName(tg) + " den BuildMode entfernt.", true);
            Log.HIGH.write(p, "hat " + Script.getName(tg) + " den BuildMode entfernt.");
            Log.HIGH.write(tg, "wurde von " + Script.getName(p) + " den BuildMode entfernt.");
        } else {
            if(Cache.getInventory(tg) != null) {
                p.sendMessage(Messages.ERROR + "Dieser Spieler kann gerade nicht in den BuildMode wechseln, da er bereits ein Inventar gespeichert hat.");
                return true;
            }
            setBuildMode(tg);
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " in den BuildMode gesetzt.");
            tg.sendMessage(PREFIX + Messages.RANK_PREFIX(p) + " hat dich in den BuildMode gesetzt.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat " + Script.getName(tg) + " in den BuildMode gesetzt.", true);
            Log.HIGH.write(p, "hat " + Script.getName(tg) + " in den BuildMode gesetzt.");
            Log.HIGH.write(tg, "wurde von " + Script.getName(p) + " in den BuildMode gesetzt.");
        }


        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!isInBuildMode(e.getPlayer())) e.setCancelled(true);
        if(!e.isCancelled()) Script.addToBauLog(e.getPlayer(), e.getBlock().getType() , e.getBlock().getLocation(), false);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onBlockSet(BlockPlaceEvent e) {
        if (!isInBuildMode(e.getPlayer())) e.setCancelled(true);
        if(!e.isCancelled() && e.getBlock().getState() instanceof ItemFrame) e.getPlayer().sendMessage(Messages.INFO + "Bitte beachte, dass ItemFrames den Server zum laggen bringen können und daher ggf. später entfernt werden.");
        if(!e.isCancelled()) Script.addToBauLog(e.getPlayer(), e.getBlock().getType() , e.getBlock().getLocation(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (isInBuildMode(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (isInBuildMode(e.getPlayer())) removeBuildMode(e.getPlayer());
    }

    @EventHandler
    public void openChest(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(Script.isInTestMode()) {
            //Script.sendActionBar(p, Messages.INFO + "WORK IN PROGRESS " + p.getName());
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType().equals(Material.CHEST))
                if (!isInBuildMode(p)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if(isInBuildMode(p) && e.getEntity() instanceof Player) {
                p.sendMessage(Messages.INFO + "Du kannst im BuildMode keine Spieler schlagen.");
                e.setCancelled(true);
            }
        }
    }

    public static boolean isInBuildMode(Player p) {
        return p.getGameMode().equals(GameMode.CREATIVE);
    }

    public static void removeBuildMode(Player p) {
        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) p.setOp(false);
        p.getInventory().clear();
        Cache.loadInventory(p);
        wasBuildMode.remove(p.getName());
        p.setGameMode(GameMode.SURVIVAL);
        Log.NORMAL.write(p, "hat den BuildMode verlassen.");
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(NewRoleplayMain.getInstance(), p);
        }
        if (SDuty.isSDuty(p) && Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.setAllowFlight(true);
            p.setFlying(true);
        }
        Script.updateListname(p);
    }

    public static void setBuildMode(Player p) {
        if(Script.isInTestMode()) {
            p.sendMessage(PREFIX + "Du hast dich geheilt.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat sich geheilt.", true);
            Log.NORMAL.write(p, "hat sich geheilt.");
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setFireTicks(0);

            if(!Handschellen.isCuffed(p)) {
                for (PotionEffect e : p.getActivePotionEffects()) {
                    p.removePotionEffect(e.getType());
                }
            } else {
                p.sendMessage(PREFIX + "Die Effekte wurden nicht entfernt, da du Handschellen trägst.");
            }

            Krankheit.GEBROCHENER_ARM.remove(Script.getNRPID(p));
            Krankheit.GEBROCHENES_BEIN.remove(Script.getNRPID(p));
            p.setWalkSpeed(0.2F);
            p.setOp(true);
        }
        Cache.saveInventory(p);
        p.getInventory().clear();
        if(!wasBuildMode.contains(p.getName())) wasBuildMode.add(p.getName());
        if(Script.hasRank(p, Rank.ADMINISTRATOR, false)) p.getInventory().addItem(new ItemStack(Material.COMPASS));
        p.setGameMode(GameMode.CREATIVE);
        Log.NORMAL.write(p, "hat den BuildMode betreten.");
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!Script.hasRank(online, Rank.DEVELOPER, false)) {
                if (Team.getTeam(online) != Team.Teams.BAU) {
                    online.hidePlayer(NewRoleplayMain.getInstance(), p);
                }
            }
        }
        Script.updateListname(p);
    }
}
