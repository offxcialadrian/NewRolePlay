package de.newrp.features.takemoney;

import org.bukkit.entity.Player;

public interface ITakeMoneyService {

    boolean hasActiveMoneyToBeTaken(final Player player);

    void takeMoneyFromPlayer(final Player player);

    void deleteMoney(final Player player);

    int getMoney(final Player player);

    void addIllegalObtainedMoneyToPlayer(final Player player, final int amount);

}
