package de.newrp.discord.events;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class GuildReadyListener extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {

        event.getGuild().updateCommands().addCommands(
                Commands.slash("verify", "Verifiziere deinen Minecraft-Account")
                        .addOption(OptionType.STRING, "verify", "Verifiziere deinen Minecraft-Account", true, true)
        ).queue();
        //event.getJDA().upsertCommand("verify", "Verifiziere deinen Minecraft-Account").queue();
        super.onGuildReady(event);
    }
}
