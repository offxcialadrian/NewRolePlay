package de.newrp.Vehicle;

import de.newrp.API.HologramList;
import de.newrp.NewRoleplayMain;
import de.newrp.API.PaymentType;
import de.newrp.API.Script;
import de.newrp.Shop.Shop;
import de.newrp.Shop.ShopType;
import de.newrp.Shop.Shops;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Kennzeichen implements CommandExecutor, Listener {

    public static final String PREFIX = "§8[§cKennzeichen§8]§6 ";

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        if (p.getInventory().getItemInMainHand().getType() == Material.NAME_TAG && p.getInventory().getItemInMainHand().getAmount() == 1) {
            if (p.getLocation().distance(HologramList.KFZSTELLE.getLocation()) <= 6) {
                if (args.length < 2) {
                    p.sendMessage(PREFIX + "Du musst folgende Angaben machen: XX 0000");
                } else {
                    if (args[0].length() == 2 && args[1].length() == 4) {
                        if (Script.isInt(args[1])) {
                            int i = Integer.parseInt(args[1]);
                            String s1 = args[0];
                            if (!check(s1)) {
                                if (Script.getMoney(p, PaymentType.BANK) >= 60) {
                                    String kennzeichen = "N" + "-" + s1 + "-" + i;
                                    if (isInUse(kennzeichen)) {
                                        p.sendMessage(PREFIX + "Das Kennzeichen ist bereits vergeben.");
                                    } else {
                                        p.sendMessage(PREFIX + "Du hast das Kennzeichen " + kennzeichen + " beschriftet.");
                                        p.sendMessage(PREFIX + "Du musst nun nur noch das Kennzeichen anbringen (Rechtsklick)");
                                        ItemStack plate = p.getInventory().getItemInMainHand();
                                        ItemMeta plateMeta = plate.getItemMeta();
                                        plateMeta.setDisplayName(kennzeichen);
                                        plate.setItemMeta(plateMeta);
                                        Script.removeMoney(p, PaymentType.BANK, 60);
                                    }
                                } else {
                                    p.sendMessage(PREFIX + "Ein Kennzeichen kostet 60$!");
                                }
                            } else {
                                p.sendMessage(PREFIX + "Das Kennzeichen darf keine Sonderzeichen beinhalten!");
                            }
                        }
                    } else {
                        p.sendMessage(PREFIX + "Du musst folgende Angaben machen: XX 0000");
                    }
                }
            } else {
                p.sendMessage(PREFIX + "Du bist nicht bei einer KFZ-Anmeldestelle!");
            }
        } else {
            p.sendMessage(PREFIX + "Du hast kein Kennzeichen in der Hand!");
        }
        return true;
    }

    public boolean check(String s) {
        Pattern p = Pattern.compile("\\p{Alpha}*\\p{Punct}\\p{Alpha}*");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public boolean isInUse(String s) {
        try (PreparedStatement stmt = NewRoleplayMain.getConnection().prepareStatement("SELECT id FROM vehicle WHERE kennzeichen = ?")) {

            stmt.setString(1, s);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @EventHandler
    public static void onClick(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NAME_TAG) {
            if (event.getRightClicked() instanceof Boat) {
                Player player = event.getPlayer();
                Car car = Car.getCarByEntityID(event.getRightClicked().getEntityId());
                ItemStack plate = event.getPlayer().getInventory().getItemInMainHand();
                String license = plate.getItemMeta().getDisplayName();
                if (license.contains("Kennzeichen")) {
                    player.sendMessage(PREFIX + "Das Kennzeichen ist unbeschriftet!");
                } else {
                    if (car.isCarOwner(player)) {
                        car.setLicenseplate(license);
                        plate.setAmount(plate.getAmount() - 1);
                        player.sendMessage(PREFIX + "Das Kennzeichen deines " + car.getCarType().getName() + " wurde auf " + license + " gesetzt.");
                    } else {
                        player.sendMessage(PREFIX + "Dieses Auto gehört dir nicht!");
                    }
                }
                event.setCancelled(true);
            }
        }
    }
}
