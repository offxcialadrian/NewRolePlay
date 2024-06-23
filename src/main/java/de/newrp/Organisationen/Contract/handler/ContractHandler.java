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

    }
}
