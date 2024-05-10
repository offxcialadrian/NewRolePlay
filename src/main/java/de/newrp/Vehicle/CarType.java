package de.newrp.Vehicle;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;

public enum CarType {

    OPPEL("Oppel", TreeSpecies.JUNGLE, Material.JUNGLE_BOAT, 500, 1.0, 0, Fuel.BENZIN, 2.0F, 6000, 79,1.0F, 2500, 100),
    VOLTSWAGEN("Voltswagen", TreeSpecies.ACACIA, Material.ACACIA_BOAT, 700, 1.2, 0, Fuel.BENZIN, 1.8F, 14900, 113,1.2F, 5000,200),
    NMW("NMW", TreeSpecies.DARK_OAK, Material.DARK_OAK_BOAT, 900, 1.4, 0, Fuel.BENZIN, 1.6F, 27500, 162,1.4F, 10000, 300),
    AWDI("Awdi", TreeSpecies.BIRCH, Material.BIRCH_BOAT, 1100, 1.6, 0, Fuel.BENZIN, 1.4F, 52000, 197, 1.6F, 15000, 400),
    MERCADAS("Mercadas", TreeSpecies.REDWOOD, Material.SPRUCE_BOAT, 1300, 1.8, 0, Fuel.BENZIN, 1.2F, 23400, 234,1.8F, 20000, 500),
    PAWSCHE("Pawsche", TreeSpecies.GENERIC, Material.OAK_BOAT, 1500, 2.0, 0, Fuel.BENZIN, 1.0F, 23400, 271, 2.0F, 30000, 600);

    private final String name;
    private final TreeSpecies type;
    private final Material material;
    private final int carheal;
    private final double max_speed;
    private final int kofferraum;
    private final Fuel kraftstoff;
    private final float consumption;
    private final int price;
    private final int tax;
    private final float speed;
    private final int minpreis;
    private final int insurance;

    CarType(String name, TreeSpecies type, Material material, int carheal, double max_speed, int kofferraum, Fuel kraftstoff, float consumption, int price, int tax, float speed, int minpreis, int insurance) {
        this.name = name;
        this.type = type;
        this.material = material;
        this.carheal = carheal;
        this.max_speed = max_speed;
        this.kofferraum = kofferraum;
        this.kraftstoff = kraftstoff;
        this.consumption = consumption;
        this.price = price;
        this.tax = tax;
        this.speed = speed;
        this.minpreis = minpreis;
        this.insurance = insurance;
    }

    public static CarType getCarTypeByName(String name) {
        for (CarType ct : values()) {
            if (ct.getName().equalsIgnoreCase(name)) return ct;
        }
        return null;
    }

    public TreeSpecies getType() {
        return this.type;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getName() {
        return this.name;
    }

    public int getCarheal() {
        return this.carheal;
    }

    public double getMaxSpeed() {
        return this.max_speed;
    }

    public int getKofferraumSize() {
        return this.kofferraum;
    }

    public Fuel getFuel() {
        return this.kraftstoff;
    }

    public float getConsumption() {
        return this.consumption;
    }

    public int getPrice() {
        return this.price;
    }

    public int getTax() {
        return this.tax;
    }

    public float getSpeed() {
        return this.speed;
    }

    public int getMinPreis() {
        return this.minpreis;
    }

    public int getInsurance() {
        return this.insurance;
    }
}
