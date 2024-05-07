package de.newrp.features.deathmatcharena;

import de.newrp.features.deathmatcharena.data.DeathmatchArenaStats;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface IDeathmatchArenaService {

    void joinDeathmatchArena(final Player player);

    boolean isInDeathmatch(final Player player, boolean forceRemoveIfIn);

    void equipWeaponsAndDrugs(final Player player);

    void quitDeathmatchArena(final Player player);

    void banDeathmatchArena(final Player player, final long duration, final String reason, final Player targetPlayer);

    void unbanDeathmatchArena(final Player player, final Player targetPlayer);

    void printStatsToPlayer(final Player player);

    void sendMessageToArenaMembers(final String message);

    String getPrefix();

    List<Location> getSpawnPoints();

    Location getRandomSpawnPoint();

    DeathmatchArenaStats getStats(final Player player);




}
