package de.newrp.Organisationen;

import de.newrp.API.Baseballschlaeger;
import de.newrp.API.Machete;
import de.newrp.API.Script;
import de.newrp.Berufe.Equip;
import de.newrp.NewRoleplayMain;
import de.newrp.Shop.ShopItem;
import de.newrp.Waffen.Weapon;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public enum Stuff {

    BROT(1, Equip.Stuff.BROT.getName(), Equip.Stuff.BROT.getItem(), 1, 4),
    WASSER(2, Equip.Stuff.TRINKWASSER.getName(), Equip.Stuff.TRINKWASSER.getItem(), 2, 2),
    BASEY(3, "Baseballschläger", Baseballschlaeger.getItem(), 2, 250),
    PISTOLE(4, Weapon.PISTOLE.getName(), Weapon.PISTOLE.getWeapon(), 3, 350),
    STRIKER(5, Weapon.MP7.getName(), Weapon.MP7.getWeapon(), 4, 700),
    BRECHI(6, "Brechstange", Script.brechstange(), 5, 250),
    SCHUTZWESTE(7, "Schutzweste", Equip.Stuff.KEVLAR.getItem(), 6, 700),
    MACHETE(8, "Machete", Machete.getItem(), 7, 3100),
    MUNITION_AK47(9, "7.62 x 39mm", Script.setName(ShopItem.AMMO_762MM.getItemStack(), "7.62 x 39mm"), 5, 20);

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
            if (stuff.getName().equalsIgnoreCase(name.replaceAll("§7", ""))) {
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

    public int getPrice(int org) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM equip WHERE id=" + -org + " AND equip=" + this.id)) {
            if (rs.next()) return rs.getInt("price");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPrice(int org, int price) {
        if (getPrice(org) == 0) {
            try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM equip WHERE id=" + -org + " AND equip=" + this.id)) {
                if (!rs.next()) Script.executeUpdate("INSERT INTO equip (id, equip, price) VALUES (" + -org + ", " + this.id + ", 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Script.executeUpdate("UPDATE equip SET price=" + price + " WHERE id=" + -org + " AND equip=" + this.id);
    }
}
