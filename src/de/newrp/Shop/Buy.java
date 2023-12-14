package de.newrp.Shop;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Location;
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

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        Location ploc = p.getLocation();
        Shops s = null;
        for (Shops shop : Shops.values()) {
            Location l = shop.getBuyLocation();
            if (l != null) {
                if (ploc.distance(l) < 4) {
                    s = shop;
                    break;
                }
            }
        }

        if (s == null) {
            p.sendMessage("§cDu bist nicht in der nähe von einem Shop.");
            return true;
        }

        current.put(p.getName(), s);
        HashMap<Integer, ItemStack> c = s.getItems();
        int size = (c.size() > 9 ? 3 : 2) * 9;
        Inventory inv = p.getServer().createInventory(null, size, "§6" + s.getPublicName());
        int i = 0;

        for (Map.Entry<Integer, ItemStack> n : c.entrySet()) {
            ItemStack is = n.getValue();
            inv.setItem(i++, is);
        }

        if(i == 0) {
            p.sendMessage(Messages.ERROR + "Dieser Shop bietet derzeit nichts an.");
            return true;
        }

        inv.setItem(((size / 9) <= 2 ? 13 : 22), Script.setName(Material.BARRIER, "§cSchließen"));
        p.openInventory(inv);

        return false;
    }
}
