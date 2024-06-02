package de.newrp.API;

import java.util.Random;

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

    public static String convertToDrunkText(String input, float level) {
        if (level >= 5.5) return heavyDrunk(input);
        else if (level >= 2.5) return mediumDrunk(input);
        else return slightDrunk(input);
    }

    private static String slightDrunk(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            sb.append(c);
            if (Character.isLetter(c) && new Random().nextDouble() < 0.1) {
                sb.append(c);
            }
        }
        return insertHicks(sb.toString(), 0.005);
    }

    private static String mediumDrunk(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c) && new Random().nextDouble() < 0.1) {
                sb.append(c).append(c);
            } else if (Character.isLetter(c) && new Random().nextDouble() < 0.1) {
                sb.append(c).append(new Random().nextBoolean() ? 'z' : 'x');
            } else {
                sb.append(c);
            }
        }
        return insertHicks(sb.toString(), 0.02);
    }

    private static String heavyDrunk(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c) && new Random().nextDouble() < 0.2) {
                sb.append(c).append(c);
            } else if (Character.isLetter(c) && new Random().nextDouble() < 0.2) {
                sb.append(new Random().nextBoolean() ? 'z' : 'x');
            } else if (Character.isLetter(c) && new Random().nextDouble() < 0.1) {
                sb.append((char) (c + 1));
            } else {
                sb.append(c);
            }
        }
        return insertHicks(sb.toString(), 0.05);
    }

    private static String insertHicks(String input, double hicksProbability) {
        StringBuilder sb = new StringBuilder(input);
        int index = 0;
        while (index < sb.length()) {
            if (new Random().nextDouble() < hicksProbability) {
                sb.insert(index, "*hicks*");
                index += 8;
            }
            index++;
        }
        return sb.toString();
    }
}
