package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Vehicle.Car;
import de.newrp.Vehicle.CarType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCar implements CommandExecutor {

    public static String PREFIX = "§8[§eFahrzeug§8] §e" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/spawncar [Typ] [Besitzer]");
            return true;
        }

        CarType type = CarType.getCarTypeByName(args[0]);
        if(type == null) {
            p.sendMessage(PREFIX + "Dieser Fahrzeugtyp existiert nicht.");
            return true;
        }

        Player owner = Script.getPlayer(args[1]);
        if(owner == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        Car.createCar(type, p.getLocation(), owner);
        p.sendMessage(PREFIX + "Du hast ein " + type.getName() + " für " + Script.getName(owner) + " gespawnt.");

        return false;
    }
}
