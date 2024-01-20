package de.newrp.API;

import org.bukkit.Location;

public enum HologramList {

    CAFE_SH(1, "§8/§6buy", new Location(Script.WORLD, 626, 67, 1030)),
    BANKENCOMMAND(2, "§8/§6banken", new Location(Script.WORLD, 949, 77, 934.5)),
    AEKI(3, "§8/§6buy", new Location(Script.WORLD, 681, 67, 902)),
    AEKI_CAFE(4, "§8/§6buy",new Location(Script.WORLD, 689, 137, 909)),
    CAFE_AM_X3(5, "§8/§6buy",new Location(Script.WORLD, 755, 71, 924)),
    GUNSHOP(6, "§8/§6buy",new Location(Script.WORLD, 454, 68, 929)),
    EQUIP_COPS(7, "§8/§6equip",new Location(Script.WORLD, 405, 70, 824)),
    DUTY_COPS(8, "§8/§6duty",new Location(Script.WORLD, 408, 70, 824)),
    PERSONALAUSWEIS(9, "§8/§6personalausweis",new Location(Script.WORLD, 554, 69, 984)),
    DUTY_MEDIC(10, "§8/§6duty",new Location(Script.WORLD, 348, 75, 1267)),
    DUTY_MEDIC_2(11, "§8/§6duty",new Location(Script.WORLD, 286, 74, 1239)),
    EQUIP_MEDIC(12, "§8/§6equip",new Location(Script.WORLD, 267,74,1253)),
    EQUIP_NEWS(13, "§8/§6equip",new Location(Script.WORLD, 289, 66, 788)),
    BLACKJACK(14, "§8/§6blackjack",new Location(Script.WORLD, 790, 108, 858)),
    ARBEITSLOSENGELD(15, "§8/§6arbeitslosengeld",new Location(Script.WORLD, 552, 69, 966)),
    SELFSTORAGE(16, "§8/§6selfstorage",new Location(Script.WORLD, 1012, 67, 1202)),
    BUY_ELEKTRO(17, "§8/§6buy", new Location(Script.WORLD, 865, 73, 964)),
    BUY_SUPERMARKT(18, "§8/§6buy", new Location(Script.WORLD, 640, 67, 861)),
    AKTIENMARKT(19, "§8/§6aktienmarkt", new Location(Script.WORLD, 943, 76, 941)),
    WAHLEN(20, "§8/§6wahlen", new Location(Script.WORLD, 545, 69, 1014)),
    BUY_MUSIKLADEN(21, "§8/§6buy", new Location(Script.WORLD, 864, 73, 906)),
    BUY_FLOWER(22, "§8/§6buy", new Location(Script.WORLD, 866, 73, 951)),
    BUY_APOTHEKE(23, "§8/§6buy", new Location(Script.WORLD, 346, 75, 1079)),
    SCHULE(24, "§8/§6schule", new Location(Script.WORLD, 731, 66, 750)),
    LAGERARBEITER(25, "§8/§6lagerarbeiter", new Location(Script.WORLD, 995, 68, 1260));

    private final int id;
    private final String name;
    private final Location loc;

    HologramList(int id, String name, Location loc) {
        this.id = id;
        this.name = name;
        this.loc = loc;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.loc;
    }
}
