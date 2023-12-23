package de.newrp.TeamSpeak;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.newrp.main;
import de.newrp.API.Premium;
import de.newrp.API.Script;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PremiumChannel implements CommandExecutor {
    public static final ArrayList<UUID> creator = new ArrayList<>();
    public static int channel = 1;

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player p = (Player) cs;
        int id = Script.getNRPID(p);
        if (Premium.hasPremium(p)) {
            if (hasChannel(p)) {
                p.sendMessage(TeamSpeak.PREFIX + "Du hast bereits ein eigenen Teamspeak Channel erstellt.");
            } else {
                if (args.length == 0) {
                    Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
                        try {
                            Map<ChannelProperty, String> properties = new HashMap<>();
                            properties.put(ChannelProperty.CHANNEL_NAME, "» Privater Talk [" + channel + "]");
                            properties.put(ChannelProperty.CHANNEL_DESCRIPTION, "» Privater Talk von " + p.getName() + ".");
                            properties.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
                            properties.put(ChannelProperty.CPID, "88");

                            TeamSpeak.getTsApiAsync().createChannel("» Privater Talk [" + channel + "]", properties);
                            channel += 1;
                            creator.add(p.getUniqueId());
                            p.sendMessage(TeamSpeak.PREFIX + "Du hast einen privaten Channel im Teamspeak erstellt.");
                            Client c = TeamSpeak.getClient(id);
                            if (c == null) {
                                p.sendMessage(TeamSpeak.PREFIX + "Es konnte kein Channel erstellt werden da du nicht auf dem Teamspeak verbunden bist.");
                                return;
                            }
                            TeamSpeak.addToChannelGroup(TeamSpeak.getApi().getChannelByNameExact("» Privater Talk [" + (channel - 1) + "]", false).getId(), TeamspeakServerGroup.TeamspeakChannelGroup.CHANNEL_ERSTELLER, c.getDatabaseId());
                        } catch (Exception e) {
                            p.sendMessage(TeamSpeak.PREFIX + "Du kannst derzeit keine privaten Teamspeak Channel erstellen.");
                            e.printStackTrace();
                        }
                    });
                } else {
                    if (args[0].length() > 0 && args[0].length() < 10) {
                        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
                            try {
                                Map<ChannelProperty, String> properties = new HashMap<>();
                                properties.put(ChannelProperty.CHANNEL_NAME, "» Privater Talk [" + channel + "]");
                                properties.put(ChannelProperty.CHANNEL_DESCRIPTION, "» Privater Talk von " + p.getName() + ".");
                                properties.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
                                properties.put(ChannelProperty.CPID, "88");
                                properties.put(ChannelProperty.CHANNEL_PASSWORD, args[0]);

                                TeamSpeak.getApi().createChannel("» Privater Talk [" + channel + "]", properties);
                                channel += 1;
                                creator.add(p.getUniqueId());
                                p.sendMessage(TeamSpeak.PREFIX + "Du hast einen privaten Channel im Teamspeak erstellt.");
                                p.sendMessage(TeamSpeak.PREFIX + "Passwort: " + args[0]);
                                Client c = TeamSpeak.getClient(id);
                                if (c == null) {
                                    p.sendMessage(TeamSpeak.PREFIX + "Es wurde kein verifizierter Client auf dem Teamspeak gefunden.");
                                } else {
                                    TeamSpeak.addToChannelGroup(TeamSpeak.getApi().getChannelByNameExact("» Privater Talk [" + (channel - 1) + "]", false).getId(), TeamspeakServerGroup.TeamspeakChannelGroup.CHANNEL_ERSTELLER, c.getDatabaseId());
                                }
                            } catch (Exception e) {
                                p.sendMessage(TeamSpeak.PREFIX + "Du kannst derzeit keine privaten Teamspeak Channel erstellen.");
                                e.printStackTrace();
                            }
                        });
                    } else {
                        p.sendMessage(TeamSpeak.PREFIX + "Das Passwort muss zwischen 1 und 10 Zeichen haben.");
                    }
                }
            }
        } else {
            p.sendMessage(TeamSpeak.PREFIX + "Du kannst nur mit Premium eigene Teamspeak Channel erstellen.");
        }
        return true;
    }

    public boolean hasChannel(Player p) {
        return creator.contains(p.getUniqueId());
    }
}
