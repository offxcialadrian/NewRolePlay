package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import de.newrp.Ticket.TicketCommand;
import de.newrp.Ticket.TicketTopic;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.scoreboards.IScoreboardService;
import de.newrp.features.scoreboards.boards.AdminDutyScoreboardConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SDuty implements CommandExecutor, Listener {

    private static final List<String> sduty = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.DEVELOPER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 1 && Script.hasRank(p, Rank.OWNER, false) && args[0].equalsIgnoreCase("removeall")) {
            for(Player all : Bukkit.getOnlinePlayers()) {
                if(isSDuty(all)) {
                    removeSDuty(all);
                }
            }
            Script.sendTeamMessage("§8[§cSDuty§8] §cAlle Supporter wurden aus dem Supporter-Dienst entfernt.");
            return true;
        }

        if (isSDuty(p)) {
            removeSDuty(p);
            return true;
        }

        setSDuty(p);
        long time = System.currentTimeMillis();
        Long lastUsage = AFK.lastDmg.get(p.getName());
        if (lastUsage != null && lastUsage + 15 * 1000 > time) {
            for(Player team : Script.getNRPTeam()) {
                if(Script.hasRank(team, Rank.ADMINISTRATOR, false)) {
                    team.sendMessage(AntiCheatSystem.PREFIX + "§cVerdacht auf Supporter-Dienst Missbrauch bei " + Script.getName(p) + " (Supporter-Dienst nach Schaden)");
                }
            }
        }

        return false;
    }

    public static Boolean isSDuty(Player p) {
        return sduty.contains(p.getName());
    }

    public static Boolean isSDuty(UUID p) {
        return sduty.contains(Bukkit.getPlayer(p).getName());
    }

    public static void removeSDuty(Player p) {
        Log.NORMAL.write(p, "hat den Supporter-Dienst verlassen.");
        if (BuildMode.isInBuildMode(p)) {
            BuildMode.removeBuildMode(p);
            p.sendMessage(BuildMode.PREFIX + "Du hast den BuildMode verlassen.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den BuildMode verlassen.", true);
        }
        //Script.sendTeamMessage(p, ChatColor.RED, "hat den Supporter-Dienst verlassen.", false);
        Script.sendTeamMessage("§8[" + ChatColor.RED + "§LT§8] §c" + p.getName() + " hat den Supporter-Dienst verlassen.");
        p.sendMessage(Messages.INFO + "Du darfst nun wieder am aktiven Spielgeschehen teilnehmen.");
        final IScoreboardService scoreboardService = DependencyContainer.getContainer().getDependency(IScoreboardService.class);
        scoreboardService.hideScoreboard(p);
        sduty.remove(p.getName());
        Cache.loadScoreboard(p);
        p.setCollidable(true);
        p.setGameMode(GameMode.SURVIVAL);
        p.setFlying(false);
        p.setAllowFlight(false);
        Script.updateListname(p);
    }

    public static void setSDuty(Player p) {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").removeEntry(p.getName());
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("anrps").addEntry(p.getName());
        Log.NORMAL.write(p, "hat den Supporter-Dienst betreten.");
        if(Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.setAllowFlight(true);
            p.setFlying(true);
        }
        sduty.add(p.getName());
        // Script.sendTeamMessage(p, ChatColor.RED, "hat den Supporter-Dienst betreten.", false);
        Script.sendTeamMessage("§8[" + ChatColor.RED + "§LT§8] §c" + p.getName() + " hat den Supporter-Dienst betreten.");
        p.sendMessage(Messages.INFO + "Du darfst nun nicht mehr am aktiven Spielgeschehen teilnehmen.");
        Cache.saveScoreboard(p);
        final IScoreboardService scoreboardService = DependencyContainer.getContainer().getDependency(IScoreboardService.class);
        scoreboardService.setScoreboard(new AdminDutyScoreboardConfiguration(), p);
        updateScoreboard();

        Script.updateListname(p);
    }

    public static void updateScoreboard() {
        int stadtkasse = Stadtkasse.getStadtkasse();
        HashMap<TicketTopic, Integer> amount = TicketCommand.getTicketAmount();
        DecimalFormat df = new DecimalFormat("#,###");
        final IScoreboardService scoreboardService = DependencyContainer.getContainer().getDependency(IScoreboardService.class);

        for (Player p : Script.getNRPTeam()) {
            if (isSDuty(p)) {
                scoreboardService.updateBoard(new AdminDutyScoreboardConfiguration(), p, null);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player supporter = (Player) e.getEntity();
            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                if (isSDuty(supporter)) {
                    e.setCancelled(true);
                    Script.sendActionBar(damager, Messages.ERROR + "Du kannst " + Script.getName(supporter) + " nicht schlagen, da " + (Script.getGender(supporter).equals(Gender.MALE) ? "er" : "sie") + " im Supporter-Dienst ist.");
                }
            }
        }
    }

    @EventHandler
    public void onDamage1(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player)
            if (isSDuty((Player) e.getEntity())) e.setCancelled(true);
    }

    @EventHandler
    public void foodChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getFoodLevel() > e.getEntity().getFoodLevel()) return;
            Player p = (Player) e.getEntity();
            if (isSDuty(p)) e.setCancelled(true);
        }
    }


}
