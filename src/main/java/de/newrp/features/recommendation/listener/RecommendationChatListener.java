package de.newrp.features.recommendation.listener;

import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.recommendation.IRecommendationService;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class RecommendationChatListener implements Listener {


    private final IRecommendationService recommendationService = DependencyContainer.getContainer().getDependency(IRecommendationService.class);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if(event.getMessage().startsWith("/")) {
            return;
        }

        if (recommendationService.hasActiveChatInput(player)) {
            event.setCancelled(true);
            recommendationService.giveRecommendation(player, event.getMessage());
            player.sendMessage("§8[§6§lMarktforschung§8] §6§l» §7Vielen Dank für deine Rückmeldung!");
            player.playSound(player.getEyeLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }
    }

}
