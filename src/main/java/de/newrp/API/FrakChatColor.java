package de.newrp.API;

import de.newrp.Administrator.Notifications;
import de.newrp.Berufe.Beruf;
import de.newrp.Organisationen.Organisation;
import de.newrp.NewRoleplayMain;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class FrakChatColor implements CommandExecutor, Listener {
    public final HashMap<String, String> cache = new HashMap<>();
    public static HashMap<String, String> token_hash = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender cs,Command cmd,String s, String[] args) {

        if(cs instanceof Player) {

            Player p = (Player) cs;
            if(args.length == 0) {
                if (getToken(p) == null) {
                    p.sendMessage(Messages.ERROR + "Du hast kein Fraktions-Chat Farb-Token.");
                    if(Beruf.isLeader(p, true) || Organisation.isLeader(p, true)) p.sendMessage(Messages.INFO + "Nutze /chatcolor [Token] um die Farbe zu ändern.");
                    return true;
                }

                p.sendMessage(Messages.INFO + "Dein Fraktions-Chat Farb-Token: §c" + getToken(p));
                if(Beruf.isLeader(p, true) || Organisation.isLeader(p, true)) p.sendMessage(Messages.INFO + "Nutze /chatcolor [Token] um die Farbe zu ändern.");
                return true;
            }

            if(args.length == 1) {
                String token = args[0];
                if(!checkIfTokenExists(token)) {
                    p.sendMessage(Messages.ERROR + "Dieser Token existiert nicht.");
                    return true;
                }

                if(!Beruf.isLeader(p, true) && !Organisation.isLeader(p, true)) {
                    p.sendMessage(Messages.ERROR + "Du bist kein Leader.");
                    return true;
                }

                token_hash.put(p.getName(), token);
                openGUI(p);

            }

        }

        if (!(cs instanceof ConsoleCommandSender)) return true;

        OfflinePlayer player = Script.getOfflinePlayer(args[0]);
        String token = generateToken();

        if(player.isOnline()) {
            Player p = player.getPlayer();
            assert p != null;
            Script.addEXP(p, 50);
            p.sendMessage(Messages.INFO + "Dein Fraktions-Chat Farb-Token: §c" + token);
            Notifications.sendMessage(Notifications.NotificationType.NRPSHOP, Script.getName(p) + " hat ein Fraktions-Chat Farb-Token erworben.");
            Script.executeUpdate("INSERT INTO frakcolor_token (nrp_id, token) VALUES (" + Script.getNRPID(player) + ", '" + token + "')");
            return true;
        }

        Script.addEXP(Script.getNRPID(player), 50);
        Script.addOfflineMessage(player, Messages.INFO + "Dein Fraktions-Chat Farb-Token: §c" + token);
        Script.executeUpdate("INSERT INTO frakcolor_token (nrp_id, token) VALUES (" + Script.getNRPID(player) + ", '" + token + "')");

        return false;
    }


    public static String generateToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    public static String getToken(Player p) {
        return Script.getString(p, "frakcolor_token", "token");
    }

    public static boolean checkIfTokenExists(String token) {
        return Script.checkIfEntryExists("frakcolor_token", "token", token);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§6F-Chat Farbe")) return;

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().equals(Material.AIR) || !item.hasItemMeta())
            return;

        if (e.getClickedInventory().getType() != e.getView().getTopInventory().getType()) return;

        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        e.getView().close();

        Organisation o = Organisation.getOrganisation(p);
        Beruf.Berufe b = Beruf.getBeruf(p);

        if (o == null && b == null) return;
        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        String color = String.valueOf(name.charAt(1));
        String color1;
        if (cache.containsKey(p.getName())) {
            Script.executeUpdate("DELETE FROM frakcolor_token WHERE token='" + token_hash.get(p.getName()) + "'");
            color1 = cache.get(p.getName());
            p.sendMessage(Messages.INFO + "Du hast die Farben von dem Chat deines Berufes/deiner Organisation geändert.");
            if(o != null) {
                o.sendLeaderMessage(Organisation.PREFIX +  Script.getName(p) + " hat die Farbe des Organisations-Chat geändert.");
                Script.executeUpdate("DELETE FROM org_chat_color WHERE orgID=" + o.getID());
                Script.executeUpdate("INSERT INTO org_chat_color (orgID, color1, color2) VALUES (" + o.getID() + ", '" + color1 + "', '" + color + "')");
            } else {
                b.sendLeaderMessage(Beruf.PREFIX +  Script.getName(p) + " hat die Farbe des Berufs-Chat geändert.");
                Script.executeUpdate("DELETE FROM beruf_chat_color WHERE berufID=" + b.getID());
                Script.executeUpdate("INSERT INTO beruf_chat_color (berufID, color1, color2) VALUES (" + b.getID() + ", '" + color1 + "', '" + color + "')");
            }
            cache.remove(p.getName());
            token_hash.remove(p.getName());

        } else {
            p.sendMessage(Messages.INFO + "Farbe des Namens ausgewählt.");
            openGUI(p);
            cache.put(p.getName(), color);
        }
    }

    public static void openGUI(Player p) {
        Inventory inv = p.getServer().createInventory(null, 18, "§6F-Chat Farbe");
        inv.setItem(0, Script.setName(new ItemStack(Material.ORANGE_WOOL, 1), "§6Orange"));
        inv.setItem(1, Script.setName(new ItemStack(Material.PURPLE_WOOL, 1), "§5Lila"));
        inv.setItem(2, Script.setName(new ItemStack(Material.LIGHT_BLUE_WOOL, 1), "§bHellblau"));
        inv.setItem(3, Script.setName(new ItemStack(Material.YELLOW_WOOL, 1), "§eGelb"));
        inv.setItem(4, Script.setName(new ItemStack(Material.LIME_WOOL, 1), "§aHellgrün"));
        inv.setItem(5, Script.setName(new ItemStack(Material.PINK_WOOL, 1), "§dPink"));
        inv.setItem(6, Script.setName(new ItemStack(Material.LIGHT_GRAY_WOOL, 1), "§7Hellgrau"));
        inv.setItem(7, Script.setName(new ItemStack(Material.GRAY_WOOL, 1), "§8Dunkelgrau"));
        inv.setItem(8, Script.setName(new ItemStack(Material.CYAN_WOOL, 1), "§3Türkis"));
        inv.setItem(9, Script.setName(new ItemStack(Material.RED_WOOL, 1), "§cHellrot"));
        inv.setItem(10, Script.setName(new ItemStack(Material.BLUE_WOOL, 1), "§9Blau"));
        inv.setItem(11, Script.setName(new ItemStack(Material.GREEN_WOOL, 1), "§2Grün"));
        inv.setItem(12, Script.setName(new ItemStack(Material.RED_WOOL, 1), "§4Rot"));
        p.openInventory(inv);
    }

    public static String getNameColor(Beruf.Berufe b) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM beruf_chat_color WHERE berufID=" + b.getID())) {
            if (rs.next()) {
                return rs.getString("color1");
            }
        } catch (Exception e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return "e";
    }

    public static String getNameColor(Organisation o) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM org_chat_color WHERE orgID=" + o.getID())) {
            if (rs.next()) {
                return rs.getString("color1");
            }
        } catch (Exception e) {
            Debug.debug("SQLException -> " + e.getMessage());
            e.printStackTrace();
        }
        return "e";
    }

    public static String getTextColor(Beruf.Berufe b) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM beruf_chat_color WHERE berufID=" + b.getID())) {
            if (rs.next()) {
                return rs.getString("color2");
            }
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
        return "e";
    }

    public static String getTextColor(Organisation o) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM org_chat_color WHERE orgID=" + o.getID())) {
            if (rs.next()) {
                return rs.getString("color2");
            }
        } catch (Exception e) {
            NewRoleplayMain.handleError(e);
        }
        return "e";
    }

}
