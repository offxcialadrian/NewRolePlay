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
import java.util.Objects;

public class SperrShop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.GOVERNMENT)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (Beruf.getAbteilung(p, true) != Abteilung.Abteilungen.FINANZAMT) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        Shops shop = null;
        for (Shops shops : Shops.values()) {
            if(shops.getBuyLocation() != null) {
                if (p.getLocation().distance(shops.getBuyLocation()) < 5) {
                    shop = shops;
                }
            }
        }

        if (shop == null) {
            p.sendMessage(Messages.ERROR + "Du befindest dich in keinem Shop.");
            return true;
        }

        if (shop.isLocked()) {
            Beruf.Berufe.GOVERNMENT.sendMessage(Shop.PREFIX + "Der Shop " + shop.getPublicName() + " ist nun wieder entsperrt.");
            if (Objects.requireNonNull(Script.getOfflinePlayer(shop.getOwner())).isOnline()) {
                Script.getOfflinePlayer(shop.getOwner()).getPlayer().sendMessage(Shop.PREFIX + "Dein Shop " + shop.getPublicName() + " wurde von der Stadt wieder entsperrt.");
            } else {
                Script.addOfflineMessage(shop.getOwner(), Shop.PREFIX + "Dein Shop " + shop.getPublicName() + " wurde von der Stadt wieder entsperrt.");
            }
            Log.WARNING.write(p, "hat den Shop " + shop.getPublicName() + " von " + Script.getOfflinePlayer(shop.getOwner()).getName() + " entsperrt (SPERRSHOP).");
            shop.setLocked(false);
        } else {
            Stadtkasse.removeStadtkasse(5000, "Sperren des Shops " + shop.getPublicName() + " durch " + Script.getName(p));
            Beruf.Berufe.GOVERNMENT.sendMessage(Shop.PREFIX + "Der Shop " + shop.getPublicName() + " ist nun gesperrt.");
            Beruf.Berufe.GOVERNMENT.sendMessage(Messages.INFO + "Dies hat die Stadtkasse 5000€ gekostet.");
            if (Objects.requireNonNull(Script.getOfflinePlayer(shop.getOwner())).isOnline()) {
                Script.getOfflinePlayer(shop.getOwner()).getPlayer().sendMessage(Shop.PREFIX + "Dein Shop " + shop.getPublicName() + " wurde von der Stadt gesperrt.");
            } else {
                Script.addOfflineMessage(shop.getOwner(), Shop.PREFIX + "Dein Shop " + shop.getPublicName() + " wurde von der Stadt gesperrt.");
            }
            Log.WARNING.write(p, "hat den Shop " + shop.getPublicName() + " von " + Script.getOfflinePlayer(shop.getOwner()).getName() + " gesperrt (SPERRSHOP).");
            shop.setLocked(true);
        }

        return true;
    }
}
