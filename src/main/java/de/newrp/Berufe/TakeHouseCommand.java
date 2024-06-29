package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.House.House;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TakeHouseCommand implements CommandExecutor {

    public static final String PREFIX = "§8[§6Haus§8] §7" + Messages.ARROW + " §6";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Beruf.hasBeruf(player)) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (Beruf.getBeruf(player) != Beruf.Berufe.GOVERNMENT) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (!Beruf.isLeader(player, true)) {
                if (Beruf.getAbteilung(player) != Abteilung.Abteilungen.INNENMINISTERIUM && Beruf.getAbteilung(player) != Abteilung.Abteilungen.FINANZAMT) {
                    player.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }
            }

            if (args.length == 0) {
                player.sendMessage(Messages.ERROR + "/takehouse [Nummer]");
                return true;
            }

            int id;
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Messages.ERROR + "Bitte gebe eine gültige Nummer an.");
                return true;
            }

            House house = House.getHouseByID(id);
            if (house == null) {
                player.sendMessage(Messages.ERROR + "Das Haus existiert nicht.");
                return true;
            }

            if (house.getOwner() == 0 || Script.getOfflinePlayer(house.getOwner()) == null) {
                player.sendMessage(Messages.ERROR + "Das Haus hat keinen Besitzer.");
                return true;
            }

            house.setOwner(0);
            player.sendMessage(PREFIX + "Du hast das Haus von " + Script.getOfflinePlayer(house.getOwner()).getName() + " übernommen.");
            if (Script.getOfflinePlayer(house.getOwner()).isOnline()) {
                Script.getPlayer(house.getOwner()).sendMessage(PREFIX + "Dein Haus " + id + " wurde von der Regierung übernommen.");
            } else {
                Script.addOfflineMessage(Script.getOfflinePlayer(house.getOwner()), PREFIX + "Dein Haus " + id + " wurde von der Regierung übernommen.");
            }
            for (House.Mieter mieter : house.getMieter()) {
                if (mieter.getID() == 0) continue;
                house.removeMieter(mieter.getID());
                if (Script.getOfflinePlayer(mieter.getID()) != null && Script.getOfflinePlayer(mieter.getID()).isOnline()) {
                    Script.getPlayer(mieter.getID()).sendMessage(PREFIX + "Dein Mietvertrag für das Haus " + id + " wurde gekündigt.");
                }
            }
        }

        return true;
    }
}
