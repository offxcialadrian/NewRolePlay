package de.newrp.Player;

import de.newrp.API.Debug;
import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Notruf implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§cNotruf§8] §c" + Messages.ARROW + "§7 ";
    public static HashMap<Player, Questions> questions = new HashMap<>();
    public static HashMap<String, String> answers = new HashMap<>();
    public static HashMap<Player, Location> call = new HashMap<>();
    public static ArrayList<Player> accepted = new ArrayList<>();

    public static void openGUI(Player p, Questions question) {
        int size = (int) Math.ceil(question.getAnswers().size() / 9.0) * 9;
        Inventory inv = Bukkit.createInventory(null, size, "§8[§cNotruf§8] §7" + question.getQuestion());
        int i = 0;
        for (Answers answer : Answers.values()) {
            if (answer.question == question) {
                inv.setItem(i, new ItemBuilder(Material.OAK_SIGN).setName(answer.answer).build());
                i++;
            }
        }
        p.openInventory(inv);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (questions.containsKey(p)) {
            openGUI(p, questions.get(p));
            return true;
        }

        questions.put(p, Questions.FRAGE1);
        openGUI(p, Questions.FRAGE1);
        Me.sendMessage(p, "wählt den Notruf auf seinem Handy.");

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().startsWith("§8[§cNotruf§8]")) {
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
                        } catch (NumberFormatException ex) {
                            verletzte = -1;
                        }

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

                        if (verletzte != 0 && straftat) {
                            Beruf.Berufe.POLICE.sendMessage(sb.toString());
                            for (Player police : Beruf.Berufe.POLICE.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                            Beruf.Berufe.RETTUNGSDIENST.sendMessage(sb.toString());
                            for (Player police : Beruf.Berufe.RETTUNGSDIENST.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                        } else if (verletzte > 0 || verletzte == -1) {
                            Beruf.Berufe.RETTUNGSDIENST.sendMessage(sb.toString());
                            for (Player police : Beruf.Berufe.RETTUNGSDIENST.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                        } else if (straftat) {
                            Beruf.Berufe.POLICE.sendMessage(sb.toString());
                            for (Player police : Beruf.Berufe.POLICE.getMembers()) {
                                Script.sendClickableMessage(police, PREFIX + "§6Notruf annehmen und Route anzeigen", "/acceptnotruf " + p.getName(), "Klicke hier um den Notruf anzunehmen.");
                            }
                        }

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

}
