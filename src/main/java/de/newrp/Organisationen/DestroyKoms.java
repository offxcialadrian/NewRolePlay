package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import de.newrp.Player.AFK;
import de.newrp.Player.Mobile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DestroyKoms implements CommandExecutor {

    public static String PREFIX = "§8[§6DestroyKoms§8] §6" + Messages.ARROW + " §7";

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Organisation.hasOrganisation(p) && !Beruf.hasBeruf(p)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/destroykoms [Name]");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist nicht online.");
            return true;
        }

        if(AFK.isAFK(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist AFK.");
            return true;
        }

        if(SDuty.isSDuty(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler ist im Support-Dienst.");
            return true;
        }

        if(!Mobile.hasPhone(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat kein Handy.");
            return true;
        }

        if(Mobile.getPhone(tg).isDestroyed(tg)) {
            p.sendMessage(Messages.ERROR + "Die Koms sind bereits zerstört.");
            return true;
        }

        if(p.getLocation().distance(tg.getLocation()) > 5) {
            p.sendMessage(Messages.ERROR + "Du bist zu weit entfernt.");
            return true;
        }

        if (tg.isInsideVehicle()) {
            if (tg.getVehicle() instanceof Player) {
                p.sendMessage(Messages.ERROR + "Die Person wird gerade gepackt.");
                return true;
            }
        }
        

        p.sendMessage(PREFIX + "Du hast die Koms von " + tg.getName() + " zerstört.");
        tg.sendMessage(PREFIX + "Deine Koms wurden von " + p.getName() + " zerstört.");
        Me.sendMessage(p, "zerstört die Koms von " + Script.getName(tg) + ".");
        Mobile.getPhone(tg).setDestroyed(tg, true);
        Mobile.getPhone(tg).setOff(tg);

        return false;
    }
}
