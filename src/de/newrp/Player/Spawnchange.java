package de.newrp.Player;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.House.House;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class Spawnchange implements CommandExecutor, Listener {

    public static String PREFIX = "§8[§6Spawn§8] §6» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du benötigst Premium, um deinen Spawnpoint zu ändern.");
            return true;
        }

        p.sendMessage(PREFIX + "Dein aktuelles Spawnpoint ist §6" + getSpawnName(p) + "§7.");

        Inventory inv = Bukkit.createInventory(null, 9, "§6Spawnpoint auswählen");
        inv.addItem(new ItemBuilder(Material.PAPER).setName("§6Krankenhaus").build());
        inv.addItem(new ItemBuilder(Material.PAPER).setName("§6Stadthalle").build());
        if(Beruf.hasBeruf(p)) inv.addItem(new ItemBuilder(Material.PAPER).setName("§6HQ").build());
        for(House h : House.getHouses(Script.getNRPID(p))) {
            inv.addItem(new ItemBuilder(Material.PAPER).setName("§6Haus " + h.getID()).build());
        }
        p.openInventory(inv);

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(e.getView().getTitle().equals("§6Spawnpoint auswählen")) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null) return;
            if(e.getCurrentItem().getType() == Material.AIR) return;
            Player p = (Player) e.getWhoClicked();
            assert e.getCurrentItem() != null;
            assert e.getCurrentItem().getItemMeta() != null;
            switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                case "§6Krankenhaus":
                    p.sendMessage(PREFIX + "Du hast deinen Spawnpoint auf das Krankenhaus gesetzt.");
                    Script.setString(p, "spawnpoint", "name", "Krankenhaus");
                    Script.setString(p, "spawnpoint", "loc", "278/75/1232/270.05463f/0.84689033f");
                    p.closeInventory();
                    break;
                case "§6Stadthalle":
                    p.sendMessage(PREFIX + "Du hast deinen Spawnpoint auf die Stadthalle gesetzt.");
                    Script.setString(p, "spawnpoint", "name", "Stadthalle");
                    Script.setString(p, "spawnpoint", "loc", "581/69/992/-269.48206f/-3.5868206f)");
                    p.closeInventory();
                    break;
                case "§6HQ":
                    p.sendMessage(PREFIX + "Du hast deinen Spawnpoint auf das HQ gesetzt.");
                    Location loc = Beruf.getBeruf(p).getLoc();
                    Script.setString(p, "spawnpoint", "name", "HQ");
                    Script.setString(p, "spawnpoint", "loc", loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ() + "/" + loc.getYaw() + "/" + loc.getPitch());
                    p.closeInventory();
                    break;
                default:
                    if(e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§6Haus")) {
                        int id = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1]);
                        House h = House.getHouseByID(id);
                        if(h == null) {
                            p.sendMessage(Messages.ERROR + "Das Haus existiert nicht.");
                            return;
                        }

                        Location loc1 = h.getSignLocation();
                        Script.setString(p, "spawnpoint", "name", "Haus " + id);
                        Script.setString(p, "spawnpoint", "loc", loc1.getBlockX() + "/" + loc1.getBlockY() + "/" + loc1.getBlockZ() + "/" + loc1.getYaw() + "/" + loc1.getPitch());
                        p.sendMessage(PREFIX + "Du hast deinen Spawnpoint auf das Haus " + id + " gesetzt.");
                        p.closeInventory();
                    }
                    break;

            }
        }
    }

    public static String getSpawnName(Player p) {
        return Script.getString(p, "spawnpoint", "name");
    }

    public static Location getSpawnLoc(Player p) {
        if(Script.getString(p, "spawnpoint", "loc") == null) return null;
        String[] loc = Script.getString(p, "spawnpoint", "loc").split("/");
        return new Location(Bukkit.getWorld("world"), Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Float.parseFloat(loc[3]), Float.parseFloat(loc[4]));
    }

}
