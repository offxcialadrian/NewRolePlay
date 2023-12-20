package de.newrp.Waffen;

import de.newrp.API.Script;
import de.newrp.Player.AFK;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class WaffenDamage implements Listener {
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.ARROW || e.getEntity().getType() != EntityType.PLAYER) return;

        Arrow arrow = (Arrow) e.getDamager();
        if (arrow.getCustomName() == null) return;

        Weapon w = Script.getWeapon(arrow.getCustomName());
        if (w == null) return;

        Player p = (Player) e.getEntity();
        ItemStack chestplate = p.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
            e.setDamage(isHeadshot(arrow, p) ? w.getDamage() + 2D : w.getDamage());
        } else {
            if (!(p.getInventory().getItemInMainHand().getType().equals(Material.SHIELD))) {
                if (e.getDamage() > 0 && !AFK.isAFK(p)) {
                    double dmg = w.getArmorPenetration();
                    chestplate.setDurability((short) Math.max(30, chestplate.getDurability() + dmg));
                }

                e.setDamage(w.getDamage() / 4);
            }
        }

        if (arrow.getShooter() != null) {
            Player p1 = (Player) arrow.getShooter();
        }
    }

    public boolean isHeadshot(Arrow a, Player p) {
        return a.getLocation().getY() - p.getLocation().getY() > 1.35D;
    }
}