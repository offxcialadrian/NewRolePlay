package de.newrp.Berufe;

import de.newrp.API.Messages;
import de.newrp.API.UseDrogen;
import de.newrp.API.Utils;
import de.newrp.Administrator.SDuty;
import de.newrp.Chat.Me;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Drogen;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class DrugTestCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§5DrugTest§8] §5" + Messages.ARROW + " §e";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Beruf.hasBeruf(player) && !SDuty.isSDuty(player)) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (Beruf.hasBeruf(player) && !SDuty.isSDuty(player)) {
                if (Beruf.getBeruf(player) != Beruf.Berufe.POLICE && Beruf.getBeruf(player) != Beruf.Berufe.RETTUNGSDIENST) {
                    player.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }
            }

            if (args.length == 0) {
                player.sendMessage(Messages.ERROR + "/drugtest [player]");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Messages.ERROR + "Der Spieler wurde nicht gefunden.");
                return true;
            }

            if (player.getLocation().distance(target.getLocation()) > 3) {
                player.sendMessage(Messages.ERROR + "Der Spieler befindet sich nicht in deiner Nähe.");
                return true;
            }

            Drogen drug;
            long last;
            if (Drogen.lastDrug.containsKey(target.getUniqueId())) {
                drug = Drogen.lastDrug.get(target.getUniqueId());
                last = Drogen.lastUse.get(target.getUniqueId());
            } else {
                drug = null;
                last = 0;
            }

            if (!SDuty.isSDuty(player)) {
                Me.sendMessage(player, "überprüft den Drogenkonsum von " + target.getName() + ".");
                Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
                    if (player.getLocation().distance(target.getLocation()) > 3) {
                        player.sendMessage(Messages.ERROR + "Der Spieler hat sich zu weit von dir entfernt.");
                    } else {
                        player.sendMessage(PREFIX + "Drogen-Werte von " + target.getName() + ": " + (System.currentTimeMillis() - last < TimeUnit.MINUTES.toMillis(15) ? drug.getName() + " (" + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - last) + "min)" : "Keine Daten"));
                    }
                }, 5 * 20L);
                return true;
            }

            player.sendMessage(PREFIX + "Drogen-Werte von " + target.getName() + ": " + (last > 0 ? drug.getName() + " (" + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - last) + "min)" : "Keine Daten"));
        }

        return true;
    }
}
