package de.newrp.Administrator;

import de.newrp.API.Script;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AntiCheatFly implements Listener {

    public static boolean activated = true;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        if (!activated) return;
        Player p = e.getPlayer();
        if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE)) return;
        if ((p.isSprinting() && (e.getTo().getY() - e.getFrom().getY()) > 0) || hasElytra(p)) return;
        if (p.hasPotionEffect(PotionEffectType.SPEED)) {
            int speed = 0;
            for (PotionEffect pe : p.getActivePotionEffects()) {
                if (pe.getType().equals(PotionEffectType.SPEED)) {
                    speed = speed + pe.getAmplifier() + 1;
                }
            }
            if (speed > 40) return;
        }
        double i = e.getTo().toVector().distance(e.getFrom().toVector());
        boolean sponge = !p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SPONGE);
        boolean allowFlight = p.getAllowFlight();
        boolean fallDistance = p.getFallDistance() < 4.0F;
        boolean blockIsAir = p.getLocation().getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR);
        boolean onGround = p.isOnGround();
        if (sponge) {
            if (p.getVehicle() != null || allowFlight) return;
            if ((fallDistance) && (blockIsAir) && i > 1.35D && !onGround) {
                //if ((fallDistance) && (blockIsAir) && i > 1.25D && !onGround) {
                AntiCheatSystem.warn(p, AntiCheatSystem.Cheat.FLY);
            }
        }
    }

    public boolean hasElytra(Player p) {
        for (ItemStack item : p.getInventory().getArmorContents())
            if (item != null && item.getType().name().contains("ELYTRA"))
                return true;
        return false;
    }

}
