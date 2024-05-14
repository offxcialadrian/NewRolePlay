package de.newrp.Organisationen;

import de.newrp.API.Baseballschlaeger;
import de.newrp.API.Script;
import de.newrp.Berufe.Equip;
import de.newrp.Waffen.Weapon;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public enum Stuff {

    BROT(1, Equip.Stuff.BROT.getName(), Equip.Stuff.BROT.getItem(), 1, 4),
    WASSER(2, Equip.Stuff.TRINKWASSER.getName(), Equip.Stuff.TRINKWASSER.getItem(), 1, 2),
    BASEY(3, "Baseballschläger", Baseballschlaeger.getItem(), 3, 250),
    PISTOLE(4, Weapon.PISTOLE.getName(), Weapon.PISTOLE.getWeapon(), 3, 350),
    STRIKER(5, Weapon.MP7.getName(), Weapon.MP7.getWeapon(), 4, 700),
    BRECHI(6, "Brechstange", Script.brechstange(), 5, 250),
    SCHUTZWESTE(7, "Schutzweste", Equip.Stuff.KEVLAR.getItem(), 6, 700);

    private final int id;
    private final String name;
    private final ItemStack item;
    private final int level;
    private final int cost;

    Stuff(int id, String name, ItemStack item, int level, int cost) {
        this.id = id;
        this.name = name;
        this.item = item;
        this.level = level;
        this.cost = cost;
    }

    public static Stuff getStuff(String name) {
        for (Stuff stuff : values()) {
            if (stuff.getName().equalsIgnoreCase(name)) {
                return stuff;
            }
        }
        return null;
    }

    public static Stuff getStuff(int id) {
        for (Stuff stuff : values()) {
            if (stuff.getId() == id) {
                return stuff;
            }
        }
        return null;
    }
}
