package de.newrp.features.bizwar.commands;

import de.newrp.API.Messages;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shop;
import de.newrp.Shop.Shops;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FreeBizCommand implements CommandExecutor {

    private final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        final Player player = (Player) commandSender;
        final Beruf.Berufe berufe = Beruf.getBeruf(player);

        if(berufe != Beruf.Berufe.POLICE) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        if(!Beruf.hasAbteilung(player, Abteilung.Abteilungen.SEK, Abteilung.Abteilungen.ABTEILUNGSLEITUNG)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        final Shops shop = Shops.getShopByLocation(player.getLocation(), 5f);
        if(shop == null) {
            player.sendMessage(Messages.ERROR + "Du befindest dich nicht in einem Shop.");
            return false;
        }

        final Organisation organisation = this.bizWarService.getCurrentOwnerOfShop(shop);
        if(organisation == null) {
            player.sendMessage(Messages.ERROR + "Dieser Shop wird nicht unterdrückt!");
            return false;
        }

        if(this.bizWarService.isBeeingFreed(shop)) {
            player.sendMessage(Messages.ERROR + "Dieser Shop wird bereits befreit!");
            return false;
        }

        Beruf.Berufe.POLICE.sendMessage(this.bizWarService.getPrefix() + "Der Spieler §e" + player.getName() + " §7beginnt den Shop §e" + shop.getPublicName() + " §7zu befreien");
        organisation.sendMessage(this.bizWarService.getPrefix() + "Der Shop §e" + shop.getPublicName() + " §7wird von der Polizei befreit! §lVerteidigt den Shop!");
        this.bizWarService.setBeeingFreed(shop, player);

        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
            this.bizWarService.setBeeingFreed(shop, null);

            final double distanceOfPlayerToShop = player.getLocation().distance(shop.getLocation());
            if(distanceOfPlayerToShop > 5) {
                Beruf.Berufe.POLICE.sendMessage(this.bizWarService.getPrefix() + "Der Spieler §e" + player.getName() + " §7hat versucht den Shop §e" + shop.getPublicName() + " §7zu befreien, war aber zu weit entfernt.");
                organisation.sendMessage(this.bizWarService.getPrefix() + "Die Polizei ist beim Versuch euren Shop §e" + shop.getPublicName() + " §7zu befreien gescheitert!");
                return;
            }

            this.bizWarService.setOwnerOfShop(shop, null);
            Beruf.Berufe.POLICE.sendMessage(this.bizWarService.getPrefix() + "Der Shop " + shop.getPublicName() + " wurde befreit.");
            organisation.sendMessage(this.bizWarService.getPrefix() + "Die Polizei hat euren Shop §e" + shop.getPublicName() + " §7befreit!");
        }, (20 * 60) * 5);


        return false;
    }
}
