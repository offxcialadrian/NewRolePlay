package de.newrp.features.roadblocks.commands;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.roadblocks.IFactionBlockService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RoadBlockCommand implements CommandExecutor {

    private final IFactionBlockService factionBlockService = DependencyContainer.getContainer().getDependency(IFactionBlockService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final Beruf.Berufe faction = Beruf.getBeruf(player);

        if(faction != Beruf.Berufe.POLICE) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(!Duty.isInDuty(player)) {
            player.sendMessage(Messages.ERROR + "Du bist nicht im Dienst!");
            return false;
        }

        final Abteilung.Abteilungen abteilung = Beruf.getAbteilung(player);
        if(!Beruf.hasAbteilung(player, Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        final Player isSystemActive = this.factionBlockService.isSystemActive(faction);

        if(args.length > 0 && args[0].equalsIgnoreCase("reset")) {
            if(abteilung == Abteilung.Abteilungen.ABTEILUNGSLEITUNG || abteilung == Abteilung.Abteilungen.POLIZEIVIZE || abteilung == Abteilung.Abteilungen.POLIZEIPRÄSIDENT) {
                if(isSystemActive != null) {
                    this.factionBlockService.deactivateSystemForPlayer(isSystemActive, faction);
                }
                this.factionBlockService.clearFactionBlockAmount(player, faction);
                return false;
            }
        }

        if(isSystemActive != null && isSystemActive.getUniqueId() != player.getUniqueId()) {
            player.sendMessage(this.factionBlockService.getPrefix(faction) + Script.getName(isSystemActive) + " operiert derzeit das RoadBlock System!");
            return false;
        } else if(isSystemActive != null && isSystemActive.getUniqueId() == player.getUniqueId()) {
            this.factionBlockService.deactivateSystemForPlayer(player, faction);
            return false;
        }

        this.factionBlockService.activateSystemForPlayer(player, faction);
        return false;
    }
}
