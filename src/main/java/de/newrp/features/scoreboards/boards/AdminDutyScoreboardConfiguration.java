package de.newrp.features.scoreboards.boards;

import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Government.Stadtkasse;
import de.newrp.Player.AFK;
import de.newrp.Ticket.TicketCommand;
import de.newrp.Ticket.TicketTopic;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.scoreboards.BoardConfiguration;
import de.newrp.features.scoreboards.IScoreboardService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDutyScoreboardConfiguration implements BoardConfiguration {

    private final IScoreboardService scoreboardService = DependencyContainer.getContainer().getDependency(IScoreboardService.class);

    @Override
    public String getScoreboardTitle(Player player) {
        return Script.getRank(player).getName() + " Dienst";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> list = new ArrayList<>();
        list.add("§0");
        list.add("§bOnline§8:");
        list.add("[online]");
        list.add("§1");
        list.add("§bTickets§8:");
        for (TicketTopic value : TicketTopic.values()) {
            list.add("[" + value.name().toLowerCase() + "]");
        }
        list.add("§2");
        if(Script.hasRank(player, Rank.ADMINISTRATOR, false)) {
            list.add("§bStadtkasse§8:");
            list.add("[stadtkasse]");
        }
        return list;
    }

    @Override
    public void update(Player player, Map<String, String> argumentMap) {
        int stadtkasse = Stadtkasse.getStadtkasse();
        DecimalFormat df = new DecimalFormat("#,###");
        HashMap<TicketTopic, Integer> amount = TicketCommand.getTicketAmount();

        scoreboardService.updateLine(player, "online", " §8» §a" + (Bukkit.getOnlinePlayers().size() - AFK.afk.size()) + " §8| §c" + AFK.afk.size() + " §8| §e" + Bukkit.getOnlinePlayers().size());
        for (TicketTopic value : TicketTopic.values()) {
            scoreboardService.updateLine(player, value.name().toLowerCase(), " §8» §e" + value.getName() + ": " + amount.get(value));
        }
        scoreboardService.updateLine(player, "stadtkasse", " §8» §e" + df.format(stadtkasse) + "€");
    }
}
