package de.newrp.Player;

import de.newrp.API.*;
import de.newrp.GFB.GFB;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class HouseListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(GFB.CURRENT.containsKey(p.getName())) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign s = (Sign) e.getClickedBlock().getState();
        if(s.getLine(1).startsWith("==") && s.getLine(1).endsWith("==")) {
            House h = House.getHouseByID(Integer.parseInt(s.getLine(1).replace("==", "").replace(" ", "")));
            if(h == null) return;
            if(h.getSignLocation().distance(e.getClickedBlock().getLocation()) > 1) return;
            Inventory inv = Bukkit.createInventory(null, 4*9, "§6Haus " + h.getID());
            inv.setItem(13, new ItemBuilder(Material.PLAYER_HEAD).setName("§6Besitzer").setLore((h.getOwner()==0?"§8§c» §cKein Besitzer": "§8§c» §6" + Script.getOfflinePlayer(h.getOwner()).getName())).build());
            inv.setItem(21, new ItemBuilder(Material.PAPER).setName("§6geringster Marktwert").setLore("§8§c» §6" + h.getPrice() + "€").build());
            inv.setItem(22, new ItemBuilder(Material.PAPER).setName("§6Hausaddons").setLore("§8§c» §6Hauskasse: " + (h.hasAddon(HouseAddon.HAUSKASSE)?"Ja":"Nein"), "§8§c» §6Mieterslots: " + h.getSlots(), "§8§c» §6Alarmanlage: " + (h.hasAddon(HouseAddon.ALARM)?"Ja":"Nein"), "§8§c» §6Waffenschrank: " + (h.hasAddon(HouseAddon.WAFFENSCHRANK)?"Ja":"Nein"), "§8§c» §6Kühlschrank: " + (h.hasAddon(HouseAddon.KUEHLSCHRANK)?"Ja":"Nein")).build());
            if(h.getOwner() == 0) inv.setItem(23, new ItemBuilder(Material.EMERALD_BLOCK).setName("§aKaufen").setLore("§6Kaufe dieses Haus für " + h.getPrice() + "€").build());
            Script.fillInv(inv);
            p.openInventory(inv);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().startsWith("§6Haus ")) return;
        e.setCancelled(true);
        e.getView().close();
        if(e.getCurrentItem().getType() == Material.EMERALD_BLOCK) {
            Player p = (Player) e.getWhoClicked();

            if(House.getHouses(Script.getNRPID(p)).size() >= SlotLimit.HOUSE.get(Script.getNRPID(p))) {
                p.sendMessage(Messages.ERROR + "Du hast bereits die maximale Anzahl an Häusern erreicht.");
                return;
            }

            House h = House.getHouseByID(Integer.parseInt(e.getView().getTitle().replace("§6Haus ", "")));
            if(h == null) return;
            if(h.getOwner() != 0) return;
            if(Script.getMoney(p, PaymentType.BANK) >= h.getPrice()) {
                Achievement.HAUS.grant(p);
                Script.removeMoney(p, PaymentType.BANK, h.getPrice());
                h.setOwner(Script.getNRPID(p));
                h.updateSign();
                Script.executeAsyncUpdate("INSERT INTO house_bewohner (houseID, mieterID, vermieter, miete, nebenkosten, immobilienmarkt) VALUES (" + h.getID() + ", " + Script.getNRPID(p) + ", TRUE, 0, 0, FALSE); ");
                p.sendMessage(Script.PREFIX + "Du hast das Haus gekauft.");
            } else {
                p.sendMessage(Messages.ERROR + "Du hast nicht ausreichend Geld.");
            }
        }
    }

}
