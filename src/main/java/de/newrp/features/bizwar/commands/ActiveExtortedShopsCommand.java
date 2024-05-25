package de.newrp.features.bizwar.commands;

import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shops;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActiveExtortedShopsCommand implements CommandExecutor {

    private final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;

        player.sendMessage(bizWarService.getPrefix() + "Folgende Businesses werden aktuell erpresst:");
        for (Organisation organisation : Organisation.values()) {
            for (Shops shops : bizWarService.getShopsOfFaction(organisation)) {
                player.sendMessage(bizWarService.getPrefix() + " §7- §e" + shops.getPublicName() + " §7(§e" + organisation.getName()+ "§7)");
            }
        }

        return false;
    }
}
