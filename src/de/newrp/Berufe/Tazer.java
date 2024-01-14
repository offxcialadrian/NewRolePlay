package de.newrp.Berufe;

import de.newrp.API.Achievement;
import de.newrp.API.Particle;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Chat.Me;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Tazer implements Listener {


    public static final HashMap<String, Double> cooldowns = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (p.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_HOE)) {
            if (canUse(p)) {
                Location loc = p.getEyeLocation();
                double maxLength = 7;
                Player p1 = null;
                f:
                for (double d = 0; d <= maxLength; d += 0.3) {
                    loc.add(loc.getDirection().multiply(.2D));
                    if (!loc.getBlock().getType().equals(Material.AIR) && !loc.getBlock().getType().equals(Material.GRASS) && !loc.getBlock().getType().equals(Material.TALL_GRASS))
                        break;

                    for (Entity ent : p.getWorld().getNearbyEntities(loc, .3D, .3D, .3D)) {
                        if (ent instanceof Player) {
                            Player pent = (Player) ent;
                            if (pent != p) {
                                p1 = pent;
                                break f;
                            }
                        }
                    }
                    new Particle(org.bukkit.Particle.FIREWORKS_SPARK, loc, true, 0.01F, 0.01F, 0.01F, 0.01F, 1).sendAll();
                }
                tazerHit(p1, p);
                cooldowns.put(p.getName(), 0.0);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (cooldowns.get(p.getName()) < 20) {
                            if (p.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_HOE)) {
                                progressBar(20.0, p);
                            }
                        } else {
                            cancel();
                            cooldowns.remove(p.getName());
                            if (p.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_HOE)) {
                                Script.sendActionBar(p, "§eRecharge... §8» §aComplete");
                            }
                        }
                    }
                }.runTaskTimer(main.getInstance(), 20, 20);
            }
            e.setCancelled(true);
        }
    }

    private static Boolean canUse(Player p) {
        if (cooldowns.isEmpty()) return true;
        return !cooldowns.containsKey(p.getName());
    }


    public static void tazerHit(Player p, Player cop) {
        if (p == null) return;
        if (SDuty.isSDuty(p)) return;

        if (p.getActivePotionEffects().stream().anyMatch(effect -> effect.getType() == PotionEffectType.SLOW && effect.getAmplifier() == 3)) {
            p.removePotionEffect(PotionEffectType.SLOW);
            p.removePotionEffect(PotionEffectType.JUMP);
            p.removePotionEffect(PotionEffectType.GLOWING);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
        }

        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 17 * 20, 3));
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 17 * 20, -5));
        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 8 * 20, 2));

        Achievement.TAZED.grant(p);
        p.sendMessage("§8[§eTazer§8] §e" + Script.getName(cop) + " hat dich getazert.");
        Me.sendMessage(p, "wurde von " + Script.getName(cop) + " getazert.");
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = cooldowns.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§a▉");
            } else {
                sb.append("§8▉");
            }
        }
        cooldowns.replace(p.getName(), cooldowns.get(p.getName()) + 1.0);
        Script.sendActionBar(p, "§eRecharge... §8» §a" + sb);
    }
}