package de.newrp.Waffen;

import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import de.newrp.features.deathmatcharena.data.DeathmatchArenaStats;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class WaffenDamage implements Listener {

    private final IDeathmatchArenaService deathmatchArenaService = DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class);


    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.ARROW || e.getEntity().getType() != EntityType.PLAYER) return;

        Arrow arrow = (Arrow) e.getDamager();
        if (arrow.getCustomName() == null) return;

        Weapon w = Script.getWeapon(arrow.getCustomName());
        if (w == null) return;

        Player p = (Player) e.getEntity();
        ItemStack chestplate = p.getInventory().getChestplate();
        if(deathmatchArenaService.isInDeathmatch(p, false)) {
            final DeathmatchArenaStats stats = this.deathmatchArenaService.getStats(p);
            stats.shotsHit(stats.shotsHit() + 1);
        }
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
            e.setDamage(isHeadshot(arrow, p) ? w.getDamage() + 2D : w.getDamage());
            if (Script.getRandom(1, 100) == 2 && !deathmatchArenaService.isInDeathmatch(p, false)) {
                Health.setBleeding(p);
            }
        } else {
            if (!(p.getInventory().getItemInMainHand().getType().equals(Material.SHIELD))) {
                if (e.getDamage() > 0 && !AFK.isAFK(p)) {
                    double dmg = w.getArmorPenetration();
                    chestplate.setDurability((short) Math.max(30, chestplate.getDurability() + dmg));
                }

                e.setDamage(w.getDamage() / 4);
            }
        }

    }

    public boolean isHeadshot(Arrow a, Player p) {
        if(a.getLocation().getY() - p.getLocation().getY() > 1.35D) Debug.debug("Headshot");
        return a.getLocation().getY() - p.getLocation().getY() > 1.35D;
    }
}