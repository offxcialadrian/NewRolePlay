package de.newrp.Player;

import de.newrp.API.Expression;
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

        Expression expr = new Expression(String.join(" ", args).replace(" ", ""));
        try {
            expr.evaluate();
        } catch (Expression.ExpressionException e) {
            p.sendMessage(Messages.ERROR + "Ungültiger Ausdruck.");
            return true;
        }

        p.sendMessage(PREFIX + String.join(" ", args) + " = " + expr.evaluate());

        return false;
    }
}
