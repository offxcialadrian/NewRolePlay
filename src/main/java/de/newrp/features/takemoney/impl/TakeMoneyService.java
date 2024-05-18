package de.newrp.features.takemoney.impl;

import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.features.takemoney.ITakeMoneyService;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TakeMoneyService implements ITakeMoneyService {

    private final Map<UUID, Integer> amountOfMoneyToBeTaken = new ConcurrentHashMap<>();

    @Override
    public boolean hasActiveMoneyToBeTaken(Player player) {
        return this.amountOfMoneyToBeTaken.containsKey(player.getUniqueId());
    }

    @Override
    public void takeMoneyFromPlayer(Player player) {
        if (!hasActiveMoneyToBeTaken(player)) {
            return;
        }

        final int amount = this.amountOfMoneyToBeTaken.get(player.getUniqueId());
        Script.removeMoney(player, PaymentType.CASH, amount);
        this.amountOfMoneyToBeTaken.remove(player.getUniqueId());
    }

    @Override
    public void addIllegalObtainedMoneyToPlayer(Player player, int amount) {
        this.amountOfMoneyToBeTaken.put(player.getUniqueId(), amount);
    }
}
