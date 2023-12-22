package de.newrp.Waffen;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GetGun implements CommandExecutor, Listener {
    public static boolean haveGun(Player p, Weapon weapon) {
        HashMap<Weapon, WeaponData> weapon_data = WeaponData.getWeaponData(Script.getNRPID(p));

        if (weapon_data != null) {
            WeaponData data = weapon_data.get(weapon);
            return data != null && data.getWear() > 0;
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        int id = Script.getNRPID(p);
        if (Sperre.WAFFENSPERRE.isActive(id)) {
            p.sendMessage(Messages.ERROR + "Mit einer Waffensperre kannst du keine Waffen aus dem Waffenschrank nehmen.");
            return true;
        }

        if(House.getHouses(Script.getNRPID(p)).size() > SlotLimit.HOUSE.get(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du wohnst in " + House.getHouses(Script.getNRPID(p)).size() + " Häusern. Du kannst nur " + SlotLimit.HOUSE.get(Script.getNRPID(p)) + " Häuser besitzen.");
            p.sendMessage(Messages.INFO + "Du kannst einen weiteren Hausslot im Shop erwerben.");
            return true;
        }

        House h = House.getInsideHouse(p);
        if (h != null) {
            if (h.hasAddon(HouseAddon.WAFFENSCHRANK)) {
                if (!canTakeAnotherWeapon(p)) {
                    p.sendMessage(GetAmmo.PREFIX + "Du kannst nur mehrere Waffen tragen wenn diese über Waffenskill-Level 6 sind.");
                    return true;
                }

                HashMap<Weapon, WeaponData> weapon_data = WeaponData.getWeaponData(id);
                if (weapon_data.isEmpty()) {
                    p.sendMessage(GetAmmo.PREFIX + "Du besitzt keine Waffen.");
                    return true;
                }

                if(BuildMode.isInBuildMode(p)) {
                    p.sendMessage(Messages.ERROR + "Du kannst keine Waffen aus dem Waffenschrank nehmen wenn du im BuildMode bist.");
                    return true;
                }

                Inventory inv = p.getPlayer().getServer().createInventory(null, 9, "§l§6Waffen Menü");

                int i = 0;
                for (WeaponData data : weapon_data.values()) {
                    if (!Script.haveGunInInventory(p, data.getWeapon()) && data.getWear() > 0) {
                        ItemStack is = data.getWeapon().getWeapon();
                        ItemMeta meta = is.getItemMeta();
                        meta.setLore(Arrays.asList("§6Munition§8:§7 " + data.getAmmo(), "§6Verschleiss§8:§7 " + data.getWear() + "/" + data.getWeapon().getMaxWear()));
                        is.setItemMeta(meta);
                        inv.setItem(i++, is);
                    } else {
                        Debug.debug("has gun in inventory " + data.getWeapon().getName());
                    }
                }
                p.openInventory(inv);
            } else {
                p.sendMessage(GetAmmo.PREFIX + "Dein Haus hat keinen Waffenschrank.");
            }
        } else {
            p.sendMessage(GetAmmo.PREFIX + "Deine Waffen sind Zuhause im Waffenschrank.");
        }
        return true;
    }

    public boolean canTakeAnotherWeapon(Player p) {
        List<Weapon> list = new ArrayList<>();

        for (ItemStack is : p.getInventory().getContents()) {
            if (is == null || is.getType().equals(Material.AIR)) continue;
            for (Weapon w : Weapon.values()) {
                //if (w == Weapon.FB2000) continue;
                if (is.getType().equals(w.getWeapon().getType())) {
                    list.add(w);
                    break;
                }
            }
        }

        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("§l§6Waffen Menü")) {
            if (e.getCurrentItem() != null && (e.getCurrentItem().getType() != Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                e.setCancelled(true);
                e.getView().close();
                int id = Script.getNRPID(p);
                if (Sperre.WAFFENSPERRE.isActive(id)) {
                    p.sendMessage(Messages.ERROR + "Du hast noch eine Waffensperre.");
                } else {
                    Weapon w = null;
                    for (Weapon all : Weapon.values()) {
                        if (e.getCurrentItem().getType().equals(all.getWeapon().getType())) {
                            w = all;
                            break;
                        }
                    }
                    if (w != null) {
                        WeaponData wdata = WeaponData.getWeaponData(id, w);
                        if (wdata.getWear() > 0) {
                            p.getInventory().addItem(Waffen.setAmmo(w.getWeapon(), 0, 0));
                            w.removeWear(id, 1);
                            p.sendMessage("§8[§7Waffenschrank§8] §3Du hast deine " + w.getName() + " aus dem Waffenschrank genommen.");
                            Log.NORMAL.write(p, "hat seine " + w.getName() + " aus dem Waffenschrank genommen.");
                        }
                    }
                }
            }
        }
    }
}
