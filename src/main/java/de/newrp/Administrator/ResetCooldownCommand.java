package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Organisationen.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetCooldownCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        final Player player = (Player) commandSender;

        if(!Script.hasRank(player, Rank.ADMINISTRATOR, false)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length == 0) {
            player.sendMessage(Messages.ERROR + "/resetcooldown [Labor, Staatsbank, ATM, Breakin] ([Fraktion]/All)");
            return false;
        }

        final String factionName = args.length == 2 ? args[1] : null;
        final Organisation organisation = factionName != null ? Organisation.getOrganisation(factionName) : null;

        switch (args[0].toLowerCase()) {
            case "labor":
                LabBreakIn.cooldown = System.currentTimeMillis();
                Script.sendTeamMessage(player, ChatColor.LIGHT_PURPLE, "hat den Cooldown für das Labor zurückgesetzt!", false);
                if(LabBreakIn.schedulerID != 0) {
                    Bukkit.getScheduler().cancelTask(LabBreakIn.schedulerID);
                }
                LabBreakIn.progress = 0;
                LabBreakIn.lastPut = 0;
                LabBreakIn.schedulerID = 0;
                LabBreakIn.putLocation = null;
                LabBreakIn.brokeIn = null;
                return true;
            case "staatsbank":
                Bankraub.lastTime = 0L;
                Script.sendTeamMessage(player, ChatColor.LIGHT_PURPLE, "hat den Cooldown für die Staatsbank zurückgesetzt!", false);
                return true;
            case "atm":
                if(args.length == 1) {
                    player.sendMessage(Messages.ERROR + "/resetcooldown atm [Fraktion]");
                    return false;
                }

                if(organisation == null) {
                    player.sendMessage(Messages.ERROR + "Die Fraktion " + factionName + " existiert nicht!");
                    return false;
                }

                Bankautomaten.cooldown.remove(organisation);
                Script.sendTeamMessage(player, ChatColor.LIGHT_PURPLE, "hat den Cooldown für den ATM Raub der Fraktion " + organisation.getName() + " zurückgesetzt", false);
                return true;
            case "breakin":
                if(args.length == 1) {
                    player.sendMessage(Messages.ERROR + "/resetcooldown breakin [Fraktion]");
                    return false;
                }

                if(organisation == null) {
                    player.sendMessage(Messages.ERROR + "Die Fraktion " + factionName + " existiert nicht!");
                    return false;
                }

                BreakinCommand.cooldowns.remove(organisation);
                Script.sendTeamMessage(player, ChatColor.LIGHT_PURPLE, "hat den Cooldown für Breakin der Fraktion " + organisation.getName() + " zurückgesetzt", false);
                return true;
        }

        return false;
    }
}
