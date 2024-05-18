package de.newrp.features.recommendation;

import org.bukkit.entity.Player;

public interface IRecommendationService {

    void openInventoryForRecommendation(final Player player);

    boolean hasRecommendation(final Player player);

    void closeInventory(final Player player);

    void giveRecommendation(final Player player, final String value);

}
