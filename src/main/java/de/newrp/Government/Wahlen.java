package de.newrp.Government;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.News.NewsCommand;
import de.newrp.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Wahlen implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§6Wahlen§8] " + Messages.ARROW + " §6";
    public static boolean extend = false;
    public static boolean alreadyExtended = false;
    public static boolean neuWahlen = false;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("rücktritt") || args[0].equalsIgnoreCase("zurücktreten")) {

                if (!hasApplied(p)) {
                    p.sendMessage(PREFIX + "Du hast dich nicht aufgestellt.");
                    return true;
                }

                Script.executeAsyncUpdate("DELETE FROM wahlen WHERE nrp_id = '" + Script.getNRPID(p) + "' AND quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'");
                p.sendMessage(PREFIX + "Du hast dich erfolgreich zurückgetreten.");
                Script.sendTeamMessage(PREFIX + "Der Spieler " + Script.getName(p) + " ist zurückgetreten.");

            } else if (args[0].equalsIgnoreCase("result")) {
                if (!wahlenActive()) {
                    p.sendMessage(PREFIX + "Es sind derzeit keine Wahlen aktiv.");
                    return true;
                }

                if (Beruf.getBeruf(p) == Beruf.Berufe.NEWS && !SDuty.isSDuty(p)) {
                    p.sendMessage(PREFIX + "Aktuelle Hochrechnungen:");
                    try (Statement stmt = Main.getConnection().createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'")) {
                        if (rs.next()) {
                            do {
                                p.sendMessage("§8» §6" + Script.getOfflinePlayer(rs.getInt("nrp_id")).getName() + " §8× §6" + Script.manipulateInt(rs.getInt("votings")) + " Stimmen");
                            } while (rs.next());
                        } else {
                            p.sendMessage(PREFIX + "Es gibt derzeit keine Kandidaten.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }


                if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                if (!SDuty.isSDuty(p)) {
                    p.sendMessage(Messages.NO_SDUTY);
                    return true;
                }

                getWahlResult();
                return true;
            }

            if (args[0].equalsIgnoreCase("neuwahlen")) {
                if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                if (!SDuty.isSDuty(p)) {
                    p.sendMessage(Messages.NO_SDUTY);
                    return true;
                }

                Script.executeAsyncUpdate("UPDATE wahlen SET votings = 0 WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'");
                Script.executeUpdate("DELETE FROM votes WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'");

                neuWahlen = true;
                p.sendMessage(PREFIX + "Du hast die Neuwahlen aktiviert.");
                Bukkit.broadcastMessage(PREFIX + "Es wurden Neuwahlen ausgerufen und die Wahllokale haben mit sofortiger Wirkung geöffnet!");
                return true;

            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                if (!SDuty.isSDuty(p)) {
                    p.sendMessage(Messages.NO_SDUTY);
                    return true;
                }

                OfflinePlayer tg = Script.getPlayer(args[1]);
                if (tg == null) {
                    p.sendMessage(Messages.PLAYER_NOT_FOUND);
                    return true;
                }

                if (hasApplied(tg)) {
                    p.sendMessage(PREFIX + "Der Spieler hat sich bereits aufgestellt.");
                    return true;
                }

                if (getWahlApplications() >= 54) {
                    p.sendMessage(PREFIX + "Es können maximal 54 Spieler aufgestellt werden.");
                    return true;
                }

                addToWahlen(tg);
                p.sendMessage(PREFIX + "Du hast " + tg.getName() + " erfolgreich aufgestellt.");
                Script.sendTeamMessage(p, ChatColor.GOLD, "hat " + tg.getName() + " zur Wahl aufgestellt.", true);
                if (tg.isOnline()) {
                    tg.getPlayer().sendMessage(PREFIX + "Du wurdest von " + Messages.RANK_PREFIX(p) + " zur Wahl aufgestellt.");
                } else {
                    Script.addOfflineMessage(tg, PREFIX + "Du wurdest von " + Messages.RANK_PREFIX(p) + " zur Wahl aufgestellt.");
                }
                return true;

            }
        }

        if (!wahlenActive()) {
            p.sendMessage(PREFIX + "Es sind derzeit keine Wahlen aktiv.");
            return true;
        }

        if (p.getLocation().distance(new Location(Script.WORLD, 545, 70, 1014, 0.46572876f, 14.078306f)) > 10) {
            p.sendMessage(Messages.ERROR + "Du befindest dich nicht an einer Wahlurne.");
            return true;
        }

        sendWahlGUI(p);
        return false;
    }

    public static boolean wahlenActive() {
        if (neuWahlen) return true;
        if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JANUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) {
            return true;
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JANUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 20 && extend)) {
            return true;
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) {
            return true;
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 20 && extend)) {
            return true;
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JULY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) {
            return true;
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JULY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 20 && extend)) {
            return true;
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18) {
            return true;
        } else
            return Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 20 && extend);
    }

    public static void getWahlResult() {
        NewsCommand.wahlenNewsActive = true;
        if (getWinner() == -1 && !alreadyExtended) {
            Beruf.Berufe.NEWS.sendMessage(PREFIX + "Es gibt keinen Gewinner. Die Wahlen werden bis 20 Uhr verlängert.");
            return;
        }

        if (getWinner() == -1 && alreadyExtended) {
            Beruf.Berufe.NEWS.sendMessage(PREFIX + "Es konnte immer noch kein Gewinner ermittelt werden. Die Legislaturperiode der aktuellen Regierung wird um 1 Quartal verlängert.");
            return;
        }

        if (getWinner() == -1 && getWahlApplications() == 0) {
            Beruf.Berufe.NEWS.sendMessage(PREFIX + "Es gibt keine Kandidaten. Die Legislaturperiode der aktuellen Regierung wird um 1 Quartal verlängert.");
        }

        Beruf.Berufe.NEWS.sendMessage(PREFIX + Script.getPlayer(getWinner()).getName() + " hat die Wahlen gewonnen.");
        Beruf.Berufe.NEWS.sendMessage(PREFIX + "Die Nächsten Wahlen finden am §e" + getNextElection() + "§6 statt.");

        Beruf.Berufe.NEWS.sendMessage(PREFIX + "Das Ergebnis der Wahlen:");
        for (OfflinePlayer p : getPlayers()) {
            if (hasApplied(p)) {
                Beruf.Berufe.NEWS.sendMessage(PREFIX + "§6" + p.getName() + " §8× §6" + getVotes(p) + " Stimmen (" + Script.getPercentage(getVotes(p), getTotalVotes()) + "%)");
            }
        }

        neuWahlen = false;
        new BukkitRunnable() {

            @Override
            public void run() {
                OfflinePlayer winner = Script.getOfflinePlayer(getWinner());
                if (!NewsCommand.wahlenNews) {
                    Bukkit.broadcastMessage(NewsCommand.NEWS + winner.getName() + " hat die Wahlen mit " + getVotes(winner) + " Stimmen gewonnen!");
                    Bukkit.broadcastMessage(NewsCommand.NEWS + "Die Nächsten Wahlen finden am " + getNextElection() + " statt!");
                    Beruf.Berufe.NEWS.sendMessage(PREFIX + "Die News hat es nicht rechtzeitig geschafft das Ergebnis der Wahlen zu verkünden. Es wurde automatisch eine Meldung abgegeben.");
                    Script.sendTeamMessage(PREFIX + "Die News hat es nicht rechtzeitig geschafft das Ergebnis der Wahlen zu verkünden.");
                }

                if (Beruf.getBeruf(winner) != Beruf.Berufe.GOVERNMENT || !Beruf.isLeader(winner, false)) {
                    for (Player all : Beruf.Berufe.GOVERNMENT.getMembers()) {
                        all.sendMessage(PREFIX + "Die Legislaturperiode der aktuellen Regierung ist abgelaufen. Du bist nun nicht mehr in der Regierung.");
                    }

                    Script.executeUpdate("DELETE FROM berufe WHERE berufID = '" + Beruf.Berufe.GOVERNMENT.getID() + "'");
                }

                NewsCommand.wahlenNews = false;
                NewsCommand.wahlenNewsActive = false;
                if (Beruf.hasBeruf(winner) && Beruf.getBeruf(winner) != Beruf.Berufe.GOVERNMENT)
                    Beruf.getBeruf(winner).removeMember(winner);
                if (Beruf.getBeruf(winner) != Beruf.Berufe.GOVERNMENT) Beruf.Berufe.GOVERNMENT.addMember(winner);
                Beruf.setLeader(winner, true);
                Achievement.WAHL_GEWONNEN.grant(winner);
                if (Script.getPlayer(winner.getName()) != null)
                    Script.getPlayer(winner.getName()).sendMessage(Messages.INFO + "Herzlichen Glückwunsch! Du hast die Wahlen gewonnen! Du hast hiermit deine Rechte erhalten. Solltest du Hilfe benötigen, steht das Server-Team dir jederzeit zur Verfügung.");


            }
        }.runTaskLater(Main.getInstance(), 20L * 60 * 10);
    }

    public static String getNextElection() {
        Calendar nextElectionDate = Calendar.getInstance();

        int nextQuartal = getNextQuartal();
        if (nextQuartal == 1) {
            nextElectionDate.set(Calendar.MONTH, Calendar.JANUARY);
            nextElectionDate.set(Calendar.DAY_OF_MONTH, 15);
        } else if (nextQuartal == 2) {
            nextElectionDate.set(Calendar.MONTH, Calendar.APRIL);
            nextElectionDate.set(Calendar.DAY_OF_MONTH, 15);
        } else if (nextQuartal == 3) {
            nextElectionDate.set(Calendar.MONTH, Calendar.JULY);
            nextElectionDate.set(Calendar.DAY_OF_MONTH, 15);
        } else {
            nextElectionDate.set(Calendar.MONTH, Calendar.OCTOBER);
            nextElectionDate.set(Calendar.DAY_OF_MONTH, 15);
        }

        // Format the date
        return new SimpleDateFormat("dd.MM.yyyy").format(nextElectionDate.getTime());
    }

    public static int getTotalVotes() {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM votes WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'")) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void sendWahlGUI(Player p) {
        p.sendMessage(PREFIX + "Lade Wahl...");
        p.sendMessage(Messages.INFO + "Niemand kann sehen, wen du wählst. Auch nicht die Administration.");
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'")) {
            if (rs.next()) {
                Inventory inv = Bukkit.createInventory(null, 54, "§8[§6Wahlen§8]");
                do {
                    inv.addItem(new ItemBuilder(Material.PLAYER_HEAD).setName("§6" + Script.getOfflinePlayer(rs.getInt("nrp_id")).getName()).build());
                } while (rs.next());
                p.openInventory(inv);
            } else {
                p.sendMessage(PREFIX + "Es gibt derzeit keine Kandidaten.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addVote(Player p, int id) {
        Script.executeAsyncUpdate("UPDATE wahlen SET votings = votings + 1 WHERE nrp_id = '" + id + "'");
        Script.executeAsyncUpdate("INSERT INTO votes (id, nrp_id, president, quartal, year) VALUES (NULL, '" + Script.getNRPID(p) + "', '" + id + "', '" + getCurrentQuartal() + "', '" + Calendar.getInstance().get(Calendar.YEAR) + "')");
    }

    public static boolean hasVoted(Player p) {
        return Script.getInt(p, "votes", "quartal") == getCurrentQuartal() && Script.getInt(p, "votes", "year") == Calendar.getInstance().get(Calendar.YEAR);
    }

    public static boolean hasVoted(OfflinePlayer p) {
        return Script.getInt(p, "votes", "quartal") == getCurrentQuartal() && Script.getInt(p, "votes", "year") == Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getVote(Player p) {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM votes WHERE quartal = " + getCurrentQuartal() + " AND year = " + Calendar.getInstance().get(Calendar.YEAR) + " AND nrp_id = " + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("president");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getVote(OfflinePlayer p) {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM votes WHERE quartal = " + getCurrentQuartal() + " AND year = " + Calendar.getInstance().get(Calendar.YEAR) + " AND nrp_id = " + Script.getNRPID(p))) {
            if (rs.next()) {
                return rs.getInt("president");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getWinner() {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "' ORDER BY votings DESC")) {
            if (rs.next()) {
                int i = 0;
                int nrp_id = 0;
                do {
                    if (rs.getInt("votings") > i) {
                        i = rs.getInt("votings");
                        nrp_id = rs.getInt("nrp_id");
                    } else if (rs.getInt("votings") == i) {
                        if (extend) {
                            alreadyExtended = true;
                            return -1;
                        } else {
                            extend = true;
                            Bukkit.broadcastMessage(PREFIX + "Es gibt einen Gleichstand zwischen folgenden Kandidaten:");
                            List<Integer> list = getEvenPlayers(Script.getOfflinePlayer(rs.getInt("nrp_id")));
                            for (int id : list) {
                                Bukkit.broadcastMessage("§8" + Messages.ARROW + " §6" + Script.getOfflinePlayer(id).getName());
                            }
                            return -1;
                        }
                    }
                } while (rs.next());
                return nrp_id;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getVotes(OfflinePlayer p) {
        return Script.getInt(p, "wahlen", "votings");
    }

    public static List<Integer> getEvenPlayers(OfflinePlayer p) {
        List<Integer> list = new ArrayList<>();
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "' AND votings= '" + getVotes(p) + "'")) {
            if (rs.next()) {
                do {
                    list.add(rs.getInt("nrp_id"));
                } while (rs.next());
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<OfflinePlayer> getPlayers() {
        ArrayList<OfflinePlayer> list = new ArrayList<>();
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'")) {
            if (rs.next()) {
                do {
                    list.add(Script.getOfflinePlayer(rs.getInt("nrp_id")));
                } while (rs.next());
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addToWahlen(Player p) {
        Script.executeAsyncUpdate("INSERT INTO wahlen (id, nrp_id, quartal, year, votings) VALUES (NULL, '" + Script.getNRPID(p) + "', '" + getCurrentQuartal() + "', '" + Calendar.getInstance().get(Calendar.YEAR) + "', '0');");
    }

    public static void addToWahlen(OfflinePlayer p) {
        Script.executeAsyncUpdate("INSERT INTO wahlen (id, nrp_id, quartal, year, votings) VALUES (NULL, '" + Script.getNRPID(p) + "', '" + getCurrentQuartal() + "', '" + Calendar.getInstance().get(Calendar.YEAR) + "', '0');");
    }

    public static boolean hasApplied(Player p) {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE nrp_id = '" + Script.getNRPID(p) + "' AND quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'")) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasApplied(OfflinePlayer p) {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE nrp_id = '" + Script.getNRPID(p) + "' AND quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'")) {
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getWahlApplications() {
        try (Statement stmt = Main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'")) {
            if (rs.next()) {
                int i = 0;
                do {
                    i++;
                } while (rs.next());
                return i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCurrentQuartal() {
        if (Calendar.getInstance().get(Calendar.MONTH) <= 3) {
            return 1;
        } else if (Calendar.getInstance().get(Calendar.MONTH) <= 6) {
            return 2;
        } else if (Calendar.getInstance().get(Calendar.MONTH) <= 9) {
            return 3;
        } else {
            return 4;
        }
    }

    public static int getNextQuartal() {
        if (Calendar.getInstance().get(Calendar.MONTH) <= 3) {
            return 2;
        } else if (Calendar.getInstance().get(Calendar.MONTH) <= 6) {
            return 3;
        } else if (Calendar.getInstance().get(Calendar.MONTH) <= 9) {
            return 4;
        } else {
            return 1;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle() != null && e.getView().getTitle().equals("§8[§6Wahlen§8]")) {
            e.setCancelled(true);
            e.getView().close();
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
                Player p = (Player) e.getWhoClicked();
                OfflinePlayer president = Bukkit.getOfflinePlayer(e.getCurrentItem().getItemMeta().getDisplayName().replace("§6", ""));
                if (hasVoted(p)) {
                    p.sendMessage(PREFIX + "Du hast bereits abgestimmt.");
                    return;
                }
                addVote(p, Script.getNRPID(president));
                p.sendMessage(PREFIX + "Du hast erfolgreich für " + president.getName() + " abgestimmt.");
                Achievement.WAEHLER.grant(p);
            }
        }
    }

}
