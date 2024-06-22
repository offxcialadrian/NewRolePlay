package de.newrp.features.bomb.data;

import lombok.Getter;
import org.bukkit.Color;

public enum WireType {

    RED("§cRot", Color.RED),
    GREEN("§aGrün", Color.GREEN),
    YELLOW("§eGelb", Color.YELLOW),
    BLUE("§bBlau", Color.BLUE),
    PURPLE("§dLila", Color.PURPLE),
    ORANGE("§6Orange", Color.ORANGE);

    @Getter
    private final String name;

    @Getter
    private final Color color;

    WireType(final String name, final Color color) {
        this.name = name;
        this.color = color;
    }

}
