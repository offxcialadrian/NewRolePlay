package de.newrp.Votifier;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class VoteShop implements CommandExecutor {
    public static final String PREFIX = "§8[§6VoteShop§8]§6 " + Messages.ARROW + " §7";
    public static final HashMap<String, Integer> karma = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        p.sendMessage(Messages.ERROR + "Der Voteshop ist derzeit deaktiviert.");
        return true;
            /*Inventory inv = p.getServer().createInventory(null, 18, "§l§6Voteshop");
            inv.setItem(4, Script.setNameAndLore(Script.addGlow(new ItemStack(Material.NETHER_STAR)), "§3§lVoteshop", "§cDu hast §l" + (VoteListener.getVotepoints(Script.getNRPID(p))>0?VoteListener.getVotepoints(Script.getNRPID(p)):"keine") + "§c Votepunkte!"));
            inv.setItem(11, Script.setNameAndLore(new ItemStack(Material.CHEST, Votekiste.NORMAL.getPrice()), "§6Votekiste §7[§3§lNormal§7]", "§c" + Votekiste.NORMAL.getPrice() + " Votepunkte", "§8=========",
                    "§cPremium 1 Tag",
                    "§6750 Exp",
                    "§6500 Exp",
                    "§7Munition",
                    "§7Baseballschläger",
                    "§8..."));
            inv.setItem(13, Script.setNameAndLore(new ItemStack(Material.CHEST, Votekiste.SPECIAL.getPrice()), "§6Votekiste §7[§3§lSpecial§7]", "§c" + Votekiste.SPECIAL.getPrice() + " Votepunkte", "§8=========",
                    "§cPremium 7 Tage",
                    "§cPremium 3 Tage",
                    "§61500 Exp",
                    "§61250 Exp",
                    "§61000 Exp",
                    "§8..."));
            inv.setItem(15, Script.setNameAndLore(new ItemStack(Material.CHEST, Votekiste.ULTIMATE.getPrice()), "§6Votekiste §7[§3§lUltimate§7]", "§c" + Votekiste.ULTIMATE.getPrice() + " Votepunkte", "§8=========",
                    "§cPremium 14 Tage",
                    "§cPremium 7 Tage",
                    "§6ChangeToken (Personalausweis)",
                    "§61500 Exp",
                    "§62000 Exp",
                    "§62500 Exp",
                    "§8..."));
            p.openInventory(inv);
        return true;*/
    }

}
