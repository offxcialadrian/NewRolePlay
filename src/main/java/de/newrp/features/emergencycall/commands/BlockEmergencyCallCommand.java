package de.newrp.features.emergencycall.commands;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.BlockPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            player.sendMessage(Messages.ERROR + "/blocknotruf [list, block, unblock]");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "block":
                this.block(player, playerFaction, args);
                break;
            case "list":
                this.list(player, playerFaction);
                break;
            case "unblock":
                this.unblock(player, playerFaction, args);
                break;
        }




        return false;
    }

    private void unblock(final Player player, final Beruf.Berufe playerFaction, final String[] args) {
        final String offlinePlayerName = args[1];
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(offlinePlayerName);

            if(offlinePlayer == null) {
                player.sendMessage(Messages.ERROR + "Es existiert kein Spieler mit dem Namen!");
                return;
            }

            final boolean isLocked = this.emergencyCallService.isBlocked(offlinePlayer, playerFaction);
            if(!isLocked) {
                player.sendMessage(Messages.ERROR + "Die Notruf von diesem Spieler sind nicht blockiert!");
                return;
            }

            this.emergencyCallService.unblockEmergencyCalls(offlinePlayer, playerFaction);
            if(offlinePlayer.isOnline()) {
                ((Player) offlinePlayer).sendMessage(this.emergencyCallService.getPrefix() + "Deine Notruf bei " + (playerFaction == Beruf.Berufe.POLICE ? "der Polizei" : "dem Rettungsdienst") + " wurde entsperrt!");
            }
            playerFaction.sendMessage(this.emergencyCallService.getPrefix() + Script.getName(player) + " hat die Notrufe von " + Script.getName(offlinePlayer) + " entsperrt!");
        });
    }

    private void block(final Player player, final Beruf.Berufe playerFaction, final String[] args) {
        final String offlinePlayerName = args[1];
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(offlinePlayerName);

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
            }
            playerFaction.sendMessage(this.emergencyCallService.getPrefix() + Script.getName(player) + " hat die Notrufe von " + Script.getName(offlinePlayer) + " blockiert!");
        });
    }

    private void list(final Player player, final Beruf.Berufe playerFaction) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            final List<BlockPlayerInfo> blockPlayerInfos = this.emergencyCallService.getAllBlockedPlayers(playerFaction);
            if(blockPlayerInfos.isEmpty()) {
                player.sendMessage(this.emergencyCallService.getPrefix() + "Es gibt keine blockierten Nutzer aktuell im Notrufsystem");
                return;
            }

            player.sendMessage(this.emergencyCallService.getPrefix() + "Folgende Spieler sind blockiert vom Notrufsystem §7§o(" + blockPlayerInfos.size() + ")");
            for (BlockPlayerInfo blockPlayerInfo : blockPlayerInfos) {
                player.sendMessage(this.emergencyCallService.getPrefix() + blockPlayerInfo.userName() + " " + Messages.ARROW + " §c§lBLOCKIERT");
            }
        });
    }
}
