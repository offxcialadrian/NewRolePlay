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
    SUPPORTER("1190277910469300265");

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
