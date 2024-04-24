package de.newrp.Entertainment;

import de.newrp.API.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Shisha implements Listener {

    public static final String PREFIX = "§8[§6Shisha§8]§e ";
    public final HashMap<String, Long> EXHALE = new HashMap<>();

    public static ShishaType getShishaType(String s) {
        for (ShishaType t : ShishaType.values()) {
            if (ChatColor.stripColor(s).equalsIgnoreCase(t.getName())) return t;
        }
        return null;
    }

    public static ItemStack setUses(Player p, ItemStack is) {
        if (!is.hasItemMeta()) return is;
        if (is.getItemMeta().getLore() == null) return is;
        boolean b = true;
        for (ShishaType type : ShishaType.values()) {
            if (is.getItemMeta().getLore().get(0) != null) {
                if (ChatColor.stripColor(is.getItemMeta().getLore().get(0)).equalsIgnoreCase(type.getName())) b = false;
            }
        }
        if (b) return is;
        String s = is.getItemMeta().getLore().get(1);
        s = ChatColor.stripColor(s);
        int duration;
        String first = s.split("/")[0];
        int i = Integer.parseInt(first) - 1;
        if (i <= 0) {
            p.sendMessage(PREFIX + "Die Shisha ist aufgeraucht.");
            return new ItemStack(Material.AIR);
        } else {
            duration = Integer.parseInt(s.split("/")[1]);
            return Script.setNameAndLore(is.getType(), "§6Shisha", is.getItemMeta().getLore().get(0), "§7" + i + "/" + duration);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getHand().equals(EquipmentSlot.OFF_HAND)) return;
            Player p = e.getPlayer();
            if (e.getClickedBlock().getType().equals(Material.BREWING_STAND)) {

                p.getInventory().getItemInMainHand();
                if (!p.getInventory().getItemInMainHand().getType().equals(Material.STICK))
                    return;

                if (Chair.sitsOnChair(p)) {
                    if (getUses(p.getInventory().getItemInMainHand()) > 0) {
                        if (new CooldownAPI(p, CooldownAPI.CooldownTime.MEDIUM, false).checkInput(Shisha.class)) {
                            if (!EXHALE.containsKey(p.getName())) {
                                EXHALE.put(p.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5));
                                p.sendMessage(PREFIX + "Du hast genug an der Shisha gezogen. Mit Linksklick kannst du den Rauch ausatmen.");
                            }
                        }
                    } else {
                        p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        p.sendMessage("§7Du hast die Shisha zu ende geraucht.");
                    }
                }
            }
        } else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            if (Chair.sitsOnChair(p)) {
                if (getUses(p.getInventory().getItemInMainHand()) > 0) {
                    if (EXHALE.containsKey(p.getName()) && EXHALE.get(p.getName()) > System.currentTimeMillis()) {
                        ShishaType type = getType(p.getInventory().getItemInMainHand());
                        if (type == null) return;
                        Location loc = p.getEyeLocation();
                        double maxLength = 4D;

                        float offset = type.getSmoke();
                        Particle particle = new Particle(org.bukkit.Particle.CLOUD, loc, false, offset, offset, offset, 0.01F, (int) maxLength);

                        for (double d = 0; d <= maxLength; d += 0.1) {
                            loc.add(loc.getDirection().multiply(.1D));

                            particle.setLocation(loc);
                            particle.sendAll();
                        }
                        Achievement.SHISHA.grant(p);
                        p.getInventory().setItemInMainHand(setUses(p, p.getInventory().getItemInMainHand()));
                        EXHALE.remove(p.getName());
                    }
                }
            }
        }
    }

    public int getUses(ItemStack is) {
        if (!is.getType().equals(Material.STICK)) return 0;
        if (!is.hasItemMeta()) return 0;
        if (is.getItemMeta().getLore() == null) return 0;

        String lore = ChatColor.stripColor(is.getItemMeta().getLore().get(1));
        int i = Integer.parseInt(lore.split("/")[0]);
        return (Math.max(i, 0));
    }

    public ShishaType getType(ItemStack is) {
        if (!is.hasItemMeta()) return null;
        String s = is.getItemMeta().getLore().get(0);
        return getShishaType(ChatColor.stripColor(s));
    }
}