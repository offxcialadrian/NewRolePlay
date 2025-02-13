package de.newrp.Administrator;

import de.newrp.API.*;
import de.newrp.Police.Handschellen;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.addiction.IAddictionService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class HealCommand implements CommandExecutor {

    private static final String PREFIX = "§8[§cHeal§8] §c" + Messages.ARROW + " ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player p = (Player) cs;

        if (!Script.hasRank(p, Rank.DEVELOPER, false)) {
            p.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (!SDuty.isSDuty(p)) {
            p.sendMessage(Messages.NO_SDUTY);
            return true;
        }

        if (args.length > 1) {
            p.sendMessage(Messages.ERROR + "/heal {Spieler}");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(PREFIX + "Du hast dich geheilt.");
            Script.sendTeamMessage(p, ChatColor.RED, "hat sich geheilt.", true);
            Log.NORMAL.write(p, "hat sich geheilt.");
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            Health.BLOOD.set(Script.getNRPID(p), Health.BLOOD.getMax());
            Health.BLEEDING.remove(p.getName());
            p.setFireTicks(0);
            Script.unfreeze(p);

            if(!Handschellen.isCuffed(p)) {
                for (PotionEffect e : p.getActivePotionEffects()) {
                    p.removePotionEffect(e.getType());
                }
            } else {
                p.sendMessage(PREFIX + "Die Effekte wurden nicht entfernt, da du Handschellen trägst.");
            }

            Krankheit.GEBROCHENER_ARM.remove(Script.getNRPID(p));
            Krankheit.GEBROCHENES_BEIN.remove(Script.getNRPID(p));
            Krankheit.HUSTEN.remove(Script.getNRPID(p));
            Health.THIRST.set(Script.getNRPID(p), Health.THIRST.getMax());
            p.setWalkSpeed(0.2F);
            if(Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
                DependencyContainer.getContainer().getDependency(IAddictionService.class).clearAddiction(p);
            }
            return true;
        }

        Player tg = Bukkit.getPlayer(args[0]);
        if (tg == null) {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }
        
        p.sendMessage(PREFIX + "Du hast " + Script.getName(tg) + " geheilt.");
        tg.sendMessage(PREFIX + Script.getRank(p).getName(p) + " " + Script.getName(p) + " hat dich geheilt.");
        Script.sendTeamMessage(p, ChatColor.RED, "hat " + Script.getName(tg) + " geheilt", true);
        Log.NORMAL.write(p, "hat " + Script.getName(tg) + " geheilt.");
        Log.HIGH.write(tg, "wurde von " + Script.getName(p) + " geheilt.");
        tg.setHealth(tg.getMaxHealth());
        tg.setFoodLevel(20);
        tg.setFireTicks(0);
        Health.BLOOD.set(Script.getNRPID(tg), Health.BLOOD.getMax());
        Health.BLEEDING.remove(tg.getName());
        Krankheit.GEBROCHENER_ARM.remove(Script.getNRPID(tg));
        Krankheit.GEBROCHENES_BEIN.remove(Script.getNRPID(tg));
        Health.THIRST.set(Script.getNRPID(tg), Health.THIRST.getMax());
        tg.setWalkSpeed(0.2F);
        Script.unfreeze(tg);
        if(!Handschellen.isCuffed(tg)) {
            for (PotionEffect e : p.getActivePotionEffects()) {
                tg.removePotionEffect(e.getType());
            }
        } else {
            p.sendMessage(PREFIX + "Die Effekte wurden nicht entfernt, da der Spieler Handschellen trägt.");
        }
        return false;
    }
}
