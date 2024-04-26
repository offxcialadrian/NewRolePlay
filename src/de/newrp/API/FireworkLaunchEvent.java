package de.newrp.API;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class FireworkLaunchEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final ItemStack firework;

    public FireworkLaunchEvent(Player p, ItemStack firework) {
        super(p);
        this.firework = firework;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ItemStack getFirework() {
        return this.firework;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
