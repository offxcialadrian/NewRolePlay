package de.newrp.Commands;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.GFB.Tabakplantage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

public class Test implements CommandExecutor, Listener {

    private static final boolean block = true;
    public static int smarktID;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        Tabakplantage.respawnPlantage(new Location(Script.WORLD, 105, 65.0, 625), new Location(Script.WORLD, 118, 67, 656));

        /*

        if(args.length == 2 && args[0].equalsIgnoreCase("drink")) {
            if(!Script.isInt(args[1])) {
                p.sendMessage(Messages.ERROR + "Bitte gib eine Zahl an.");
                return true;
            }

            int amount = Integer.parseInt(args[1]);
            if(amount < 1) {
                p.sendMessage(Messages.ERROR + "Die Anzahl muss größer als 0 sein.");
                return true;
            }

            new ItemBuilder(Material.POTION).setName("§7Trinkwasser").setAmount(amount).build();

            return true;
        }

        Block b = p.getLocation().getBlock();
        Slab slab = (Slab) b.getBlockData();

        Slab.Type type = slab.getType();

        Vector direction = p.getLocation().getDirection().normalize();
        Debug.debug("Direction: " + direction.toString());

// Berechne die Koordinaten des Blocks hinter dem gegebenen Block basierend auf der Blickrichtung des Spielers
        int x = (int) Math.round(b.getX() + direction.getX());
        int y = (int) Math.round(b.getY() + direction.getY());
        int z = (int) Math.round(b.getZ() + direction.getZ());

        Location loc = new Location(Script.WORLD, x, y, z);

        Debug.debug("X: " + b.getX() + " Y: " + b.getY() + " Z: " + b.getZ());
        Debug.debug("X: " + x + " Y: " + y + " Z: " + z);




        // Gib den Block an den berechneten Koordinaten zurück


        try {
            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("player").addEntry(p.getName());
        } catch (Exception e) {
            Debug.debug("Error: " + e.getMessage());
            e.printStackTrace();
        }

        for(String entries : ScoreboardManager.MAIN.getTeam("player").getEntries()) {
            Debug.debug(entries);
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/testinv [Anzahl an Reihen]");
            return true;
        }

        int rows = Integer.parseInt(args[0]);
        if(rows < 1 || rows > 6) {
            p.sendMessage(Messages.ERROR + "Die Anzahl an Reihen muss zwischen 1 und 6 liegen.");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, rows * 9, "§8» §eTest");
        for(int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName("§8» §r" + i).setAmount(i).build());
        }
        p.openInventory(inv);*/

        return false;
    }



}
