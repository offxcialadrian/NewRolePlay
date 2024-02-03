package de.newrp.Commands;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class Test implements CommandExecutor, Listener {

    private static final boolean block = true;
    public static int smarktID;

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
