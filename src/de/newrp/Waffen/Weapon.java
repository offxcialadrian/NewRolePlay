package de.newrp.Waffen;

import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum Weapon {
    PISTOLE(1, Script.setName(Material.IRON_HORSE_ARMOR, "§7Pistole"), "Pistole", 0.45F, .850, 15, 8.5D, 1.8, 4.1, 600, Munition.AMMO_9MM);

    private final int weaponID;
    private final ItemStack is;
    private final String name;
    private final float recoil;
    private final double cooldown;
    private final int magazine;
    private final double damage;
    private final double reload;
    private final double armor;
    private final int max_wear;
    private final Munition ammo;

    Weapon(int weaponID, ItemStack is, String name, float recoil, double cooldown, int magazine, double damage, double reload, double armor, int max_wear, Munition ammo) {
        this.weaponID = weaponID;
        this.is = is;
        this.name = name;
        this.recoil = recoil;
        this.cooldown = cooldown;
        this.magazine = magazine;
        this.damage = damage;
        this.reload = reload;
        this.armor = armor;
        this.max_wear = max_wear;
        this.ammo = ammo;
    }

    public static Weapon getWeaponByID(int id) {
        for (Weapon w : values()) {
            if (w.getWeaponID() == id) {
                return w;
            }
        }
        return null;
    }

    public int getWeaponID() {
        return this.weaponID;
    }

    public ItemStack getWeapon() {
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setUnbreakable(true);
        is.setItemMeta(itemMeta);
        return this.is.clone();
    }

    public String getName() {
        return this.name;
    }

    public float getRecoil() {
        return this.recoil;
    }

    public double getCooldown() {
        return this.cooldown;
    }

    public int getMagazineSize() {
        return this.magazine;
    }

    public double getDamage() {
        return this.damage;
    }

    public double getReload(int skill) {
        if (skill == 8) return this.reload;
        double reload = this.reload;
        double d = ((8 - skill) * (this.reload / 10));
        return (reload + d);
    }

    public Munition getAmmoType() {
        return this.ammo;
    }

    public double getArmorPenetration() {
        return this.armor;
    }

    public int getMaxWear() {
        return this.max_wear;
    }

    public void addToInventory(int id) {
        WeaponData wdata = WeaponData.getWeaponData(id, this);
        if (wdata != null) {
            wdata.setWear(wdata.getWear() + 250);
        }
        final Weapon w = this;
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT weaponDataID FROM weapon WHERE userID = " + id + " AND weaponID = " + w.getWeaponID())) {
                if (rs.next()) {
                    int wdataID = rs.getInt("weaponDataID");
                    Script.executeAsyncUpdate("UPDATE weapon SET wear = wear+250  WHERE weaponDataID = " + wdataID);
                } else {
                    Script.executeAsyncUpdate("INSERT INTO weapon (userID, weaponID, ammo, wear) VALUES (" + id + ", " + w.getWeaponID() + ", 0, " + w.getMaxWear() + ");");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int getMunition(int id) {
        WeaponData wdata = WeaponData.getWeaponData(id, this);
        if (wdata != null) return wdata.getAmmo();
        return 0;
    }

    public void addMunition(int id, int ammo) {
        WeaponData wdata = WeaponData.getWeaponData(id, this);
        if (wdata != null) {
            wdata.setAmmo(wdata.getAmmo() + ammo);
        }
        final Weapon w = this;
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT weaponDataID FROM weapon WHERE userID = " + id + " AND weaponID = " + w.getWeaponID())) {
                if (rs.next()) {
                    int wdataID = rs.getInt("weaponDataID");
                    Script.executeAsyncUpdate("UPDATE weapon SET ammo = ammo + " + ammo + " WHERE weaponDataID = " + wdataID);
                } else {
                    Script.executeAsyncUpdate("INSERT INTO weapon (userID, weaponID, ammo, wear) VALUES (" + id + ", " + w.getWeaponID() + ", " + ammo + ", " + w.getMaxWear() + ");");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeMunition(int id, int ammo) {
        WeaponData wdata = WeaponData.getWeaponData(id, this);
        if (wdata != null) {
            int i = wdata.getAmmo() - ammo;
            if (i < 0) i = 0;
            wdata.setAmmo(i);
        }
        final Weapon w = this;
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT weaponDataID FROM weapon WHERE userID = " + id + " AND weaponID = " + w.getWeaponID())) {
                if (rs.next()) {
                    int wdataID = rs.getInt("weaponDataID");
                    Script.executeAsyncUpdate("UPDATE weapon SET ammo = ammo - " + ammo + " WHERE weaponDataID = " + wdataID);
                } else {
                    Script.executeAsyncUpdate("INSERT INTO weapon (userID, weaponID, ammo, wear) VALUES (" + id + ", " + w.getWeaponID() + ", " + ammo + ", " + w.getMaxWear() + ");");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int getWear(int id) {
        WeaponData wdata = WeaponData.getWeaponData(id, this);
        if (wdata != null) return wdata.getWear();
        return 0;
    }

    public void addWear(int id, int wear) {
        WeaponData wdata = WeaponData.getWeaponData(id, this);
        if (wdata != null) {
            wdata.setWear(wdata.getWear() + wear);
        }
        final Weapon w = this;
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT weaponDataID FROM weapon WHERE userID = " + id + " AND weaponID = " + w.getWeaponID())) {
                if (rs.next()) {
                    int wdataID = rs.getInt("weaponDataID");
                    Script.executeAsyncUpdate("UPDATE weapon SET wear = wear + " + wear + " WHERE weaponDataID = " + wdataID);
                } else {
                    Script.executeAsyncUpdate("INSERT INTO weapon (userID, weaponID, ammo, wear) VALUES (" + id + ", " + w.getWeaponID() + ", 0, " + wear + ");");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeWear(int id, int wear) {
        WeaponData wdata = WeaponData.getWeaponData(id, this);
        if (wdata != null) {
            int i = wdata.getWear() - wear;
            if (i < 0) i = 0;
            wdata.setWear(i);
        }
        final Weapon w = this;
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT weaponDataID FROM weapon WHERE userID = " + id + " AND weaponID = " + w.getWeaponID())) {
                if (rs.next()) {
                    int wdataID = rs.getInt("weaponDataID");
                    Script.executeAsyncUpdate("UPDATE weapon SET wear = wear - " + wear + " WHERE weaponDataID = " + wdataID);
                } else {
                    Script.executeAsyncUpdate("INSERT INTO weapon (userID, weaponID, ammo, wear) VALUES (" + id + ", " + w.getWeaponID() + ", 0, " + wear + ");");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public enum Munition {
        AMMO_9MM(0, "9mm"),
        AMMO_19MM(1, "9x19mm"),
        AMMO_5_56_45MM(2, "5.56 x 45mm"),
        AMMO_7_62_51MM(3, "7.62 x 51mm"),
        SCHROT(4, "Schrot"),
        AMMO_7_62_39MM(5, "7.62 x 39mm"),
        AMMO_50AE(6, ".50 AE");

        private final int id;
        private final String name;

        Munition(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static Munition getAmmoByID(int id) {
            for (Munition m : values()) {
                if (m.getID() == id) return m;
            }
            return null;
        }

        public static Munition getAmmoByName(String name) {
            for (Munition m : values()) {
                if (m.getName().equals(name)) return m;
            }
            return null;
        }

        public int getID() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

    }
}
