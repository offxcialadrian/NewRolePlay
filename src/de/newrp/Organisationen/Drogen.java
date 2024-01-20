package de.newrp.Organisationen;

import de.newrp.API.Debug;
import de.newrp.API.Krankheit;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Equip;
import de.newrp.Chat.Me;
import de.newrp.Police.Handschellen;
import de.newrp.main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public enum Drogen {

    /*KOKAIN*/PULVER(0, true, "Pulver", new String[]{"Koks", "Kokain", "Pulver"}, null, "g", true),
    /*MARIHUANA*/KRÄUTER(1, true, "Kräuter", new String[]{"Kräuter", "Kraut", "Marihuana", "Gras", "Weed", "Hanf", "Ott"}, null, "g", true),
    /*METHAMPHETAMIN*/KRISTALLE(2, true, "Kristalle", new String[]{"Kristalle", "Kristall", "Methamphetamin", "Meth", "Speed", "Chystal"}, null, "g", true),
    ANTIBIOTIKA(6, false, "Antibiotika", null, DrugPurity.HIGH, " Päckchen", true),
    SCHWARZPULVER(8, false, "Schwarzpulver", null, DrugPurity.HIGH, " Kisten", false),
    EISEN(9, false, "Eisen", null, DrugPurity.HIGH, " Stück", false);

    private final int id;
    private final boolean drug;
    private final String name;
    private final String[] alternativeNames;
    private final DrugPurity defaultPurity;
    private final String suffix;
    private final boolean consumable;


    Drogen(int id, boolean drug, String name, String[] alternativeNames, DrugPurity defaultPurity, String suffix, boolean consumable) {
        this.id = id;
        this.drug = drug;
        this.name = name;
        this.alternativeNames = alternativeNames;
        this.defaultPurity = defaultPurity;
        this.suffix = suffix;
        this.consumable = consumable;
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

    public static int getAddiction(Player p) {
        int i = 0;
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM drug_addiction WHERE nrp_id='" + Script.getNRPID(p) + "' AND heal = false")) {
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

    public static int getAddictionHeal(Player p) {
        int i = 0;
        try (Statement stmt = main.getConnection().createStatement();
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
        Script.executeAsyncUpdate("INSERT INTO drug_addiction (nrp_id, time, heal) VALUES (" + Script.getNRPID(p) + ", " + System.currentTimeMillis() + ", false)");
        if(getAddiction(p) >= Script.getRandom(20, 30) && !Krankheit.ABHAENGIGKEIT.isInfected(Script.getNRPID(p))) {
            Krankheit.ABHAENGIGKEIT.add(Script.getNRPID(p));
            p.sendMessage(Messages.INFO + "Du hast eine Abhängigkeit entwickelt. Lasse dich von einem Arzt behandeln.");
        }
    }

    public static void healAddiction(Player p) {
        if(getAddictionHeal(p) != 3) {
            Script.executeAsyncUpdate("INSERT INTO drug_addiction (nrp_id, time, heal) VALUES (" + Script.getNRPID(p) + ", " + System.currentTimeMillis() + ", true)");
        } else {
            Script.executeAsyncUpdate("DELETE FROM drug_addiction WHERE nrp_id = " + Script.getNRPID(p));
            Krankheit.ABHAENGIGKEIT.remove(Script.getNRPID(p));
        }
    }

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

        if(Krankheit.ABHAENGIGKEIT.isInfected(id)) {
            p.sendMessage(Messages.INFO + "Du hast eine Abhängigkeit entwickelt. Das Konsumieren hat keine Wirkung gezeigt.");
            return;
        }

        Drogen.addToAdiction(p);

        switch (this) {
            case PULVER:
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 220 * 20, 4 - purity.getID(), false, false));
                if (purity.getID() >= 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * (5 + purity.getID()), 0, false, false));
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2 * 20 * (8 - 2 * purity.getID()), 0, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (10 - purity.getID()), 2, false, false));
                break;
            case KRÄUTER:
                if (purity.getID() > 0) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 15 * purity.getID(), 0, false, false));
                }

                if (purity.getID() >= 2) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * (5 + purity.getID()), 0, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * (15 + purity.getID()), 0, false, false));
                }

                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 270 * 20 ,4 - purity.getID(), false, false));
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
            case SCHWARZPULVER:
            case EISEN:
                p.sendMessage("§7Du kannst dieses Item nicht konsumieren.");
                break;
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
