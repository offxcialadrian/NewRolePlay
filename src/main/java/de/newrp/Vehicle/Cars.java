package de.newrp.Vehicle;

import org.bukkit.TreeSpecies;

public enum Cars {
    VOLKSWAGEN("Volkswagen", TreeSpecies.ACACIA, 1.0),
    BMW("BMW", TreeSpecies.JUNGLE, 1.3),
    AUDI("Audi", TreeSpecies.DARK_OAK, 1.6),
    MERCEDES("Mercedes", TreeSpecies.GENERIC, 2.0),
    FEUERWEHR("Feuerwehr", TreeSpecies.BIRCH, 1.0),
    POLIZEI("Polizei", TreeSpecies.REDWOOD, 1.0);

    private final String name;
    private final TreeSpecies type;
    private final double speed;

    Cars(String name, TreeSpecies type, double speed) {
        this.name = name;
        this.type = type;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public TreeSpecies getType() {
        return type;
    }

    public double getSpeed() {
        return speed;
    }
}
