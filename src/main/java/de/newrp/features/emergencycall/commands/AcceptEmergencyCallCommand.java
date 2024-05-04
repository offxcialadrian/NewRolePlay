package de.newrp.features.emergencycall.commands;

import de.newrp.API.Messages;
import de.newrp.API.Route;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Player.Notruf;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AcceptEmergencyCallCommand extends Command {

    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);

    protected AcceptEmergencyCallCommand() {
        super("acceptnotruf");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final Beruf.Berufe playerFaction = Beruf.getBeruf(player);

        if(playerFaction == null) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(playerFaction != Beruf.Berufe.POLICE && playerFaction != Beruf.Berufe.RETTUNGSDIENST) {
            return false;
        }

        if(args.length < 1) {
            player.sendMessage(Messages.ERROR + "/acceptnotruf [Spieler]");
            return false;
        }

        if(this.emergencyCallService.factionMemberHasAccepted(player)) {
            player.sendMessage(Messages.ERROR + "Du hast bereits einen aktiven Notruf!");
            return false;
        }

        final String targetPlayerString = args[0];
        final Player targetPlayer = Bukkit.getPlayer(targetPlayerString);
        if(targetPlayer == null) {
            player.sendMessage(Messages.ERROR + "Der Spieler ist nicht online");
            return false;
        }

        final Optional<EmergencyCall> emergencyCallOptional = this.emergencyCallService.getEmergencyCallByPlayer(targetPlayer, playerFaction);

        if(!emergencyCallOptional.isPresent()) {
            player.sendMessage(Messages.ERROR + "Dieser Spieler hat keinen aktiven Notruf");
            return false;
        }

        final EmergencyCall emergencyCall = emergencyCallOptional.get();
        if(this.emergencyCallService.hasBeenAccepted(emergencyCall)) {
            player.sendMessage(Messages.ERROR + "Dieser Notruf wurde bereits angenommen");
            return false;
        }

        this.emergencyCallService.acceptEmergencyCall(player, emergencyCall);
        playerFaction.sendMessage(Notruf.PREFIX  + "§6" + Script.getName(player) + " §7hat den Notruf von §6" + Script.getName(targetPlayer) + " §7angenommen.");
        targetPlayer.sendMessage(Notruf.PREFIX + "Die Hilfe ist in ca. " + this.emergencyCallService.estimateArrival(player.getLocation().distance(emergencyCall.location())) + " Sekunden bei Ihnen, bitte warten Sie vorort.");
        new Route(player.getName(), 0, player.getLocation(), emergencyCall.location()).start();
        return false;
    }
}
