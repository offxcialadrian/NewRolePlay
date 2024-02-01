package de.newrp.Commands;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Player.Tragen;
import nl.sbdeveloper.vehiclesplus.VehiclesPlus;
import nl.sbdeveloper.vehiclesplus.api.garages.Garage;
import nl.sbdeveloper.vehiclesplus.api.vehicles.VehicleModel;
import nl.sbdeveloper.vehiclesplus.api.vehicles.impl.SpawnedVehicle;
import nl.sbdeveloper.vehiclesplus.api.vehicles.impl.StorageVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

public class Test implements CommandExecutor, Listener {

    private static final boolean block = true;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.OWNER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
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
        p.openInventory(inv);

        return false;
    }
}
