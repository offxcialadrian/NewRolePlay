package de.newrp.API;

public enum Gender {

    MALE("Männlich"),
    FEMALE("Weiblich");

    String name;

    Gender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
