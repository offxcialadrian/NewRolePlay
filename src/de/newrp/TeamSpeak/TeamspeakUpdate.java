package de.newrp.TeamSpeak;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import de.newrp.API.Script;
import de.newrp.main;
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
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try {
                String name = "[cspacer]» " + (Bukkit.getOnlinePlayers().size()) + "/" + (Bukkit.getServer().getMaxPlayers()) + " Spieler online «";
                if (!TeamSpeak.getApi().getChannelInfo(2).getName().equals(name)) {
                    Map<ChannelProperty, String> options = new HashMap<>();
                    options.put(ChannelProperty.CHANNEL_NAME, name);
                    TeamSpeak.getApi().editChannel(2, options);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                Script.sendTeamMessage("§cDie Verbindung zum TeamSpeak-System wurde unterbrochen.");
                Script.sendTeamMessage("Bei längeren Ausfällen bitte manuell neu starten.");
			/*AdminChat.sendMessage("Die Verbindung zum Teamspeak wurde unterbrochen.");
			if(SELF_TRY) {
				AdminChat.sendMessage("Versuche automatisch verbindung herzustellen...");
				try {
					Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), Teamspeak::disconnect);
				} catch (Exception exc) {
					AdminChat.sendMessage("Die Verbindung zum Teamspeak konnte nicht automatisch hergstellt werden.");
				} finally {
					Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), Teamspeak::connect);
					AdminChat.sendMessage("Verbindung zum Teamspeak wieder aufgebaut.");
				}
			}*/
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            try {
                String name = "[cspacer]» " + (Bukkit.getOnlinePlayers().size()) + "/" + (Bukkit.getServer().getMaxPlayers()) + " Spieler online «";
                if (!TeamSpeak.getApi().getChannelInfo(2).getName().equals(name)) {
                    Map<ChannelProperty, String> options = new HashMap<>();
                    options.put(ChannelProperty.CHANNEL_NAME, name);
                    TeamSpeak.getApi().editChannel(2, options);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }
}