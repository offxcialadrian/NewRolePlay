package de.newrp.House;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Leitungswasser implements CommandExecutor {

    private static final String PREFIX = "§8[§9Leitungswasser§8] §9" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        Player p = (Player) sender;

        int id = Script.getNRPID(p);
        House house = null;
        for (House h : House.getHouses(id)) {
            if (h.isInside(p)) {
                house = h;
                break;
            }
        }
        boolean equip = false;
        if (house == null) {
            if (Beruf.hasBeruf(p)) {
                if (p.getLocation().distance(Beruf.getBeruf(p).getEquipLoc()) < 7) equip = true;
            } else if (Organisation.hasOrganisation(p)) {
                if (p.getLocation().distance(Organisation.getOrganisation(p).getEquipLoc()) < 7) equip = true;
            }
        }
        if (house != null || equip) {
            if (p.getInventory().getItemInMainHand().getType().equals(Material.GLASS_BOTTLE)) {
                int amount = p.getInventory().getItemInMainHand().getAmount();
                ItemStack water = Script.setName(new ItemStack(Material.POTION, 1), "§9Trinkwasser");
                p.sendMessage(PREFIX + "Du hast deine Flasche mit Wasser gefüllt.");
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - amount);
                for (int i = 0; i < amount; i++) {
                    p.getInventory().addItem(water);
                }
                if (house != null) {
                    House.Mieter m = house.getMieterByID(house.getOwner());
                    int nebenkosten = 0;
                    nebenkosten = (amount * 2);
                    m.setNebenkosten(house, m.getNebenkosten() + nebenkosten);
                }
            } else {
                p.sendMessage(PREFIX + "Du hast keine Flasche in der Hand.");
            }
        } else {
            p.sendMessage(PREFIX + "Du bist nicht in deinem Haus.");
        }
        return true;
    }
}
