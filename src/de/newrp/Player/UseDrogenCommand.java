package de.newrp.Player;

import de.newrp.API.Krankheit;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.API.UseDrogen;
import de.newrp.Chat.Me;
import de.newrp.Medic.Medikamente;
import de.newrp.Medic.UseMedikamente;
import de.newrp.Organisationen.Drogen;
import de.newrp.Police.Handschellen;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class UseDrogenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length < 1) {
            p.sendMessage(Messages.ERROR + "/use [Substanz] [Reinheitsgrad]");
            return true;
        }

        if(args[0].startsWith("Schmerzmittel")) {
            Medikamente m = Medikamente.getMedikament(args[0].replace("-", " "));
            if (m != null) {
                Long lastUsage = UseDrogen.DRUG_COOLDOWN.get(p.getName());
                if (lastUsage != null && lastUsage + TimeUnit.SECONDS.toMillis(15) > System.currentTimeMillis()) {
                    long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.SECONDS.toMillis(15) - System.currentTimeMillis());
                    Script.sendActionBar(p, Messages.ERROR + "Du bist gerade noch im Rausch. (" + cooldown + " Sekunden verbleibend)");
                    return true;
                }

                if (Handschellen.isCuffed(p) || Fesseln.isTiedUp(p)) {
                    Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Drogen konsumieren, wenn du gefesselt bist.");
                    return true;
                }
                if (m == Medikamente.SCHMERZMITTEL) {
                    if (Krankheit.ABHAENGIGKEIT.isInfected(Script.getNRPID(p))) {
                        p.sendMessage(UseMedikamente.PREFIX + "Das konsumieren von Schmerzmitteln hat bei dir keine Wirkung gezeigt.");
                        Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 0));
                        return true;
                    }
                    Me.sendMessage(p, "nimmt ein Schmerzmittel ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 15, 1));
                    Drogen.addToAdiction(p);
                    return true;
                } else if (m == Medikamente.SCHMERZMITTEL_HIGH) {
                    if (Krankheit.ABHAENGIGKEIT.isInfected(Script.getNRPID(p))) {
                        p.sendMessage(UseMedikamente.PREFIX + "Das konsumieren von Schmerzmitteln hat bei dir keine Wirkung gezeigt.");
                        Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                        p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 220 * 20, 2, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 0));
                        return true;
                    }
                    Me.sendMessage(p, "nimmt ein Schmerzmittel ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                    if (!p.hasPotionEffect(PotionEffectType.ABSORPTION))
                        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 220 * 20, 2, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 15, 2));
                    Drogen.addToAdiction(p);
                    return true;
                }
            }
        }

        if(args.length != 2) {
            p.sendMessage(Messages.ERROR + "/use [Substanz] [Reinheitsgrad]");
            return true;
        }

        Drogen droge = Drogen.getItemByName(args[0]);
        if(droge == null) {
            p.sendMessage(Messages.ERROR + "Diese Substanz existiert nicht.");
            return true;
        }

        if(!Script.isInt(args[1])) {
            p.sendMessage(Messages.ERROR + "Der Reinheitsgrad muss eine Zahl sein.");
            return true;
        }

        Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByID(Integer.parseInt(args[1]));
        if(purity == null) {
            p.sendMessage(Messages.ERROR + "Dieser Reinheitsgrad existiert nicht.");
            return true;
        }

        Long lastUsage = UseDrogen.DRUG_COOLDOWN.get(p.getName());
        if (lastUsage != null && lastUsage + TimeUnit.SECONDS.toMillis(15) > System.currentTimeMillis()) {
            long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.SECONDS.toMillis(15) - System.currentTimeMillis());
            Script.sendActionBar(p, Messages.ERROR + "Du bist gerade noch im Rausch. (" + cooldown + " Sekunden verbleibend)");
            return true;
        }

        if(Handschellen.isCuffed(p) || Fesseln.isTiedUp(p)) {
            Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Drogen konsumieren, wenn du gefesselt bist.");
            return true;
        }

        for(ItemStack is : p.getInventory().getContents()) {
            if(is == null) continue;
            if(is.getItemMeta() == null) continue;
            is.getItemMeta().getDisplayName();
            if(is.getItemMeta().getDisplayName().equalsIgnoreCase(droge.getName()) && (Drogen.DrugPurity.getPurityByName(is.getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", "")) == purity)) {
                droge.consume(p, purity);
                UseDrogen.DRUG_COOLDOWN.put(p.getName(), System.currentTimeMillis());
                is.setAmount(is.getAmount() - 1);
                return true;
            }
        }

        p.sendMessage(Messages.ERROR + "Du hast kein " + droge.getName() + " mit dem Reinheitsgrad " + purity.getText() + " in deinem Inventar.");

        return false;
    }
}
