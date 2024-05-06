package de.newrp.Medic;

import de.newrp.API.*;
import de.newrp.Administrator.SDuty;
import de.newrp.Berufe.Beruf;
import de.newrp.Berufe.Duty;
import de.newrp.Chat.Me;
import de.newrp.Waffen.Waffen;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ReviveCommand implements CommandExecutor {

    private static final HashMap<String, Long> cooldown = new HashMap<>();
    public static final HashMap<String, Long> cooldowns = new HashMap<>();
    public static final HashMap<String, Long> exp_cooldowns = new HashMap<>();
    private static final String PREFIX = "§8[§cRevive§8] §c" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (args.length == 1 && Script.hasRank(p, Rank.SUPPORTER, false) && SDuty.isSDuty(p)) {

            Player tg = Script.getPlayer(args[0]);
            if (tg == null) {
                p.sendMessage(Messages.PLAYER_NOT_FOUND);
                return false;
            }
            if (!Friedhof.isDead(tg)) {
                p.sendMessage(Messages.ERROR + "Der Spieler ist nicht tot");
                return true;
            }

            long time = System.currentTimeMillis();
            if (cooldown.containsKey(p.getName()) && !Script.isInTestMode()) {
                Long lastUsage = cooldown.get(p.getName());
                if (lastUsage + 5 * 60 * 1000 > time) {
                    p.sendMessage(Messages.ERROR + "Du kannst nur alle 5 Minuten einen Spieler wiederbeleben.");
                    return false;
                }
            }

            Friedhof f = Friedhof.getDead(tg);
            p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " wiederbelebt.");
            tg.sendMessage(PREFIX + "Du wurdest von " + Messages.RANK_PREFIX(p) + " wiederbelebt.");
            Friedhof.revive(tg, f.getDeathLocation());
            Script.sendTeamMessage(p, ChatColor.RED, "hat " + Script.getName(tg) + " wiederbelebt.", true);
            if(Corpse.npcMap.containsKey(p)) Corpse.removeNPC(p);
            if (f.getInventoryContent() != null) {
                tg.getInventory().clear();
                tg.getInventory().setContents(f.getInventoryContent());
            }
            if(!Script.hasRank(p, Rank.ADMINISTRATOR, false)) cooldown.put(p.getName(), System.currentTimeMillis());
            if (f.getCash() > 0) Script.setMoney(tg, PaymentType.CASH, f.getCash());
            return true;
        }


        if(Beruf.getBeruf(p) != Beruf.Berufe.RETTUNGSDIENST) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Rettungsdienst.");
            return true;
        }

        if(!Duty.isInDuty(p)) {
            p.sendMessage(Messages.ERROR + "Du bist nicht im Dienst.");
            return true;
        }


        Friedhof f_c;
        f_c = Friedhof.getDeathByLocation(p.getLocation());
        if (f_c == null) {
            f_c = Friedhof.getDeathByLocation(p.getLocation());

            if (f_c != null && System.currentTimeMillis() > f_c.getDeathTime() + TimeUnit.SECONDS.toMillis(f_c.getHelpCounter() * 60L)) {
                f_c = null;
            }
        }
        if (f_c == null) {
            p.sendMessage(Messages.ERROR + "Du bist bei keiner Leiche.");
            return true;
        }

        final Friedhof f = f_c;

        Player tg = Script.getPlayer(f.getUsername());
        if (tg == null) {
            p.sendMessage(Messages.ERROR + "Du bist bei keiner Leiche.");
            return true;
        }

        if (!Friedhof.isDead(tg)) {
            p.sendMessage(Messages.ERROR + "Die Person lebt bereits.");
            return true;
        }


        long time = System.currentTimeMillis();
        Long lastUsage = cooldowns.get(p.getName());
        if (lastUsage != null) {
            if (lastUsage + 8 * 1000 > time) {
                p.sendMessage(Messages.ERROR + "Du belebst bereits eine Person wieder.");
                return false;
            }
        }

        if (cooldowns.containsKey(tg.getName())) {
            if (cooldowns.get(tg.getName()) + 8 * 1000 > time) {
                p.sendMessage(Messages.ERROR + "Die Person wird bereits wiederbelebt.");
                return false;
            }
        }

        p.sendMessage("§7Du beginnst mit der Wiederbelebung von " + Script.getName(tg) + "...");
        Me.sendMessage(p, "hat mit der Wiederbelebung von " + Script.getName(tg) + " begonnen.");
        Log.LOW.write(p, "hat mit der Wiederbelebung von " + Script.getName(tg) + " begonnen.");
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 8 * 20, 2));
        tg.sendMessage("§7Du wirst von " + Script.getName(p) + " wiederbelebt.");
        cooldowns.put(p.getName(), time);
        cooldowns.put(tg.getName(), time);
        final Location loc = p.getLocation();
        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
            if (p.getLocation().distance(f.getDeathLocation()) > 3D) {
                p.sendMessage(Messages.ERROR + "Du bist zuweit von der Leiche weg.");
                return;
            }
            if(Corpse.npcMap.containsKey(p)) Corpse.removeNPC(p);
            if (!Friedhof.isDead(tg)) {
                p.sendMessage(Messages.ERROR + "Die Person lebt bereits.");
                return;
            }
            Friedhof.revive(tg, loc);
            p.sendMessage("§7Du hast " + Script.getName(tg) + " erfolgreich wiederbelebt.");

            tg.resetPlayerWeather();
            Log.LOW.write(tg, "wurde von " + Script.getName(p) + " wiederbelebt.");
            if (f.getCash() > 0) Script.setMoney(tg, PaymentType.CASH, f.getCash());
            if (f.getInventoryContent() != null) {
                tg.getInventory().clear();
                tg.getInventory().setContents(f.getInventoryContent());
            }
            long time1 = System.currentTimeMillis();
            Long lastUsage1 = exp_cooldowns.get(p.getName());
            if (exp_cooldowns.containsKey(p.getName())) {
                if (lastUsage1 + 300 * 1000 > time1) {
                    return;
                }
            }
            Script.addEXP(p, Script.getRandom(4, 8));
            exp_cooldowns.put(p.getName(), time1);
            Waffen.REVIVE_COOLDOWN.put(p.getName(), System.currentTimeMillis());
        }, 5 * 20L);
        return false;
    }
}
