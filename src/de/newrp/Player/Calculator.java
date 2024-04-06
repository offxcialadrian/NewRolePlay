package de.newrp.Player;

import de.newrp.API.Expressions;
import de.newrp.API.Messages;
import de.newrp.API.Premium;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Calculator implements CommandExecutor {

    public static String PREFIX = "§8[§bTaschenrechner§8] §b" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length == 0) {
            p.sendMessage(Messages.ERROR + "/calc [Ausdruck]");
            return true;
        }

        if(!Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du benötigst Premium, um den Taschenrechner zu benutzen.");
            p.sendMessage(Messages.INFO + "Du kannst Premium im Shop unter https://shop.newrp.de erwerben.");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for(String arg : args) {
            sb.append(arg);
        }
        String input = sb.toString();

        Expressions expr = new Expressions(input);
        try {
            expr.evaluate();
            p.sendMessage(PREFIX + input + " §f= §b" + expr.parse());
        } catch (Expressions.ExpressionException e) {
            p.sendMessage(Messages.ERROR + "Deine Rechnung war ungültig: " + e.getMessage());
            return true;
        }

        return false;
    }
}
