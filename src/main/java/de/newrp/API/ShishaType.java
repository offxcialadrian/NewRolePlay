package de.newrp.API;

public enum ShishaType {

    WASSERMELONE("Wassermelone", 30, 0.25F, 35),
    DOPPELAPFEL("Doppel-Apfel", 27, 0.19F, 40),
    TRAUBE("Traube", 23, 0.27F, 25),
    ZITRONE("Zitrone", 32, 0.21F, 35),
    PFIRSICH_MINZE("Pfirsich-Minze", 35, 0.26F, 50),
    SPECIAL("Special", 40, 0.35F, 75);

    private final String name;
    private final int duration;
    private final float smoke;
    private final int price;

    ShishaType(String name, int duration, float smoke, int price) {
        this.name = name;
        this.duration = duration;
        this.smoke = smoke;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public int getDuration() {
        return this.duration;
    }

    public float getSmoke() {
        return this.smoke;
    }

    public int getPrice() {
        return this.price;
    }
}