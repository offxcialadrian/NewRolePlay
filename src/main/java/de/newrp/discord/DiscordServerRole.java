package de.newrp.discord;

import de.newrp.dependencies.DependencyContainer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public enum DiscordServerRole {

    VERIFIED("1184935696138514473"),
    ADMINISTRATOR("1184934307173777512"),
    MODERATOR("1190277637210394744"),
    SUPPORTER("1190277910469300265"),
    ENTWICKLER("1232772063538712657"),
    SOCIALMEDIA("1213214746539589772"),
    BAUTEAM("1194417938489425960"),
    EVENTTEAM("1236492824946675742");

    private final String roleId;
    private Role role;

    DiscordServerRole(String roleId) {
        this.roleId = roleId;
        this.role = null; // Rolle wird im Konstruktor initialisiert
    }

    public Role getRole(Guild guild) {
        if (role == null) {
            this.role = guild.getRoleById(this.roleId);
        }
        return this.role;
    }
}
