package de.newrp.Player;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.AcceptNotruf;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.BlockNotruf;
import de.newrp.Berufe.Duty;
import de.newrp.Chat.Me;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
    public static HashMap<Player, Type> call3 = new HashMap<>();

    public static void openGUI(Player p, Questions question) {
        int size = (int) Math.ceil(question.getAnswers().size() / 9.0) * 9;
        Inventory inv = Bukkit.createInventory(null, size, "§8[§cNotruf§8] §7" + question.getQuestion());
        int i = 0;
        for (Answers answer : Answers.values()) {
            if (answer.question == question) {
                if(question.getID() == 2 && call3.get(p) != null && answer.getType() != call3.get(p) && answer.getType() != null) continue;
                inv.setItem(i, new ItemBuilder(Material.OAK_SIGN).setName(answer.answer).build());
                i++;
            }
        }
        Script.fillInv(inv);
        p.openInventory(inv);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onClick1(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("§8[§cNotruf§8] §7Polizei oder Rettungsdienst?")) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals("Polizei")) {
                call3.put(p, Type.POLICE);
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("Rettungsdienst")) {
                call3.put(p, Type.RETTUNG);
            }
            questions.put(p, Questions.FRAGE2);
            openGUI(p, Questions.FRAGE2);
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().startsWith("§8[§cNotruf§8] §7")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            for (Answers answer : Answers.values()) {
                if (answer.answer.equals(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()))) {
                    int questionID = answer.question.getID();
                    answers.put(p.getName() + questionID, answer.answer);
                    if (Questions.FRAGE2.getID() == questionID) {

                        if(call3.get(p) == null && (BlockNotruf.isBlocked(Beruf.Berufe.POLICE, p) || BlockNotruf.isBlocked(Beruf.Berufe.RETTUNGSDIENST, p))) {
                            p.sendMessage(PREFIX + "Deine Notrufe sind blockiert. Bitte wende dich an die Leitung.");
                            return;
                        } else if(call3.get(p) != null && call3.get(p) == Type.RETTUNG && BlockNotruf.isBlocked(Beruf.Berufe.RETTUNGSDIENST, p)) {
                            p.sendMessage(PREFIX + "Deine Notrufe sind blockiert. Bitte wende dich an die Leitung.");
                            return;
                        } else if(call3.get(p) != null && call3.get(p) == Type.POLICE && BlockNotruf.isBlocked(Beruf.Berufe.POLICE, p)) {
                            p.sendMessage(PREFIX + "Deine Notrufe sind blockiert. Bitte wende dich an die Leitung.");
                            return;
                        }

                        p.sendMessage(PREFIX + "Vielen Dank für Ihren Anruf. Die Polizei und/oder der Rettungsdienst werden sich umgehend um Ihr Anliegen kümmern.");
                        Me.sendMessage(p, "wählt den Notruf auf seinem Handy.");

                        StringBuilder sb = new StringBuilder();
                        sb.append(PREFIX + "§6Achtung! Ein Notruf von " + Script.getName(p) + " ist eingegangen.")
                                .append("\n")
                                .append(PREFIX + "§6Vorfall§8:§6 " + answers.get(p.getName() + Questions.FRAGE2.getID()))
                                .append("\n");


                        ArrayList<Beruf.Berufe> berufe = new ArrayList<>();
                        if (call3.get(p) == null) {
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
                        } else if (call3.get(p) == Type.RETTUNG) {
                            Beruf.Berufe.RETTUNGSDIENST.sendMessage(sb.toString());
                            Beruf.Berufe.RETTUNGSDIENST.sendMessage(getNearestPlayersString(Beruf.Berufe.RETTUNGSDIENST, p.getLocation()));
                            for (Player police : Beruf.Berufe.RETTUNGSDIENST.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                            berufe.add(Beruf.Berufe.RETTUNGSDIENST);
                        } else {
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

    public enum Type {
        POLICE(),
        RETTUNG();

    }

    public enum Questions {
        FRAGE1(1, "Polizei oder Rettungsdienst?"),
        FRAGE2(2, "Was ist passiert?");

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

        // frage 1

        FRAGE1_1(1, Questions.FRAGE1, null, "Polizei"),
        FRAGE1_2(2, Questions.FRAGE1, null,"Rettungsdienst"),

        // FRAGE 1

        FRAGE_1_MEDIC(1, Questions.FRAGE2, Type.RETTUNG, "Reanimation"),
        FRAGE_2_MEDIC(2, Questions.FRAGE2, Type.RETTUNG, "Verletzung"),
        FRAGE_3_MEDIC(3, Questions.FRAGE2, Type.RETTUNG, "Knochenbruch"),
        FRAGE_1_POLICE(4, Questions.FRAGE2, Type.POLICE, "Einbruch"),
        FRAGE_2_POLICE(5, Questions.FRAGE2, null, "Mord"),
        FRAGE_3_POLICE(6, Questions.FRAGE2, null, "Körperverletzung"),
        FRAGE_4_POLICE(7, Questions.FRAGE2, Type.POLICE, "Raubüberfall"),
        FRAGE_5_POLICE(8, Questions.FRAGE2, Type.POLICE, "Diebstahl"),
        SONSTIGES(9, Questions.FRAGE2, null, "Sonstiges");


        private final int id;
        private final Questions question;
        private final Type type;
        private final String answer;

        Answers(int id, Questions question, Type type, String answer) {
            this.id = id;
            this.question = question;
            this.type = type;
            this.answer = answer;
        }

        public String getAnswer() {
            return answer;
        }

        public Questions getQuestion() {
            return question;
        }

        public Type getType() {
            return type;
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
        return PREFIX + "§6Am nächsten sind: " + stringJoiner.toString();
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
