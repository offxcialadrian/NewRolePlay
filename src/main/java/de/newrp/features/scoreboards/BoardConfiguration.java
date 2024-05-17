package de.newrp.features.scoreboards;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface BoardConfiguration {

    String getScoreboardTitle(final Player player);

    List<String> getLines(final Player player);

    void update(final Player player, final Map<String, String> argumentMap);

}
