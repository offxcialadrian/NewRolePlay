package de.newrp.discord.listeners;

import de.newrp.API.Script;
import de.newrp.Player.Annehmen;
import de.newrp.discord.Discord;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.entities.ReceivedMessage;
import org.bukkit.entity.Player;

public class VerifyListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("verify")) {
            if (event.getChannel().getType() == ChannelType.PRIVATE) {
                event.reply("Bitte nutze den #verify Channel!").setEphemeral(true).queue();
                return;
            }

            if (event.getOptions().size() != 1) {
                event.reply("Bitte nutze /verify [Name]").setEphemeral(true).queue();
                return;
            }

            String name = event.getOptions().get(0).getAsString();
            Player p = Script.getPlayer(name);

            if (p == null) {
                event.reply("Der Spieler konnte nicht gefunden werden.").setEphemeral(true).queue();
                return;
            }

            if(Discord.isVerified(Script.getNRPID(p))) {
                event.reply("Du hast dich bereits verifiziert.").setEphemeral(true).queue();
                return;
            }

            event.reply("Du hast eine Anfrage an " + Script.getName(p) + " gesendet.").setEphemeral(true).queue();
            Annehmen.offer.put(p.getName() + ".dcverify", event.getUser().getId());
            p.sendMessage(Discord.PREFIX + "Der User " + event.getUser().getEffectiveName() + " möchte sich mit deinem Account verifizieren.");
            Script.sendAcceptMessage(p);
        }
    }
}
