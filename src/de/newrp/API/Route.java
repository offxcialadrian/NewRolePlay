package de.newrp.API;


import de.newrp.API.ParticleManager.ParticleWrapper;
import de.newrp.main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

public class Route {

    public static final HashMap<String, Route> ROUTES = new HashMap<>();

    public static final ParticleWrapper[] VALID_PARTICLE_ROUTE = new ParticleWrapper[]{ParticleWrapper.WATER_WAKE, ParticleWrapper.SUSPENDED_DEPTH, ParticleWrapper.CRIT,
            ParticleWrapper.CRIT_MAGIC, ParticleWrapper.SMOKE_NORMAL, ParticleWrapper.SPELL, ParticleWrapper.SPELL_WITCH, ParticleWrapper.VILLAGER_HAPPY, ParticleWrapper.ENCHANTMENT_TABLE,
            ParticleWrapper.FLAME, ParticleWrapper.CLOUD, ParticleWrapper.DRAGON_BREATH, ParticleWrapper.END_ROD, ParticleWrapper.TOTEM};
    public static final ParticleWrapper[] VALID_PARTICLE_SPOT = new ParticleWrapper[]{ParticleWrapper.FIREWORKS_SPARK, ParticleWrapper.CLOUD, ParticleWrapper.SUSPENDED_DEPTH, ParticleWrapper.SMOKE_LARGE,
            ParticleWrapper.SPELL_INSTANT, ParticleWrapper.SPELL_MOB, ParticleWrapper.SPELL_WITCH, ParticleWrapper.VILLAGER_HAPPY, ParticleWrapper.PORTAL, ParticleWrapper.FLAME,
            ParticleWrapper.CLOUD, ParticleWrapper.HEART, ParticleWrapper.DRAGON_BREATH, ParticleWrapper.END_ROD, ParticleWrapper.TOTEM};

    private final String name;
    private final int userID;
    private final Location origin;
    private final Location destiny;
    private final String message;
    private final Runnable spotRunnable;
    private ParticleWrapper particle_route;
    private ParticleWrapper particle_spot;
    private BukkitTask particleRunnable;

    public Route(String name, int userID, Location origin, Location destiny, String message, Runnable spotRunnable) {
        this.name = name;
        this.userID = userID;
        this.origin = origin;
        this.message = message;
        this.particle_route = (ParticleManager.getParticle(userID, ParticleManager.ParticleType.ROUTE)!=null) ? ParticleManager.getParticle(userID, ParticleManager.ParticleType.ROUTE) : ParticleWrapper.END_ROD;
        this.particle_spot = (ParticleManager.getParticle(userID, ParticleManager.ParticleType.SPOT)!=null) ? ParticleManager.getParticle(userID, ParticleManager.ParticleType.SPOT) : ParticleWrapper.FIREWORKS_SPARK;
        this.particleRunnable = null;
        this.destiny = destiny;
        this.spotRunnable = spotRunnable;
    }

    public Route(String name, int userID, Location origin, Location destiny, Runnable spotRunnable) {
        this.name = name;
        this.userID = userID;
        this.origin = origin;
        this.message = null;
        this.particle_route = ParticleManager.getParticle(userID, ParticleManager.ParticleType.ROUTE);
        this.particle_spot = ParticleManager.getParticle(userID, ParticleManager.ParticleType.SPOT);
        this.particleRunnable = null;
        this.destiny = destiny;
        this.spotRunnable = spotRunnable;
    }

    public Route(String name, int userID, Location origin, Location destiny, String message) {
        this.name = name;
        this.userID = userID;
        this.origin = origin;
        this.message = message;
        this.particle_route = (ParticleManager.getParticle(userID, ParticleManager.ParticleType.ROUTE)!=null) ? ParticleManager.getParticle(userID, ParticleManager.ParticleType.ROUTE) : ParticleWrapper.END_ROD;
        this.particle_spot = (ParticleManager.getParticle(userID, ParticleManager.ParticleType.SPOT)!=null) ? ParticleManager.getParticle(userID, ParticleManager.ParticleType.SPOT) : ParticleWrapper.FIREWORKS_SPARK;
        this.particleRunnable = null;
        this.destiny = destiny;
        this.spotRunnable = null;
    }

    public Route(String name, int userID, Location origin, Location destiny) {
        this.name = name;
        this.userID = userID;
        this.origin = origin;
        this.message = null;
        this.particle_route = (ParticleManager.getParticle(userID, ParticleManager.ParticleType.ROUTE)!=null) ? ParticleManager.getParticle(userID, ParticleManager.ParticleType.ROUTE) : ParticleWrapper.END_ROD;
        this.particle_spot = (ParticleManager.getParticle(userID, ParticleManager.ParticleType.SPOT)!=null) ? ParticleManager.getParticle(userID, ParticleManager.ParticleType.SPOT) : ParticleWrapper.FIREWORKS_SPARK;
        this.particleRunnable = null;
        this.destiny = destiny;
        this.spotRunnable = null;
    }

    public static Route getRoute(Player p) {
        return ROUTES.get(p.getName());
    }

    public static boolean invalidate(Player p) {
        Route r = getRoute(p);
        if (r == null) return false;
        r.invalidate();
        return true;
    }

    public String getUsername() {
        return this.name;
    }

    public int getUserID() {
        return this.userID;
    }

    public Location getOrigin() {
        return this.origin;
    }

    public Location getDestiny() {
        return this.destiny;
    }

    public String getMessage() {
        return this.message;
    }

    public ParticleWrapper getRouteParticle() {
        return this.particle_route;
    }

    public void setRouteParticle(ParticleWrapper particle) {
        this.particle_route = particle;
    }

    public ParticleWrapper getSpotParticle() {
        return this.particle_spot;
    }

    public void setSpotParticle(ParticleWrapper particle) {
        this.particle_spot = particle;
    }

    public BukkitTask getParticleRunnable() {
        return this.particleRunnable;
    }

    public Runnable getSpotRunnable() {
        return this.spotRunnable;
    }

    public void start() {
        final Player p = Script.getPlayer(getUsername());
        if (p == null) return;
        Route route = getRoute(p);
        if (route != null) {
            route.invalidate();
        }
        final ParticleWrapper route_particle = getRouteParticle();
        final ParticleWrapper spot_particle = getSpotParticle();
        this.particleRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline() || !ROUTES.containsKey(p.getName())) {
                    this.cancel();
                    return;
                }
                try {
                    Location origin = p.getEyeLocation().add(0, -.55, 0);
                    Vector originVector = origin.toVector();
                    Vector targetVector = getDestiny().toVector();

                    origin.setDirection(targetVector.subtract(originVector));
                    Vector increase = origin.getDirection();
                    increase.multiply(0.1);
                    double maxLength = 5;

                    Particle particle = new Particle(route_particle.getParticle(), false, 0.01F, 0.01F, 0.01F, 0F, 1);

                    for (double d = 0; d <= maxLength; d += 0.1) {
                        Location loc = origin.add(increase);
                        particle.setLocation(loc);
                        particle.sendPlayer(p);
                    }

                    double distance = p.getLocation().distance(getDestiny());

                    if (distance < 100) {
                        Script.sendActionBar(p, "§6Noch §l" + new DecimalFormat("#.#").format(distance) + "m§6 bis zum Ziel. §8(§6" + calcETA(distance) + " Sekunden§8)");
                        Location l = getDestiny();

                        particle = new Particle(spot_particle.getParticle(), true, 0, 0, 0, 0, 1);

                        for (Location loc : Script.circle(l, .9D, 10)) {
                            particle.setLocation(loc);
                            particle.sendPlayer(p);
                        }
                    } else {
                        Script.sendActionBar(p, "§6Noch §l" + ((int) distance) + "m§6 bis zum Ziel. §8(§6" + calcETA(distance) + " Sekunden§8)");
                    }
                    if (distance <= 1.5) {
                        finish();
                        this.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(main.getInstance(), 0L, 10L);
        ROUTES.put(getUsername(), this);
    }

    public void invalidate() {
        if (getParticleRunnable() != null) getParticleRunnable().cancel();
        ROUTES.remove(this.getUsername());
    }

    public void finish() {
        if (getSpotRunnable() != null) getSpotRunnable().run();
        Player p = Script.getPlayer(getUsername());
        if (p != null) {
            p.sendMessage(Objects.requireNonNullElse(message, Navi.PREFIX + "Du hast dein Ziel erreicht."));
            Script.sendActionBar(p, "§r");
        }
        ROUTES.remove(this.getUsername());
    }

    private static int calcETA(double meter) {
        return (int) (meter / 6.0);
    }

}
