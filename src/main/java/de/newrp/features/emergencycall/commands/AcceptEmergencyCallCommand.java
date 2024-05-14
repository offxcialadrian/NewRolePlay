package de.newrp.features.emergencycall.commands;

import de.newrp.API.Messages;
import de.newrp.API.Route;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AcceptEmergencyCallCommand implements CommandExecutor {

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
        if(!playerFaction.isDuty(player)) {
            player.sendMessage(Messages.ERROR + "Du bist nicht im Dienst!");
            return false;
        }

        if(this.emergencyCallService.factionMemberHasAccepted(player)) {
            player.sendMessage(Messages.ERROR + "Du hast bereits einen aktiven Notruf!");
            return false;
        }

        EmergencyCall emergencyCall = null;
        Player targetPlayer;


        if(args.length > 0) {
            final String targetPlayerString = args[0];
            targetPlayer = Bukkit.getPlayer(targetPlayerString);
            if(targetPlayer == null) {
                player.sendMessage(Messages.ERROR + "Der Spieler ist nicht online");
                return false;
            }

            final Optional<EmergencyCall> emergencyCallOptional = this.emergencyCallService.getEmergencyCallByPlayer(targetPlayer, playerFaction);

            if(!emergencyCallOptional.isPresent()) {
                player.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen aktiven Notruf!");
                return false;
            }

            emergencyCall = emergencyCallOptional.get();
        } else {
            final Optional<EmergencyCall> emergencyCallOptional = this.emergencyCallService.getAllOpenEmergencyCallsForFaction(playerFaction).stream().findFirst();

            if(!emergencyCallOptional.isPresent()) {
                player.sendMessage(Messages.ERROR + "Es gibt keinen aktiven Notruf");
                return false;
            }

            emergencyCall = emergencyCallOptional.get();
            targetPlayer = emergencyCall.sender();
        }


        if(this.emergencyCallService.hasBeenAccepted(emergencyCall)) {
            player.sendMessage(Messages.ERROR + "Dieser Notruf wurde bereits angenommen");
            return false;
        }

        this.emergencyCallService.acceptEmergencyCall(player, emergencyCall);
        playerFaction.sendMessage(this.emergencyCallService.getPrefix() + "§6" + Script.getName(player) + " §7hat den Notruf von §6" + Script.getName(targetPlayer) + " §7angenommen.");
        targetPlayer.sendMessage(this.emergencyCallService.getPrefix() + "Die Hilfe ist in ca. " + this.emergencyCallService.estimateArrival(player.getLocation().distance(emergencyCall.location())) + " Sekunden bei Ihnen, bitte warten Sie vorort.");
        new Route(player.getName(), 0, player.getLocation(), emergencyCall.location()).start();
        return false;
    }
}
