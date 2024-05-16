package de.newrp.features.emergencycall.commands;

import de.newrp.API.Messages;
import de.newrp.Berufe.Beruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DoneEmergencyCallCommand implements CommandExecutor {

    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final Player player = (Player) commandSender;
        final Beruf.Berufe playerFaction = Beruf.getBeruf(player);

        if(playerFaction == null) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(playerFaction != Beruf.Berufe.POLICE && playerFaction != Beruf.Berufe.RETTUNGSDIENST) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(!this.emergencyCallService.factionMemberHasAccepted(player)) {
            player.sendMessage(Messages.ERROR + "Du hast keinen aktiven Notruf!");
            return false;
        }



        this.emergencyCallService.doneEmergencyCall(player, this.emergencyCallService.getAcceptedEmergencyCallByFactionMember(player).get());
        return false;
    }
}
