package de.newrp.features.group.commands;

import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.config.MainConfig;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.group.IGroupService;
import de.newrp.features.group.data.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GruppierungCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final IGroupService groupService = DependencyContainer.getContainer().getDependency(IGroupService.class);
        final Group group = groupService.getGroup(player);
        final String prefix = groupService.getPrefix();

        if(args.length == 0) {
            player.sendMessage(prefix + "/gruppierung [create, invite, kick, leave, info]");
            return false;
        }

        switch (args[0]) {
            case "info":
                this.infoCommand(player, group);
                break;
            case "create":
                break;
            case "invite":
                break;
            case "kick":
                break;
            case "leave":
                break;
        }

        return false;
    }

    private void infoCommand(final Player player, final Group group) {
        final String prefix = DependencyContainer.getContainer().getDependency(IGroupService.class).getPrefix();
        player.sendMessage(prefix + "Gruppenname: " + group.groupName());
        player.sendMessage(prefix + "Besitzer: " + Bukkit.getOfflinePlayer(group.groupOwner()).getName());
        player.sendMessage(prefix + "Mitglieder: " + group.members().size() + "/" + group.maxMembers());
    }

    private void create(final Player player, final String[] args) {
        final IGroupService groupService = DependencyContainer.getContainer().getDependency(IGroupService.class);
        final int groupPrice = DependencyContainer.getContainer().getDependency(MainConfig.class).getGroupPrice();
        final int moneyOnBank = Script.getMoney(player, PaymentType.BANK);

        if(moneyOnBank < groupPrice) {
            player.sendMessage(groupService.getPrefix() + "Du hast nicht genügend Geld auf der Bank.");
            return;
        }

        final String groupName = args[0];
        groupService.createGroup(args[1], player);
    }

}
