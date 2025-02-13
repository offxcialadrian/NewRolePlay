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
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.deathmatcharena.IDeathmatchArenaService;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UseDrogenCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(args.length < 1) {
            p.sendMessage(Messages.ERROR + "/use [Substanz] [Reinheitsgrad]");
            return true;
        }

        if(args[0].toLowerCase().startsWith("schmerzmittel")) {
            Medikamente m = Medikamente.getMedikament(args[0].replace("-", " "));
            if (m != null) {
                if(!Script.isInTestMode()) {
                    if(!DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class).isInDeathmatch(p, false)) {
                        Long lastUsage = UseDrogen.DRUG_COOLDOWN.get(p.getName());
                        if (lastUsage != null && lastUsage + TimeUnit.SECONDS.toMillis(15) > System.currentTimeMillis()) {
                            long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.SECONDS.toMillis(15) - System.currentTimeMillis());
                            Script.sendActionBar(p, Messages.ERROR + "Du bist gerade noch im Rausch. (" + cooldown + " Sekunden verbleibend)");
                            return true;
                        }
                    }
                }

                if (Handschellen.isCuffed(p) || Fesseln.isTiedUp(p)) {
                    Script.sendActionBar(p, Messages.ERROR + "Du kannst keine Drogen konsumieren, wenn du gefesselt bist.");
                    return true;
                }

                final int amountOfMedicationsOfType = Medikamente.getAmountOfMedications(p, m);
                if(amountOfMedicationsOfType == 0) {
                    p.sendMessage(Messages.ERROR + "Du hast kein Schmerzmittel!");
                    return true;
                }
                Medikamente.removeMedication(p, m);

                if (m == Medikamente.SCHMERZMITTEL) {
                    Me.sendMessage(p, "nimmt ein Schmerzmittel ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);

                    if(!p.hasPotionEffect(PotionEffectType.REGENERATION)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 15, 1));
                    }
                    return true;
                } else if (m == Medikamente.SCHMERZMITTEL_HIGH) {
                    Me.sendMessage(p, "nimmt ein Schmerzmittel ein.");
                    Script.playLocalSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 5);
                    if (!p.hasPotionEffect(PotionEffectType.ABSORPTION))
                        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 220 * 20, 2, false, false));

                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 2));
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
        if(!Script.isInTestMode()) {
            if(!DependencyContainer.getContainer().getDependency(IDeathmatchArenaService.class).isInDeathmatch(p, false)) {
                if (lastUsage != null && lastUsage + TimeUnit.SECONDS.toMillis(15) > System.currentTimeMillis()) {
                    long cooldown = TimeUnit.MILLISECONDS.toSeconds(lastUsage + TimeUnit.SECONDS.toMillis(15) - System.currentTimeMillis());
                    Script.sendActionBar(p, Messages.ERROR + "Du bist gerade noch im Rausch. (" + cooldown + " Sekunden verbleibend)");
                    return true;
                }
            }
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> args1 = new ArrayList<>();
        for (Drogen drug : Drogen.values()) args1.add(drug.getName());
        args1.add("Schmerzmittel");
        args1.add("Schmerzmittel-(High)");
        String[] args2 = new String[] {"0", "1", "2", "3"};
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (String string : args1) if (string.toLowerCase().startsWith(args[0].toLowerCase())) completions.add(string);
        } else if (args.length == 2) {
            for (String string : args2) if (string.toLowerCase().startsWith(args[1].toLowerCase())) completions.add(string);
        }
        return completions;
    }
}
