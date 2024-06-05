package de.newrp.Entertainment;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Casino implements CommandExecutor, TabCompleter {

    public static final String PREFIX = "§8[§6Casino§8] §6" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Organisation.hasOrganisation(player)) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (Organisation.getOrganisation(player) != Organisation.FALCONE) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (Organisation.getRank(player) < 4) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (player.getLocation().distance(new Location(Script.WORLD, 756, 109, 864)) > 5) {
                player.sendMessage(Messages.ERROR + "Du bist nicht am Casino-Schalter!");
                return true;
            }

            if (args.length == 1) {
                if (Organisation.getRank(player) >= 5) {
                    if (Organisation.isLeader(player, true)) {
                        if (args[0].equalsIgnoreCase("limit")) {
                            player.sendMessage(PREFIX + "Das tägliche Spieler-Limit beträgt " + getLimit() + "€.");
                            return true;
                        } else if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("max")) {
                            player.sendMessage(PREFIX + "Der tägliche Maximalgewinn beträgt " + getMax() + "€.");
                            return true;
                        } else if (args[0].equalsIgnoreCase("bet") || args[0].equalsIgnoreCase("einsatz")) {
                            player.sendMessage(PREFIX + "Der Maximaleinsatz beträgt " + getBet() + "€.");
                            return true;
                        } else if (args[0].equalsIgnoreCase("p")) {
                            player.sendMessage(PREFIX + "P gleich " + getP() + ".");
                            return true;
                        }
                    }
                }

                player.sendMessage(Messages.ERROR + "/casino [einzahlen/auszahlen/limit/einsatz/max] ([amount])");
                return true;
            }

            if (args.length > 1) {
                int a;
                try {
                    a = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    player.sendMessage(Messages.ERROR + "Ungültige Menge!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("einzahlen") || args[0].equalsIgnoreCase("add")) {
                    addMoney(a);
                    Script.removeMoney(player, PaymentType.CASH, a);

                    Organisation.FALCONE.sendMessage(PREFIX + player.getName() + " hat §a" + a + "€ §7in das Casino gezahlt.");
                    return true;
                } else if (Organisation.getRank(player) >= 5) {
                    if (args[0].equalsIgnoreCase("auszahlen") || args[0].equalsIgnoreCase("get")) {
                        if (getMoney() < a) {
                            player.sendMessage(Messages.ERROR + "Im Casino ist nicht genug Geld.");
                            return true;
                        }

                        removeMoney(a);
                        Script.addMoney(player, PaymentType.CASH, a);

                        Organisation.FALCONE.sendMessage(PREFIX + player.getName() + " hat §c" + a + "€ §7aus dem Casino ausgezahlt.");
                        return true;
                    } else if (args[0].equalsIgnoreCase("limit")) {
                        setLimit(a);
                        player.sendMessage(PREFIX + "Limit auf " + a + "€ gesetzt.");
                        return true;
                    } else if (args[0].equalsIgnoreCase("bet") || args[0].equalsIgnoreCase("einsatz")) {
                        setBet(a);
                        player.sendMessage(PREFIX + "Einsatz auf " + a + "€ gesetzt.");
                        return true;
                    } else if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("max")) {
                        setMax(a);
                        player.sendMessage(PREFIX + "Maximum auf " + a + "€ gesetzt.");
                        return true;
                    } else if (Organisation.isLeader(player, false)) {
                        if (args[0].equalsIgnoreCase("p")) {
                            setP(a);
                            player.sendMessage(PREFIX + "P auf " + a + " gesetzt.");
                            return true;
                        }
                    }
                }

                player.sendMessage(Messages.ERROR + "/casino [einzahlen/auszahlen/limit/einsatz/max] ([amount])");
                return true;
            }

            player.sendMessage(PREFIX + "Guthaben des Casinos:");
            player.sendMessage("            §8" + Messages.ARROW + " §7" + getMoney() + "€");
            return true;
        }

        return true;
    }

    public static void addMoney(int money) {
        Script.executeAsyncUpdate("UPDATE casino SET money=" + (getMoney() + money));
    }

    public static void removeMoney(int money) {
        Script.executeAsyncUpdate("UPDATE casino SET money=" + Math.max((getMoney() - money), 0));
    }

    public static int getMoney() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM casino")) {
            if (rs.next()) return rs.getInt("money");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setP(int p) {
        Script.executeAsyncUpdate("UPDATE casino SET p=" + p);
    }

    public static int getP() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM casino")) {
            if (rs.next()) return rs.getInt("p");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setLimit(int limit) {
        Script.executeAsyncUpdate("UPDATE casino SET lim=" + limit);
    }

    public static int getLimit() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM casino")) {
            if (rs.next()) return rs.getInt("lim");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setBet(int bet) {
        Script.executeAsyncUpdate("UPDATE casino SET bet=" + bet);
    }

    public static int getBet() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM casino")) {
            if (rs.next()) return rs.getInt("bet");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setMax(int max) {
        Script.executeAsyncUpdate("UPDATE casino SET max=" + max);
    }

    public static int getMax() {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM casino")) {
            if (rs.next()) return rs.getInt("max");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String[] args1 = new String[] {"einzahlen", "auszahlen", "limit", "max", "einsatz"};
        String[] args2 = new String[] {"0"};
        List<String> completions = new ArrayList<>();
        if (Organisation.hasOrganisation((Player) sender)) {
            if (Organisation.getOrganisation((Player) sender) == Organisation.FALCONE) {
                if (Organisation.getRank((Player) sender) >= 4) {
                    if (args.length == 1) {
                        for (String string : args1)
                            if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
                    } else if (args.length == 2) {
                        for (String string : args2)
                            if (string.toLowerCase().startsWith(args[1].toLowerCase())) completions.add(string);
                    }
                }
            }
        }
        return completions;
    }
}
