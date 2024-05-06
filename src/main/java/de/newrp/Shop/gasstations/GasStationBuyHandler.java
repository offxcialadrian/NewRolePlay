package de.newrp.Shop.gasstations;

import de.newrp.API.Messages;
import de.newrp.Shop.Shops;
import de.newrp.Shop.generic.GenericBuyHandler;
import org.bukkit.entity.Player;

public class GasStationBuyHandler implements GenericBuyHandler {

    @Override
    public boolean buyItem(Player player, Shops shop, final Object... args) {
        player.sendMessage(Messages.INFO + "Tankstelle :3");
        return false;
    }

}
