package de.newrp.features.bizwar.commands;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.bizwar.data.ActiveBizWarInformation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JoinFightCommand implements CommandExecutor {

    private final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final Organisation organisation = Organisation.getOrganisation(player);

        if (organisation == null) {
            player.sendMessage(Messages.ERROR + "§cDu bist in keiner Organisation!");
            return false;
        }

        if(Script.getLevel(player) < 3) {
            player.sendMessage(Messages.ERROR + "§cDu musst Level 3 sein um einem Kampf beizutreten!");
            return false;
        }

        /*if(organisation == Organisation.HITMEN) {
            player.sendMessage(Messages.ERROR + "§cDu kannst keinem Kampf beitreten");
            return false;
        }*/

        final ActiveBizWarInformation activeBizWarInformation = this.bizWarService.getBizWarOfOrganisation(organisation);
        if(activeBizWarInformation == null) {
            player.sendMessage(Messages.ERROR + "§cDeine Organisation ist in keinem Kampf!");
            return false;
        }

        this.bizWarService.joinBizWar(activeBizWarInformation, player, organisation);
        return false;
    }
}
