package de.newrp.API;

import de.newrp.Waffen.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class SniperZoom implements Listener {
    public final ArrayList<Player> zoom = new ArrayList<>();

    @EventHandler
    public void onZoom(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR) {
            Player p = e.getPlayer();
            if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType().equals(Weapon.SNIPER.getWeapon().getType())) {
                if (zoom.contains(p)) {
                    zoom.remove(p);
                    p.removePotionEffect(PotionEffectType.SLOW);
                } else {
                    if (p.isSneaking()) {
                        boolean blocked = false;
                        for (PotionEffect pe : p.getActivePotionEffects()) {
                            if (pe.getType().equals(PotionEffectType.SLOW)) {
                                blocked = true;
                                break;
                            }
                        }
                        if (blocked) {
                            p.sendMessage(Messages.ERROR + "Du kannst derzeit keinen Zoom benutzen.");
                        } else {
                            zoom.add(p);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100, false, false));
                        }
                    }
                }
            }
        }
    }
}
