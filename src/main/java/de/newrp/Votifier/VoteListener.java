package de.newrp.Votifier;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.newrp.API.Achievement;
import de.newrp.API.Debug;
import de.newrp.API.Event;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;

public class VoteListener implements Listener {

    public static HashMap<Integer, Integer> max_votes = new HashMap<>();
    public static void addVote(Vote v) {
        String player = v.getUsername().toLowerCase();
        if (player.contains("votefor_")) {
            player = player.replace("votefor_", "");
        }
        Debug.debug(player + " voted for nrp");


        if(Script.getNRPID(player) == 0) {
            Script.executeAsyncUpdate("INSERT INTO preReleaseVote (username, timestamp) VALUES ('" + player + "', " + System.currentTimeMillis() + ")");
            return;
        }

        if(!max_votes.containsKey(Script.getNRPID(player))) {
            max_votes.put(Script.getNRPID(player), 1);
        } else {
            if(max_votes.get(Script.getNRPID(player)) == 3) return;
            max_votes.put(Script.getNRPID(player), max_votes.get(Script.getNRPID(player)) + 1);
        }

        int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean weekend = dayOfTheWeek == Calendar.SATURDAY || dayOfTheWeek == Calendar.SUNDAY;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int votes = getTotalVotesToday();
        if (++votes >= (weekend ? de.newrp.API.Vote.VOTE_AMOUNT_WEEKEND : de.newrp.API.Vote.VOTE_AMOUNT_WEEK)) {
            if (NewRoleplayMain.event == null) Script.startEvent(Event.VOTE, true);
        }
        Script.executeAsyncUpdate("UPDATE voteday SET votes=votes+1 WHERE day=" + day + " AND year=" + year);
        Player p = Script.getPlayer(player);
        if (p != null) {
            int id = Script.getNRPID(p);
            int points = getVotepoints(id);
            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1F, 1F);
            p.sendMessage("§8[§6Vote§8]§e Vielen Dank, dass du für uns abgestimmt hast!");
            p.sendMessage("§8[§6Vote§8]§e Du hast nun §6" + (points < 0 ? 1 : points + 1) + " §eVotepoints.");
            Script.addEXP(p, 50);
            Achievement.VOTEN.grant(p);
        } else {
            Script.addEXP(Script.getNRPID(player), 50);
        }
        int id = Script.getNRPID(v.getUsername());
        if (id != 0) {
            int points = getVotepoints(id);
            if (points == -1) {
                Script.executeUpdate("INSERT INTO vote(id, votepoints, totalvotes) VALUES(" + id + ", 1, 1)");
            } else {
                Script.executeUpdate("UPDATE vote SET votepoints=votepoints+1, totalvotes=totalvotes+1 WHERE id=" + id);
            }
            addVoteStreak(id);
        }
        VoteCommand.votes.put(player + "." + v.getServiceName().replace(".", "_"), v.getTimeStamp());
    }

    public static void addVoteStreak(int p) {
        int c_year = Calendar.getInstance().get(Calendar.YEAR);
        int c_day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_vote_day, last_vote_year FROM vote_history WHERE id=" + p)) {
            if (rs.next()) {
                int day = rs.getInt("last_vote_day");
                int year = rs.getInt("last_vote_year");
                if (c_year == year) {
                    if ((day + 1) == c_day || day == c_day) {
                        if (de.newrp.API.Vote.getVotesToday(p) < 2) {
                            Script.executeUpdate("UPDATE vote_history SET days=days+1, last_vote_day=" + c_day + ", last_vote_year=" + c_year + " WHERE id=" + p);
                        }
                    } else {
                        Script.executeUpdate("UPDATE vote_history SET days=1, last_vote_day=" + c_day + ", last_vote_year=" + c_year + " WHERE id=" + p);
                    }
                } else {
                    Script.executeUpdate("UPDATE vote_history SET days=1, last_vote_day=" + c_day + ", last_vote_year=" + c_year + " WHERE id=" + p);
                }
            } else {
                Script.executeUpdate("INSERT INTO vote_history (id, days, last_vote_day, last_vote_year) VALUES (" + p + ", 1, " + c_day + ", " + c_year + ");");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getVotepoints(int p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT votepoints FROM vote WHERE id=" + p)) {
            if (rs.next()) {
                return rs.getInt("votepoints");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getTotalVotepoints(int p) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT totalvotes FROM vote WHERE id=" + p)) {
            if (rs.next()) {
                return rs.getInt("totalvotes");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void removeVotepoints(Player p, int i) {
        int id = Script.getNRPID(p);
        int points = getVotepoints(id) - i;
        if (points < 0) points = 0;
        Script.executeUpdate("UPDATE vote SET votepoints=" + points + " WHERE id=" + id);
    }

    public static int getTotalVotesToday() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT votes FROM voteday WHERE day=" + day + " AND year=" + year)) {
            if (rs.next()) {
                return rs.getInt("votes");
            } else {
                Script.executeAsyncUpdate("INSERT INTO voteday (day, year, votes) VALUES (" + day + "," + year + ", 0);");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @EventHandler
    public void onPlayerVote(VotifierEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            Vote v = e.getVote();
            addVote(v);
            int id = Script.getNRPID(v.getUsername());
            if (id == 0) {
                String ip = v.getAddress();
                try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT id FROM ip WHERE ip='" + ip + "' LIMIT 1")) {
                    if (rs.next()) {
                        id = rs.getInt("id");
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                if (id != 0) {
                    Script.executeUpdate("INSERT INTO vote_cache (id, day) VALUES(" + id + ", " + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + ");");
                    Script.executeUpdate("INSERT INTO votelist (id, username, address, service, time) VALUES (" + id + ", '" + Script.getPlayer(id).getName() + "', '" + v.getAddress() + "', '" + v.getServiceName() + "', '" + System.currentTimeMillis() + "');");
                    int i = de.newrp.API.Vote.getVotesToday(id);
                }
            } else {
                String username = Script.getPlayer(id).getName();
                Script.executeUpdate("INSERT INTO vote_cache (id, day) VALUES(" + id + ", " + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + ");");
                Script.executeUpdate("INSERT INTO votelist (id, username, address, service, time) VALUES (" + id + ", '" + username + "', '" + v.getAddress() + "', '" + v.getServiceName() + "', '" + System.currentTimeMillis() + "');");
            }
        });
    }
}