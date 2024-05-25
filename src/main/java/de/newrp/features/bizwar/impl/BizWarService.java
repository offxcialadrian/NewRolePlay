package de.newrp.features.bizwar.impl;

import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Drogen;
import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shops;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.bizwar.config.BizWarConfig;
import de.newrp.features.bizwar.config.BizWarShopConfig;
import de.newrp.features.bizwar.data.ActiveBizWarInformation;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class BizWarService implements IBizWarService {

    private final BizWarConfig bizWarConfig = DependencyContainer.getContainer().getDependency(BizWarConfig.class);

    private final Map<Organisation, Long> lastAttackOfOrganisation = new HashMap<>();
    private final Map<Shops, Long> lastAttackOnShop = new HashMap<>();
    private final Set<ActiveBizWarInformation> activeBizWarInformations = new HashSet<>();
    private final Map<Shops, Organisation> activeExtortions = new HashMap<>();

    @Override
    public void loadActiveExtortions() {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT * FROM extorted_shops")) {
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final int shopID = resultSet.getInt("shop_id");
                    final int organisationID = resultSet.getInt("organisation_id");
                    final long extortedTimestamp = resultSet.getLong("extorted_timestamp");

                    final Shops shop = Shops.getShop(shopID);
                    final Organisation organisation = Organisation.getOrganisation(organisationID);
                    if(organisation == null) {
                        return;
                    }

                    // Watch in test server
                    this.activeExtortions.put(shop, organisation);
                    this.lastAttackOnShop.put(shop, extortedTimestamp);
                }
            }
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }

    @Override
    public void startBizWar(Shops shop, Player player, Organisation organisation, Organisation defenderOrganisation) {
        final Optional<BizWarShopConfig> optional = bizWarConfig.getShopConfigs().stream().filter(e -> e.getShopId() == shop.getID()).findFirst();
        if(!optional.isPresent()) {
            player.sendMessage(getPrefix() + "Der Shop §e" + shop.getPublicName() + " §7kann nicht angegriffen werden!");
            return;
        }

        final ActiveBizWarInformation bizWarInformation = new ActiveBizWarInformation(shop, organisation, defenderOrganisation, System.currentTimeMillis(), new HashSet<>(), new HashSet<>(),
                optional.get().getAttackerSpawn().toLocation(),
                optional.get().getDefenderSpawn().toLocation(),
                1,
                0,
                0);

        this.activeBizWarInformations.add(bizWarInformation);
        bizWarInformation.startBizWarScheduler(this);
        bizWarInformation.getJoinedMembersOfAttackers().add(player.getUniqueId());

        for (final UUID defenderPlayerUUID : defenderOrganisation.getMember()) {
            final Player defenderPlayer = Bukkit.getPlayer(defenderPlayerUUID);
            if(defenderPlayer == null) continue;

            defenderPlayer.sendTitle("§c§lBiz War gestartet", "§7Der Shop §e" + shop.getPublicName() + " §7wird angegriffen! Verteidige ihn!", 10, 60, 10);
            defenderPlayer.sendMessage(this.getPrefix() + "§7Der Shop §e" + shop.getPublicName() + " §7wird von §e" + organisation.getName() + " §7angegriffen!");
            defenderPlayer.sendMessage(Messages.INFO + "Du kannst dem Biz War mit /joinfight beitreten!");
        }

        this.joinBizWar(bizWarInformation, player, organisation);
    }

    @Override
    public void finishBizWar(Shops shop) {
        final ActiveBizWarInformation activeBizWarInformation = this.activeBizWarInformations.stream().filter(e -> e.getAttackedShop() == shop).findFirst().orElse(null);
        if(activeBizWarInformation == null) {
            Debug.debug("Stopping bizwar on shop " + shop.getName() + " failed because there is no active bizwar on this shop!");
            return;
        }

        this.activeBizWarInformations.remove(activeBizWarInformation);
        final Organisation winner = activeBizWarInformation.getCurrentAttackerPoints() >= activeBizWarInformation.getCurrentDefenderPoints() ? activeBizWarInformation.getAttackerOrganisation() : activeBizWarInformation.getDefenderOrganisation();
        final Organisation loser = activeBizWarInformation.getAttackerOrganisation() == winner ? activeBizWarInformation.getDefenderOrganisation() : activeBizWarInformation.getAttackerOrganisation();

        for (UUID winnerUUID : winner.getMember()) {
            final Player winnerPlayer = Bukkit.getPlayer(winnerUUID);
            winnerPlayer.sendTitle("§a§lBIZ-War gewonnen!", "§7Ihr habt erfolgreich den Biz War gewonnen!", 0, 100, 0);
            winnerPlayer.sendMessage(getPrefix() + "Deine Organisation hat den Biz War gegen §c" + loser.getName() + " §7gewonnen!");
            winnerPlayer.playSound(winnerPlayer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }
        winner.addExp(Script.getRandom(40, 50));

        for (UUID loserUUID : loser.getMember()) {
            final Player loserPlayer = Bukkit.getPlayer(loserUUID);
            loserPlayer.sendTitle("§c§lBIZ-War verloren!", "§7Ihr habt den Biz War verloren!", 0, 100, 0);
            loserPlayer.sendMessage(getPrefix() + "Deine Organisation hat den Biz War gegen §a" + winner.getName() + " §7verloren!");
            loserPlayer.playSound(loserPlayer.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 1f);
        }

        this.setOwnerOfShop(shop, winner);
    }

    @Override
    public void joinBizWar(ActiveBizWarInformation bizWarInformation, Player player, Organisation organisation) {
        if(bizWarInformation.getAttackerOrganisation() != organisation && bizWarInformation.getDefenderOrganisation() != organisation) {
            player.sendMessage(this.getPrefix() + "Deine Organisation ist nicht am Biz War beteiligt!");
            return;
        }

        if(bizWarInformation.getJoinedMembersOfAttackers().contains(player.getUniqueId())) {
            player.sendMessage(this.getPrefix() + "§cDu bist bereits im Biz War!");
            return;
        }

        if(bizWarInformation.getJoinedMembersOfDefenders().contains(player.getUniqueId())) {
            player.sendMessage(this.getPrefix() + "§cDu bist bereits im Biz War!");
            return;
        }

        if(bizWarInformation.getAttackerOrganisation() == organisation) {
            bizWarInformation.getJoinedMembersOfAttackers().add(player.getUniqueId());
            bizWarInformation.getDefenderOrganisation().sendMessage(this.getPrefix() + "§e" + player.getName() + " §7ist den Angreifern beigetreten (§e" + bizWarInformation.getJoinedMembersOfAttackers().size() + "§7)!");
        } else {
            bizWarInformation.getJoinedMembersOfDefenders().add(player.getUniqueId());
            bizWarInformation.getDefenderOrganisation().sendMessage(this.getPrefix() + "§e" + player.getName() + " §7ist den Verteidigern beigetreten (§e" + bizWarInformation.getJoinedMembersOfAttackers().size() + "§7)!");
        }

        Cache.saveInventory(player);
        this.equipPlayerForBizWar(player);
    }

    @Override
    public boolean isBizWarRunning(Shops shops) {
        return this.activeBizWarInformations.stream()
                .anyMatch(e -> e.getAttackedShop() == shops);
    }

    @Override
    public ActiveBizWarInformation getBizWarOfOrganisation(Organisation organisation) {
        return this.activeBizWarInformations.stream()
                .filter(e -> e.getDefenderOrganisation() == organisation || e.getAttackerOrganisation() == organisation)
                .findFirst()
                .orElse(null);
    }

    @Override
    public long getActiveCooldownOnShop(Shops shops) {
        return this.lastAttackOnShop.getOrDefault(shops, System.currentTimeMillis());
    }

    @Override
    public long getActiveCooldownOnOrganisation(Organisation organisation) {
        return this.lastAttackOfOrganisation.getOrDefault(organisation, System.currentTimeMillis());
    }

    @Override
    public Set<Shops> getShopsOfFaction(Organisation organisation) {
        final Set<Shops> activeShops = new HashSet<>();
        for (Map.Entry<Shops, Organisation> shopsOrganisationEntry : this.activeExtortions.entrySet()) {
            if(shopsOrganisationEntry.getValue() == organisation) {
                activeShops.add(shopsOrganisationEntry.getKey());
            }
        }
        return activeShops;
    }

    @Override
    public Organisation getCurrentOwnerOfShop(Shops shop) {
        return this.activeExtortions.getOrDefault(shop, null);
    }

    @Override
    public void setOwnerOfShop(Shops shop, Organisation organisation) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("UPDATE extorted_shops SET organisation_id = ?, exorted_timestamp = ? WHERE shop_id = ?")) {
            preparedStatement.setInt(1, organisation.getID());
            preparedStatement.setLong(2, System.currentTimeMillis());
            preparedStatement.setInt(3, shop.getID());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
    }

    @Override
    public void equipPlayerForBizWar(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        for (Weapon weapon : Weapon.values()) {
            if(weapon == Weapon.SNIPER) continue;
            if(weapon == Weapon.DESERT_EAGLE) continue;

            player.getInventory().addItem(Waffen.setAmmo(weapon.getWeapon(), weapon.getMagazineSize(), 100));
        }

        for (final Drogen drug : Drogen.values()) {
            if(drug == Drogen.ANTIBIOTIKA) {
                continue;
            }

            if(drug == Drogen.ECSTASY) {
                continue;
            }

            if(drug == Drogen.KRISTALLE) {
                continue;
            }

            player.getInventory().addItem(new ItemBuilder(drug.getMaterial()).setName(drug.getName()).setLore("§7Reinheitsgrad: " + Drogen.DrugPurity.HIGH.getText()).setAmount(5).build());
        }
    }

    @Override
    public String getPrefix() {
        return "§8[§cBIZ-War§8] §c" + Messages.ARROW + " §7";
    }
}
