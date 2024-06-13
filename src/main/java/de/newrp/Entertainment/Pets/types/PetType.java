package de.newrp.Entertainment.Pets.types;

import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.util.Objects;

@Getter
public enum PetType {

    DOG(1, "Hund", EntityType.WOLF, "DEFAULT", false),
    CAT(2, "Katze", EntityType.CAT, "CALICO", false),
    FOX(3, "Fuchs", EntityType.FOX, "RED", false),
    PARROT(4, "Papagei", EntityType.PARROT, "RED", false),
    BEE(5, "Biene", EntityType.BEE, "DEFAULT", false),
    OCELOT(6, "Ozelot", EntityType.OCELOT, "DEFAULT", false),
    CHICKEN(7, "Huhn", EntityType.CHICKEN, "DEFAULT", false),
    POLAR(8, "Eisbär", EntityType.POLAR_BEAR, "DEFAULT", true),
    HORSE(9, "Pferd", EntityType.HORSE, "DEFAULT", true),
    PIG(10, "Schwein", EntityType.PIG, "DEFAULT", true),
    COW(11, "Kuh", EntityType.COW, "DEFAULT", true),
    SHEEP(12, "Schaf", EntityType.SHEEP, "DEFAULT", true),
    ALPACA(13, "Alpaka", EntityType.LLAMA, "CREAMY", true),
    TURTLE(14, "Schildkröte", EntityType.TURTLE, "DEFAULT", false),
    PANDA(15, "Panda", EntityType.PANDA, "DEFAULT", true);

    private final int id;
    private final String name;
    private final EntityType type;
    private final String variant;
    private final boolean baby;

    PetType(int id, String name, EntityType type, String variant, boolean baby) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.variant = variant;
        this.baby = baby;
    }

    public static PetType getType(int id) {
        for (PetType type : PetType.values())
            if (type.getId() == id) return type;
        return null;
    }

    public static PetType getType(String name) {
        for (PetType type : PetType.values())
            if (Objects.equals(type.getName(), name)) return type;
        return null;
    }
}
