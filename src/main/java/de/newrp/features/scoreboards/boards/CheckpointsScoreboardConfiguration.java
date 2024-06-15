package de.newrp.features.scoreboards.boards;

import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.scoreboards.BoardConfiguration;
import de.newrp.features.scoreboards.IScoreboardService;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class CheckpointsScoreboardConfiguration implements BoardConfiguration {

    private final IScoreboardService scoreboardService = DependencyContainer.getContainer().getDependency(IScoreboardService.class);

    @Override
    public String getScoreboardTitle(Player player) {
        return "§cNRP × Checkpoints";
    }

    @Override
    public List<String> getLines(Player player) {
        return List.of("§bCheckpoints§8:", "[checkpoints]", "§1");
    }

    @Override
    public void update(Player player, Map<String, String> argumentMap) {
        scoreboardService.updateLine(player, "", "§8» §a" + argumentMap.get("checkpoints"));
    }
}
