package de.newrp.features.bizwar.commands;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shops;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class StartBizWarCommand implements CommandExecutor {

    private final IBizWarService bizWarService = DependencyContainer.getContainer().getDependency(IBizWarService.class);

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)) {
            return false;
        }

        final Player player = (Player) commandSender;
        final Organisation organisation = Organisation.getOrganisation(player);

        if(organisation == null) {
            player.sendMessage(Messages.ERROR + "§cDu bist in keiner Organisation!");
            return false;
        }

        if(Script.getLevel(player) < 3) {
            player.sendMessage(Messages.ERROR + "§cDu musst mindestens Level 3 sein um einen Biz War starten zu können");
            return false;
        }

        /*if(organisation == Organisation.HITMEN) {
            player.sendMessage(Messages.ERROR + "§cDeine Fraktion kann keinen Biz War starten!");
            return false;
        }*/ // Deactivate with Hitman features
        final int playerRankInOrganisation = Organisation.getRank(player);

        if(playerRankInOrganisation <= 2) {
            player.sendMessage(Messages.NO_PERMISSION);
            return false;
        }

        final long organisationCooldown = this.bizWarService.getActiveCooldownOnOrganisation(organisation);
        final long timeTillNextAttackOnOrganisation = organisationCooldown - System.currentTimeMillis();

        if(timeTillNextAttackOnOrganisation > 0) {
            player.sendMessage(Messages.ERROR + "§cDeine Organisation kann erst in §c§l" + new SimpleDateFormat("HH:mm").format(timeTillNextAttackOnOrganisation) + " Stunden §cerneut angreifen!");
            return false;
        }

        final Shops shop = Shops.getShopByLocation(player.getLocation(), 5f);
        if(shop == null) {
            player.sendMessage(Messages.ERROR + "§cDu bist in keiner Organisation!");
            return false;
        }

        if(this.bizWarService.isBeeingFreed(shop)) {
            player.sendMessage(Messages.ERROR + "§cDer Shop wird derzeit befreit!");
            return false;
        }

        if(this.bizWarService.isBizWarRunning(shop)) {
            player.sendMessage(Messages.ERROR + "§cEs läuft bereits ein Biz War um diesen Shop!");
            return false;
        }


        final long shopCooldown = this.bizWarService.getActiveCooldownOnShop(shop);
        final long timeTillNextAttack = shopCooldown - System.currentTimeMillis();

        if(timeTillNextAttack > 0) {
            player.sendMessage(Messages.ERROR + "§cDu kannst diesen Shop erst in §c§l" + new SimpleDateFormat("HH:mm").format(timeTillNextAttack) + " Stunden §cerneut angreifen!");
            return false;
        }

        final Organisation activeOwner = bizWarService.getCurrentOwnerOfShop(shop);
        if(activeOwner == null) {
            organisation.sendMessage(this.bizWarService.getPrefix() + "Der Shop §e" + shop.getPublicName() + " §7wurde von §e" + Script.getName(player) + " §7übernommen!");
            this.bizWarService.setOwnerOfShop(shop, organisation);
            this.bizWarService.addOrgaCooldown(organisation, TimeUnit.HOURS.toMillis(1));
            organisation.sendMessage(Messages.INFO + "Wegen der kampflosen Übernahme habt ihr nur einen Cooldown von einer Stunde bekommmen");
            return false;
        }

        if(activeOwner == organisation) {
            player.sendMessage(Messages.ERROR + "§cDu kannst einen Shop unter eurer Kontrolle nicht erneut angreifen!");
            return false;
        }

        this.bizWarService.startBizWar(shop, player, organisation, activeOwner);
        return false;
    }
}
