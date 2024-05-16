package de.newrp.features.emergencycall.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Navi;
import de.newrp.API.Route;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.NewRoleplayMain;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.data.AcceptEmergencyCallMetadata;
import de.newrp.features.emergencycall.data.BlockPlayerInfo;
import de.newrp.features.emergencycall.data.EmergencyCall;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EmergencyCallService implements IEmergencyCallService {

    private final Set<EmergencyCall> emergencyCalls = new HashSet<>();
    private final Set<Beruf.Berufe> factionsWithEmergencyCalls = new HashSet<>();
    private final Cache<String, Boolean> blockedCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    public EmergencyCallService() {
        this.factionsWithEmergencyCalls.add(Beruf.Berufe.POLICE);
        this.factionsWithEmergencyCalls.add(Beruf.Berufe.RETTUNGSDIENST);
    }

    @Override
    public void createEmergencyCall(Player player, Location location, Beruf.Berufe targetFaction, final String reason) {
        final EmergencyCall emergencyCall = new EmergencyCall(player, location, targetFaction, reason, null);
        this.emergencyCalls.add(emergencyCall);

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefix()).append("§6Achtung! Ein Notruf von ").append(Script.getName(player)).append(" ist eingegangen.")
                .append("\n").append(getPrefix()).append("§6Vorfall§8: §6").append(reason)
                .append("\n").append(getPrefix()).append("§6Position§8: §6").append(Navi.getNextNaviLocation(location).getName())
                .append("\n").append(buildNearbyPlayersString(location, targetFaction));

        for (UUID member : targetFaction.getBeruf().keySet()) {
            Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage(stringBuilder.toString());
            Script.sendClickableMessage(Objects.requireNonNull(Bukkit.getPlayer(member)), getPrefix() + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + player.getName(), "Klicke hier um den Notruf anzunehmen.");
        }
    }

    @Override
    public void dropEmergencyCall(EmergencyCall emergencyCall) {
        final boolean hasBeenRemoved = this.emergencyCalls.remove(emergencyCall);
        if(hasBeenRemoved) {
            for (UUID member : emergencyCall.faction().getBeruf().keySet()) {
                Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage(this.getPrefix() + "Der Notruf von " + Script.getName(emergencyCall.sender()) + " wurde abgebrochen.");
            }
        }
    }

    @Override
    public void doneEmergencyCall(Player player, EmergencyCall emergencyCall) {
        for (UUID member : emergencyCall.faction().getBeruf().keySet()) {
            Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage(this.getPrefix() + Script.getName(player) + " hat den Notruf von " + Script.getName(emergencyCall.sender()) + " abgeschlossen");
        }
        this.emergencyCalls.remove(emergencyCall);
    }

    @Override
    public boolean hasBeenAccepted(EmergencyCall emergencyCall) {
        return emergencyCall.acceptEmergencyCallMetadata() != null;
    }

    @Override
    public Set<EmergencyCall> getAllOpenEmergencyCallsForFaction(Beruf.Berufe faction) {
        return this.emergencyCalls.stream()
                .filter(e -> e.acceptEmergencyCallMetadata() == null && e.faction() == faction)
                .collect(Collectors.toSet());
    }

    @Override
    public void acceptEmergencyCall(Player player, EmergencyCall emergencyCall) {
        final AcceptEmergencyCallMetadata acceptEmergencyCallMetadata = new AcceptEmergencyCallMetadata(player, player.getLocation(), System.currentTimeMillis());
        emergencyCall.acceptEmergencyCallMetadata(acceptEmergencyCallMetadata);
    }

    @Override
    public Optional<EmergencyCall> getAcceptedEmergencyCallByFactionMember(Player player) {
        return emergencyCalls.stream().filter(e -> e.acceptEmergencyCallMetadata() != null && e.acceptEmergencyCallMetadata().player().getUniqueId() == player.getUniqueId()).findFirst();
    }

    @Override
    public void requeueAcceptedEmergencyCall(Player player, EmergencyCall emergencyCall) {
        emergencyCall.acceptEmergencyCallMetadata(null);
        for (UUID member : emergencyCall.faction().getBeruf().keySet()) {
            Objects.requireNonNull(Bukkit.getPlayer(member)).sendMessage(this.getPrefix() + Script.getName(player) + " hat den Notruf von " + Script.getName(emergencyCall.sender()) + " wieder geöffnet!");
        }
        Route.invalidate(player);
    }

    @Override
    public boolean factionMemberHasAccepted(Player player) {
        return emergencyCalls.stream().anyMatch(e -> e.acceptEmergencyCallMetadata() != null && e.acceptEmergencyCallMetadata().player().getUniqueId() == player.getUniqueId());
    }

    @Override
    public Set<Beruf.Berufe> getAllFactionsWithEmergencyCalls() {
        return this.factionsWithEmergencyCalls;
    }

    @Override
    public Optional<EmergencyCall> getEmergencyCallByPlayer(Player player, Beruf.Berufe faction) {
        return this.emergencyCalls.stream()
                .filter(e -> e.sender().getUniqueId() == player.getUniqueId() && (faction == null || e.faction() == faction))
                .findFirst();
    }

    @Override
    public int estimateArrival(double meter) {
        return (int) (meter / 6.0);
    }

    @Override
    public List<String> getReasonsForEmergency(Beruf.Berufe faction) {
        final List<String> reasons = new ArrayList<>();
        switch (faction) {
            case RETTUNGSDIENST:
                reasons.add("Reanimation");
                reasons.add("Verletzung");
                reasons.add("Knochenbruch");
                break;
            case POLICE:
                reasons.add("Einbruch");
                reasons.add("Mord");
                reasons.add("Körperverletzung");
                reasons.add("Raubüberfall");
                reasons.add("Diebstahl");
                break;
        }
        return reasons;
    }

    @Override
    public String getPrefix() {
        return "§8[§4Notruf§8] §c" + Messages.ARROW + "§7 ";
    }

    @Override
    public List<Player> getNearbyPlayersOfFactionToLocation(Location location, Beruf.Berufe faction) {
        return faction.getBeruf().keySet().stream()
                .sorted(Comparator.comparingDouble((a) -> Objects.requireNonNull(Bukkit.getPlayer(a)).getLocation().distance(location)))
                .filter(faction::isDuty)
                .limit(2)
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList());
    }

    @Override
    public void blockEmergencyCalls(OfflinePlayer targetPlayer, Beruf.Berufe faction) {
        this.blockedCache.put(targetPlayer.getUniqueId().toString() + "_" + faction.getName(), true);
        try (final PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO blocked_notruf (nrp_id, berufID) VALUES (?, ?)")) {
            statement.setInt(1, Script.getNRPID(targetPlayer));
            statement.setInt(2, faction.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unblockEmergencyCalls(OfflinePlayer targetPlayer, Beruf.Berufe faction) {
        this.blockedCache.invalidate(targetPlayer.getUniqueId().toString() + "_" + faction.getName());
        try (final PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "DELETE FROM blocked_notruf WHERE nrp_id = ? AND berufID = ?")) {
            statement.setInt(1, Script.getNRPID(targetPlayer));
            statement.setInt(2, faction.getID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isBlocked(OfflinePlayer targetPlayer, Beruf.Berufe faction) {
        if(this.blockedCache.asMap().containsKey(targetPlayer.getUniqueId().toString() + "_" + faction.getName())) {
            return this.blockedCache.getIfPresent(targetPlayer.getUniqueId().toString() + "_" + faction.getName());
        }
        try (final PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT nrp_id FROM blocked_notruf WHERE nrp_id = ? AND berufID = ?")) {
            statement.setInt(1, Script.getNRPID(targetPlayer));
            statement.setInt(2, faction.getID());
            try (final ResultSet resultSet = statement.executeQuery()) {
                final boolean isBlocked = resultSet.next();
                this.blockedCache.put(targetPlayer.getUniqueId().toString() + "_" + faction.getName(), isBlocked);
                return isBlocked;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<BlockPlayerInfo> getAllBlockedPlayers(Beruf.Berufe faction) {
        final List<BlockPlayerInfo> blockPlayerInfos = new ArrayList<>();
        try (final PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT nid.id, nid.uuid, nid.name FROM blocked_notruf bn LEFT JOIN nrp_id nid ON nid.id = bn.nrp_id WHERE bn.berufID = ?")) {
            statement.setInt(1, faction.getID());
            try (final ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    blockPlayerInfos.add(new BlockPlayerInfo(resultSet.getString(3), UUID.fromString(resultSet.getString(2)), resultSet.getInt(1)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return blockPlayerInfos;
    }

    private String buildNearbyPlayersString(final Location location, final Beruf.Berufe faction) {
        final StringBuilder stringBuilder = new StringBuilder(getPrefix());
        stringBuilder.append("§6Am nächsten sind: ");
        List<Player> nearbyPlayers = getNearbyPlayersOfFactionToLocation(location, faction);
        if(nearbyPlayers.isEmpty()) {
            return stringBuilder.toString() + "§6Keine Person im Dienst ist in der Nähe";
        }

        final Player firstPlayer = nearbyPlayers.get(0);
        final Player secondPlayer = getOrDefault(1, null, nearbyPlayers);

        stringBuilder.append(Script.getName(firstPlayer)).append(" (").append((int) firstPlayer.getLocation().distance(location)).append("m)");
        if(secondPlayer != null) {
            stringBuilder.append(" und ").append(Script.getName(secondPlayer)).append(" (").append((int) secondPlayer.getLocation().distance(location)).append("m)");
        }

        return stringBuilder.toString();
    }

    private <E> E getOrDefault(int index, E defaultValue, List<E> list) {
        return index <= list.size() - 1 ? list.get(index) : defaultValue;
    }
}
