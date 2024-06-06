package de.newrp.features.bizwar.data;

import de.newrp.API.Friedhof;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Organisation;
import de.newrp.Shop.Shops;
import de.newrp.features.bizwar.IBizWarService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@AllArgsConstructor
public class ActiveBizWarInformation {

    private final Shops attackedShop;
    private final Organisation attackerOrganisation;
    private final Organisation defenderOrganisation;
    private final long startTimestamp;
    private final Set<UUID> joinedMembersOfAttackers;
    private final Set<UUID> joinedMembersOfDefenders;
    private final Location attackerSpawn;
    private final Location defenderSpawn;
    private int currentAttackerPoints;
    private int currentDefenderPoints;
    private int schedulerId;

    public void startBizWarScheduler(final IBizWarService service) {
        this.schedulerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(NewRoleplayMain.getInstance(), () -> tickBizWar(service), 0, 10);
    }

    public void tickBizWar(final IBizWarService bizWarService) {
        final long endTime = this.startTimestamp + TimeUnit.MINUTES.toMillis(7);
        final long minutesPassedSinceStart = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - this.startTimestamp);
        System.out.println(minutesPassedSinceStart);
        if(minutesPassedSinceStart >= 7) {
            bizWarService.finishBizWar(this.attackedShop);
            Bukkit.getScheduler().cancelTask(this.schedulerId);
            return;
        }

        final long minutesReamining = 7 - minutesPassedSinceStart;
        final long secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(endTime - System.currentTimeMillis());
        String time = " §7§o(" + minutesReamining + " " + (minutesReamining == 1 ? "Minute" : "Minuten") + " verbleibend)";

        if(minutesReamining == 6) {
            time = " §7§o(" + secondsRemaining + " " + (secondsRemaining == 1 ? "Sekunde" : "Sekunden") + " verbleibend)";
        }


        for (UUID joinedMembersOfAttacker : this.getJoinedMembersOfAttackers()) {
            final Player player = Bukkit.getPlayer(joinedMembersOfAttacker);
            if(player == null) continue;

            if(Friedhof.isDead(player)) continue;
            Script.sendActionBar(player, "§a" + this.currentAttackerPoints + " §7| §c" + this.currentDefenderPoints + time);
        }

        for (UUID joinedMembersOfDefender : this.getJoinedMembersOfDefenders()) {
            final Player player = Bukkit.getPlayer(joinedMembersOfDefender);
            if(player == null) continue;

            if(Friedhof.isDead(player)) continue;
            Script.sendActionBar(player, "§a" + this.currentDefenderPoints + " §7| §c" + this.currentAttackerPoints + time);
        }
    }


}
