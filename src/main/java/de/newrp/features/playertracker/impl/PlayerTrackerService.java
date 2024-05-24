package de.newrp.features.playertracker.impl;

import de.newrp.features.playertracker.IPlayerTrackerService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerTrackerService implements IPlayerTrackerService {

    private final Set<UUID> uniquePlayerList = new HashSet<>();
    private int highestPlayerCount = 0;
    private long lastTimestamp = System.currentTimeMillis();

    @Override
    public void trackCurrentPlayerCount() {
        final int onlinePlayerSize = Bukkit.getOnlinePlayers().size();
        if(onlinePlayerSize > this.highestPlayerCount) {
            this.highestPlayerCount = onlinePlayerSize;
            this.lastTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void increaseUniquePlayerSize(Player player) {
        this.trackCurrentPlayerCount();
        if(this.uniquePlayerList.contains(player.getUniqueId())) {
            return;
        }

        this.uniquePlayerList.add(player.getUniqueId());
    }

    @Override
    public int getHighestPlayerCount() {
        return this.highestPlayerCount;
    }

    @Override
    public int getUniquePlayerSize() {
        return this.uniquePlayerList.size();
    }

    @Override
    public long getTimestamp() {
        return this.lastTimestamp;
    }
}
