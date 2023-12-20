package de.newrp.Entertainment;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Shop.ShopItem;
import de.newrp.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

public class Lotto implements CommandExecutor {


    public static String PREFIX = "§8[§6Lotto§8] §6" + Messages.ARROW + " ";


    public static String NEWS = "§8[§6News§8] §6" + Messages.ARROW + " ";

    public static boolean haveLottoschein(Player p) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nrp_id FROM lotto WHERE nrp_id=" + Script.getNRPID(p))) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getJackpot() {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT amount FROM lotto_jackpot")) {
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<Integer> getLottoPlayer() {
        ArrayList<Integer> player = new ArrayList<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nrp_id FROM lotto")) {
            while (rs.next()) {
                player.add(rs.getInt("nrp_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    public static void start() {
        int i = Script.getRandom(1, 999);
        String day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? "Sonntagabend" : "Mittwochabend";
        Bukkit.broadcastMessage(NEWS + "Guten Abend! Herzlich Willkommen zur Lottoziehung am "+ day + "!");
        Bukkit.getServer().getScheduler().runTaskLater(main.getInstance(), () -> Bukkit.broadcastMessage(NEWS + "Der aktuelle Jackpot beträgt §e§l" + getJackpot() + "§r§6€..."), 20 * 20L);
        Bukkit.getServer().getScheduler().runTaskLater(main.getInstance(), () -> Bukkit.broadcastMessage(NEWS + "Es wird nun eine Nummer gezogen..."), 33 * 20L);
        Bukkit.getServer().getScheduler().runTaskLater(main.getInstance(), () -> Bukkit.broadcastMessage(NEWS + "Und es ist die Zahl Nummer... §e§l" + i + "§r§6!"), 40 * 20L);
        Bukkit.getServer().getScheduler().runTaskLater(main.getInstance(), () -> {
            int jackpot = getJackpot();
            ArrayList<Integer> winner = getWinner(i);
            if (!winner.isEmpty()) {
                Script.executeUpdate("UPDATE lotto_jackpot SET amount=0");
                int anteil = (jackpot / winner.size());
                if (winner.size() > 1) {
                    Bukkit.broadcastMessage(NEWS + "Es gab " + winner.size() + " Gewinner. Jeder Gewinner erhält " + anteil + "€.");
                } else {
                    Bukkit.broadcastMessage(NEWS + "Es gab einen Gewinner. Der Gewinner erhält " + anteil + "€.");
                }
                for (int f : winner) {
                    Player p = Script.getPlayer(f);
                    if (p == null) {
                        Script.addMoney(f, PaymentType.BANK, anteil);
                        Script.addOfflineMessage(f, PREFIX + "Du hast im Lotto gewonnen! §7(§a" + anteil + "€§7)§r");
                    } else {
                        Script.addMoney(p, PaymentType.BANK, anteil);
                        p.sendMessage(PREFIX + "Du hast im Lotto gewonnen! §7(§a" + anteil + "€§7)§r");
                    }
                }
            } else {
                Bukkit.broadcastMessage(NEWS + "Es gab keinen Gewinner. Der Jackpot von " + jackpot + "€ wird auf die nächste Ziehung übertragen!");
            }
            for (int id : getLottoPlayer()) {
                if (!winner.contains(id)) {
                    Player p = Script.getPlayer(id);
                    if (p == null) {
                        Script.addOfflineMessage(id, PREFIX + "Du hast beim Lotto verloren.");
                    } else {
                        p.sendMessage(PREFIX + "Du hast beim Lotto verloren.");
                    }
                }
            }
            Script.executeAsyncUpdate("TRUNCATE lotto;");
        }, 53 * 20L);
    }

    public static void addLottoJackpot(int add) {
        Script.executeUpdate("UPDATE lotto_jackpot SET amount=" + (getJackpot() + add));
    }

    public static ArrayList<Integer> getWinner(int i) {
        ArrayList<Integer> l = new ArrayList<>();
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nrp_id FROM lotto WHERE number=" + i)) {
            while (rs.next()) {
                l.add(rs.getInt("nrp_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return l;
    }

    public static int getLottoNummer(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT number FROM lotto WHERE nrp_id=" + id)) {
            if (rs.next()) {
                return rs.getInt("number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (args.length >= 1 && args[0].equalsIgnoreCase("info")) {
            Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> p.sendMessage(PREFIX + "Es befinden sich " + getJackpot() + "€ im Jackpot."));
        } else if (haveLottoschein(p)) {
            p.sendMessage(PREFIX + "Du hast einen Lottoschein mit der Nummer " + getLottoNummer(Script.getNRPID(p)) + ".");
        } else {
            if (!activate()) {
                p.sendMessage(Messages.ERROR + "Du kannst im Moment kein Lottoschein einlösen.");
            } else {
                ItemStack lotto = Script.setName(Material.PAPER, "§7Lottoschein");
                if (p.getInventory().contains(lotto)) {
                    if (args.length == 0) {
                        p.sendMessage(Messages.ERROR + "/lotto [Nummer (1-999)]");
                    } else {
                        if (Script.isInt(args[0])) {
                            int nummer = Integer.parseInt(args[0]);
                            if (nummer >= 1 && nummer <= 999) {
                                Script.executeUpdate("INSERT INTO lotto (nrp_id, number) VALUES (" + Script.getNRPID(p) + ", " + nummer + ");");
                                p.getInventory().remove(lotto);
                                p.sendMessage(PREFIX + "Du hast nun einen Lottoschein mit der Nummer " + nummer + ".");
                                addLottoJackpot(ShopItem.LOTTOSCHEIN.getBuyPrice());
                            } else {
                                p.sendMessage(Messages.ERROR + "Es gibt nur Lottonummer zwischen 1 und 999.");
                            }
                        } else {
                            p.sendMessage(Messages.ERROR + "/lotto [Nummer (1-999)]");
                        }
                    }
                } else {
                    p.sendMessage(Messages.ERROR + "Du hast keinen Lottoschein.");
                }
            }
        }
        return true;
    }

    public boolean activate() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
                || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY
                || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) != 18
                || Calendar.getInstance().get(Calendar.MINUTE) < 10
                || Calendar.getInstance().get(Calendar.MINUTE) > 20;
    }
}