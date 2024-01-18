package de.newrp.Police;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Organisationen.Plantage;
import de.newrp.Player.AFK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class BurnPlant implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keinen Beruf.");
            return true;
        }

        if (!Beruf.getBeruf(p).equals(Beruf.Berufe.POLICE)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Polizist.");
            return true;
        }

        if (!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Dienst.");
            return true;
        }

        List<Player> nearbyCops = p.getNearbyEntities(5, 5, 5)
                .stream()
                .filter(ent -> ent instanceof Player)
                .map(ent -> (Player) ent)
                .filter(nearbyPlayer -> Beruf.getBeruf(nearbyPlayer).equals(Beruf.Berufe.POLICE))
                .filter(Duty::isInDuty)
                .filter(nearbyPlayer -> !AFK.isAFK(nearbyPlayer)).collect(Collectors.toList());

        nearbyCops.add(p);
        int cops = nearbyCops.size();
        if (cops < 5) {
            p.sendMessage(Messages.ERROR + "Es braucht mindestens 5 Beamte um eine Plantage zu verbrennen.");
            return true;
        }

        Plantage plant = Plantage.getNextPlantage(p, 3);

        if (plant == null) {
            p.sendMessage(Messages.ERROR + "Du bist bei keiner Plantage.");
        } else {
            plant.burn();

            String typeName = plant.getType().getItem().getName();
            Beruf.Berufe.POLICE.sendMessage(Plantage.PREFIX + "Beamter " + Script.getName(p) + " hat erfolgreich eine " + typeName + " Plantage verbrannt.");
            Beruf.Berufe.GOVERNMENT.sendMessage(Plantage.PREFIX + "Beamter " + Script.getName(p) + " hat erfolgreich eine " + typeName + " Plantage verbrannt.");

            int exp = Script.getRandom(10, 18);
            for (Player nearbyCop : nearbyCops) {
                nearbyCop.sendMessage(Plantage.PREFIX + "Du hast erfolgreich eine " + typeName + " Plant verbrannt.");
                Script.addEXP(nearbyCop, Script.getRandom(8, 12));
            }
        }

        return true;
    }
}