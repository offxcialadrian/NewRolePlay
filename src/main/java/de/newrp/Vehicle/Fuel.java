package de.newrp.Vehicle;

public enum Fuel {

    BENZIN("Benzin"),
    LPG("LPG-Autogas"),
    DIESEL("Diesel");

    private final String name;

    Fuel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
