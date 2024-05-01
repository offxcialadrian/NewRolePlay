package de.newrp.Vehicle;

public enum CarType {

    ALGERARI("Algerari", 1000, 11D, 30, Fuel.BENZIN, 6000, 69,1.9F, 2500, 100),
    HOSSAROSSA("HossaRossa", 1250, 17D, 50, Fuel.BENZIN, 14900, 200,2.2F, 5000,200),
    BIMBORGINI("Bimborgini", 1500, 25D, 40, Fuel.BENZIN, 27500, 282,2.6F, 10000, 300),
    VAN("Van", 1750, 15D, 150, Fuel.BENZIN, 52000, 242, 2F, 15000, 400),
    CHEETAH("Cheetah", 1500, 21D, 55, Fuel.BENZIN, 23400, 235,2.4F, 20000, 500),
    WINTLEY("Wintley", 1750, 27D, 50, Fuel.BENZIN, 30000, 287, 2.8F, 25000, 600),
    TAXI("Taxi", 1250, 20D, 0, Fuel.BENZIN, 0, 0, 1.9F, 0, 0);

    private final String name;
    private final int carheal;
    private final double max_speed;
    private final int kofferraum;
    private final Fuel kraftstoff;
    private final int price;
    private final int tax;
    private final float speed;
    private final int minpreis;
    private final int insurance;

    CarType(String name, int carheal, double max_speed, int kofferraum, Fuel kraftstoff, int price, int tax, float speed, int minpreis, int insurance) {
        this.name = name;
        this.carheal = carheal;
        this.max_speed = max_speed;
        this.kofferraum = kofferraum;
        this.kraftstoff = kraftstoff;
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
