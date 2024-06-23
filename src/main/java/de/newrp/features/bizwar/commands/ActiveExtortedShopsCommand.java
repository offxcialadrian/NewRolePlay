package de.newrp.features.bizwar.commands;

import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shops;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.bizwar.config.BizWarConfig;
import de.newrp.features.bizwar.config.BizWarShopConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ActiveExtortedShopsCommand implements CommandExecutor {

    private final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;

        player.sendMessage(bizWarService.getPrefix() + "Folgende Businesses werden aktuell erpresst:");
        final Set<Integer> extortedShops = new HashSet<>();
        for (Organisation organisation : Organisation.values()) {
            for (Shops shops : bizWarService.getShopsOfFaction(organisation)) {
                player.sendMessage(bizWarService.getPrefix() + " §7- §e" + shops.getPublicName() + " §7(§e" + organisation.getName()+ "§7)");
                extortedShops.add(shops.getID());
            }
        }

        final BizWarConfig bizWarConfig = DependencyContainer.getContainer().getDependency(BizWarConfig.class);
        if (!bizWarConfig.getShopConfigs().isEmpty()) {
            player.sendMessage(bizWarService.getPrefix() + "§7Folgende Businesses können noch erpresst werden:");
            for (BizWarShopConfig shopConfig : bizWarConfig.getShopConfigs()) {
                if (!extortedShops.contains(shopConfig.getShopId())) {
                    player.sendMessage(bizWarService.getPrefix() + " §7- §e" + Shops.getShop(shopConfig.getShopId()).getPublicName());
                }
            }
        }

        return false;
    }
}
