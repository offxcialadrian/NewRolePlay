package de.newrp.discord;

import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.NewRoleplayMain;
import de.newrp.TeamSpeak.TeamspeakServerGroup;
import de.newrp.dependencies.DependencyContainer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Discord {


    public static boolean isVerified(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM discord WHERE id=" + id)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteVerfify(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM discord WHERE id=" + id);
            JDA jda =  DependencyContainer.getContainer().getDependency(IJdaService.class).getJda();
            Member member = jda.getGuildById("1183386774374981662").getMemberById(getDiscordID(id));
            Guild guild = jda.getGuildById("1183386774374981662");
            for(Role role : member.getRoles()) {
                jda.getGuildById("1183386774374981662").removeRoleFromMember(member, role).queue();
            }
            jda.getGuildById("1183386774374981662").addRoleToMember(member, DiscordServerRole.VERIFIED.getRole(guild)).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getDiscordID(int id) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT discord_id FROM discord WHERE nrp_id=" + id)) {
            if (rs.next()) {
                return rs.getInt("discord_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void sync(int id) {
        JDA jda =  DependencyContainer.getContainer().getDependency(IJdaService.class).getJda();
        Member member = jda.getGuildById("1183386774374981662").getMemberById(getDiscordID(id));
        Guild guild = jda.getGuildById("1183386774374981662");
        for(Role role : member.getRoles()) {
            jda.getGuildById("1183386774374981662").removeRoleFromMember(member, role).queue();
        }
        jda.getGuildById("1183386774374981662").addRoleToMember(member, DiscordServerRole.VERIFIED.getRole(guild)).queue();
        Rank rank = Script.getRank(Script.getOfflinePlayer(id));
        switch (rank) {
            case OWNER:
            case ADMINISTRATOR:
                addToRole(id, DiscordServerRole.ADMINISTRATOR);
                break;
            case MODERATOR:
                addToRole(id, DiscordServerRole.MODERATOR);
                break;
            case SUPPORTER:
                addToRole(id, DiscordServerRole.SUPPORTER);
                break;
        }
    }

    public static Member findMemberByName(JDA jda, String guildId, String username) {
        Guild guild = jda.getGuildById(guildId);
        guild.findMembers(a -> a.getNickname().equalsIgnoreCase(username)).get().stream().findFirst();
        return null;
    }

    public static void addToRole(int id, DiscordServerRole role) {
        JDA jda =  DependencyContainer.getContainer().getDependency(IJdaService.class).getJda();
        Member member = jda.getGuildById("1183386774374981662").getMemberById(getDiscordID(id));
        Guild guild = jda.getGuildById("1183386774374981662");
        jda.getGuildById("1183386774374981662").addRoleToMember(member, role.getRole(guild)).queue();
    }

    public static void verify(int id, Member member) {
        try (Statement stmt = NewRoleplayMain.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO discord (id, nrp_id, discord_id) VALUES (NULL, " + id + ", " + member.getId() + ")");
            Discord.sync(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
