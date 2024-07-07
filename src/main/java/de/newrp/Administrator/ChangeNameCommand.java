package de.newrp.Administrator;

import de.newrp.API.Messages;
import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChangeNameCommand implements CommandExecutor, Listener {

    public static final String prefix = "§8[§cChangeName§8] §8» §7";
    public static final List<String> locked = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;

        if(!Script.hasRank(player, Rank.ADMINISTRATOR, false)) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }
        if(!SDuty.isSDuty(player)) {
            player.sendMessage(Messages.NO_SDUTY);
            return true;
        }
        if(args.length == 0) {
            player.sendMessage(prefix + "Nutze: §c/changename [name]");
            return true;
        }
        OfflinePlayer offlinePlayer = Script.getOfflinePlayer(args[0]);
        if (Script.getNRPID(offlinePlayer) == 0) {
            player.sendMessage(prefix + "§cDer Spieler wurde nicht gefunden!");
            return true;
        }
        if(hasNameChange(offlinePlayer, false)) {
            if(args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
                removePlayer(offlinePlayer);
                player.sendMessage(prefix + "Du hast §c" + args[0] + " §7wieder entsperrt!");
                return true;
            }
            player.sendMessage(prefix + "Der Spieler §c" + args[0] + " §7ist bereits gesperrt!");
            player.sendMessage(Messages.INFO + "Du kannst ihn mit '/changename [name] confirm' wieder entsperren");
            return true;
        }
        if(offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().kickPlayer("§8» §cNRP × New RolePlay §8┃ §cKick §8« \n\n§8§m------------------------------\n\n§7Du wurdest vom Server gekickt§8.\n\n§7Grund §8× §e" + "Dein Ingame-Name ist hier nicht erlaubt.");
        }
        addPlayer(offlinePlayer);
        Script.sendTeamMessage(prefix + "§c" + Script.getName(player) + " §7hat §c" + args[0] + " §7eine Sperre gegeben.");
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!hasNameChange(player, true)) {
            return;
        }
        ChangeNameCommand.locked.add(player.getName());
        Bukkit.getScheduler().runTaskLater(NewRoleplayMain.getInstance(), () -> {
            player.sendMessage(prefix + "Du wurdest gefreezed da du einen bei uns unerlaubten Minecraft-Namen verwendest!");
            player.sendMessage(Messages.INFO + "Ändere dein Namen um wieder entsperrt zu werden");
        }, 20L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!isLocked(player)) {
            return;
        }
        event.setCancelled(true);
    }

    public static void addPlayer(OfflinePlayer player) {
        if(player.isOnline()) {
            ChangeNameCommand.locked.add(player.getName());
        }
        Script.executeAsyncUpdate("INSERT INTO change_name(nrp_id, name) VALUES(" + Script.getNRPID(player) + ", '" + player.getName() + "');");
    }

    public static void removePlayer(OfflinePlayer player) {
        if(player.isOnline()) {
            player.getPlayer().sendMessage(prefix + "Deine Sperre wurde aufgehoben!");
            ChangeNameCommand.locked.remove(player.getName());
        }
        Script.executeAsyncUpdate("DELETE FROM change_name WHERE nrp_id=" + Script.getNRPID(player));
    }

    public static boolean isLocked(Player player) {
        return ChangeNameCommand.locked.contains(player.getName());
    }

    public static boolean hasNameChange(OfflinePlayer player, boolean update) {
        try (Statement statement = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM change_name WHERE nrp_id= '" + Script.getNRPID(player) + "'")) {
            if(!update) {
                return rs.next();
            }
            if(!rs.next()) {
                return false;
            }
            if(player.getName() == null) {
                return false;
            }
            boolean hasInvalidName = player.getName().equals(rs.getString("name"));
            if(!hasInvalidName) {
                removePlayer(player);
            }
            return hasInvalidName;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
