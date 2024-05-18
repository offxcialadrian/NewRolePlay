package de.newrp.features.recommendation.impl;

import de.newrp.API.Debug;
import de.newrp.NewRoleplayMain;
import de.newrp.features.recommendation.IRecommendationService;
import de.newrp.features.recommendation.inventory.RecommendationInventory;
import de.newrp.features.recommendation.inventory.RecommendationInventoryHolder;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RecommendationService implements IRecommendationService {

    @Override
    public void openInventoryForRecommendation(Player player) {
        final RecommendationInventory recommendationInventory = new RecommendationInventory(player);
        player.closeInventory();
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
    public void closeInventory(Player player) {
        final RecommendationInventoryHolder holder = (RecommendationInventoryHolder) player.getOpenInventory().getTopInventory().getHolder();
        if(holder == null) {
            Debug.debug("Cannot close inventory because the holder is null on player " + player.getName());
            return;
        }
        holder.setClosed(true);
        player.closeInventory();
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
    }
}
