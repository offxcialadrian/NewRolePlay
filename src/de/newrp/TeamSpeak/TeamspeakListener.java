package de.newrp.TeamSpeak;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
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
}