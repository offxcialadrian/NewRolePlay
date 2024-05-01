package de.newrp.API;

public enum PaymentType {

    CASH(1, "cash"),
    BANK(2, "bank");

    private final int id;
    private final String name;

    PaymentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

}
