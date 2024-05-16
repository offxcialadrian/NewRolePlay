package de.newrp.TeamSpeak;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import de.newrp.API.Messages;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class TeamspeakUpdate implements Listener {

    public static boolean SELF_TRY = true;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            try {
                String name = "[cspacer]» " + (Bukkit.getOnlinePlayers().size()) + "/" + (Bukkit.getServer().getMaxPlayers()) + " Spieler online «";
                if (!TeamSpeak.getApi().getChannelInfo(4).getName().equals(name)) {
                    Map<ChannelProperty, String> options = new HashMap<>();
                    options.put(ChannelProperty.CHANNEL_NAME, name);
                    TeamSpeak.getApi().editChannel(4, options);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                Script.sendTeamMessage(TeamSpeak.PREFIX + "§cDie Verbindung zum TeamSpeak-System wurde unterbrochen.");
                Script.sendTeamMessage(Messages.INFO + "Bei längeren Ausfällen bitte manuell neu starten.");
			if(SELF_TRY) {
				Script.sendTeamMessage(TeamSpeak.PREFIX + "§cVersuche automatisch verbindung herzustellen...");
				try {
					Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), TeamSpeak::disconnect);
				} catch (Exception exc) {
                    Script.sendTeamMessage(TeamSpeak.PREFIX + "§cDie Verbindung zum Teamspeak konnte nicht automatisch hergstellt werden.");
				} finally {
					Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), TeamSpeak::connect);
                    Script.sendTeamMessage(TeamSpeak.PREFIX + "§aVerbindung zum Teamspeak wieder aufgebaut.");
				}
			}
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
            try {
                String name = "[cspacer]» " + (Bukkit.getOnlinePlayers().size()) + "/" + (Bukkit.getServer().getMaxPlayers()) + " Spieler online «";
                if (!TeamSpeak.getApi().getChannelInfo(4).getName().equals(name)) {
                    Map<ChannelProperty, String> options = new HashMap<>();
                    options.put(ChannelProperty.CHANNEL_NAME, name);
                    TeamSpeak.getApi().editChannel(4, options);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }
}