package de.newrp.API;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class TrashcanEmptyEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Block b;

    public TrashcanEmptyEvent(Player p, Block b) {
        super(p);
        this.b = b;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Block getBlock() {
        return this.b;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
