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
        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();

        Property textures = (Property) craftPlayer.getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));

        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(), gameProfile,
                new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle()));

        // Set the position and orientation
        Location bed = player.getLocation().add(1, 0, 0);
        entityPlayer.setPositionRotation(bed.getX(), bed.getY(), bed.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

        // Set the lying down pose
        DataWatcher watcher = entityPlayer.getDataWatcher();
        try {
            byte b = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
            watcher.set(DataWatcherRegistry.a.a(16), b);
            Field poseField = Entity.class.getDeclaredField("POSE");
            poseField.setAccessible(true);
            DataWatcherObject<EntityPose> POSE = (DataWatcherObject<EntityPose>) poseField.get(null);
            watcher.set(POSE, EntityPose.SLEEPING);
        } catch (Exception e) {
            Debug.debug("Error setting the pose for the NPC");
            Debug.debug(e);
        }

        Location bedLocation = player.getLocation().add(1, 0, 0);
        entityPlayer.e(new BlockPosition(bedLocation.getX(), bedLocation.getY(), bedLocation.getZ()));

        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
                player.getName());
        ArrayList<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(entityPlayer.getName());
        PacketPlayOutScoreboardTeam scoreboard1 = new PacketPlayOutScoreboardTeam(team, 1);
        PacketPlayOutScoreboardTeam scoreboard0 = new PacketPlayOutScoreboardTeam(team, 0);
        PacketPlayOutScoreboardTeam scoreboard3 = new PacketPlayOutScoreboardTeam(team, playerToAdd, 3);

        PacketPlayOutEntity.PacketPlayOutRelEntityMove move = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                entityPlayer.getId(), (byte) 0, (byte) 0, (byte) 0, false); // Set motion to 0

        List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
        equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
        equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
        equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(player.getInventory().getBoots())));
        PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(entityPlayer.getId(), equipmentList);

        for (Player on : Bukkit.getOnlinePlayers()) {
            PlayerConnection p = ((CraftPlayer) on).getHandle().playerConnection;
            p.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
            p.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            p.sendPacket(scoreboard1);
            p.sendPacket(scoreboard0);
            p.sendPacket(scoreboard3);
            p.sendPacket(equipment);
            p.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, false));
            p.sendPacket(move);
        }

        npcMap.put(player, entityPlayer);
    }

    public static void spawnNPC(Player player, Location loc) {
        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();

        Property textures = (Property) craftPlayer.getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));

        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(), gameProfile,
                new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle()));
        entityPlayer.setPosition(loc.getX(), loc.getY(), loc.getZ());

        Location bed = loc.add(1, 0, 0);
        entityPlayer.e(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));

        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
                player.getName());
        ArrayList<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(entityPlayer.getName());
        PacketPlayOutScoreboardTeam scoreboard1 = new PacketPlayOutScoreboardTeam(team, 1);
        PacketPlayOutScoreboardTeam scoreboard0 = new PacketPlayOutScoreboardTeam(team, 0);
        PacketPlayOutScoreboardTeam scoreboard3 = new PacketPlayOutScoreboardTeam(team, playerToAdd, 3);

        DataWatcher watcher = entityPlayer.getDataWatcher();
        try {
            byte b = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
            watcher.set(DataWatcherRegistry.a.a(16), b);
            Field poseField = Entity.class.getDeclaredField("POSE");
            poseField.setAccessible(true);
            DataWatcherObject<EntityPose> POSE = (DataWatcherObject<EntityPose>) poseField.get(null);
            watcher.set(POSE, EntityPose.SLEEPING);
        } catch (Exception ignored) {
        }
        PacketPlayOutEntity.PacketPlayOutRelEntityMove move = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                entityPlayer.getId(), (byte) 0, (byte) 0, (byte) 0, false); // Set motion to 0

        List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
        equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
        equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
        equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(player.getInventory().getBoots())));
        PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(entityPlayer.getId(), equipmentList);

        for (Player on : Bukkit.getOnlinePlayers()) {
            PlayerConnection p = ((CraftPlayer) on).getHandle().playerConnection;
            p.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
            p.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            p.sendPacket(scoreboard1);
            p.sendPacket(scoreboard0);
            p.sendPacket(scoreboard3);
            p.sendPacket(equipment);
            p.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, false));
            p.sendPacket(move);
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
            // Reset the player's scoreboard (you might want to modify this method to exclude removing the player from the tablist)
            Cache.resetScoreboard(player);
        }
    }

    public static void reloadNPC(Player newPlayer) {
        for (EntityPlayer entityPlayer : npcMap.values()) {
            PlayerConnection playerConnection = ((CraftPlayer) newPlayer).getHandle().playerConnection;

            playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
            playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));

            DataWatcher watcher = entityPlayer.getDataWatcher();
            playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true));

            PacketPlayOutEntity.PacketPlayOutRelEntityMove movePacket = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                    entityPlayer.getId(), (byte) 0, (byte) 0, (byte) 0, false);
            playerConnection.sendPacket(movePacket);
        }
    }
}
