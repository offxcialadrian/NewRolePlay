package de.newrp.Berufe;

import de.newrp.API.Activity;
import de.newrp.API.ActivityCommand;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Forum.Forum;
import de.newrp.Organisationen.Organisation;
import de.newrp.TeamSpeak.TeamSpeak;
import de.newrp.Vehicle.Car;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UninviteCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§eUninvite§8] §e» ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Beruf.hasBeruf(p) && !Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(Messages.ERROR + "/uninvite [Spieler]");
            return true;
        }

        if (!Beruf.isLeader(p, true) && !Organisation.isLeader(p, true)) {
            p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
            return true;
        }

        OfflinePlayer tg = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(tg) == 0) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }



        if(!Beruf.hasBeruf(tg) && !Organisation.hasOrganisation(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist in keiner Organisation.");
            return true;
        }

        if(Beruf.hasBeruf(tg)) {
            Beruf.Berufe beruf = Beruf.getBeruf(p);

            if(!Beruf.hasBeruf(tg)) {
                p.sendMessage(Messages.ERROR + "Der Spieler hat keinen Beruf.");
                return true;
            }

            if(Beruf.getBeruf(tg) != beruf) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deinem Beruf.");
                return true;
            }

            if(Beruf.isCoLeader(p) && tg != p && Beruf.isLeader(tg, false)) {
                p.sendMessage(Messages.ERROR + "Du kannst den Leader nicht entlassen.");
                return true;
            }

            if (beruf.getLeasedAmount() > 0) {
                if (tg.isOnline()) {
                    for (Car car : Car.CARS) {
                        if (car.getOwner() == tg.getPlayer()) {
                            if (car.getLicenseplate().startsWith("N-RP-")) {
                                p.sendMessage(Messages.ERROR + "Der Spieler hat noch einen Leasing-Vertrag.");
                                return true;
                            }
                        }
                    }
                }
            }

            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " aus der " + Beruf.getBeruf(p).getName() + " entlassen.");

            if(tg.isOnline() && tg.getPlayer() != null) {
                tg.getPlayer().sendMessage(PREFIX + "Du wurdest aus der " + Beruf.getBeruf(p).getName() + " entlassen.");
                Equip.removeEquip(tg.getPlayer());
            } else {
                Script.addOfflineMessage(tg, PREFIX + "Du wurdest aus der " + Beruf.getBeruf(p).getName() + " entlassen.");
            }

            if(tg.getPlayer() != null && Duty.isInDuty(tg.getPlayer())) {
                Duty.removeDuty(tg.getPlayer());
                Script.updateListname(tg.getPlayer());
            }
            beruf.removeMember(tg, p);
            if (tg.getPlayer() != null) beruf.deleteMember(tg.getPlayer());
            Script.removeEXP(tg.getName(), Script.getRandom(50, 100));
            Activity.deleteActivities(Script.getNRPID(tg));
            TeamSpeak.sync(Script.getNRPID(tg));
            Forum.syncPermission(tg);
        } else {
            Organisation orga = Organisation.getOrganisation(p);

            if (!Organisation.hasOrganisation(tg)) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deiner Organisation.");
                return true;
            }

            if (Organisation.getOrganisation(tg) != orga) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht in deiner Organisation.");
                return true;
            }

            orga.removeExp(Script.getRandom(20, 30));
            if (Organisation.isCoLeader(p) && tg != p && Beruf.isLeader(tg, false)) {
                p.sendMessage(Messages.ERROR + "Du kannst den Leader nicht entlassen.");
                return true;
            }
            p.sendMessage(PREFIX + "Du hast " + tg.getName() + " aus der " + Organisation.getOrganisation(p).getName() + " entlassen.");

            if (tg.isOnline() && tg.getPlayer() != null) {
                tg.getPlayer().sendMessage(PREFIX + "Du wurdest aus der " + Organisation.getOrganisation(p).getName() + " entlassen.");
            } else {
                Script.addOfflineMessage(tg, PREFIX + "Du wurdest aus der " + Organisation.getOrganisation(p).getName() + " entlassen.");
            }

            orga.removeMember(tg, p);
            if (tg.getPlayer() != null) orga.deleteMember(tg.getPlayer());
            Script.removeEXP(tg.getName(), Script.getRandom(50, 100));
            Activity.deleteActivities(Script.getNRPID(tg));
            TeamSpeak.sync(Script.getNRPID(tg));
            Forum.syncPermission(tg);
        }

        return true;
    }
}


