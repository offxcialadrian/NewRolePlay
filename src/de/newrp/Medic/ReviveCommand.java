package de.newrp.Medic;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import org.apache.logging.log4j.core.jmx.AppenderAdmin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ReviveCommand implements CommandExecutor {

    private static final HashMap<String, Long> cooldown = new HashMap<>();
    private static final String PREFIX = "§8[§cRevive§8] §c" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length == 1 && Script.hasRank(p, Rank.MODERATOR, true) && SDuty.isSDuty(p)) {

            Player tg = Script.getPlayer(args[0]);
            if (tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return false;
            }
            if (!Friedhof.isDead(tg)) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht tot");
                return true;
            }

            long time = System.currentTimeMillis();
            if (cooldown.containsKey(p.getName())) {
                Long lastUsage = cooldown.get(p.getName());
                if (lastUsage + 300 * 1000 > time) {
                    p.sendMessage("§cDu kannst erst in 5 Minuten einen Spieler wiederbeleben.");
                    return true;
                }
            }

            Friedhof f = Friedhof.getDead(tg);
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " wiederbelebt.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Messages.RANK_PREFIX(p) + " wiederbelebt.");
            Friedhof.revive(tg, null);
            if (f.getInventoryContent() != null) {
                tg.getInventory().clear();
                tg.getInventory().setContents(f.getInventoryContent());
            }
            if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) cooldown.put(p.getName(), System.currentTimeMillis());
            if (f.getCash() > 0) Script.setMoney(tg, PaymentType.CASH, f.getCash());
        }

        return false;
    }
}
