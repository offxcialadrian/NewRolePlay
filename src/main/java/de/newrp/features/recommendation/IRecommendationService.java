package de.newrp.features.recommendation;

import de.newrp.features.recommendation.inventory.RecommendationInventoryHolder;
import org.bukkit.entity.Player;

public interface IRecommendationService {

    void openInventoryForRecommendation(final Player player);

    boolean hasRecommendation(final Player player);

    void closeInventory(final Player player, final RecommendationInventoryHolder holder);

    void giveRecommendation(final Player player, final String value);

    void activateChatInput(final Player player);

    boolean hasActiveChatInput(final Player player);

}
