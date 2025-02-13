package de.newrp.Waffen;

import de.newrp.API.Log;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.BuildMode;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.House.House;
import de.newrp.House.HouseAddon;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.bizwar.IBizWarService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DropAmmo implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        House h = House.getInsideHouse(p);

        if(GangwarCommand.isInGangwar(p) || DependencyContainer.getContainer().getDependency(IBizWarService.class).isMemberOfBizWar(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst im Gangwar keine Munition droppen.");
            return true;
        }

        if (h != null) {
            if (h.hasAddon(HouseAddon.WAFFENSCHRANK)) {
                if (args.length != 2) {
                    p.sendMessage(Messages.ERROR + "/dropammo [Waffe] [Menge]");
                } else {
                    if (BuildMode.isInBuildMode(p)) {
                        p.sendMessage(Messages.ERROR + "Du kannst im Buildmode keine Munition droppen.");
                        return true;
                    }

                    Weapon w = null;
                    for (Weapon weapons : Weapon.values()) {
                        if (args[0].equalsIgnoreCase(weapons.getName())) {
                            w = weapons;
                            break;
                        }
                    }
                    if (w != null) {
                        ItemStack item = null;
                        HashMap<Weapon, WeaponData> weapon_data = WeaponData.getWeaponData(Script.getNRPID(p));
                        if(!weapon_data.containsKey(w)) {
                            p.sendMessage(Messages.ERROR + "Du kannst keine Munition für eine Waffe einlagern, welche du nicht im Besitz hast!");
                            return true;
                        }

                        for (ItemStack is : p.getInventory().getContents()) {
                            if (is != null) {
                                if (is.getType().equals(w.getWeapon().getType())) {
                                    if (is.hasItemMeta() && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals(w.getName())) {
                                        item = is;
                                        break;
                                    }
                                }
                            }
                        }
                        if (item != null) {
                            if (Script.isInt(args[1])) {
                                int i = Integer.parseInt(args[1]);
                                if (i > 0) {
                                    if (i <= 350) {
                                        int ammo = Waffen.getAmmoTotal(item);
                                        if (ammo >= i) {
                                            w.addMunition(Script.getNRPID(p), i);
                                            Waffen.setAmmo(item, Waffen.getAmmo(item), (ammo - i));
                                            p.sendMessage(GetAmmo.PREFIX + "Du hast " + i + " " + w.getName() + " Kugeln zurück in deinen Waffenschrank gelegt.");
                                            Log.HIGH.write(p, p.getName() + " hat " + i + " " + w.getName() + " Kugeln zurück in den Waffenschrank gelegt.");
                                        } else {
                                            p.sendMessage(Messages.ERROR + "Du hast nicht genug Munition in der Waffe.");
                                        }
                                    } else {
                                        p.sendMessage(Messages.ERROR + "Du kannst nicht mehr als 350 Munition auf einmal ablegen.");
                                    }
                                } else {
                                    p.sendMessage(Messages.ERROR + "Du kannst nicht weniger als 1 Munition ablegen.");
                                }
                            } else {
                                p.sendMessage(Messages.ERROR + "/dropammo [Waffe] [Menge]");
                            }
                        } else {
                            p.sendMessage(Messages.ERROR + "Du hast diese Waffe nicht bei dir.");
                        }
                    } else {
                        p.sendMessage(Messages.ERROR + "/dropammo [Waffe] [Menge]");
                    }
                }
            } else {
                p.sendMessage(GetAmmo.PREFIX + "Dein Haus hat keinen Waffenschrank.");
            }
        } else {
            p.sendMessage(Messages.ERROR + "Du bist nicht in deinem Haus.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args) {
        Player p = (Player) cs;
        if (cmd.getName().equalsIgnoreCase("dropammo")) {
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
