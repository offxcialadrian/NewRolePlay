package de.newrp.API;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class WingsuitListener implements Listener {
    final ArrayList<Player> falling = new ArrayList<>();

    public boolean canOpen(Player p) {
        return (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) && (!p.isFlying()) && p.getWorld().getBlockAt(p.getLocation()).getRelative(BlockFace.DOWN).getType().equals(Material.AIR) && p.getWorld().getBlockAt(p.getLocation()).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType().equals(Material.AIR);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType().equals(Script.fallschirm().getType())) {
            if (!falling.contains(p)) {
                if (Script.isFalling(e)) {
                    falling.add(p);
                }
            } else {
                if (!p.getWorld().getBlockAt(p.getLocation()).getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                    destroyWingsuit(p);
                }
            }
        }
    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
            Player p = (Player) e.getEntity();
            if (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType().equals(Script.fallschirm().getType())) {
                p.getInventory().getChestplate().setType(Material.AIR);
                p.sendMessage("§8[§bFallschirm§8] §3Dein Fallschirm wurde zerschossen.");
            }
        }
    }

    @EventHandler
    public void onUnEquip(InventoryClickEvent e) {
        if (e.getRawSlot() == 6) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                if (e.getCurrentItem().getType().equals(Material.ELYTRA)) {
                    e.setCancelled(true);
                    e.getView().close();
                    Player p = (Player) e.getWhoClicked();
                    if (falling.contains(p)) {
                        destroyWingsuit(p);
                    }
                }
            }
        }
    }

    private void destroyWingsuit(Player p) {
        p.sendMessage("§8[§bFallschirm§8] §3Dein Fallschirm ist nun verbraucht.");
        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 1));
        falling.remove(p);
    }
}
