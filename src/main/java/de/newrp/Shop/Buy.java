package de.newrp.Shop;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Drone;
import de.newrp.Police.Fahndung;
import de.newrp.Shop.gasstations.GasStationBuyHandler;
import de.newrp.Shop.generic.GenericBuyHandler;
import de.newrp.Shop.gym.GymBuyHandler;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Buy implements CommandExecutor {

    public static final HashMap<String, Shops> current = new HashMap<>();
    public static final HashMap<String, Integer> amount = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        final Player player = (Player) commandSender;
        final Shops shop = getNearbyShop(player);

        if(Drone.isDrone(player)) {
            player.sendMessage(Messages.ERROR + "Du kannst als Drohne keine Shops benutzen.");
            return true;
        }

        if (shop == null) {
            player.sendMessage(Messages.ERROR + "§cDu bist nicht in der Nähe von einem Shop.");
            return true;
        }

        if(shop.getType() == ShopType.GUNSHOP && Fahndung.isFahnded(player)) {
            player.sendMessage(Messages.ERROR + "§cDu kannst keine Waffen kaufen, da du gesucht wirst.");
            return true;
        }

        final GenericBuyHandler buyHandler = resolveCustomBuyHandlerByType(shop.getType());
        if(buyHandler != null) {
            final boolean buyHandlerResult = buyHandler.buyItem(player, shop);
            if(buyHandlerResult) {
                return true;
            }
        }

        current.put(player.getName(), shop);
        final Map<Integer, ItemStack> shopItems = shop.getItems();
        final int inventorySize = (shopItems.size() > 9 ? 3 : 2) * 9;
        Inventory inv = player.getServer().createInventory(null, inventorySize, "§6" + shop.getPublicName());
        int i = 0;

        for (Map.Entry<Integer, ItemStack> n : shopItems.entrySet()) {
            ItemStack is = n.getValue();
            if (is == null) {
                continue;
            }
            inv.setItem(i++, is);
        }

        if(i == 0) {
            player.sendMessage(Messages.ERROR + "Dieser Shop bietet derzeit nichts an.");
            return true;
        }

        // Reset amount if players doesn't
        amount.remove(player.getName());

        inv.setItem(((inventorySize / 9) <= 2 ? 13 : 22), Script.setName(Material.BARRIER, "§cSchließen"));
        player.openInventory(inv);

        if(args.length == 1) {
            if(!Script.isInt(args[0])) {
                player.sendMessage(Messages.ERROR + "§cBitte gib eine gültige Zahl an.");
                return true;
            }

            int a = Integer.parseInt(args[0]);
            if(a < 1) {
                player.sendMessage(Messages.ERROR + "§cBitte gib eine gültige Zahl an.");
                return true;
            }

            amount.put(player.getName(), a);
            player.sendMessage(Messages.INFO + "Du kaufst nun " + a + "x.");
        }

        return false;
    }

    /**
     * Gets the shop buy handler
     * @param shop Shop type of the shop where the buy command is executed
     * @return The corresponding buy handler (Null if no handler)
     */
    private GenericBuyHandler resolveCustomBuyHandlerByType(final ShopType shop) {
        switch (shop) {
            case GAS_STATION:
                return new GasStationBuyHandler();
            case GYM:
                return new GymBuyHandler();
            default:
                return null;
        }
    }

    /**
     * Gets the nearby shop
     * @param player The player where a shop should be located
     * @return The shop where the player is buying - Null if player is not at a shop
     */
    private Shops getNearbyShop(final Player player) {
        for (final Shops shop : Shops.values()) {
            if (player.getLocation().distance(shop.getBuyLocation()) < 4) {
                return shop;
            }
        }
        return null;
    }

}
