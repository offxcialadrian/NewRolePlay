package de.newrp.Organisationen;

import de.newrp.API.Debug;
import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Plantage {
    public static final ArrayList<Plantage> PLANTAGEN = new ArrayList<>();
    public static final String PREFIX = "§8[§2Plantage§8] §2"  + Messages.ARROW + " §7";

    private final int plantID;
    private final Organisation fraktion;
    private final Location loc;
    private final PlantageType type;
    private long time;
    private int ertrag;
    private boolean fertilize;
    private boolean water;
    private long last_fertilize;
    private long last_water;
    private int purityCounter;

    public Plantage(int plantID, Organisation fraktion, Location loc, PlantageType type) {
        this.plantID = plantID;
        this.fraktion = fraktion;
        this.loc = loc;
        this.type = type;
        this.time = (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(type.getTime()));
        this.ertrag = 0;
        this.fertilize = false;
        this.water = false;
        this.last_fertilize = 0;
        this.last_water = 0;
        this.purityCounter = 0;
    }

    public Plantage(int plantID, Organisation fraktion, Location loc, PlantageType type, long time, int ertrag, boolean fertilize, boolean water, long last_fertilize, long last_water, int purityCounter) {
        this.plantID = plantID;
        this.fraktion = fraktion;
        this.loc = loc;
        this.type = type;
        this.time = time;
        this.ertrag = ertrag;
        this.fertilize = fertilize;
        this.water = water;
        this.last_fertilize = last_fertilize;
        this.last_water = last_water;
        this.purityCounter = purityCounter;
    }

    public static Plantage getNextPlantage(Player p) {
        Location loc = p.getLocation();
        for (Plantage plant : Plantage.PLANTAGEN) {
            if (Script.isInRange(loc, plant.getLocation(), 1.1)) {
                return plant;
            }
        }
        return null;
    }

    public static Plantage getNextPlantage(Player p, double distance) {
        Location loc = p.getLocation();
        for (Plantage plant : Plantage.PLANTAGEN) {
            if (Script.isInRange(loc, plant.getLocation(), distance)) {
                return plant;
            }
        }
        return null;
    }

    public static Plantage getNextPlantage(Player p, Organisation f) {
        // get the -> nearest <- plant instead of the first plant which satisfies the condition
        Location loc = p.getLocation();
        Plantage foundPlant = null;
        double distanceToFound = Double.MAX_VALUE;

        for (Plantage plant : Plantage.PLANTAGEN) {
            if (plant.getOrganisation() != f) continue;

            double distance = plant.getLocation().distance(loc);
            if (distance > 1.1) continue;
            if (distance > distanceToFound) continue;

            foundPlant = plant;
            distanceToFound = distance;
        }

        return foundPlant;
    }

    public static int getPlantageCount(Organisation f) {
        int i = 0;
        for (Plantage plant : PLANTAGEN) {
            if (plant.getOrganisation().equals(f)) i++;
        }
        return i;
    }

    public static void loadAll() {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try (Statement stmt = main.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM plantage")) {
                while (rs.next()) {
                    Plantage p = new Plantage(rs.getInt("plantID"), Organisation.getOrganisation(rs.getInt("organisationID")), new Location(Script.WORLD, rs.getInt("x"), rs.getInt("y"), rs.getInt("z")),
                            PlantageType.getTypeByDrugID(rs.getInt("drugID")), rs.getLong("time"), rs.getInt("ertrag"), rs.getBoolean("fertilize"), rs.getBoolean("water"),
                            rs.getLong("last_fertilize"), rs.getLong("last_water"), rs.getInt("purityCounter"));
                    PLANTAGEN.add(p);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void tick() {
        if (PLANTAGEN.isEmpty()) return;
        HashMap<Organisation, List<Plantage>> plants = new HashMap<>();

        for (Plantage plant : PLANTAGEN) {
            Organisation f = plant.getOrganisation();
            if (plants.containsKey(f)) {
                plants.get(f).add(plant);
            } else {
                ArrayList<Plantage> list = new ArrayList<>();
                list.add(plant);
                plants.put(f, list);
            }
        }
        for (Map.Entry<Organisation, List<Plantage>> ent : plants.entrySet()) {
            Organisation f = ent.getKey();
            List<Plantage> list = ent.getValue();
            int good = 0, bad = 0, crit = 0;
            boolean sendMessage = (Calendar.getInstance().get(Calendar.MINUTE) % 15 == 0);
            for (Plantage plant : list) {
                int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(plant.getTime() - System.currentTimeMillis());
                if (sendMessage) {
                    if (minutes < 0) {
                        bad++;
                    } else if (!plant.getFertilize() || !plant.getWater()) {
                        crit++;
                    } else {
                        good++;
                    }
                }
                if (!plant.getFertilize() && !plant.getWater()) {
                    if (Calendar.getInstance().get(Calendar.MINUTE) % 10 == 0) {
                        plant.setErtrag(plant.getErtrag() - Script.getRandom(1, 2));
                    }
                } else {
                    if (minutes >= 0 && Calendar.getInstance().get(Calendar.MINUTE) % 2 == 0) {
                        plant.setPurity(plant.getCurrentPurity() + Script.getRandom(3, 4));
                    }
                    if(minutes >= 0 && Calendar.getInstance().get(Calendar.MINUTE) % 5 == 0) {
                        plant.setErtrag(plant.getErtrag() + Script.getRandom(0, 2));
                    }
                }
                if (minutes >= 0 && plant.getLastFertilize() == 0 || (System.currentTimeMillis() - plant.getLastFertilize()) > TimeUnit.MINUTES.toMillis(Script.getRandom(60, 80))) {
                    plant.setFertilize(false);
                }
                if (minutes >= 0 && plant.getLastWater() == 0 || (System.currentTimeMillis() - plant.getLastWater()) > TimeUnit.MINUTES.toMillis(Script.getRandom(45, 60))) {
                    plant.setWater(false);
                }
            }
            if (sendMessage) {
                if (good == 1) {
                    f.sendMessage(PREFIX + "Eine Plantage befindet sich im Reifeprozess...");
                } else if (good > 1) {
                    f.sendMessage(PREFIX + good + " Plantagen befinden sich im Reifeprozess...");
                }
                if (bad == 1) {
                    f.sendMessage(PREFIX + "Eine Plantage verkommt...");
                } else if (bad > 1) {
                    f.sendMessage(PREFIX + bad + " Plantagen verkommen...");
                }
                if (crit == 1) {
                    f.sendMessage(PREFIX + "Eine Plantage muss gedüngt/gewässert werden.");
                } else if (crit > 1) {
                    f.sendMessage(PREFIX + crit + " Plantagen müssen gedüngt/gewässert werden.");
                }
            }
        }
        plants.clear();
    }

    public String toString() {
        return "{plantID: " + plantID + ", fraktion: " + fraktion + ", location: " + loc + ", type: " + type + ", time: " + time + ", ertrag: " + ertrag + ", fertilize: " + fertilize + ", water: " + water + ", last_fertilize: " + last_fertilize + ", last_water: " + last_water + ", purityCounter: " + purityCounter + "}";
    }

    public int getID() {
        return this.plantID;
    }

    public Organisation getOrganisation() {
        return this.fraktion;
    }

    public Location getLocation() {
        return this.loc;
    }

    public PlantageType getType() {
        return this.type;
    }

    public long getTime() {
        return this.time;
    }

    @Deprecated
    public void setTime(long time) {
        this.time = time;
        Script.executeAsyncUpdate("UPDATE plantage SET time = " + time + " WHERE plantID = " + getID());
    }

    public int getErtrag() {
        return this.ertrag;
    }

    public void setErtrag(int ertrag) {
        if (ertrag < 0) ertrag = 0;
        this.ertrag = ertrag;
        Script.executeAsyncUpdate("UPDATE plantage SET ertrag = " + ertrag + " WHERE plantID = " + getID());
    }

    public boolean getFertilize() {
        return this.fertilize;
    }

    public void setFertilize(boolean fertilize) {
        this.fertilize = fertilize;
        Script.executeAsyncUpdate("UPDATE plantage SET fertilize = " + fertilize + " WHERE plantID = " + getID());
    }

    public boolean getWater() {
        return this.water;
    }

    public void setWater(boolean water) {
        this.water = water;
        Script.executeAsyncUpdate("UPDATE plantage SET water = " + water + " WHERE plantID = " + getID());
    }

    public long getLastFertilize() {
        return this.last_fertilize;
    }

    public void setLastFertilize(long time) {
        this.last_fertilize = time;
        Script.executeAsyncUpdate("UPDATE plantage SET last_fertilize = " + time + " WHERE plantID = " + getID());
    }

    public long getLastWater() {
        return this.last_water;
    }

    public void setLastWater(long time) {
        this.last_water = time;
        Script.executeAsyncUpdate("UPDATE plantage SET last_water = " + time + " WHERE plantID = " + getID());
    }

    public int getCurrentPurity() {
        return this.purityCounter;
    }

    public void setPurity(int purity) {
        if (purity < 0) purity = 0;
        this.purityCounter = purity;
        Script.executeAsyncUpdate("UPDATE plantage SET purityCounter = " + purity + " WHERE plantID = " + getID());
    }

    @SuppressWarnings("deprecation")
    public void register() {
        PLANTAGEN.add(this);
        Bukkit.getScheduler().runTask(main.getInstance(), () -> {
            getLocation().getBlock().setType(getType().getMaterial());
            getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.DIRT);
        });
        Script.executeAsyncUpdate("INSERT INTO plantage (organisationID, x, y, z, drugID, time, ertrag, fertilize, water, last_fertilize, last_water, purityCounter) VALUES (" +
                getOrganisation().getID() + ", " + getLocation().getBlockX() + ", " + getLocation().getBlockY() + ", " + getLocation().getBlockZ() + ", " + getType().getItem().getID() + ", " +
                (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(getType().getTime())) + ", 0, false, false, 0, 0, 0);");
    }

    public void harvest(Player p) {
        Drogen.DrugPurity purity;
        int purityCounter = getCurrentPurity();

        if (purityCounter >= 190) {
            purity = Drogen.DrugPurity.HIGH;
        } else if (purityCounter >= 175) {
            purity = Drogen.DrugPurity.GOOD;
        } else if (purityCounter >= 160) {
            purity = Drogen.DrugPurity.MEDIUM;
        } else {
            purity = Drogen.DrugPurity.BAD;
        }

        for(int i = 0; i < getErtrag(); i++) {
            if(getType() == PlantageType.PULVER) p.getInventory().addItem(new ItemBuilder(Material.SUGAR).setName(getType().getName()).setLore("§7Reinheitsgrad: " + purity.getText()).build());
            else p.getInventory().addItem(new ItemBuilder(Material.GREEN_DYE).setName(getType().getName()).setLore("§7Reinheitsgrad: " + purity.getText()).build());
        }
        getLocation().getBlock().setType(Material.AIR);
        getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.DIRT);
        p.playSound(p.getLocation(), org.bukkit.Sound.ITEM_HOE_TILL, 1F, 1F);
        getOrganisation().addExp(Script.getRandom(10, 20));
        Script.addEXP(p, Script.getRandom(10, 20));
        Script.executeAsyncUpdate("DELETE FROM plantage WHERE plantID = " + this.plantID);
        PLANTAGEN.remove(this);
    }

    public void burn() {
        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> {
            getLocation().getBlock().setType(Material.FIRE);
            getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.DIRT);
        }, 50L);
        Bukkit.getScheduler().runTaskLater(main.getInstance(), () -> getLocation().getBlock().setType(Material.AIR), 24 * 20L);
        Script.executeAsyncUpdate("DELETE FROM plantage WHERE plantID = " + this.plantID);
        PLANTAGEN.remove(this);
    }

    public enum PlantageType {
        KRÄUTER(0, "Kräuter", Drogen.KRÄUTER, 120, Material.LARGE_FERN, (byte) 2),
        PULVER(1, "Pulver", Drogen.PULVER, 160, Material.LARGE_FERN, (byte) 2);

        private final int id;
        private final String name;
        private final Drogen item;
        private final int time;
        private final Material material;
        private final byte data;

        PlantageType(int id, String name, Drogen item, int time, Material material, byte data) {
            this.id = id;
            this.name = name;
            this.item = item;
            this.time = time;
            this.material = material;
            this.data = data;
        }

        public static PlantageType getTypeByID(int id) {
            for (PlantageType type : values()) {
                if (type.getID() == id) return type;
            }
            return null;
        }

        public static PlantageType getTypeByDrugID(int id) {
            for (PlantageType type : values()) {
                if (type.getItem().getID() == id) return type;
            }
            return null;
        }

        public int getID() {
            return this.id;
        }

        public Drogen getItem() {
            return this.item;
        }

        public String getName() {
            return this.name;
        }

        public int getTime() {
            return this.time;
        }

        public Material getMaterial() {
            return this.material;
        }

        public byte getData() {
            return this.data;
        }
    }
}
