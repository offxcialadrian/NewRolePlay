package de.newrp.features.deathmatcharena.impl;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Organisationen.Drogen;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import de.newrp.features.deathmatcharena.data.DeathmatchJoinData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DeathmatchArenaService implements IDeathmatchArenaService {

    private final Map<UUID, DeathmatchJoinData> activeParticipants = new ConcurrentHashMap<>();
    private final List<Location> spawnPoints = new ArrayList<>();

    @Override
    public void joinDeathmatchArena(Player player) {
        if(isInDeathmatch(player, false)) {
            player.sendMessage(Messages.ERROR + "Du bist bereits in der Deathmatch Arena");
            return;
        }

        this.activeParticipants.put(player.getUniqueId(), createJoinData(player));
        this.equipWeaponsAndDrugs(player);
        this.sendMessageToArenaMembers(getPrefix() + "Spieler " + Script.getName(player) + " hat die Deathmatch Arena betreten!");
        if(SDuty.isSDuty(player)) {
            player.sendMessage(Messages.INFO + "§c§lDu bist noch im Supporter Dienst");
            player.playSound(player.getEyeLocation(), Sound.ENTITY_CAT_HISS, 1f, 1f);
            return;
        }


    }

    @Override
    public boolean isInDeathmatch(Player player, boolean forceRemoveIfIn) {
        return false;
    }

    @Override
    public void equipWeaponsAndDrugs(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        for(final Weapon w : Weapon.values()) {
            if(w == Weapon.SNIPER) continue;

            player.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), w.getMagazineSize(), 200));
        }
        player.getInventory().addItem(new ItemBuilder(Material.BREAD).setAmount(32).build());

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

            player.getInventory().addItem(new ItemBuilder(drug.getMaterial()).setName(drug.getName()).setLore("§7Reinheitsgrad: " + Drogen.DrugPurity.HIGH).setAmount(10).build());
        }
    }

    @Override
    public void quitDeathmatchArena(Player player) {
        final DeathmatchJoinData joinData = this.activeParticipants.getOrDefault(player.getUniqueId(), null);
        if(joinData == null) {
            player.getInventory().clear();
            player.sendMessage(Messages.ERROR + "Dein Inventar konnte nicht geladen werden, bitte melde dich bei der Administration");
            return;
        }
    }

    @Override
    public void banDeathmatchArena(Player player, long duration, String reason, Player targetPlayer) {

    }

    @Override
    public void unbanDeathmatchArena(Player player, Player targetPlayer) {

    }

    @Override
    public void printStatsToPlayer(Player player) {

    }

    @Override
    public void sendMessageToArenaMembers(String message) {
        for (final DeathmatchJoinData value : this.activeParticipants.values()) {
            if(value.player() == null) {
                Bukkit.getLogger().warning("A player is null in deathmatch arena, maybe not removed properly?");
                continue;
            }

            value.player().sendMessage(message);
        }
    }

    @Override
    public String getPrefix() {
        return "§8[§cDeathmatch§8] §7";
    }

    @Override
    public List<Location> getSpawnPoints() {
        return this.spawnPoints;
    }

    @Override
    public Location getRandomSpawnPoint() {
        return null;
    }

    private DeathmatchJoinData createJoinData(final Player player) {
        return new DeathmatchJoinData(player, player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getLocation());
    }
}
