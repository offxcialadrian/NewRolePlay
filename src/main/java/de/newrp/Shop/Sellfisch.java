package de.newrp.Shop;

import de.newrp.API.*;
import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.Notifications;
import de.newrp.Administrator.Punish;
import de.newrp.GFB.GFB;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Sellfisch implements CommandExecutor {

    public static final HashMap<String, Long> COOLDOWN = new HashMap<>();

    public static final String PREFIX = "§8[§9SellFisch§8]§9 " + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if (p.getLocation().distance(Navi.FISCH.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist nicht am Fischladen.");
            return true;
        }

        if(COOLDOWN.containsKey(p.getName()) && COOLDOWN.get(p.getName()) > System.currentTimeMillis()) {
            p.sendMessage(Messages.ERROR + "Du kannst erst in " + Script.getRemainingTime(COOLDOWN.get(p.getName())) + " wieder verkaufen.");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(Messages.ERROR + "/sellfisch");
            return true;
        }

        if(BuildMode.isInBuildMode(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst im Baumodus keine Fische verkaufen.");
            return true;
        }

        if (GFB.CURRENT.containsKey(p.getName())) {
            p.sendMessage(Messages.ERROR + "Du kannst nicht verkaufen, während du einen GFB-Job hast.");
            return true;
        }

        if(p.getInventory().getItemInOffHand().getType() != Material.AIR) {
            Notifications.sendMessage(Notifications.NotificationType.ADVANCED_ANTI_CHEAT, "Verdacht auf SellFisch Buguse bei " + Script.getName(p) + " (Wahrscheinlichkeit: 100%) [" + p.getInventory().getItemInOffHand().getType() + "]");
            if(Script.getLevel(p) == 1) {
                Script.executeUpdate("INSERT INTO `ban` (id, ban_id, nrp_id, since, until, reason, banned_by) VALUES (NULL, '" + Punish.generatePunishID() + "', '" + Script.getNRPID(p) + "', '" + System.currentTimeMillis() + "', " + ("NULL") + ", '" + Punish.Violation.BUGUSE_EIGENERTRAG.getName() + "', '" + 0 + "');");
                Log.WARNING.write(p, "wurde vom AntiCheat für " + Punish.Violation.BUGUSE_EIGENERTRAG.getName() + " gebannt.");
                Bukkit.broadcastMessage(Script.PREFIX + "§c" + Script.getName(p) + " wurde vom " + "AntiCheat" + " für §l" + Punish.Violation.BUGUSE_EIGENERTRAG.getName() + "§c gebannt.");
                p.kickPlayer("§8» §cNRP × New RolePlay §8┃ §cBANN §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gebannt§8.\n\n§7Grund §8× §e" + Punish.Violation.BUGUSE_EIGENERTRAG.getName() + "\n§7Gebannt bis §8× §e" + "Lebenslang" + "\n\n§7Eine Entbannungsantrag ist ausgeschlossen.\n\n§8§m------------------------------");
                Script.setMoney(p, PaymentType.BANK, 0);
                Script.setMoney(p, PaymentType.CASH, 0);
            } else {
                p.sendMessage(Messages.ERROR + "Du kannst nicht verkaufen, während du einen Gegenstand in der Offhand hast.");
                return true;
            }
        }
        p.getInventory().getItemInOffHand().setType(Material.AIR);

        int i = 0;
        int c = 0;
        //count fish but ignore offhand
        Inventory inv = p.getInventory();
        for(int j = 0; j < inv.getSize(); j++) {
            if(j == 45) continue;
            ItemStack is = inv.getItem(j);
            if (is == null) continue;
            if (is.getType() != Material.TROPICAL_FISH && is.getType() != Material.COD && is.getType() != Material.SALMON && is.getType() != Material.PUFFERFISH)
                continue;
            if(is.getType() == Material.PUFFERFISH) i += ((3*is.getAmount())/2);
            if(is.getType() == Material.TROPICAL_FISH) i += 10*is.getAmount();
            if(is.getType() == Material.COD) i += is.getAmount();
            if(is.getType() == Material.SALMON) i += (int) (1.5*is.getAmount());
            c += is.getAmount();
        }

        if (i == 0) {
            p.sendMessage(Messages.ERROR + "Du hast keine Fische im Inventar.");
            return true;
        }

        int price = i;
        for (ItemStack is : p.getInventory().getContents()) {
            if (is == null) continue;
            if (is.getType() != Material.TROPICAL_FISH && is.getType() != Material.COD && is.getType() != Material.SALMON && is.getType() != Material.PUFFERFISH)
                continue;
            p.getInventory().remove(is);
            if(p.getInventory().getItemInOffHand().getType() != Material.AIR) {
                Notifications.sendMessage(Notifications.NotificationType.ADVANCED_ANTI_CHEAT, "Verdacht auf SellFisch Buguse bei " + Script.getName(p) + " (Wahrscheinlichkeit: 100%) [" + p.getInventory().getItemInOffHand().getType() + "]");
            }
            p.getInventory().getItemInOffHand().setType(Material.AIR);

        }

        p.sendMessage(PREFIX + "Du hast " + c + " Fische für " + price + "€ verkauft.");
        COOLDOWN.put(p.getName(), System.currentTimeMillis() + 1000 * 60 * 5);
        Script.addMoney(p, PaymentType.CASH, price);


        return false;
    }


}
