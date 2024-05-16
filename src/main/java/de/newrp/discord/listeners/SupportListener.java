package de.newrp.discord.listeners;

import de.newrp.API.Script;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SupportListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined().getId().equals("1183386776165961821")) {
            Script.sendTeamMessage(Script.PREFIX + "Der User " + event.getEntity().getEffectiveName() + " hat den Discord-Support betreten.");
        }
    }

}
