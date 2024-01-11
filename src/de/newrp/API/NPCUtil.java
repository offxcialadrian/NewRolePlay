package de.newrp.API;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NPCUtil {

    public static HashMap<Player, EntityPlayer> npcs = new HashMap<>();

    public static void spawnNPC(Player player) {
        EntityPlayer craftPlayer = ((CraftPlayer)player).getHandle();

        // NPC textures
        Property textures = (Property) craftPlayer.getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));



        // Creating the NPC
        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(), gameProfile,
                new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle()));
        entityPlayer.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        Location bed = player.getLocation().add(1, 0, 0);
        entityPlayer.e(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));





        // Pose and overlays
        DataWatcher watcher = entityPlayer.getDataWatcher();
        try {
            byte b = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40; // each of the overlays (cape, jacket, sleeves, pants, hat)
            watcher.set(DataWatcherRegistry.a.a(16), b); // To find value use wiki.vg

            Field poseField = Entity.class.getDeclaredField("POSE");
            poseField.setAccessible(true);
            DataWatcherObject<EntityPose> POSE = (DataWatcherObject<EntityPose>) poseField.get(null);
            watcher.set(POSE, EntityPose.SLEEPING);
        } catch (Exception ignored) {

        }
        PacketPlayOutEntity.PacketPlayOutRelEntityMove move = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                entityPlayer.getId(), (byte) 0, (byte) ((player.getLocation()
                .getY() - 1.7 - player.getLocation().getY()) * 32),
                (byte) 0, false);



        // Equipment
        List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
        equipmentList.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
        equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
        equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
        equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(player.getInventory().getBoots())));

        PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(entityPlayer.getId(), equipmentList);
        npcs.put(player, entityPlayer);

        // Sending packets
        for (Player on : Bukkit.getOnlinePlayers()) {
            PlayerConnection p = ((CraftPlayer) on).getHandle().playerConnection;
            p.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
            p.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            p.sendPacket(equipment);
            p.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, false));
            p.sendPacket(move);
        }
    }

    public static void removeNPC(Player player) {
        if (npcs.containsKey(player)) {
            EntityPlayer entityPlayer = npcs.get(player);


            Cache.loadScoreboard(player);

            //kill the NPC
            PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());
            for (Player on : Bukkit.getOnlinePlayers()) {
                PlayerConnection p = ((CraftPlayer) on).getHandle().playerConnection;
                p.sendPacket(destroy);
            }

            // Clear data related to the NPC
            npcs.remove(player);
        }
    }

    public static void reloadNPC(Player player) {

        for (EntityPlayer entityPlayer : npcs.values()) {

            ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
                    player.getName());
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
            ArrayList<String> playerToAdd = new ArrayList<>();

            DataWatcher watcher = entityPlayer.getDataWatcher();
            try {
                byte b = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40; // each of the overlays (cape, jacket, sleeves, pants, hat)
                watcher.set(DataWatcherRegistry.a.a(16), b); // To find value use wiki.vg

                Field poseField = Entity.class.getDeclaredField("POSE");
                poseField.setAccessible(true);
                DataWatcherObject<EntityPose> POSE = (DataWatcherObject<EntityPose>) poseField.get(null);
                watcher.set(POSE, EntityPose.SLEEPING);
            } catch (Exception ignored) {

            }
            PacketPlayOutEntity.PacketPlayOutRelEntityMove move = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                    entityPlayer.getId(), (byte) 0, (byte) ((player.getLocation()
                    .getY() - 1.7 - player.getLocation().getY()) * 32),
                    (byte) 0, false);



            // Equipment
            List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
            equipmentList.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
            equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
            equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
            equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(player.getInventory().getBoots())));

            PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(entityPlayer.getId(), equipmentList);

            playerToAdd.add(entityPlayer.getName());
            PacketPlayOutScoreboardTeam scoreboard1 = new PacketPlayOutScoreboardTeam(team, 1);
            PacketPlayOutScoreboardTeam scoreboard0 = new PacketPlayOutScoreboardTeam(team, 0);
            PacketPlayOutScoreboardTeam scoreboard3 = new PacketPlayOutScoreboardTeam(team, playerToAdd, 3);
            PlayerConnection p = ((CraftPlayer) player).getHandle().playerConnection;
            p.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
            p.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
            p.sendPacket(scoreboard1);
            p.sendPacket(scoreboard0);
            p.sendPacket(scoreboard3);
            p.sendPacket(equipment);
            p.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, false));
            p.sendPacket(move);
        }
    }

}