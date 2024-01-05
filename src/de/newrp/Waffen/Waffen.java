package de.newrp.Waffen;

import de.newrp.API.*;
import de.newrp.Administrator.AimBot;
import de.newrp.Administrator.SDuty;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;

public class Waffen implements Listener {

    public static final HashMap<String, Long> REVIVE_COOLDOWN = new HashMap<>();
    public static final HashMap<String, Long> cooldown = new HashMap<>();
    public static int getAmmo(ItemStack is) {
        if (!is.hasItemMeta() || is.getItemMeta().getLore() == null) return 0;
        String s = is.getItemMeta().getLore().toString();
        s = s.split("/")[0];
        s = s.substring(3);
        if (!Script.isInt(s)) return 0;
        return Integer.parseInt(s);
    }

    public static int getAmmoTotal(ItemStack is) {
        if (!is.hasItemMeta()) return 0;
        if (is.getItemMeta().getLore() == null) return 0;
        String s = is.getItemMeta().getLore().toString();
        if (s.length() < 1) return 0;
        if (s.split("/")[1] == null) return 0;
        s = s.split("/")[1];
        s = s.substring(0, s.length() - 1);
        if (!Script.isInt(s)) return 0;
        return Integer.parseInt(s);
    }

    public static ItemStack setAmmo(ItemStack is, int ammo, int total) {
        ItemMeta meta = is.getItemMeta();
        meta.setLore(Collections.singletonList("§6" + ammo + "/" + total));
        is.setItemMeta(meta);
        is.setAmount(1);
        return is;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player p = e.getPlayer();
        if (p.isInsideVehicle()) return;

        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand == null) return;

        ItemMeta itemMeta = hand.getItemMeta();
        if (itemMeta == null) return;

        String name = itemMeta.getDisplayName();
        if (name == null) return;

        if (!name.startsWith("§7")) return;

        Weapon weapon = null;
        for (Weapon w1 : Weapon.values()) {
            if (hand.getType().equals(w1.getWeapon().getType())) {
                weapon = w1;
                break;
            }
        }

        if (weapon == null) return;

        if(Sperre.WAFFENSPERRE.isActive(Script.getNRPID(p))) {
            p.sendMessage(Messages.ERROR + "Du hast noch eine Waffensperre.");
            return;
        }

        if(SDuty.isSDuty(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Waffen benutzen, wenn du im Supporter-Dienst bist.");
            e.setCancelled(true);
            return;
        }



        Long globalCooldown = REVIVE_COOLDOWN.get(p.getName());
        if (globalCooldown != null && globalCooldown > System.currentTimeMillis()) {
            Script.sendActionBar(p, "§7§cDu fühlst dich noch zu schwach...");
            e.setCancelled(true);
            return;
        }


        if (!canUseOtherWeapon(p, weapon)) return;

        long time = System.currentTimeMillis();
        Long lastUsage = cooldown.get(p.getName() + "." + weapon.getName().toLowerCase());
        if (lastUsage != null && lastUsage + weapon.getCooldown() * 1000 > time) {
            Script.sendActionBar(p, "§7§oDu kannst die Waffe gerade nicht benutzen...");
            e.setCancelled(true);
            return;
        }

        fire(p, weapon, hand);
    }

    @EventHandler
    public void onArrowDamage(EntityDamageEvent e) {
        if (e.getEntity().getType().equals(EntityType.ARROW)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupArrowEvent e) {
        e.setCancelled(true);
    }

    public ItemStack reload(ItemStack is, Weapon w) {
        if (!is.hasItemMeta() || is.getItemMeta().getLore() == null) return is;
        if (getAmmo(is) == w.getMagazineSize()) return is;
        int total = getAmmoTotal(is);
        if (total == 0) return setAmmo(is, getAmmo(is), 0);
        int ammo = w.getMagazineSize() - getAmmo(is);
        if (total < ammo) ammo = total;
        return setAmmo(is, getAmmo(is) + ammo, total - ammo);
    }

    public void fire(Player p, Weapon w, ItemStack is) {
        int skill = (Krankheit.GEBROCHENES_BEIN.isInfected(Script.getNRPID(p)) ? 1 : 6);
        float recoil = w.getRecoil();

        recoil = recoil - recoil / (10 - skill);

        if (!p.isSprinting()) recoil = (recoil * .3F);

        if (p.getInventory().getItemInOffHand().getType().equals(Material.SHIELD) || p.getInventory().getItemInMainHand().getType().equals(Material.SHIELD)) {
            return;
        }
        int ammo = getAmmo(is);
        if (ammo <= 0) {
            p.playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);
            p.getInventory().setItemInMainHand(reload(is, w));
            cooldown.put(p.getName() + "." + w.getName().toLowerCase(), (long) (System.currentTimeMillis() + (w.getReload(skill) * 1000)));
            return;
        }

        cooldown.put(p.getName() + "." + w.getName().toLowerCase(), System.currentTimeMillis());
        p.getInventory().setItemInMainHand(setAmmo(is, ammo - 1, getAmmoTotal(is)));

        float v = 0;
        float v1 = 0;
        boolean cloud = false;
        switch (w) {
            case PISTOLE:
                v = 2.0F;
                v1 = .95F;
                break;
            /*case SCATTER3:
                v = 2.0F;
                v1 = 1.0F;
                break;*/
            case AK47:
                v = 2.5F;
                v1 = .85F;
                break;
            case MP7:
                v = 2.2F;
                v1 = .90F;
                break;
            /*case VIPER9:
                v = 10.0F;
                v1 = .1F;
                cloud = true;
                break;
            case EXTENSO18:
                v = 5.0F;
                v1 = .1F;
                cloud = true;
                break;
            case ALPHA7:
                Debug.debug("switch RPG7");
                RPG7.launch(p);
                return;*/
            default:
                Debug.debug("switch default");
                break;
        }


        Location ploc = p.getLocation();

        for (Entity ent : p.getNearbyEntities(30D, 20D, 30D)) {
            if (ent.getType().equals(EntityType.PLAYER)) {
                Player online = (Player) ent;
                online.playSound(ploc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, v, v1);
                online.playSound(ploc, Sound.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);
            }
        }

        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, v, v1);
        p.playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);

        Location loc = p.getEyeLocation();
        double maxLength = .5;
        for (double d = 0; d <= maxLength; d += 0.3) {
            loc.add(loc.getDirection().multiply(1D));
            new Particle(org.bukkit.Particle.SMOKE_NORMAL, loc, false, 0.001F, 0.001F, 0.1F, 0.001F, 1).sendAll();
            if (cloud) new Particle(org.bukkit.Particle.CLOUD, loc, false, 0.001F, 0.001F, 0.1F, 0.001F, 1).sendAll();
        }

        Location direction = p.getEyeLocation();
        direction.setYaw(direction.getYaw() + (Script.getRandom(1, 2) == 1 ? recoil : -recoil));
        direction.setPitch(direction.getPitch() + (Script.getRandom(1, 2) == 1 ? recoil : -recoil));
        Arrow a = p.launchProjectile(Arrow.class);
        if(!AimBot.aimbot.contains(p)) {
            a.setCustomName(w.getName());
            a.setGravity(false);
            a.setBounce(false);
            a.setInvulnerable(false);
            a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            a.setShooter(p);
            a.setVelocity(direction.getDirection().multiply(4));
        } else {
            a.setCustomName(w.getName());
            a.setGravity(false);
            a.setBounce(false);
            a.setInvulnerable(false);
            a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            a.setShooter(p);
            a.setVelocity(p.getLocation().toVector().subtract(a.getLocation().toVector()).normalize().multiply(2.0));
        }
    }

    public boolean canUseOtherWeapon(Player p, Weapon current) {
        long h = 0L;
        Weapon weapon = null;
        for (Weapon w : Weapon.values()) {
            Long l = cooldown.get(p.getName() + "." + w.getName().toLowerCase());
            if (l != null && l > h) {
                h = l;
                weapon = w;
            }
        }
        return h + 1250 <= System.currentTimeMillis() || current.equals(weapon);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Item i = e.getItemDrop();
        if (e.getItemDrop() == null) return;
        Weapon w = null;
        for (Weapon w1 : Weapon.values()) {
            if (i.getItemStack().getType().equals(w1.getWeapon().getType())) {
                w = w1;
                break;
            }
        }
        if (w != null) {
            e.getItemDrop().remove();
            Player p = e.getPlayer();
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            if (getAmmo(i.getItemStack()) != w.getMagazineSize()) {
                p.playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1F, .01F);
                p.playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1F, .05F);
                cooldown.put(p.getName() + "." + w.getName().toLowerCase(), (long) (System.currentTimeMillis() + (w.getReload(6) * 1000)));
            }
            p.getInventory().setItemInMainHand(reload(i.getItemStack(), w));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        long c = System.currentTimeMillis();
        for (Weapon w : Weapon.values()) {
            if (cooldown.containsKey(p.getName() + "." + w.getName().toLowerCase())) {
                long t = cooldown.get(p.getName() + "." + w.getName().toLowerCase());
                if (t < c) {
                    cooldown.remove(p.getName() + "." + w.getName().toLowerCase());
                }
            }
        }
    }
}

