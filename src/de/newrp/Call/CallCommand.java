package de.newrp.Call;

import de.newrp.API.Messages;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import de.newrp.Player.Anrufbeantworter;
import de.newrp.Player.Mobile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class CallCommand implements CommandExecutor, Listener {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if(args.length != 1) {
            p.sendMessage(Messages.ERROR + "/call [Spieler]");
            return true;
        }

        if(!Mobile.hasPhone(p)) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy.");
            return true;
        }

        if(!Mobile.isPhone(p.getInventory().getItemInMainHand())) {
            p.sendMessage(Messages.ERROR + "Du hast kein Handy in der Hand.");
            return true;
        }

        if(!Mobile.mobileIsOn(p)) {
            p.sendMessage(Messages.ERROR + "Dein Handy ist ausgeschaltet.");
            return true;
        }

        if(!Mobile.hasConnection(p)) {
            p.sendMessage(Messages.ERROR + "Du hast keine Verbindung.");
            return true;
        }

        if(!Call.isOnCall(p) && Call.getParticipantsAmount(Call.getCallIDByPlayer(p)) >= 3 && !Premium.hasPremium(p)) {
            p.sendMessage(Messages.ERROR + "Du kannst ohne Premium nur mit 3 Personen gleichzeitig telefonieren.");
            p.sendMessage(Messages.INFO + "Du kannst in unserem Shop Premium kaufen: §6https://shop.newrp.de");
            return true;
        }

        Player tg = Script.getPlayer(args[0]);
        if(tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }

        if(p == tg) {
            p.sendMessage(Messages.ERROR + "Du kannst dich nicht selbst anrufen.");
            return true;
        }

        if(!Mobile.hasPhone(tg)) {
            p.sendMessage(Messages.ERROR + "Der Spieler hat kein Handy.");
            return true;
        }

        if(!Mobile.mobileIsOn(tg)) {
            p.sendMessage(Anrufbeantworter.PREFIX + (Anrufbeantworter.getAnrufbeantworter(tg) == null ? "Der gewünschte Gesprächsteilnehmer ist derzeit nicht erreichbar." : Anrufbeantworter.getAnrufbeantworter(tg)));
            return true;
        }

        if(!Mobile.hasConnection(tg)) {
            p.sendMessage(Anrufbeantworter.PREFIX + (Anrufbeantworter.getAnrufbeantworter(tg) == null ? "Der gewünschte Gesprächsteilnehmer ist derzeit nicht erreichbar." : Anrufbeantworter.getAnrufbeantworter(tg)));
            return true;
        }

        if(Call.isOnCall(tg) || Call.isWaitingForCall(tg)) {
            p.sendMessage(Anrufbeantworter.PREFIX + (Anrufbeantworter.getAnrufbeantworter(tg) == null ? "Der gewünschte Gesprächsteilnehmer ist derzeit nicht erreichbar." : Anrufbeantworter.getAnrufbeantworter(tg)));
            return true;
        }

        if(!Call.isOnCall(p)) {
            Call.call(p, tg);
        } else {
            Call.addParticipant(p, tg);
        }

        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(Call.isWaitingForCall(p)) {
            Call.deny(p);
            p.sendMessage(Messages.INFO + "Du hast den Anruf abgelehnt.");
            return;
        }

        if(Call.getParticipants(Call.getCallIDByPlayer(p)).size() == 1) {
            Call.abort(p);
            return;
        }

        Call.hangup(p);
    }

}
