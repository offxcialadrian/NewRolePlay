package de.newrp.Organisationen.Contract.handler;

import de.newrp.API.Activities;
import de.newrp.API.Activity;
import de.newrp.API.Script;
import de.newrp.Organisationen.Contract.model.Contract;
import de.newrp.Organisationen.Organisation;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ContractHandler implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player killed = e.getEntity();
        Player killer = killed.getKiller();
        if (killer == null) return;

        if (!Organisation.hasOrganisation(killer)) return;
        Organisation orga = Organisation.getOrganisation(killer);
        if (orga != Organisation.HITMEN) return;
        if (!Contract.hasContract(killed)) return;
        if (DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class).isInDeathmatch(killed, false)) return;

        Contract ct = Contract.getContract(killed);
        assert ct != null;
        Script.addEXP(killer, Script.getRandom(8, 16), true);
        orga.addExp(Script.getRandom(20, 30));
        orga.sendMessage(Contract.PREFIX + Script.getName(killer) + " hat " + Script.getName(killed) + " getötet.");
        killed.sendMessage(Contract.PREFIX + "Du wurdest getötet weil ein Kopfgeld auf dich ausgesetzt wurde.");
        Activity.grantActivity(Script.getNRPID(killer), Activities.CT_KILL);
        orga.addKasse(ct.getPrice());
        Contract.remove(ct);
    }
}
