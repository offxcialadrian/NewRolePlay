package de.newrp.Ticket;

public enum TicketTopic {

    FRAGE(0, "Frage"),
    SPIELER(1, "Spieler"),
    BUG(2, "Bug"),
    ACCOUNT(3, "Account");

    private final int id;
    private final String name;

    TicketTopic(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
