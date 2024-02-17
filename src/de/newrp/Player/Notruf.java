package de.newrp.Player;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.AcceptNotruf;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Chat.Me;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

public class Notruf implements Listener {

    public static final String PREFIX = "§8[§4Notruf§8] §c" + Messages.ARROW + "§7 ";
    public static HashMap<Player, Questions> questions = new HashMap<>();
    public static HashMap<String, String> answers = new HashMap<>();
    public static HashMap<Player, Location> call = new HashMap<>();
    public static HashMap<Player, List<Beruf.Berufe>> call2 = new HashMap<>();

    public static void openGUI(Player p, Questions question) {
        int size = (int) Math.ceil(question.getAnswers().size() / 9.0) * 9;
        Inventory inv = Bukkit.createInventory(null, size, "§8[§c112§8] §7" + question.getQuestion());
        int i = 0;
        for (Answers answer : Answers.values()) {
            if (answer.question == question) {
                inv.setItem(i, new ItemBuilder(Material.OAK_SIGN).setName(answer.answer).build());
                i++;
            }
        }
        p.openInventory(inv);
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().startsWith("§8[§c112§8]")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            for (Answers answer : Answers.values()) {
                if (answer.answer.equals(e.getCurrentItem().getItemMeta().getDisplayName())) {
                    int questionID = answer.question.getID();
                    answers.put(p.getName() + questionID, answer.answer);
                    if (Questions.FRAGE3.getID() == questionID) {
                        p.sendMessage(PREFIX + "Vielen Dank für Ihren Anruf. Die Polizei und/oder der Rettungsdienst werden sich umgehend um Ihr Anliegen kümmern.");
                        int verletzte = -1;
                        try {
                            verletzte = Integer.parseInt(answers.get(p.getName() + Questions.FRAGE2.getID()));
                        } catch (NumberFormatException ignored) {
                        }
                        Me.sendMessage(p, "wählt den Notruf auf seinem Handy.");

                        StringBuilder sb = new StringBuilder();
                        sb.append(PREFIX + "§6Achtung! Ein Notruf von " + Script.getName(p) + " ist eingegangen.")
                                .append("\n")
                                .append(PREFIX + "§6Vorfall§8:§6 " + answers.get(p.getName() + Questions.FRAGE1.getID()))
                                .append("\n")
                                .append(PREFIX + "§6Anzahl der Verletzten§8:§6 " + (verletzte == -1 ? "8 oder mehr" : verletzte))
                                .append("\n")
                                .append(PREFIX + "§6Straftat§8:§6 " + answers.get(p.getName() + Questions.FRAGE3.getID()))
                                .append("\n");


                        boolean straftat = answers.get(p.getName() + Questions.FRAGE3.getID()).equals("Ja");
                        ArrayList<Beruf.Berufe> berufe = new ArrayList<>();
                        if (verletzte != 0 && straftat) {
                            Beruf.Berufe.POLICE.sendMessage(sb.toString());
                            Beruf.Berufe.POLICE.sendMessage(getNearestPlayersString(Beruf.Berufe.POLICE, p.getLocation()));
                            for (Player police : Beruf.Berufe.POLICE.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                            Beruf.Berufe.RETTUNGSDIENST.sendMessage(sb.toString());
                            Beruf.Berufe.RETTUNGSDIENST.sendMessage(getNearestPlayersString(Beruf.Berufe.RETTUNGSDIENST, p.getLocation()));
                            for (Player police : Beruf.Berufe.RETTUNGSDIENST.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                            berufe.add(Beruf.Berufe.POLICE);
                            berufe.add(Beruf.Berufe.RETTUNGSDIENST);
                        } else if (verletzte > 0) {
                            Beruf.Berufe.RETTUNGSDIENST.sendMessage(sb.toString());
                            Beruf.Berufe.RETTUNGSDIENST.sendMessage(getNearestPlayersString(Beruf.Berufe.RETTUNGSDIENST, p.getLocation()));
                            for (Player police : Beruf.Berufe.RETTUNGSDIENST.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                            berufe.add(Beruf.Berufe.RETTUNGSDIENST);
                        } else if (straftat) {
                            Beruf.Berufe.POLICE.sendMessage(sb.toString());
                            Beruf.Berufe.POLICE.sendMessage(getNearestPlayersString(Beruf.Berufe.POLICE, p.getLocation()));
                            for (Player police : Beruf.Berufe.POLICE.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                            berufe.add(Beruf.Berufe.POLICE);
                        }

                        call2.put(p, berufe);
                        p.closeInventory();
                        questions.remove(p);
                        answers.remove(p.getName() + questionID);
                        call.put(p, p.getLocation());
                        return;
                    }
                    questions.put(p, Questions.values()[questionID]);
                    openGUI(p, Questions.values()[questionID]);
                }
            }
        }
    }

    public enum Questions {
        FRAGE1(1, "Was ist passiert?"),
        FRAGE2(2, "Wie viele Verletzte gibt es?"),
        FRAGE3(3, "Liegt eine Straftat vor?");

        private final int id;
        private final String question;

        Questions(int id, String question) {
            this.id = id;
            this.question = question;
        }

        public String getQuestion() {
            return question;
        }

        public int getID() {
            return id;
        }

        public List<Answers> getAnswers() {
            List<Answers> answers = new ArrayList<>();
            for(Answers answer : Answers.values()) {
                if(answer.question == this) {
                    answers.add(answer);
                }
            }
            return answers;
        }

    }

    public enum Answers {

        // FRAGE 1
        FRAGE1_1(1, Questions.FRAGE1, "Verkehrsunfall"),
        FRAGE1_2(2, Questions.FRAGE1, "Brand"),
        FRAGE1_3(3, Questions.FRAGE1, "Häusliche Gewalt"),
        FRAGE1_4(4, Questions.FRAGE1, "Körperverletzung"),
        FRAGE1_5(5, Questions.FRAGE1, "Überfall"),
        FRAGE1_6(6, Questions.FRAGE1, "Einbruch"),
        FRAGE1_7(7, Questions.FRAGE1, "Sonstiges"),

// FRAGE 2

        FRAGE2_1(8, Questions.FRAGE2, "0"),
        FRAGE2_2(9, Questions.FRAGE2, "1"),
        FRAGE2_3(10, Questions.FRAGE2, "2"),
        FRAGE2_4(11, Questions.FRAGE2, "3"),
        FRAGE2_5(12, Questions.FRAGE2, "4"),
        FRAGE2_6(13, Questions.FRAGE2, "5"),
        FRAGE2_7(14, Questions.FRAGE2, "6"),
        FRAGE2_8(15, Questions.FRAGE2, "7"),
        FRAGE2_9(16, Questions.FRAGE2, "8 oder mehr"),

// FRAGE 3

        FRAGE3_1(17, Questions.FRAGE3, "Ja"),
        FRAGE3_2(18, Questions.FRAGE3, "Nein");


        private final int id;
        private final Questions question;
        private final String answer;

        Answers(int id, Questions question, String answer) {
            this.id = id;
            this.question = question;
            this.answer = answer;
        }

        public String getAnswer() {
            return answer;
        }

        public Questions getQuestion() {
            return question;
        }

        public int getID() {
            return id;
        }
    }

    public String getNearestPlayersString(Beruf.Berufe b, Location loc) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Map.Entry<Player, Double> entry : getNearestPlayers(b, loc).entrySet()) {
            String name = Script.getName(entry.getKey());
            int distance = entry.getValue().intValue();

            stringJoiner.add(name + " (" + distance + "m)");
        }

        if(stringJoiner.toString().isEmpty()) {
            return PREFIX + "§6Es ist niemand in der Nähe.";
        }
        return PREFIX + "§6Am nächsten sind: " + stringJoiner.toString() + "";
    }

    public static Map<Player, Double> getNearestPlayers(Beruf.Berufe b, Location loc) {
        List<Player> players = new ArrayList<>();
        for (Player p : b.getMembers()) {
            if (!Duty.isInDuty(p)) continue;
            if (AFK.isAFK(p)) continue;
            if (AcceptNotruf.accept.containsKey(p)) continue;

            players.add(p);
        }

        Map<Player, Double> playerDistances = new HashMap<>(players.size());

        for (Player onlinePlayer : players) {
            playerDistances.put(onlinePlayer, onlinePlayer.getLocation().distance(loc));
        }

        playerDistances = playerDistances
                .entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));

        Map<Player, Double> nearestPlayers = new LinkedHashMap<>();

        int i = 0;
        for (Map.Entry<Player, Double> entry : playerDistances.entrySet()) {
            if (++i == 3) break;

            nearestPlayers.put(entry.getKey(), entry.getValue());
        }

        return nearestPlayers;
    }

}
