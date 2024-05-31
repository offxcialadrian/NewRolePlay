package de.newrp.House;

public enum HouseAddon {

    ALARM(1, "Alarmanlage", 4800),
    HAUSKASSE(2, "Hauskasse", 6300),
    SLOT(4, "Mieterslot", 3700),
    KUEHLSCHRANK(5, "Kühlschrank", 600),
    WAFFENSCHRANK(6, "Waffenschrank", 4900),
    SICHERHEITSTUER(7, "Sicherheitstür", 5200);

    private final int id;
    private final String name;
    private final int price;

    HouseAddon(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static HouseAddon getHausAddonByID(int id) {
        for (HouseAddon addon : values()) {
            if (addon.getID() == id) return addon;
        }
        return null;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public static HouseAddon getHausAddonByName(String name) {
        for (HouseAddon addon : values()) {
            if (addon.getName().equalsIgnoreCase(name)) return addon;
        }
        return null;
    }
}
