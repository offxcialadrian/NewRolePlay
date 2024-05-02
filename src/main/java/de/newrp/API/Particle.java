package de.newrp.API;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Particle {

    private final org.bukkit.Particle particletype;
    private final boolean longdistance;
    private final float offsetx;
    private final float offsety;
    private final float offsetz;
    private final float speed;
    private final int amount;
    private Location location;

    public Particle(org.bukkit.Particle particletype, Location location, boolean longdistance, float offsetx, float offsety, float offsetz, float speed, int amount) {
        this.particletype = particletype;
        this.location = location;
        this.longdistance = longdistance;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.offsetz = offsetz;
        this.speed = speed;
        this.amount = amount;
    }

    public Particle(org.bukkit.Particle particletype, boolean longdistance, float offsetx, float offsety, float offsetz, float speed, int amount) {
        this.particletype = particletype;
        this.longdistance = longdistance;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.offsetz = offsetz;
        this.speed = speed;
        this.amount = amount;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void sendAll() {
        Script.WORLD.spawnParticle(this.particletype, this.location, this.amount, this.offsetx, this.offsety, this.offsetz, this.speed);
    }

    public void sendPlayer(Player player) {
        if (player == null) return;
        player.spawnParticle(this.particletype, this.location, this.amount, this.offsetx, this.offsety, this.offsetz, this.speed);
    }
}