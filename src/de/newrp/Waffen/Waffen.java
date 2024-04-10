package de.newrp.Waffen;

import de.newrp.API.*;
import de.newrp.Administrator.AimBot;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Government.Stadtkasse;
import de.newrp.Organisationen.Bankautomaten;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.AFK;
import de.newrp.Player.Fesseln;
import de.newrp.Police.Handschellen;
import de.newrp.Shop.Shop;
import de.newrp.Shop.Shops;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Waffen implements Listener {

    public static final HashMap<String, Long> REVIVE_COOLDOWN = new HashMap<>();
    public static final ConcurrentHashMap<String, Long> cooldown = new ConcurrentHashMap<>();
    public static final HashMap<Organisation, Long> robcooldown = new HashMap<>();
    public static String PREFIX = "§8[§6Waffe§8] §6" + Messages.ARROW + " §7";
    public static HashMap<String, Integer> progress = new HashMap<>();

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

        if(Fesseln.isTiedUp(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Waffen benutzen, wenn du gefesselt bist.");
            e.setCancelled(true);
            return;
        }

        if(SDuty.isSDuty(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Waffen benutzen, wenn du im Supporter-Dienst bist.");
            e.setCancelled(true);
            return;
        }

        if(Handschellen.isCuffed(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Waffen benutzen, wenn du gefesselt bist.");
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

        if(!GangwarCommand.isInGangwar(p)) {

            Shops shop = Shops.getShopByLocation(p.getLocation());
            if (shop != null) {
                if (!Organisation.hasOrganisation(p)) return;
                Organisation org = Organisation.getOrganisation(p);
                if (robcooldown.containsKey(org) && robcooldown.get(org) > System.currentTimeMillis()) {
                    Script.sendActionBar(p, Messages.ERROR + "Du kannst nicht so schnell hintereinander einen Shop überfallen (" + Script.getRemainingTime(robcooldown.get(org)) + ")");
                    return;
                }

                List<Player> cops = Beruf.Berufe.POLICE.getMembers().stream()
                        .filter(Beruf::hasBeruf)
                        .filter(nearbyPlayer -> Beruf.getBeruf(nearbyPlayer).equals(Beruf.Berufe.POLICE))
                        .filter(Duty::isInDuty)
                        .filter(nearbyPlayer -> !SDuty.isSDuty(nearbyPlayer))
                        .filter(nearbyPlayer -> !AFK.isAFK(nearbyPlayer)).collect(Collectors.toList());

                if (cops.size() < 3 && !Script.isInTestMode()) {
                    p.sendMessage(Messages.ERROR + "Es braucht mindestens 3 Beamte um einen Shop zu überfallen.");
                    return;
                }

                robcooldown.put(org, System.currentTimeMillis() + 10800000);
                p.getInventory().remove(Material.TNT);
                p.sendMessage(PREFIX + "Der Shop ist in 360 Sekunden überfallen.");
                Beruf.Berufe.POLICE.sendMessage(PREFIX + "ACHTUNG! ES WURDE EIN STILLER ALARM IM SHOP " + shop.getPublicName() + " AUSGELÖST!");
                Beruf.Berufe.POLICE.sendMessage(Messages.INFO + "In der Nähe von " + Navi.getNextNaviLocation(p.getLocation()).getName());
                progress.put(p.getName(), 0);
                Location loc = shop.getBuyLocation();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (p.getLocation().distance(loc) > 10) {
                            p.sendMessage(PREFIX + "Du bist zu weit entfernt.");
                            cancel();
                            return;
                        }

                        if (progress.get(p.getName()) >= 360) {
                            int remove = (int) Script.getPercent(20, shop.getKasse());
                            shop.removeKasse(remove);
                            org.sendMessage(PREFIX + Script.getName(p) + " hat einen Shop überfallen und " + remove + "€ gestohlen.");
                            Beruf.Berufe.POLICE.sendMessage(PREFIX + "Der Shop " + shop.getName() + " wurde überfallen. Es wurden " + remove + "€ gestohlen.");
                            Script.addMoney(p, PaymentType.CASH, remove);
                            org.addExp(remove / 50);
                            Bankautomaten.win.put(p, remove);
                            progress.remove(p.getName());
                            cancel();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Bankautomaten.win.remove(p);
                                }
                            }.runTaskLater(main.getInstance(), 20L * 60 * 15);
                        } else {
                            progressBar(360, p);
                            progress.replace(p.getName(), progress.get(p.getName()) + 1);
                        }
                    }
                }.runTaskTimer(main.getInstance(), 20L, 20L);

            }
        }
    }

    private static void progressBar(double required_progress, Player p) {
        double current_progress = progress.get(p.getName());
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
        Script.sendActionBar(p, "§cÜberfall.. §8» §a" + sb.toString());
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

        Script.sendActionBar(p, Messages.INFO + "Du hast noch §b" + ammo + "§8/§b" + getAmmoTotal(is) + " §rSchuss.");

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
            case DESERT_EAGLE:
                v = 2.0F;
                v1 = .1F;
                break;
            case AK47:
                v = 2.5F;
                v1 = .85F;
                break;
            case MP7:
                v = 2.2F;
                v1 = .90F;
                break;
            case SNIPER:
                v = 10.0F;
                v1 = .1F;
                cloud = true;
                break;
            case JAGDFLINTE:
                v = 5.0F;
                v1 = .1F;
                cloud = true;
                break;
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
        Vector knockback = loc.getDirection().multiply(w.getKnockback());

        // Apply knockback to the player
        if(p.getFallDistance() == 0 && w.getKnockback()<0) p.setVelocity(knockback);

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
        a.setCustomName(w.getName());
        a.setGravity(false);
        a.setBounce(false);
        a.setInvulnerable(false);
        a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        a.setShooter(p);
        a.setVelocity(direction.getDirection().multiply(4));
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
        int slot = e.getPlayer().getInventory().getHeldItemSlot();
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
            p.getInventory().setItem(slot, reload(i.getItemStack(), w));
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

