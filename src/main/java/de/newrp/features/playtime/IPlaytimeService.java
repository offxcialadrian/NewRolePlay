package de.newrp.features.playtime;

import de.newrp.features.playtime.data.PlaytimeData;
import org.bukkit.entity.Player;

public interface IPlaytimeService {

    void increasePlaytime(final Player player);

    void handleQuit(final Player player);

    void loadPlaytime(final Player player);

    PlaytimeData getPlaytime(final Player player);

}
