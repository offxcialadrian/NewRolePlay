package de.newrp.features.recommendation.impl;

import de.newrp.API.Debug;
import de.newrp.NewRoleplayMain;
import de.newrp.features.recommendation.IRecommendationService;
import de.newrp.features.recommendation.inventory.RecommendationInventory;
import de.newrp.features.recommendation.inventory.RecommendationInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RecommendationService implements IRecommendationService {

    private final Set<UUID> activeChatInputs = new HashSet<>();

    @Override
    public void openInventoryForRecommendation(Player player) {
        final RecommendationInventory recommendationInventory = new RecommendationInventory(player);
        player.openInventory(recommendationInventory.getInventory());
    }

    @Override
    public boolean hasRecommendation(Player player) {
        try (final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("SELECT uuid FROM recommendation WHERE uuid=?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            try(final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
            return false;
        }
    }

    @Override
    public void closeInventory(Player player, RecommendationInventoryHolder holder) {
        holder.setClosed(true);
        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
            player.closeInventory();
        }, 2L);
        this.activeChatInputs.remove(player.getUniqueId());
    }

    @Override
    public void giveRecommendation(Player player, String value) {
        try(final PreparedStatement preparedStatement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO recommendation (uuid, value) VALUES (?, ?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, value);
            preparedStatement.executeUpdate();
            Debug.debug("Added recommendation from player " + player.getName() + " with value " + value + " to the database.");
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
        this.activeChatInputs.remove(player.getUniqueId());
    }

    @Override
    public void activateChatInput(Player player) {
        this.activeChatInputs.add(player.getUniqueId());
    }

    @Override
    public boolean hasActiveChatInput(Player player) {
        System.out.println("has active chat input: " + this.activeChatInputs.contains(player.getUniqueId()));
        return this.activeChatInputs.contains(player.getUniqueId());
    }
}
