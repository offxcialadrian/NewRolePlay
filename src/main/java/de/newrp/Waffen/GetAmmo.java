package de.newrp.Waffen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.SlotLimit;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.GoTo;
import de.newrp.Administrator.SDuty;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import de.newrp.Player.Hotel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetAmmo implements CommandExecutor, TabCompleter {
    public static final String PREFIX = "§8[§7Waffenschrank§8] §3";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        House h = House.getInsideHouse(p);
        if(House.getHouses(Script.getNRPID(p)).size() > SlotLimit.HOUSE.get(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du wohnst in " + House.getHouses(Script.getNRPID(p)).size() + " Häusern. Du kannst nur " + SlotLimit.HOUSE.get(Script.getNRPID(p)) + " Häuser besitzen.");
            p.sendMessage(Messages.INFO + "Du kannst einen weiteren Hausslot im Shop erwerben.");
            return true;
        }

        if (h == null && !(Hotel.isInHotelRoom(p) && Hotel.getHotelRoom(p).getType() == Hotel.RoomType.PRAESIDENTEN_SUITE)) {
            p.sendMessage(PREFIX + "Deine Waffen sind Zuhause im Waffenschrank.");
            return true;
        }

        if (!h.hasAddon(HouseAddon.WAFFENSCHRANK) && !(Hotel.isInHotelRoom(p) && Hotel.getHotelRoom(p).getType() == Hotel.RoomType.PRAESIDENTEN_SUITE)) {
            p.sendMessage(GetAmmo.PREFIX + "Dein Haus hat keinen Waffenschrank.");
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(PREFIX + "/getammo [Waffe] [Menge]");
            return true;
        }

        Weapon w = Script.getWeapon(args[0]);
        if (w == null) {
            p.sendMessage(PREFIX + "/getammo [Waffe] [Menge]");
            return true;
        }

        if (!p.getInventory().contains(w.getWeapon().getType())) {
            p.sendMessage(PREFIX + "Du musst die Waffe zuerst aus dem Waffenschrank nehmen.");
            return true;
        }

        if (!Script.isInt(args[1])) {
            p.sendMessage(PREFIX + "/getammo " + w.getName() + " [Menge]");
            return true;
        }

        int amount = Integer.parseInt(args[1]);
        if (amount < 0) {
            p.sendMessage(PREFIX + "Gib eine Menge für die Munition an.");
            return true;
        }

        int id = Script.getNRPID(p);

        int left = w.getMunition(id);
        if (amount > left) {
            p.sendMessage(PREFIX + "Du hast nicht genug Munition.");
            return true;
        }

        ItemStack weapon = null;
        for (ItemStack is : p.getInventory().getContents()) {
            if (is != null) {
                if (is.getType().equals(w.getWeapon().getType())) {
                    weapon = is;
                    break;
                }
            }
        }

        if (weapon == null) return true;

        int ammunitionInWeapon = Waffen.getAmmo(weapon) + Waffen.getAmmoTotal(weapon);

        w.removeMunition(id, amount);
        p.getInventory().remove(w.getWeapon().getType());

        int newAmmunitionInWeapon = ammunitionInWeapon + amount;

        int magazine;
        int total;
        if (newAmmunitionInWeapon > w.getMagazineSize()) {
            magazine = w.getMagazineSize();
            total = newAmmunitionInWeapon - w.getMagazineSize();
        } else {
            magazine = newAmmunitionInWeapon;
            total = 0;
        }

        p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), magazine, total));
        p.sendMessage(PREFIX + "Du hast deine " + w.getName() + " mit " + amount + " Kugeln beladen (" + (left - amount) + " Kugeln verbleibend)");
        w.removeWear(id, 1);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("getammo") || cmd.getName().equalsIgnoreCase("getmunition")) {
            final List<String> oneArgList = new ArrayList<>();
            final List<String> completions = new ArrayList<>();
            for (Weapon weapon : Weapon.values()) {
                if(!GetGun.haveGun(p, weapon)) continue;
                oneArgList.add(weapon.getName());
            }

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], oneArgList, completions);
            }

            if (args.length == 2) {
                return null;
            }
            Collections.sort(completions);
            return completions;
        }
        return Collections.EMPTY_LIST;
    }

}