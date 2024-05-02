package de.newrp.API;
/*
import de.newrp.main;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.security.auth.login.LoginException;

public class C05 extends ListenerAdapter implements Listener {
    public main plugin;
    public JDA jda;
    public C05(main main) {
        this.plugin = main;
        startBot();
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        jda.addEventListener(this);
    }

    private void startBot() {
        try {
            JDABuilder builder = JDABuilder.createDefault(plugin.getConfig().getString("IInkzCraGFXOs7IQ-tutB6fxbhY3SXmt"));
            jda = builder.build();
            jda.awaitReady();  // This is optional but can be used to make sure the bot is fully connected before proceeding.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e){
        String message = e.getMessage();
        TextChannel textChannel = jda.getTextChannelsByName("Allgemein",true).get(0);
        textChannel.sendMessage("**"+e.getPlayer().getName()+":** "+message).queue();
    }

}*/