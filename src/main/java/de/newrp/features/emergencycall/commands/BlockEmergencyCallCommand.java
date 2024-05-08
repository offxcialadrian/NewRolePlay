package de.newrp.features.emergencycall.commands;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BlockEmergencyCallCommand implements CommandExecutor {

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

        if(args.length < 1) {
            player.sendMessage(Messages.ERROR + "/blocknotruf [Spieler]");
            return false;
        }

        final String offlinePlayerName = args[0];
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[0]);

            if(offlinePlayer == null) {
                player.sendMessage(Messages.ERROR + "Es existiert kein Spieler mit dem Namen!");
                return;
            }

            final boolean isLocked = this.emergencyCallService.isBlocked(offlinePlayer, playerFaction);
            if(isLocked) {
                player.sendMessage(Messages.ERROR + "Die Notruf von diesem Spieler sind bereits blockiert!");
                return;
            }

            this.emergencyCallService.blockEmergencyCalls(offlinePlayer, playerFaction);
            if(offlinePlayer.isOnline()) {
                ((Player) offlinePlayer).sendMessage(this.emergencyCallService.getPrefix() + "Deine Notruf bei " + (playerFaction == Beruf.Berufe.POLICE ? "der Polizei" : "dem Rettungsdienst") + " wurden blockiert!");
                playerFaction.sendMessage(this.emergencyCallService.getPrefix() + Script.getName(player) + " hat die Notrufe von " + Script.getName(offlinePlayer) + " blockiert!");
            }
        });


        return false;
    }
}
