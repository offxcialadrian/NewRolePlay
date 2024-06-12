package de.newrp.API;

import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import de.newrp.Organisationen.Drogen;
import de.newrp.Shop.ShopNPC;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinLayers;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Dealer implements Listener {

    public static NPC npc = null;

    public static final String PREFIX = "§8[§eDealer§8]§e " + Messages.ARROW + " §7";

    @Getter
    private static boolean undercover = false;

    public static void spawn() {
        undercover = new Random().nextInt(5) == 0;
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "§eDealer");
        npc.getOrAddTrait(SkinTrait.class).setSkinName("AbuBerke");
        npc.getOrAddTrait(SkinLayers.class).hideCape();
        npc.spawn(getRandomLoc());
        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> ShopNPC.addNpc(null, npc), 10 * 20L);
    }

    public static void respawn() {
        undercover = new Random().nextInt(5) == 0;
        npc.despawn();
        npc.spawn(getRandomLoc());
    }

    private static Location getRandomLoc() {
        switch (new Random().nextInt(4)) {
            case 1:
                return new Location(Script.WORLD, 570.2, 66, 1337.7, -336f, 0.7f);
            case 2:
                return new Location(Script.WORLD, 758.7, 65, 1209.8, -45.2f, 2.8f);
            case 3:
                return new Location(Script.WORLD, 752.1, 66, 1325.5, -232.1f, 0.2f);
            default:
                return new Location(Script.WORLD, 656.4, 65, 1252.4, -310.1f, 2.6f);
        }
    }

    public static final String[] TEXT_PRE_TRADE = new String[]{"Hast du ein bisschen Stoff?", "Das sind keine Drogen.", "Ich brauche ein bisschen Zeug, hast du was?", "Ey, wenn du was zum Verkaufen hast, komm zu mir.", "Yo, gib die Tütchen schnell her bevor uns jemand sieht!"};
    public static final String[] TEXT_POST_TRADE = new String[]{"Danke fürs Geschäft.", "Hier hast du das Geld, verschwinde nun!", "Gute Ware, geh nun lieber.", "Schnell, geh bevor die Polizei kommt!", "Nächstes mal gibts Stammkundenrabatt!", "Ich hoffe mal das ist guter Stoff..."};

    @EventHandler
    public static void onClick(NPCRightClickEvent event) {
        NPC clicked = event.getNPC();
        if (clicked.getId() == npc.getId()) {
            Player player = event.getClicker();
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                player.sendMessage(PREFIX + TEXT_PRE_TRADE[new Random().nextInt(TEXT_PRE_TRADE.length)]);
                return;
            }
            if(!player.getInventory().getItemInMainHand().hasItemMeta()) return;
            if(!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;
            Drogen drug = Drogen.getItemByName(ChatColor.stripColor(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName()));
            if (drug == null) return;
            if(!player.getInventory().getItemInMainHand().getItemMeta().hasLore()) return;
            Drogen.DrugPurity purity = Drogen.DrugPurity.getPurityByName(player.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).replace("§7Reinheitsgrad: ", ""));
            if (purity == null) return;
            int price;
            switch (drug) {
                case PULVER:
                    price = 30;
                    break;
                case KRÄUTER:
                    price = 35;
                    break;
                case ECSTASY:
                    price = 400;
                    break;
                case KRISTALLE:
                    price = 1000;
                    break;
                case ANTIBIOTIKA:
                    price = 10;
                    break;
                default:
                    return;
            }
            int amount = player.getInventory().getItemInMainHand().getAmount();
            price *= amount;
            price -= (int) Math.round(price * 0.2 * purity.getID());
            Script.addMoney(player, PaymentType.CASH, price);
            player.sendMessage(PREFIX + TEXT_POST_TRADE[new Random().nextInt(TEXT_POST_TRADE.length)]);
            if (isUndercover()) Beruf.Berufe.BUNDESNACHRICHTENDIENST.sendMessage(PREFIX + "Der Undercover-Dealer meldet einen Verkauf von " + amount + "g " + drug.getName() + " von " + player.getName() + ".");
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }
    }
}
