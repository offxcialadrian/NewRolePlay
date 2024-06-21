package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Call.Call;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import de.newrp.Player.Mobile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;


public class WiretapCall implements CommandExecutor {

    private static final String PREFIX = "§8[§cAbhören§8] §8" + Messages.ARROW + " §7";

    public static HashMap<UUID, PlayerWiretap> wiretap = new HashMap<>();
    private BukkitRunnable runnable = null;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if(Beruf.getBeruf(player).equals(Beruf.Berufe.BUNDESKRIMINALAMT)) {
            player.sendMessage(PREFIX + "Du bist kein Mitglied des Bundeskriminalamts.");
            return true;
        }
        if(!wiretap.containsKey(player.getUniqueId())) {
            wiretap.remove(player.getUniqueId());
            updateRunnable();
            player.sendMessage(PREFIX + "Die Abhörung wurde beendet.");
            return true;
        }
        if(!Duty.isInDuty(player)) {
            player.sendMessage(PREFIX + "Du bist nicht im Dienst.");
            return true;
        }
        if(args.length != 1) {
            player.sendMessage(PREFIX + "Nutze §c/abhören [Name]");
            return true;
        }
        Mobile.Phones phone = Mobile.Phones.getPhone(player.getInventory().getItemInMainHand());
        if(phone == null) {
            player.sendMessage(PREFIX + "Du musst dein Handy in der Hand halten.");
            return true;
        }
        if(phone.getID() == 3) {
            player.sendMessage(PREFIX + "Dieses Handy besitzt nicht die modernsten Funkausstattungen.");
            return true;
        }
        Player callPlayer = Script.getPlayer(args[0]);
        if(callPlayer == null) {
            player.sendMessage(PREFIX + "Der Spieler §c" + args[0] + " §7wurde nicht gefunden.");
            return true;
        }
        if(!Organisation.hasOrganisation(callPlayer)) {
            player.sendMessage(PREFIX + "Du kannst nur Anrufe von Spielern aus Organisationen abhören.");
            return true;
        }
        if(!Call.isOnActiveCall(callPlayer)) {
            player.sendMessage(PREFIX + "Der Spieler §c" + args[0] + " tätigt gerade keinen Anruf.");
            return true;
        }
        int callId = Call.getCallIDByPlayer(callPlayer);
        wiretap.put(player.getUniqueId(), new PlayerWiretap(player, callPlayer, callId, System.currentTimeMillis()));
        player.sendMessage(PREFIX + "Du hörst nun den Anruf §e#" + callId + " §7von §6" + args[0] + " §7ab.");
        updateRunnable();
        return true;
    }

    public static void sendNotification(Player player, int callId, boolean created) {
        if(!Organisation.hasOrganisation(player)) {
            return;
        }
        String message;
        if(created) {
            message = PREFIX + player.getName() + " hat den Anruf §e#" + callId + " §7gestartet";
        } else {
            message = PREFIX + player.getName() + " ist dem Anruf §e#" + callId + " §7beigetreten";
        }

        for (UUID memberUuid : Beruf.Berufe.BUNDESKRIMINALAMT.getMember()) {
            final Player memberPlayer = Bukkit.getPlayer(memberUuid);
            if(memberPlayer == null) {
                continue;
            }

            Script.sendClickableMessage(memberPlayer, message, "/abhören " + player.getName(), "§cAnruf Abhören");
        }
    }

    public static void sendCallMessage(int callId, String message) {
        wiretap.forEach((uuid, playerWiretap) -> {
            if(playerWiretap.getCallId() == callId) {
                playerWiretap.getPlayer().sendMessage(PREFIX + "§e" + message);
            }
        });
    }

    private void updateRunnable() {
        if(wiretap.isEmpty() && runnable != null) {
            runnable.cancel();
            runnable = null;
            return;
        }
        if(!wiretap.isEmpty() && runnable == null) {
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    wiretap.forEach((key, value) -> value.update());
                }
            };
            runnable.runTaskTimer(NewRoleplayMain.getInstance(), 0, 20);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class PlayerWiretap {

        private final Player player;
        private final Player callPlayer;
        private final int callId;
        private long millis;

        public void update() {
            long newMillis = System.currentTimeMillis();
            if(!Call.ON_CALL.containsKey(callId)) {
                player.sendMessage(PREFIX + "Die Abhörung wurde unterbrochen, da der Anruf beendet wurde.");
                wiretap.remove(player.getUniqueId());
                return;
            }
            if(millis < newMillis) {
                return;
            }
            millis = newMillis+60000;
            if(!Bukkit.getOnlinePlayers().contains(player)) {
                wiretap.remove(player.getUniqueId());
                return;
            }
            int money = Script.getMoney(player, PaymentType.BANK);
            if(money-50 < 0) {
                player.sendMessage(PREFIX + "Die Abhörung wurde unterbrochen, da du zu wenig Geld auf dem Konto hast.");
                wiretap.remove(player.getUniqueId());
                return;
            }
            Script.removeMoney(player, PaymentType.BANK, 50);
            player.sendMessage(PREFIX + "Dir wurden 50€ Abhörgebühren abgerechnet.");
        }

    }

}
