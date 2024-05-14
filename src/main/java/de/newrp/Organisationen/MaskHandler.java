package de.newrp.Organisationen;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.newrp.API.Debug;
import de.newrp.API.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MaskHandler implements Listener {

    public static final String PREFIX = "§8[§6Mask§8] §6" + Messages.ARROW + " §7";


    public static HashMap<UUID, Long> masks = new HashMap<>();

    @EventHandler
    public static void onMask(PlayerArmorChangeEvent event) {
        if (event.getSlotType() == PlayerArmorChangeEvent.SlotType.HEAD) {
            if (event.getNewItem() != null) {
                if (event.getNewItem().getType() == Material.CARVED_PUMPKIN) {
                    if (Objects.requireNonNull(event.getOldItem()).getType() == Material.AIR) {
                        masks.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 10 * 60 * 1000);
                        event.getPlayer().sendMessage(Component.text(PREFIX + "Du hast dir eine Maske übergezogen."));
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onWear(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().getType() == Material.CARVED_PUMPKIN) {
                if (event.getHand() != null) {
                    if (event.getPlayer().getInventory().getItem(event.getHand()) != null) {
                        if (event.getPlayer().getInventory().getItem(EquipmentSlot.HEAD) != null) {
                            if (Objects.requireNonNull(event.getPlayer().getInventory().getItem(EquipmentSlot.HEAD)).getType() == Material.CARVED_PUMPKIN) {
                                event.getPlayer().sendMessage(Component.text(PREFIX + "Du hast bereits eine Maske angezogen!"));
                                return;
                            }
                        }
                        int amount = Objects.requireNonNull(event.getPlayer().getInventory().getItem(event.getHand())).getAmount();
                        Objects.requireNonNull(event.getPlayer().getInventory().getItem(event.getHand())).setAmount(amount - 1);
                        event.getPlayer().getInventory().setItem(EquipmentSlot.HEAD, new ItemStack(Material.CARVED_PUMPKIN));
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                if (event.getSlot() == 39) {
                    if (event.getCurrentItem() != null) {
                        if (event.getCurrentItem().getType() == Material.CARVED_PUMPKIN) {
                            masks.remove(event.getWhoClicked().getUniqueId());
                            event.getWhoClicked().getInventory().setItem(EquipmentSlot.HEAD, new ItemStack(Material.AIR));
                            event.getWhoClicked().sendMessage(PREFIX + "Du hast deine Maske abgezogen.");
                        }
                    }
                }
            }
        }
    }

    public static void clearMasksOutOfEnderchest(final Player player) {
        final Inventory inventory = player.getEnderChest();
        for (ItemStack content : inventory.getContents()) {
            if(content == null) {
                continue;
            }

            if(content.getType() !=  Material.CARVED_PUMPKIN) {
                continue;
            }

            inventory.remove(content);
            Debug.debug("Removing mask with amount " + content.getAmount() + " from " + player.getName());
        }
    }
}
