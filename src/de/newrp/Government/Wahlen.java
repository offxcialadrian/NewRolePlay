package de.newrp.Government;

import de.newrp.API.Debug;
import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Wahlen implements CommandExecutor, Listener {

    private static final String PREFIX = "§8[§6Wahlen§8] " + Messages.ARROW + " §6";
    public static boolean extend = false;
    public static boolean alreadyExtended = false;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("aufstellen") || args[0].equalsIgnoreCase("apply")) {

                if (Script.getLevel(p) < 5) {
                    p.sendMessage(Messages.ERROR + "Du musst mindestens Level 5 sein, um dich aufstellen zu können.");
                    return true;
                }

                if (getWahlApplications() >= 54) {
                    p.sendMessage(PREFIX + "Es können maximal 54 Spieler aufgestellt werden.");
                    return true;
                }

                if (wahlenActive()) {
                    p.sendMessage(PREFIX + "Es sind derzeit Wahlen aktiv.");
                    return true;
                }

                if (hasApplied(p)) {
                    p.sendMessage(PREFIX + "Du hast dich bereits aufgestellt.");
                    return true;
                }

                addToWahlen(p);
                p.sendMessage(PREFIX + "Du hast dich erfolgreich aufgestellt.");
                Script.sendTeamMessage(PREFIX + "Der Spieler " + Script.getName(p) + " hat sich aufgestellt.");
                return true;

            } else if (args[0].equalsIgnoreCase("rücktritt") || args[0].equalsIgnoreCase("zurücktreten")) {

                if (!hasApplied(p)) {
                    p.sendMessage(PREFIX + "Du hast dich nicht aufgestellt.");
                    return true;
                }

                Script.executeAsyncUpdate("DELETE FROM wahlen WHERE nrp_id = '" + Script.getNRPID(p) + "' AND quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'");
                p.sendMessage(PREFIX + "Du hast dich erfolgreich zurückgetreten.");
                Script.sendTeamMessage(PREFIX + "Der Spieler " + Script.getName(p) + " ist zurückgetreten.");

            } else if (args[0].equalsIgnoreCase("result")) {
                if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                if(!SDuty.isSDuty(p)) {
                    p.sendMessage(Messages.NO_SDUTY);
                    return true;
                }

                if (!wahlenActive()) {
                    p.sendMessage(PREFIX + "Es sind derzeit keine Wahlen aktiv.");
                    return true;
                }

                getWahlResult();
                return true;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("set")) {
                if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                    p.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                if(!SDuty.isSDuty(p)) {
                    p.sendMessage(Messages.NO_SDUTY);
                    return true;
                }

                Player tg = Script.getPlayer(args[1]);
                if(tg == null) {
                    p.sendMessage(Messages.PLAYER_NOT_FOUND);
                    return true;
                }

                if(hasApplied(tg)) {
                    p.sendMessage(PREFIX + "Der Spieler hat sich bereits aufgestellt.");
                    return true;
                }

                addToWahlen(tg);
                p.sendMessage(PREFIX + "Du hast den Spieler erfolgreich aufgestellt.");
                tg.sendMessage(PREFIX + "Du wurdest von " + Script.getName(p) + " aufgestellt.");
                Script.sendTeamMessage(p, ChatColor.GOLD, "hat " + Script.getName(tg) + " zur Wahl aufgestellt.", true);
                return true;

            }
        }

        if (!wahlenActive()) {
            p.sendMessage(PREFIX + "Es sind derzeit keine Wahlen aktiv.");
            return true;
        }

        sendWahlGUI(p);
        return false;
    }

    public static boolean wahlenActive() {
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
        } else return Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 && (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 20 && extend);
    }

    public static void getWahlResult() {
        if (getWinner() == -1 && !alreadyExtended) {
            Bukkit.broadcastMessage(PREFIX + "Es gibt keinen Gewinner Die Wahlen werden bis 20 Uhr verlängert.");
            return;
        }

        if (getWinner() == -1 && alreadyExtended) {
            Bukkit.broadcastMessage(PREFIX + "Es konnte immer noch kein Gewinner ermittelt werden. Die Legislaturperiode der aktuellen Regierung wird um 1 Quartal verlängert.");
            return;
        }

        if(getWinner() == -1 && getWahlApplications()==0) {
            Bukkit.broadcastMessage(PREFIX + "Es gibt keine Kandidaten. Die Legislaturperiode der aktuellen Regierung wird um 1 Quartal verlängert.");
        }

        Debug.debug("§6Wahlen: " + Script.getPlayer(getWinner()).getName() + " hat die Wahlen gewonnen.");
        Debug.debug("§6Die nächsten Wahlen finden am " + getNextElection() + " statt.");
    }

    public static String getNextElection() {
        if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JANUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) > 15) {
            return "15.04.";
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) > 15) {
            return "15.07.";
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JULY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) > 15) {
            return "15.10.";
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) > 15) {
            return "15.01.";
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JANUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) <= 15) {
            return "15.01.";
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) <= 15) {
            return "15.04.";
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.JULY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) <= 15) {
            return "15.07.";
        } else if (Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) <= 15) {
            return "15.10.";
        }
        return "15.01.";
    }

    public static void sendWahlGUI(Player p) {
        p.sendMessage(PREFIX + "Lade Wahl...");
        p.sendMessage(Messages.INFO + "Niemand kann sehen, wen du wählst. Auch nicht die Administration.");
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM wahlen WHERE quartal = '" + getCurrentQuartal() + "' AND year = '" + Calendar.getInstance().get(Calendar.YEAR) + "'")) {
            if (rs.next()) {
                Inventory inv = Bukkit.createInventory(null, 54, "§8[§6Wahlen§8]");
                do {
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                    meta.setOwningPlayer(Script.getOfflinePlayer(rs.getInt("nrp_id")));
                    meta.setDisplayName("§6" + Script.getOfflinePlayer(rs.getInt("nrp_id")).getName());
                    head.setItemMeta(meta);
                    inv.addItem(head);
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

    public static int getWinner() {
        try (Statement stmt = main.getConnection().createStatement();
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
        try (Statement stmt = main.getConnection().createStatement();
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

    public static void addToWahlen(Player p) {
        Script.executeAsyncUpdate("INSERT INTO wahlen (id, nrp_id, quartal, year, votings) VALUES (NULL, '" + Script.getNRPID(p) + "', '" + getCurrentQuartal() + "', '" + Calendar.getInstance().get(Calendar.YEAR) + "', '0');");
    }

    public static boolean hasApplied(Player p) {
        return Script.getInt(p, "wahlen", "quartal") == getCurrentQuartal() && Script.getInt(p, "wahlen", "year") == Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getWahlApplications() {
        try (Statement stmt = main.getConnection().createStatement();
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
            }
        }
    }

}
