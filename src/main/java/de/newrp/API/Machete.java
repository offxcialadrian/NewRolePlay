package de.newrp.API;

import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.SDuty;
import de.newrp.Chat.Me;
import de.newrp.Player.AFK;
import de.newrp.Police.Handschellen;
import de.newrp.Waffen.Waffen;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Random;

public class Machete implements Listener {
    final HashMap<String, Long> cooldown = new HashMap<>();

    public static ItemStack getItem() {
        return Script.setNameAndLore(Material.IRON_SWORD, "§7Machete", "§625/25");
    }

    @EventHandler
    public void onInteract(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if(e.isCancelled()) return;
            Player victim = (Player) e.getEntity();

            if (SDuty.isSDuty(victim) || BuildMode.isInBuildMode(victim) || AFK.isAFK(victim) || Script.getLevel(victim)==1 || Friedhof.isDead(victim)) {
                return;
            }

            Player damager = (Player) e.getDamager();
            if(damager.getLevel()==1) return;
            if (damager.getInventory().getItemInMainHand() != null && damager.getInventory().getItemInMainHand().hasItemMeta() && damager.getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null && damager.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§7Machete")) {
                long time = System.currentTimeMillis();
                Long lastUsage = cooldown.get(damager.getName());
                if (cooldown.containsKey(damager.getName())) {
                    if (lastUsage + 10 * 1000 > time) {
                        e.setDamage(0);
                        e.setCancelled(true);
                        return;
                    }
                }
                if (Handschellen.isCuffed(damager)) {
                    damager.sendMessage(Messages.ERROR + "§7Du bist gefesselt.");
                } else if (Sperre.WAFFENSPERRE.isActive(Script.getNRPID(damager))) {
                    damager.sendMessage(Messages.ERROR + "Du hast eine Waffensperre.");
                    damager.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                } else {
                    ItemStack is = damager.getInventory().getItemInMainHand();
                    int ammo = Waffen.getAmmo(is);
                    if(ammo == 0) ammo = 25;
                    if (ammo > 0) {
                        cooldown.put(damager.getName(), time);
                        if (Spawnschutz.isInSpawnschutz(victim)) return;
                        if (Script.getLevel(victim) > 1 && !SDuty.isSDuty(victim) && !AFK.isAFK(victim)) {
                            double dmg = Script.getRandom(9, 15);
                            victim.damage(dmg);
                            victim.setLastDamageCause(new EntityDamageEvent(damager, EntityDamageEvent.DamageCause.ENTITY_ATTACK, dmg));
                            if (new Random().nextInt(2) == 0) {
                                Health.setBleeding(victim);
                            }
                            if (new Random().nextInt(3) == 0) {
                                if (!Krankheit.GEBROCHENER_ARM.isInfected(Script.getNRPID(victim))) {
                                    Me.sendMessage(victim, "hat sich " + (Script.getGender(victim) == Gender.MALE ? "sein" : "ihr") + "en Arm gebrochen.");
                                    Krankheit.GEBROCHENER_ARM.add(Script.getNRPID(victim));
                                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1, false, false));
                                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1, false, false));
                                }
                            }
                            damager.getInventory().setItemInMainHand(Waffen.setAmmo(is, ammo - 1, 25));
                        }
                    } else {
                        damager.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        damager.playSound(damager.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (event.getItem().getType() == Material.IRON_SWORD) {
            event.setDamage(0);
            event.setCancelled(true);
        }
    }
}
