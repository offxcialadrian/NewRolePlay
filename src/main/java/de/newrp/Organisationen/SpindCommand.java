package de.newrp.Organisationen;

import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.NewRoleplayMain;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;

public class SpindCommand implements CommandExecutor, Listener {

    public static final String PREFIX = "§8[§eSpind§8] §e» §7";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!Beruf.hasBeruf(player) && !Organisation.hasOrganisation(player)) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            int id = 0;
            if (Beruf.hasBeruf(player)) {
                if (!Beruf.getAbteilung(player).isLeader() && !Beruf.isLeader(player, true)) {
                    player.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }
                if (player.getLocation().distance(Beruf.getBeruf(player).getEquipLoc()) > 7) {
                    player.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
                    return true;
                }
                Beruf.getBeruf(player).sendLeaderMessage(PREFIX + player.getName() + " greift auf den Spind zu.");
                id = Beruf.getBeruf(player).getID();
            }
            if (Organisation.hasOrganisation(player)) {
                if (Organisation.getRank(player) < 3 && !Organisation.isLeader(player, true)) {
                    player.sendMessage(Messages.ERROR + "Du musst Rang-3 sein um auf den Spind zugreifen zu können.");
                    return true;
                }
                if (player.getLocation().distance(Organisation.getOrganisation(player).getEquipLoc()) > 7) {
                    player.sendMessage(Messages.ERROR + "Du musst dich in der Nähe des Equip-Punktes befinden.");
                    return true;
                }
                id = -Organisation.getOrganisation(player).getID();
                Organisation.getOrganisation(player).sendLeaderMessage(PREFIX + player.getName() + " greift auf den Spind zu.");
            }

            Inventory inv = Bukkit.createInventory(player, 9, "§8» §7Spind");
            for (int i = 0; i < 9; i++) {
                if (i < 3 || i > 5) inv.setItem(i, Script.setName( new ItemStack(Material.WHITE_STAINED_GLASS_PANE), ""));
            }

            Inventory items = getInv(id);
            if (items == null) {
                for (int i = 3; i < 6; i++) inv.setItem(i, new ItemStack(Material.AIR));
            } else {
                for (int i = 3; i < 6; i++) inv.setItem(i, items.getItem(i));
            }

            player.openInventory(inv);
        }

        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getOpenInventory().title() instanceof TextComponent) {
            if (event.getView().getTitle().contains("Spind")) {
                if (event.getWhoClicked() instanceof Player) {
                    Player player = (Player) event.getWhoClicked();

                    if (!Beruf.hasBeruf(player) && !Organisation.hasOrganisation(player)) {
                        event.setCancelled(true);
                    }

                    int id = 0;
                    if (Beruf.hasBeruf(player)) {
                        id = Beruf.getBeruf(player).getID();
                    }
                    if (Organisation.hasOrganisation(player)) {
                        id = -Organisation.getOrganisation(player).getID();
                    }

                    if (event.getCurrentItem() != null) {
                        if (event.getCurrentItem().getType() == Material.WHITE_STAINED_GLASS_PANE) event.setCancelled(true);
                    }

                    setInv(id, event.getView().getTopInventory());
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().contains("Spind")) {
            if (event.getPlayer() instanceof Player) {
                Player player = (Player) event.getPlayer();

                if (!Beruf.hasBeruf(player) && !Organisation.hasOrganisation(player)) {
                    return;
                }

                int id = 0;
                if (Beruf.hasBeruf(player)) {
                    id = Beruf.getBeruf(player).getID();
                }
                if (Organisation.hasOrganisation(player)) {
                    id = -Organisation.getOrganisation(player).getID();
                }

                setInv(id, event.getView().getTopInventory());
            }
        }
    }

    public Inventory getInv(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM spind WHERE id=" + id)) {
            if (rs.next()) return fromBase64(rs.getString("inv"));
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setInv(int id, Inventory inv) {
        if (getInv(id) == null) Script.executeUpdate("INSERT INTO spind (id, inv) VALUES (" + id + ", '" + toBase64(inv) + "')");
        Script.executeUpdate("UPDATE spind SET inv='" + toBase64(inv) + "' WHERE id=" + id);
    }

    public static String toBase64(Inventory inv) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(inv.getSize());

            for (int i = 0; i < inv.getSize(); i++) {
                dataOutput.writeObject(inv.getItem(i));
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
