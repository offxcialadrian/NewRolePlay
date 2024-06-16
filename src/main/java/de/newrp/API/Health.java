package de.newrp.API;

import de.newrp.Administrator.BuildMode;
import de.newrp.Administrator.SDuty;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Player.AFK;
import de.newrp.Police.Jail;
import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Objects;

public enum Health {
    THIRST(1, "thirst", "Durst", 20, 20),
    FAT(2, "fat", "Fett", 20, 0),
    BLOOD(3, "bloodamount", "Blut", 6, 6),
    MUSCLES(4, "muscles", "Muskeln", 10, 0);

    public static final HashMap<String, Float> BLEEDING = new HashMap<>();
    private final int id;
    private final String name;
    private final String publicname;
    private final float max;
    private final float d_amount;

    Health(int id, String name, String publicname, float max, float d_amount) {
        this.id = id;
        this.name = name;
        this.publicname = publicname;
        this.max = max;
        this.d_amount = d_amount;
    }

    public static String PREFIX = "§8[§4Gesundheit§8] §4" + Messages.ARROW + " §7";

    public static void update() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (AFK.isAFK(p)) continue;
            if (BLEEDING.containsKey(p.getName()) && !GangwarCommand.isInGangwar(p)) {
                float amount = BLEEDING.get(p.getName());
                Health.BLOOD.remove(Script.getNRPID(p), amount);
                p.damage(.5D);
                if (amount < 1F) {
                    p.sendMessage(PREFIX + "Du blutest leicht...");
                } else {
                    p.sendMessage(PREFIX + "Du blutest stark...");
                }
            } else {
                float amount = Script.getRandomFloat(.2F, .3F);
                Health.BLOOD.add(Script.getNRPID(p), amount);
            }
            if (!AFK.isAFK(p) && !Jail.isInJail(p) && !Friedhof.isDead(p) && !SDuty.isSDuty(p) && !BuildMode.isInBuildMode(p)) {
                Health.THIRST.remove(Script.getNRPID(p), Script.getRandomFloat(.09F, .12F));
            }
        }
    }

    public static void setup(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM health WHERE id=" + id)) {
            if (!rs.next()) {
                Script.executeAsyncUpdate("INSERT INTO health (id, bloodamount, thirst, fat, muscles) VALUES (" + id + ", 6, 20, 0, 0);");
            }
        } catch (SQLException e) {

            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static HashMap<Health, Float> getFull(int id) {
        return getHealthStats(id);
    }


    public static float getBloodAmount(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT bloodamount FROM health WHERE id=" + id)) {
            if (rs.next()) {
                return rs.getFloat("bloodamount");
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return 0f;
    }


    public static int getMuscleLevel(int id) {
        float f = Health.MUSCLES.get(id);
        return (int) Math.floor(f);
    }

    public static void setBleeding(Player p) {
        if (!SDuty.isSDuty(p) && !GangwarCommand.isInGangwar(p)) {
            if (Health.BLEEDING.containsKey(p.getName()) && !GangwarCommand.isInGangwar(p)) {
                float amount = Health.BLEEDING.get(p.getName());
                amount += Script.getRandomFloat(.1F, .3F);
                Health.BLEEDING.put(p.getName(), amount);
                if (amount < 1F) {
                    p.sendMessage(PREFIX + "Du blutest leicht...");
                } else {
                    p.sendMessage(PREFIX + "Du blutest stark...");
                }
            } else {
                Health.BLEEDING.put(p.getName(), Script.getRandomFloat(.1F, .3F));
                p.sendMessage(PREFIX + "Du blutest leicht...");
            }
        }
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPublicName() {
        return this.publicname;
    }

    public float getMax() {
        return this.max;
    }

    public float getDefault() {
        return this.d_amount;
    }

    public float get(int id) {
        HashMap<Health, Float> health = getHealthStats(id);
        return (health.get(this));
    }

    public void add(int id, float amount) {
        float get = get(id);
        float f = get + amount;
        if (f > this.max) f = max;
        HashMap<Health, Float> health = getFull(id);
        health.put(this, f);
        if(this.get(id) >= this.max) return;
        Script.executeAsyncUpdate("UPDATE health SET " + this.name + "=" + f + " WHERE id=" + id);
    }

    public void set(int id, float amount) {
        if (amount > this.max) amount = max;
        HashMap<Health, Float> health = getFull(id);
        health.put(this, amount);
        Script.executeAsyncUpdate("UPDATE health SET " + this.name + "=" + amount + " WHERE id=" + id);
    }

    public static HashMap<Health, Float> getHealthStats(int id) {
        HashMap<Health, Float> health = new HashMap<>();
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM health WHERE id=" + id)) {
            if (rs.next()) {
                health.put(Health.THIRST, rs.getFloat("thirst"));
                health.put(Health.FAT, rs.getFloat("fat"));
                health.put(Health.BLOOD, rs.getFloat("bloodamount"));
                health.put(Health.MUSCLES, rs.getFloat("muscles"));
            } else {
                for (Health h : Health.values()) health.put(h, h.getDefault());
            }
        } catch (SQLException e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return health;
    }

    public void remove(int id, float amount) {
        float get = get(id);
        float f = (get - amount);
        if (f < 0) f = 0;
        Script.executeAsyncUpdate("UPDATE health SET " + this.name + "=" + f + " WHERE id=" + id);
        HashMap<Health, Float> health = getFull(id);
        health.put(this, f);
        if (this.id == 3) {
            if (f <= 2.0F) {
                Player p = Script.getPlayer(id);
                if (p != null) {
                    if(!GangwarCommand.isInGangwar(p)) {
                        Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 300 * 20, 1, false, false));
                            p.sendMessage(PREFIX + "Dir wird unwohl...");
                        });
                    }
                }
            }
            Player p = Script.getPlayer(id);
            if (p != null) {
                Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 1, false, false)));
            }
        }
        if (this.id == 1) {
            Player p = Script.getPlayer(id);
            if (f <= 2.0F) {
                if (p != null) {
                    Bukkit.getScheduler().runTask(NewRoleplayMain.getInstance(), () -> {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 300 * 20, 1, false, false));
                        p.sendMessage(PREFIX + "Du verdurstest...");
                    });
                }
            } else if (f < 10.0F) {
                if (p != null) {
                    Script.sendActionBar(p, PREFIX + "Du bist durstig.");
                    p.sendMessage(PREFIX + "Du bist durstig.");
                }
            }
        }
        // Unnötig - kann bei MUSCLES.add bearbeitet werden statt hier entfernt zu werden ~1Minify
        /*if (this.id == 2) {
            if (get(id) > .3) { // > .3 trifft immer zu da Anagbe 1F-2F random ist ~1Minify
                Health.MUSCLES.remove(id, Script.getRandomFloat(.03F, .05F));
            }
        }*/
    }
}
