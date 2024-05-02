package de.newrp.Vehicle;

public enum VehicleAddon {

    LADESTATION(0, "akku", "Ladestation", 4500),
    //MUSIKANLAGE(1, "musik", "Musikanlage", 6000),
    VERSICHERUNG(2, "versicherung", "Versicherung", 500);

    private final int id;
    private final String name;
    private final String publicName;
    private final int preis;

    VehicleAddon(int id, String name, String publicName, int preis) {
        this.id = id;
        this.name = name;
        this.publicName = publicName;
        this.preis = preis;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPublicName() {
        return this.publicName;
    }

    public int getPreis() {
        return this.preis;
    }
}
