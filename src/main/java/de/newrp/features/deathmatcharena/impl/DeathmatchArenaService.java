package de.newrp.features.deathmatcharena.impl;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Organisationen.Drogen;
import de.newrp.Waffen.Waffen;
import de.newrp.Waffen.Weapon;
import de.newrp.config.data.LocationConfig;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import de.newrp.features.deathmatcharena.data.DeathmatchArenaConfig;
import de.newrp.features.deathmatcharena.data.DeathmatchArenaStats;
import de.newrp.features.deathmatcharena.data.DeathmatchJoinData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeathmatchArenaService implements IDeathmatchArenaService {

    private final Map<UUID, DeathmatchJoinData> activeParticipants = new ConcurrentHashMap<>();
    private final List<Location> spawnPoints = new ArrayList<>();
    private final Location joinLocation;
    private final DeathmatchArenaConfig deathmatchArenaConfig = DependencyContainer.getContainer().getDependency(DeathmatchArenaConfig.class);

    public DeathmatchArenaService() {
        for (LocationConfig locationConfig : this.deathmatchArenaConfig.arenas().getOrDefault("default", new ArrayList<>())) {
            this.spawnPoints.add(locationConfig.toLocation());
        }
        this.joinLocation = this.deathmatchArenaConfig.enterLocation().toLocation();
        this.joinLocation.getChunk().load();
        Hologram.HOLOGRAMS.add(new Hologram(this.joinLocation, "§6/dm join"));
    }

    @Override
    public void joinDeathmatchArena(Player player) {
        if(isInDeathmatch(player, false)) {
            player.sendMessage(Messages.ERROR + "Du bist bereits in der Deathmatch Arena");
            return;
        }

        if(player.getLocation().distance(this.joinLocation) > 3) {
            player.sendMessage(Messages.ERROR + "Du bist zu weit vom Eingang der Deathmatch Arena entfernt!");
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

        player.setHealth(player.getMaxHealth());
    }

    @Override
    public boolean isInDeathmatch(Player player, boolean forceRemoveIfIn) {
        final boolean isInDeathmatch = this.activeParticipants.containsKey(player.getUniqueId());
        if(isInDeathmatch && forceRemoveIfIn) {
            this.quitDeathmatchArena(player);
        }
        return isInDeathmatch;
    }

    @Override
    public void equipWeaponsAndDrugs(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        for(final Weapon w : Weapon.values()) {
            if(!this.deathmatchArenaConfig.activatedWeapons().contains(w.getName())) {
                continue;
            }

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

            player.getInventory().addItem(new ItemBuilder(drug.getMaterial()).setName(drug.getName()).setLore("§7Reinheitsgrad: " + Drogen.DrugPurity.HIGH.getText()).setAmount(this.deathmatchArenaConfig.drugAmount()).build());
        }

        player.teleport(this.getRandomSpawnPoint());
    }

    @Override
    public void quitDeathmatchArena(Player player) {
        final DeathmatchJoinData joinData = this.activeParticipants.getOrDefault(player.getUniqueId(), null);
        player.getInventory().clear();
        if(joinData == null) {
            player.sendMessage(Messages.ERROR + "Dein Inventar konnte nicht geladen werden, bitte melde dich bei der Administration");
            return;
        }

        player.getInventory().setContents(joinData.contentsInventory());
        player.getInventory().setArmorContents(joinData.contentsArmor());
        player.teleport(joinData.oldLocation());
        this.sendMessageToArenaMembers(getPrefix() + "Spieler " + Script.getName(player) + " hat die Deathmatch Arena verlassen!");
        player.sendMessage(Messages.INFO + "Verbrachte Zeit in der Deathmatch Arena: " + new SimpleDateFormat("mm:ss").format(System.currentTimeMillis() - joinData.joinTime()) + "m");
        this.printStatsToPlayer(player);
        this.activeParticipants.remove(player.getUniqueId());
    }

    @Override
    public void banDeathmatchArena(Player player, long duration, String reason, Player targetPlayer) {

    }

    @Override
    public void unbanDeathmatchArena(Player player, Player targetPlayer) {

    }

    @Override
    public void printStatsToPlayer(Player player) {
        final DeathmatchJoinData joinData = this.activeParticipants.getOrDefault(player.getUniqueId(), null);
        if(joinData == null) {
            return;
        }

        final DeathmatchArenaStats stats = joinData.stats();
        player.sendMessage(getPrefix() + "Kills§8: §e" + stats.kills());
        player.sendMessage(getPrefix() + "Tode§8: §e" + stats.deaths());
        player.sendMessage(getPrefix() + "K/D§8: §e" + ((float) stats.kills() / (float) stats.deaths()));
        player.sendMessage(getPrefix() + "Geschossene Schüsse§8: §e" + stats.shotsFired());
        player.sendMessage(getPrefix() + "Getroffene Schüsse§8: §e" + stats.shotsHit());
        player.sendMessage(getPrefix() + "Trefferquote§8: §e" + (int) (((float) stats.shotsHit() / (float) stats.shotsFired()) * 100f) + "%");
    }

    @Override
    public void sendMessageToArenaMembers(String message) {
        for (final DeathmatchJoinData value : this.activeParticipants.values()) {
            if(value.player() == null) {
                Debug.debug("A player is null in deathmatch arena, maybe not removed properly?");
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
        return this.getRandomValue(this.spawnPoints);
    }

    @Override
    public DeathmatchArenaStats getStats(Player player) {
        final DeathmatchJoinData joinData = this.activeParticipants.getOrDefault(player.getUniqueId(), null);
        if(joinData == null) {
            return null;
        }

        return joinData.stats();
    }

    private DeathmatchJoinData createJoinData(final Player player) {
        return new DeathmatchJoinData(player, player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getLocation());
    }

    private <T> T getRandomValue(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List must not be null or empty");
        }

        if(list.size() == 1) {
            return list.get(0);
        }

        final Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
}
