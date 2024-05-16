package de.newrp.features.deathmatcharena.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
@RequiredArgsConstructor
@Accessors(fluent = true)
public class DeathmatchJoinData {

    private final Player player;
    private final ItemStack[] contentsInventory;
    private final ItemStack[] contentsArmor;
    private final long joinTime = System.currentTimeMillis();
    private final Location oldLocation;
    private final DeathmatchArenaStats stats = new DeathmatchArenaStats();

}
