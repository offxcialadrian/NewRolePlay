package de.newrp.features.bizwar.listener;

import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shops;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.bizwar.config.BizWarConfig;
import de.newrp.features.bizwar.config.BizWarShopConfig;
import de.newrp.features.bizwar.data.ActiveBizWarInformation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BizWarPlayerDeathListener implements Listener {

    private final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();

        if(!bizWarService.isMemberOfBizWar(player)) {
            return;
        }

        final Player killer = player.getKiller();
        final ActiveBizWarInformation activeBizWarInformation = this.bizWarService.getBizWarOfPlayer(player);
        if(activeBizWarInformation == null) {
            return;
        }

        final boolean isAttacker = activeBizWarInformation.getAttackerOrganisation().getMembers().contains(player);
        if(isAttacker) {
            activeBizWarInformation.setCurrentDefenderPoints(activeBizWarInformation.getCurrentDefenderPoints() + 1);
        } else {
            activeBizWarInformation.setCurrentAttackerPoints(activeBizWarInformation.getCurrentAttackerPoints() + (isMVO(activeBizWarInformation.getDefenderOrganisation()) ? 2 : 1));
        }

        if(killer == null) {
            activeBizWarInformation.getAttackerOrganisation().sendMessage(this.bizWarService.getPrefix() + "§c§l" + player.getName() + " §7ist gestorben.");
            activeBizWarInformation.getDefenderOrganisation().sendMessage(this.bizWarService.getPrefix() + "§c§l" + player.getName() + " §7ist gestorben.");
        } else {
            activeBizWarInformation.getAttackerOrganisation().sendMessage(this.bizWarService.getPrefix() + "§c" + player.getName() + " §7wurde von §c" + Script.getName(killer) + " §7getötet.");
            activeBizWarInformation.getDefenderOrganisation().sendMessage(this.bizWarService.getPrefix() + "§c" + player.getName() + " §7wurde von §c" + Script.getName(killer) + " §7getötet.");
        }
    }

    private boolean isMVO(Organisation orga) { // Most Valuable Organisation :D
        Organisation organisation = getMVO();
        if (organisation == null) return false;
        return organisation == orga;
    }

    private Organisation getMVO() {
        final HashMap<Organisation, Integer> extorted = new HashMap<>();
        for (Organisation orga : Organisation.values()) {
            extorted.put(orga, bizWarService.getShopsOfFaction(orga).size());
        }

        int max = 0;
        Organisation organisation = null;
        int eql = -1; // Checks if there are multiple MVOs -> soll Vorteile mit größerer OrgaID verhindern
        for (Organisation orga : extorted.keySet()) {
            if (extorted.get(orga) > max) {
                max = extorted.get(orga);
                organisation = orga;
            } else if (extorted.get(orga) == max) {
                eql = extorted.get(orga);
            }
        }

        if (eql >= 0)
            if (eql == max) return null;

        return organisation;
    }
}
