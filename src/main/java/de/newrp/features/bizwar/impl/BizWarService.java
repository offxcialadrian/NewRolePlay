package de.newrp.features.bizwar.impl;

import de.newrp.API.*;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Drogen;
import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.ShopItem;
import de.newrp.Shop.Shops;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.bizwar.config.BizWarConfig;
import de.newrp.features.bizwar.config.BizWarShopConfig;
import de.newrp.features.bizwar.data.ActiveBizWarInformation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BizWarService implements IBizWarService {

    private final BizWarConfig bizWarConfig = DependencyContainer.getContainer().getDependency(BizWarConfig.class);

    private final Map<Organisation, Long> lastAttackOfOrganisation = new HashMap<>();
    private final Map<Shops, Long> lastAttackOnShop = new HashMap<>();
    private final Set<ActiveBizWarInformation> activeBizWarInformations = new HashSet<>();
    private final Map<Shops, Organisation> activeExtortions = new HashMap<>();
    private final Map<Shops, Player> beeingFreed = new HashMap<>();

    @Override
    public void loadActiveExtortions() {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT * FROM extorted_shops")) {
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final int shopID = resultSet.getInt("shop_id");
                    final int organisationID = resultSet.getInt("organisation_id");
                    final long extortedTimestamp = resultSet.getLong("exorted_timestamp");

                    final Shops shop = Shops.getShop(shopID);
                    final Organisation organisation = Organisation.getOrganisation(organisationID);
                    if(organisation == null) {
                        continue;
                    }

                    if(this.bizWarConfig.getShopConfigs().stream().noneMatch(e -> e.getShopId() == shop.getID())) {
                        continue;
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
                0, 0);

        this.activeBizWarInformations.add(bizWarInformation);
        bizWarInformation.startBizWarScheduler(this);
        this.addOrgaCooldown(organisation, TimeUnit.HOURS.toMillis(1));
        this.addShopCooldown(shop, TimeUnit.MINUTES.toMillis(30));

        for (final UUID defenderPlayerUUID : defenderOrganisation.getMember()) {
            final Player defenderPlayer = Bukkit.getPlayer(defenderPlayerUUID);
            if(defenderPlayer == null) continue;

            defenderPlayer.sendTitle("§c§lBiz War gestartet", "§7Der Shop §e" + shop.getPublicName() + " §7wird angegriffen! Verteidige ihn!", 10, 60, 10);
            defenderPlayer.sendMessage(this.getPrefix() + "§7Der Shop §e" + shop.getPublicName() + " §7wird von §e" + organisation.getName() + " §7angegriffen!");
            defenderPlayer.sendMessage(Messages.INFO + "Du kannst dem Biz War mit /joinfight beitreten!");
            defenderPlayer.playSound(defenderPlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
        }

        for (UUID uuid : organisation.getMember()) {
            final Player attackerPlayer = Bukkit.getPlayer(uuid);
            if(attackerPlayer == null) continue;

            attackerPlayer.sendTitle("§a§lBiz War gestartet", "Viel Erfolg!", 10, 60, 10);
            attackerPlayer.sendMessage(this.getPrefix() + "§7Deine Organisation greift den Shop §e" + shop.getPublicName() + " §7an!");
            attackerPlayer.sendMessage(Messages.INFO + "Ihr greift §e" + defenderOrganisation.getName() + " §7an!");
            attackerPlayer.sendMessage(Messages.INFO + "Du kannst dem Biz War mit /joinfight beitreten!");
            attackerPlayer.playSound(attackerPlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
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
        final Organisation winner = activeBizWarInformation.getCurrentAttackerPoints() > activeBizWarInformation.getCurrentDefenderPoints() ? activeBizWarInformation.getAttackerOrganisation() : activeBizWarInformation.getDefenderOrganisation();
        final Organisation loser = activeBizWarInformation.getAttackerOrganisation() == winner ? activeBizWarInformation.getDefenderOrganisation() : activeBizWarInformation.getAttackerOrganisation();

        for (UUID winnerUUID : winner.getMember()) {
            final Player winnerPlayer = Bukkit.getPlayer(winnerUUID);
            winnerPlayer.sendTitle("§a§lBIZ-War gewonnen!", "§7Ihr habt erfolgreich den Biz War gewonnen!", 0, 100, 0);
            winnerPlayer.sendMessage(getPrefix() + "Deine Organisation hat den Biz War gegen §c" + loser.getName() + " §7gewonnen!");
            winnerPlayer.playSound(winnerPlayer.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            Script.addEXP(Script.getNRPID(winnerPlayer), Script.getRandom(40, 50));
            winnerPlayer.sendMessage(Messages.INFO + "Danke für deine Teilnahme am Biz War!");
        }


        for (UUID loserUUID : loser.getMember()) {
            final Player loserPlayer = Bukkit.getPlayer(loserUUID);
            loserPlayer.sendTitle("§c§lBIZ-War verloren!", "§7Ihr habt den Biz War verloren!", 0, 100, 0);
            loserPlayer.sendMessage(getPrefix() + "Deine Organisation hat den Biz War gegen §a" + winner.getName() + " §7verloren!");
            loserPlayer.playSound(loserPlayer.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 1f);
            Script.addEXP(Script.getNRPID(loserPlayer), Script.getRandom(40, 50));
            loserPlayer.sendMessage(Messages.INFO + "Danke für deine Teilnahme am Biz War!");
        }

        if(!loser.getMember().isEmpty()) {
            winner.addExp(Script.getRandom(70, 90));
            loser.removeExp(Script.getRandom(30, 45));
        }

        for (UUID joinedMembersOfDefender : activeBizWarInformation.getJoinedMembersOfDefenders()) {
            final Player player = Bukkit.getPlayer(joinedMembersOfDefender);
            if(player == null) continue;
            Script.updateListname(player);
            Script.resetHealth(player);
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(activePotionEffect.getType());
            }
            player.setHealth(player.getMaxHealth());
            Cache.loadInventory(player);
        }

        for (UUID joinedMembersOfAttacker : activeBizWarInformation.getJoinedMembersOfAttackers()) {
            final Player player = Bukkit.getPlayer(joinedMembersOfAttacker);
            if(player == null) continue;
            Script.updateListname(player);
            Script.resetHealth(player);
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(activePotionEffect.getType());
            }
            player.setHealth(player.getMaxHealth());
            Cache.loadInventory(player);
        }
        this.setOwnerOfShop(shop, winner);
    }

    @Override
    public void joinBizWar(ActiveBizWarInformation bizWarInformation, Player player, Organisation organisation) {
        if(bizWarInformation.getAttackerOrganisation() != organisation && bizWarInformation.getDefenderOrganisation() != organisation) {
            player.sendMessage(this.getPrefix() + "Deine Organisation ist nicht am Biz War beteiligt!");
            return;
        }

        final boolean isAttacker = bizWarInformation.getAttackerOrganisation() == organisation;
        final int sizeOfDefenders = bizWarInformation.getJoinedMembersOfDefenders().size();
        final int sizeOfAttackers = bizWarInformation.getJoinedMembersOfAttackers().size();

        if(isAttacker) {
            if(sizeOfAttackers > sizeOfDefenders) {
                player.sendMessage(this.getPrefix() + "§cDie Angreifer haben bereits mehr Mitglieder als die Verteidiger!");
                return;
            }
        } else {
            if(sizeOfDefenders > sizeOfAttackers) {
                player.sendMessage(this.getPrefix() + "§cDie Verteidiger haben bereits mehr Mitglieder als die Angreifer!");
                return;
            }
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
            bizWarInformation.getAttackerOrganisation().sendMessage(this.getPrefix() + "§a" + player.getName() + " §7ist euch beigetreten (§e" + bizWarInformation.getJoinedMembersOfAttackers().size() + "§7)!");
            bizWarInformation.getDefenderOrganisation().sendMessage(this.getPrefix() + "§c" + player.getName() + " §7ist den Angreifern beigetreten (§e" + bizWarInformation.getJoinedMembersOfAttackers().size() + "§7)!");
            player.teleport(bizWarInformation.getAttackerSpawn());
            player.sendMessage(Messages.INFO + "Du bist nun Teil vom Biz War, daher wurdest du zum Angreifer Spawn teleportiert!");

        } else {
            bizWarInformation.getJoinedMembersOfDefenders().add(player.getUniqueId());
            bizWarInformation.getDefenderOrganisation().sendMessage(this.getPrefix() + "§a" + player.getName() + " §7ist euch beigetreten (§e" + bizWarInformation.getJoinedMembersOfDefenders().size() + "§7)!");
            bizWarInformation.getAttackerOrganisation().sendMessage(this.getPrefix() + "§c" + player.getName() + " §7ist den Verteidigern beigetreten (§e" + bizWarInformation.getJoinedMembersOfDefenders().size() + "§7)!");
            player.teleport(bizWarInformation.getDefenderSpawn());
            player.sendMessage(Messages.INFO + "Du bist nun Teil vom Biz War, daher wurdest du zum Verteidiger Spawn teleportiert!");
        }

        Activity.grantActivity(Script.getNRPID(player), Activities.BIZWAR);

        Script.updateListname(player);

        Krankheit.ENTZUENDUNG.remove(Script.getNRPID(player));
        Cache.saveInventory(player);
        this.equipPlayerForBizWar(player);
    }

    @Override
    public boolean isBizWarRunning(Shops shops) {
        return this.activeBizWarInformations.stream()
                .anyMatch(e -> e.getAttackedShop() == shops);
    }

    @Override
    public boolean isMemberOfBizWar(Player player) {
        for (ActiveBizWarInformation activeBizWarInformation : this.activeBizWarInformations) {
            if(activeBizWarInformation.getJoinedMembersOfDefenders().contains(player.getUniqueId()) || activeBizWarInformation.getJoinedMembersOfAttackers().contains(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBeeingFreed(Shops shop) {
        return this.beeingFreed.containsKey(shop);
    }

    @Override
    public void setBeeingFreed(Shops shop, Player player) {
        if(player == null) {
            this.beeingFreed.remove(shop);
            return;
        }

        this.beeingFreed.put(shop, player);
    }

    @Override
    public ActiveBizWarInformation getBizWarOfOrganisation(Organisation organisation) {
        return this.activeBizWarInformations.stream()
                .filter(e -> e.getDefenderOrganisation() == organisation || e.getAttackerOrganisation() == organisation)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ActiveBizWarInformation getBizWarOfPlayer(Player player) {
        return this.activeBizWarInformations.stream()
                .filter(e -> e.getJoinedMembersOfAttackers().contains(player.getUniqueId()) || e.getJoinedMembersOfDefenders().contains(player.getUniqueId()))
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
        if(organisation == null) {
            this.activeExtortions.remove(shop);
            try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("DELETE FROM extorted_shops WHERE shop_id = ?")) {
                preparedStatement.setInt(1, shop.getID());
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                NewRoleplayMain.handleError(e);
            }
        } else {
            this.activeExtortions.put(shop, organisation);
            try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("REPLACE INTO extorted_shops(shop_id, organisation_id, exorted_timestamp) VALUES(?, ?, ?)")) {
                preparedStatement.setInt(1, shop.getID());
                preparedStatement.setInt(2, organisation.getID());
                preparedStatement.setLong(3, System.currentTimeMillis());
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                NewRoleplayMain.handleError(e);
            }
        }
    }

    @Override
    public void equipPlayerForBizWar(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setMaxHealth(40);
        player.setHealth(player.getMaxHealth());

        player.getInventory().addItem(Waffen.setAmmo(Weapon.AK47.getWeapon(), Weapon.AK47.getMagazineSize(), 300));
        player.getInventory().addItem(Waffen.setAmmo(Weapon.JAGDFLINTE.getWeapon(), Weapon.JAGDFLINTE.getMagazineSize(), 15));
        player.getInventory().addItem(new ItemBuilder(Material.BREAD).setNoDrop().setAmount(64).build());

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


            player.getInventory().addItem(new ItemBuilder(drug.getMaterial()).setName(drug.getName()).setNoDrop().setLore("§7Reinheitsgrad: " + Drogen.DrugPurity.HIGH.getText()).setAmount(5).build());
        }

        player.getInventory().addItem(new ItemBuilder(ShopItem.SCHMERZMITTEL_HIGH.getItemStack().clone()).setNoDrop().setAmount(10).build());
        player.getInventory().addItem(new ItemBuilder(ShopItem.VERBAND.getItemStack().clone()).setNoDrop().setAmount(2).build());

        final ItemStack itemStack = new ItemBuilder(ShopItem.TRINKWASSER.getItemStack().clone()).setNoDrop().build();
        for(int i = 0; i < 10; i++) {
            player.getInventory().addItem(itemStack);
        }

    }

    @Override
    public Location getCorrespondingLocationForBizWarPlayer(Player player) {
        final ActiveBizWarInformation activeBizWarInformation = this.activeBizWarInformations.stream()
                .filter(e -> e.getJoinedMembersOfAttackers().contains(player.getUniqueId()) || e.getJoinedMembersOfDefenders().contains(player.getUniqueId()))
                .findFirst()
                .orElse(null);

        if(activeBizWarInformation == null) {
            return null;
        }

        if(activeBizWarInformation.getJoinedMembersOfAttackers().contains(player.getUniqueId())) {
            return activeBizWarInformation.getAttackerSpawn();
        }

        if(activeBizWarInformation.getJoinedMembersOfDefenders().contains(player.getUniqueId())) {
            return activeBizWarInformation.getDefenderSpawn();
        }
        return null;
    }

    @Override
    public String getPrefix() {
        return "§8[§cBIZ-War§8] §c" + Messages.ARROW + " §7";
    }

    @Override
    public void checkForTeamKill(Player player) {
        if(!this.isMemberOfBizWar(player)) {
            return;
        }

        final ActiveBizWarInformation activeBizWarInformation = this.getBizWarOfPlayer(player);
        final boolean isAttacker = activeBizWarInformation.getJoinedMembersOfAttackers().contains(player.getUniqueId());

        if(isAttacker) {
            boolean allAttackersDead = true;
            for (UUID joinedMembersOfAttacker : activeBizWarInformation.getJoinedMembersOfAttackers()) {
                if(joinedMembersOfAttacker == player.getUniqueId()) {
                    continue;
                }
                final Player attacker = Bukkit.getPlayer(joinedMembersOfAttacker);
                if(attacker == null) continue;
                if(!Friedhof.isDead(attacker)) {
                    allAttackersDead = false;
                    break;
                }
            }

            if(allAttackersDead) {
                activeBizWarInformation.getDefenderOrganisation().sendMessage(this.getPrefix() + "§6§lTeam-Kill! §7Ihr habt alle Angreifer getötet, gute Arbeit!");
                for (UUID uuid : activeBizWarInformation.getDefenderOrganisation().getMember()) {
                    final Player defender = Bukkit.getPlayer(uuid);
                    if(defender == null) continue;

                    defender.playSound(defender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    defender.sendTitle("§6§lTeam-Kill!", "§7Ihr habt alle Angreifer getötet!", 10, 60, 10);
                }
            }
        } else {
            boolean allDefendersDead = true;
            for (UUID joinedMembersOfDefenders : activeBizWarInformation.getJoinedMembersOfDefenders()) {
                if(joinedMembersOfDefenders == player.getUniqueId()) {
                    continue;
                }
                final Player defender = Bukkit.getPlayer(joinedMembersOfDefenders);
                if(defender == null) continue;
                if(!Friedhof.isDead(defender)) {
                    allDefendersDead = false;
                    break;
                }
            }

            if(allDefendersDead) {
                activeBizWarInformation.getAttackerOrganisation().sendMessage(this.getPrefix() + "§6§lTeam-Kill! §7Ihr habt alle Verteidiger getötet, gute Arbeit!");
                for (UUID uuid : activeBizWarInformation.getAttackerOrganisation().getMember()) {
                    final Player attacker = Bukkit.getPlayer(uuid);
                    if(attacker == null) continue;

                    attacker.playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    attacker.sendTitle("§6§lTeam-Kill!", "§7Ihr habt alle Verteidiger getötet!", 10, 60, 10);
                }
            }
        }
    }

    @Override
    public void addOrgaCooldown(Organisation organisation, long cooldown) {
        if(Script.isInTestMode()) return;
        this.lastAttackOfOrganisation.put(organisation, System.currentTimeMillis() + cooldown);
    }

    @Override
    public void addShopCooldown(Shops shop, long cooldown) {
        if(Script.isInTestMode()) return;
        this.lastAttackOnShop.put(shop, System.currentTimeMillis() + cooldown);
    }
}
