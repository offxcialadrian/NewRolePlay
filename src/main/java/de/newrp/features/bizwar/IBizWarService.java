package de.newrp.features.bizwar;

import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shops;
import de.newrp.features.bizwar.data.ActiveBizWarInformation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public interface IBizWarService {

    void loadActiveExtortions();

    void startBizWar(final Shops shop, final Player player, final Organisation organisation, final Organisation defenderOrganisation);

    void finishBizWar(final Shops shop);

    void joinBizWar(final ActiveBizWarInformation bizWarInformation, final Player player, final Organisation organisation);

    boolean isBizWarRunning(final Shops shops);

    ActiveBizWarInformation getBizWarOfOrganisation(final Organisation organisation);

    long getActiveCooldownOnShop(final Shops shops);

    long getActiveCooldownOnOrganisation(final Organisation organisation);

    Set<Shops> getShopsOfFaction(final Organisation organisation);

    Organisation getCurrentOwnerOfShop(final Shops shop);

    void setOwnerOfShop(final Shops shop, final Organisation organisation);

    void equipPlayerForBizWar(final Player player);

    String getPrefix();


}
