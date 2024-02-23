package de.newrp.API;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import de.newrp.API.Cache;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Corpse {

    public static final Map<Player, EntityPlayer> npcMap = new HashMap<>();

    public static void spawnNPC(Player player) {
        spawnNPC(player, player.getLocation());
    }

    public static void spawnNPC(Player player, Location loc) {
        if(npcMap.keySet().contains(player)) {
            removeNPC(player);
        }
        EntityPlayer craftPlayer = ((CraftPlayer)player).getHandle();



        Property textures = (Property) craftPlayer.getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));

        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(),
                gameProfile,
                new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle())
        );





        Location bedLocation = loc.subtract(1, 0, 0);


        //TODO Hier sind zwei Möglichkeiten... Eine wo der Kopf, naja überall hindrehen könnte, wo der Spieler hingeguckt hat
        //npcEntity.setLocation(bedLocation.getX(), bedLocation.getY(), bedLocation.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        //TODO Und eine, wo der Kopf nach oben, oder nur zur Seite gedreht ist. Man könnte es weiter ausbauen, aber mir gefällt die Variante mehr :D
        entityPlayer.setLocation(bedLocation.getX(), bedLocation.getY(), bedLocation.getZ(), player.getLocation().getYaw(), 0);


        entityPlayer.setPose(EntityPose.SLEEPING);

        DataWatcher watcher = entityPlayer.getDataWatcher();
        try {
            byte b = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
            watcher.set(DataWatcherRegistry.a.a(16), b);

        } catch (Exception ignored) {
        }

        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
                player.getName());
        ArrayList<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(craftPlayer.getName());
        PacketPlayOutScoreboardTeam scoreboard1 = new PacketPlayOutScoreboardTeam(team, 1);
        PacketPlayOutScoreboardTeam scoreboard0 = new PacketPlayOutScoreboardTeam(team, 0);
        PacketPlayOutScoreboardTeam scoreboard3 = new PacketPlayOutScoreboardTeam(team, playerToAdd, 3);



        PacketPlayOutEntity.PacketPlayOutRelEntityMove move = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                entityPlayer.getId(), (byte) 0, (byte) 0, (byte) 0, false);



        List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
        equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
        equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
        equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(player.getInventory().getBoots())));
        PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(craftPlayer.getId(), equipmentList);


        for (Player on : Bukkit.getOnlinePlayers()) {
            PlayerConnection playerConnection = ((CraftPlayer) on).getHandle().playerConnection;

            playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
            playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            playerConnection.sendPacket(scoreboard1);
            playerConnection.sendPacket(scoreboard0);
            playerConnection.sendPacket(scoreboard3);
            playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), true));
            playerConnection.sendPacket(equipment);

            playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true));
            //Wofür????
            playerConnection.sendPacket(move);
        }

        npcMap.put(player, entityPlayer);
    }

    public static void removeNPC(Player player) {
        EntityPlayer entityPlayer = npcMap.remove(player);

        if (entityPlayer != null) {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityPlayer.getId());
            for (Player on : Bukkit.getOnlinePlayers()) {
                PlayerConnection p = ((CraftPlayer) on).getHandle().playerConnection;
                p.sendPacket(destroyPacket);
            }
        }
        Script.updateListname(player);
    }

    public static void reloadNPC(Player player) {
        for (EntityPlayer entityPlayerCorps : npcMap.values()) {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayerCorps));
            playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayerCorps));
            playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayerCorps.getId(), entityPlayerCorps.getDataWatcher(), true));
        }
    }
}
