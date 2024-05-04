package de.newrp.discord.impl;

import de.newrp.discord.IJdaService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class JdaService implements IJdaService {

    private JDA jda;


    @Override
    public JDA createJDAInstance(String token) {
        this.jda = JDABuilder.createDefault(token, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MODERATION).build();
        return this.jda;
    }

    @Override
    public void addEvent(ListenerAdapter adapter) {
        this.jda.addEventListener(adapter);
    }
}
