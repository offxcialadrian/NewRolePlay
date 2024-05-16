package de.newrp.features.emergencycall.commands;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteEmergencyCallCommand implements CommandExecutor {

    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
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



        final EmergencyCall emergencyCall = this.emergencyCallService.getAcceptedEmergencyCallByFactionMember(player).get();
        this.emergencyCallService.dropEmergencyCall(emergencyCall);
        playerFaction.sendMessage(this.emergencyCallService.getPrefix() + Script.getName(player) + " hat den Notruf von " + Script.getName(emergencyCall.sender()) + " gelöscht");
        emergencyCall.sender().sendMessage(this.emergencyCallService.getPrefix() + "Dein Notruf wurde gelöscht");
        return false;
    }
}
