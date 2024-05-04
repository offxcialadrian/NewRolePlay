package de.newrp.Waffen;

import de.newrp.NewRoleplayMain;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class WeaponData {
    private final Weapon w;
    private int ammo;
    private int wear;

    public WeaponData(Weapon w, int ammo, int wear) {
        this.w = w;
        this.ammo = ammo;
        this.wear = wear;
    }

    public static HashMap<Weapon, WeaponData> getWeaponData(Integer id) {
        HashMap<Weapon, WeaponData> data = new HashMap<>();
        try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement(
                "SELECT weaponID, ammo, wear FROM weapon WHERE userID = ?")) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Weapon w = Weapon.getWeaponByID(rs.getInt("weaponID"));
                    if (w != null) {
                        data.put(w, new WeaponData(w, rs.getInt("ammo"), rs.getInt("wear")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static WeaponData getWeaponData(int id, Weapon w) {
        return getWeaponData(id).get(w);
    }

    @Override
    public String toString() {
        return "WeaponData{" +
                "w=" + w +
                ", ammo=" + ammo +
                ", wear=" + wear +
                '}';
    }

    public Weapon getWeapon() {
        return this.w;
    }

    public int getAmmo() {
        return this.ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public int getWear() {
        return this.wear;
    }

    public void setWear(int wear) {
        this.wear = wear;
    }
}
