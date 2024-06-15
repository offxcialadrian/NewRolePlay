package de.newrp.features.bizwar.listener;

import de.newrp.API.Script;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.bizwar.data.ActiveBizWarInformation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

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
            activeBizWarInformation.setCurrentAttackerPoints(activeBizWarInformation.getCurrentAttackerPoints() + 1);
        }

        if(killer == null) {
            activeBizWarInformation.getAttackerOrganisation().sendMessage(this.bizWarService.getPrefix() + "§c§l" + player.getName() + " §7ist gestorben.");
            activeBizWarInformation.getDefenderOrganisation().sendMessage(this.bizWarService.getPrefix() + "§c§l" + player.getName() + " §7ist gestorben.");
        } else {
            activeBizWarInformation.getAttackerOrganisation().sendMessage(this.bizWarService.getPrefix() + "§c" + player.getName() + " §7wurde von §c" + Script.getName(killer) + " §7getötet.");
            activeBizWarInformation.getDefenderOrganisation().sendMessage(this.bizWarService.getPrefix() + "§c" + player.getName() + " §7wurde von §c" + Script.getName(killer) + " §7getötet.");
        }
    }
}
