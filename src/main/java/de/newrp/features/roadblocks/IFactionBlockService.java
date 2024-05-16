package de.newrp.features.roadblocks;

import de.newrp.Berufe.Beruf;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IFactionBlockService {

    void placeFactionBlock(final Player player, final Location clickedBlock, final Beruf.Berufe faction);

    void deleteFactionBlock(final Player player, final Block block, final Beruf.Berufe faction);

    int getFactionBlockAmount(final Beruf.Berufe faction);

    void clearFactionBlockAmount(final Player player, final Beruf.Berufe faction);

    int getMaxFactionBlockAmount(final Beruf.Berufe faction);

    String getPrefix(final Beruf.Berufe faction);

    String getType(final Beruf.Berufe faction);

    void activateSystemForPlayer(final Player player, final Beruf.Berufe faction);

    Player isSystemActive(final Beruf.Berufe faction);

    void deactivateSystemForPlayer(final Player player, final Beruf.Berufe faction);

    ItemStack getItem();
}
