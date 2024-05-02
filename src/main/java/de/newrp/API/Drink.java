package de.newrp.API;

public enum Drink {
    TRINKWASSER("Trinkwasser", 0),
    CRAPPUCCINO("Crappuccino", 0),
    KAFFEE("Kaffee", 0),
    ESPRESSO("Espresso", 0),
    LATTE_MACCHIATO("Latte Macchiato", 0),
    FILTERKAFFEE("Filterkaffee", 0),
    HOT_CHOCOLATE("Heiße Schokolade", 0);

    private final String name;
    private final double alk;

    Drink(String name, double alk) {
        this.name = name;
        this.alk = alk;
    }

    public String getName() {
        return this.name;
    }

    public double getAlcohol() {
        return this.alk;
    }
}
