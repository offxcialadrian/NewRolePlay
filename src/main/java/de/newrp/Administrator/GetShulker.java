package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GetShulker implements CommandExecutor, Listener {


    private static ArrayList<Player> getShulker = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(!BuildMode.isInBuildMode(p) || !Script.isInTestMode()) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 0) {
            p.sendMessage(Messages.ERROR + "/getshulker");
            return true;
        }

        if(getShulker.contains(p)) {
            getShulker.remove(p);
            p.sendMessage(Script.PREFIX + "§7Du kannst nun keine Shulker mehr bekommen.");
            return true;
        }

        getShulker.add(p);
        p.sendMessage(Script.PREFIX + "§7Du kannst nun Shulker bekommen.");

        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!getShulker.contains(p)) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(!(e.getClickedBlock().getState() instanceof ShulkerBox)) return;
        ShulkerBox box = (ShulkerBox) e.getClickedBlock().getState();
        for(ItemStack item : box.getInventory().getContents()) {
            if(item == null) continue;
            p.getInventory().addItem(item);
        }
        getShulker.remove(p);
        p.sendMessage(Script.PREFIX + "§7Du hast den Shulker erhalten.");
    }

}
