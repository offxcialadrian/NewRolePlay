package de.newrp.Berufe;

import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Call.Call;
import de.newrp.NewRoleplayMain;
import de.newrp.Player.Mobile;
import de.newrp.Police.Fahndung;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class WiretapCall implements CommandExecutor {

    private static final String PREFIX = "§8[§cAbhören§8] §8» §7";

    public static Map<UUID, PlayerWiretap> wiretap = new ConcurrentHashMap<>();
    private BukkitRunnable runnable = null;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        if(Beruf.getBeruf(player) != Beruf.Berufe.GOVERNMENT) {
            player.sendMessage(PREFIX + "Du bist kein Mitglied des Bundeskriminalamts.");
            return true;
        }
        if(wiretap.containsKey(player.getUniqueId())) {
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
        Mobile.Phones phone = Mobile.getPhone(player);
        if(phone == null || !Mobile.isPhone(player.getInventory().getItemInMainHand())) {
            player.sendMessage(PREFIX + "Du musst dein Handy in der Hand halten.");
            return true;
        }

        if(!Mobile.mobileIsOn(player)) {
            player.sendMessage(PREFIX + "Dein Handy muss angeschaltet sein.");
            return true;
        }
        /*if(phone.getID() == 3) {
            player.sendMessage(PREFIX + "Dieses Handy besitzt nicht die modernsten Funkausstattungen.");
            return true;
        }*/
        Player callPlayer = Script.getPlayer(args[0]);
        if(callPlayer == null) {
            player.sendMessage(PREFIX + "§c" + args[0] + " §7wurde nicht gefunden.");
            return true;
        }
        if(!Call.isOnActiveCall(callPlayer)) {
            player.sendMessage(PREFIX + "§c" + args[0] + " §7tätigt gerade keinen Anruf.");
            return true;
        }
        int callId = Call.getCallIDByPlayer(callPlayer);
        boolean hasWanteds = Call.ON_CALL.get(callId).stream().anyMatch(player1 -> Fahndung.getWanteds(player1) != 0);
        if(!hasWanteds) {
            player.sendMessage(PREFIX + "Nach §c" + args[0] + " §7wird nicht gefahndet.");
            return true;
        }
        wiretap.put(player.getUniqueId(), new PlayerWiretap(player, callId, System.currentTimeMillis()));
        player.sendMessage(PREFIX + "Du hörst nun den Anruf von §c" + args[0] + " §7ab.");
        updateRunnable();
        return true;
    }

    public static void sendNotification(int callId) {
        List<Player> players = Call.ON_CALL.get(callId);
        if(players.size() != 2) {
            return;
        }
        boolean hasWanteds = players.stream().anyMatch(player1 -> Fahndung.getWanteds(player1) != 0);
        if(!hasWanteds) {
            return;
        }
        /*for (UUID memberUUID : Beruf.Berufe.BUNDESKRIMINALAMT.getMember()) {
            final Player memberPlayer = Bukkit.getPlayer(memberUUID);
            if(memberPlayer != null) {
                Script.sendClickableMessage(memberPlayer, PREFIX + players.get(0).getName() + " hat einen Anruf mit " + players.get(1).getName() + " gestartet.", "/abhören " + players.get(0).getName(), "§cAnruf Abhören");
            }
        }*/
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
        private final int callId;
        private long millis;

        public void update() {
            long newMillis = System.currentTimeMillis();
            if(!Call.ON_CALL.containsKey(callId)) {
                player.sendMessage(PREFIX + "Die Abhörung wurde unterbrochen, da der Anruf beendet wurde.");
                wiretap.remove(player.getUniqueId());
                return;
            }
            if(millis > newMillis) {
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
            Script.sendActionBar(player, PREFIX + "Dir wurden 50€ Abhörgebühren abgerechnet.");
        }

    }

}
