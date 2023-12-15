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
import org.bukkit.entity.Pose;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NPCUtil {

    public static void spawnNPC(Player player) {
        EntityPlayer craftPlayer = ((CraftPlayer)player).getHandle();

        Property textures = (Property) craftPlayer.getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));



        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(), gameProfile,
                new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle()));
        entityPlayer.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        Location bed = player.getLocation().add(1, 0, 0);
        entityPlayer.e(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));



        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
                player.getName());
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        ArrayList<String> playerToAdd = new ArrayList<>();

        playerToAdd.add(entityPlayer.getName()); //Add the fake player so this player will not have a nametag
        PacketPlayOutScoreboardTeam scoreboard1 = new PacketPlayOutScoreboardTeam(team, 1);
        PacketPlayOutScoreboardTeam scoreboard0 = new PacketPlayOutScoreboardTeam(team, 0);
        PacketPlayOutScoreboardTeam scoreboard3 = new PacketPlayOutScoreboardTeam(team, playerToAdd, 3);


        DataWatcher watcher = entityPlayer.getDataWatcher();
        try {
            byte b = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40; // each of the overlays (cape, jacket, sleeves, pants, hat)
            watcher.set(DataWatcherRegistry.a.a(16), b); // To find value use wiki.vg

            Field poseField = Entity.class.getDeclaredField("POSE");
            poseField.setAccessible(true);
            watcher.set((DataWatcherObject<Pose>) poseField.get(null), Pose.SLEEPING);
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
    }

    public static void removeNPC(Player player) {
        EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();

        Property textures = (Property) craftPlayer.getProfile().getProperties().get("textures").toArray()[0];
        GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
        gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));
    }

}