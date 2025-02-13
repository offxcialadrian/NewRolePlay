package de.newrp.Organisationen;

import de.newrp.API.*;
import de.newrp.Chat.Me;
import de.newrp.Gangwar.GangwarCommand;
import de.newrp.Police.Handschellen;
import de.newrp.NewRoleplayMain;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.addiction.IAddictionService;
import de.newrp.features.addiction.data.AddictionLevel;
import de.newrp.features.bizwar.IBizWarService;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public enum Drogen {

    /*KOKAIN*/PULVER(0, true, "Pulver", new String[]{"Koks", "Kokain", "Pulver"}, null, "g", true, Material.SUGAR, 400),
    /*MARIHUANA*/KRÄUTER(1, true, "Kräuter", new String[]{"Kräuter", "Kraut", "Marihuana", "Gras", "Weed", "Hanf", "Ott"}, null, "g", true, Material.GREEN_DYE, 400),
    /*METHAMPHETAMIN*/KRISTALLE(2, true, "Kristalle", new String[]{"Kristalle", "Kristall", "Methamphetamin", "Meth", "Speed", "Chystal"}, null, "g", true, null, 100),
    /*MDMA*/ECSTASY(3, true, "Exiyty", new String[]{"Ecstasy", "XTC", "Pille", "Tablette"}, null, " Pillen", true, Material.WARPED_BUTTON, 100),
    ANTIBIOTIKA(6, false, "Antibiotika", null, DrugPurity.HIGH, " Päckchen", true, null, 0);
    //SCHWARZPULVER(8, false, "Schwarzpulver", null, DrugPurity.HIGH, " Kisten", false, null),
    //EISEN(9, false, "Eisen", null, DrugPurity.HIGH, " Stück", false, null);

    private final int id;
    private final boolean drug;
    private final String name;
    private final String[] alternativeNames;
    private final DrugPurity defaultPurity;
    private final String suffix;
    private final boolean consumable;
    private final Material material;
    @Getter
    private final int addictionChance;



    public static HashMap<String, Integer> taskID = new HashMap<>();
    public static HashMap<String, Drogen> test  = new HashMap<>();
    Drogen(int id, boolean drug, String name, String[] alternativeNames, DrugPurity defaultPurity, String suffix, boolean consumable, Material material, int addictionChance) {
        this.id = id;
        this.drug = drug;
        this.name = name;
        this.alternativeNames = alternativeNames;
        this.defaultPurity = defaultPurity;
        this.suffix = suffix;
        this.consumable = consumable;
        this.material = material;
        this.addictionChance = addictionChance;
    }

    public static Drogen getItemByID(int id) {
        for (Drogen i : values()) {
            if (i.getID() == id) return i;
        }
        return null;
    }

    public static Drogen getItemByName(String name) {
        for (Drogen i : values()) {
            if (i.getName().equalsIgnoreCase(name)) {
                return i;
            } else {
                String[] alt = i.getAlternativeNames();
                if (alt == null || alt.length == 0) continue;
                for (String s : alt) {
                    if (s.equalsIgnoreCase(name)) return i;
                }
            }
        }
        return null;
    }

    public int getID() {
        return this.id;
    }

    public boolean isDrug() {
        return this.drug;
    }

    public String[] getAlternativeNames() {
        return this.alternativeNames;
    }

    public String getName() {
        return this.name;
    }

    public DrugPurity getDefaultPurity() {
        return this.defaultPurity;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public boolean isConsumable() {
        return this.consumable;
    }

    public Material getMaterial() {
        return material;
    }

    public static int getAddiction(Player p) {
        int i = 0;
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM drug_addiction WHERE nrp_id='" + Script.getNRPID(p) + "' AND heal = false")) {
            while (rs.next()) {
                if (rs.getLong("time") + TimeUnit.HOURS.toMillis(12) > System.currentTimeMillis()) {
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static int getAddictionHeal(Player p) {
        int i = 0;
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM drug_addiction WHERE nrp_id='" + Script.getNRPID(p) + "' AND heal = true")) {
            while (rs.next()) {
                if (rs.getLong("time") + TimeUnit.DAYS.toMillis(5) > System.currentTimeMillis()) {
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static void addToAdiction(Player p) {
        if(DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class).isInDeathmatch(p.getPlayer(), false)) return;
        if(GangwarCommand.isInGangwar(p)) return;
        Script.executeAsyncUpdate("INSERT INTO drug_addiction (nrp_id, time, heal) VALUES (" + Script.getNRPID(p) + ", " + System.currentTimeMillis() + ", false)");
        if(getAddiction(p) >= (Premium.hasPremium(p)?Script.getRandom(40, 50):Script.getRandom(30, 40)) && !Krankheit.ABHAENGIGKEIT.isInfected(Script.getNRPID(p))) {
            Krankheit.ABHAENGIGKEIT.add(Script.getNRPID(p));
            p.sendMessage(Messages.INFO + "Du hast eine Abhängigkeit entwickelt. Lasse dich von einem Arzt behandeln.");
        }
    }

    public static void healAddiction(Player p) {
        if(Premium.hasPremium(p)) {
            Script.executeAsyncUpdate("DELETE FROM drug_addiction WHERE nrp_id = " + Script.getNRPID(p));
            Krankheit.ABHAENGIGKEIT.remove(Script.getNRPID(p));
            return;
        }
        if(getAddictionHeal(p) < 2) {
            Script.executeAsyncUpdate("INSERT INTO drug_addiction (nrp_id, time, heal) VALUES (" + Script.getNRPID(p) + ", " + System.currentTimeMillis() + ", true)");
        } else {
            Script.executeAsyncUpdate("DELETE FROM drug_addiction WHERE nrp_id = " + Script.getNRPID(p));
            Krankheit.ABHAENGIGKEIT.remove(Script.getNRPID(p));
        }
    }

    public static Map<UUID, Drogen> lastDrug = new HashMap<>();
    public static Map<UUID, Long> lastUse = new HashMap<>();

    public void consume(Player p, DrugPurity purity) {
        int id = Script.getNRPID(p);
        if (Handschellen.isCuffed(p)) {
            p.sendMessage(Messages.ERROR + "Du bist gefesselt.");
            return;
        }
        if (isConsumable()) {
            Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
            Me.sendMessage(p, "konsumiert " + this.getName() + ".");
        }

        lastDrug.put(p.getUniqueId(), this);
        lastUse.put(p.getUniqueId(), System.currentTimeMillis());

        if(test.containsKey(p.getName())) {
            test.remove(p.getName());
            Bukkit.getScheduler().cancelTask(taskID.get(p.getName()));
            taskID.remove(p.getName());
        }

        int task = Bukkit.getScheduler().scheduleSyncDelayedTask(NewRoleplayMain.getInstance(), () -> {
            if(!test.containsKey(p.getName())) return;
            test.remove(p.getName());
            taskID.remove(p.getName());
        }, 30*60*20L);

        test.put(p.getName(), this);
        taskID.put(p.getName(), task);
        final IAddictionService addictionService = DependencyContainer.getContainer().getDependency(IAddictionService.class);

        if(!DependencyContainer.getContainer().getDependency(IBizWarService.class).isMemberOfBizWar(p)) {
            if(!GangwarCommand.isInGangwar(p) && !DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class).isInDeathmatch(p.getPlayer(), false)) {
                if(addictionService.getAddictionLevel(p, this).getAddictionLevel() == AddictionLevel.FULLY_ADDICTED) {
                    p.sendMessage(Messages.ERROR + "Du bist abhängig von " + this.getName() + ".");
                    return;
                }
            }
        }

        addictionService.evaluteDrugUse(p.getPlayer(), this);

        switch (this) {
            case ECSTASY:
                if(!p.hasPotionEffect(PotionEffectType.ABSORPTION)) p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 300 * 20, 7 - purity.getID(), false, false));
                if (purity.getID() >= 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * (5 + purity.getID()), 0, false, false));
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2 * 20 * (8 - 2 * purity.getID()), 2, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30 * (10 - purity.getID()), 1, false, false));
                break;

            case PULVER:
                if(!p.hasPotionEffect(PotionEffectType.ABSORPTION)) p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 220 * 20, 4 - purity.getID(), false, false));
                if (purity.getID() >= 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * (5 + purity.getID()), 0, false, false));
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2 * 20 * (8 - 2 * purity.getID()), 0, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (10 - purity.getID()), 1, false, false));
                break;
            case KRÄUTER:
                if (purity.getID() > 0) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 15 * purity.getID(), 0, false, false));
                }

                if (purity.getID() >= 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * (5 + purity.getID()), 0, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * (15 + purity.getID()), 0, false, false));
                }

                if(!p.hasPotionEffect(PotionEffectType.ABSORPTION)) p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 270 * 20 ,4 - purity.getID(), false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2 * 20 * (14 - 2 * purity.getID()), 1, false, false));
                break;
            case KRISTALLE:
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 250 * 20, 4 - purity.getID(), false, false));
                if (purity.getID() >= 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * (3 + purity.getID()), 0, false, false));
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * (8 - 2 * purity.getID()), 1, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (10 - purity.getID()), 1, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * (30 - purity.getID() * 5), 0, false, false));
                break;
            /*case SCHWARZPULVER:
            case EISEN:
                p.sendMessage("§7Du kannst dieses Item nicht konsumieren.");
                break;*/
        }
    }


    public enum DrugPurity {
        HIGH(0, "Höchste Reinheit"),
        GOOD(1, "Gute Reinheit"),
        MEDIUM(2, "Mittlere Reinheit"),
        BAD(3, "Schlechte Reinheit");

        private final int id;
        private final String text;

        DrugPurity(int id, String text) {
            this.id = id;
            this.text = text;
        }

        public static DrugPurity getPurityByID(int id) {
            for (DrugPurity all : values()) {
                if (all.getID() == id) return all;
            }
            return null;
        }

        public static DrugPurity getPurityByName(String name) {
            for (DrugPurity all : values()) {
                if (all.getText().equals(name)) return all;
            }
            return null;
        }

        public int getID() {
            return this.id;
        }

        public String getText() {
            return this.text;
        }
    }

    public enum Trip {
        BLUE(1, 228 * 1000, new int[]{238, 246, 244, 247}),
        RED(2, 173 * 1000, new int[]{237, 241, 249, 250}),
        GREEN(3, 183 * 1000, new int[]{236, 239, 240, 248});
		/*BLUE(1, 228*1000, new int[]{3, 9, 10, 11}),
		RED(2, 173*1000, new int[]{1, 2, 6, 14}),
		GREEN(3, 183*1000, new int[]{4, 5, 8, 13});*/

        private final int id;
        private final int duration;
        private final int[] mat;

        Trip(int id, int duration, int[] mat) {
            this.id = id;
            this.duration = duration;
            this.mat = mat;
        }

        public int getID() {
            return this.id;
        }

        public int getDuration() {
            return this.duration;
        }

        public int[] getMaterial() {
            return this.mat;
        }
    }

}
