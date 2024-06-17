package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Chat.Me;
import de.newrp.Waffen.Weapon;
import de.newrp.Waffen.WeaponData;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CheckGun implements CommandExecutor {

    private static String PREFIX = "§8[§6Waffen§8] " + Messages.ARROW + " §7";
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (args.length == 0) {
            p.sendMessage("");
            p.sendMessage("§8===§6Waffen Info§8===");
            HashMap<Weapon, WeaponData> wdata = WeaponData.getWeaponData(Script.getNRPID(p));
            for (WeaponData data : wdata.values()) {
                if (data.getWear() > 0) {
                    p.sendMessage(" §7- §9Waffe: §b" + data.getWeapon().getName());
                    p.sendMessage(" §7- §9Munition(" + data.getWeapon().getAmmoType().getName() + "): §b" + data.getAmmo());
                    p.sendMessage(" §7- §9Verschleiss: §b" + data.getWear() + "/" + data.getWeapon().getMaxWear());
                    p.sendMessage("");
                }
            }
        } else if (args.length == 1) {
            if (Beruf.getBeruf(p) == Beruf.Berufe.POLICE || Beruf.getBeruf(p) == Beruf.Berufe.BUNDESKRIMINALAMT) {
                if(!Duty.isInDuty(p)) {
                    p.sendMessage(Messages.ERROR + "Du musst im Dienst sein um diese Funktion zu nutzen.");
                    return true;
                }
                Player tg = Script.getPlayer(args[0]);
                if (tg != null) {
                    if (!Script.isInRange(p.getLocation(), tg.getLocation(), 5)) {
                        p.sendMessage(Messages.ERROR+ "Der Spieler ist zu weit entfernt.");
                    } else {
                        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " nach Waffen durchsucht..");
                        tg.sendMessage(PREFIX + Script.getName(p) + " hat dich nach Waffen durchsucht..");
                        Me.sendMessage(p, "durchsucht " + Script.getName(tg) + " nach Waffen.");

                        boolean b = false;
                        ItemStack[] inventory = tg.getInventory().getContents();
                        f:
                        for (ItemStack item : inventory) {
                            if (item != null && !item.getType().equals(Material.AIR)) {
                                for (Weapon w : Weapon.values()) {
                                    if (item.getType().equals(w.getWeapon().getType())) {
                                        b = true;
                                        break f;
                                    }
                                }
                            }
                        }
                        for (Weapon w : Weapon.values()) {
                            if (p.getItemOnCursor().getType().equals(w.getWeapon().getType())) {
                                b = true;
                                break;
                            }
                        }
                        if (b) {
                            p.sendMessage(PREFIX + "§cAchtung, der Spieler ist bewaffnet!");
                        } else {
                            p.sendMessage(PREFIX + "Der Spieler trägt keine Waffen bei sich.");
                        }
                    }
                } else {
                    p.sendMessage(Messages.PLAYER_NOT_FOUND);
                }
            }
        }
        return true;
    }
}
