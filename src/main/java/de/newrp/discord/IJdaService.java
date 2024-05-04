package de.newrp.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public interface IJdaService {

    JDA createJDAInstance(final String token);

    void addEvent(final ListenerAdapter adapter);

}
