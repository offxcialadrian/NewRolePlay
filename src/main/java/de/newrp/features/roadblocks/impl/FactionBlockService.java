package de.newrp.features.roadblocks.impl;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.config.MainConfig;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.roadblocks.IFactionBlockService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FactionBlockService implements IFactionBlockService {

    private final Map<Beruf.Berufe, Set<Block>> roadBlocks = new HashMap<>();
    private final MainConfig mainConfig = DependencyContainer.getContainer().getDependency(MainConfig.class);
    private final Map<Beruf.Berufe, Player> activePlayerForFaction = new HashMap<>();
    private final ItemStack operatorItem = buildItemStack();

    public FactionBlockService() {
        this.roadBlocks.put(Beruf.Berufe.POLICE, new HashSet<>());
        this.roadBlocks.put(Beruf.Berufe.RETTUNGSDIENST, new HashSet<>());
    }

    @Override
    public void placeFactionBlock(Player player, Location clickedBlock, Beruf.Berufe faction) {
        if(!this.roadBlocks.containsKey(faction)) {
            return;
        }
        final Block block = clickedBlock.getBlock();
        final Set<Block> blocks = this.roadBlocks.get(faction);
        if(blocks.contains(block)) {
            this.deleteFactionBlock(player, block, faction);
            return;
        }

        final Location blockOverClickedBlock = clickedBlock.clone().add(0, 1, 0);
        final Block targetBlock = blockOverClickedBlock.getBlock();

        if(targetBlock.getType() != Material.AIR) {
            player.sendMessage(getPrefix(faction) + "Du kannst hier keinen Berufsblock platzieren!");
            return;
        }

        if(getFactionBlockAmount(faction) >= getMaxFactionBlockAmount(faction)) {
            player.sendMessage(getPrefix(faction) + "Das Limit ist erreicht!");
            return;
        }

        targetBlock.setType(faction == Beruf.Berufe.POLICE ? Material.RED_NETHER_BRICK_WALL : Material.SLIME_BLOCK, true);
        this.roadBlocks.get(faction).add(targetBlock);
        player.getWorld().playSound(targetBlock.getLocation(), Sound.BLOCK_STONE_PLACE, 1f, 1f);
        // faction.sendMessage(getPrefix(faction) + Script.getName(player) + " hat ein " + getType(faction) + " platziert (" + getFactionBlockAmount(faction) + "/" + getMaxFactionBlockAmount(faction) + ")");
    }

    @Override
    public void deleteFactionBlock(Player player, Block block, Beruf.Berufe faction) {
        if(!this.roadBlocks.containsKey(faction)) {
            return;
        }

        block.setType(Material.AIR);
        this.roadBlocks.get(faction).remove(block);
        player.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1f);
        // faction.sendMessage(getPrefix(faction) + Script.getName(player) + " hat ein " + getType(faction) + " entfernt (" + getFactionBlockAmount(faction) + "/" + getMaxFactionBlockAmount(faction) + ")");
    }

    @Override
    public int getFactionBlockAmount(Beruf.Berufe faction) {
        return this.roadBlocks.get(faction).size();
    }

    @Override
    public void clearFactionBlockAmount(final Player player, Beruf.Berufe faction) {
        if(!this.roadBlocks.containsKey(faction)) {
            return;
        }

        for (final Block block : this.roadBlocks.get(faction)) {
            block.setType(Material.AIR);
        }
        this.roadBlocks.get(faction).clear();
        faction.sendMessage(getPrefix(faction) + Script.getName(player) + " hat das " + getType(faction) + "-System zurückgesetzt!");
    }

    @Override
    public int getMaxFactionBlockAmount(Beruf.Berufe faction) {
        return this.mainConfig.getMaxRoadBlockAmount();
    }

    @Override
    public String getPrefix(Beruf.Berufe faction) {
        return "§8[" + (faction == Beruf.Berufe.POLICE ? "§9RoadBlocks" : "§cSprungtücher") + "§8] §7";
    }

    @Override
    public String getType(Beruf.Berufe faction) {
        return faction == Beruf.Berufe.POLICE ? "RoadBlock" : "Sprungtuch";
    }

    @Override
    public void activateSystemForPlayer(Player player, Beruf.Berufe faction) {
        final Player activePlayer = isSystemActive(faction);

        if(activePlayer != null) {
            player.sendMessage(getPrefix(faction) + "Es ist bereits jemand im " + getType(faction) + "-System!");
            return;
        }

        this.activePlayerForFaction.put(faction, player);
        player.getInventory().addItem(this.operatorItem);
        faction.sendMessage(getPrefix(faction) + Script.getName(player) + " hat das " + getType(faction) + "-System betreten!");
    }

    @Override
    public Player isSystemActive(Beruf.Berufe faction) {
        return this.activePlayerForFaction.getOrDefault(faction, null);
    }

    @Override
    public void deactivateSystemForPlayer(Player player, Beruf.Berufe faction) {
        final Player activePlayer = isSystemActive(faction);

        if(activePlayer == null) {
            player.sendMessage(getPrefix(faction) + "Du bist nicht im " + getType(faction) + "-System!");
            return;
        }

        if(activePlayer.getUniqueId() != player.getUniqueId()) {
           player.sendMessage(getPrefix(faction) + "Du bist nicht der aktive Operator im " + getType(faction) + "-System!");
            return;
        }

        this.activePlayerForFaction.remove(faction);
        player.getInventory().removeItemAnySlot(this.operatorItem);
        faction.sendMessage(getPrefix(faction) + Script.getName(player) + " hat das " + getType(faction) + "-System verlassen!");
    }

    @Override
    public ItemStack getItem() {
        return this.operatorItem;
    }

    private ItemStack buildItemStack() {
        return new ItemBuilder(Material.STICK)
                .setName("§bBerufsblock Stock")
                .setLore("§7§oDieses Item ist an das Berufsblock System gebunden")
                .build();
    }
}
