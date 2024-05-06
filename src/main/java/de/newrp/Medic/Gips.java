package de.newrp.Medic;

import de.newrp.API.*;
import de.newrp.GFB.Lagerarbeiter;
import de.newrp.NewRoleplayMain;
import de.newrp.Chat.Me;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Gips implements Listener {

    public static HashMap<Player, Integer> hits = new HashMap<>();

    private static final Map<String, Long> BANDAGE_COOLDOWN = new HashMap<>();
    private static final Map<String, Long> LAST_CLICK = new HashMap<>();
    private static final Map<String, Integer> LEVEL = new HashMap<>();


    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!(e.getRightClicked() instanceof Player)) return;

        Player p = e.getPlayer();
        if (!interact(p)) return;

        long time = System.currentTimeMillis();
        Player rightClicked = (Player) e.getRightClicked();

        Long lastUsage = BANDAGE_COOLDOWN.get(rightClicked.getName());
        if (lastUsage != null && lastUsage + TimeUnit.MINUTES.toMillis(9) > time) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat bereits einen Gips");
            return;
        }

        if (!Krankheit.GEBROCHENER_ARM.isInfected(Script.getNRPID(rightClicked)) && !Krankheit.GEBROCHENES_BEIN.isInfected(Script.getNRPID(rightClicked))) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat keine gebrochenen Knochen");
            return;
        }

        Long lastClick = LAST_CLICK.get(p.getName());
        if (lastClick == null) {
            LAST_CLICK.put(p.getName(), time);
            return;
        }

        long difference = time - lastClick;
        if (difference >= 500) LEVEL.remove(p.getName());

        int level = LEVEL.computeIfAbsent(p.getName(), k -> 0);

        LAST_CLICK.put(p.getName(), time);
        LEVEL.put(p.getName(), level + 1);
        progressBar(30, p);

        if (level >= 30) {
            PlayerInventory inv = p.getInventory();
            ItemStack is = inv.getItemInMainHand();
            if (is.getAmount() > 1) {
                is.setAmount(is.getAmount() - 1);
            } else {
                inv.setItemInMainHand(new ItemStack(Material.AIR));
            }

            Me.sendMessage(p, "legt " + Script.getName(rightClicked) + " einen Gips an.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    Krankheit.GEBROCHENER_ARM.remove(Script.getNRPID(rightClicked));
                    Krankheit.GEBROCHENES_BEIN.remove(Script.getNRPID(rightClicked));
                    rightClicked.setWalkSpeed(0.2F);
                    for (PotionEffect e : rightClicked.getActivePotionEffects()) {
                        rightClicked.removePotionEffect(e.getType());
                    }
                }
            }.runTaskLater(NewRoleplayMain.getInstance(), 20L * 10);

            BANDAGE_COOLDOWN.put(rightClicked.getName(), time);
            LAST_CLICK.remove(p.getName());
            LEVEL.remove(p.getName());
        }
    }

    public boolean interact(Player p) {
        if (p.getInventory().getItemInMainHand() == null) return false;

        ItemStack is = p.getInventory().getItemInMainHand();
        return is.hasItemMeta() && is.getItemMeta().getDisplayName() != null && is.getItemMeta().getDisplayName().equals("§7Gips");
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = LEVEL.get(p.getName());
        double progress_percentage = current_progress / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 10;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                sb.append("§c▉");
            } else {
                sb.append("§8▉");
            }
        }
        Script.sendActionBar(p, "§cGips anlegen.. §8» §a" + sb.toString());
    }


    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (e.isCancelled()) return;
        if (Friedhof.isDead((Player) e.getEntity())) return;
        Player p = (Player) e.getEntity();
        if (e.getDamage() > 15) {
            Krankheit.GEBROCHENES_BEIN.add(Script.getNRPID(p));
            Me.sendMessage(p, (Script.getGender(p) == Gender.MALE ? "sein" : "ihr") + " Bein hat geknackt.");
            p.setWalkSpeed(0.1F);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1, false, false));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (Krankheit.GEBROCHENES_BEIN.isInfected(Script.getNRPID(p)) && !p.hasPotionEffect(PotionEffectType.HEAL)) {
            p.setWalkSpeed(0.1F);
        }
    }

    @EventHandler
    public void onJump(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        /*if (SDuty.isSDuty(p)) return;
        if (BuildMode.isInBuildMode(p)) return;
        if (e.getFrom().getY() < e.getTo().getY()) {
            if (Krankheit.GEBROCHENES_BEIN.isInfected(Script.getNRPID(p))) {
                p.damage(1D);
                //p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1, false, false));
            }
        }*/
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent e) {
        Player p = e.getPlayer();
        /*if (e.isSprinting()) {
            if (SDuty.isSDuty(p)) return;
            if (BuildMode.isInBuildMode(p)) return;
            if (Krankheit.GEBROCHENES_BEIN.isInfected(Script.getNRPID(p))) {
                p.damage(1D);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1, false, false));
            }
        }*/
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;
        EntityDamageEvent.DamageCause cause = e.getCause();

        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)
            return;

        Player p = (Player) e.getEntity();
        Player d = (Player) e.getDamager();


        if (Krankheit.GEBROCHENER_ARM.isInfected(Script.getNRPID(d))) {
            d.damage(4D);
            Script.sendActionBar(d, Messages.INFO + "Du hast Schaden erlitten, da du mit einem gebrochenen Arm schlägst.");
            d.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1, false, false));
            d.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 1, false, false));
        }

        if (!hits.containsKey(d)) {
            hits.put(d, 1);
            new BukkitRunnable() {
                @Override
                public void run() {
                    hits.remove(d);
                }
            }.runTaskLater(NewRoleplayMain.getInstance(), 20 * 5);
        } else {
            hits.put(d, hits.get(d) + 1);
        }

        if (hits.get(d) < 5) return;

        if (Script.getRandom(1, 100) <= Health.getMuscleLevel(Script.getNRPID(d)) / 2) {
            if (!Krankheit.GEBROCHENER_ARM.isInfected(Script.getNRPID(d))) {
                Me.sendMessage(d, (Script.getGender(d) == Gender.MALE ? "sein" : "ihr") + " Arm hat geknackt.");
                Krankheit.GEBROCHENER_ARM.add(Script.getNRPID(d));
                d.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1, false, false));
                d.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1, false, false));
            }
        }

        if (Script.getRandom(1, 100) <= Health.getMuscleLevel(Script.getNRPID(d))) {
            if (!Krankheit.GEBROCHENER_ARM.isInfected(Script.getNRPID(d))) {
                Me.sendMessage(p, (Script.getGender(p) == Gender.MALE ? "sein" : "ihr") + " Arm hat geknackt.");
                Krankheit.GEBROCHENER_ARM.add(Script.getNRPID(p));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1, false, false));
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (Krankheit.GEBROCHENER_ARM.isInfected(Script.getNRPID(p))) {
            p.sendMessage(Messages.INFO + "Du hast Schaden erlitten, da du mit einem gebrochenen Arm etwas konsumierst.");
            p.damage(4D);
        }

        for (Krankheit krankheit : Krankheit.getAllKrankheiten(Script.getNRPID(p))) {
            if (krankheit.isFoodIntolerance()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10 * 30, 1, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 48 * 30, 2, false, false));
                p.sendMessage(Health.PREFIX + "§7Dir gehts nicht so gut...");
                Me.sendMessage(p, "hat sich übergeben.");
                new Particle(org.bukkit.Particle.SLIME, p.getLocation(), true, 0.5F, 0.5F, 0.5F, 0.5F, 10).sendAll();
                break;
            }
        }
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        if (Lagerarbeiter.ON_JOB.containsKey(p.getName())) {
            e.setCancelled(true);
            p.sendMessage(Messages.INFO + "Du kannst während dieses Jobs nichts trinken.");
            return;
        }

        if (!Premium.hasPremium(p)) {
            if (e.getItem().getType() == Material.COOKED_BEEF || e.getItem().getType() == Material.BAKED_POTATO || e.getItem().getType() == Material.COOKED_CHICKEN) {
                Health.FAT.add(Script.getNRPID(p), Script.getRandomFloat(2F, 4F));
            }
        }

        if (e.getItem().getType().equals(Material.POTION)) {
            p.getInventory().setItemInMainHand(Script.Pfandflasche());
            Health.THIRST.add(Script.getNRPID(p), Script.getRandomFloat(2.5F, 3F));
            p.removePotionEffect(PotionEffectType.WITHER);
            boolean b = false;
            if (e.getItem().hasItemMeta() && e.getItem().getItemMeta().getDisplayName() != null) {
                for (Drink d : Drink.values()) {
                    if (ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()).equalsIgnoreCase(d.getName())) {
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                Krankheit.CHOLERA.add(Script.getNRPID(p));
                p.sendMessage(Messages.INFO + "Du hast zuviel verunreinigtes Wasser getrunken.");
            }
        }
    }

}
