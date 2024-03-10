package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Player.AFK;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaffleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (players.size() < 5) {
            p.sendMessage(Messages.ERROR + "Es sind zuwenig Spieler online.");
            return false;
        }
        List<String> winner = new ArrayList<>();

        int i = 0;

        while (winner.size() < 5) {
            if (i++ > 50) {
                p.sendMessage("§cRaffle nach 50 Versuchen abgebrochen.");
                break;
            }

            int random = Script.getRandom(0, players.size() - 1);

            Player current = players.get(random);

            if (AFK.isAFK(current) || winner.contains(current.getName()))
                continue;

            winner.add(current.getName());
        }

        Collections.shuffle(winner);

        String sb = "\n§6==== VERLOSUNG ====\n" + "  §7» 1. Platz: §l" + winner.get(0) + "§r\n" +
                "  §7» 2. Platz: §l" + winner.get(1) + "§r\n" +
                "  §7» 3. Platz: §l" + winner.get(2) + "§r\n" +
                "  §7» 4. Platz: §l" + winner.get(3) + "§r\n" +
                "  §7» 5. Platz: §l" + winner.get(4) + "§r\n\n";

        Bukkit.broadcastMessage(sb);
        return true;
    }
}
