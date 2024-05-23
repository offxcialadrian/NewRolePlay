package de.newrp.features.playertracker;

import org.bukkit.entity.Player;

public interface IPlayerTrackerService {

    void trackCurrentPlayerCount();

    void increaseUniquePlayerSize(final Player player);

    int getHighestPlayerCount();

    int getUniquePlayerSize();

    long getTimestamp();

}
