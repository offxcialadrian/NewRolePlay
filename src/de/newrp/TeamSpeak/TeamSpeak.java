package de.newrp.TeamSpeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.newrp.API.*;
import de.newrp.Berufe.Beruf;
import de.newrp.main;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TeamSpeak {
    public static final String PREFIX = "§8[§9Teamspeak§8]§9 " + Messages.ARROW + " §7";
    private static TS3Query tsQuery;
    private static TS3Api tsApi;
    private static TS3ApiAsync tsApiAsync;

    public static void connect() {
        TS3Config tsConfig = new TS3Config();
        tsConfig.setHost("85.214.163.72");
        tsConfig.setDebugLevel(Level.WARNING);
        tsConfig.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
        tsConfig.setFloodRate(TS3Query.FloodRate.UNLIMITED);
        tsConfig.setQueryPort(10011);

        tsQuery = new TS3Query(tsConfig);
        tsQuery.connect();

        tsApi = tsQuery.getApi();
        tsApiAsync = tsQuery.getAsyncApi();

        tsApi.selectVirtualServerByPort(9987);
        tsApi.login("serveradmin", "mRlpUFC2");
        tsApi.setNickname("New RolePlay Bot");
        tsApi.registerEvent(TS3EventType.SERVER);

        tsApi.addTS3Listeners(new TeamspeakListener());
    }

    public static boolean isVerified(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM teamspeak WHERE id=" + id)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteVerfify(int id) {
        Script.executeUpdate("DELETE FROM teamspeak WHERE id=" + id);
    }

    public static void verify(int id, Client c) {
        String uid = c.getUniqueIdentifier();
        int dbID = c.getDatabaseId();
        addToServerGroup(TeamspeakServerGroup.VERIFIED, dbID);
        Script.executeUpdate("INSERT INTO teamspeak (id, uid) VALUES (" + id + ", '" + uid + "');");
        sync(id, c);
    }

    public static String getVerification(int id) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT uid FROM teamspeak WHERE id=" + id)) {
            if (rs.next()) return rs.getString("uid");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void ban(int id, Client c) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            if (c == null) return;
            int dbID = c.getDatabaseId();
            for (int g : c.getServerGroups()) removeFromServerGroup(g, dbID);
            Beruf.Berufe b = Beruf.getBeruf(id);
            for (Beruf.Berufe fraks : Beruf.Berufe.values()) {
                if (fraks != b) {
                    int cID = fraks.getChannelID();
                    if (cID != 0) {
                        removeFromChannelGroup(cID, dbID);
                    }
                }
            }
            addToServerGroup(TeamspeakServerGroup.GEBANNT, dbID);
        });
    }

    public static void sync(int id, Client c) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            String uid = getVerification(id);
            if (uid == null || c == null) return;
            int dbID = c.getDatabaseId();
            for (int g : c.getServerGroups()) {
                removeFromServerGroup(g, dbID);
            }
            String name = Script.getPlayer(id).getName();
            boolean admin = Script.isNRPTeam(Script.getPlayer(id));
            setDescription(c.getId(), (admin ? "NRP × " + name : name));
            Beruf.Berufe f = Beruf.getBeruf(id);
            addToServerGroup(f.getTeamspeakServerGroup(), dbID);
            addToChannelGroup(f.getChannelID(), (f.isLeader(Script.getPlayer(id)) ? TeamspeakServerGroup.TeamspeakChannelGroup.LEADER : TeamspeakServerGroup.TeamspeakChannelGroup.MEMBER), dbID);

            Rank rank = Script.getRank(Script.getPlayer(id));
            switch (rank) {
                case OWNER:
                    addToServerGroup(TeamspeakServerGroup.ADMINISTRATOR, dbID);
                    addToServerGroup(TeamspeakServerGroup.NRP_SERVERTEAM, dbID);
                case ADMINISTRATOR:
                    addToServerGroup(TeamspeakServerGroup.ADMINISTRATOR, dbID);
                    addToServerGroup(TeamspeakServerGroup.NRP_SERVERTEAM, dbID);
                    break;
                case MODERATOR:
                    addToServerGroup(TeamspeakServerGroup.MODERATOR, dbID);
                    addToServerGroup(TeamspeakServerGroup.NRP_SERVERTEAM, dbID);
                    break;
                case SUPPORTER:
                    addToServerGroup(TeamspeakServerGroup.SUPPORTER, dbID);
                    addToServerGroup(TeamspeakServerGroup.NRP_SERVERTEAM, dbID);
                    break;
            }

            addToServerGroup(TeamspeakServerGroup.VERIFIED, dbID);
            if (!admin) {
                if(Premium.hasPremium(Script.getPlayer(id))) {
                    addToServerGroup(TeamspeakServerGroup.PREMIUM, dbID);
                }
            }
            for (Beruf.Berufe fraks : Beruf.Berufe.values()) {
                    int cID = fraks.getChannelID();
                    if (cID != 0) {
                        removeFromChannelGroup(cID, dbID);
                    }
            }
            for (Team.Teams t : Team.Teams.values()) {
                if (t.getTeamspeakServerGroup() == null) continue;
                if (Team.getTeam(Script.getPlayer(id)) != null) {
                    addToServerGroup(t.getTeamspeakServerGroup(), dbID);
                    break;
                }
            }
            sendClientMessage(c.getId(), "Deine Teamspeak Rechte wurden erfolgreich synchronisiert.");
        });
    }



    public static void sync(int id) {
        Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> {
            String uid = getVerification(id);
            Client c = getClient(uid);
            if (uid == null || c == null) return;
            int dbID = c.getDatabaseId();
            for (int g : c.getServerGroups()) {
                removeFromServerGroup(g, dbID);
            }
            String name = Script.getPlayer(id).getName();
            boolean admin = Script.isNRPTeam(Script.getPlayer(id));
            setDescription(c.getId(), (admin ? "NRP × " + name : name));
            Beruf.Berufe f = Beruf.getBeruf(id);
            addToServerGroup(f.getTeamspeakServerGroup(), dbID);
            addToChannelGroup(f.getChannelID(), (f.isLeader(Script.getPlayer(id)) ? TeamspeakServerGroup.TeamspeakChannelGroup.LEADER : TeamspeakServerGroup.TeamspeakChannelGroup.MEMBER), dbID);

            Rank rank = Script.getRank(Script.getPlayer(id));
            switch (rank) {
                case OWNER:
                    addToServerGroup(TeamspeakServerGroup.ADMINISTRATOR, dbID);
                    addToServerGroup(TeamspeakServerGroup.NRP_SERVERTEAM, dbID);
                case ADMINISTRATOR:
                    addToServerGroup(TeamspeakServerGroup.ADMINISTRATOR, dbID);
                    addToServerGroup(TeamspeakServerGroup.NRP_SERVERTEAM, dbID);
                    break;
                case MODERATOR:
                    addToServerGroup(TeamspeakServerGroup.MODERATOR, dbID);
                    addToServerGroup(TeamspeakServerGroup.NRP_SERVERTEAM, dbID);
                    break;
                case SUPPORTER:
                    addToServerGroup(TeamspeakServerGroup.SUPPORTER, dbID);
                    addToServerGroup(TeamspeakServerGroup.NRP_SERVERTEAM, dbID);
                    break;
            }

            addToServerGroup(TeamspeakServerGroup.VERIFIED, dbID);
            if (!admin) {
                if(Premium.hasPremium(Script.getPlayer(id))) {
                    addToServerGroup(TeamspeakServerGroup.PREMIUM, dbID);
                }
            }
            for (Beruf.Berufe fraks : Beruf.Berufe.values()) {
                int cID = fraks.getChannelID();
                if (cID != 0) {
                    removeFromChannelGroup(cID, dbID);
                }
            }
            for (Team.Teams t : Team.Teams.values()) {
                if (t.getTeamspeakServerGroup() == null) continue;
                if (Team.getTeam(Script.getPlayer(id)) != null) {
                    addToServerGroup(t.getTeamspeakServerGroup(), dbID);
                    break;
                }
            }
        });
    }

    public static void addFrakGroups(int id, Beruf.Berufe f) {
        String uid = getVerification(id);
        if (uid == null) return;
        Client c = getClient(uid);
        int dbID = c.getDatabaseId();
        addToServerGroup(f.getTeamspeakServerGroup(), dbID);
        addToChannelGroup(f.getChannelID(), (f.isLeader(id) ? TeamspeakServerGroup.TeamspeakChannelGroup.LEADER : TeamspeakServerGroup.TeamspeakChannelGroup.MEMBER), dbID);
    }

    public static void removeFrakGroups(int id, Beruf.Berufe f) {
        String uid = getVerification(id);
        if (uid == null) return;
        Client c = getClient(uid);
        int dbID = c.getDatabaseId();
        addToServerGroup(f.getTeamspeakServerGroup(), dbID);
        addToChannelGroup(f.getChannelID(), (f.isLeader(id) ? TeamspeakServerGroup.TeamspeakChannelGroup.LEADER : TeamspeakServerGroup.TeamspeakChannelGroup.MEMBER), dbID);
    }

    public static void disconnect() {
        tsQuery.exit();

        tsQuery = null;
        tsApi = null;
        tsApiAsync = null;
    }

    public static TS3Api getApi() {
        return tsApi;
    }

    public static boolean isConnected() {
        return tsQuery != null;
    }

    public static TS3ApiAsync getTsApiAsync() {
        return tsApiAsync;
    }

    public static void addToServerGroup(TeamspeakServerGroup g, int dbID) {
        if (dbID != 0) {
            tsApiAsync.addClientToServerGroup(g.getGroupID(), dbID);
        }
    }

    public static void removeFromServerGroup(TeamspeakServerGroup g, int dbID) {
        if (dbID != 0) {
            tsApiAsync.removeClientFromServerGroup(g.getGroupID(), dbID);
        }
    }

    public static void addToChannelGroup(int channel, TeamspeakServerGroup.TeamspeakChannelGroup g, int dbID) {
        if (dbID != 0) {
            tsApiAsync.setClientChannelGroup(g.getGroupID(), channel, dbID);
        }
    }

    public static void removeFromChannelGroup(int channel, int dbID) {
        if (dbID != 0) {
            tsApiAsync.setClientChannelGroup(TeamspeakServerGroup.TeamspeakChannelGroup.GUEST.getGroupID(), channel, dbID);
        }
    }

    public static void addToServerGroup(int g, int dbID) {
        if (dbID != 0) {
            Bukkit.getScheduler().runTaskAsynchronously(main.getInstance(), () -> tsApi.addClientToServerGroup(g, dbID));
        }
    }

    public static void removeFromServerGroup(int g, int dbID) {
        if (dbID != 0) {
            tsApiAsync.removeClientFromServerGroup(g, dbID);
        }
    }

    public static void addToChannelGroup(int channel, int g, int dbID) {
        if (dbID != 0) {
            tsApiAsync.setClientChannelGroup(g, channel, dbID);
        }
    }

    public static void setDescription(int clientID, String description) {
        Map<ClientProperty, String> properties = new HashMap<>();
        properties.put(ClientProperty.CLIENT_DESCRIPTION, description);
        tsApiAsync.editClient(clientID, properties);
    }

    public static Client getClient(int unicaID) {
        String uniqueID = getVerification(unicaID);
        if (uniqueID == null) return null;
        return getClient(uniqueID);
    }

    public static Client getClient(String uniqueID) {
        if (tsApi == null) return null;
        return tsApi.getClientByUId(uniqueID);
    }

    public static boolean UidIsUsed(String uid) {
        try (Statement stmt = main.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM teamspeak WHERE uid='" + uid + "'")) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void sendClientMessage(int clientID, String message) {
        TS3ApiAsync api = TeamSpeak.getTsApiAsync();
        api.sendPrivateMessage(clientID, message);
    }

    public static void sendClientMessage(int clientID, String... message) {
        TS3ApiAsync api = TeamSpeak.getTsApiAsync();
        for (String s : message) api.sendPrivateMessage(clientID, s);
    }
}
