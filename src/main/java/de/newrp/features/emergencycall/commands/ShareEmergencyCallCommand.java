package de.newrp.features.emergencycall.commands;

import de.newrp.API.Messages;
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

public class ShareEmergencyCallCommand implements CommandExecutor {

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

        if(args.length == 0) {
            player.sendMessage(Messages.ERROR + "/sharenotruf [Spieler]");
            return false;
        }

        if(!this.emergencyCallService.factionMemberHasAccepted(player)) {
            player.sendMessage(Messages.ERROR + "Du hast keinen aktiven Notruf!");
            return false;
        }

        final EmergencyCall emergencyCall = this.emergencyCallService.getAcceptedEmergencyCallByFactionMember(player).get();
        final Player targetPlayer = Bukkit.getPlayer(args[0]);
        if(targetPlayer == null) {
            player.sendMessage(Messages.ERROR + "Kein Spieler mit dem Namen ist online!");
            return false;
        }

        final Beruf.Berufe targetPlayerFaction = Beruf.getBeruf(targetPlayer);
        if(targetPlayerFaction == null || targetPlayerFaction != playerFaction) {
            player.sendMessage(Messages.ERROR + "Du kannst keinen Notruf mit Spielern ausserhalb deiner Fraktion teilen!");
            return false;
        }

        playerFaction.sendMessage(this.emergencyCallService.getPrefix() + "Der aktive Noturf von " + Script.getName(player) + " wurde mit " + Script.getName(targetPlayer) + " geteilt");
        Script.sendClickableMessage(targetPlayer, this.emergencyCallService.getPrefix() + "Route anzeigen", "/navi " + emergencyCall.location().getBlockX() + "/" + emergencyCall.location().getBlockY() + "/" + emergencyCall.location().getBlockZ(), "Klicke um die Route anzeigen zu lassen.");
        targetPlayer.sendMessage(Messages.INFO + Script.getName(player) + " hat einen Notruf mit dir geteilt, klick auf Route anzeigen um mit zu kommen");
        return false;
    }

}
