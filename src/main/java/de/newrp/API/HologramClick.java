package de.newrp.API;

import de.newrp.Administrator.Notifications;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class HologramClick implements Listener {

    @EventHandler
    public void onRename(PlayerInteractEntityEvent e) {
        ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
        ItemStack is1 = e.getPlayer().getInventory().getItemInOffHand();
        if (is != null) {
            if (is.getType().equals(Material.NAME_TAG)) {
                e.setCancelled(true);
                return;
            }
        }
        if (is1 != null) {
            if (is1.getType().equals(Material.NAME_TAG)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTouch(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
            e.setCancelled(true);
            ArmorStand hologram = (ArmorStand) e.getRightClicked();
            String name = hologram.getCustomName();
            if (name == null) return;
            name = ChatColor.stripColor(name);
            if (!name.startsWith("/")) return;
            Notifications.sendMessage(Notifications.NotificationType.COMMAND, "§e" + Script.getName(e.getPlayer()) + " §7hat den Befehl §e" + name + " §7ausgeführt.");
            String cmd = name.replace("/", "");
            Log.COMMAND.write(e.getPlayer(), "/" + cmd);
            e.getPlayer().performCommand(cmd);
        }
    }
}
