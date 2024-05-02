package de.newrp.Shop.generic;

import de.newrp.Shop.Shops;
import org.bukkit.entity.Player;

public interface GenericBuyHandler {

    boolean buyItem(final Player player, final Shops shop, final Object... args);

}
