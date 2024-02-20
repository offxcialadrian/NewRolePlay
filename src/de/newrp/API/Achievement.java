package de.newrp.API;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import de.newrp.main;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

public enum Achievement {
    FIRST_JOIN(1, "Willkommen!", "§rEin neuer Bewohner!", 50, null, false),
    STATS(2, "Statistiken", "§rNutze §8/§6stats§r um dir deine Statistiken anzusehen", 10, "Du kannst dir deine Statistiken mit §8/§6stats ansehen.\nHier siehst du, wie lange du schon auf dem Server bist, wie viel Bargeld du hast und vieles mehr.\nBeachte, dass du deinen Kontostand nur an einem Bankautomaten sehen kannst.", false),
    NAVI(3, "Navi", "§rNutze §8/§6navi§r, um dir die Karte anzusehen", 10, "Du kannst dir die Karte mit §8/§6navi §ransehen.\nHier siehst du alle wichtigen Orte und kannst dich schnell zurechtfinden.", false),
    PERSONALAUSWEIS(4, "Personalausweis", "§rBeantrage dir einen Personalausweis in der Stadthalle.", 10, "Du hast nun einen Personalausweis. Dieser ist wichtig, um dich zu identifizieren.", false),
    WAHLEN(5, "Wahlen", "§rJeden 15. eines neuen Quartals finden Wahlen statt. Führe §8/§6regierung §raus, um mehr zu erfahren.", 10, "Jeden 15. eines neuen Quartals finden Wahlen statt. Die Regierung legt bspw. die Steuern fest und macht die Budgetplanung der Stadt.", false),
    SCHULE(6, "Gehe zur Schule", "§rGehe zur Schule und lerne etwas", 10, "Du kannst zur Schule gehen und lernen. Hier kannst du dein Wissen erweitern und deine GFB-Jobs leveln.", false),
    HEALTH(7, "Gesundheit", "§rNutze §8/§6health§r, um dir deine Gesundheit anzusehen", 10, "Du musst dringend auf deine Gesundheit achten!\nDu kannst deine Muskeln im Fitnessstudio steigern.\nAchte immer auf deinen Hunger und trinke genug Wasser.", false),
    TEAMSPEAK(8, "TeamSpeak Verbindung", "§rVerbinde dich mit unserem TeamSpeak-Server und folge den Anweisungenen.", 10, "Du hast dich nun mit dem TeamSpeak verbunden.", false),
    GFB_JOBS(9, "Führe einen GFB-Job aus", "§rSchaue im Navi nach einem GFB-Job und führe ihn aus.", 10, "GFB-Jobs sind der perfekte Einstieg darin Geld zu verdienen.\nArbeite hart und du wirst vielleicht einer der reichsten Spieler", false),
    BANKKONTO(10, "Bankkonto", "§rEröffne ein Bankkonto in der Staatsbank", 10, "Du hast nun ein Bankkonto, du kannst jetzt Geldautomaten nutzen uvm.\nDu verdienst nun auch Geld bei den GFB-Jobs.", false),
    BERUFE(11, "Berufe", "§rNeben den GFB-Jobs gibt es auch Berufe, welche dich festanstellen (Klicke dieses Achievement an).", 10, "Es gibt folgende Berufe:\nPolizei\nRegierung\nRettungsdienst\nNews\n\nDu kannst dich bei diesen Berufen im Forum bewerben.", true),
    ORGANISATIONEN(12, "Organisationen", "§rEs gibt verschiedene Organisationen, welche du beitreten kannst (Klicke dieses Achievement an).", 10, "Du kannst verschiedenen illegalen Organisationen beitreten. Diese sind ähnlich zu den Berufen, nur das diese gegen das System arbeiten und das Böse in der Stadt verbreiten.", true),
    HOTEL(13, "Hotel", "§rSolang du kein Haus besitzt oder keinen Mietvertrag hast, kannst du im Hotel schlafen.", 10, "Du bist nun im Hotel untergebracht. Du solltest aber bald ein Haus (zur Miete) finden.", false),
    HAUS(14, "Haus", "§rKaufe dir ein Haus oder schließe einen Mietvertrag ab.", 10, "Du hast nun ein Haus. Hier kannst du mit §8/§6akku §rdeinen Akku aufladen und vieles mehr.", false),
    UBAHN(15, "UBahn", "§rFahre mit der U-Bahn", 10, "Die U-Bahn ist ein schnelles Transportmittelm um von A nach B zu kommen.\nNutze sie, um schneller zu deinem Ziel zu gelangen.", false),
    EINKAUFEN(16, "Einkaufen", "§rKaufe etwas", 10, "Du warst nun in diesem Shop einkaufen.\nDu wirst immer wieder einkaufen müssen, um deine Bedürfnisse zu stillen.\nDu kannst später auch selbst Shops erwerben.", false),
    VOTEN(17, "Voten", "§rVotes sind für unseren Server von entscheidener Bedeutung. Nutze §8/§6vote", 50, "Vielen Dank für deinen Vote!\nDu kannst jeden Tag für uns Voten.", false),
    BACKUPCODE(18, "Backupcode", "§rErstelle dir einen Backupcode", 10, "Du hast nun einen Backupcode. Dieser ist wichtig, um deinen Account wiederherzustellen, falls du dein Passwort vergessen hast.", false),
    FORUM(19, "Forum", "§rVerbinde dich mit dem Forum", 10, "Du hast dich nun mit dem Forum verbunden.\nHier kannst du dich mit anderen Spielern austauschen und wichtige Informationen erhalten.", false),
    WAEHLER(20, "Wähler", "§rNimm an einer Wahl teil!", 50, null, false),
    SERVER_TEAM(21, "Serverteam", "§rWerde ein Mitglied des Serverteams!", 100, null, false),
    BETA_TESTER(22, "Beta-Tester", "§rWerde Early-Access Tester", 100, null, false),
    BERUF_JOIN(23, "Berufseinsteiger", "§rTrete einem Beruf bei.", 50, null, false),
    SHOP_OWNER(24, "Shopbesitzer", "§rKauf deinen eigenen Shop!", 50, null, false),
    HOUSE_RENT(25, "Hausmieter", "§rMiete dein erstes Haus!", 50, null, false),
    WAHL_GEWONNEN(26, "Wahlsieger", "§rGewinne eine Wahl", 500, null, false),
    REZEPT(27, "Rezept", "§rWerde Krank und lass dir ein Rezept im Krankenhaus ausstellen.", 50, null, false),
    TEAMJOIN(28, "Teammitglied", "§rTrete einem Team bei", 50, null, false),
    TEAMLEADER(29, "Teamleiter", "§rWerde Teamleiter", 50, null, false);




    private final int id;
    private final String text;
    private final String name;
    private final int exp;
    private final String explanation;
    private final boolean justExplained;

    Achievement(int id, String name, String text, int exp, String explanation, boolean justExplained) {
        this.id = id;
        this.text = text;
        this.name = name;
        this.exp = exp;
        this.explanation = explanation;
        this.justExplained = justExplained;
    }

    public static Achievement getAchievementByID(int id) {
        for (Achievement a : values()) {
            if (a.getID() == id) return a;
        }
        return null;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getText() {
        return this.text;
    }

    public int getExp() {
        return this.exp;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public boolean justExplained() {
        return this.justExplained;
    }

    public void grant(Player p) {
        int id = Script.getNRPID(p);
        LinkedHashMap<Achievement, Boolean> cache = getAchievements(id);
        boolean done = false;
        if (cache != null) {
            done = cache.get(this);
        }
        if (!done) {
            Title.sendTitle(p, 20, 100, 20, "§aAchievement freigeschaltet!");
            p.sendMessage("§8[§aAchievement§8] §6» Du hast das Achievement \"§6§l" + this.getText() + "§r§6\" freigeschaltet!");
            Script.addEXP(p, this.getExp());
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            Script.executeAsyncUpdate("INSERT INTO achievements (userID, achievementID, time, done) VALUES (" + id + ", " + this.getID() + ", " + System.currentTimeMillis() + ", TRUE);");
            if(this.getExplanation() != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage("§8[§aErklärung§8] §6» " + Achievement.this.getExplanation().replace("\n", "\n§8[§aErklärung§8] §6» "));
                    }
                }.runTaskLater(main.getInstance(), 10*20L);
            }
        }
    }

    public void grant(OfflinePlayer p) {
        int id = Script.getNRPID(p);
        LinkedHashMap<Achievement, Boolean> cache = getAchievements(id);
        boolean done = false;
        if (cache != null) {
            done = cache.get(this);
        }
        if (!done) {
            Script.executeAsyncUpdate("INSERT INTO achievements (userID, achievementID, time, done) VALUES (" + id + ", " + this.getID() + ", " + System.currentTimeMillis() + ", TRUE);");
            if(p.isOnline()) {
                Player player = p.getPlayer();
                Title.sendTitle(player, 20, 100, 20, "§aAchievement freigeschaltet!");
                player.sendMessage("§8[§aAchievement§8] §6» Du hast das Achievement \"§6§l" + this.getName() + "§r§6\" freigeschaltet!");
                Script.addEXP(player, this.getExp());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            } else {
                Script.addOfflineMessage(p, "§8[§aAchievement§8] §6» Du hast das Achievement \"§6§l" + this.getName() + "§r§6\" freigeschaltet!");
                Script.addEXP(Script.getNRPID(p), this.getExp());
            }
        }
    }

    public static LinkedHashMap<Achievement, Boolean> getAchievements(int id) {
        LinkedHashMap<Achievement, Boolean> map = new LinkedHashMap<>();
        Achievement[] all = Achievement.values();

        for (Achievement a : all) {
            map.put(a, false);
        }
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT achievementID FROM achievements WHERE userID=" + id + " AND done=TRUE")) {
            while (rs.next()) {
                Achievement a = Achievement.getAchievementByID(rs.getInt("achievementID"));
                if (a != null) {
                    map.put(a, true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Achievement getAchievementByName(String name) {
        for (Achievement a : values()) {
            if (a.getName().equalsIgnoreCase(name)) return a;
        }
        return null;
    }
}
