package de.newrp.API;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.newrp.NewRoleplayMain;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.eq2online.permissions.ReplicatedPermissionsContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModListener implements Listener {

    /*private final ListMultimap<String, ReplicatedPermissionsContainer> tempContainer = ArrayListMultimap.create();

    public ModListener() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(NewRoleplayMain.getInstance(), "fml:hs");
        Bukkit.getMessenger().registerIncomingPluginChannel(NewRoleplayMain.getInstance(), "fml:hs", (s, player, data) -> {
            if (data[0] == 2) {
                Map<String, String> modMap = getModData(data);

                Bukkit.getScheduler().runTaskAsynchronously(NewRoleplayMain.getInstance(), () -> {
                    try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO modification_forge (userID, mods) VALUES (?, ?) ON DUPLICATE KEY UPDATE mods=?")) {
                        statement.setInt(1, Script.getNRPID(player));
                        statement.setString(2, modMap.toString());
                        statement.setString(3, modMap.toString());

                        statement.execute();
                    } catch (SQLException e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
        });

        Bukkit.getMessenger().registerIncomingPluginChannel(NewRoleplayMain.getInstance(), "permissionsrepl", (s, player, data) -> {
            ReplicatedPermissionsContainer replicatedPermissionsContainer = ReplicatedPermissionsContainer.fromBytes(data);
            replicatedPermissionsContainer.sanitise();

            if (tempContainer.get(player.getName()).isEmpty()) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(NewRoleplayMain.getInstance(), () -> {
                    Map<String, Float> modMap = new HashMap<>();
                    for (ReplicatedPermissionsContainer permissionsContainer : tempContainer.get(player.getName())) {
                        modMap.put(permissionsContainer.modName, permissionsContainer.modVersion);
                    }

                    try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO modification_liteloader (userID, mods) VALUES (?, ?) ON DUPLICATE KEY UPDATE mods=?")) {
                        statement.setInt(1, Script.getNRPID(player));
                        statement.setString(2, modMap.toString());
                        statement.setString(3, modMap.toString());

                        statement.execute();
                    } catch (SQLException e) {
                        throw new IllegalStateException(e);
                    }

                    tempContainer.removeAll(player.getName());
                }, 10);
            }

            tempContainer.put(player.getName(), replicatedPermissionsContainer);
        });

        Bukkit.getMessenger().registerIncomingPluginChannel(NewRoleplayMain.getInstance(), "lmc", (s, player, data) -> {
            ByteBuf buf = Unpooled.wrappedBuffer(data);

            String messageKey = LMCUtils.readString(buf, Short.MAX_VALUE);
            String messageContents = LMCUtils.readString(buf, Short.MAX_VALUE);


            if (messageKey.equals("INFO")) {
                JsonElement element = new JsonParser().parse(messageContents);
                JsonObject object = element.getAsJsonObject();

                JsonArray addons = object.get("addons").getAsJsonArray();
                JsonArray mods = null;
                if (object.get("mods") != null) {
                    mods = object.get("mods").getAsJsonArray();
                }

                Map<String, String> addonMap = new HashMap<>();
                Map<String, String> modsMap = new HashMap<>();

                for (JsonElement addon : addons) {
                    JsonObject addonObject = addon.getAsJsonObject();
                    String addonUUID = addonObject.get("uuid").getAsString();
                    String addonName = addonObject.get("name").getAsString();

                    addonMap.put(addonUUID, addonName);
                }
                if (mods != null) {
                    for (JsonElement mod : mods) {
                        JsonObject modObject = mod.getAsJsonObject();
                        String modHash = modObject.get("hash").getAsString();
                        String modName = modObject.get("name").getAsString();

                        modsMap.put(modHash, modName);
                    }
                }

                try (PreparedStatement statement = NewRoleplayMain.getConnection().prepareStatement("INSERT INTO modification_labymod (userID, addons, mods) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE addons=?, mods=?")) {
                    statement.setInt(1, Script.getNRPID(player));
                    statement.setString(2, addonMap.toString());
                    statement.setString(3, modsMap.toString());
                    statement.setString(4, addonMap.toString());
                    statement.setString(5, modsMap.toString());

                    statement.execute();
                } catch (SQLException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    @EventHandler
    public void onChannel(PlayerRegisterChannelEvent event) {
        if (event.getChannel().equals("fml:hs")) {
            Player p = event.getPlayer();

            p.sendPluginMessage(NewRoleplayMain.getInstance(), "fml:hs", new byte[]{-2, 0}); //Handshake Reset
            p.sendPluginMessage(NewRoleplayMain.getInstance(), "fml:hs", new byte[]{0, 2, 0, 0, 0, 0}); //Server Hello
            p.sendPluginMessage(NewRoleplayMain.getInstance(), "fml:hs", new byte[]{2, 0, 0, 0, 0}); //Mod List
        }
    }

    private Map<String, String> getModData(byte[] data) {
        Map<String, String> mods = new HashMap<>();

        boolean store = false;
        String tempName = null;

        for (int i = 2; i < data.length; store = !store) {
            int end = i + data[i] + 1;
            byte[] range = Arrays.copyOfRange(data, i + 1, end);

            String string = new String(range, StandardCharsets.UTF_8);

            if (store) {
                mods.put(tempName, string);
            } else {
                tempName = string;
            }

            i = end;
        }

        return mods;
    }

    public void deleteOld() {
        Script.executeUpdate("DELETE FROM modification_liteloader WHERE time<NOW() - INTERVAL 2 WEEK",
                "DELETE FROM modification_forge WHERE time<NOW() - INTERVAL 2 WEEK",
                "DELETE FROM modification_labymod WHERE time<NOW() - INTERVAL 2 WEEK");
    }*/
}
