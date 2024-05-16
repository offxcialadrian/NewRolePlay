package de.newrp.features.deathmatcharena.commands;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeathmatchArenaCommand implements CommandExecutor {

    private final IDeathmatchArenaService arenaService = DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        if(args.length == 0) {
            player.sendMessage(Messages.ERROR + "/dm [join, quit, stats]");
            return false;
        }

        if(Script.getLevel(player) < 3) {
            player.sendMessage(Messages.ERROR + "Du kannst die Deathmatch Arena nur ab Level 3 betreten!");
            return false;
        }

        if(!Premium.hasPremium(player)) {
            player.sendMessage(Messages.ERROR + "Die Deathmatch Arena ist nur für Premium Spieler!");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "join":
            case "j":
                this.joinPlayer(player);
                break;
            case "leave":
            case "quit":
            case "q":
            case "l":
                if(!this.arenaService.isInDeathmatch(player, false)) {
                    player.sendMessage(Messages.ERROR + "Du bist nicht in der Deathmatch Arena!");
                    break;
                }
                this.quitPlayer(player);
                break;
            case "stats":
            case "s":
                if(!this.arenaService.isInDeathmatch(player, false)) {
                    player.sendMessage(Messages.ERROR + "Du bist nicht in der Deathmatch Arena!");
                    break;
                }
                this.printStats(player);
                break;
            default:
                player.sendMessage(Messages.ERROR + "/dm [join, leave, stats]");
                break;
        }

        return false;
    }

    private void joinPlayer(final Player player) {
        this.arenaService.joinDeathmatchArena(player);
    }

    private void quitPlayer(final Player player) {
        this.arenaService.quitDeathmatchArena(player);
    }

    private void printStats(final Player player) {
        this.arenaService.printStatsToPlayer(player);
    }
}
