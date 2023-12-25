package de.newrp.Forum;


import de.newrp.API.Rank;
import de.newrp.API.Script;
import de.newrp.Berufe.Beruf;
import de.newrp.main;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Forum {
    public static final String prefix = "§8[§eForum§8]§6 ";

    public static int getForumID(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT forumID FROM forum WHERE id=" + id)) {
            if (rs.next()) {
                return rs.getInt("forumID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void deleteVerify(int userID) {
        Script.executeUpdate("DELETE FROM forum WHERE id=" + userID);
    }

    public static void addUserToGroup(int forumid, ForumGroup group) {
        Script.executeUpdate("INSERT INTO wcf.wcf1_user_to_group (userID, groupID) VALUES(" + forumid + ", " + group.getID() + ") ON DUPLICATE KEY UPDATE userID=userID;");
    }

    public static void removeUserFromGroup(int forumid, ForumGroup group) {
        Script.executeUpdate("DELETE FROM wcf.wcf1_user_to_group WHERE userID=" + forumid + " AND groupID=" + group.getID());
    }

    public static void clearUserGroups(int forumid) {
        Script.executeUpdate("DELETE FROM wcf.wcf1_user_to_group WHERE userID=" + forumid + " AND groupID <> 1 AND groupID <> 3");
    }

    public static void setForumName(int forumid, String name) {
        Script.executeUpdate("UPDATE wcf.wcf1_user SET username='" + name + "' WHERE userID=" + forumid);
    }

    public static void ban(int forumid, boolean banned) {
        Script.executeUpdate("UPDATE wcf.wcf1_user SET banned=" + banned + " WHERE userID=" + forumid);
    }

    public static void ban(int forumid, boolean banned, String reason) {
        Script.executeUpdate("UPDATE wcf.wcf1_user SET banned=" + banned + ", banReason='" + reason + "' WHERE userID=" + forumid);
    }

    public static void setName(int forumid, String name) {
        Script.executeUpdate("UPDATE wcf.wcf1_user SET username='" + name + "' WHERE userID=" + forumid);
    }

    public static void syncPermission(Player p) {
        int id = Script.getNRPID(p);
        int forumid = getForumID(id);
        if (forumid == 0) return;
        setName(forumid, Script.isNRPTeam(p) ? ("NRP × " + p.getName()) : p.getName());
        clearUserGroups(forumid);
        Beruf.Berufe f = Beruf.getBeruf(p);
        addUserToGroup(forumid, ForumGroup.ZIVILIST);
        addUserToGroup(forumid, ForumGroup.VERIFIED);
        if (f != null) addUserToGroup(forumid, f.getForumGroup(f.isLeader(p)));

        if(Script.hasRank(p, Rank.OWNER, false)) {
            addUserToGroup(forumid, ForumGroup.ADMINISTRATOR);
        } else if(Script.hasRank(p, Rank.ADMINISTRATOR, false)) {
            addUserToGroup(forumid, ForumGroup.ADMINISTRATOR);
        } else if(Script.hasRank(p, Rank.MODERATOR, false)) {
            addUserToGroup(forumid, ForumGroup.MODERATOR);
        } else if(Script.hasRank(p, Rank.SUPPORTER, false)) {
            addUserToGroup(forumid, ForumGroup.SUPPORTER);
        }

        /*if (Team.BAUTEAM.isInTeam(id)) addUserToGroup(forumid, ForumGroup.BAUTEAM);
        if (Team.EVENT.isInTeam(id)) addUserToGroup(forumid, ForumGroup.EVENT_TEAM);
        //if (Team.ENTWICKLUNG.isInTeam(id)) addUserToGroup(forumid, ForumGroup.ENTWICKLUNG);
        if (Team.SOCIAL_MEDIA.isInTeam(id)) addUserToGroup(forumid, ForumGroup.SOCIAL_MEDIA);
        if (Script.isYoutuber(id)) addUserToGroup(forumid, ForumGroup.YOUTUBER);*/
    }
}
