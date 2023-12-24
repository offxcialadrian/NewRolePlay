package de.newrp.TeamSpeak;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.newrp.API.Debug;
import de.newrp.API.Script;
import de.newrp.main;
import org.bukkit.Bukkit;

public class TeamspeakListener extends TS3EventAdapter {
    @Override
    public void onClientJoin(ClientJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            if (!e.getClientDescription().isEmpty()) return;
            TeamSpeak.sendClientMessage(e.getClientId(), "Willkommen " + e.getClientNickname() + ", auf dem Teamspeak von New RolePlay!",
                    "Um dich freizuschalten und den Teamspeak normal nutzen zu können, musst du dich erst einmal verifizieren.",
                    "Gib auf unserem Minecraft Server (newrp.de) \"/ts " + e.getUniqueClientIdentifier() + "\" ein, um dich zu verifizieren.");
        });
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            Debug.debug("Client moved: " + e.getClientId() + " " + e.getTargetChannelId());
            if (e.getTargetChannelId() != 15) return;
            Client client = TeamSpeak.getApi().getClientInfo(e.getClientId());
            Script.sendTeamMessage("§8[§cSupport§8] §c" + client.getNickname() + " §7hat den TeamSpeak-Support betreten.");
        });
    }

}