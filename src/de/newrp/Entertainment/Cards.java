package de.newrp.Entertainment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Cards {

    TWO(1, "Zwei", 2),
    THREE(1, "Drei", 3),
    FOUR(1, "Vier", 4),
    FIVE(1, "Fünf", 5),
    SIX(1, "Sechs", 6),
    SEVEN(1, "Sieben", 7),
    EIGHT(1, "Acht", 8),
    NINE(1, "Neun", 9),
    TEN(1, "Zehn", 10),
    JACK(1, "Bube", 10),
    QUEEN(1, "Dame", 10),
    KING(1, "König", 10),
    ACE(1, "Ass", 11);

    int id;
    String name;
    int value;

    Cards(int id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    private static final List<Cards> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int size = VALUES.size();

    public static Cards getRandomCard() {
        Random r = new Random();
        return VALUES.get(r.nextInt(size));
    }

}