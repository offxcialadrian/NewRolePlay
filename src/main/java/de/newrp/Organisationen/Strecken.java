package de.newrp.Organisationen;

import de.newrp.API.ItemBuilder;
import de.newrp.API.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Strecken implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) cs;

        if(!Organisation.hasOrganisation(p)) {
            p.sendMessage(Messages.ERROR + "Du bist in keiner Organisation.");
            return true;
        }

        p.getInventory().getItemInMainHand();
        if(p.getInventory().getItemInMainHand().getType() == Material.AIR || !p.getInventory().getItemInMainHand().hasItemMeta()) {
            p.sendMessage(Messages.ERROR + "Du hast keine Substanz in der Hand.");
            return true;
        }

        Drogen droge = Drogen.getItemByName(ChatColor.stripColor(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()));
        if(droge == null) {
            p.sendMessage(Messages.ERROR + "Du hast keine Substanz in der Hand.");
            return true;
        }
        Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByName(p.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", ""));
        if(purity == null) {
            p.sendMessage(Messages.ERROR + "Diese Substanz hat keinen Reinheitsgrad.");
            return true;
        }

        if(purity == Drogen.DrugPurity.BAD) {
            p.sendMessage(Messages.ERROR + "Diese Substanz ist zu schlecht um sie zu verarbeiten.");
            return true;
        }

        int amountOfMehl = 0;
        int amountOfSubstanz = 0;

        for(ItemStack is : p.getInventory().getContents()) {
            if(is == null) continue;
            if(is.getItemMeta() == null) continue;
            if(is.getItemMeta().getDisplayName().equalsIgnoreCase("§fMehl")) amountOfMehl += is.getAmount();
            if(is.getItemMeta().getDisplayName().equalsIgnoreCase(droge.getName()) && (Drogen.DrugPurity.getPurityByName(is.getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", "")) == purity)) amountOfSubstanz += is.getAmount();
        }

        if(amountOfMehl == 0) {
            p.sendMessage(Messages.ERROR + "Du hast kein Mehl.");
            return true;
        }

        if(amountOfSubstanz == 0) {
            p.sendMessage(Messages.ERROR + "Du hast keine Substanz.");
            return true;
        }

        if(amountOfMehl < amountOfSubstanz) {
            p.sendMessage(Messages.ERROR + "Du benötigst mindestens so viel Mehl wie Substanz.");
            return true;
        }

        ItemStack is = p.getInventory().getItemInMainHand().clone();
        p.getInventory().setItemInMainHand(null);
        for(ItemStack item : p.getInventory().getContents()) {
            if(item == null) continue;
            if(item.getItemMeta() == null) continue;
            if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§fMehl")) {
                if(item.getAmount() > amountOfSubstanz) {
                    item.setAmount(item.getAmount() - amountOfSubstanz);
                    break;
                } else {
                    amountOfMehl -= item.getAmount();
                    item.setAmount(0);
                }
            }
        }
        purity = Drogen.DrugPurity.getPurityByID(purity.getID() + 1);
        for(int i = 0; i < (amountOfSubstanz * 2); i++) {
            p.getInventory().addItem(new ItemBuilder(is.getType()).setName(is.getItemMeta().getDisplayName()).setLore("§7Reinheitsgrad: " + purity.getText()).build());
        }

        p.sendMessage(Organisation.PREFIX + "Du hast " + amountOfSubstanz + " " + ChatColor.stripColor(is.getItemMeta().getDisplayName()) + "§7 zu " + amountOfSubstanz + "x " + purity.getText() + " verarbeitet.");
        Organisation.getOrganisation(p).sendMessage(Organisation.PREFIX + p.getName() + " hat " + amountOfSubstanz + " " + ChatColor.stripColor(is.getItemMeta().getDisplayName()) + "§7 zu " + amountOfSubstanz + "x " + purity.getText() + " verarbeitet.");

        return false;
    }
}
