package de.newrp.Government;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Berufe.Abteilung;
import de.newrp.Berufe.Beruf;
import de.newrp.Shop.Shop;
import de.newrp.Shop.ShopItem;
import de.newrp.Shop.ShopType;
import de.newrp.Shop.Shops;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class TakeShop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(Beruf.getAbteilung(p, true) != Abteilung.Abteilungen.FINANZAMT) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        Shops shop = null;
        for(Shops shops : Shops.values()) {
            if(shops.getBuyLocation() != null) {
                if(p.getLocation().distance(shops.getBuyLocation()) < 5) {
                    shop = shops;
                }
            }
        }

        if(shop == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich in keinem Shop.");
            return true;
        }

        if(Stadtkasse.getStadtkasse() < (shop.getPrice()/2)) {
            p.sendMessage(Messages.ERROR + "Die Stadtkasse ist nicht ausreichend gefüllt.");
            return true;
        }

        if(shop.getOwner() == 0) {
            p.sendMessage(Messages.ERROR + "Dieser Shop gehört bereits der Stadt.");
            return true;
        }

        Stadtkasse.removeStadtkasse(shop.getPrice()/2, "Abkauf des Shops " + shop.getPublicName() + " durch " + Script.getName(p));
        Beruf.Berufe.GOVERNMENT.sendMessage(Shop.PREFIX + "Der Shop " + shop.getPublicName() + " ist nun im Besitz der Stadt.");
        Beruf.Berufe.GOVERNMENT.sendMessage(Messages.INFO + "Dies hat die Stadtkasse " + (shop.getPrice()/2) + "€ gekostet.");
        if(Script.getOfflinePlayer(shop.getOwner()).isOnline()) {
            Script.getOfflinePlayer(shop.getOwner()).getPlayer().sendMessage(Shop.PREFIX + "Dein Shop " + shop.getPublicName() + " wurde von der Stadt abgekauft.");
        } else {
            Script.addOfflineMessage(shop.getOwner(), Shop.PREFIX + "Dein Shop " + shop.getPublicName() + " wurde von der Stadt abgekauft.");
        }
        Log.WARNING.write(p, "hat den Shop " + shop.getPublicName() + " von " + Script.getOfflinePlayer(shop.getOwner()).getName() + " für " + (shop.getPrice()/2) + "€ abgekauft (TAKESHOP).");
        Script.addMoney(shop.getOwner(), PaymentType.BANK, shop.getPrice()/2);
        shop.setOwner(0);
        Script.executeAsyncUpdate("DELETE FROM shopprice WHERE shopID=" + shop.getID());
        for(ShopItem si : ShopItem.values()) {
            ArrayList<ShopType> types = new ArrayList<>(Arrays.asList(si.getShopTypes()));
            if(types.contains(shop.getType())) {
                Script.executeAsyncUpdate("INSERT INTO shopprice (amount, price, itemID, shopID) VALUES (" + si.getItemStack().getAmount() + ", " + Math.max(si.getBuyPrice()+(int) Script.getPercent(70, si.getBuyPrice()), 5) + ", " + si.getID() + ", " + shop.getID() + ")");
            }
        }


        return true;
    }
}
