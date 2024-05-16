package de.newrp.features.emergencycall.listener;

import de.newrp.API.Messages;
import de.newrp.Berufe.Beruf;
import de.newrp.Chat.Me;
import de.newrp.dependencies.DependencyContainer;
import de.newrp.features.emergencycall.IEmergencyCallService;
import de.newrp.features.emergencycall.inventory.EmergencyCallFactionSelectionInventory;
import de.newrp.features.emergencycall.inventory.EmergencyCallReasonSelectInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EmergencyCallInventoryListener implements Listener {

    private final IEmergencyCallService emergencyCallService = DependencyContainer.getContainer().getDependency(IEmergencyCallService.class);

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final Inventory clickedInventory = event.getClickedInventory();
        if(clickedInventory == null) {
            return;
        }

        final ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.getItemMeta() == null) {
            return;
        }

        if(clickedInventory.getHolder() instanceof EmergencyCallFactionSelectionInventory.EmergencyCallFactionSelectionInventoryHolder) {
            final Beruf.Berufe faction = getFactionBySlotId(event.getSlot());
            if(faction == null) {
                return;
            }

            if(this.emergencyCallService.getEmergencyCallByPlayer(player, faction).isPresent()) {
                player.sendMessage(this.emergencyCallService.getPrefix() + "Du hast bereits einen aktiven Notruf!");
                player.sendMessage(Messages.INFO + "Du kannst den Notruf über /cancelnotruf abbrechen");
                player.closeInventory();
                return;
            }

            if(this.emergencyCallService.isBlocked(player, faction)) {
                player.sendMessage(this.emergencyCallService.getPrefix() + "Deine Notruf sind blockiert!");
                player.sendMessage(Messages.INFO + "Du kannst die Sperre ggf. bei der Leitungseben des Berufes anfechten");
                player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_DESTROY, 1f, 1f);
                player.closeInventory();
                return;
            }

            final EmergencyCallReasonSelectInventory emergencyCallReasonSelectInventory = new EmergencyCallReasonSelectInventory(faction);
            emergencyCallReasonSelectInventory.openToPlayer(player);
        } else if(clickedInventory.getHolder() instanceof EmergencyCallReasonSelectInventory.EmergencyCallReasonSelectInventoryHolder) {
            final EmergencyCallReasonSelectInventory.EmergencyCallReasonSelectInventoryHolder holder = (EmergencyCallReasonSelectInventory.EmergencyCallReasonSelectInventoryHolder) clickedInventory.getHolder();
            final String reason = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            this.emergencyCallService.createEmergencyCall(player, player.getLocation(), holder.getFaction(), reason);
            player.sendMessage(this.emergencyCallService.getPrefix() + "Vielen Dank für Ihren Anruf. Die Polizei und/oder der Rettungsdienst werden sich umgehend um Ihr Anliegen kümmern.");
            Me.sendMessage(player, "wählt den Notruf auf seinem Handy.");
            player.closeInventory();
        }
    }


    private Beruf.Berufe getFactionBySlotId(final int slotId) {
        switch (slotId) {
            case 0:
                return Beruf.Berufe.POLICE;
            case 1:
                return Beruf.Berufe.RETTUNGSDIENST;
        }
        return null;
    }

}
