package de.newrp.API;

import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

public class Hologram {

    public static final ArrayList<Hologram> HOLOGRAMS = new ArrayList<>();

    private final Location loc;
    private final String name;
    private final ArmorStand stand;

    public Hologram(Location loc, String text) {
        loc.getChunk().load();
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0, -.2D, 0), EntityType.ARMOR_STAND);
        stand.setCustomName(text);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setVisible(false);

        this.loc = loc;
        this.name = text;
        this.stand = stand;
    }

    public static void reload() {
        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), Hologram::respawn, 2 * 20L);
    }

    public static void respawn() {
        clear();
        create();
    }

    public static void create() {
        Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> {
            for (HologramList h : HologramList.values()) {
                h.getLocation().getChunk().load();
                Hologram.HOLOGRAMS.add(new Hologram(h.getLocation(), h.getName()));
            }
        });
    }

    public static void clear() {
        for (Hologram h : HOLOGRAMS) {
            ArmorStand stand = h.getArmorStand();
            stand.remove();
        }
        for (ArmorStand ent : Script.WORLD.getEntitiesByClass(ArmorStand.class)) {

            if (ent.getPassengers().isEmpty()) {
                ent.remove();
            }
        }
        HOLOGRAMS.clear();
    }

    public Location getLocation() {
        return this.loc;
    }

    public String getText() {
        return this.name;
    }

    public ArmorStand getArmorStand() {
        return this.stand;
    }
}
