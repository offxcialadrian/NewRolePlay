package de.newrp.Vehicle;

import de.newrp.main;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Kennzeichen implements CommandExecutor {
    public static final String prefix = "§8[§cKFZ-Kennzeichen§8]§6 ";
    public static final HashMap<Player, String> kfz = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (p.getLocation().distance(new Location(p.getWorld(), -149, 68, 260)) < 5) {
            if (args.length != 3) {
                p.sendMessage(prefix + "Sie müssen folgende Angaben machen: XX 0000");
            } else {
                if (args[0].length() == 2 && args[1].length() == 4) {
                    if (Script.isInt(args[1])) {
                        int i = Integer.parseInt(args[1]);
                        String s1 = args[0];
                        if (!check(s1)) {
                            if (Script.getMoney(p, PaymentType.BANK) >= 60) {
                                String kennzeichen = "UC" + "-" + s1 + "-" + i;
                                if (isInUse(kennzeichen)) {
                                    p.sendMessage(prefix + "Das Kennzeichen ist bereits vergeben.");
                                } else {
                                    p.sendMessage(prefix + "Sie haben das Kennzeichen " + kennzeichen + " gekauft.");
                                    p.sendMessage(prefix + "Sie müssen nun nurnoch das Kennzeichen anbringen (Rechtsklick)");
                                    kfz.put(p, kennzeichen);
                                    Script.removeMoney(p, PaymentType.BANK, 60);
                                }
                            } else {
                                p.sendMessage(prefix + "Ein Kennzeichen kostet 60$.");
                            }
                        } else {
                            p.sendMessage(prefix + "Das Kennzeichen darf keine Sonderzeichen beinhalten.");
                        }
                    }
                } else {
                    p.sendMessage(prefix + "Sie müssen folgende Angaben machen: XX 0000");
                }
            }
        } else {
            p.sendMessage(prefix + "Du bist nicht bei der KFZ Anmeldestelle.");
        }
        return true;
    }

    public boolean check(String s) {
        Pattern p = Pattern.compile("[\\p{Alpha}]*[\\p{Punct}][\\p{Alpha}]*");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public boolean isInUse(String s) {
        try (PreparedStatement stmt = main.getConnection().prepareStatement("SELECT id FROM vehicle WHERE kennzeichen = ?")) {

            stmt.setString(1, s);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
