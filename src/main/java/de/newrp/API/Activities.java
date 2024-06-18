package de.newrp.API;

import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter()
@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public enum Activities {

    REMOVE("Abzug", -1.0F, Arrays.asList(0, 1, 2, 3, 4)),
    ARREST("Verhaftung", 0.4F, Arrays.asList(3)),
    WPKILL("Tötung", 0.1F, Arrays.asList(3)),
    STRAFZETTEL("Strafzettel", 0.6F, Arrays.asList(3)),
    REZEPT("Rezept", 0.1F, Arrays.asList(4)),
    REVIVE("Belebung", 0.2F, Arrays.asList(4)),
    GIPS("Gips", 0.9F, Arrays.asList(4)),
    BANDAGE( "Bandage", 0.1F, Arrays.asList(4)),
    NEWS("News", 0.5F, Arrays.asList(2)),
    ROLEPLAY("Roleplay", 1.0F, Arrays.asList(0, 1, 2, 3, 4)),
    GRAFFITI("Graffiti", 0.05F, Arrays.asList(0)),
    ATM("Sprengung", 0.5F, Arrays.asList(0, 3)),
    COPKILL("Cop-Mord", 0.1F, Arrays.asList(0)),
    BLKILL("Vergeltung", 0.2F, Arrays.asList(0)),
    BREAKIN("Break-In", 0.4F, Arrays.asList(0, 3)),
    UEBERFALL("Überfall", 0.5F, Arrays.asList(0, 3)),
    LABOR("Labor", 0.6F, Arrays.asList(0, 3)),
    ROB("Raub", 0.2F, Arrays.asList(0)),
    EINBRUCH("Einbruch", 0.3F, Arrays.asList(0, 3)),
    PFANDNAHME("Pfandnahme", 2.0F, Arrays.asList(0, 3, 4)),
    PLANTAGE("Plantage", 0.1F, Arrays.asList(0, 3)),
    DROGEN("Drogen", 0.01F, Arrays.asList(0)),
    GELD("Geld", 0.05F, Arrays.asList(0, 1, 2, 3, 4)),
    BANKRAUB("Bankraub", 1.8F, Arrays.asList(0, 3)),
    GANGWAR("Gangwar", 1.5F, Arrays.asList(0)),
    AUSRAUB("Ausraub", 0.4F, Arrays.asList(0)),
    GROSSAKTI("Großaktivität", 1.0F, Arrays.asList(0, 1, 2, 3, 4)),
    BLACKLIST("Blacklist", 0.6F, Arrays.asList(0)),
    MARRY("Hochzeit", 0.8F, Arrays.asList(1)),
    ARBEITSLOSENGELD("Arbeitslosengeld", 0.4F, Arrays.asList(1)),
    REPORTAGE("Reportage", 1.5F, Arrays.asList(2)),
    SHOW("Show", 2.0F, Arrays.asList(2)),
    VERKAUF("Verkauf", 1.0F, Arrays.asList(1)),
    BRAND("Brand", 0.9F, Arrays.asList(4)),
    MELDUNG("Meldung", 0.6F, Arrays.asList(1, 3)),
    BEHANDLUNG("Behandlung", 0.7F, Arrays.asList(4)),
    FLUGBLATT("Flugblatt", 0.4F, Arrays.asList(2)),
    ZEITUNG("Zeitung", 3.0F, Arrays.asList(2)),
    BUSSGELD("Bußgeld", 0.1F, Arrays.asList(3)),
    CONTRACT("Auftrag", 0.3F, Arrays.asList(-3)),
    CT_KILL("Kopfgeld", 0.6F, Arrays.asList(-3)),
    NOTRUF("Notruf", 0.4F, Arrays.asList(3, 4)),
    IMPFUNG("Impfung", 0.4F, Arrays.asList(4)),
    BIZWAR("BizWar", 0.3F, Arrays.asList(0)),
    CASINO("Casino", 0.02F, Arrays.asList(-1));

    // 0 = Alle Organisationen
    // Negative ID = Orga-ID

    public static final HashMap<Integer, List<Activities>> disabled = new HashMap<>();

    public final String name;
    public final float points;
    public final List<Integer> user;

    Activities(String name, float points, List<Integer> user) {
        this.name = name;
        this.points = points;
        this.user = user;
    }

    public static void loadDisabled() {
        for (Beruf.Berufe beruf : Beruf.Berufe.values()) {
            switch (beruf) {
                case GOVERNMENT:
                    disabled.put(beruf.getID(), Arrays.asList());
                case NEWS:
                    disabled.put(beruf.getID(), Arrays.asList());
                case POLICE:
                    disabled.put(beruf.getID(), Arrays.asList());
                case RETTUNGSDIENST:
                    disabled.put(beruf.getID(), Arrays.asList(BANDAGE, REZEPT, GIPS));
                default:
            }
        }
        for (Organisation orga : Organisation.values()) {
            switch (orga) {
                case FALCONE:
                    disabled.put(-orga.getID(), Arrays.asList());
                case TRIORLA:
                    disabled.put(-orga.getID(), Arrays.asList());
                case HITMEN:
                    disabled.put(-orga.getID(), Arrays.asList());
                case CORLEONE:
                    disabled.put(-orga.getID(), Arrays.asList());
                default:
            }
        }
    }

    public static List<Activities> getDisabled(int id) {
        return disabled.get(id);
    }

    public static boolean isDisabled(int id, Activity activity) {
        return getDisabled(id).contains(getActivity(activity.getName()));
    }

    public static List<String> getCompletions(int id) {
        List<String> list = new ArrayList<>();
        for (Activities activity : Activities.values()) {
            if ((activity.getUser().contains(id) || (id <= 0 && activity.getUser().contains(0)))) {
                list.add(activity.getName());
            }
        }
        return list;
    }

    public static Activities getActivity(String name) {
        for (Activities activity : Activities.values()) {
            if (activity.getName().equalsIgnoreCase(name)) return activity;
        }
        return null;
    }
}
