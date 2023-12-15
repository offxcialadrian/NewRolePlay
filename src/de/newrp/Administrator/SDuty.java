package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Government.Stadtkasse;
import de.newrp.Ticket.TicketCommand;
import de.newrp.Ticket.TicketTopic;
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
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SDuty implements CommandExecutor, Listener {

    private static final List<String> sduty = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.SUPPORTER, false)) {
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

        return false;
    }

    public static Boolean isSDuty(Player p) {
        return sduty.contains(p.getName());
    }

    public static void removeSDuty(Player p) {
        Log.NORMAL.write(p, "hat den Supporter-Dienst verlassen.");
        if (BuildMode.isInBuildMode(p)) {
            BuildMode.removeBuildMode(p);
            p.sendMessage(BuildMode.PREFIX + "Du hast den BuildMode verlassen.");
            Script.sendTeamMessage(p, ChatColor.YELLOW, "hat den BuildMode verlassen.", true);
        }
        sduty.remove(p.getName());
        Script.sendTeamMessage(p, ChatColor.RED, "hat den Supporter-Dienst verlassen.", false);
        p.sendMessage(Messages.INFO + "Du darfst nun wieder am aktiven Spielgeschehen teilnehmen.");
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Chache.loadScoreboard(p);
        p.setPlayerListName(Script.getName(p));
        p.setCollidable(true);
        p.setGameMode(GameMode.SURVIVAL);
        p.setFlying(false);
        p.setAllowFlight(false);
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        if (board.getTeam("nopush") == null) {
            Team t = board.registerNewTeam("nopush");
            t.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            t.addEntry(p.getName());
        }
    }

    public static void setSDuty(Player p) {
        Log.NORMAL.write(p, "hat den Supporter-Dienst betreten.");
        if(Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.setAllowFlight(true);
            p.setFlying(true);
        }
        sduty.add(p.getName());
        Script.sendTeamMessage(p, ChatColor.RED, "hat den Supporter-Dienst betreten.", false);
        p.sendMessage(Messages.INFO + "Du darfst nun nicht mehr am aktiven Spielgeschehen teilnehmen.");
        Chache.saveScoreboard(p);
        int stadtkasse = Stadtkasse.getStadtkasse();
        DecimalFormat df = new DecimalFormat("#,###");

        ScoreboardManager m = Bukkit.getScoreboardManager();
        Scoreboard b = m.getNewScoreboard();

        Objective o = b.registerNewObjective("Gold", "");
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        o.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "§cNRP × Support");
        HashMap<TicketTopic, Integer> amount = TicketCommand.getTicketAmount();

        Score platzhalter1 = o.getScore(ChatColor.RED + "");
        Score platzhalter2 = o.getScore(ChatColor.YELLOW + "");
        Score score1 = o.getScore(ChatColor.GRAY + "§bOnline§8:");
        Score score2 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + Bukkit.getOnlinePlayers().size() + "§8/§e" + Bukkit.getMaxPlayers());
        Score score3 = o.getScore(ChatColor.GRAY + "§bTickets§8:");
        Score score4 = o.getScore(ChatColor.DARK_AQUA + " §8» §eBug: " + amount.get(TicketTopic.BUG));
        Score score5 = o.getScore(ChatColor.DARK_AQUA + " §8» §eFrage: " + amount.get(TicketTopic.FRAGE));
        Score score6 = o.getScore(ChatColor.DARK_AQUA + " §8» §eSpieler: " + amount.get(TicketTopic.SPIELER));
        Score score7 = o.getScore(ChatColor.DARK_AQUA + " §8» §eAccount: " + amount.get(TicketTopic.ACCOUNT));
        Score score8 = o.getScore(ChatColor.GRAY + "");
        Score score9 = o.getScore(ChatColor.GRAY + "§bStadtkasse§8:");
        Score score10 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + df.format(stadtkasse) + "€");
        p.setPlayerListName("§cNRP §8× §9" + p.getName());
        p.setCollidable(false);
        platzhalter1.setScore(9);
        score1.setScore(8);
        score2.setScore(7);
        platzhalter2.setScore(6);
        score3.setScore(5);
        score4.setScore(4);
        score5.setScore(3);
        score6.setScore(2);
        score7.setScore(1);
        if (Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            score8.setScore(0);
            score9.setScore(-1);
            score10.setScore(-2);
        }
        p.setScoreboard(b);
    }

    public static void updateScoreboard() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isSDuty(p)) {
                int stadtkasse = Stadtkasse.getStadtkasse();
                DecimalFormat df = new DecimalFormat("#,###");
                ScoreboardManager m = Bukkit.getScoreboardManager();
                Scoreboard b = m.getNewScoreboard();
                Objective o = b.registerNewObjective("Gold", "");
                o.setDisplaySlot(DisplaySlot.SIDEBAR);
                o.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "§cNRP × Support");
                HashMap<TicketTopic, Integer> amount = TicketCommand.getTicketAmount();

                Score platzhalter1 = o.getScore(ChatColor.RED + "");
                Score platzhalter2 = o.getScore(ChatColor.YELLOW + "");
                Score score1 = o.getScore(ChatColor.GRAY + "§bOnline§8:");
                Score score2 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + Bukkit.getOnlinePlayers().size() + "§8/§e" + Bukkit.getMaxPlayers());
                Score score3 = o.getScore(ChatColor.GRAY + "§bTickets§8:");
                Score score4 = o.getScore(ChatColor.DARK_AQUA + " §8» §eBug: " + amount.get(TicketTopic.BUG));
                Score score5 = o.getScore(ChatColor.DARK_AQUA + " §8» §eFrage: " + amount.get(TicketTopic.FRAGE));
                Score score6 = o.getScore(ChatColor.DARK_AQUA + " §8» §eSpieler: " + amount.get(TicketTopic.SPIELER));
                Score score7 = o.getScore(ChatColor.DARK_AQUA + " §8» §eAccount: " + amount.get(TicketTopic.ACCOUNT));
                Score score8 = o.getScore(ChatColor.GRAY + "");
                Score score9 = o.getScore(ChatColor.GRAY + "§bStadtkasse§8:");
                Score score10 = o.getScore(ChatColor.DARK_AQUA + " §8» §e" + df.format(stadtkasse) + "€");
                p.setPlayerListName("§cNRP §8× §9" + p.getName());
                p.setCollidable(false);
                platzhalter1.setScore(9);
                score1.setScore(8);
                score2.setScore(7);
                platzhalter2.setScore(6);
                score3.setScore(5);
                score4.setScore(4);
                score5.setScore(3);
                score6.setScore(2);
                score7.setScore(1);
                if (Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    score8.setScore(0);
                    score9.setScore(-1);
                    score10.setScore(-2);
                }
                p.setScoreboard(b);
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
            if (e.getFoodLevel() > ((Player) e.getEntity()).getFoodLevel()) return;
            Player p = (Player) e.getEntity();
            if (isSDuty(p)) e.setCancelled(true);
        }
    }


}
