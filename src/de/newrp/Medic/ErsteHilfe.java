package de.newrp.Medic;

import de.newrp.API.Friedhof;
import de.newrp.API.Licenses;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Chat.Me;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ErsteHilfe implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§4Erste-Hilfe§8] §4" + Messages.ARROW + " §7";
    private static final HashMap<String, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Licenses.ERSTE_HILFE.hasLicense(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du benötigst einen Erste-Hilfe-Schein um Erste-Hilfe leisten zu können.");
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/erstehilfe [Spieler]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(p.equals(tg)) {
            p.sendMessage(Messages.ERROR + "Du kannst dir nicht selbst Erste-Hilfe leisten.");
            return true;
        }

        if(!Friedhof.isDead(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht tot.");
            return true;
        }

        if(Friedhof.getDead(tg).getDeathtimeLeft() < 30) {
            p.sendMessage(Messages.ERROR + "Du kannst nichts mehr tun...");
            return true;
        }

        if (cooldown.containsKey(p.getName())) {
            if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
                p.sendMessage(Messages.ERROR + "Du musst noch " + Script.getRemainingTime(cooldown.get(p.getName())) + " warten.");
                return true;
            }
        }

        p.sendMessage(ErsteHilfe.PREFIX + "Du hast Erste-Hilfe angeboten.");

        Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, "§7Erste-Hilfe annehmen?");
        inv.setItem(1, Script.setName(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "§aErste-Hilfe von " + Script.getName(p) + " annehmen."));
        inv.setItem(3, Script.setName(new ItemStack(Material.RED_STAINED_GLASS_PANE), "§cErste-Hilfe von " + Script.getName(p) + " ablehnen."));
        tg.openInventory(inv);
        cooldown.put(p.getName(), System.currentTimeMillis()+1000*60*2);
        new BukkitRunnable() {
            @Override
            public void run() {
                p.sendMessage(ErsteHilfe.PREFIX + "Du kannst nun wieder Erste-Hilfe leisten.");
            }
        }.runTaskLater(main.getInstance(), 20L * 120);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§7Erste-Hilfe annehmen?")) {
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory())) {
                if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                    ItemStack is = e.getCurrentItem();
                    Player p = (Player) e.getWhoClicked();

                    e.setCancelled(true);
                    e.getView().close();

                    String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());

                    if (name.endsWith(" annehmen.")) {
                        p.sendMessage(ErsteHilfe.PREFIX + "Du hast die Erste-Hilfe angenommen.");
                        p.sendMessage(Messages.INFO + "Du bist nun eine Minute länger auf dem Friedhof.");
                        Player reviver = Script.getPlayer(name.split(" ")[2].replace("NRP × ", ""));
                        if (reviver != null) {
                            reviver.sendMessage(ErsteHilfe.PREFIX + "Du hast Erste-Hilfe bei " + Script.getName(p) + " geleistet.");
                            Script.addEXP(reviver, Script.getRandom(1, 3));
                            Me.sendMessage(reviver, "leistet Erste-Hilfe bei " + Script.getName(p) + ".");
                        }
                        Friedhof f = Friedhof.getDead(p);
                        f.setHelpCounter(f.getHelpCounter() + 1);

                        f.addDeathTime(p, 60);
                    } else if (name.endsWith(" ablehnen.")) {
                        p.sendMessage(ErsteHilfe.PREFIX + "Du hast die Erste-Hilfe abgelehnt.");
                    }
                }
            }
        }
    }
}
